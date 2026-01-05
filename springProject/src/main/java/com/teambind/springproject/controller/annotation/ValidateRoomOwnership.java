package com.teambind.springproject.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Room의 Place 소유권 검증이 필요한 API에 적용
 * Place Info 서비스를 호출하여 소유자 확인
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateRoomOwnership {

	/**
	 * roomId 파라미터명 (기본값: "roomId")
	 */
	String roomIdParam() default "roomId";

	/**
	 * placeId 소스 위치
	 * - "path": URL path에서 roomId로 Room 조회 후 placeId 추출
	 * - "body": RequestBody에서 직접 placeId 추출 (생성 시)
	 */
	String placeIdSource() default "path";
}
