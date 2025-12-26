package com.teambind.springproject.entity;

import com.teambind.springproject.entity.attribute.ReservationField;
import com.teambind.springproject.entity.enums.FieldType;
import com.teambind.springproject.entity.enums.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationFieldTest {

    @Nested
    @DisplayName("ReservationField 생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("Given valid parameters When creating ReservationField Then should create correctly")
        void shouldCreateReservationFieldCorrectly() {
            // Given
            String title = "요청사항";
            FieldType inputType = FieldType.TEXT;
            Boolean required = true;
            Integer maxLength = 200;
            Integer sequence = 1;

            // When
            ReservationField field = ReservationField.create(title, inputType, required, maxLength, sequence);

            // Then
            assertThat(field.getTitle()).isEqualTo(title);
            assertThat(field.getInputType()).isEqualTo(inputType);
            assertThat(field.getRequired()).isTrue();
            assertThat(field.getMaxLength()).isEqualTo(maxLength);
            assertThat(field.getSequence()).isEqualTo(sequence);
        }

        @Test
        @DisplayName("Given null inputType When creating Then should default to TEXT")
        void shouldDefaultToTextType() {
            // Given & When
            ReservationField field = ReservationField.create("테스트", null, false, 100, 1);

            // Then
            assertThat(field.getInputType()).isEqualTo(FieldType.TEXT);
        }

        @Test
        @DisplayName("Given null required When creating Then should default to false")
        void shouldDefaultToNotRequired() {
            // Given & When
            ReservationField field = ReservationField.create("테스트", FieldType.TEXT, null, 100, 1);

            // Then
            assertThat(field.getRequired()).isFalse();
        }

        @Test
        @DisplayName("Given null maxLength When creating Then should use default value")
        void shouldUseDefaultMaxLength() {
            // Given & When
            ReservationField field = ReservationField.create("테스트", FieldType.TEXT, false, null, 1);

            // Then
            assertThat(field.getMaxLength()).isEqualTo(ReservationField.DEFAULT_MAX_LENGTH);
        }

        @Test
        @DisplayName("Given null title When creating Then should throw exception")
        void shouldThrowExceptionForNullTitle() {
            // Given & When & Then
            assertThatThrownBy(() -> ReservationField.create(null, FieldType.TEXT, false, 100, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("필드 타이틀은 필수입니다.");
        }

        @Test
        @DisplayName("Given empty title When creating Then should throw exception")
        void shouldThrowExceptionForEmptyTitle() {
            // Given & When & Then
            assertThatThrownBy(() -> ReservationField.create("   ", FieldType.TEXT, false, 100, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("필드 타이틀은 필수입니다.");
        }

        @Test
        @DisplayName("Given too long title When creating Then should throw exception")
        void shouldThrowExceptionForTooLongTitle() {
            // Given
            String longTitle = "a".repeat(101);

            // When & Then
            assertThatThrownBy(() -> ReservationField.create(longTitle, FieldType.TEXT, false, 100, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최대 100자");
        }

        @Test
        @DisplayName("Given null sequence When creating Then should throw exception")
        void shouldThrowExceptionForNullSequence() {
            // Given & When & Then
            assertThatThrownBy(() -> ReservationField.create("테스트", FieldType.TEXT, false, 100, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("순서는 1 이상이어야 합니다.");
        }

        @Test
        @DisplayName("Given zero sequence When creating Then should throw exception")
        void shouldThrowExceptionForZeroSequence() {
            // Given & When & Then
            assertThatThrownBy(() -> ReservationField.create("테스트", FieldType.TEXT, false, 100, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("순서는 1 이상이어야 합니다.");
        }
    }

    @Nested
    @DisplayName("RoomInfo 연관관계 테스트")
    class RoomInfoAssociationTest {

        @Test
        @DisplayName("Given ReservationField When assigning to RoomInfo Then should set roomInfo")
        void shouldAssignRoomInfo() {
            // Given
            RoomInfo roomInfo = RoomInfo.builder()
                    .roomId(1L)
                    .roomName("테스트룸")
                    .placeId(100L)
                    .status(Status.OPEN)
                    .build();

            ReservationField field = ReservationField.create("요청사항", FieldType.TEXT, false, 100, 1);

            // When
            field.assignRoom(roomInfo);

            // Then
            assertThat(field.getRoomInfo()).isEqualTo(roomInfo);
        }

        @Test
        @DisplayName("Given assigned ReservationField When removing room Then should clear roomInfo")
        void shouldRemoveRoomInfo() {
            // Given
            RoomInfo roomInfo = RoomInfo.builder()
                    .roomId(1L)
                    .roomName("테스트룸")
                    .placeId(100L)
                    .status(Status.OPEN)
                    .build();

            ReservationField field = ReservationField.create("요청사항", FieldType.TEXT, false, 100, 1);
            field.assignRoom(roomInfo);

            // When
            field.removeRoom();

            // Then
            assertThat(field.getRoomInfo()).isNull();
        }
    }

    @Nested
    @DisplayName("RoomInfo에서 ReservationField 관리 테스트")
    class RoomInfoManageFieldTest {

        @Test
        @DisplayName("Given RoomInfo When adding ReservationField Then should add to list")
        void shouldAddReservationField() {
            // Given
            RoomInfo roomInfo = RoomInfo.builder()
                    .roomId(1L)
                    .roomName("테스트룸")
                    .placeId(100L)
                    .status(Status.OPEN)
                    .build();

            // When
            roomInfo.addReservationField("요청사항", FieldType.TEXT, true, 200, 1);
            roomInfo.addReservationField("인원수", FieldType.NUMBER, false, 10, 2);

            // Then
            assertThat(roomInfo.getReservationFieldCount()).isEqualTo(2);
            assertThat(roomInfo.getReservationFields().get(0).getTitle()).isEqualTo("요청사항");
            assertThat(roomInfo.getReservationFields().get(1).getTitle()).isEqualTo("인원수");
        }

        @Test
        @DisplayName("Given RoomInfo with 10 fields When adding 11th Then should throw exception")
        void shouldThrowExceptionWhenExceedingMaxFields() {
            // Given
            RoomInfo roomInfo = RoomInfo.builder()
                    .roomId(1L)
                    .roomName("테스트룸")
                    .placeId(100L)
                    .status(Status.OPEN)
                    .build();

            for (int i = 1; i <= 10; i++) {
                roomInfo.addReservationField("필드" + i, FieldType.TEXT, false, 100, i);
            }

            // When & Then
            assertThatThrownBy(() ->
                    roomInfo.addReservationField("필드11", FieldType.TEXT, false, 100, 11))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("최대 10개");
        }

        @Test
        @DisplayName("Given RoomInfo with fields When clearing Then should remove all fields")
        void shouldClearAllReservationFields() {
            // Given
            RoomInfo roomInfo = RoomInfo.builder()
                    .roomId(1L)
                    .roomName("테스트룸")
                    .placeId(100L)
                    .status(Status.OPEN)
                    .build();

            roomInfo.addReservationField("요청사항", FieldType.TEXT, true, 200, 1);
            roomInfo.addReservationField("인원수", FieldType.NUMBER, false, 10, 2);

            // When
            roomInfo.clearReservationFields();

            // Then
            assertThat(roomInfo.getReservationFieldCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("Given RoomInfo When adding field with auto sequence Then should set correct sequence")
        void shouldSetAutoSequence() {
            // Given
            RoomInfo roomInfo = RoomInfo.builder()
                    .roomId(1L)
                    .roomName("테스트룸")
                    .placeId(100L)
                    .status(Status.OPEN)
                    .build();

            // When
            roomInfo.addReservationField("필드1", FieldType.TEXT, false, 100, null);
            roomInfo.addReservationField("필드2", FieldType.TEXT, false, 100, null);

            // Then
            assertThat(roomInfo.getReservationFields().get(0).getSequence()).isEqualTo(1);
            assertThat(roomInfo.getReservationFields().get(1).getSequence()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("FieldType 테스트")
    class FieldTypeTest {

        @Test
        @DisplayName("Given all FieldTypes When checking Then should have correct values")
        void shouldHaveCorrectFieldTypes() {
            // Given & When & Then
            assertThat(FieldType.values()).containsExactly(FieldType.TEXT, FieldType.NUMBER, FieldType.SELECT);
        }
    }
}
