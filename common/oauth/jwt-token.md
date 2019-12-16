## 1. tradiional solution

### session

1. User sends username and password to server
2. After the server passes the authentication, relevant data is saved in the current session (such as user role, login time, etc.)
3. The server returns a session_id to the user and writes the user's cookie
4. Each user subsequent request will pass the session_id back to the server via a cookie
5. The server receives the session_id, finds the data saved in the previous period, and knows the identity of the user

### disadvantage

1. scaling bad
2. Oriented to Stand-alone
3. 如果是服务器集群，或者是跨域的服务导向架构，就要求 session 数据共享，每台服务器都能够读取 session
4. 如果 session 存储的节点挂了，那么整个服务都会瘫痪，体验相当不好，风险也很高

## 2. jwt

1. JWT 的实现方式是将用户信息存储在客户端，服务端不进行保存
2. struct: Header.Payload.Signature[Base64-URL]
   - Header: 令牌的类型 + 使用的签名算法
   - Payload: iss(签发人) + exp(过期时间) + sub(主题) + aud(受众) + nbf(生效时间) + iat(签发时间) + jti(编号) + any custom column
   - Signature: 对前两部分的签名(secret 相当于 password/slat)，防止数据篡改
3. processor
   ![avatar](/static/image/oauth/jwt-processor.png)

4. validate token
   - validate signature, if wrong, server will return 401
   - then validate payload info, if invalid, will return 401 with message, such as token expire etc

## 3. conclusion

1. JWT 在获取 token 后不需要再次返回 server, 需要知道 token 和 secret 就可以算出 signature, 与 token 中的 signature 进行比较, 一致说明用户合法且处于登录状态[内部会校验 time 等参数]
2. 密钥(secret)只有服务器才知道, 不能泄露给用户, 用于 signature 的生成
3. JWT 校验时, 只会比较比较 signature 和校验 payload 中 time 数据等信息

## 4. practice

### wso2is

1. processor
   ![avatar](/static/image/oauth/is-processor.png)
2. 调用不提供 Authorization: Basic BASE64(username:password) 会出现 401, 因为使用 ISServer 需要登录;
3. 随意修改 signature, 在提供 Authorization: Basic BASE64(username:password) 情况下， 都可以验证 token 通过
4. 调用时提供 token 的 header 或 payload 错误会出现 401;
5. 调用时提供 token 的 signature 的值随意修改都可以通过验证;
6. 感觉 wso2is 的 oauth 有点奇怪， 本身实现并不是 JWT 标准的
