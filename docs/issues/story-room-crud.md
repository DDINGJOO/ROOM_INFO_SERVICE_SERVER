[STORY] Room CRUD API 구현

## 배경
합주실 정보를 생성, 조회, 수정, 삭제할 수 있는 기본 API가 필요하다.
관리자는 웹 어드민을 통해 합주실 정보를 관리하고, 사용자는 앱에서 합주실 정보를 조회한다.

## 수용 기준(AC)
- [ ] Room 생성 API (POST /api/rooms)
  - placeId, roomName, status, timeSlot 필수 입력
  - 생성 시 이미지 최대 10개, FurtherDetail 최대 7개, CautionDetail 최대 8개 제한 검증
- [ ] Room 단건 조회 API (GET /api/rooms/{id})
  - 존재하지 않는 Room 조회 시 404 반환
- [ ] Room 목록 조회 API (GET /api/rooms)
  - 페이징 지원 (page, size)
- [ ] Room 수정 API (PUT /api/rooms/{id})
  - 이미지, 상세정보, 키워드 전체 교체 방식
  - 최대 개수 제한 검증
- [ ] Room 삭제 API (DELETE /api/rooms/{id})
  - Soft delete (status를 CLOSE로 변경)
- [ ] 모든 API 에러 응답 통일 (400, 404, 500)

## 디자인/계약 링크
- API 명세: docs/api/room-api-spec.md
- 엔티티 설계: springProject/src/main/java/com/teambind/springproject/entity/

## 구현 메모/리스크
- 이미지, 상세정보, 키워드는 cascade로 자동 관리되므로 별도 삭제 API 불필요
- 비즈니스 검증 로직은 엔티티 내부에 구현되어 있음
- Room 생성/삭제 시 Place 서비스 알림은 별도 Story에서 처리

## 연결된 Epic
Epic: Room 서비스 핵심 기능 구현
