package com.teambind.springproject.entity;

import com.teambind.springproject.entity.enums.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomInfoMaxOccupancyTest {

    @Test
    @DisplayName("Given valid maxOccupancy When creating RoomInfo Then should set maxOccupancy correctly")
    void shouldSetMaxOccupancyCorrectly() {
        // Given
        Integer maxOccupancy = 10;

        // When
        RoomInfo roomInfo = RoomInfo.builder()
                .roomId(1L)
                .roomName("Conference Room A")
                .placeId(100L)
                .status(Status.PENDING)
                .maxOccupancy(maxOccupancy)
                .build();

        // Then
        assertThat(roomInfo.getMaxOccupancy()).isEqualTo(maxOccupancy);
    }

    @Test
    @DisplayName("Given null maxOccupancy When creating RoomInfo Then should allow null value")
    void shouldAllowNullMaxOccupancy() {
        // Given & When
        RoomInfo roomInfo = RoomInfo.builder()
                .roomId(1L)
                .roomName("Conference Room B")
                .placeId(100L)
                .status(Status.PENDING)
                .maxOccupancy(null)
                .build();

        // Then
        assertThat(roomInfo.getMaxOccupancy()).isNull();
    }

    @Test
    @DisplayName("Given invalid maxOccupancy When validating Then should throw exception")
    void shouldThrowExceptionForInvalidMaxOccupancy() {
        // Given
        RoomInfo roomInfo = RoomInfo.builder()
                .roomId(1L)
                .roomName("Conference Room C")
                .placeId(100L)
                .status(Status.PENDING)
                .maxOccupancy(0)
                .build();

        // When & Then
        assertThatThrownBy(() -> roomInfo.validateMaxOccupancy())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 수용 인원은 1명 이상이어야 합니다.");
    }

    @Test
    @DisplayName("Given negative maxOccupancy When validating Then should throw exception")
    void shouldThrowExceptionForNegativeMaxOccupancy() {
        // Given
        RoomInfo roomInfo = RoomInfo.builder()
                .roomId(1L)
                .roomName("Conference Room D")
                .placeId(100L)
                .status(Status.PENDING)
                .maxOccupancy(-5)
                .build();

        // When & Then
        assertThatThrownBy(() -> roomInfo.validateMaxOccupancy())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 수용 인원은 1명 이상이어야 합니다.");
    }

    @Test
    @DisplayName("Given new maxOccupancy When updating Then should update and validate")
    void shouldUpdateAndValidateMaxOccupancy() {
        // Given
        RoomInfo roomInfo = RoomInfo.builder()
                .roomId(1L)
                .roomName("Conference Room E")
                .placeId(100L)
                .status(Status.PENDING)
                .maxOccupancy(5)
                .build();

        // When
        roomInfo.updateMaxOccupancy(15);

        // Then
        assertThat(roomInfo.getMaxOccupancy()).isEqualTo(15);
    }

    @Test
    @DisplayName("Given invalid new maxOccupancy When updating Then should throw exception")
    void shouldThrowExceptionWhenUpdatingWithInvalidMaxOccupancy() {
        // Given
        RoomInfo roomInfo = RoomInfo.builder()
                .roomId(1L)
                .roomName("Conference Room F")
                .placeId(100L)
                .status(Status.PENDING)
                .maxOccupancy(5)
                .build();

        // When & Then
        assertThatThrownBy(() -> roomInfo.updateMaxOccupancy(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 수용 인원은 1명 이상이어야 합니다.");
    }
}