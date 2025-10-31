package com.teambind.springproject.entity;

import com.teambind.springproject.entity.attribute.CautionDetail;
import com.teambind.springproject.entity.attribute.FurtherDetail;
import com.teambind.springproject.entity.attribute.StringAttribute;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.entity.enums.TimeSlot;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_id")
	private Long roomId;
	
	@Column(name = "room_name", nullable = false)
	private String roomName;
	
	// 해당 룸을 관리하는 공간 ID
	@Column(name = "place_id", nullable = false)
	private Long placeId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "time_slot")
	private TimeSlot timeSlot;
	
	@OneToMany(mappedBy = "roomInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<FurtherDetail> furtherDetails = new ArrayList<>();
	
	@OneToMany(mappedBy = "roomInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CautionDetail> cautionDetails = new ArrayList<>();
	
	@OneToMany(mappedBy = "roomInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<RoomImage> roomImages = new ArrayList<>();
	
	@OneToMany(mappedBy = "roomInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<RoomOptionsMapper> roomOptions = new ArrayList<>();
	
	
	// 연관관계 편의 메서드
	public void addStringAttribute(StringAttribute stringAttribute) {
		stringAttribute.setRoomInfo(this);
		if (stringAttribute instanceof FurtherDetail) {
			if (furtherDetails == null) {
				furtherDetails = new ArrayList<>();
			}
			furtherDetails.add((FurtherDetail) stringAttribute);
		} else if (stringAttribute instanceof CautionDetail) {
			if (cautionDetails == null) {
				cautionDetails = new ArrayList<>();
			}
			cautionDetails.add((CautionDetail) stringAttribute);
		}
	}
	
	public void removeStringAttribute(StringAttribute stringAttribute) {
		if (stringAttribute instanceof FurtherDetail) {
			furtherDetails.remove(stringAttribute);
		} else if (stringAttribute instanceof CautionDetail) {
			cautionDetails.remove(stringAttribute);
		}
	}
	
	public void addRoomImage(RoomImage roomImage) {
		roomImage.setRoomInfo(this);
		if (roomImages == null) {
			roomImages = new ArrayList<>();
		}
		roomImages.add(roomImage);
	}
	
	public void removeRoomImage(RoomImage roomImage) {
		roomImages.remove(roomImage);
		roomImage.setRoomInfo(null);
	}
	
	public void addRoomOption(RoomOptionsMapper mapper) {
		mapper.setRoomInfo(this);
		if (roomOptions == null) {
			roomOptions = new ArrayList<>();
		}
		roomOptions.add(mapper);
	}
	
	public void removeRoomOption(RoomOptionsMapper mapper) {
		roomOptions.remove(mapper);
		mapper.setRoomInfo(null);
	}
}
