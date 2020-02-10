# asterisk-connector


## Logging example: 

`curl -v -d 'ashdasdh' http://localhost:8080/rest/say`

Example output:

```
2020-02-08 10:45:23.153  INFO 4127 --- [nio-8080-exec-1] r.n.a.RequestAndResponseLoggingFilter    : 0:0:0:0:0:0:0:1|> POST /rest/say
2020-02-08 10:45:23.156  INFO 4127 --- [nio-8080-exec-1] r.n.a.RequestAndResponseLoggingFilter    : 0:0:0:0:0:0:0:1|> host: localhost:8080
2020-02-08 10:45:23.156  INFO 4127 --- [nio-8080-exec-1] r.n.a.RequestAndResponseLoggingFilter    : 0:0:0:0:0:0:0:1|> user-agent: curl/7.63.0
2020-02-08 10:45:23.156  INFO 4127 --- [nio-8080-exec-1] r.n.a.RequestAndResponseLoggingFilter    : 0:0:0:0:0:0:0:1|> accept: */*
2020-02-08 10:45:23.156  INFO 4127 --- [nio-8080-exec-1] r.n.a.RequestAndResponseLoggingFilter    : 0:0:0:0:0:0:0:1|> content-length: 8
2020-02-08 10:45:23.156  INFO 4127 --- [nio-8080-exec-1] r.n.a.RequestAndResponseLoggingFilter    : 0:0:0:0:0:0:0:1|> content-type: application/x-www-form-urlencoded
2020-02-08 10:45:23.156  INFO 4127 --- [nio-8080-exec-1] r.n.a.RequestAndResponseLoggingFilter    : 0:0:0:0:0:0:0:1|>
2020-02-08 10:45:23.287  INFO 4127 --- [nio-8080-exec-1] r.n.a.RequestAndResponseLoggingFilter    : 0:0:0:0:0:0:0:1|> ashdasdh=
2020-02-08 10:45:23.288  INFO 4127 --- [nio-8080-exec-1] r.n.a.RequestAndResponseLoggingFilter    : 0:0:0:0:0:0:0:1|< 200 OK
2020-02-08 10:45:23.289  INFO 4127 --- [nio-8080-exec-1] r.n.a.RequestAndResponseLoggingFilter    : 0:0:0:0:0:0:0:1|<
2020-02-08 10:45:23.289  INFO 4127 --- [nio-8080-exec-1] r.n.a.RequestAndResponseLoggingFilter    : 0:0:0:0:0:0:0:1|< Say
```