[TASK] Room 생성/삭제 카프카 Producer 개발

## 연결된 Story/Epic
Story: Place 서비스 이벤트 연동

## 작업 범위
- Kafka Producer 설정
- Topic: room-created, room-deleted
- RoomEventPublisher 구현
- Room 생성 시 이벤트 발행 (AOP 또는 Service 레이어)
- Room 삭제 시 이벤트 발행
- 이벤트 스키마 정의 (RoomCreatedEvent, RoomDeletedEvent)
- 발행 실패 시 재시도 로직
- 로깅

## Done 기준
- [ ] 테스트 작성
  - 통합 테스트 (EmbeddedKafka)
  - 이벤트 발행 검증
- [ ] 문서/스키마 업데이트
- [ ] 린트/빌드/CI 통과
- [ ] PR 리뷰/머지
