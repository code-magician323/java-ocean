## 1. 元字符(**metacharacter**):

> 它们并**不能匹配自身**, 它们定义了字符类、子组匹配和模式重复次数等:

```
.   ^   $   *   +   ?   { }   [ ]   \   |   ( )
```

## **2. annotation**

- illustration
  1. `* + ?` 默认为贪婪模式

|    model    |        description         |
| :---------: | :------------------------: |
|      ^      |      匹配字符串的开头      |
|     \$      |      匹配字符串的结尾      |
|      .      |      除换行符任意字符      |
|     \*      |          0/1 匹配          |
|      +      |          1/n 匹配          |
|      ?      |          0/1 匹配          |
| \*?, +?, ?? |         非贪婪模式         |
|  {n [,m]}   |        匹配 n-m 次         |
|   {n,m}?    |   非贪婪模式[匹配 n 次]    |
|    [...]    |      匹配[]内任意字符      |
|    [a-z]    |     匹配 a-z 任意字符      |
|   [^...]    |     匹配非[]内任意字符     |
|    `x|y`    |        匹配 x 或 y         |
|    (...)    |  指定子组的开始和结束位置  |
|     \       |           反转义           |
|     \b      | 匹配边界[字与空格间的位置] |
|     \B      |          非 `\b`           |
|     \d      |       数字匹配[0-9]        |
|     \D      |          非 `\d`           |
|     \f      |         匹配换页符         |
|     \n      |         匹配换行符         |
|    \num     |      匹配 num[正整数]      |
|     \r      |         匹配回车符         |
|     \t      |       匹配水平制表符       |
|     \v      |       匹配垂直制表符       |
|     \s      |      匹配任何空白字符      |
|     \S      |          非 `\s`           |
|     \w      |      匹配任何字类字符      |
|     \W      |          非 `\w`           |

- sapmle
  ```txt
  1. "er\b"匹配"never"中的"er", 但不匹配"verb"中的"er"
  2. "(...)\1"匹配两个连续的相同字符
  3. [ \f\n\r\t\v] 等价于 \s
  4. [A-Za-z0-9_] 等价于 \w
  ```

<table>
    <tr bgcolor = '#FFFAFA' align='center'>
        <td>(...)</td>
        <td>匹配圆括号中的正则表达式, 或者指定一个子组的开始和结束位置.<br>
        注: <strong>子组的内容可以在匹配之后被 \数字 再次引用</strong><br>
        举个栗子： (\w+) \1 可以字符串 "FishC FishC.com" 中的 "FishC FishC"（注意有空格）</td>
    </tr>
    <tr bgcolor = '#FFFAFA' align='center'>
        <td>\num</td>
        <td>匹配 num, 此处的 num 是一个正整数。到捕获匹配的反向<strong>引用</strong>.例如, "(...)\1"匹配两个连续的相同字符。</td>
    </tr>
    <tr bgcolor = '#FFFAFA' align='center'>
        <td>(?:...)</td>
        <td align = 'left'>匹配 pattern 但不捕获该匹配的子表达式,即它是一个非捕获匹配,不存储供以后使用的匹配。<br>这对于用"or"字符 (|) 组合模式部件的情况很有用。例如,'industr(?:y|ies) 是比 'industry|industries' 更经济的表达式</td>
    </tr>
    <tr bgcolor = '#FFF0F5' align='center'>
        <td>(?=...)</td>
        <td align = 'left'>执行正向预测先行搜索的子表达式,该表达式匹配处于匹配 pattern 的字符串的起始点的字符串。它是一个非捕获匹配,即不能捕获供以后使用的匹配。例如,'Windows (?=95|98|NT|2000)' 匹配"Windows 2000"中的"Windows",但不匹配"Windows 3.1"中的"Windows"。预测先行不占用字符,即发生匹配后,下一匹配的搜索紧随上一匹配之后,而不是在组成预测先行的字符后</td>
    </tr>
    <tr bgcolor = '#FFFAFA' align='center'>
        <td>(?!...)</td>
        <td align = 'left'>执行反向预测先行搜索的子表达式,该表达式匹配不处于匹配 pattern 的字符串的起始点的搜索字符串。它是一个非捕获匹配,即不能捕获供以后使用的匹配。例如,'Windows (?!95|98|NT|2000)' 匹配"Windows 3.1"中的 "Windows",但不匹配"Windows 2000"中的"Windows"。预测先行不占用字符,即发生匹配后,下一匹配的搜索紧随上一匹配之后,而不是在组成预测先行的字符后。</td>
    </tr>
    <tr bgcolor = '#A9A9A9' align='center'>
        <td>(?...)</td>
        <td>(? 开头的表示为正则表达式的扩展语法（下边这些是 Python 支持的所有扩展语法</td>
    </tr>
    <tr bgcolor = '#A9A9A9' align='center'>
        <td>(?aiLmsux)</td>
        <td align = 'left'>
        1. (? 后可以紧跟着 'a', 'i', 'L', 'm', 's', 'u', 'x' 中的一个或多个字符, 只能在正则表达式的开头使用<br>
        2. 每一个字符对应一种匹配标志：re-A（只匹配 ASCII 字符）, re-I（忽略大小写）, re-L（区域设置）, re-M（多行模式）, re-S（. 匹配任何符号）, re-X（详细表达式）, 包含这些字符将会影响整个正则表达式的规则<br>
        3. 当你不想通过 re.compile() 设置正则表达式标志, 这种方法就非常有用啦
        注意, 由于 (?x) 决定正则表达式如何被解析, 所以它应该总是被放在最前边（最多允许前边有空白符）。如果 (?x) 的前边是非空白字符, 那么 (?x) 就发挥不了作用了。</td>
    </tr>
    <tr bgcolor = '#A9A9A9' align='center'>
        <td>(?P<name>...)</td>
        <td>命名组, 通过组的名字（name）即可访问到子组匹配的字符串</td>
    </tr>
    <tr bgcolor = '#A9A9A9' align='center'>
        <td>(?P=name)</td>
        <td>反向引用一个命名组, 它匹配指定命名组匹配的任何内容</td>
    </tr>
    <tr bgcolor = '#A9A9A9' align='center'>
        <td>(?#...)</td>
        <td>注释, 括号中的内容将被忽略</td>
    </tr>
    <tr bgcolor = '#A9A9A9' align='center'>
        <td>(?<=...)</td>
        <td align = 'left'>后向肯定断言。跟前向肯定断言一样, 只是方向相反。
举个栗子：(?<=love)FishC 只匹配前边紧跟着 "love" 的字符串 "FishC"</td>
    </tr>
    <tr bgcolor = '#A9A9A9' align='center'>
        <td>(?<!...)</td>
        <td align = 'left'>后向否定断言。跟前向肯定断言一样, 只是方向相反。举个栗子：(?<!FishC)\.com 只匹配前边不是 "FishC" 的字符串 ".com"</td>
    </tr>
    <tr bgcolor = '#A9A9A9' align='center'>
        <td>(?(id/name)yes-pattern|no-pattern)</td>
        <td align = 'left'>1. 如果子组的序号或名字存在的话, 则尝试 yes-pattern 匹配模式；否则尝试 no-pattern 匹配模式
        2. no-pattern 是可选的
        举个栗子：(<)?(\w+@\w+(?:\.\w+)+)(?(1)>|$) 是一个匹配邮件格式的正则表达式, 可以匹配 <user@fishc.com> 和 'user@fishc.com', 但是不会匹配 '<user@fishc.com' 或 'user@fishc.com>'</td>
    </tr>
</table>
