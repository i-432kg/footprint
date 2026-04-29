# `jp.i432kg.footprint.application.command.service` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.application.command.service`
- 対象クラス: `PostCommandService`, `ReplyCommandService`, `UserCommandService`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | 事前検証 | リソース存在確認や重複確認を先に行い、不正時は repository や port を呼ばないこと |
| 2 | 永続化 | ドメインモデルを組み立てて repository へ保存委譲すること |
| 3 | 例外変換 | `IOException`, `ImageProcessingException`, `DataAccessException` をユースケース例外へ変換すること |
| 4 | 補償処理 | 投稿作成失敗時に保存済み画像を削除すること |
| 5 | 分岐 | 親返信あり / なし、正常 / 異常などの分岐ごとに副作用が正しいこと |
| 6 | 固定値生成 | `Clock.fixed(...)` と固定 ID generator により、保存対象の `createdAt` / ID を明示的に検証すること |
| 7 | 成功イベントログ | 投稿作成・返信作成・ユーザー登録成功時に `event` と主要識別子を key-value で出力できること |
| 8 | 補助 warning ログ | 補償処理失敗など、一次障害を隠さない warning ログを必要箇所で残せること |

## 3. グルーピング方針

- 投稿系: `PostCommandService`
  - ユーザー存在確認、画像保存、メタデータ抽出、投稿保存、補償削除を確認する
- 返信系: `ReplyCommandService`
  - 投稿存在確認、ユーザー存在確認、親返信の存在・投稿整合性確認、保存、返信数更新を確認する
- ユーザー系: `UserCommandService`
  - メール重複確認、同一 `username` + 別 `email` の登録許可、パスワードハッシュ化、保存失敗時の例外変換を確認する

## 4. テスト実装メモ

- モック化する依存:
  - `PostRepository`, `ReplyRepository`, `UserRepository`
  - `ImageStorage`, `ImageMetadataExtractor`
  - `PostDomainService`, `ReplyDomainService`, `UserDomainService`
  - `PasswordEncoder`
- 固定化する値:
  - `Clock.fixed(...)` による `createdAt`
  - `application.port.id.*IdGenerator` の test double による生成 ID
- `PostCommandService`, `ReplyCommandService` は保存対象の `createdAt` と生成 ID を検証する
- `UserCommandService` は生成された `UserId` を検証する
- `PostCommandService` の cleanup 失敗は再送出せずログのみなので、UT は一次例外優先に加え、必要に応じて warning ログも確認する
- ログ観点を追加する場合は `footprint.audit` / `footprint.app` に `ListAppender<ILoggingEvent>` を付与する
- 各テストメソッドには `@DisplayName` を付与し、日本語の見出しで観点を明示する
