# 分からない点を正確にぬき出せれば答えは出る

# CompilerとInterpreterの違い
Compileしてbinaryファイルを出力してから実行するか、その場で直接実行されるかの違い

# 飛越命令, Goto, Jumpとは

# Instruction 番地

# Goto のバックパッチ
## 2step
## step1
1: Goto label -> label: ?  label表を作成
2: label      -> label: 2  label表に2を格納
## step2
1: Goto label -> lable: 2を参照し、 Goto 2 に変換
2: label

## 1step
1: Goto label  -> label : 1  labelに1を入れる
2: Goto label  -> label : 2
3: Label       -> label : 1  から命令の番地1を取り出し、Gotoを3 に書き換え。
書き換え場所は一箇所でなく、label: 2も同時に書き換える。
Load命令 +　番地部分
0101_0001920010010 <= イメージ

# Assemblerをなぜ学ぶのか
Assemblerは何を処理し、コンパイラは何を処理するのか

# Assemblerの処理の全体像
メモリの動的割り当てはどうやって行うのか
OSとの役割分担は?
Assemblerプログラムはsecureなのか？

# ガーベッジコレクションなどはどのレイヤーでどう実装されるのか

# Tokenizeの結果は文字列としてではなくTokenとして構文解析へ渡される
字句解析の規則は字句の規則とし、構文の規則には含めない。

# One pass コンパイラ
構文解析が字句解析ルーチンを呼び出すタイプ
・最適化の層が無い
・デバッグが目的
・構文解析までがワンパスで行われるものが主流

# 曖昧さ
if then else 
=> 文法規則を変更することで曖昧さを消せるが、直感的に分かりにくくなるので、別のルールで解消する方法が考えられる。

# 正規右辺文法
=> 正規表現を使った文法