## oauth

### traditional C/S 
- Introduction: 
    - authenticating with the server using the resource owner's credentials 
    - the resource owner shares its credentials with the third party
- feature
    - Third-party applications are required to store the resource owner's credentials for future use, typically a password in clear-text.
    - Servers are required to support password authentication, despite the security weaknesses inherent in passwords.
    - Third-party applications gain overly broad access, and cannot limit duration or subset resources
    - Resource owners cannot revoke access individual, but can revoke access to all applications by changing password
    - Compromise of any third-party application results in compromise of the end-user's password and all of the data protected by that password.

### OAuth 
#### Introduction: 
- Introduce authorization layer and separating the client and owner
- the client requests access to resources controlled by the resource owner and hosted by the resource server
- issued a different set of credentials than those of the resource owner.
- the client obtains an access token `denoting a specific scope, lifetime, and other access attributes`
- Access tokens are issued to third-party clients by an authorization server with the approval of the resource owner.
-  The client uses the access token to access the protected resources hosted by the resource server

##### 1.1 role: four
- resource owner: An entity capable of granting access to a protected resource.
- resource server: The server hosting the protected resources, capable of accepting and responding to protected resource requests using access tokens.
- client: use access token and request protected resource requests on behalf of the resource owner and with its authorization, including server, a desktop, or other devices
- authorization server: The server issuing access tokens to the client after successfully authenticating the resource owner and obtaining authorization

##### 1.2 Protocol Flow
- flow
    ```java
    +--------+                               +---------------+
    |        |--(A)- Authorization Request ->|   Resource    |
    |        |                               |     Owner     |
    |        |<-(B)-- Authorization Grant ---|               |
    |        |                               +---------------+
    |        |
    |        |                               +---------------+
    |        |--(C)-- Authorization Grant -->| Authorization |
    | Client |                               |     Server    |
    |        |<-(D)----- Access Token -------|               |
    |        |                               +---------------+
    |        |
    |        |                               +---------------+
    |        |--(E)----- Access Token ------>|    Resource   |
    |        |                               |     Server    |
    |        |<-(F)--- Protected Resource ---|               |
    +--------+                               +---------------+
    ```
    - A: The client requests authorization from the resource owner. The authorization request can be made directly to the resource owner (as shown), or preferably indirectly via the authorization server as an intermediary.
    - B: four grant types and extension grant type. The authorization grant type depends on the method used by the        client to request authorization and the types supported by the authorization server.
    - C: the client get access token by using `Authorization Grant`
    - D: issues an access token.
    - E: client use access token to request protected resource
    - F: success request

- Authorization Grant type
    - authorization code: get from authorization server
        ```json
        client --> resource owner --> Authorization server --Authorization Code-->  resource owner --> client
        ```
    - implicit: simplified 
    - resource owner password credentials: there is a high degree of trust
    - client credentials: scope is limited to the protected resources


- Access tokens
    - This abstraction enables issuing access tokens more restrictive than the authorization grant used to obtain them

- Refresh tokens
    - issue by authorization server
    - access tokens may have a shorter lifetime and fewer permissions than authorized by the resource owner
    - Issuing a refresh token is optional at the discretion of the authorization server
    - flow
    ```java
    +--------+                                           +---------------+
    |        |--(A)------- Authorization Grant --------->|               |
    |        |                                           |               |
    |        |<-(B)----------- Access Token -------------|               |
    |        |               & Refresh Token             |               |
    |        |                                           |               |
    |        |                            +----------+   |               |
    |        |--(C)---- Access Token ---->|          |   |               |
    |        |                            |          |   |               |
    |        |<-(D)- Protected Resource --| Resource |   | Authorization |
    | Client |                            |  Server  |   |     Server    |
    |        |--(E)---- Access Token ---->|          |   |               |
    |        |                            |          |   |               |
    |        |<-(F)- Invalid Token Error -|          |   |               |
    |        |                            +----------+   |               |
    |        |                                           |               |
    |        |--(G)----------- Refresh Token ----------->|               |
    |        |                                           |               |
    |        |<-(H)----------- Access Token -------------|               |
    +--------+           & Optional Refresh Token        +---------------+
    ```
