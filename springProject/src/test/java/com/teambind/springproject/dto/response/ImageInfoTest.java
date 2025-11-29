package com.teambind.springproject.dto.response;

import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.attribute.image.RoomImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageInfoTest {

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
	@DisplayName("RoomImage 엔티티로부터 ImageInfo DTO를 생성할 수 있다")
	void createImageInfoFromEntity() {
		// Given
		RoomImage roomImage = new RoomImage("img001", "https://example.com/image.jpg", 1L, roomInfo);

		// When
		ImageInfo imageInfo = ImageInfo.fromEntity(roomImage);

		// Then
		assertThat(imageInfo).isNotNull();
		assertThat(imageInfo.getImageId()).isEqualTo("img001");
		assertThat(imageInfo.getImageUrl()).isEqualTo("https://example.com/image.jpg");
		assertThat(imageInfo.getSequence()).isEqualTo(1L);
	}

	@Test
	@DisplayName("null RoomImage로부터 ImageInfo 생성시 null을 반환한다")
	void returnNullForNullEntity() {
		// When
		ImageInfo imageInfo = ImageInfo.fromEntity(null);

		// Then
		assertThat(imageInfo).isNull();
	}

	@Test
	@DisplayName("imageId가 비어있는 RoomImage로부터 ImageInfo 생성시 null을 반환한다")
	void returnNullForEmptyImageId() {
		// Given
		RoomImage roomImage = RoomImage.builder()
				.imageId("")
				.imageUrl("https://example.com/image.jpg")
				.sequence(1L)
				.roomInfo(roomInfo)
				.build();

		// When
		ImageInfo imageInfo = ImageInfo.fromEntity(roomImage);

		// Then
		assertThat(imageInfo).isNull();
	}

	@Test
	@DisplayName("imageUrl이 비어있는 RoomImage로부터 ImageInfo 생성시 null을 반환한다")
	void returnNullForEmptyImageUrl() {
		// Given
		RoomImage roomImage = RoomImage.builder()
				.imageId("img001")
				.imageUrl("")
				.sequence(1L)
				.roomInfo(roomInfo)
				.build();

		// When
		ImageInfo imageInfo = ImageInfo.fromEntity(roomImage);

		// Then
		assertThat(imageInfo).isNull();
	}

	@Test
	@DisplayName("sequence가 null인 경우 기본값 1로 설정된다")
	void setDefaultSequenceWhenNull() {
		// Given
		RoomImage roomImage = RoomImage.builder()
				.imageId("img001")
				.imageUrl("https://example.com/image.jpg")
				.sequence(null)
				.roomInfo(roomInfo)
				.build();

		// When
		ImageInfo imageInfo = ImageInfo.fromEntity(roomImage);

		// Then
		assertThat(imageInfo).isNotNull();
		assertThat(imageInfo.getSequence()).isEqualTo(1L);
	}

	@Test
	@DisplayName("builder를 사용하여 ImageInfo를 생성할 수 있다")
	void createImageInfoWithBuilder() {
		// When
		ImageInfo imageInfo = ImageInfo.builder()
				.imageId("img001")
				.imageUrl("https://example.com/image.jpg")
				.sequence(5L)
				.build();

		// Then
		assertThat(imageInfo.getImageId()).isEqualTo("img001");
		assertThat(imageInfo.getImageUrl()).isEqualTo("https://example.com/image.jpg");
		assertThat(imageInfo.getSequence()).isEqualTo(5L);
	}
}