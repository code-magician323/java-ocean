## curl HTTP cheat sheet

|        type         |     explian      |       explian        |
| :-----------------: | :--------------: | :------------------: |
|       Verbose       |        -v        | --trace-ascii <file> |
|    hide progress    |        -s        |
|     extra info      |   -w "format"    |
|    Write output     |        -O        |      -o <file>       |
|       Timeout       |   -m <seconds>   |
|        POST         |   -d "string"    |       -d @file       |
| multipart formpost  |  -F name=value   |    -F name=@file     |
|         PUT         |    -T <file>     |
|        HEAD         |        -I        |
|    Custom method    |   -X "METHOD"    |
|     Basic auth      | -u user:password |
|   read cookiejar    |    -b <file>     |
|   write cookiejar   |    -c <file>     |
|    send cookies     |  -b "c=1; d=2"   |
|     user-agent      |   -A "string"    |
|      Use proxy      |  -x <host:port>  |
| Headers, add/remove | -H "name: value" |      -H "name:"      |
|  follow redirects   |        -L        |
|  gzipped response   |   --compressed   |
|   Insecure HTTPS    |        -k        |
