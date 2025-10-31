package com.teambind.springproject.entity;

import com.teambind.springproject.entity.attribute.StringBased.CautionDetail;
import com.teambind.springproject.entity.attribute.StringBased.FurtherDetail;
import com.teambind.springproject.entity.attribute.StringBased.Keyword;
import com.teambind.springproject.entity.attribute.StringBased.StringAttribute;
import com.teambind.springproject.entity.attribute.image.RoomImage;
import com.teambind.springproject.entity.attribute.keyword.RoomOptionsMapper;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.entity.enums.TimeSlot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "room_info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInfo {
	
	private static final int MAX_ROOM_IMAGES = 10;

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
	
	// 컬렉션 캡슐화 - 불변 컬렉션 반환
	public List<FurtherDetail> getFurtherDetails() {
		return Collections.unmodifiableList(furtherDetails);
	}
	
	public List<CautionDetail> getCautionDetails() {
		return Collections.unmodifiableList(cautionDetails);
	}
	
	public List<RoomImage> getRoomImages() {
		return Collections.unmodifiableList(roomImages);
	}
	
	public List<RoomOptionsMapper> getRoomOptions() {
		return Collections.unmodifiableList(roomOptions);
	}
	
	
	// ===== FurtherDetail 관련 메서드 =====
	public void addFurtherDetail(FurtherDetail detail) {
		validateCanAddDetail(detail, furtherDetails.size());
		detail.assignRoom(this);
		furtherDetails.add(detail);
	}
	
	public void removeFurtherDetail(FurtherDetail detail) {
		furtherDetails.remove(detail);
		detail.removeRoom();
	}
	
	// ===== CautionDetail 관련 메서드 =====
	public void addCautionDetail(CautionDetail detail) {
		validateCanAddDetail(detail, cautionDetails.size());
		detail.assignRoom(this);
		cautionDetails.add(detail);
	}
	
	public void removeCautionDetail(CautionDetail detail) {
		cautionDetails.remove(detail);
		detail.removeRoom();
	}
	
	// ===== RoomImage 관련 메서드 =====
	public void addRoomImage(RoomImage roomImage) {
		if (roomImages.size() >= MAX_ROOM_IMAGES) {
			throw new IllegalStateException(
					String.format("방 이미지는 최대 %d개까지 추가할 수 있습니다.", MAX_ROOM_IMAGES)
			);
		}
		roomImage.assignRoom(this);
		roomImages.add(roomImage);
	}
	
	public void removeRoomImage(RoomImage roomImage) {
		roomImages.remove(roomImage);
		roomImage.removeRoom();
	}
	
	// ===== Keyword 관련 메서드 =====
	public void addKeyword(Keyword keyword) {
		if (hasKeyword(keyword)) {
			throw new IllegalStateException("이미 등록된 키워드입니다.");
		}
		
		RoomOptionsMapper mapper = RoomOptionsMapper.create(this, keyword);
		mapper.assignRoom(this);
		roomOptions.add(mapper);
	}
	
	public void removeKeyword(Keyword keyword) {
		roomOptions.removeIf(mapper -> {
			boolean matches = mapper.getKeyword().getKeywordId().equals(keyword.getKeywordId());
			if (matches) {
				mapper.removeRoom();
			}
			return matches;
		});
	}
	
	// ===== 비즈니스 로직 (검증) =====
	private void validateCanAddDetail(StringAttribute detail, int currentSize) {
		int maxLimit = detail.getMaxLimit();
		if (currentSize >= maxLimit) {
			String detailType = detail instanceof FurtherDetail ? "추가 정보" : "주의 사항";
			throw new IllegalStateException(
					String.format("%s는 최대 %d개까지 추가할 수 있습니다.", detailType, maxLimit)
			);
		}
	}
	
	private boolean hasKeyword(Keyword keyword) {
		return roomOptions.stream()
				.anyMatch(mapper -> mapper.getKeyword().getKeywordId().equals(keyword.getKeywordId()));
	}
	
	public int getRoomImageCount() {
		return roomImages.size();
	}
	
	public int getFurtherDetailCount() {
		return furtherDetails.size();
	}
	
	public int getCautionDetailCount() {
		return cautionDetails.size();
	}
	
	public int getKeywordCount() {
		return roomOptions.size();
	}
}
