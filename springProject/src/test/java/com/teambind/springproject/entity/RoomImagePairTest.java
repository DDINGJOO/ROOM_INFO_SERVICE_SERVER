package com.teambind.springproject.entity;

import com.teambind.springproject.entity.attribute.image.RoomImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomImagePairTest {

	private RoomInfo roomInfo;

	@BeforeEach
	void setUp() {
		roomInfo = RoomInfo.builder()
				.roomId(1L)
				.roomName("Test Room")
				.placeId(100L)
				.build();
	}

	@Test
	@DisplayName("이미지 ID와 URL 쌍으로 이미지를 추가할 수 있다")
	void addRoomImageWithPair() {
		// When
		roomInfo.addRoomImage("img001", "https://example.com/image1.jpg");
		roomInfo.addRoomImage("img002", "https://example.com/image2.jpg");

		// Then
		assertThat(roomInfo.getRoomImages()).hasSize(2);

		RoomImage firstImage = roomInfo.getRoomImages().get(0);
		assertThat(firstImage.getImageId()).isEqualTo("img001");
		assertThat(firstImage.getImageUrl()).isEqualTo("https://example.com/image1.jpg");
		assertThat(firstImage.getSequence()).isEqualTo(1L);

		RoomImage secondImage = roomInfo.getRoomImages().get(1);
		assertThat(secondImage.getImageId()).isEqualTo("img002");
		assertThat(secondImage.getImageUrl()).isEqualTo("https://example.com/image2.jpg");
		assertThat(secondImage.getSequence()).isEqualTo(2L);
	}

	@Test
	@DisplayName("sequence를 지정하여 이미지를 추가할 수 있다")
	void addRoomImageWithSequence() {
		// When
		roomInfo.addRoomImageWithSequence("img001", "https://example.com/image1.jpg", 5L);
		roomInfo.addRoomImageWithSequence("img002", "https://example.com/image2.jpg", 2L);

		// Then
		assertThat(roomInfo.getRoomImages()).hasSize(2);

		RoomImage firstImage = roomInfo.getRoomImages().get(0);
		assertThat(firstImage.getSequence()).isEqualTo(5L);

		RoomImage secondImage = roomInfo.getRoomImages().get(1);
		assertThat(secondImage.getSequence()).isEqualTo(2L);
	}

	@Test
	@DisplayName("imageId가 null인 경우 예외가 발생한다")
	void throwExceptionWhenImageIdIsNull() {
		// When & Then
		assertThatThrownBy(() -> roomInfo.addRoomImage(null, "https://example.com/image.jpg"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Cannot add image with null imageId or imageUrl");
	}

	@Test
	@DisplayName("imageUrl이 null인 경우 예외가 발생한다")
	void throwExceptionWhenImageUrlIsNull() {
		// When & Then
		assertThatThrownBy(() -> roomInfo.addRoomImage("img001", null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Cannot add image with null imageId or imageUrl");
	}

	@Test
	@DisplayName("유효하지 않은 URL 형식인 경우 예외가 발생한다")
	void throwExceptionForInvalidUrlFormat() {
		// When & Then
		assertThatThrownBy(() -> new RoomImage("img001", "invalid-url", 1L, roomInfo))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Invalid image URL format");
	}

	@Test
	@DisplayName("imageId가 너무 짧은 경우 예외가 발생한다")
	void throwExceptionForShortImageId() {
		// When & Then
		assertThatThrownBy(() -> new RoomImage("ab", "https://example.com/image.jpg", 1L, roomInfo))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Image ID is too short");
	}

	@Test
	@DisplayName("상대 경로 URL도 유효한 URL로 처리된다")
	void acceptRelativeUrlPath() {
		// When
		roomInfo.addRoomImage("img001", "/images/room/image1.jpg");

		// Then
		assertThat(roomInfo.getRoomImages()).hasSize(1);
		RoomImage image = roomInfo.getRoomImages().get(0);
		assertThat(image.getImageUrl()).isEqualTo("/images/room/image1.jpg");
	}

	@Test
	@DisplayName("최대 이미지 개수를 초과하면 예외가 발생한다")
	void throwExceptionWhenExceedingMaxImages() {
		// Given: 10개의 이미지 추가
		for (int i = 1; i <= 10; i++) {
			roomInfo.addRoomImage("img" + String.format("%03d", i),
					"https://example.com/image" + i + ".jpg");
		}

		// When & Then: 11번째 이미지 추가 시 예외 발생
		assertThatThrownBy(() -> roomInfo.addRoomImage("img011", "https://example.com/image11.jpg"))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("방 이미지는 최대 10개까지 추가할 수 있습니다");
	}

	@Test
	@DisplayName("RoomImage의 updateImagePair 메소드로 이미지 정보를 쌍으로 업데이트할 수 있다")
	void updateImagePair() {
		// Given
		RoomImage roomImage = new RoomImage("img001", "https://example.com/old.jpg", 1L, roomInfo);

		// When
		roomImage.updateImagePair("img002", "https://example.com/new.jpg");

		// Then
		assertThat(roomImage.getImageId()).isEqualTo("img002");
		assertThat(roomImage.getImageUrl()).isEqualTo("https://example.com/new.jpg");
		assertThat(roomImage.getSequence()).isEqualTo(1L); // sequence는 변경되지 않음
	}
}