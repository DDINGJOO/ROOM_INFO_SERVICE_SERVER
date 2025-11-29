package com.teambind.springproject.service;

import com.teambind.springproject.dto.query.RoomSearchQuery;
import com.teambind.springproject.dto.response.RoomDetailResponse;
import com.teambind.springproject.dto.response.RoomSimpleResponse;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.entity.enums.TimeSlot;
import com.teambind.springproject.mapper.RoomQueryMapper;
import com.teambind.springproject.repository.RoomQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomQueryServiceTest {

    @Mock
    private RoomQueryRepository roomQueryRepository;

    @Mock
    private RoomQueryMapper roomQueryMapper;

    @InjectMocks
    private RoomQueryService roomQueryService;

    private RoomInfo testRoom;
    private RoomSimpleResponse simpleResponse;
    private RoomDetailResponse detailResponse;

    @BeforeEach
    void setUp() {
        testRoom = RoomInfo.builder()
                .roomId(1L)
                .roomName("Test Room")
                .placeId(100L)
                .status(Status.OPEN)
                .timeSlot(TimeSlot.HOUR)
                .maxOccupancy(10)
                .build();

        simpleResponse = RoomSimpleResponse.builder()
                .roomId(1L)
                .roomName("Test Room")
                .placeId(100L)
                .timeSlot(TimeSlot.HOUR)
                .maxOccupancy(10)
                .build();

        detailResponse = RoomDetailResponse.builder()
                .roomId(1L)
                .roomName("Test Room")
                .placeId(100L)
                .status(Status.OPEN)
                .timeSlot(TimeSlot.HOUR)
                .maxOccupancy(10)
                .build();
    }

    @Test
    @DisplayName("Given search query with minOccupancy When searching Then should filter by occupancy")
    void shouldSearchRoomsWithMinOccupancy() {
        // Given
        RoomSearchQuery query = RoomSearchQuery.builder()
                .minOccupancy(5)
                .build();
        List<RoomInfo> rooms = Arrays.asList(testRoom);
        List<RoomSimpleResponse> expectedResponses = Arrays.asList(simpleResponse);

        when(roomQueryRepository.findAllByQuery(any(RoomSearchQuery.class))).thenReturn(rooms);
        when(roomQueryMapper.toSimpleResponseList(rooms)).thenReturn(expectedResponses);

        // When
        List<RoomSimpleResponse> result = roomQueryService.searchRooms(query);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMaxOccupancy()).isEqualTo(10);
        verify(roomQueryRepository).findAllByQuery(argThat(q ->
            q.getMinOccupancy() != null && q.getMinOccupancy().equals(5)));
    }

    @Test
    @DisplayName("Given room ID When finding room by ID Then should return room with maxOccupancy")
    void shouldFindRoomByIdWithMaxOccupancy() {
        // Given
        Long roomId = 1L;
        when(roomQueryRepository.findByIdWithDetails(roomId)).thenReturn(Optional.of(testRoom));
        when(roomQueryMapper.toDetailResponse(testRoom)).thenReturn(detailResponse);

        // When
        RoomDetailResponse result = roomQueryService.findRoomById(roomId);

        // Then
        assertThat(result.getRoomId()).isEqualTo(roomId);
        assertThat(result.getMaxOccupancy()).isEqualTo(10);
        verify(roomQueryRepository).findByIdWithDetails(roomId);
    }

    @Test
    @DisplayName("Given non-existent room ID When finding room Then should throw exception")
    void shouldThrowExceptionForNonExistentRoom() {
        // Given
        Long roomId = 999L;
        when(roomQueryRepository.findByIdWithDetails(roomId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roomQueryService.findRoomById(roomId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Room not found with id: " + roomId);
    }

    @Test
    @DisplayName("Given place ID When finding rooms by place Then should return all rooms for place")
    void shouldFindRoomsByPlaceId() {
        // Given
        Long placeId = 100L;
        List<RoomInfo> rooms = Arrays.asList(testRoom);
        List<RoomSimpleResponse> expectedResponses = Arrays.asList(simpleResponse);

        when(roomQueryRepository.findAllByQuery(any(RoomSearchQuery.class))).thenReturn(rooms);
        when(roomQueryMapper.toSimpleResponseList(rooms)).thenReturn(expectedResponses);

        // When
        List<RoomSimpleResponse> result = roomQueryService.findRoomsByPlaceId(placeId);

        // Then
        assertThat(result).hasSize(1);
        verify(roomQueryRepository).findAllByQuery(argThat(query ->
                query.getPlaceId() != null && query.getPlaceId().equals(placeId)));
    }

    @Test
    @DisplayName("Given room name When searching by name Then should return matching rooms")
    void shouldFindRoomsByName() {
        // Given
        String roomName = "Conference";
        List<RoomInfo> rooms = Arrays.asList(testRoom);
        List<RoomSimpleResponse> expectedResponses = Arrays.asList(simpleResponse);

        when(roomQueryRepository.findAllByQuery(any(RoomSearchQuery.class))).thenReturn(rooms);
        when(roomQueryMapper.toSimpleResponseList(rooms)).thenReturn(expectedResponses);

        // When
        List<RoomSimpleResponse> result = roomQueryService.findRoomsByName(roomName);

        // Then
        assertThat(result).hasSize(1);
        verify(roomQueryRepository).findAllByQuery(argThat(query ->
                query.getRoomName() != null && query.getRoomName().equals(roomName)));
    }

    @Test
    @DisplayName("Given keyword IDs When searching by keywords Then should return matching rooms")
    void shouldFindRoomsByKeywords() {
        // Given
        List<Long> keywordIds = Arrays.asList(1L, 2L);
        List<RoomInfo> rooms = Arrays.asList(testRoom);
        List<RoomSimpleResponse> expectedResponses = Arrays.asList(simpleResponse);

        when(roomQueryRepository.findAllByQuery(any(RoomSearchQuery.class))).thenReturn(rooms);
        when(roomQueryMapper.toSimpleResponseList(rooms)).thenReturn(expectedResponses);

        // When
        List<RoomSimpleResponse> result = roomQueryService.findRoomsByKeywords(keywordIds);

        // Then
        assertThat(result).hasSize(1);
        verify(roomQueryRepository).findAllByQuery(argThat(query ->
                query.getKeywordIds() != null && query.getKeywordIds().equals(keywordIds)));
    }

    @Test
    @DisplayName("Given multiple room IDs When finding by IDs Then should return all rooms")
    void shouldFindRoomsByIds() {
        // Given
        List<Long> roomIds = Arrays.asList(1L, 2L, 3L);
        List<RoomInfo> rooms = Arrays.asList(testRoom);
        List<RoomDetailResponse> expectedResponses = Arrays.asList(detailResponse);

        when(roomQueryRepository.findByIdsWithDetails(roomIds)).thenReturn(rooms);
        when(roomQueryMapper.toDetailResponseList(rooms)).thenReturn(expectedResponses);

        // When
        List<RoomDetailResponse> result = roomQueryService.findRoomsByIds(roomIds);

        // Then
        assertThat(result).hasSize(1);
        verify(roomQueryRepository).findByIdsWithDetails(roomIds);
    }
}