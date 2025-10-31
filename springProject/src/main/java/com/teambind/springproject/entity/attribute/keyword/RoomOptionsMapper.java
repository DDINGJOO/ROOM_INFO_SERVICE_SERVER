package com.teambind.springproject.entity.attribute.keyword;

import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.attribute.StringBased.Keyword;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_options_mapper")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class RoomOptionsMapper {
	
	@EmbeddedId
	private RoomOptionsMapperId id;
	
	@Setter(AccessLevel.PROTECTED)
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("roomId")
	@JoinColumn(name = "room_id", nullable = false)
	private RoomInfo roomInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("keywordId")
	@JoinColumn(name = "keyword_id", nullable = false)
	private Keyword keyword;
	
	// 팩토리 메서드 - 복합키 자동 생성
	public static RoomOptionsMapper create(RoomInfo roomInfo, Keyword keyword) {
		RoomOptionsMapperId id = RoomOptionsMapperId.builder()
				.roomId(roomInfo.getRoomId())
				.keywordId(keyword.getKeywordId())
				.build();
		
		return RoomOptionsMapper.builder()
				.id(id)
				.roomInfo(roomInfo)
				.keyword(keyword)
				.build();
	}
	
	// 양방향 연관관계 편의 메서드
	public void assignRoom(RoomInfo roomInfo) {
		this.roomInfo = roomInfo;
	}
	
	public void removeRoom() {
		this.roomInfo = null;
	}
}
