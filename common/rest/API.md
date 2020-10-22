## API definition

- /api/[controller]/action: Big hump
- /api/[controller]/resource/{id}:

## comparison with grpc

| grpc                          | rest                   |
| ----------------------------- | ---------------------- |
| proto buffer: smaller, faster | json: slower, lager    |
| HTTP/2: lower latency         | HTTP/1: high latency   |
| bi/directional & async        | C/S only               |
| stream support                | Req/Res mechanism only |
| API oriented, no constraints  | CRUD oriented          |
| rpc based                     | http based             |
