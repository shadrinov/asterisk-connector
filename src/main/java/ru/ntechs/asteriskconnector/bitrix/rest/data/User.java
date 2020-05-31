package ru.ntechs.asteriskconnector.bitrix.rest.data;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
	@JsonProperty("ID")
	private Long id;

	@JsonProperty("ACTIVE")
	private String active;

	@JsonProperty("EMAIL")
	private String email;

	@JsonProperty("NAME")
	private String name;

	@JsonProperty("LAST_NAME")
	private String lastName;

	@JsonProperty("SECOND_NAME")
	private String secondName;

	@JsonProperty("PERSONAL_GENDER")
	private String personalGender;

	@JsonProperty("PERSONAL_PROFESSION")
	private String personalProfession;

	@JsonProperty("PERSONAL_WWW")
	private String personalWww;

	@JsonProperty("PERSONAL_BIRTHDAY")
	private String personalBirthday;

//	@JsonProperty("PERSONAL_PHOTO")
//	private Integer personalPhoto;

	@JsonProperty("PERSONAL_ICQ")
	private String personalIcq;

	@JsonProperty("PERSONAL_PHONE")
	private String personalPhone;

	@JsonProperty("PERSONAL_FAX")
	private String personalFax;

	@JsonProperty("PERSONAL_MOBILE")
	private String personalMobile;

	@JsonProperty("PERSONAL_PAGER")
	private String personalPager;

	@JsonProperty("PERSONAL_STREET")
	private String personalStreet;

	@JsonProperty("PERSONAL_CITY")
	private String personalCity;

	@JsonProperty("PERSONAL_STATE")
	private String personalState;

	@JsonProperty("PERSONAL_ZIP")
	private String personalZip;

	@JsonProperty("PERSONAL_COUNTRY")
	private String personalCountry;

	@JsonProperty("WORK_COMPANY")
	private String workCompany;

	@JsonProperty("WORK_POSITION")
	private String workPosition;

	@JsonProperty("WORK_PHONE")
	private String workPhone;

	@JsonProperty("UF_DEPARTMENT")
	private ArrayList<Integer> ufDepartment;

	@JsonProperty("UF_INTERESTS")
	private String ufInterests;

	@JsonProperty("UF_SKILLS")
	private String ufSkills;

	@JsonProperty("UF_WEB_SITES")
	private String ufWebSites;

	@JsonProperty("UF_XING")
	private String ufXing;

	@JsonProperty("UF_LINKEDIN")
	private String ufLinkedIn;

	@JsonProperty("UF_FACEBOOK")
	private String ufFacebook;

	@JsonProperty("UF_TWITTER")
	private String ufTwitter;

	@JsonProperty("UF_SKYPE")
	private String ufSkype;

	@JsonProperty("UF_DISTRICT")
	private String ufDistrict;

	@JsonProperty("UF_PHONE_INNER")
	private String ufPhoneInner;

	@JsonProperty("USER_TYPE")
	private String userType;
}
