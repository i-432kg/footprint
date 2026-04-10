# Re-Review

## 対象
- `docs/review/review.md` の High 指摘 `No.1` から `No.3`

## 再レビュー結果
| No. | 観点 | 対象 | 優先度 | 再レビュー結果 | 状況 | 備考 |
|---|---|---|---|---|---|---|
| 1 | DDD / Onion Architecture | `src/main/java/jp/i432kg/footprint/domain/service/PostDomainService.java`<br>`src/main/java/jp/i432kg/footprint/domain/service/ReplyDomainService.java`<br>`src/main/java/jp/i432kg/footprint/domain/service/UserDomainService.java`<br>`src/main/java/jp/i432kg/footprint/config/DomainServiceConfig.java`<br>`src/main/java/jp/i432kg/footprint/application/command/PostCommandService.java`<br>`src/main/java/jp/i432kg/footprint/application/command/ReplyCommandService.java` | High | 問題なし。`domain.service` から Spring `@Service` と `application.exception.*` 依存が除去され、Bean 登録は外側の `config` へ移動している。`NotFound` 変換も application 層へ移っており、依存方向は改善されている。 | クローズ | `domain.service` は pure Java として成立している。 |
| 2 | 実装の統一感 / 不整合 | `src/main/java/jp/i432kg/footprint/domain/value/UserId.java`<br>`src/main/java/jp/i432kg/footprint/domain/value/PostId.java`<br>`src/main/java/jp/i432kg/footprint/domain/value/ReplyId.java`<br>`src/main/java/jp/i432kg/footprint/domain/value/ImageId.java`<br>`src/main/java/jp/i432kg/footprint/domain/helper/UlidValidation.java` | High | 問題なし。ID 系値オブジェクトはすべて `UlidValidation.requireValidUlid(...)` を経由し、入力値そのものを厳格検証する方針に統一されている。`trim()` による検証値と保持値のズレは解消された。 | クローズ | 同種値オブジェクトのルールが 1 か所に集約され、差分混入の余地も下がっている。 |
| 3 | セキュリティ | `src/main/java/jp/i432kg/footprint/presentation/api/GlobalExceptionHandler.java`<br>`src/main/java/jp/i432kg/footprint/logging/masking/SensitiveDataMasker.java`<br>`src/main/java/jp/i432kg/footprint/logging/masking/MaskingTarget.java`<br>`src/main/java/jp/i432kg/footprint/logging/masking/MaskingStrategy.java`<br>`src/main/java/jp/i432kg/footprint/domain/exception/EmailAlreadyUsedException.java`<br>`src/main/java/jp/i432kg/footprint/domain/value/RawPassword.java` | High | 問題なし。例外 `details` は `SensitiveDataMasker` を通してログと `ProblemDetail.details` の両方でマスクされる。`password` / `secret` / `token` 系は固定値、`email` 系は部分マスクされ、`RawPassword` の `rejectedValue` と `EmailAlreadyUsedException` の `emailAddress` 露出は抑止されている。 | クローズ | `EmailAlreadyUsedException` 自体は依然としてメール存在有無を表すが、レビュー指摘の「機微情報露出」は解消している。 |

## 総括
- High 指摘 `No.1` から `No.3` について、新たな問題は確認されませんでした。
- 今回の確認範囲では、修正内容をクローズして問題ないと判断します。

## 補足
- `docs/review/review.md` の Medium / Low 指摘 (`No.4` 以降) は今回の再レビュー対象外です。
- 実装確認は対象ソースのレビューに加え、関連テストの通過結果を前提に判断しています。
