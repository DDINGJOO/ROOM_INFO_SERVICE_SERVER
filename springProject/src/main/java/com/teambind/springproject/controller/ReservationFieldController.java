package com.teambind.springproject.controller;

import com.teambind.springproject.dto.request.ReservationFieldRequest;
import com.teambind.springproject.dto.response.ReservationFieldResponse;
import com.teambind.springproject.service.ReservationFieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms/{roomId}/reservation-fields")
@RequiredArgsConstructor
public class ReservationFieldController {

    private final ReservationFieldService reservationFieldService;

    @GetMapping
    public ResponseEntity<List<ReservationFieldResponse>> getReservationFields(
            @PathVariable Long roomId) {
        List<ReservationFieldResponse> fields = reservationFieldService.getReservationFields(roomId);
        return ResponseEntity.ok(fields);
    }

    @PutMapping
    public ResponseEntity<List<ReservationFieldResponse>> replaceReservationFields(
            @PathVariable Long roomId,
            @Valid @RequestBody List<ReservationFieldRequest> requests) {
        List<ReservationFieldResponse> fields = reservationFieldService.replaceReservationFields(roomId, requests);
        return ResponseEntity.ok(fields);
    }
}
