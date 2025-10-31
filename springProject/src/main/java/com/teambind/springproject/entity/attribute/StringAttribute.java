package com.teambind.springproject.entity.attribute;

import com.teambind.springproject.entity.RoomInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "string_attribute")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "attribute_type")
@Getter
@Setter
public abstract class StringAttribute {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "attribute_id")
	private Long attributeId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id", nullable = false)
	private RoomInfo roomInfo;
	
	@Column(name = "contents", nullable = false, length = 1000)
	private String contents;
}
