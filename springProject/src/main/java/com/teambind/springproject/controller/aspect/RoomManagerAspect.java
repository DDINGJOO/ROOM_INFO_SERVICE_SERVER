package com.teambind.springproject.controller.aspect;

import com.teambind.springproject.client.PlaceInfoClient;
import com.teambind.springproject.controller.annotation.RequireRoomManager;
import com.teambind.springproject.controller.annotation.ValidateRoomOwnership;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.enums.AppType;
import com.teambind.springproject.exception.ForbiddenException;
import com.teambind.springproject.exception.InvalidRequestException;
import com.teambind.springproject.exception.RoomNotFoundException;
import com.teambind.springproject.repository.RoomInfoRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(1)
public class RoomManagerAspect {

	private static final String HEADER_APP_TYPE = "X-App-Type";
	private static final String HEADER_USER_ID = "X-User-Id";

	private final RoomInfoRepository roomInfoRepository;
	private final PlaceInfoClient placeInfoClient;

	@Before("@annotation(requireRoomManager)")
	public void validateRoomManagerHeaders(JoinPoint joinPoint, RequireRoomManager requireRoomManager) {
		HttpServletRequest request = getCurrentRequest();

		String appTypeHeader = request.getHeader(HEADER_APP_TYPE);
		String userId = request.getHeader(HEADER_USER_ID);

		validateRequiredHeader(userId, HEADER_USER_ID);
		validatePlaceManagerApp(parseAppType(appTypeHeader));

		log.debug("RoomManager 헤더 검증 통과: userId={}", userId);
	}

	@Before("@annotation(validateRoomOwnership)")
	public void validateResourceOwnership(JoinPoint joinPoint, ValidateRoomOwnership validateRoomOwnership) {
		HttpServletRequest request = getCurrentRequest();
		String userId = request.getHeader(HEADER_USER_ID);

		if (userId == null || userId.isBlank()) {
			throw InvalidRequestException.headerMissing(HEADER_USER_ID);
		}

		Long placeId = extractPlaceId(joinPoint, validateRoomOwnership);

		if (placeId != null) {
			String ownerId = placeInfoClient.getPlaceOwnerId(placeId)
					.orElseThrow(ForbiddenException::placeNotFound);

			if (!ownerId.equals(userId)) {
				log.warn("소유권 검증 실패: placeId={}, ownerId={}, requestUserId={}", placeId, ownerId, userId);
				throw ForbiddenException.notOwner();
			}

			log.debug("소유권 검증 통과: placeId={}, userId={}", placeId, userId);
		}
	}

	private Long extractPlaceId(JoinPoint joinPoint, ValidateRoomOwnership annotation) {
		String source = annotation.placeIdSource();

		if ("body".equals(source)) {
			return extractPlaceIdFromBody(joinPoint);
		} else {
			return extractPlaceIdFromRoom(joinPoint, annotation.roomIdParam());
		}
	}

	private Long extractPlaceIdFromBody(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		for (Object arg : args) {
			if (arg == null) continue;

			try {
				Method getPlaceId = arg.getClass().getMethod("getPlaceId");
				Object result = getPlaceId.invoke(arg);
				if (result instanceof Long) {
					return (Long) result;
				}
			} catch (NoSuchMethodException e) {
				// getPlaceId 메서드가 없으면 다음 인자로
			} catch (Exception e) {
				log.warn("Failed to extract placeId from request body: {}", e.getMessage());
			}
		}

		return null;
	}

	private Long extractPlaceIdFromRoom(JoinPoint joinPoint, String roomIdParam) {
		Long roomId = extractRoomId(joinPoint, roomIdParam);

		if (roomId == null) {
			return null;
		}

		RoomInfo roomInfo = roomInfoRepository.findById(roomId)
				.orElseThrow(RoomNotFoundException::new);

		return roomInfo.getPlaceId();
	}

	private Long extractRoomId(JoinPoint joinPoint, String paramName) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Parameter[] parameters = signature.getMethod().getParameters();
		Object[] args = joinPoint.getArgs();

		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].getName().equals(paramName)) {
				Object value = args[i];
				if (value instanceof Long) {
					return (Long) value;
				}
				if (value != null) {
					try {
						return Long.parseLong(value.toString());
					} catch (NumberFormatException e) {
						log.warn("Failed to parse roomId: {}", value);
					}
				}
			}
		}
		return null;
	}

	private HttpServletRequest getCurrentRequest() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attrs == null) {
			throw new IllegalStateException("요청 컨텍스트를 찾을 수 없습니다.");
		}
		return attrs.getRequest();
	}

	private void validateRequiredHeader(String headerValue, String headerName) {
		if (headerValue == null || headerValue.isBlank()) {
			throw InvalidRequestException.headerMissing(headerName);
		}
	}

	private AppType parseAppType(String appTypeHeader) {
		validateRequiredHeader(appTypeHeader, HEADER_APP_TYPE);
		try {
			return AppType.valueOf(appTypeHeader);
		} catch (IllegalArgumentException e) {
			throw InvalidRequestException.invalidFormat(HEADER_APP_TYPE);
		}
	}

	private void validatePlaceManagerApp(AppType appType) {
		if (appType != AppType.PLACE_MANAGER) {
			throw ForbiddenException.placeManagerOnly();
		}
	}
}
