#実装メモ
値オブジェクトの生成 -> ファクトリメソッド
　of: 複数の引数、または標準的な生成（例：Coordinate.of(lat, lng)）
　from: 別の型からの変換（例：UserName.from(apiDto.getName())）
　valueOf: 基本型からの変換（Java標準の Integer.valueOf などに近いニュアンス）
　バリデーションを実装する場合はlombokのstaticNameではなく、手動で実装する（コンストラクタはAccessLevel.PRIVATE）
