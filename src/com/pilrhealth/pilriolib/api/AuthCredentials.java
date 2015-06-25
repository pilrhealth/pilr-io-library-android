package com.pilrhealth.pilriolib.api;

public class AuthCredentials {
	private static String sUrl, sParticipantId, sUri, sAccessCode;
	private static String sWhereClauseLogin = "/api/v1/token?activation_key=";
	private static String sApiVersion = "v1";

	public static String getUrl() {
		return sUrl;
	}

	public static void setUrl(String url) {
		sUrl = url;
	}

	public static String getParticipantId() {
		return sParticipantId;
	}

	public static void setParticipantId(String participantId) {
		sParticipantId = participantId;
	}

	public static String getAccessCode() {
		return sAccessCode;
	}

	public static void setAccessCode(String accessCode) {
		sAccessCode = accessCode;
	}

	public static String getUri() {
		return sUri;
	}

	public static void setUri(String uri) {
		sUri = uri;
	}

	public static String getWhereClauseLogin() {
		return sWhereClauseLogin;
	}

	public static String getApiVersion() {
		return sApiVersion;
	}
}
