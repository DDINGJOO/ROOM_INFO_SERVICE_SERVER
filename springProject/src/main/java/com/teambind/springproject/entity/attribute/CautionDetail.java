package com.teambind.springproject.entity.attribute;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CAUTION")
@Getter
@NoArgsConstructor
public class CautionDetail extends StringAttribute {
	
	private static final int MAX_CAUTION_DETAILS = 8;
	
	public CautionDetail(String contents) {
		super(contents);
	}
	
	@Override
	public int getMaxLimit() {
		return MAX_CAUTION_DETAILS;
	}
}
