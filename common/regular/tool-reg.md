- [Regex](#regex)
  - [Regex Expression Syntax](#regex-expression-syntax)
    - [元字符(**metacharacter**):](#%E5%85%83%E5%AD%97%E7%AC%A6metacharacter)
    - [**annotation**](#annotation)
  - [distinction-java](#distinction-java)
    - [区别](#%E5%8C%BA%E5%88%AB)
    - [Matcher 类的方法](#matcher-%E7%B1%BB%E7%9A%84%E6%96%B9%E6%B3%95)
      - [索引方法](#%E7%B4%A2%E5%BC%95%E6%96%B9%E6%B3%95)
      - [研究方法](#%E7%A0%94%E7%A9%B6%E6%96%B9%E6%B3%95)
      - [替换方法](#%E6%9B%BF%E6%8D%A2%E6%96%B9%E6%B3%95)
      - [start 和 end 方法](#start-%E5%92%8C-end-%E6%96%B9%E6%B3%95)
      - [matches 和 lookingAt 方法](#matches-%E5%92%8C-lookingat-%E6%96%B9%E6%B3%95)
      - [replaceFirst 和 replaceAll 方法](#replacefirst-%E5%92%8C-replaceall-%E6%96%B9%E6%B3%95)
      - [appendReplacement 和 appendTail 方法](#appendreplacement-%E5%92%8C-appendtail-%E6%96%B9%E6%B3%95)
    - [PatternSyntaxException 类的方法](#patternsyntaxexception-%E7%B1%BB%E7%9A%84%E6%96%B9%E6%B3%95)
  - [distinction-python](#distinction-python)
    - [method](#method)
      - [re.match](#rematch)
      - [re.search](#research)
      - [re.sub](#resub)
      - [re.compile](#recompile)
      - [re.finditer](#refinditer)
      - [re.split](#resplit)
    - [flags 标识诠释](#flags-%E6%A0%87%E8%AF%86%E8%AF%A0%E9%87%8A)
    - [正则表达式对象](#%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F%E5%AF%B9%E8%B1%A1)
    - [python-extensions: xpath](#python-extensions-xpath)

# Regex

## Regex Expression Syntax

### 元字符(**metacharacter**):

> 它们并**不能匹配自身**, 它们定义了字符类、子组匹配和模式重复次数等:

```
.   ^   $   *   +   ?   { }   [ ]   \   |   ( )
```

### **annotation**

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

## distinction-java

### 区别

- 在其他语言中, `\` 表示转义; 在 java 中 `\\` 表示转义; **java 中 `\\` 等价于其他语言中的`\`.**

### Matcher 类的方法

#### 索引方法

- 功能: **索引方法提供了有用的索引值,精确表明输入字符串中在哪能找到匹配**
- api:

  > public int start() [返回以前匹配的初始索引],
  > public int start(int group) [返回在以前的匹配操作期间,由给定组所捕获的子序列的初始索引],
  > public int end() [返回最后匹配字符之后的偏移量]
  > public int end(int group) [返回在以前的匹配操作期间,由给定组所捕获子序列的最后字符之后的偏移量]

#### 研究方法

- 功能: **研究方法用来检查输入字符串并返回一个布尔值,表示是否找到该模式**
- api:

  > public boolean lookingAt() [尝试将从区域开头开始的输入序列与该模式匹配],
  > public boolean find() [尝试查找与该模式匹配的输入序列的下一个子序列],
  > public boolean find(int start) [重置此匹配器,然后尝试查找匹配该模式、从指定索引开始的输入序列的下一个子序列]
  > public boolean matches() [尝试将整个区域与模式匹配]

#### 替换方法

- 功能: **替换方法是替换输入字符串里文本的方法**
- api:

  > public Matcher appendReplacement(StringBuffer sb, String replacement) [实现非终端添加和替换步骤],
  > public StringBuffer appendTail(StringBuffer sb) [实现终端添加和替换步骤],
  > public String replaceAll(String replacement) [替换模式与给定替换字符串相匹配的输入序列的每个子序列]
  > public String replaceFirst(String replacement) [替换模式与给定替换字符串匹配的输入序列的第一个子序列]
  > public static String quoteReplacement(String s) [返回指定字符串的字面替换字符串. 这个方法返回一个字符串,就像传递给 Matcher 类的 appendReplacement 方法一个字面字符串一样工作]

#### start 和 end 方法

- 功能: **下面是一个对单词 "cat" 出现在输入字符串中出现次数进行计数的例子**

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches
{
    private static final String REGEX = "\\bcat\\b";
    private static final String INPUT ="cat cat cat cattie cat";

    public static void main( String args[] ){
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(INPUT); // 获取 matcher 对象
        int count = 0;

        while(m.find()) {
            count++;
            System.out.println("Match number "+count);
            System.out.println("start(): "+m.start());
            System.out.println("end(): "+m.end());
        }
    }
}

// result
/**
    Match number 1
    start(): 0
    end(): 3
    Match number 2
    start(): 4
    end(): 7
    Match number 3
    start(): 8
    end(): 11
    Match number 4
    start(): 19
    end(): 22
*/
```

#### matches 和 lookingAt 方法

- 功能: **matches 和 lookingAt 方法都用来尝试匹配一个输入序列模式。它们的不同是 matches 要求整个序列都匹配，而 lookingAt 不要求。lookingAt 方法虽然不需要整句都匹配，但是需要从第一个字符开始匹配。**
- demo

  ```java
  import java.util.regex.Matcher;
  import java.util.regex.Pattern;

  public class RegexMatches
  {
      private static final String REGEX = "foo";
      private static final String INPUT = "fooooooooooooooooo";
      private static final String INPUT2 = "ooooofoooooooooooo";
      private static Pattern pattern;
      private static Matcher matcher;
      private static Matcher matcher2;

      public static void main( String args[] ){
          pattern = Pattern.compile(REGEX);
          matcher = pattern.matcher(INPUT);
          matcher2 = pattern.matcher(INPUT2);

          System.out.println("Current REGEX is: "+REGEX);
          System.out.println("Current INPUT is: "+INPUT);
          System.out.println("Current INPUT2 is: "+INPUT2);

          System.out.println("lookingAt(): "+matcher.lookingAt());
          System.out.println("matches(): "+matcher.matches());
          System.out.println("lookingAt(): "+matcher2.lookingAt());
      }
  }
  // result
  /**
      Current REGEX is: foo
      Current INPUT is: fooooooooooooooooo
      Current INPUT2 is: ooooofoooooooooooo
      lookingAt(): true
      matches(): false
      lookingAt(): false
  */

  ```

#### replaceFirst 和 replaceAll 方法

- 功能: **replaceFirst 和 replaceAll 方法用来替换匹配正则表达式的文本。不同的是，replaceFirst 替换首次匹配，replaceAll 替换所有匹配**
- demo

```java
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    public class RegexMatches
    {
        private static String REGEX = "dog";
        private static String INPUT = "The dog says meow. " +
                                        "All dogs say meow.";
        private static String REPLACE = "cat";

        public static void main(String[] args) {
            Pattern p = Pattern.compile(REGEX);
            // get a matcher object
            Matcher m = p.matcher(INPUT);
            INPUT = m.replaceAll(REPLACE);
            System.out.println(INPUT);   // The cat says meow. All cats say meow.
        }
    }
```

#### appendReplacement 和 appendTail 方法

- 功能: **Matcher 类也提供了 appendReplacement 和 appendTail 方法用于文本替换**
- demo

```java
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    public class RegexMatches
    {
        private static String REGEX = "a*b";
        private static String INPUT = "aabfooaabfooabfoobkkk";
        private static String REPLACE = "-";

        public static void main(String[] args) {
            Pattern p = Pattern.compile(REGEX);
            // 获取 matcher 对象
            Matcher m = p.matcher(INPUT);
            StringBuffer sb = new StringBuffer();
            while(m.find()){
                m.appendReplacement(sb,REPLACE);
            }
            m.appendTail(sb);
            System.out.println(sb.toString()); // -foo-foo-foo-kkk
        }
    }
```

### PatternSyntaxException 类的方法

- PatternSyntaxException 是一个非强制异常类，它指示一个正则表达式模式中的语法错误。
- PatternSyntaxException 类提供了下面的方法来帮助我们查看发生了什么错误。
- api
  > public String getDescription() [获取错误的描述]
  > public int getIndex() [获取错误的索引]
  > public String getPattern() [获取错误的正则表达式模式]
  > public String getMessage() [返回多行字符串，包含语法错误及其索引的描述、错误的正则表达式模式和模式中错误索引的可视化指示]

## distinction-python

- re 模块使 Python 语言拥有全部的正则表达式功能
- compile 函数根据一个模式字符串和可选的标志参数生成一个正则表达式对象。该对象拥有一系列方法用于正则表达式匹配和替换。

### method

#### re.match

> re.match(pattern, string, flags=0)

- 功能: **尝试从字符串的起始位置匹配一个模式,如果不是<font color ='red'>起始位置</font>匹配成功的话,match()就返回 none**
- 参数:

  > pattern [匹配的正则表达式],
  > string [要匹配的字符串],
  > flags [标志位,用于控制正则表达式的匹配方式,如：是否区分大小写,多行匹配等等]

- demo

  ```python
  import re
  print(re.match('www', 'www.runoob.com').span())  # (0, 3)  在起始位置匹配
  print(re.match('com', 'www.runoob.com'))         # None 不在起始位置匹配

  line = "Cats are smarter than dogs"
  matchObj = re.match( r'(.*) are (.*?) .*', line, re.M|re.I)
  # matchObj.group() :  Cats are smarter than dogs
  # matchObj.group(1) :  Cats
  # matchObj.group(2) :  smarter
  ```

#### re.search

> re.search(pattern, string, flags=0)

- 功能: **扫描整个字符串并返回第一个成功的匹配**
- 参数:
  > pattern [匹配的正则表达式],
  > string [要匹配的字符串],
  > flags [标志位,用于控制正则表达式的匹配方式,如：是否区分大小写,多行匹配等等]
- demo

  ```python
  import re
  print(re.match('www', 'www.runoob.com').span())  # (0, 3)  在起始位置匹配
  print(re.match('com', 'www.runoob.com'))         # (11, 14) 不在起始位置匹配

  line = "Cats are smarter than dogs"
  matchObj = re.match( r'(.*) are (.*?) .*', line, re.M|re.I)
  # matchObj.group() :  Cats are smarter than dogs
  # matchObj.group(1) :  Cats
  # matchObj.group(2) :  smarter
  ```

#### re.sub

> re.sub(pattern, repl, string, count=0, flags=0)

- 功能: **用于替换字符串中的匹配项**
- 参数:
  > pattern [匹配的正则表达式],
  > string [要匹配的字符串],
  > flags [标志位,用于控制正则表达式的匹配方式,如：是否区分大小写,多行匹配等等],
  > repl [替换的字符串,也可为一个函数],
  > count [模式匹配后替换的最大次数,默认 0 表示替换所有的匹配]
- demo

  ```python
  import re

  phone = "2004-959-559 # 这是一个国外电话号码"
  # 删除字符串中的 Python注释
  num = re.sub(r'#.*$', "", phone)
  print ("电话号码是: ", num)  # 电话号码是:  2004-959-559

  # 删除非数字(-)的字符串
  num = re.sub(r'\D', "", phone)
  print ("电话号码是: ", num)  # 电话号码是 :  2004959559
  ```

#### re.compile

> re.compile(pattern[, flags])

- 功能: **函数用于编译正则表达式,生成一个正则表达式（ Pattern ）对象,<font color = 'red'>供 match() 和 search() 和 findall()这三个函数使用</font>**
- 参数:
  > 说明: 这里的 match 与之前的不一样
  > pattern [匹配的正则表达式],
  > flags [标志位,用于控制正则表达式的匹配方式,如：是否区分大小写,多行匹配等等],
- demo

  ```python
  import re

  pattern = re.compile(r'\d+')
  m = pattern.match('one12twothree34four')        # none
  m = pattern.match('one12twothree34four', 2, 10) # none 从'e'的位置开始匹配,没有匹配
  m = pattern.match('one12twothree34four', 3, 10) # ok
  print (m)  # <_sre.SRE_Match object at 0x10a42aac0>
  # group([group1, …]) 方法用于获得一个或多个分组匹配的字符串,当要获得整个匹配的子串时,可直接使用 group() 或 group(0)；
  m.group(0)   # '12' 可省略 0
  # start([group]) 方法用于获取分组匹配的子串在整个字符串中的起始位置（子串第一个字符的索引）,参数默认值为 0
  m.start(0)   # 3 可省略 0
  # end([group]) 方法用于获取分组匹配的子串在整个字符串中的结束位置（子串最后一个字符的索引+1）,参数默认值为 0；
  m.end(0)  # 5
  # span([group]) 方法返回 (start(group), end(group))
  m.span(0) # (3, 5)

  pattern = re.compile(r'([a-z]+) ([a-z]+)', re.I)   # re.I 表示忽略大小写
  m = pattern.match('Hello World Wide Web')
  m.groups()     # ('Hello', 'World')

  # findall
  pattern = re.compile(r'\d+')   # 查找数字
  result1 = pattern.findall('runoob 123 google 456')
  result2 = pattern.findall('run88oob123google456', 0, 10)

  print(result1)  # ['123', '456']
  print(result2)  # ['88', '12']
  ```

#### re.finditer

> re.finditer(pattern, string, flags=0)

- 功能: **和 findall 类似,在字符串中找到正则表达式所匹配的所有子串,并把它们作为一个迭代器返回**
- 参数:
  > pattern [匹配的正则表达式],
  > string [要匹配的字符串],
  > flags [标志位,用于控制正则表达式的匹配方式,如：是否区分大小写,多行匹配等等]
- demo

  ```python
  import re

  it = re.finditer(r"\d+","12a32bc43jf3")
  for match in it:
  print (match.group())  # 12 32 43 3
  ```

#### re.split

> re.split(pattern, string[, maxsplit=0, flags=0])

- 功能: **split 方法按照能够匹配的子串将字符串分割后返回列表**
- 参数:
  > pattern [匹配的正则表达式],
  > string [要匹配的字符串],
  > flags [标志位,用于控制正则表达式的匹配方式,如：是否区分大小写,多行匹配等等],
  > maxsplit [分隔次数,maxsplit=1 分隔一次,默认为 0,不限制次数]
- demo

  ```python
  import re
  # '\W+' 匹配,但是不要, 最后有个 ''
  re.split('\W+', 'runoob, runoob, runoob.')  # ['runoob', 'runoob', 'runoob', '']
  re.split('\W+', ' runoob, runoob, runoob.', 1)  # ['', 'runoob, runoob, runoob.']
  # '(\W+)' 匹配,但是要, 最后有个 ''
  re.split('(\W+)', ' runoob, runoob, runoob.')  # ['', ' ', 'runoob', ', ', 'runoob', ', ', 'runoob', '.', '']
  ```

### flags 标识诠释

> re.I 忽略大小写
> re.L 表示特殊字符集 \w, \W, \b, \B, \s, \S 依赖于当前环境
> re.M 多行模式
> re.S 即为 . 并且包括换行符在内的任意字符（. 不包括换行符）
> re.U 表示特殊字符集 \w, \W, \b, \B, \d, \D, \s, \S 依赖于 Unicode 字符属性数据库
> re.X 为了增加可读性,忽略空格和 # 后面的注释

### 正则表达式对象

- re.RegexObject **re.compile() 返回 RegexObject 对象**
- re.MatchObject
  > group() 返回被 RE 匹配的字符串。
  > start() 返回匹配开始的位置
  > end() 返回匹配结束的位置
  > span() 返回一个元组包含匹配 (开始,结束) 的位置

### python-extensions: xpath

- [reference](http://www.w3school.com.cn/xpath/xpath_syntax.asp)
- 在 XPath 中,有七种类型的节点：元素、属性、文本、命名空间、处理指令、注释以及文档节点（或称为根节点）
- demo
  > 示例
  > book_type_urls = selector.xpath('//ul[@class="navigation"]//li//ul//li//a/@href')
  > book_name = selector.xpath('//div[@class="mysw"]//h1/text()')
  > book_price = selector.xpath('//div[@class="s1"]//h2//font[2]/text()')
  > book_image_url = selector.xpath('//img[@class="simg"]/@src')
