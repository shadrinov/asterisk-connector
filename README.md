# NTechs Asterisk Connector

## Integration with Bitrix24

Running connector:

`java -jar asterisk-connector-0.1.0.jar`

Example output:

```

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.4.RELEASE)

2020-05-26 01:17:25.624  INFO 873861 --- [           main] ru.ntechs.asteriskconnector.Application  : Starting Application v0.1.0 on asterisk.termomir with PID 873861 (/root/asterisk-connector/0.1.0/asterisk-connector-0.1.0.jar started by root in /root/asterisk-connector/0.1.0)
2020-05-26 01:17:25.637  INFO 873861 --- [           main] ru.ntechs.asteriskconnector.Application  : No active profile set, falling back to default profiles: default
2020-05-26 01:17:27.610  INFO 873861 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2020-05-26 01:17:27.631  INFO 873861 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2020-05-26 01:17:27.631  INFO 873861 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.30]
2020-05-26 01:17:27.756  INFO 873861 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2020-05-26 01:17:27.757  INFO 873861 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 2039 ms
2020-05-26 01:17:28.143  INFO 873861 --- [            ami] ru.ntechs.ami.AMI                        : connecting to ami://localhost:5038...
2020-05-26 01:17:28.177  INFO 873861 --- [            ami] ru.ntechs.ami.AMI                        : connected to ami://localhost:5038, protocol version: 5.0.1 (5.0.1)
2020-05-26 01:17:28.195  INFO 873861 --- [pool-1-thread-1] ru.ntechs.ami.AMI                        : login successful: Authentication accepted
2020-05-26 01:17:28.556  INFO 873861 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2020-05-26 01:17:29.139  INFO 873861 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2020-05-26 01:17:29.159  INFO 873861 --- [           main] ru.ntechs.asteriskconnector.Application  : Started Application in 4.39 seconds (JVM running for 4.883)
2020-05-26 01:17:35.146  INFO 873861 --- [pool-1-thread-3] r.n.a.eventchain.EventChain              : Progress: [1, 0, 0, 0, 0]:0, Result: PROGRESS! Got Newchannel on PJSIP/pstn-livecomm-00000109, waiting for QueueCallerJoin
2020-05-26 01:17:35.147  INFO 873861 --- [pool-1-thread-3] r.n.a.eventchain.EventChain              : Progress: [1, 0, 0, 1, 0]:3, Result: PROGRESS! Got Newchannel on PJSIP/pstn-livecomm-00000109, waiting for QueueCallerJoin
2020-05-26 01:17:35.147  INFO 873861 --- [pool-1-thread-3] r.n.a.eventchain.EventChain              : Progress: [1, 0, 0, 1, 1]:4, Result: PROGRESS! Got Newchannel on PJSIP/pstn-livecomm-00000109, waiting for QueueCallerJoin
2020-05-26 01:17:41.300  INFO 873861 --- [pool-1-thread-2] r.n.a.eventchain.EventChain              : Progress: [2, 0, 0, 1, 1]:0, Result: MATCH! Got QueueCallerJoin on PJSIP/pstn-livecomm-00000109, executing action: [ConnectorAction(method=telephony.externalcall.register, data={USER_PHONE_INNER=10, PHONE_NUMBER=${QueueCallerJoin(CallerIDNum)}, TYPE=2, SHOW=0})]
2020-05-26 01:17:41.301  INFO 873861 --- [pool-1-thread-2] r.n.a.eventchain.EventChain              : Progress: [0, 0, 0, 2, 1]:3, Result: PROGRESS! Got QueueCallerJoin on PJSIP/pstn-livecomm-00000109, waiting for AgentCalled
2020-05-26 01:17:41.304  INFO 873861 --- [pool-1-thread-1] r.n.a.eventchain.EventChain              : Progress: [1, 0, 0, 0, 0]:0, Result: PROGRESS! Got Newchannel on PJSIP/hp-kassa-0000010a, waiting for QueueCallerJoin
2020-05-26 01:17:41.305  INFO 873861 --- [pool-1-thread-1] r.n.a.eventchain.EventChain              : Progress: [1, 0, 0, 1, 0]:3, Result: PROGRESS! Got Newchannel on PJSIP/hp-kassa-0000010a, waiting for QueueCallerJoin
2020-05-26 01:17:41.305  INFO 873861 --- [pool-1-thread-1] r.n.a.eventchain.EventChain              : Progress: [1, 0, 0, 1, 1]:4, Result: PROGRESS! Got Newchannel on PJSIP/hp-kassa-0000010a, waiting for QueueCallerJoin
2020-05-26 01:17:41.302  INFO 873861 --- [pool-1-thread-2] r.n.a.eventchain.EventChain              : Progress: [0, 0, 0, 2, 2]:4, Result: PROGRESS! Got QueueCallerJoin on PJSIP/pstn-livecomm-00000109, waiting for AgentCalled
2020-05-26 01:17:41.323  INFO 873861 --- [pool-1-thread-2] r.n.a.s.MethodRegisterExternalCall       : source: {USER_PHONE_INNER=10, PHONE_NUMBER=${QueueCallerJoin(CallerIDNum)}, TYPE=2, SHOW=0}
2020-05-26 01:17:41.323  INFO 873861 --- [pool-1-thread-2] r.n.a.s.MethodRegisterExternalCall       : evaluated: {USER_PHONE_INNER=10, SHOW=0, PHONE_NUMBER=679606, TYPE=2}
2020-05-26 01:17:41.938  INFO 873861 --- [pool-1-thread-4] r.n.a.eventchain.EventChain              : Progress: [0, 0, 1, 2, 2]:2, Result: MATCH! Got AgentCalled on PJSIP/pstn-livecomm-00000109, executing action: [ConnectorAction(method=telephony.externalcall.show, data={USER_ID=$(REST("user.get", "USER_ID", "UF_PHONE_INNER", ${AgentCalled(DestCallerIDNum)}))})]
2020-05-26 01:17:41.938  INFO 873861 --- [pool-1-thread-4] r.n.a.eventchain.EventChain              : Progress: [0, 0, 0, 3, 2]:3, Result: PROGRESS! Got AgentCalled on PJSIP/pstn-livecomm-00000109, waiting for AgentConnect
2020-05-26 01:17:41.939  INFO 873861 --- [pool-1-thread-4] r.n.a.eventchain.EventChain              : Progress: [0, 0, 0, 3, 3]:4, Result: PROGRESS! Got AgentCalled on PJSIP/pstn-livecomm-00000109, waiting for Hangup
2020-05-26 01:17:42.082  INFO 873861 --- [pool-1-thread-4] r.n.a.scripting.MethodShowExternalCall   : source: {USER_ID=$(REST("user.get", "USER_ID", "UF_PHONE_INNER", ${AgentCalled(DestCallerIDNum)}))}
2020-05-26 01:17:42.083  INFO 873861 --- [pool-1-thread-4] r.n.a.scripting.MethodShowExternalCall   : evaluated: {USER_ID=12}
2020-05-26 01:17:45.912  INFO 873861 --- [pool-1-thread-1] r.n.a.eventchain.EventChain              : Progress: [0, 0, 0, 3, 4]:4, Result: MATCH! Got Hangup on PJSIP/pstn-livecomm-00000109, executing action: [ConnectorAction(method=telephony.externalcall.finish, data={USER_ID=$(REST("user.get", "USER_ID", "UF_PHONE_INNER", ${AgentCalled(DestCallerIDNum)})), STATUS_CODE=304})]
2020-05-26 01:17:46.042  INFO 873861 --- [pool-1-thread-1] r.n.a.s.MethodFinishExternalCall         : source: {USER_ID=$(REST("user.get", "USER_ID", "UF_PHONE_INNER", ${AgentCalled(DestCallerIDNum)})), STATUS_CODE=304}
2020-05-26 01:17:46.042  INFO 873861 --- [pool-1-thread-1] r.n.a.s.MethodFinishExternalCall         : evaluated: {USER_ID=12, STATUS_CODE=304}
2020-05-26 01:17:46.229  INFO 873861 --- [pool-1-thread-1] r.n.a.bitrix.BitrixDateDeserializer      : !!!! date is {}

```