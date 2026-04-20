# `FaviconController` テスト仕様書

## 1. 基本情報

- 対象クラス: `FaviconController`
- 対象パッケージ: `jp.i432kg.footprint.presentation.web`
- 対応するテストクラス: `FaviconControllerTest`
- 作成者: Codex
- 作成日: 2026-04-20

## 2. 対象概要

- 何をするクラスか: `/favicon.ico` へのリクエストを静的な SVG favicon へ案内する Web controller
- 主なメソッド: `favicon()`

## 3. テスト観点

| No. | 区分 | 観点 | 確認内容 |
|---|---|---|---|
| 1 | 正常系 | favicon リダイレクト | `/favicon.ico` 用ハンドラが `/favicon.svg` へのリダイレクト view 名を返すこと |

## 4. テストケース一覧

| No. | 区分 | テストケース | 入力値 / 事前条件 | 期待結果 |
|---|---|---|---|---|
| 1 | 正常系 | SVG favicon へリダイレクトする | なし | `redirect:/favicon.svg` を返す |

## 5. 実装メモ

- controller 単体の戻り値確認で十分
- Spring MVC のルーティングや静的ファイル配信は統合テストではなく、Security / MVC 設定側の責務として扱う

## 6. 対応するテストメソッド

| No. | テストメソッド名 | `@DisplayName` |
|---|---|---|
| 1 | `should_redirectToSvgFavicon_when_faviconIcoIsRequested` | `FaviconController は favicon.ico を favicon.svg へリダイレクトする` |
