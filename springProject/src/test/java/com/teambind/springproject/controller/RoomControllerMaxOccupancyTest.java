package com.teambind.springproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.dto.request.RoomCreateRequest;
import com.teambind.springproject.dto.response.RoomSimpleResponse;
import com.teambind.springproject.entity.enums.TimeSlot;
import com.teambind.springproject.service.RoomCommandService;
import com.teambind.springproject.service.RoomQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({RoomCommandController.class, RoomQueryController.class})
class RoomControllerMaxOccupancyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomCommandService roomCommandService;

    @MockBean
    private RoomQueryService roomQueryService;

    @Test
    @DisplayName("Given valid maxOccupancy When creating room Then should accept and return created ID")
    void shouldCreateRoomWithMaxOccupancy() throws Exception {
        // Given
        RoomCreateRequest request = RoomCreateRequest.builder()
                .roomName("Conference Room")
                .placeId(100L)
                .timeSlot(TimeSlot.MORNING)
                .maxOccupancy(20)
                .build();

        when(roomCommandService.createRoom(any())).thenReturn(1L);

        // When & Then
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Given invalid maxOccupancy When creating room Then should return bad request")
    void shouldReturnBadRequestForInvalidMaxOccupancy() throws Exception {
        // Given
        RoomCreateRequest request = RoomCreateRequest.builder()
                .roomName("Conference Room")
                .placeId(100L)
                .timeSlot(TimeSlot.MORNING)
                .maxOccupancy(0)  // Invalid: less than 1
                .build();

        // When & Then
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.maxOccupancy").exists());
    }

    @Test
    @DisplayName("Given null maxOccupancy When creating room Then should accept request")
    void shouldAcceptNullMaxOccupancy() throws Exception {
        // Given
        RoomCreateRequest request = RoomCreateRequest.builder()
                .roomName("Conference Room")
                .placeId(100L)
                .timeSlot(TimeSlot.MORNING)
                .maxOccupancy(null)  // Null is allowed
                .build();

        when(roomCommandService.createRoom(any())).thenReturn(1L);

        // When & Then
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Given minOccupancy parameter When searching rooms Then should filter by occupancy")
    void shouldSearchRoomsByMinOccupancy() throws Exception {
        // Given
        List<RoomSimpleResponse> mockResponses = Arrays.asList(
                RoomSimpleResponse.builder()
                        .roomId(1L)
                        .roomName("Large Room")
                        .placeId(100L)
                        .maxOccupancy(20)
                        .build(),
                RoomSimpleResponse.builder()
                        .roomId(2L)
                        .roomName("Medium Room")
                        .placeId(100L)
                        .maxOccupancy(10)
                        .build()
        );

        when(roomQueryService.searchRooms(argThat(query ->
                query.getMinOccupancy() != null && query.getMinOccupancy().equals(10)
        ))).thenReturn(mockResponses);

        // When & Then
        mockMvc.perform(get("/api/rooms/search")
                        .param("minOccupancy", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].maxOccupancy").value(20))
                .andExpect(jsonPath("$[1].maxOccupancy").value(10));
    }

    @Test
    @DisplayName("Given multiple filters including minOccupancy When searching Then should apply all filters")
    void shouldApplyAllFiltersIncludingMinOccupancy() throws Exception {
        // Given
        List<RoomSimpleResponse> mockResponses = Arrays.asList(
                RoomSimpleResponse.builder()
                        .roomId(1L)
                        .roomName("Conference Room A")
                        .placeId(100L)
                        .maxOccupancy(15)
                        .build()
        );

        when(roomQueryService.searchRooms(argThat(query ->
                query.getMinOccupancy() != null && query.getMinOccupancy().equals(10) &&
                query.getPlaceId() != null && query.getPlaceId().equals(100L) &&
                query.getRoomName() != null && query.getRoomName().equals("Conference")
        ))).thenReturn(mockResponses);

        // When & Then
        mockMvc.perform(get("/api/rooms/search")
                        .param("roomName", "Conference")
                        .param("placeId", "100")
                        .param("minOccupancy", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].roomName").value("Conference Room A"))
                .andExpect(jsonPath("$[0].maxOccupancy").value(15));
    }

    @Test
    @DisplayName("Given invalid minOccupancy When searching Then should ignore and proceed")
    void shouldHandleInvalidMinOccupancyInSearch() throws Exception {
        // Given
        when(roomQueryService.searchRooms(any())).thenReturn(Arrays.asList());

        // When & Then - The controller accepts the parameter as Integer, so invalid values become null
        mockMvc.perform(get("/api/rooms/search")
                        .param("minOccupancy", "invalid"))
                .andDo(print())
                .andExpect(status().isBadRequest());  // Spring will return 400 for type conversion error
    }

    @Test
    @DisplayName("Given no minOccupancy When searching Then should return all rooms")
    void shouldReturnAllRoomsWhenNoMinOccupancySpecified() throws Exception {
        // Given
        List<RoomSimpleResponse> mockResponses = Arrays.asList(
                RoomSimpleResponse.builder()
                        .roomId(1L)
                        .roomName("Room 1")
                        .placeId(100L)
                        .maxOccupancy(5)
                        .build(),
                RoomSimpleResponse.builder()
                        .roomId(2L)
                        .roomName("Room 2")
                        .placeId(100L)
                        .maxOccupancy(null)  // Room with no occupancy info
                        .build(),
                RoomSimpleResponse.builder()
                        .roomId(3L)
                        .roomName("Room 3")
                        .placeId(100L)
                        .maxOccupancy(20)
                        .build()
        );

        when(roomQueryService.searchRooms(argThat(query ->
                query.getMinOccupancy() == null
        ))).thenReturn(mockResponses);

        // When & Then
        mockMvc.perform(get("/api/rooms/search"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }
}