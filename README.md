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
    * [user.get](https://dev.1c-bitrix.ru/rest_help/users/user_get.php) - get filtered list of users

#### REST API Methods

 * [crm.lead.add](https://dev.1c-bitrix.ru/rest_help/crm/leads/crm_lead_add.php) - Add lead
 * [crm.lead.update](https://dev.1c-bitrix.ru/rest_help/crm/leads/crm_lead_update.php) - Update (modify) lead
 * [telephony.externalcall.attachrecord](https://dev.1c-bitrix.ru/rest_help/scope_telephony/telephony/telephony_externalCall_attachRecord.php) - Attach call record
 * [telephony.externalcall.finish](https://dev.1c-bitrix.ru/rest_help/scope_telephony/telephony/telephony_externalcall_finish.php) - Finish registered call
 * [telephony.externalcall.hide](https://dev.1c-bitrix.ru/rest_help/scope_telephony/telephony/telephony_externalcall_hide.php) - Hide call card
 * [telephony.externalcall.register](https://dev.1c-bitrix.ru/rest_help/scope_telephony/telephony/telephony_externalcall_register.php) - Register call
 * [telephony.externalcall.show](https://dev.1c-bitrix.ru/rest_help/scope_telephony/telephony/telephony_externalcall_show.php) - Show call card

#### AMI Actions

 * [asterisk.ami.dbdel](https://wiki.asterisk.org/wiki/display/AST/Asterisk+17+ManagerAction_DBDel) - Delete DB entry
 * [asterisk.ami.dbdeltree](https://wiki.asterisk.org/wiki/display/AST/Asterisk+17+ManagerAction_DBDelTree) - Delete DB Tree
 * [asterisk.ami.dbput](https://wiki.asterisk.org/wiki/display/AST/Asterisk+17+ManagerAction_DBPut) - Put DB entry
 * [asterisk.ami.setvar](https://wiki.asterisk.org/wiki/display/AST/Asterisk+17+ManagerAction_Setvar) - Sets a channel variable or function value

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