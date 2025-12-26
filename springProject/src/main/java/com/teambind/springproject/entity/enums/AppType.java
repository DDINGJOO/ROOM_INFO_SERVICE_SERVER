package com.teambind.springproject.entity.enums;

public enum AppType {
	GENERAL("일반 앱"),
	PLACE_MANAGER("공간관리자 앱");

	private final String description;

	AppType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
