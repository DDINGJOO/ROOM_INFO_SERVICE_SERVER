package com.teambind.springproject.entity.attribute;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("FURTHER")
@Getter
@NoArgsConstructor
public class FurtherDetail extends StringAttribute {
	
	private static final int MAX_FURTHER_DETAILS = 7;
	
	public FurtherDetail(String contents) {
		super(contents);
	}
	
	@Override
	public int getMaxLimit() {
		return MAX_FURTHER_DETAILS;
	}
}
