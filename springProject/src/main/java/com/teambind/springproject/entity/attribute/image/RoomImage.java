package com.teambind.springproject.entity.attribute.image;


import com.teambind.springproject.entity.RoomInfo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_image")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RoomImage {

	@Id
	@Column(name = "image_id", length = 100)
	private String imageId;

	@Setter(AccessLevel.PROTECTED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id", nullable = false)
	private RoomInfo roomInfo;

	@Column(name = "image_url", nullable = false, length = 500)
	private String imageUrl;

	@Column(name = "sequence")
	private Long sequence;

	// 생성자에서 검증 추가
	public RoomImage(String imageId, String imageUrl, Long sequence, RoomInfo roomInfo) {
		validateImagePair(imageId, imageUrl);
		this.imageId = imageId;
		this.imageUrl = imageUrl;
		this.sequence = sequence != null ? sequence : 1L;
		this.roomInfo = roomInfo;
	}

	// imageId와 imageUrl 쌍 검증
	private void validateImagePair(String imageId, String imageUrl) {
		if (imageId == null || imageId.trim().isEmpty()) {
			throw new IllegalArgumentException("Image ID cannot be null or empty");
		}
		if (imageUrl == null || imageUrl.trim().isEmpty()) {
			throw new IllegalArgumentException("Image URL cannot be null or empty");
		}
		if (imageId.length() < 3) {
			throw new IllegalArgumentException("Image ID is too short: " + imageId);
		}
		if (!isValidUrl(imageUrl)) {
			throw new IllegalArgumentException("Invalid image URL format: " + imageUrl);
		}
	}

	private boolean isValidUrl(String url) {
		return url.startsWith("http://") ||
		       url.startsWith("https://") ||
		       url.startsWith("/");
	}

	// 쌍으로 업데이트하는 메소드 추가
	public void updateImagePair(String newImageId, String newImageUrl) {
		validateImagePair(newImageId, newImageUrl);
		this.imageId = newImageId;
		this.imageUrl = newImageUrl;
	}

	// 양방향 연관관계 편의 메서드
	public void assignRoom(RoomInfo roomInfo) {
		this.roomInfo = roomInfo;
	}

	public void removeRoom() {
		this.roomInfo = null;
	}
}
