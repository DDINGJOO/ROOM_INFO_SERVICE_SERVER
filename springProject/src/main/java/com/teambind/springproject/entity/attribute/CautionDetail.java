package com.teambind.springproject.entity.attribute;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CAUTION")
@Getter
@Setter
@NoArgsConstructor
public class CautionDetail extends StringAttribute {
}
