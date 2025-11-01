[STORY] Place 서비스 이벤트 연동

## 배경

Room은 Place에 속해있으므로 두 서비스 간 데이터 동기화가 필요하다.
Room 생성/삭제 시 Place 서비스에 알려야 하고, Place가 비활성화되면 Room도 함께 비활성화되어야 한다.

## 수용 기준(AC)

- [ ] Room 생성 시 Place 서비스에 알림
	- 카프카 이벤트 발행 (topic: room-created)
	- 또는 동기 HTTP 호출 (POST /api/places/{placeId}/rooms)
- [ ] Room 삭제 시 Place 서비스에 알림
	- 카프카 이벤트 발행 (topic: room-deleted)
	- 또는 동기 HTTP 호출 (DELETE /api/places/{placeId}/rooms/{roomId})
- [ ] Place 비활성화/삭제 이벤트 수신 (topic: place-status-changed)
	- 해당 Place의 모든 Room status를 CLOSE로 변경
	- 배치 업데이트 처리
- [ ] 통신 실패 시 재시도 로직 (최대 3회)
- [ ] 재시도 실패 시 Dead Letter Queue로 전송

## 디자인/계약 링크

- Room 생성/삭제 이벤트 스키마:

```json
{
  "eventType": "ROOM_CREATED" | "ROOM_DELETED",
  "roomId": 123,
  "placeId": 456,
  "timestamp": "2025-10-31T10:00:00Z"
}
```

- Place 상태 변경 이벤트 스키마:

```json
{
  "placeId": 456,
  "status": "INACTIVE" | "DELETED",
  "timestamp": "2025-10-31T10:00:00Z"
}
```

## 구현 메모/리스크

- 카프카 vs HTTP: 비동기 처리가 가능한 경우 카프카 사용 권장
- Place 서비스 장애 시에도 Room은 생성되어야 함 (Eventually Consistent)
- Place 상태 변경 시 대량의 Room이 영향받을 수 있으므로 배치 처리 최적화 필요

## 연결된 Epic

Epic: Room 서비스 핵심 기능 구현
