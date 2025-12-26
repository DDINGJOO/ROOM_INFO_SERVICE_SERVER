package com.teambind.springproject.entity.attribute;

import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.enums.FieldType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservation_field")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReservationField {

    public static final int MAX_TITLE_LENGTH = 100;
    public static final int DEFAULT_MAX_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_id")
    private Long fieldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomInfo roomInfo;

    @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "input_type", nullable = false)
    private FieldType inputType;

    @Column(name = "required", nullable = false)
    @Builder.Default
    private Boolean required = false;

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    public static ReservationField create(String title, FieldType inputType,
                                          Boolean required, Integer maxLength, Integer sequence) {
        validateTitle(title);
        validateSequence(sequence);

        return ReservationField.builder()
                .title(title.trim())
                .inputType(inputType != null ? inputType : FieldType.TEXT)
                .required(required != null ? required : false)
                .maxLength(maxLength != null ? maxLength : DEFAULT_MAX_LENGTH)
                .sequence(sequence)
                .build();
    }

    private static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("필드 타이틀은 필수입니다.");
        }
        if (title.trim().length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("필드 타이틀은 최대 %d자까지 가능합니다.", MAX_TITLE_LENGTH));
        }
    }

    private static void validateSequence(Integer sequence) {
        if (sequence == null || sequence < 1) {
            throw new IllegalArgumentException("순서는 1 이상이어야 합니다.");
        }
    }

    public void assignRoom(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;
    }

    public void removeRoom() {
        this.roomInfo = null;
    }

    public void updateSequence(Integer sequence) {
        validateSequence(sequence);
        this.sequence = sequence;
    }
}