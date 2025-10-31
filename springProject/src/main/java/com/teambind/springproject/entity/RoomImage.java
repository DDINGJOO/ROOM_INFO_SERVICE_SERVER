package com.teambind.springproject.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomImage {

	@Id
	@Column(name = "image_id", length = 100)
	private String imageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id", nullable = false)
	private RoomInfo roomInfo;

	@Column(name = "image_url", nullable = false, length = 500)
	private String imageUrl;
}
