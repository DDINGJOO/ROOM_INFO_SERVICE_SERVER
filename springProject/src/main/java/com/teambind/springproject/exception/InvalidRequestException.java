package com.teambind.springproject.exception;

public class InvalidRequestException extends RuntimeException {

	public InvalidRequestException(String message) {
		super(message);
	}

	public static InvalidRequestException headerMissing(String headerName) {
		return new InvalidRequestException(String.format("필수 헤더가 누락되었습니다: %s", headerName));
	}

	public static InvalidRequestException invalidFormat(String fieldName) {
		return new InvalidRequestException(String.format("잘못된 형식입니다: %s", fieldName));
	}
}
