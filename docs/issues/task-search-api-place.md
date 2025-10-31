[TASK] PlaceID 검색 API 개발

## 연결된 Story/Epic
Story: Room 검색 API 구현

## 작업 범위
- GET /api/rooms?placeId={placeId} 엔드포인트 구현
- RoomRepository.findByPlaceId() 쿼리 메서드
- 페이징 지원 (Pageable)
- Fetch Join으로 N+1 문제 해결
- 응답: PagedRoomResponse

## Done 기준
- [ ] 테스트 작성
  - Repository 테스트
  - 통합 테스트
- [ ] 문서/스키마 업데이트
- [ ] 린트/빌드/CI 통과
- [ ] PR 리뷰/머지
