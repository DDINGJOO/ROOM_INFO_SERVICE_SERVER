package com.teambind.springproject.service;

import com.teambind.springproject.dto.request.ReservationFieldRequest;
import com.teambind.springproject.dto.response.ReservationFieldResponse;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.attribute.ReservationField;
import com.teambind.springproject.repository.ReservationFieldRepository;
import com.teambind.springproject.repository.RoomCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationFieldService {

    private final ReservationFieldRepository reservationFieldRepository;
    private final RoomCommandRepository roomCommandRepository;

    @Transactional(readOnly = true)
    public List<ReservationFieldResponse> getReservationFields(Long roomId) {
        validateRoomExists(roomId);
        List<ReservationField> fields = reservationFieldRepository.findByRoomInfoRoomIdOrderBySequenceAsc(roomId);
        return fields.stream()
                .map(ReservationFieldResponse::from)
                .toList();
    }

    @Transactional
    public List<ReservationFieldResponse> replaceReservationFields(Long roomId, List<ReservationFieldRequest> requests) {
        RoomInfo roomInfo = roomCommandRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        roomInfo.clearReservationFields();

        if (requests != null && !requests.isEmpty()) {
            for (ReservationFieldRequest request : requests) {
                roomInfo.addReservationField(
                        request.getTitle(),
                        request.getInputType(),
                        request.getRequired(),
                        request.getMaxLength(),
                        request.getSequence()
                );
            }
        }

        roomCommandRepository.save(roomInfo);

        return roomInfo.getReservationFields().stream()
                .map(ReservationFieldResponse::from)
                .toList();
    }

    private void validateRoomExists(Long roomId) {
        if (!roomCommandRepository.existsById(roomId)) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }
    }
}
