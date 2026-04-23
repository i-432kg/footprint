# 実装メモ
## 命名規則
getXXXは取得できなかった場合例外をthrowする
findXXXはOptional

## モデルをMybatisからアクセス可能にする
モデルにlombok.@Valueを付与するとMyBatisからアクセスできなくなる
全引数コンストラクタ or @NoArgsConstructor(force = true)で回避する

## MyBatis の自動採番ID取得
insertの戻り値は挿入に成功した件数(int)
insertしたレコードのidを知りたい

- ① @SelectKeyを使用する方法
    - レコード挿入後にidをselectするSQLを実行する
        - MySQLだとSELECT LAST_INSERT_IDが使える
        - AUTO_INCREMENT以外の採番方法にも対応できる
- ② useGeneratedKeysとkeyPropertyを使用する方法
    - XMLのinsertタグにuseGeneratedKeysとkeyPropertyを設定する
        - アノテーションベースのSQLは@Optionsで対応

※①②ともMyBatisがリフレクションでidの値を書き換えるためミュータブルな変数になる

## APIレスポンス, ドメインモデル, DB定義の差分吸収
コマンドクエリ責務分離（CQRS）の導入
※CQRS → Command Query Responsibility Segregation)

## AsIs
jp.i432kg.footprint
├── application
│    ├── command
│    │    ├── model (CreateXxxCommand 等)
│    │    ├── PostCommandService
│    │    ├── ReplyCommandService
│    │    └── UserCommandService
│    └── query
│         ├── model (XxxSummary 等)
│         ├── PostQueryService
│         ├── ReplyQueryService
│         └── UserQueryService
├── config (SecurityConfig, WebMvcConfig 等)
├── domain
│    ├── model (Post, Reply, User, Image 等)
│    ├── repository (各 Repository インターフェース)
│    ├── service (UserDomainService 等)
│    └── value (UserId, Comment, EmailAddress 等の値オブジェクト)
├── infrastructure
│    ├── datasource
│    │    ├── mapper (MyBatis Mapper インターフェース)
│    │    │    ├── query
│    │    │    └── repository
│    │    ├── query (QueryService の実装クラス)
│    │    ├── repository (Repository の実装クラス)
│    │    └── typehandler (MyBatis 用 TypeHandler)
│    ├── security (UserDetailsImpl, UserDetailsServiceImpl, AuthMapper 等)
│    └── storage (ImageRepositoryImpl 等)
├── presentation
│    ├── api (JSON を返却する REST API)
│    │    ├── request (Request DTO)
│    │    ├── response (Response DTO & Mapper)
│    │    ├── PostRestController
│    │    ├── ReplyRestController
│    │    └── UserRestController
│    ├── web (HTML/画面遷移を制御する Controller)
│    │    └── RootController
│    ├── helper (Converter, Constant 等)
│    └── validation (カスタムバリデーション)
└── FootprintApplication (起動クラス)

### ToBe
jp.i432kg.footprint
├── application
│    ├── service (Command: 更新系。既存のService)
│    │    └── PostApplicationService
│    └── query   (Query: 参照系。新しいインターフェース)
│         ├── PostQueryService (interface)
│         └── model (参照専用のモデル/DTO)
│              └── PostSummary
├── domain
│    ├── model (Command: 既存のドメインモデル)
│    │    └── Post
│    └── repository (Command: 既存のリポジトリ)
│         └── PostRepository
├── infrastructure
│    ├── datasource
│    │    ├── query (Query: 参照系の実装)
│    │    │    └── PostQueryServiceImpl (MyBatis等を使用)
│    │    └── impl (Command: リポジトリの実装)
│    │         └── PostRepositoryImpl
│    └── mapper
│         ├── PostMapper (Command用)
│         └── PostQueryMapper (Query用: JOIN等を多用するSQL)
└── presentation
     └── api
          └── PostRestController (ServiceとQueryServiceの両方を使用)

## 命名
値オブジェクトの生成 -> ファクトリメソッド
　of: 複数の引数、または標準的な生成（例：Coordinate.of(lat, lng)）
　from: 別の型からの変換（例：UserName.from(apiDto.getName())）
　valueOf: 基本型からの変換（Java標準の Integer.valueOf などに近いニュアンス）
　バリデーションを実装する場合はlombokのstaticNameではなく、手動で実装する（コンストラクタはAccessLevel.PRIVATE）

## 例外ルール

- InvalidValueException に追加してよい factory は、3つ以上のVOで再利用見込みがあるものだけ
- VO名を含む factory は作らない
  - 例: invalidPostTitle() は作らない
- 業務固有条件は VO 側に残す
  - 例: must_not_start_with_slash
- メッセージより reason を安定キーとして扱う


