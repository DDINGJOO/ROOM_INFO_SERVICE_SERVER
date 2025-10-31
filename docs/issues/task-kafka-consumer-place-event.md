[TASK] Place 상태 변경 카프카 Consumer 개발

## 연결된 Story/Epic
Story: Place 서비스 이벤트 연동

## 작업 범위
- Kafka Consumer 설정
- Topic: place-status-changed
- PlaceStatusEventListener 구현
- Place 비활성화/삭제 이벤트 수신
- 해당 Place의 모든 Room status를 CLOSE로 일괄 변경
- 배치 업데이트 최적화 (bulk update query)
- 트랜잭션 처리
- Dead Letter Queue 설정
- 에러 핸들링 및 로깅

## Done 기준
- [ ] 테스트 작성
  - 통합 테스트 (EmbeddedKafka)
  - 배치 업데이트 검증
  - DLQ 전송 테스트
- [ ] 문서/스키마 업데이트
- [ ] 린트/빌드/CI 통과
- [ ] PR 리뷰/머지
