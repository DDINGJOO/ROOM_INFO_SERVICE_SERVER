package com.teambind.springproject.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teambind.springproject.config.TestConfig;
import com.teambind.springproject.dto.query.RoomSearchQuery;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class RoomQueryRepositoryMaxOccupancyTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RoomQueryRepository roomQueryRepository;

    private RoomInfo smallRoom;
    private RoomInfo mediumRoom;
    private RoomInfo largeRoom;
    private RoomInfo noOccupancyRoom;

    @BeforeEach
    void setUp() {
        // Given - Set up test data
        smallRoom = RoomInfo.builder()
                .roomId(1L)
                .roomName("Small Meeting Room")
                .placeId(100L)
                .status(Status.OPEN)
                .maxOccupancy(4)
                .build();

        mediumRoom = RoomInfo.builder()
                .roomId(2L)
                .roomName("Medium Conference Room")
                .placeId(100L)
                .status(Status.OPEN)
                .maxOccupancy(10)
                .build();

        largeRoom = RoomInfo.builder()
                .roomId(3L)
                .roomName("Large Auditorium")
                .placeId(100L)
                .status(Status.OPEN)
                .maxOccupancy(50)
                .build();

        noOccupancyRoom = RoomInfo.builder()
                .roomId(4L)
                .roomName("Unknown Capacity Room")
                .placeId(100L)
                .status(Status.OPEN)
                .maxOccupancy(null)
                .build();

        entityManager.persist(smallRoom);
        entityManager.persist(mediumRoom);
        entityManager.persist(largeRoom);
        entityManager.persist(noOccupancyRoom);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Given minOccupancy of 5 When searching Then should return rooms with capacity >= 5")
    void shouldFindRoomsWithMinOccupancy() {
        // Given
        RoomSearchQuery query = RoomSearchQuery.builder()
                .minOccupancy(5)
                .build();

        // When
        List<RoomInfo> results = roomQueryRepository.findAllByQuery(query);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(RoomInfo::getRoomId)
                .containsExactlyInAnyOrder(2L, 3L);
        assertThat(results).allMatch(room ->
                room.getMaxOccupancy() != null && room.getMaxOccupancy() >= 5);
    }

    @Test
    @DisplayName("Given minOccupancy of 15 When searching Then should return only large rooms")
    void shouldFindOnlyLargeRooms() {
        // Given
        RoomSearchQuery query = RoomSearchQuery.builder()
                .minOccupancy(15)
                .build();

        // When
        List<RoomInfo> results = roomQueryRepository.findAllByQuery(query);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getRoomId()).isEqualTo(3L);
        assertThat(results.get(0).getMaxOccupancy()).isEqualTo(50);
    }

    @Test
    @DisplayName("Given minOccupancy with other filters When searching Then should apply all filters")
    void shouldApplyMultipleFiltersIncludingMinOccupancy() {
        // Given
        RoomSearchQuery query = RoomSearchQuery.builder()
                .placeId(100L)
                .roomName("Room")
                .minOccupancy(8)
                .build();

        // When
        List<RoomInfo> results = roomQueryRepository.findAllByQuery(query);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).allMatch(room ->
                room.getPlaceId().equals(100L) &&
                room.getRoomName().contains("Room") &&
                room.getMaxOccupancy() != null &&
                room.getMaxOccupancy() >= 8);
    }

    @Test
    @DisplayName("Given no minOccupancy When searching Then should return all rooms")
    void shouldReturnAllRoomsWhenNoMinOccupancy() {
        // Given
        RoomSearchQuery query = RoomSearchQuery.builder()
                .placeId(100L)
                .build();

        // When
        List<RoomInfo> results = roomQueryRepository.findAllByQuery(query);

        // Then
        assertThat(results).hasSize(4);
        assertThat(results).extracting(RoomInfo::getRoomId)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 4L);
    }

    @Test
    @DisplayName("Given minOccupancy of 100 When searching Then should return empty list")
    void shouldReturnEmptyListWhenNoRoomsMeetMinOccupancy() {
        // Given
        RoomSearchQuery query = RoomSearchQuery.builder()
                .minOccupancy(100)
                .build();

        // When
        List<RoomInfo> results = roomQueryRepository.findAllByQuery(query);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Given minOccupancy of 1 When searching with null occupancy rooms Then should exclude null occupancy")
    void shouldExcludeNullOccupancyRoomsWhenMinOccupancySpecified() {
        // Given
        RoomSearchQuery query = RoomSearchQuery.builder()
                .minOccupancy(1)
                .build();

        // When
        List<RoomInfo> results = roomQueryRepository.findAllByQuery(query);

        // Then
        assertThat(results).hasSize(3);
        assertThat(results).extracting(RoomInfo::getRoomId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
        assertThat(results).noneMatch(room -> room.getMaxOccupancy() == null);
    }
}