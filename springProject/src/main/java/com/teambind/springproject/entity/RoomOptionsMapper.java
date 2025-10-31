package com.teambind.springproject.entity;

import com.teambind.springproject.entity.attribute.Keyword;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_options_mapper")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomOptionsMapper {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mapper_id")
	private Long mapperId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id", nullable = false)
	private RoomInfo roomInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "keyword_id", nullable = false)
	private Keyword keyword;
}
