## 单点登录

1. diagram

   ![avatar](/static/image/oauth/sso.png)

2. 三个系统即使域名不一样, 想办法给三个系统同步同一个用户的票据
   - 中央认证服务器: ssoserver.com
   - 其他系统, 想要登录去 ssoserver.com 登录, 登录成功跳转回来
   - 只要有一个登录, 其他都不用登录
   - 全系统统一一个 sso-sessionid; 所有系统可能域名都不相同

## 单点登录流程

1. diagram

   ![avatar](/static/image/oauth/sso-flow.png)
