# ADR: シークページング SQL は初回表示と続きを取得する場合で分割する

## ステータス

Accepted

## 背景

Footprint の一覧系 API は `lastId` / `size` によるシークページングを採用している。

境界条件は `ORDER BY created_at DESC, id DESC` と一致させる必要があり、概念上は次の複合条件になる。

```sql
created_at < :lastCreatedAt
OR (created_at = :lastCreatedAt AND id < :lastInternalId)
```

このとき、`lastId` が未指定の初回表示と、`lastId` ありで次ページを取得する場合では SQL の形が異なる。

- 初回表示: cursor 行の参照が不要
- 次ページ取得: cursor 行の `created_at` と `id` を参照する必要がある

1 本の SQL に MyBatis の `<if test="lastId != null">` を入れて表現する方法もあるが、今回のケースでは次の差分が発生する。

- `WITH` 句の有無
- cursor 参照用の `JOIN` の有無
- seek 条件そのものの有無

このレベルまで構造が変わると、1 本の動的 SQL へ押し込めた場合に可読性が下がりやすい。

## 決定

シークページングを使う query mapper の SQL は、初回表示用と次ページ取得用で別クエリへ分割する。

具体方針は次のとおりとする。

1. service 層は `lastId == null` かどうかで mapper を呼び分ける
2. mapper は初回表示用と seek 継続取得用で別メソッドを持つ
3. seek 継続取得用の SQL では、cursor 行を CTE で 1 回だけ解決する
4. 1 本の SQL を動的分岐で切り替えるよりも、多少の重複を許容して可読性を優先する

## 理由

### 1. SQL の意図が読みやすい

初回表示 SQL には seek 条件が存在しないため、単純な一覧取得として読める。

次ページ取得 SQL には cursor 参照と seek 条件だけが含まれ、ページ境界のロジックが明確になる。

### 2. `WITH` / `JOIN` を含む動的 SQL を避けられる

`SELECT` の前後で構造が切り替わる動的 SQL は、MyBatis XML では追いづらい。

SQL を分割すれば、`<if test>` は service 層の Java コード側へ押し上げられ、mapper XML は静的な SQL として保てる。

### 3. 今回の重複は許容範囲である

重複するのは主に次の部分である。

- `SELECT` 句
- `LEFT JOIN post_images`
- `ORDER BY`
- `LIMIT`

これらの重複は、ページング境界ロジックを見通しよく保つメリットのほうが大きいと判断する。

## 不採用案

### 1. 1 本の SQL に `<if test="lastId != null">` を入れて分岐する

不採用。

差分が `WHERE` 条件だけであれば許容できるが、今回のように `WITH` 句や cursor 参照の有無まで変わる場合は可読性が下がる。

### 2. サブクエリを重複して書く

不採用。

動作上は成立するが、同じ cursor 行を複数回参照する SQL は意図が読み取りづらく、実行計画の理解もしづらい。

### 3. アプリケーション側で cursor 行を先に取得して 2 クエリに分ける

今回は不採用。

N+1 ではないが、1 リクエストあたりの DB 往復が増えるため、まずは 1 SQL のまま cursor 行を CTE で解決する。

## 影響

### 良い影響

- mapper XML の可読性が上がる
- ページング境界ロジックがレビューしやすくなる
- 初回表示と次ページ取得の責務が明確になる

### 注意点

- mapper メソッド数は増える
- 一覧クエリの `SELECT` / `JOIN` / `ORDER BY` は重複する
- クエリ変更時は、初回表示用と次ページ取得用の両方を更新する必要がある

## 実装メモ

- `PostQueryMapper` は recent / my posts / keyword search を初回表示用と seek 用に分割する
- `ReplyQueryMapper` は my replies を初回表示用と seek 用に分割する
- `PostQueryServiceImpl` と `ReplyQueryServiceImpl` は `lastId` の有無で mapper を呼び分ける
- seek 用 SQL では、cursor 行を `WITH cursor_* AS (...)` で 1 回だけ解決する
