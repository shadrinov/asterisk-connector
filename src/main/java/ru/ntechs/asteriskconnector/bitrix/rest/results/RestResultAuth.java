package ru.ntechs.asteriskconnector.bitrix.rest.results;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultAuth extends RestResult {

//	"access_token":"4bed5f5e0043d4ca0042213e0000000700000365b0deb4214ea972065c14b3c3ef2c1d",
	@JsonProperty("access_token")
	private String accessToken;

//	"expires":1583344971,
	private Integer expires;

//	"expires_in":3600,
	@JsonProperty("expires_in")
	private Integer expiresIn;

//	"scope":"app",
	private String scope;

//	"domain":"oauth.bitrix.info",
	private String domain;

//	"server_endpoint":"https:\/\/oauth.bitrix.info\/rest\/",
	@JsonProperty("server_endpoint")
	private String serverEndpoint;

//	"status":"L",
	private String status;

//	"client_endpoint":"https:\/\/ntechs.bitrix24.ru\/rest\/",
	@JsonProperty("client_endpoint")
	private String clientEndpoint;

//	"member_id":"78727e6e7334a5b7f57357a6a7e63480",
	@JsonProperty("member_id")
	private String memberId;

//	"user_id":7,
	@JsonProperty("user_id")
	private Integer userId;

//	"refresh_token":"3b6c875e0043d4ca0042213e0000000700000398370db87fe5ee24d349154a5235b3ad"
	@JsonProperty("refresh_token")
	private String refreshToken;
}
