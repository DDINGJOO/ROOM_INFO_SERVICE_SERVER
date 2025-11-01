[TASK] 이름 검색 API 개발

## 연결된 Story/Epic

Story: Room 검색 API 구현

## 작업 범위

- GET /api/rooms?name={name} 엔드포인트 구현
- 부분 일치 검색 (LIKE %name%)
- RoomRepository.findByRoomNameContaining() 쿼리 메서드
- roomName 컬럼에 인덱스 추가 (migration script)
- 페이징 지원
- 응답: PagedRoomResponse

## Done 기준

- [ ] 테스트 작성
	- Repository 테스트
	- 통합 테스트
	- 부분 일치 검색 테스트
- [ ] 문서/스키마 업데이트
	- 인덱스 추가 스크립트 작성
- [ ] 린트/빌드/CI 통과
- [ ] PR 리뷰/머지
