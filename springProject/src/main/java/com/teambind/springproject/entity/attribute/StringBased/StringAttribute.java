package com.teambind.springproject.entity.attribute.StringBased;

import com.teambind.springproject.entity.RoomInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "string_attribute")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "attribute_type")
@Getter
public abstract class StringAttribute {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "attribute_id")
	private Long attributeId;
	
	@Setter(AccessLevel.PROTECTED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id", nullable = false)
	private RoomInfo roomInfo;
	
	@Column(name = "contents", nullable = false, length = 1000)
	private String contents;
	
	protected StringAttribute() {
	}
	
	protected StringAttribute(String contents) {
		this.contents = contents;
	}
	
	// 양방향 연관관계 편의 메서드 (패키지가 다르므로 public)
	public void assignRoom(RoomInfo roomInfo) {
		this.roomInfo = roomInfo;
	}
	
	public void removeRoom() {
		this.roomInfo = null;
	}
	
	// 서브클래스에서 구현할 최대 개수 반환
	public abstract int getMaxLimit();
}
