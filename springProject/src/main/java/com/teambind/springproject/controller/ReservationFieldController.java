package com.teambind.springproject.controller;

import com.teambind.springproject.dto.request.ReservationFieldRequest;
import com.teambind.springproject.dto.response.ReservationFieldResponse;
import com.teambind.springproject.entity.enums.AppType;
import com.teambind.springproject.exception.ForbiddenException;
import com.teambind.springproject.exception.InvalidRequestException;
import com.teambind.springproject.service.ReservationFieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rooms/{roomId}/reservation-fields")
@RequiredArgsConstructor
public class ReservationFieldController {

    private static final String HEADER_APP_TYPE = "X-App-Type";
    private static final String HEADER_USER_ID = "X-User-Id";

    private final ReservationFieldService reservationFieldService;

    private AppType parseAppType(String appTypeHeader) {
        if (appTypeHeader == null || appTypeHeader.isBlank()) {
            throw InvalidRequestException.headerMissing(HEADER_APP_TYPE);
        }
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

    @GetMapping
    public ResponseEntity<List<ReservationFieldResponse>> getReservationFields(
            @PathVariable Long roomId) {
        List<ReservationFieldResponse> fields = reservationFieldService.getReservationFields(roomId);
        return ResponseEntity.ok(fields);
    }

    @PostMapping
    public ResponseEntity<ReservationFieldResponse> addReservationField(
            @RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
            @RequestHeader(value = HEADER_USER_ID, required = false) String userId,
            @PathVariable Long roomId,
            @Valid @RequestBody ReservationFieldRequest request) {
        validatePlaceManagerApp(parseAppType(appTypeHeader));
        log.info("예약 필드 추가 요청: roomId={}, userId={}", roomId, userId);

        ReservationFieldResponse field = reservationFieldService.addReservationField(roomId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(field);
    }

    @PutMapping
    public ResponseEntity<List<ReservationFieldResponse>> replaceReservationFields(
            @RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
            @RequestHeader(value = HEADER_USER_ID, required = false) String userId,
            @PathVariable Long roomId,
            @Valid @RequestBody List<ReservationFieldRequest> requests) {
        validatePlaceManagerApp(parseAppType(appTypeHeader));
        log.info("예약 필드 전체 교체 요청: roomId={}, userId={}", roomId, userId);

        List<ReservationFieldResponse> fields = reservationFieldService.replaceReservationFields(roomId, requests);
        return ResponseEntity.ok(fields);
    }

    @DeleteMapping("/{fieldId}")
    public ResponseEntity<Void> deleteReservationField(
            @RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
            @RequestHeader(value = HEADER_USER_ID, required = false) String userId,
            @PathVariable Long roomId,
            @PathVariable Long fieldId) {
        validatePlaceManagerApp(parseAppType(appTypeHeader));
        log.info("예약 필드 삭제 요청: roomId={}, fieldId={}, userId={}", roomId, fieldId, userId);

        reservationFieldService.deleteReservationField(roomId, fieldId);
        return ResponseEntity.noContent().build();
    }
}
