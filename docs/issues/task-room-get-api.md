[TASK] Room 조회 API 개발

## 연결된 Story/Epic
Story: Room CRUD API 구현

## 작업 범위
- GET /api/rooms/{id} 엔드포인트 구현 (단건 조회)
- GET /api/rooms 엔드포인트 구현 (목록 조회)
- 응답 DTO: RoomResponse, PagedRoomResponse
- Fetch Join으로 N+1 문제 해결
- 페이징 처리 (Pageable)
- 존재하지 않는 Room 조회 시 404 예외 처리

## Done 기준
- [ ] 테스트 작성
  - 단위 테스트 (RoomServiceTest)
  - 통합 테스트 (RoomControllerTest)
  - 404 예외 테스트
- [ ] 문서/스키마 업데이트
  - API 명세서 작성
- [ ] 린트/빌드/CI 통과
- [ ] PR 리뷰/머지
