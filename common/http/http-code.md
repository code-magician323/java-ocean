## HTTP

- HTTP protocol is a stateless protocol, that is, all states are saved on the server side.

## HTTP Code

- 200: success
- 202: accept
- 404: not find
- 405: request method not support

## POST/GET

- [GET 把参数包含在 URL 中，POST 通过 request body 传递参数](https://www.cnblogs.com/logsharing/p/8448446.html)\*\*

  ```xml
  GET和POST本质上就是TCP链接，并无差别。但是由于HTTP的规定和浏览器/服务器的限制，导致他们在应用过程中体现出一些不同。<br/>
  GET在浏览器回退时是无害的，而POST会再次提交请求。<br/>
  GET产生的URL地址可以被Bookmark，而POST不可以。<br/>
  GET请求会被浏览器主动cache，而POST不会，除非手动设置。<br/>
  GET请求只能进行url编码，而POST支持多种编码方式。<br/>
  GET请求参数会被完整保留在浏览器历史记录里，而POST中的参数不会被保留。<br/>
  GET请求在URL中传送的参数是有长度限制的，而POST么有。<br/>
  对参数的数据类型，GET只接受ASCII字符，而POST没有限制。<br/>
  GET比POST更不安全，因为参数直接暴露在URL上，所以不能用来传递敏感信息。<br/>
  GET参数通过URL传递，POST放在Request body中。<br/>
  ```
