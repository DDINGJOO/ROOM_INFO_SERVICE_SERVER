package com.teambind.springproject.exception;

public class ForbiddenException extends RuntimeException {

	public ForbiddenException(String message) {
		super(message);
	}

	public static ForbiddenException placeManagerOnly() {
		return new ForbiddenException("플레이스 매니저 앱에서만 접근 가능합니다.");
	}

	public static ForbiddenException notOwner() {
		return new ForbiddenException("해당 리소스의 소유자만 접근할 수 있습니다.");
	}

	public static ForbiddenException placeNotFound() {
		return new ForbiddenException("소유권 검증 실패: 공간 정보를 조회할 수 없습니다.");
	}
}
