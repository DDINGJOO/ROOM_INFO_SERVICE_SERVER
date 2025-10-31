package com.teambind.springproject.entity.attribute;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("CAUTION")
@Getter
@Setter
@NoArgsConstructor
public class CautionDetail extends StringAttribute {
}
