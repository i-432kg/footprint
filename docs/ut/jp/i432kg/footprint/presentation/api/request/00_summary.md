# `jp.i432kg.footprint.presentation.api.request` パッケージ UT仕様書サマリー

## 1. 対象概要

- 対象パッケージ: `jp.i432kg.footprint.presentation.api.request`
- 対象クラス: `PostRequest`, `ReplyRequest`, `SignUpRequest`
- 個別仕様書: 各クラスごとに `ファイル名_test_design.md` を作成

## 2. 横断観点

| No. | 観点 | 内容 |
|---|---|---|
| 1 | Bean Validation | `jakarta.validation.Validator` を使い、 DTO に付与された制約が想定どおりに動作すること |
| 2 | 必須/任意 | 必須項目と任意項目の違いが制約として表現されていること |
| 3 | 形式検証 | ULID、メールアドレス、ASCII 可視文字、制御文字禁止などの形式制約を確認すること |
| 4 | 境界値 | 文字数上限・下限、生年月日の当日許容など境界条件を確認すること |

## 3. グルーピング方針

- 投稿作成: `PostRequest`
  - `imageFile` 必須、空ファイル拒否、`comment` の長さと制御文字制約を確認する
- 返信作成: `ReplyRequest`
  - `parentReplyId` 任意、`message` 必須、ULID 形式、長さ、制御文字制約を確認する
- サインアップ: `SignUpRequest`
  - `userName`, `email`, `password`, `birthDate` の必須、形式、長さ、日付制約を確認する

## 4. テスト実装メモ

- DTO 単体の UT とし、`Validator` を直接使って violation を確認する
- 1 テスト 1 観点を基本とし、各フィールドの代表的な正常系・異常系・境界値を確認する
- `MultipartFile` は `MockMultipartFile` で空/非空を切り替える
- message 文字列そのものより、`propertyPath` と violation の有無を主に確認する
