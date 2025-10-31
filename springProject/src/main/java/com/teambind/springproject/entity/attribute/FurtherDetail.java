package com.teambind.springproject.entity.attribute;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("FURTHER")
@Getter
@Setter
@NoArgsConstructor
public class FurtherDetail extends StringAttribute {
}
