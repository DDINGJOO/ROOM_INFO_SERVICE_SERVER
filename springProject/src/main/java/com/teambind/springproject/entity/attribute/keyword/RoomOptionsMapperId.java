package com.teambind.springproject.entity.attribute.keyword;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomOptionsMapperId implements Serializable {
	
	@Column(name = "room_id")
	private Long roomId;
	
	@Column(name = "keyword_id")
	private Long keywordId;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RoomOptionsMapperId that = (RoomOptionsMapperId) o;
		return Objects.equals(roomId, that.roomId) &&
				Objects.equals(keywordId, that.keywordId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(roomId, keywordId);
	}
}
