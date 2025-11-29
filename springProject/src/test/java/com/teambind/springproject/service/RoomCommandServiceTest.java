package com.teambind.springproject.service;

import com.teambind.springproject.dto.command.RoomCreateCommand;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.entity.enums.TimeSlot;
import com.teambind.springproject.event.events.RoomCreatedEvent;
import com.teambind.springproject.event.events.RoomDeletedEvent;
import com.teambind.springproject.event.publisher.EventPublisher;
import com.teambind.springproject.mapper.RoomMapper;
import com.teambind.springproject.repository.RoomCommandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomCommandServiceTest {

    @Mock
    private RoomCommandRepository roomCommandRepository;

    @Mock
    private RoomMapper roomMapper;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private RoomCommandService roomCommandService;

    private RoomInfo testRoom;
    private RoomCreateCommand createCommand;

    @BeforeEach
    void setUp() {
        createCommand = RoomCreateCommand.builder()
                .roomName("Conference Room")
                .placeId(100L)
                .timeSlot(TimeSlot.HOUR)
                .maxOccupancy(20)
                .build();

        testRoom = RoomInfo.builder()
                .roomId(1L)
                .roomName("Conference Room")
                .placeId(100L)
                .status(Status.PENDING)
                .timeSlot(TimeSlot.HOUR)
                .maxOccupancy(20)
                .build();
    }

    @Test
    @DisplayName("Given valid command with maxOccupancy When creating room Then should save and publish event")
    void shouldCreateRoomWithMaxOccupancy() {
        // Given
        when(roomMapper.toEntity(createCommand)).thenReturn(testRoom);
        when(roomCommandRepository.save(testRoom)).thenReturn(testRoom);

        // When
        Long roomId = roomCommandService.createRoom(createCommand);

        // Then
        assertThat(roomId).isEqualTo(1L);
        verify(roomCommandRepository).save(testRoom);
        verify(eventPublisher).publish(argThat(event ->
                event instanceof RoomCreatedEvent &&
                ((RoomCreatedEvent) event).getRoomId().equals(1L)));
    }

    @Test
    @DisplayName("Given command with null maxOccupancy When creating room Then should allow creation")
    void shouldCreateRoomWithNullMaxOccupancy() {
        // Given
        RoomCreateCommand commandWithoutOccupancy = RoomCreateCommand.builder()
                .roomName("Meeting Room")
                .placeId(100L)
                .timeSlot(TimeSlot.HALFHOUR)
                .maxOccupancy(null)
                .build();

        RoomInfo roomWithoutOccupancy = RoomInfo.builder()
                .roomId(2L)
                .roomName("Meeting Room")
                .placeId(100L)
                .status(Status.PENDING)
                .timeSlot(TimeSlot.HALFHOUR)
                .maxOccupancy(null)
                .build();

        when(roomMapper.toEntity(commandWithoutOccupancy)).thenReturn(roomWithoutOccupancy);
        when(roomCommandRepository.save(roomWithoutOccupancy)).thenReturn(roomWithoutOccupancy);

        // When
        Long roomId = roomCommandService.createRoom(commandWithoutOccupancy);

        // Then
        assertThat(roomId).isEqualTo(2L);
        verify(roomCommandRepository).save(roomWithoutOccupancy);
    }

    @Test
    @DisplayName("Given room ID When deleting room Then should delete and publish event")
    void shouldDeleteRoom() {
        // Given
        Long roomId = 1L;
        when(roomCommandRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        // When
        Long deletedRoomId = roomCommandService.deleteRoom(roomId);

        // Then
        assertThat(deletedRoomId).isEqualTo(roomId);
        verify(roomCommandRepository).deleteById(roomId);
        verify(eventPublisher).publish(argThat(event ->
                event instanceof RoomDeletedEvent &&
                ((RoomDeletedEvent) event).getRoomId().equals(roomId)));
    }

    @Test
    @DisplayName("Given non-existent room ID When deleting Then should throw exception")
    void shouldThrowExceptionWhenDeletingNonExistentRoom() {
        // Given
        Long roomId = 999L;
        when(roomCommandRepository.findById(roomId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roomCommandService.deleteRoom(roomId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Room not found: " + roomId);
    }

    @Test
    @DisplayName("Given command When creating room Then should verify maxOccupancy is saved")
    void shouldVerifyMaxOccupancyIsSaved() {
        // Given
        RoomCreateCommand commandWith15Capacity = RoomCreateCommand.builder()
                .roomName("Medium Room")
                .placeId(100L)
                .timeSlot(TimeSlot.HOUR)
                .maxOccupancy(15)
                .build();

        RoomInfo roomWith15Capacity = RoomInfo.builder()
                .roomId(3L)
                .roomName("Medium Room")
                .placeId(100L)
                .status(Status.PENDING)
                .timeSlot(TimeSlot.HOUR)
                .maxOccupancy(15)
                .build();

        when(roomMapper.toEntity(commandWith15Capacity)).thenReturn(roomWith15Capacity);
        when(roomCommandRepository.save(argThat(room ->
                room.getMaxOccupancy() != null && room.getMaxOccupancy().equals(15)
        ))).thenReturn(roomWith15Capacity);

        // When
        Long roomId = roomCommandService.createRoom(commandWith15Capacity);

        // Then
        assertThat(roomId).isEqualTo(3L);
        verify(roomCommandRepository).save(argThat(room ->
                room.getMaxOccupancy().equals(15)));
    }
}