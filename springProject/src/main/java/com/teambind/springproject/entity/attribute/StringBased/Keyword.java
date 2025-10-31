package com.teambind.springproject.entity.attribute.StringBased;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "keyword")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Keyword {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "keyword_id")
	private Long keywordId;
	
	@Column(name = "keyword", nullable = false, unique = true, length = 50)
	private String keyword;
}
