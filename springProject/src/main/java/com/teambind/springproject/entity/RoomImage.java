package com.teambind.springproject.entity;


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
	
	// 양방향 연관관계 편의 메서드
	public void assignRoom(RoomInfo roomInfo) {
		this.roomInfo = roomInfo;
	}
	
	public void removeRoom() {
		this.roomInfo = null;
	}
}
