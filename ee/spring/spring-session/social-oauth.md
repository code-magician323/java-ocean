## social oauth

1. diagram

   ![avatar](/static/image/oauth/social-oauth-flow.png)

2. step

   - 开放平台创建应用: 可以得到 `App Key` 和 ~~`App Secret`~~
   - 使用第一步拼接开放平台的授权页: `https://api.weibo.com/oauth2/authorize?client_id=YOUR_CLIENT_ID&response_type=code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI`
   - 点击社交登录[上一步连接]
   - 会跳转相应的社交登录页面进行登录
   - 用户登录授权后会跳转 `YOUR_REGISTERED_REDIRECT_URI/?code=CODE`
   - 使用上一步的 CODE 可以拿到开放平台的 `access_token`
   - 使用 token 可以获取到该开放平台所有开放的受保护的信息

3. weibo social oauth flow

   ![avatar](/static/image/oauth/weibo-social-oauth-flow.png)
