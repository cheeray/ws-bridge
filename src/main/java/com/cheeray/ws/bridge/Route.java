package com.cheeray.ws.bridge;


public class Route {
	public static final String ACTION = "action";
	private String nameSpace;
	private String action;
	private String soapUrl;
	private String restUrl;
	public String getNameSpace() {
		return nameSpace;
	}
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getRestUrl() {
		return restUrl;
	}
	public void setRestUrl(String restUrl) {
		this.restUrl = restUrl;
	}
	public String getSoapUrl() {
		return soapUrl;
	}
	public void setSoapUrl(String soapUrl) {
		this.soapUrl = soapUrl;
	}
}
