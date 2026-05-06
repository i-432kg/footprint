# ADR: JSpecify `@NullMarked` を前提にした Null 安全方針

## ステータス
Superseded

## 補足
本 ADR は `@NullMarked` を全パッケージへ原則適用する前提で記載しているが、実運用上は `presentation` / `infrastructure` における nullable 境界との相性が悪いことが判明した。

現時点の採用方針は [`adr_009_nullable_boundary_policy.md`](./adr_009_nullable_boundary_policy.md) を参照すること。

## 背景
本プロジェクトでは、`domain` を中心に不変条件を強くし、`null` による不正状態や予期しない `NullPointerException` を避けたい。

Java では型システムだけで `null` を十分に表現できないため、JSpecify を導入し、パッケージ単位で `@NullMarked` を付与して nullness を明示する。

## 決定
各パッケージに `package-info.java` を配置し、原則として `@NullMarked` を付与する。

これにより、明示的に `@Nullable` を付けない限り、型は non-null とみなす。

## ルール

### 1. 基本方針
- パッケージ単位で `@NullMarked` を付与する
- `null` を許容する箇所だけ `@Nullable` を付与する
- `null` を許容しない箇所に `@NonNull` は原則付与しない

### 2. `@NonNull` の扱い
- `@NullMarked` 配下では `@NonNull` は冗長なため原則使用しない
- 例外として、外部インターフェース実装やツール都合で明示が必要な場合のみ使用してよい
- 使用する場合は JSpecify と Lombok を混在させず、注釈の意図を明確にする

### 3. `@Nullable` の扱い
- `null` を返す可能性がある戻り値
- `null` を受け取る可能性がある引数
- `null` を保持しうるフィールド

上記には必ず `@Nullable` を付与する。

### 4. `null` と特別値の併用禁止
- `null` と `unknown` / `empty object` / `Optional.empty()` を同じ意味で併用しない
- 不在表現は、`@Nullable`、`Optional`、特別値オブジェクトのいずれか 1 つに統一する

### 5. レイヤ別の扱い
- `domain` / `application`
    - 可能な限り non-null を前提とする
    - `null` を許可する場合は意図を明示する
- `presentation` / `infrastructure`
    - フレームワーク都合で `null` が入りうる箇所がある
    - 境界で `@Nullable` を明示し、内側へ持ち込む前に non-null な型へ詰め替える

### 6. 実装ルール
- factory / constructor では non-null 前提の値を早期検証する
- `Objects.requireNonNull(...)` に依存して契約を後出ししない
- JavaDoc の「null を返す」「null 可」と実装を一致させる

## この方針の狙い
- `null` を例外的なものとして扱い、nullable な箇所を目立たせる
- レビュー時に null 契約を追いやすくする
- ドメインモデルと値オブジェクトの不変条件を強化する
- framework 境界での `null` を明確に隔離する

## 現時点の既知のズレ

### 1. `@NullMarked` 配下で冗長な `@NonNull` を付与している
- `src/main/java/jp/i432kg/footprint/application/command/model/CreatePostCommand.java`
- `src/main/java/jp/i432kg/footprint/application/command/model/CreateReplyCommand.java`
- `src/main/java/jp/i432kg/footprint/infrastructure/security/UserDetailsImpl.java`

### 2. JSpecify ではなく Lombok の `@NonNull` を使っている
- `src/main/java/jp/i432kg/footprint/infrastructure/security/UserDetailsServiceImpl.java`

### 3. `null` を返しているのに `@Nullable` が付いていない
- `src/main/java/jp/i432kg/footprint/infrastructure/datasource/typehandler/*TypeHandler.java`
    - `getNullableResult(...)` が `null` を返す
- `src/main/java/jp/i432kg/footprint/presentation/api/response/mapper/PostResponseMapper.java`
- `src/main/java/jp/i432kg/footprint/presentation/api/response/mapper/ReplyResponseMapper.java`
- `src/main/java/jp/i432kg/footprint/presentation/api/response/mapper/UserProfileResponseMapper.java`

### 4. `@NullMarked` 配下の DTO フィールドが framework 初期状態では `null` を取りうるのに、契約上は non-null のままになっている
- `src/main/java/jp/i432kg/footprint/presentation/api/request/PostRequest.java`
- `src/main/java/jp/i432kg/footprint/presentation/api/request/ReplyRequest.java`
- `src/main/java/jp/i432kg/footprint/presentation/api/request/SignUpRequest.java`

### 5. `null` と特別値オブジェクトを併用している
- `src/main/java/jp/i432kg/footprint/domain/model/Location.java`
    - `unknown()` が `null` を内包する
- `src/main/java/jp/i432kg/footprint/domain/model/Image.java`
    - `hasLocation()` が `location` を `requireNonNull` している
- `src/main/java/jp/i432kg/footprint/presentation/api/ReplyRestController.java`
    - `Optional` から `orElse(null)` で `null` を流している

## 今後の運用
- 新しいパッケージを追加する場合は `package-info.java` に `@NullMarked` を付与する
- nullable な API を新設する場合は、`@Nullable` を付ける理由が説明できるようにする
- 既存コード修正時は、本 ADR の「既知のズレ」を解消する方向で合わせる
