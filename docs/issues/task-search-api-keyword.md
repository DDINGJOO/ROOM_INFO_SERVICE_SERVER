[TASK] Keyword 검색 API 개발

## 연결된 Story/Epic

Story: Room 검색 API 구현

## 작업 범위

- GET /api/rooms?keyword={keyword} 엔드포인트 구현
- 여러 키워드 OR 조건 지원 (keyword=WiFi&keyword=주차)
- RoomRepository 커스텀 쿼리 구현
- Fetch Join으로 roomOptions 및 keyword 조회
- 페이징 지원
- 응답: PagedRoomResponse

## Done 기준

- [ ] 테스트 작성
	- Repository 테스트
	- 통합 테스트
	- 여러 키워드 OR 조건 테스트
- [ ] 문서/스키마 업데이트
- [ ] 린트/빌드/CI 통과
- [ ] PR 리뷰/머지
