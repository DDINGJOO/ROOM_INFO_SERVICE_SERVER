[TASK] Room 삭제 API 개발

## 연결된 Story/Epic

Story: Room CRUD API 구현

## 작업 범위

- DELETE /api/rooms/{id} 엔드포인트 구현
- Soft delete 방식 (status를 CLOSE로 변경)
- 응답: 204 No Content
- 존재하지 않는 Room 삭제 시 404 예외 처리
- 연관 엔티티는 유지 (이미지, 상세정보 등)

## Done 기준

- [ ] 테스트 작성
	- 단위 테스트
	- 통합 테스트
	- Soft delete 검증
- [ ] 문서/스키마 업데이트
- [ ] 린트/빌드/CI 통과
- [ ] PR 리뷰/머지
