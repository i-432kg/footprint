# ADR: 機微情報マスキングは `MaskingTarget` Enum を中心に管理する

## ステータス
Proposed

## 背景
本プロジェクトでは、例外の `details` をログおよび `ProblemDetail` に含めている。

そのため、以下のような機微情報が意図せず露出するリスクがある。

- メールアドレス
- パスワード
- ハッシュ化済みパスワード
- トークン / secret 系の値
- バリデーションエラーの `rejectedValue`

単純な `if (key.contains("password"))` のような条件分岐で実装すると、判定ロジックが散らばりやすく、追加・見直しがしづらい。

## 決定
マスキング対象の識別は `MaskingTarget` Enum を中心に管理する。

ただし、判定は Enum だけに一本化せず、以下の 2 段階で行う。

1. 値の型で判定する
2. key / field / target 名を `MaskingTarget` で判定する

## 目的
- 何を機微情報として扱うかを明文化する
- マスキング条件を 1 か所に集約する
- ログ出力と API レスポンスで同じ基準を使う
- 後から対象を追加しやすくする

## 設計方針

### 1. 型ベース判定を優先する
以下のような値は、キー名に関係なくマスク対象とする。

- `RawPassword`
- `HashedPassword`
- `EmailAddress`

理由:
- キー名に依存しないため取りこぼしが少ない
- 値オブジェクトとして意味が明確

### 2. 文字列キーの判定は `MaskingTarget` Enum に集約する
`MaskingTarget` は以下を持つ。

- 対象名
- マッチ条件
- マスク方法

イメージ:

```java
public enum MaskingTarget {
    PASSWORD(
        key -> containsAny(key, "password", "secret"),
        MaskingStrategy.FULL
    ),
    TOKEN(
        key -> containsAny(key, "token", "credential"),
        MaskingStrategy.FULL
    ),
    EMAIL(
        key -> containsAny(key, "email"),
        MaskingStrategy.EMAIL
    );

    private final Predicate<String> matcher;
    private final MaskingStrategy strategy;

    public boolean matches(final String key) { ... }

    public Object mask(final Object value) { ... }
}
```

### 3. マスク方法は strategy として分ける
マスク方法は対象ごとに異なるため、Enum 内に直接書くより strategy 化した方が見通しがよい。

例:
- `FULL`: `********`
- `EMAIL`: `a********@example.com`
- `PASSTHROUGH`: マスクしない

## 具体案

### `MaskingTarget`
- キー名 / フィールド名 / target 名から対象を判定する Enum
- 例: `PASSWORD`, `TOKEN`, `EMAIL`

### `MaskingStrategy`
- 実際のマスク方法を定義する Enum または関数群
- 例: `FULL`, `EMAIL`

### `SensitiveDataMasker`
- ログ・レスポンス向けに `Map<String, Object>` を再帰的に走査する専用クラス
- `Map`, `List`, 単一値を扱う
- 型判定と `MaskingTarget` 判定の両方をここで使う

イメージ:

```java
public final class SensitiveDataMasker {

    public Map<String, Object> maskMap(Map<String, Object> source) { ... }

    public Object maskValue(String key, Object value, Map<String, Object> context) { ... }

    private Object maskByType(Object value) { ... }

    private Object maskByTarget(String key, Object value) { ... }
}
```

## この設計のメリット
- 機微情報の定義が一覧で読める
- `if` の連鎖を減らせる
- 判定追加時の修正箇所が明確
- ログとレスポンスのポリシー統一がしやすい

## この設計の注意点

### 1. `MaskingTarget` だけでは不十分
キー名だけでは `RawPassword` などを取りこぼす可能性がある。
そのため、型ベース判定は別途必要。

### 2. 完全一致 / 部分一致ルールを決める必要がある
`password`, `hashedPassword`, `accessToken`, `emailAddress` などをどう拾うかを明確にする必要がある。

### 3. `rejectedValue` は文脈付きで判定する
`rejectedValue` 自体のキー名だけでは意味が分からないため、`field`, `target`, 親コンテキストを見て判定する必要がある。

### 4. 文字列値だけに依存しない
値オブジェクトやネスト構造があるため、`String` 前提の実装に寄せすぎない。

## 運用ルール
- 新しい機微情報種別を追加する場合は `MaskingTarget` と必要なら `MaskingStrategy` を追加する
- 値オブジェクトとして機微情報を表す型を追加した場合は型ベース判定にも反映する
- ログ出力と API エラーレスポンスでは同じマスキング実装を使う

## 具体的な採用イメージ
- `GlobalExceptionHandler` から直接マスクロジックを書かない
- `SensitiveDataMasker` を利用して `details` と validation errors を変換する
- `MaskingTarget` は機微情報判定の辞書として使う
