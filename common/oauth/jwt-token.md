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
3. 如果是服务器集群, 或者是跨域的服务导向架构, 就要求 session 数据共享, 每台服务器都能够读取 session
4. 如果 session 存储的节点挂了, 那么整个服务都会瘫痪, 体验相当不好, 风险也很高

## 2. jwt

1. JWT 是一个`自包含`的访问令牌[带有声明和过期的受保护数据结构], 其实现方式是将用户信息存储在客户端, 服务端不进行保存
2. 作用: 用来在身份提供者和服务提供者间传递安全可靠的信息
3. struct: Header.Payload.Signature[Base64-URL]
   - Header: 令牌的类型 + 使用的签名算法
   - Payload: iss(签发人) + exp(过期时间) + sub(主题) + aud(受众) + nbf(生效时间) + iat(签发时间) + jti(编号) + any custom column
   - Signature: 对前两部分的签名(secret 相当于 password/slat), 防止数据篡改. 非对称加密算法
4. processor

   ![avatar](/static/image/oauth/jwt-processor.png)

   ![avatar](/static/image/oauth/jwt-workflow.png)

5. validate token

   - validate signature, if wrong, server will return 401
   - then validate payload info, if invalid, will return 401 with message, such as token expire etc

6. feature

   - 不可撤回: API 了解了密钥材料, 它就可以验证自包含的令牌, 而无需与发行者进行通信
   - JWT 默认是不加密, 可以用密钥再加密一次原始 Token
   - JWT 应该使用 HTTPS 协议传输减少盗用
   - JWT 去中心化的思想[Validate]
     - 资源收到第一个请求之后, 会去 id4 服务器获取公钥, 然后用公钥验证 token 是否合法
     - 如果合法进行后面的有效性验证
     - 有且只有第一个请求才会去 id4 服务器请求公钥
     - 后面的请求都会用第一次请求的公钥来验证
     - JWT 本身包含了认证信息, 任何人都可以获得该令牌的所有权限. JWT 的有效期应该设置得比较短. 对于一些比较重要的权限, 使用时应该再次对用户进行认证。

## 4. Reference Token: 不携带任何用户数据且可撤回

1. concept

   - 服务端会对 Token 进行持久化

2. work folow

   - 客户端请求资源端的时候, 资源端需要每次都去服务端通信去验证 Token 的合法性
   - 使用引用令牌时 IdentityServer 会将令牌的内容存储在数据存储中,
   - 并且只会将此令牌的唯一标识符发回给客户端
   - 接收此引用的 API 必须打开与 IdentityServer 的反向通道通信以验证令牌

   ![avatar](/static/image/oauth/reference-token.png)

## 5. conclusion

1. JWT 在获取 token 后不需要再次返回 server, 需要知道 token 和 secret 就可以算出 signature, 与 token 中的 signature 进行比较, 一致说明用户合法且处于登录状态[内部会校验 time 等参数]
2. 密钥(secret)只有服务器才知道, 不能泄露给用户, 用于 signature 的生成
3. JWT 校验时, 只会比较比较 signature 和校验 payload 中 time 数据等信息

## 4. practice

---

### wso2is

1. indroduce

   - processor
     ![avatar](/static/image/oauth/is-processor.png)
   - 调用不提供 Authorization: Basic BASE64(username:password) 会出现 401, 因为使用 ISServer 需要登录;
   - 随意修改 signature, 在提供 Authorization: Basic BASE64(username:password) 情况下, 都可以验证 token 通过
   - 调用时提供 token 的 header 或 payload 错误会出现 401;
   - 调用时提供 token 的 signature 的值不能随意修改;

2. config OAuth function

   - Inbound Authentication Configuration --> OAuth/OpenID Connect Configuration --> Config --> enable Enable Audience Restriction and choose Allowed Grant Types

3. get access token

   ```shell
   curl -v -k -X POST --user OAUTH_CLIENT_KEY:OAUTH_CLIENT_SECRET -H "Content-Type: application/x-www-form-urlencoded;charset=UTF-8" -d "grant_type=client_credentials&username=admin&password=admin" https://101.132.45.28:9443/oauth2/token
   ```

4. code to get token

```c#
// mapping data model
public class JWTTokenModel
{
    [JsonProperty("access_token")]
    public string AccessToken { get; set; }

    [JsonProperty("refresh_token")]
    public string RefreshToken { get; set; }

    [JsonProperty("token_type")]
    public string TokenType { get; set; }

    [JsonProperty("expires_in")]
    public int ExpiressIn { get; set; }
}


// app settings
"JWTConfiguration": {
    "BaseUrl": "https://101.132.45.28:9444",
    "Timeout": "10000",
    "GrantType": "client_credentials",
    "UserName": "admin",
    "Password": "admin",
    "OAuthClientKey": "OAUTH_CLIENT_KEY",
    "OAuthClientSecret": "OAUTH_CLIENT_SECRET"
  }


// startup register config model: JWTConfiguration
public IConfiguration Configuration { get; }
public void ConfigureServices(IServiceCollection services)
{
   services.Configure<JWTConfiguration>(Configuration.GetSection("JWTConfiguration"));
}


// get token
public class JWTTokenClient : IJWTTokenClient
{
    private const string Path = "/oauth2/token";
    private readonly string _baseUrl;
    private readonly int _timeout;
    private readonly string _grantType;
    private readonly string _userName;
    private readonly string _password;
    private readonly string _oAuthClientKey;
    private readonly string _oAuthClientSecret;
    private readonly ILogger<JWTTokenClient> _logger;

    public JWTTokenClient(ILogger<JWTTokenClient> logger,
        IOptions<JWTConfiguration> config)
    {
        _logger = logger;
        _baseUrl = config.Value.BaseUrl;
        _timeout = config.Value.Timeout;
        _grantType = config.Value.GrantType;
        _userName = config.Value.UserName;
        _password = config.Value.Password;
        _oAuthClientKey = config.Value.OAuthClientKey;
        _oAuthClientSecret = config.Value.OAuthClientSecret;
    }
    public JWTTokenModel getAccessToken()
    {
        var request = new RestRequest(Path, Method.POST) { Timeout = 10000 };
        request.AddHeader(RequestHeaders.CONTENT_TYPE, RequestHeaders.FORM_CONTENT);
        StringBuilder sb = new StringBuilder();
        sb.Append("grant_type=").Append(_grantType)
            .Append("&username=").Append(_userName)
            .Append("&password=").Append(_password);
        var client = new RestClient(_baseUrl);
        client.Authenticator = new HttpBasicAuthenticator(_oAuthClientKey, _oAuthClientSecret);
        request.AddParameter(RequestHeaders.FORM_CONTENT, sb.ToString(), ParameterType.RequestBody);
        client.RemoteCertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) => true;
        var response = client.Execute(request);

        if (response.IsSuccessful)
        {
            return JsonConvert.DeserializeObject<JWTTokenModel>(response.Content);
        }
        else
        {
            _logger.LogWarning("Get token fialed from wso2 is, cause by: " + response.ErrorException.StackTrace);
        }

        return null;
    }
}


// validate token
// controller
[Route("/[controller]/[action]")]
[Authorize]
[ApiController]
public class RatingServiceController : ControllerBase{}

// startup.cs register
public class Startup
{
    private const string jwksPath = "/oauth2/jwks";
    public JwtBearerConfig JwtBearerConfig { get; set; }
    public IConfiguration Configuration { get; }

    public Startup(Configuration configuration)
    {
        Configuration = configuration;

        JwtBearerConfig = new JwtBearerConfig();
        Configuration.GetSection("JwtBearerConfig").Bind(JwtBearerConfig);

        Configuration = builder.Build();
    }

    public void ConfigureServices(IServiceCollection services)
    {
        ...
        ConfigOAuth(services);
        ...
    }

    public void Configure(IApplicationBuilder app, IHostingEnvironment env)
    {
        // Notice: this should be placed before UseMvc
        app.UseAuthentication();
        app.UseMvc();
    }

    private void ConfigOAuth(IServiceCollection services)
    {
        var client = new RestClient(JwtBearerConfig.BaseUrl);
        client.RemoteCertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) => true;
        var request = new RestRequest(jwksPath, Method.GET);
        var jwtKey = client.Execute(request).Content;
        var Ids4keys = JsonConvert.DeserializeObject<Ids4Keys>(jwtKey);
        var jwk = Ids4keys.keys;

        services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
            .AddJwtBearer(options =>
            {
                options.TokenValidationParameters = new TokenValidationParameters
                {
                    ValidateIssuer = JwtBearerConfig.ValidateIssuer,
                    ValidateIssuerSigningKey = JwtBearerConfig.ValidateIssuerSigningKey,
                    IssuerSigningKeys = jwk,

                    ValidateAudience = JwtBearerConfig.ValidateAudience,
                    ValidAudience = JwtBearerConfig.ValidAudience,

                    ValidateLifetime = JwtBearerConfig.ValidateLifetime,
                    RequireExpirationTime = JwtBearerConfig.RequireExpirationTime
                };
            }
        );
    }
}

public class Ids4Keys
{
    public JsonWebKey[] keys { get; set; }
}
```

---

## reference

1. https://www.cnblogs.com/guolianyu/p/9872661.html
2. [jwt-自验证](https://blog.csdn.net/awodwde/article/details/113900779)
