[TASK] ID 배열 배치 조회 API 개발

## 연결된 Story/Epic

Story: Room 검색 API 구현

## 작업 범위

- GET /api/rooms/batch?ids=1,2,3,4,5 엔드포인트 구현
- 최대 100개 ID 제한 검증
- RoomRepository.findAllById() 활용
- IN 쿼리 사용
- 순서 보장 (요청한 ID 순서대로 정렬)
- 존재하지 않는 ID는 결과에서 제외
- 응답: List<RoomResponse>

## Done 기준

- [ ] 테스트 작성
	- 통합 테스트
	- 최대 개수 제한 테스트
	- 순서 보장 테스트
- [ ] 문서/스키마 업데이트
- [ ] 린트/빌드/CI 통과
- [ ] PR 리뷰/머지
