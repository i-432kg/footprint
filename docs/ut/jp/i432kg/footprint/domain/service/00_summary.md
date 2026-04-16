# `jp.i432kg.footprint.domain.service` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.domain.service`
- 対象クラス: `PostDomainService`, `ReplyDomainService`, `UserDomainService`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 委譲 | repository の戻り値や検索結果をそのまま返すこと |
| 2 | 例外送出 | ドメインルール違反時に適切なドメイン例外を送出すること |
| 3 | null の扱い | `@Nullable` 引数を受ける API と non-null 前提 API の差を明示すること |
| 4 | 副作用 | 条件に応じて repository が呼ばれる / 呼ばれないことを確認すること |
| 5 | 判定ロジック | サービス内で持つ単純な比較ロジックが期待どおりであること |

## 3. グルーピング方針

- existence 判定系: `PostDomainService.isExistPost`, `UserDomainService.isExistUser`
  - repository の boolean 戻り値をそのまま返す観点で確認する
- 取得 / 検証系: `ReplyDomainService.findReplyById`, `validateParentReplyBelongsToPost`
  - `null` 入力の扱い、`Optional` の返却、投稿不一致時の例外を確認する
- 重複防止系: `UserDomainService.ensureEmailNotAlreadyUsed`
  - repository 判定結果に応じて例外を送出するかを確認する

## 4. テスト実装メモ

- モック化する依存: `PostRepository`, `ReplyRepository`, `UserRepository`
- `ReplyDomainService.findReplyById(null)` は repository を呼ばず `Optional.empty()` を返すことを確認する
- `ReplyDomainService.validateParentReplyBelongsToPost(...)` は `ReplyPostMismatchException` の発生有無を確認する
- `UserDomainService.ensureEmailNotAlreadyUsed(...)` は `EmailAlreadyUsedException` の発生有無に加え、分岐条件ごとの repository 利用を確認する
- 各テストメソッドには `@DisplayName` を付与し、日本語の見出しで観点を明示する
