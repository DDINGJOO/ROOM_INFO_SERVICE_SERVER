package com.teambind.springproject.dto.response;

import com.teambind.springproject.entity.attribute.image.RoomImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이미지 정보 DTO
 * imageId와 imageUrl은 항상 쌍으로 관리되며, 이미지의 고유 식별자와 접근 URL을 제공합니다.
 *
 * 응답 예시:
 * {
 *   "imageId": "img_12345",
 *   "imageUrl": "https://example.com/images/img_12345.jpg",
 *   "sequence": 1
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageInfo {

	/** 이미지 고유 식별자 */
	private String imageId;

	/** 이미지 접근 URL */
	private String imageUrl;

	/** 이미지 순서 (1부터 시작) */
	private Long sequence;

	/**
	 * RoomImage 엔티티로부터 ImageInfo DTO를 생성합니다.
	 * imageId와 imageUrl이 쌍으로 존재하지 않는 경우 null을 반환합니다.
	 *
	 * @param roomImage 변환할 RoomImage 엔티티
	 * @return ImageInfo DTO 또는 null
	 */
	public static ImageInfo fromEntity(RoomImage roomImage) {
		if (roomImage == null) {
			return null;
		}

		// imageId와 imageUrl이 쌍으로 존재하는지 확인
		String imageId = roomImage.getImageId();
		String imageUrl = roomImage.getImageUrl();

		if (imageId == null || imageId.trim().isEmpty() ||
		    imageUrl == null || imageUrl.trim().isEmpty()) {
			// 유효하지 않은 이미지는 null 반환
			return null;
		}

		return ImageInfo.builder()
				.imageId(imageId)
				.imageUrl(imageUrl)
				.sequence(roomImage.getSequence() != null ? roomImage.getSequence() : 1L)
				.build();
	}
}