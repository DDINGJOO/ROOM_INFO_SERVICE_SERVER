package com.teambind.springproject.dto.response;

import com.teambind.springproject.entity.attribute.ReservationField;
import com.teambind.springproject.entity.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationFieldResponse {

    private Long fieldId;
    private String title;
    private FieldType inputType;
    private Boolean required;
    private Integer maxLength;
    private Integer sequence;

    public static ReservationFieldResponse from(ReservationField entity) {
        return ReservationFieldResponse.builder()
                .fieldId(entity.getFieldId())
                .title(entity.getTitle())
                .inputType(entity.getInputType())
                .required(entity.getRequired())
                .maxLength(entity.getMaxLength())
                .sequence(entity.getSequence())
                .build();
    }
}
