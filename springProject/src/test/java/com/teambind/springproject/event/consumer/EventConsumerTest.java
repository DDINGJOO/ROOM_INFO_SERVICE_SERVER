package com.teambind.springproject.event.consumer;

import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.entity.enums.TimeSlot;
import com.teambind.springproject.repository.RoomCommandRepository;
import com.teambind.springproject.util.json.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

    @Mock
    private JsonUtil jsonUtil;

    @Mock
    private RoomCommandRepository roomCommandRepository;

    @InjectMocks
    private EventConsumer eventConsumer;

    private RoomInfo testRoomInfo;

    @BeforeEach
    void setUp() {
        testRoomInfo = RoomInfo.builder()
                .roomId(1L)
                .roomName("Test Room")
                .placeId(100L)
                .status(Status.OPEN)
                .timeSlot(TimeSlot.HOUR)
                .build();
    }

    @Test
    @DisplayName("이미지 변경 이벤트를 받아 Room의 이미지를 업데이트한다")
    void testPlaceImageChanged_Success() {
        // Given
        String message = """
            {
                "referenceId": "1",
                "images": [
                    {
                        "imageId": "img_001",
                        "imageUrl": "https://example.com/image1.jpg",
                        "referenceId": "1",
                        "sequence": 1
                    },
                    {
                        "imageId": "img_002",
                        "imageUrl": "https://example.com/image2.jpg",
                        "referenceId": "1",
                        "sequence": 2
                    }
                ]
            }
            """;

        when(jsonUtil.fromJson(eq(message), any())).thenReturn(
                new com.teambind.springproject.event.events.image.ImagesChangeEventWrapper(
                        "1",
                        java.util.List.of(
                                new com.teambind.springproject.event.events.image.SequentialImageChangeEvent(
                                        "img_001",
                                        "https://example.com/image1.jpg",
                                        "1",
                                        1
                                ),
                                new com.teambind.springproject.event.events.image.SequentialImageChangeEvent(
                                        "img_002",
                                        "https://example.com/image2.jpg",
                                        "1",
                                        2
                                )
                        )
                )
        );

        when(roomCommandRepository.findById(1L)).thenReturn(Optional.of(testRoomInfo));

        // When
        eventConsumer.placeImageChanged(message);

        // Then
        ArgumentCaptor<RoomInfo> roomCaptor = ArgumentCaptor.forClass(RoomInfo.class);
        verify(roomCommandRepository).save(roomCaptor.capture());

        RoomInfo savedRoom = roomCaptor.getValue();
        assertThat(savedRoom.getRoomImageCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("imageId가 없는 이벤트는 무시한다")
    void testPlaceImageChanged_MissingImageId() {
        // Given
        String message = """
            {
                "referenceId": "1",
                "images": [
                    {
                        "imageUrl": "https://example.com/image1.jpg",
                        "referenceId": "1",
                        "sequence": 1
                    }
                ]
            }
            """;

        when(jsonUtil.fromJson(eq(message), any())).thenReturn(
                new com.teambind.springproject.event.events.image.ImagesChangeEventWrapper(
                        "1",
                        java.util.List.of(
                                new com.teambind.springproject.event.events.image.SequentialImageChangeEvent(
                                        null,
                                        "https://example.com/image1.jpg",
                                        "1",
                                        1
                                )
                        )
                )
        );

        when(roomCommandRepository.findById(1L)).thenReturn(Optional.of(testRoomInfo));

        // When
        eventConsumer.placeImageChanged(message);

        // Then
        ArgumentCaptor<RoomInfo> roomCaptor = ArgumentCaptor.forClass(RoomInfo.class);
        verify(roomCommandRepository).save(roomCaptor.capture());

        RoomInfo savedRoom = roomCaptor.getValue();
        assertThat(savedRoom.getRoomImageCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("imageUrl이 없는 이벤트는 무시한다")
    void testPlaceImageChanged_MissingImageUrl() {
        // Given
        String message = """
            {
                "referenceId": "1",
                "images": [
                    {
                        "imageId": "img_001",
                        "referenceId": "1",
                        "sequence": 1
                    }
                ]
            }
            """;

        when(jsonUtil.fromJson(eq(message), any())).thenReturn(
                new com.teambind.springproject.event.events.image.ImagesChangeEventWrapper(
                        "1",
                        java.util.List.of(
                                new com.teambind.springproject.event.events.image.SequentialImageChangeEvent(
                                        "img_001",
                                        null,
                                        "1",
                                        1
                                )
                        )
                )
        );

        when(roomCommandRepository.findById(1L)).thenReturn(Optional.of(testRoomInfo));

        // When
        eventConsumer.placeImageChanged(message);

        // Then
        ArgumentCaptor<RoomInfo> roomCaptor = ArgumentCaptor.forClass(RoomInfo.class);
        verify(roomCommandRepository).save(roomCaptor.capture());

        RoomInfo savedRoom = roomCaptor.getValue();
        assertThat(savedRoom.getRoomImageCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("잘못된 URL 형식의 이미지는 무시한다")
    void testPlaceImageChanged_InvalidUrlFormat() {
        // Given
        String message = """
            {
                "referenceId": "1",
                "images": [
                    {
                        "imageId": "img_001",
                        "imageUrl": "invalid-url",
                        "referenceId": "1",
                        "sequence": 1
                    }
                ]
            }
            """;

        when(jsonUtil.fromJson(eq(message), any())).thenReturn(
                new com.teambind.springproject.event.events.image.ImagesChangeEventWrapper(
                        "1",
                        java.util.List.of(
                                new com.teambind.springproject.event.events.image.SequentialImageChangeEvent(
                                        "img_001",
                                        "invalid-url",
                                        "1",
                                        1
                                )
                        )
                )
        );

        when(roomCommandRepository.findById(1L)).thenReturn(Optional.of(testRoomInfo));

        // When
        eventConsumer.placeImageChanged(message);

        // Then
        ArgumentCaptor<RoomInfo> roomCaptor = ArgumentCaptor.forClass(RoomInfo.class);
        verify(roomCommandRepository).save(roomCaptor.capture());

        RoomInfo savedRoom = roomCaptor.getValue();
        assertThat(savedRoom.getRoomImageCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("빈 이미지 리스트를 받으면 기존 이미지를 모두 삭제한다")
    void testPlaceImageChanged_EmptyImageList() {
        // Given
        String message = """
            {
                "referenceId": "1",
                "images": []
            }
            """;

        // 기존 이미지가 있는 Room 설정
        testRoomInfo.addRoomImage("existing_img", "https://example.com/existing.jpg");

        when(jsonUtil.fromJson(eq(message), any())).thenReturn(
                new com.teambind.springproject.event.events.image.ImagesChangeEventWrapper(
                        "1",
                        java.util.Collections.emptyList()
                )
        );

        when(roomCommandRepository.findById(1L)).thenReturn(Optional.of(testRoomInfo));

        // When
        eventConsumer.placeImageChanged(message);

        // Then
        ArgumentCaptor<RoomInfo> roomCaptor = ArgumentCaptor.forClass(RoomInfo.class);
        verify(roomCommandRepository).save(roomCaptor.capture());

        RoomInfo savedRoom = roomCaptor.getValue();
        assertThat(savedRoom.getRoomImageCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("sequence가 없는 경우 자동으로 순서를 부여한다")
    void testPlaceImageChanged_WithoutSequence() {
        // Given
        String message = """
            {
                "referenceId": "1",
                "images": [
                    {
                        "imageId": "img_001",
                        "imageUrl": "https://example.com/image1.jpg",
                        "referenceId": "1"
                    },
                    {
                        "imageId": "img_002",
                        "imageUrl": "https://example.com/image2.jpg",
                        "referenceId": "1"
                    }
                ]
            }
            """;

        when(jsonUtil.fromJson(eq(message), any())).thenReturn(
                new com.teambind.springproject.event.events.image.ImagesChangeEventWrapper(
                        "1",
                        java.util.List.of(
                                new com.teambind.springproject.event.events.image.SequentialImageChangeEvent(
                                        "img_001",
                                        "https://example.com/image1.jpg",
                                        "1",
                                        null
                                ),
                                new com.teambind.springproject.event.events.image.SequentialImageChangeEvent(
                                        "img_002",
                                        "https://example.com/image2.jpg",
                                        "1",
                                        null
                                )
                        )
                )
        );

        when(roomCommandRepository.findById(1L)).thenReturn(Optional.of(testRoomInfo));

        // When
        eventConsumer.placeImageChanged(message);

        // Then
        ArgumentCaptor<RoomInfo> roomCaptor = ArgumentCaptor.forClass(RoomInfo.class);
        verify(roomCommandRepository).save(roomCaptor.capture());

        RoomInfo savedRoom = roomCaptor.getValue();
        assertThat(savedRoom.getRoomImageCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Room이 존재하지 않으면 이벤트를 무시한다")
    void testPlaceImageChanged_RoomNotFound() {
        // Given
        String message = """
            {
                "referenceId": "999",
                "images": [
                    {
                        "imageId": "img_001",
                        "imageUrl": "https://example.com/image1.jpg",
                        "referenceId": "999",
                        "sequence": 1
                    }
                ]
            }
            """;

        when(jsonUtil.fromJson(eq(message), any())).thenReturn(
                new com.teambind.springproject.event.events.image.ImagesChangeEventWrapper(
                        "999",
                        java.util.List.of(
                                new com.teambind.springproject.event.events.image.SequentialImageChangeEvent(
                                        "img_001",
                                        "https://example.com/image1.jpg",
                                        "999",
                                        1
                                )
                        )
                )
        );

        when(roomCommandRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        eventConsumer.placeImageChanged(message);

        // Then
        verify(roomCommandRepository, never()).save(any(RoomInfo.class));
    }

    @Test
    @DisplayName("잘못된 roomId 형식은 처리하지 않는다")
    void testPlaceImageChanged_InvalidRoomIdFormat() {
        // Given
        String message = """
            {
                "referenceId": "invalid_id",
                "images": []
            }
            """;

        when(jsonUtil.fromJson(eq(message), any())).thenReturn(
                new com.teambind.springproject.event.events.image.ImagesChangeEventWrapper(
                        "invalid_id",
                        java.util.Collections.emptyList()
                )
        );

        // When
        eventConsumer.placeImageChanged(message);

        // Then
        verify(roomCommandRepository, never()).findById(any());
        verify(roomCommandRepository, never()).save(any());
    }
}