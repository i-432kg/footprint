# ADR Management

## 目的

`docs/adr` 配下の Architecture Decision Record を、ファイル一覧だけ見ても新旧関係が分かる形で管理する。

## 命名ルール
1
- 形式: `adr_XXX_summary.md`
- `XXX`: `001` から始まる 3 桁ゼロ埋めの連番
- `summary`: ADR の概要を英小文字スネークケースで表した短い識別子

例:

- `adr_001_implementation_notes.md`
- `adr_020_time_and_ulid_generation.md`
- `adr_021_example_decision.md`

## 採番ルール

- 番号が大きいファイルほど新しい設計判断を表す
- 新しい ADR を追加する場合は、必ず次の番号を採番する
- 既存 ADR の番号は後から振り直さない
- ADR が廃止・置換された場合も、元の番号は維持したまま `Status` で表現する

## 既存 ADR の採番方針

既存ファイルは、git 履歴上の追加順を基準に採番した。

- 古い commit から順に採番
- 同一 commit で追加された ADR は、既存ファイル名順で採番

## 運用ルール

1. 新規 ADR 作成時は `docs/adr` の最新番号を確認する
2. 次番号のファイルを `adr_XXX_summary.md` で作成する
3. ファイル冒頭に `# ADR: ...` と `Status` を明記する
4. 既存 ADR を参照する場合は、番号付きファイル名でリンクする

## 記載ルール

ADR 本文は、既存 ADR で共通している最低限の項目を必須とし、それ以外の項目は必要に応じて自由に追加してよい。

### 必須項目

新規 ADR では、少なくとも以下を含める。

1. タイトル
2. ステータス
3. 背景
4. 決定

最小テンプレート:

```md
# ADR: 〇〇

## ステータス
Accepted

## 背景
この判断が必要になった文脈を書く。

## 決定
採用する方針を書く。
```

### 見出し表記

- 見出しは日本語で記載する
- 既存 ADR に `Status` / `Context` / `Decision` など英語見出しのものがあっても、新規 ADR では `ステータス` / `背景` / `決定` を使う
- タイトルは原則 `# ADR: ...` 形式とする

### 任意項目

以下のような項目は、内容に応じて自由に追加してよい。

- 目的
- 問題
- 理由
- 設計方針
- 運用ルール
- 補足
- 選択肢
- 不採用案
- 期待する効果
- 影響
- 実施内容
- 実装メモ

項目名は内容に合っていれば多少の違いは許容するが、読み手が判断の流れを追いやすい見出しにする。

## ADR 一覧

| No. | File | 概要 |
|---|---|---|
| 001 | `adr_001_implementation_notes.md` | 初期実装メモ |
| 002 | `adr_002_domain_service_return_policy.md` | Domain Service の戻り値と例外変換方針 |
| 003 | `adr_003_exception.md` | ErrorCode と HTTP ステータス対応 |
| 004 | `adr_004_final_variable_policy.md` | `final` 変数方針 |
| 005 | `adr_005_id_validation.md` | ULID 系 ID の厳密検証方針 |
| 006 | `adr_006_image_processing_boundary.md` | 画像保存とメタデータ抽出の責務境界 |
| 007 | `adr_007_logging.md` | ログ出力ルール統一 |
| 008 | `adr_008_masking_policy.md` | 機微情報マスキング方針 |
| 009 | `adr_009_nullable_boundary_policy.md` | nullable 境界と `@NullMarked` の適用方針 |
| 010 | `adr_010_nullable_value_object_factory.md` | 値オブジェクト factory の nullability 方針 |
| 011 | `adr_011_nullness.md` | JSpecify `@NullMarked` 前提の null 安全方針 |
| 012 | `adr_012_javadoc_policy.md` | JavaDoc 記述方針 |
| 013 | `adr_013_last_login_recording.md` | 最終ログイン日時更新方針 |
| 014 | `adr_014_mybatis_direct_mapping_exception.md` | MyBatis 直接マッピング方針の例外整理 |
| 015 | `adr_015_query_read_model_nullability.md` | query model の nullability 方針 |
| 016 | `adr_016_seed_dataset_scenario.md` | seed データの固定シナリオ投入方針 |
| 017 | `adr_017_seed_manifest_loader_structure.md` | seed manifest loader 構造方針 |
| 018 | `adr_018_seed_scenario_catalog.md` | seed シナリオ識別子整理方針 |
| 019 | `adr_019_image_storage_repository_deduplication.md` | image storage 実装重複の整理方針 |
| 020 | `adr_020_time_and_ulid_generation.md` | 時刻と ULID 生成の注入方針 |
| 021 | `adr_021_auth_required_and_temporary_presigned_image_url.md` | 画面・API・画像配信の認証必須方針と暫定 presigned URL 運用 |
| 022 | `adr_022_seed_cleanup_ownership.md` | seed cleanup の削除対象を seed ユーザー所有データへ拡張する方針 |
| 023 | `adr_023_seek_pagination_boundary.md` | シークページングの `lastId` / `size` 方針と複合ソートキー境界条件 |
| 024 | `adr_024_problem_detail_error_response_policy.md` | エラーレスポンスを `ProblemDetail` ベースで統一する方針 |
| 025 | `adr_025_seek_pagination_query_split.md` | シークページング SQL を初回表示用と seek 継続取得用へ分割する方針 |
