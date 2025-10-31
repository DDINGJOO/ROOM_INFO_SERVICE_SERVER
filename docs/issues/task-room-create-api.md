[TASK] Room 생성 API 개발

## 연결된 Story/Epic
Story: Room CRUD API 구현

## 작업 범위
- POST /api/rooms 엔드포인트 구현
- 요청 DTO: CreateRoomRequest
  - placeId, roomName, status, timeSlot (필수)
  - furtherDetails (최대 7개), cautionDetails (최대 8개)
  - roomImages (최대 10개), keywords
- 응답: 201 Created, RoomResponse
- RoomService.createRoom() 비즈니스 로직 구현
- 엔티티 편의 메서드를 활용한 연관관계 설정
- 입력 검증 (Bean Validation)
- 예외 처리 및 에러 응답

## Done 기준
- [ ] 테스트 작성
  - 단위 테스트 (RoomServiceTest)
  - 통합 테스트 (RoomControllerTest)
  - 최대 개수 제한 검증 테스트
- [ ] 문서/스키마 업데이트
  - API 명세서 작성
  - Swagger 어노테이션 추가
- [ ] 린트/빌드/CI 통과
- [ ] PR 리뷰/머지
