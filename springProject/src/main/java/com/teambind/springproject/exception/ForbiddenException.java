package com.teambind.springproject.exception;

public class ForbiddenException extends RuntimeException {

	public ForbiddenException(String message) {
		super(message);
	}

	public static ForbiddenException placeManagerOnly() {
		return new ForbiddenException("플레이스 매니저 앱에서만 접근 가능합니다.");
	}
}
