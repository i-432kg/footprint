# ADR: access ログの request 受け渡しは `AccessLogContext` に集約する

## ステータス
Accepted

## 背景

`LOG-03` と `LOG-06` の対応で、read 系 API の成功イベントを `app` へ個別出力するのではなく、`access` ログへ 1 リクエスト 1 本で集約する方針を採用した。

この方針では、controller が endpoint ごとの event 名と補助項目を決め、最終的なログ出力は `AccessLogFilter` が担当する必要がある。

当初は `HttpServletRequest` の attribute に次の 2 つを直接格納していた。

- event 名を表す `String`
- 追加項目を表す `Map<String, Object>`

ただし、この形には次の問題があった。

1. request attribute から `Map<String, Object>` を取り出す際に unchecked cast が必要になる
2. event と fields が別々の attribute に分かれ、1 リクエスト分の access ログ文脈としてまとまりが悪い
3. controller / test 側が内部表現の `Map` と attribute 名に直接依存しやすい

一方で、追加項目の値は将来の JSON 構造化ログを見据えて、`String` へ早期変換せず `Object` のまま保持したい。

## 決定

read 系 access ログの request 内受け渡しは、専用型 `AccessLogContext` へ集約する。

### 1. request attribute の格納単位

`HttpServletRequest` には event 名と fields を別々に格納せず、`AccessLogContext` を 1 つだけ格納する。

### 2. `AccessLogFilter` の公開 API

controller からの設定は次の static helper 経由で行う。

- `AccessLogFilter.setEvent(...)`
- `AccessLogFilter.addField(...)`
- `AccessLogFilter.findContext(...)`

controller は request attribute 名や `Map` 実装を直接意識しない。

### 3. `AccessLogContext` の責務

`AccessLogContext` は次を保持する。

- event 名
- `Map<String, Object>` 形式の追加項目

追加項目の値は `Object` のまま保持し、JSON 構造化ログへ移行するときも数値・真偽値・`BigDecimal` などの型を失わないようにする。

### 4. unchecked cast の扱い

request attribute からの復元は `instanceof AccessLogContext` で判定し、unchecked cast を行わない。

### 5. attribute 名の衝突回避

request attribute 名は `AccessLogFilter` の FQCN を接頭辞にした private 定数で管理する。
外部へ公開するのは helper method のみとし、attribute 名自体は内部実装として隠蔽する。

## 理由

- request attribute の `Object` 境界を専用型で閉じると、`Map<?, ?>` からの unchecked cast が不要になる
- event と fields を同じ文脈オブジェクトへまとめる方が、1 リクエスト分の access ログ情報として自然である
- controller 側は「何を記録するか」だけを決め、`AccessLogFilter` 側が「どう出力するか」を担当できる
- 追加項目を `String` へ早期変換しないことで、将来 `LOG-04` の JSON 構造化ログへ移行しやすい
- request attribute 名や内部 `Map` 表現を隠蔽すると、テストを含む利用側の結合を下げられる

## 影響

### 良い影響

- read 系 access ログの受け渡しが型付きになり、安全性が上がる
- `AccessLogFilter` の内部実装変更が controller へ漏れにくくなる
- 将来の JSON 構造化ログへつなげやすい

### 注意点

- `AccessLogContext` は request スコープの一時オブジェクトであり、永続化や他層共有の文脈オブジェクトとして使わない
- event 名や fields の設定順に依存しないよう、未設定時の default event は `HTTP_ACCESS` とする

## 既存 ADR との関係

- [adr_007_logging.md](./adr_007_logging.md) の「ログ出力位置を一度に決め、重複出力を避ける」方針を具体化する判断である
- 本 ADR は access ログの責務自体を変えるものではなく、request 内の受け渡し方法を型付きに整理するものである
