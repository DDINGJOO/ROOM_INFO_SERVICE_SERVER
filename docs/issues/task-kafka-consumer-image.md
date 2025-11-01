[TASK] 이미지 업데이트 카프카 Consumer 개발

## 연결된 Story/Epic

Story: 이미지 서버 카프카 이벤트 연동

## 작업 범위

- Kafka Consumer 설정 (application.yml)
- Topic: room-image-updated
- RoomImageEventListener 구현
- 이벤트 수신 시 해당 Room의 모든 이미지 삭제
- 새 이미지 목록으로 전체 교체
- 트랜잭션 처리 (@Transactional)
- Dead Letter Queue 설정 (재시도 실패 시)
- 에러 핸들링 및 로깅

## Done 기준

- [ ] 테스트 작성
	- 통합 테스트 (EmbeddedKafka 사용)
	- 이미지 교체 로직 검증
	- DLQ 전송 테스트
- [ ] 문서/스키마 업데이트
	- 카프카 이벤트 스키마 문서화
- [ ] 린트/빌드/CI 통과
- [ ] PR 리뷰/머지
