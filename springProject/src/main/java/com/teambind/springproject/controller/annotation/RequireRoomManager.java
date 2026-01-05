package com.teambind.springproject.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * PLACE_MANAGER 앱 타입과 X-User-Id 헤더를 필수로 요구하는 API에 적용
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRoomManager {
}
