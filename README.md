# NTechs Asterisk Connector

## Integration with Bitrix24

### Overview

### Configuration file

#### Syntax

#### Events

${Event\[attrCheck=value\](attribute)}

#### Functions

 * $(Channel(channel, expression)) - evaluate `expression` in context of specified `channel`
 * $(Duration(event1, event2)) - calculate time is seconds between `event1` and `event2`
 * $(FileContents(filename)) - wait for file to appear in specified path in `filename`, read it and encode to base64
 * $(Responsible(phone)) - search for bitrix24 entities (companies, contacts, leads), related to specified phone and return responsible for this entity emploee identifier
 * $(REST(method, field[, attr, value]...)) - execute REST `method` and return value of `field` of result, supported:
    * user.get - https://dev.1c-bitrix.ru/rest_help/users/user_get.php

#### Operations

 * '|' - logical or, e.g. if left part of expression is null, then evaluate right, for example:

 ```
         action:
           - method: telephony.externalcall.register
             params:
                USER_ID: $(Responsible(${QueueCallerJoin(CallerIDNum)})) | 663
                PHONE_NUMBER: ${QueueCallerJoin(CallerIDNum)}
                LINE_NUMBER: ${Newchannel(Exten)}
                CRM_CREATE: 1
                TYPE: 2
                SHOW: 0
 ```