## api design

- http[s]://host:port/api/v1.0/resources[:{id}] `如果不能唯一标识则需要写在 query 里`
- ~~/api/[controller]/action: Big hump~~ 解释一下 `user/login` 对应的资源`authorization`

- `api/products/isexist/{userId}/{productName}`: 这个时有问题的, 不可读 + Action 命名不恰当 + 层级关系[避免层级过深的 URI]
- `api/users/{userId}/products?productName={productName}`

1. uri 代表的是一种资源: 名词+能表达出资源的含义+层次关系

   - Rest 的核心原则是`将API 拆分为逻辑上的资源`: 找出资源

     ```shell
     GET /tickets                           # 获取ticket列表
     GET /tickets/12                        # 查看某个具体的ticket
     POST /tickets                          # 新建一个ticket
     PUT /tickets/12                        # 新建ticket 12
     DELETE /tickets/12                     # 删除ticket 12
     ```

   - 处理关联

     ```shell
     GET /tickets/12/messages               # 获取ticket 12的message列表
     GET /tickets/12/messages/5             # 获取ticket 12的message 5
     POST /tickets/12/messages              # 创建ticket 12的一个message
     PUT /tickets/12/messages/5             # 更新ticket 12的message 5
     DELETE /tickets/12/messages/5          # 删除ticket 12的message 5
     ```

   - 避免层级过深的 URI: uri 会特别长, 使用查询参数

     ```shell
     # GET /zoos/1/areas/3/animals/4
     GET /animals?zoo=1&area=3
     # GET /zoo/ID/animals
     GET /animals?zoo_id=ID 的含义是相同
     ```

   - 过滤: 使用查询参数

     ```shell
     GET /tickets?state=open
     ```

   - 排序: 排序参数通过 `,` 分隔, 排序参数前加 `-` 表示降序排列

     ```shell
     GET /tickets?sort=-priority             # 获取按优先级降序排列的ticket列表
     GET /tickets?sort=-priority,created_at  # 获取按优先级降序排列的ticket列表, 在同一个优先级内, 先创建的ticket排列在前面
     ```

   - 限制 API 返回值的字段: `fields`

     ```shell
     # 提高网络带宽使用率和速度
     GET /tickets?fields=id,subject,customer_name,updated_at&state=open&sort=-updated_at
     ```

   - Response 不要包装: response 的 body 直接就是数据, 不要做多余的包装

2. HTTP 动词 + ~~http 状态码~~

   - GET: 获取 + 200
   - 返回资源 + 201 CREATED
   - POST: 添加
   - PUT: 返回资源, 改变后的完整资源
   - PATCH: 返回资源, 改变的属性
   - DELETE: 删除 + 204 NO CONTENT
   - ~~HEAD: 获取资源的元数据~~
   - ~~OPTIONS: 获取信息, 关于资源的哪些属性是客户端可以改变的~~

   ```js
   // 由于 状态码 就这么多不能详细的描述问题, 所以 status 会设计为全部返回 200, reponse 里的 code 表示具体信息
   200  OK                      - [GET]：服务器成功返回用户请求的数据，该操作是幂等的（Idempotent）.
   201  CREATED                 - [POST/PUT/PATCH]：用户新建或修改数据成功.
   202  Accepted                - [*]：表示一个请求已经进入后台排队（异步任务）
   204  NO CONTENT              - [DELETE]：用户删除数据成功.
   400  INVALID REQUEST         - [POST/PUT/PATCH]：用户发出的请求有错误，服务器没有进行新建或修改数据的操作，该操作是幂等的.
   401  Unauthorized            - [*]：表示用户没有权限（令牌、用户名、密码错误）.
   403  Forbidden               - [*] 表示用户得到授权（与401错误相对），但是访问是被禁止的.
   404  NOT FOUND               - [*]：用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的.
   406  Not Acceptable          - [GET]：用户请求的格式不可得（比如用户请求JSON格式，但是只有XML格式）.
   410  Gone                    -[GET]：用户请求的资源被永久删除，且不会再得到的.
   422  Unprocesable entity     - [POST/PUT/PATCH] 当创建一个对象时，发生一个验证错误.
   500  INTERNAL SERVER ERROR   - [*]：服务器发生错误，用户将无法判断发出的请求是否成功.
   ```

3. 固定返回码: 都返回 200, 具体信息在封装
4. 固定的返回数据结构

   ```json
   {
     // code为0代表调用成功, 其他会自定义的错误码；
     "code": -23400,
     "message": "Invalid Request",
     "data": {}
   }
   ```

5. Hypermedia 超媒体: 连向其他 API 方法，使得用户不查文档

   ```json
   {
     "link": {
       "rel": "collection https://www.example.com/zoos",
       "href": "https://api.example.com/zoos",
       "title": "List of zoos",
       "type": "application/vnd.yourformat+json"
     }
   }
   ```

## GitHub v3S

1. Current Version: `在 Accept 中, 而不是 uri`

   ```js
   Accept: application / vnd.github.v3 + json;
   ```

2. 大量使用 query 参数
3. 客户端错误

   ```js
   // 1. 发送非法JSON会返回 400 Bad Request.
   HTTP/1.1 400 Bad Request
   Content-Length: 35

   {"message":"Problems parsing JSON"}

   // 2. 发送错误类型的JSON值会返回 400 Bad Request.
   HTTP/1.1 400 Bad Request
   Content-Length: 40

   {"message":"Body should be a JSON object"}

   // 3. 发送无效的值会返回 422 Unprocessable Entity.
   HTTP/1.1 422 Unprocessable Entity
   Content-Length: 149

   {
       "message": "Validation Failed",
       "errors": [
            {
            "resource": "Issue",
            "field": "title",
            "code": "missing_field"
            }
        ]
   }
   ```

   | Error Name     | Description                    |
   | -------------- | ------------------------------ |
   | missing        | 资源不存在                     |
   | missing_field  | 资源必需的域没有被设置         |
   | invalid        | 域的格式非法                   |
   | already_exists | 另一个资源的域的值和此处的相同 |

4. 分页:

   ```shell
   # 页码是从1开始的, 当省略参数 ?page 时, 会返回首页
   https://api.github.com/user/repos?page=2&per_page=100

   #应该依赖于header Link提供的信息, 而不要尝试自己去猜或者构造URL
   Link: <https://api.github.com/search/code?q=addClass+user%3Amozilla&page=15>; rel="next",
      <https://api.github.com/search/code?q=addClass+user%3Amozilla&page=34>; rel="last",
      <https://api.github.com/search/code?q=addClass+user%3Amozilla&page=1>; rel="first",
      <https://api.github.com/search/code?q=addClass+user%3Amozilla&page=13>; rel="prev"
   ```

5. Rate Limiting[速率限制]: `429`

   ```shell
   curl -i https://api.github.com/users/whatever

   HTTP/1.1 200 OK
   Server: GitHub.com
   Date: Thu, 27 Oct 2016 03:05:42 GMT
   Content-Type: application/json; charset=utf-8
   Content-Length: 1219
   Status: 200 OK
   X-RateLimit-Limit: 60
   X-RateLimit-Remaining: 48
   X-RateLimit-Reset: 1477540017

   # 超出则会返回错误信息
   HTTP/1.1 403 Forbidden
   Date: Tue, 20 Aug 2013 14:50:41 GMT
   Status: 403 Forbidden
   X-RateLimit-Limit: 60
   X-RateLimit-Remaining: 0
   X-RateLimit-Reset: 1377013266

   {
       "message": "API rate limit exceeded for xxx.xxx.xxx.xxx. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)",
       "documentation_url": "https://developer.github.com/v3/#rate-limiting"
   }
   ```

   | Header Name           | Description                          |
   | --------------------- | ------------------------------------ |
   | X-RateLimit-Limit     | 当前用户被允许的每小时请求数         |
   | X-RateLimit-Remaining | 在当前发送窗口内还可以发送的请求数   |
   | X-RateLimit-Reset     | 按当前速率发送后，发送窗口重置的时间 |

6. 所有的 API 请求必须包含一个有效的 User-Agent 头. 请求头不包含 User-Agent 的请求会被拒绝.

## 前后端分离

1. 前后端依赖严重耦合，不能适应独立快速开发
2. 全栈工程师的难得
3. 前端技术的模块化，可以独立开发部署
4. 前端的独立为后面的服务分布式，微服务及动态扩容提供了基础
5. 前端 code[node] +server

## comparison with grpc

| grpc                          | rest                   |
| ----------------------------- | ---------------------- |
| proto buffer: smaller, faster | json: slower, lager    |
| HTTP/2: lower latency         | HTTP/1: high latency   |
| bi/directional & async        | C/S only               |
| stream support                | Req/Res mechanism only |
| API oriented, no constraints  | CRUD oriented          |
| rpc based                     | http based             |

---

## reference

1. [rest-api simple concept](http://www.ruanyifeng.com/blog/2014/05/restful_api.html)
2. [rest-api design](https://www.cnblogs.com/duanweishi/p/9539219.html)
