## shell

### 约定

1. 格式

   ```shell
   #!/bin/sh
   ```

2. 等号之间不要有空格
3. 双引号内可以有变量

### 变量

1. \$变量名

   ```shell
   # 使用语句给变量赋值, 如下循环显示出/etc目录下的文件名
   for file in 'ls /etc'
   ```

   ```shell
   for skill in Ada Coffe Action Java; do
       echo "I am good at ${skill}Script"
   done
   ```

2. readonly
3. unset variable_name

   - unset 不能删除只读变量

4. 变量类型

   - 局部变量: 脚本中定义的变量, 仅作用于本 shell 脚本内
   - 环境变量: 所有程序, 包括 shell 启动程序都能访问的环境变量
   - shell 变: shell 程序的特殊变量

### string

1. string 可以单引号, 双引号或者不用引号

   - 单引号: 单引号''之间的字符原样输出, 里面的变量也会失效. 其内部不能再有单引号, 哪怕转义符号都失效
   - 双引号: 双引号里面可以有变量, 可以有转义符号
     ```shell
     n1="abc"
     test="hello, "${n1}""
     test1="hello, ${n1}"
     echo $test $test1
     ```

2. 获取字符串长度: `# 标识变量长度`

   ```shell
   str="abcdef"
   # 输出字符串长度
   echo ${#str}
   ```

3. 获取子字符串

   ```shell
   str="abcdef"
   #下标从左至右, 0 开始.
   echo ${str:1:4}
   ```

4. 查找子字符串

   ```shell
   # //TODO: why?
   str="hello world nihaome"
   # 查找字符 i 或 s 的位置, 反引号
   echo `expr index "$str" is`
   ```

### 数组: ()表示数组, 元素用空格来分割

1. 创建

   ```shell
   array_name=(1 2 3 4 5 6 7)

   # 或者

   array_name=(
       a
       b
       c
   )

   # 或者

   array_name[0]=1
   array_name[1]=2
   array_name[3]=7
   ```

2. 读取

   ```shell
   variable=${array_name[`index`]}
   # @ 符号代替 index 表示获取所有元素
   echo ${variable[@]}
   echo ${variable[*]}

   # 获取数组长度
   length=${#array_name[@]}
   length=${#array_name[*]}
   # 多维数组
   length_n=${#array_name[n]}
   ```

### Shell 传递参数

1. sample
   ```shell
   #!/bin/bash
   echo "Shell 传参测试";
   echo "file name: $0";
   echo "first variable: $1";
   echo "second variable: $2"
   echo "third variable: $3";
   echo "参数个数为: $#"; # 3
   ```
   ```shell
   chmod +x test.sh3
   ./test.sh 1 2 3
   ```

| 参数处理 | 说明                                               |
| :------: | -------------------------------------------------- |
|   `$#`   | 传递到脚本的参数的个数                             |
|   `$*`   | 以一个单字符串显示所有向脚本传递的参数             |
|   `$$`   | 脚本运行的当前进程 ID 号                           |
|   `$!`   | 后台运行的最后一个进程的 ID 号                     |
|   `$@`   | 类似\$\*，使用时许加引号，并在引号中返回每个参数。 |
|   `$-`   | 显示 shell 使用的当前选选项，类似 set 命令         |
|   `$?`   | 显示最后命令的退出状态。0 表示无错误。其他都是错。 |

2. $*与$@的异同

   ```shell
   #!/bin/bash
   echo "-- \$* demo ---"
   for i in "$*";do
       echo $i
   done

   # 1 2 3

   echo "-- \$@ demo ---"
   for i in "$@"; do
       echo $i
   done

   # 1
   # 2
   # 3
   ```

### 运算符

1. 算数运算符: 原生 Bash 不支持简单的数学运算，可以用 awk 和 expr 实现

   - type: `+ - * / % = == !=`
   - sample

     ```shell
     val=`expr 2 + 2`
     echo $val
     # 注意: expr用反引号, 表达式和运算符之间必须有空格, 2+2 就不行
     ```

2. 关系运算符

   - type: `-eq -ne -gt -lt -ge -le`

3. 布尔运算符

   - type: `! -o -a`

4. 逻辑运算符

   - type: `&& ||`

5. 字符串运算符

   - type: `= != -z -n str`

6. 文件测试运算符:

   - type:

   ```shell
   -b file
   -c file
   -d file
   -f file
   -g file
   -k file
   -p file
   -u file
   -r file
   -w file
   -x file
   -s file
   -e file
   ```

### Shell 流程控制

1. if esle

   ```shell
   if condition1
       then
       command1
   elif condition2
       then
       command2
   else
       command
   fi
   ```

2. for 循环

   ```shell
   for var in item1 item2 ... itemN
       do
           command1
           command2
           ...

   done # for循环结束的标识

   # 写成一行
   for var in item1 item2 ... itemN;do command1; command2;...;done;
   ```

3. while 语句

   ```shell
   while condition
   do
       command
   done
   ```

   ```shell
   #!/bin/sh
   int=1
   while(( $int<=5 ))
   do
       echo $int
       let "int++" # let是个关键命令
   done
   ```

4. 无限循环

   ```shell
   while :
   do
       command
   done

   # 或者
   while true
   do
       command
   done

   # 或者
   for (( ; ; ))
   ```

5. until 循环

   ```shell
   until condition
   do
       command
   done
   ```

6. case

   ```shell
   case value in

       mode1)
           command1
           ...
           ;; #case的结束标志
       mode2)
           command2
           ...
           ;;
   esac #case的反写
   ```

   ```shell
   echo '输入 1 到 4 之间的数字:'
   echo '你输入的数字为:'
   read aNum
   case $aNum in
       1)
           echo '你选择了 1'
           ;;
       2)
           echo '你选择了 2'
           ;;
       3)
           echo '你选择了 3'
           ;;
       4)
           echo '你选择了 4'
           ;;
       *)
           echo '你没有输入 1 到 4 之间的数字'
           ;;
   esac
   ```

7. 跳出循环

   ```shell
   #!/bin/bash
   while :
   do
     echo -n "输入 1 到 5 之间的数字:"
     read aNum
     case $aNum in
         1|2|3|4|5)
           echo "你输入的数字为 $aNum!"
           ;;
         *)
           echo "你输入的数字不是 1 到 5 之间的! 游戏结束"
           break
           ;;
     esac
   done
   ```

8. continue

   ```shell
   #!/bin/bash

   while :
   do
     echo -n "输入 1 到 5 之间的数字: "
     read aNum
     case $aNum in
         1|2|3|4|5)
           echo "你输入的数字为 $aNum!"
           ;;
         *)
           echo "你输入的数字不是 1 到 5 之间的!"
           continue
           echo "游戏结束"
           ;;
     esac
   done
   ```

### Shell 函数

1. syntax

   ```shell
   # function 关键字为可选项, 参数也是可选
   [ function ] funname [()]
   {
     action;
     [return int;]
   }
   ```

2. sample

   ```shell
   #!/bin/bash
   demoFun(){
       echo "这是我的第一个 shell 函数!"
   }

   echo "-----函数开始执行-----"
   demoFun
   echo "-----函数执行完毕-----"
   ```

3. sample2: `调用函数返回值, 用$?符号, 函数必须在被调用前定义`

   ```shell
   #!/bin/bash
   funWithReturn(){
       echo "这个函数会对输入的两个数字进行相加运算..."
       echo "输入第一个数字: "
       read aNum
       echo "输入第二个数字: "
       read anotherNum
       echo "两个数字分别为 $aNum 和 $anotherNum !"
       return $(($aNum+$anotherNum))
   }

   funWithReturn
   echo "输入的两个数字之和为 $? !"
   ```

4. sample3: $10不能获取第10个参数, 因为当n>=10时候, 要用$(n)来获取参数

   ```shell
   #!/bin/bash
   funWithParam(){
     echo "第一个参数为 $1 !"
     echo "第二个参数为 $2 !"
     echo "第十个参数为 $10 !"
     echo "第十个参数为 ${10} !"
     echo "第十一个参数为 ${11} !"
     echo "参数总数有 $# 个!"
     echo "作为一个字符串输出所有参数 $* !"
   }

   funWithParam 1 2 3 4 5 6 7 8 9 34 73
   ```

#### Shell 输入/输出重定向

|      命令       |                      说明                      |
| :-------------: | :--------------------------------------------: |
| command > file  |               输出重定向到 file                |
| command < file  |               输出重定向到 file                |
| command >> file |                输出追加到 file                 |
|    n > file     |       文件描述符为 n 的文件重定向到 file       |
|    n >> file    |        文件描述符为 n 的文件追加到 file        |
|     n >& m      |              输出文件 m 和 n 合并              |
|     n <& m      |              输入文件 m 和 n 合并              |
|     << tag      | 开始标记 tag 和结束标记 tag 之间的内容作为输入 |

1. 输出重定向

   ```shell
   # 若file1存在，则被替代。可以用>>追加符号，则不替代。
   command1 > file1
   ```

2. 输入重定向

   ```shell
   # 同时替换输入和输出, 执行command1, 从文件infile读取内容, 然后将输出写入到outfile中
   command1 < infile > outfile
   ```

3. 重定向深入讲解

   - 标准输入文件(stdin): stdin 的文件描述符为 0，
   - 标准输出文件(stdout):stdout 的文件描述符为 1
   - 标准错误文件(stderr):stderr 的文件描述符为 2
   - 如果希望 stderr 重定向到 file, 可以这样写:

     ```shell
     # 模拟终端
     $ command 2 > file
     ```

   - 如果希望将 stdout 和 stderr 合并后重定向到 file

     ```shell
     command > file 2>&1
     # 或者
     command >> file 2>&1
     # 如果输入, 输出都重定向
     command <file1 >file2
     ```

#### /dev/null 文件

1. 若希望执行命令不在屏幕输出, 可重定向到 `/dev/null`

   ```shell
   command > /dev/null
   ```

2. `/dev/null` 为特殊文件, 写入的内容立即不见, 不可读出

   ```shell
   # 如果屏蔽 stdout 和 stderr
   command > /dev/null 2>&1
   ```

#### Shell 文件包含

1. syntax

   ```shell
   . filename #注意点号(.)与文件名之间有空格
   # 或
   source filename
   ```
