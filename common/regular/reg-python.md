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
