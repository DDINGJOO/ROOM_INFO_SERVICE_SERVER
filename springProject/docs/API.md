# Room Info Service API Documentation

## Base URL
```
Development: http://localhost:8080/api
Production: https://api.teambind.com/api
```

## Authentication
현재 버전에서는 인증이 필요하지 않습니다. 추후 JWT 기반 인증이 추가될 예정입니다.

## Response Format
모든 응답은 JSON 형식으로 반환됩니다.

### Success Response
```json
{
  "data": {},
  "timestamp": "2024-11-29T10:00:00Z"
}
```

### Error Response
```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "Error description",
    "details": {}
  },
  "timestamp": "2024-11-29T10:00:00Z"
}
```

---

## Room APIs

### 1. Create Room
새로운 Room을 생성합니다.

**Endpoint**
```http
POST /api/rooms
```

**Request Headers**
```
Content-Type: application/json
```

**Request Body**
| Field | Type | Required | Description | Constraints |
|-------|------|----------|-------------|-------------|
| roomName | String | Yes | Room 이름 | Not blank |
| placeId | Long | Yes | 소속 Place ID | Not null |
| timeSlot | String | No | 시간대 | MORNING, AFTERNOON, EVENING, NIGHT |
| maxOccupancy | Integer | No | 최대 수용 인원 | Min: 1 |
| furtherDetails | List<String> | No | 추가 정보 | Max: 7 items |
| cautionDetails | List<String> | No | 주의 사항 | Max: 8 items |
| keywordIds | List<Long> | No | 키워드 ID 목록 | - |

**Example Request**
```json
{
  "roomName": "대회의실 A",
  "placeId": 100,
  "timeSlot": "MORNING",
  "maxOccupancy": 30,
  "furtherDetails": [
    "WiFi 제공",
    "프로젝터 설치",
    "화이트보드 구비"
  ],
  "cautionDetails": [
    "음식물 반입 금지",
    "예약 시간 엄수"
  ],
  "keywordIds": [1, 2, 5]
}
```

**Success Response**
- Status: 201 Created
- Body: Room ID (Long)
```
1
```

**Error Responses**
| Status | Code | Description |
|--------|------|-------------|
| 400 | INVALID_REQUEST | 유효성 검증 실패 |
| 409 | CONFLICT | 중복된 Room 이름 |

---

### 2. Get Room Details
특정 Room의 상세 정보를 조회합니다.

**Endpoint**
```http
GET /api/rooms/{roomId}
```

**Path Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| roomId | Long | Room ID |

**Success Response**
- Status: 200 OK
```json
{
  "roomId": 1,
  "roomName": "대회의실 A",
  "placeId": 100,
  "status": "ACTIVE",
  "timeSlot": "MORNING",
  "maxOccupancy": 30,
  "furtherDetails": [
    "WiFi 제공",
    "프로젝터 설치",
    "화이트보드 구비"
  ],
  "cautionDetails": [
    "음식물 반입 금지",
    "예약 시간 엄수"
  ],
  "imageUrls": [
    "https://image.url/room1.jpg"
  ],
  "keywordIds": [1, 2, 5]
}
```

**Error Responses**
| Status | Code | Description |
|--------|------|-------------|
| 404 | NOT_FOUND | Room을 찾을 수 없음 |

---

### 3. Search Rooms
다양한 조건으로 Room을 검색합니다.

**Endpoint**
```http
GET /api/rooms/search
```

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| roomName | String | No | Room 이름 (부분 일치) |
| placeId | Long | No | Place ID |
| minOccupancy | Integer | No | 최소 수용 인원 |
| keywordIds | List<Long> | No | 키워드 ID 목록 (AND 조건) |

**Example Request**
```http
GET /api/rooms/search?roomName=회의&minOccupancy=10&keywordIds=1,2
```

**Success Response**
- Status: 200 OK
```json
[
  {
    "roomId": 1,
    "roomName": "대회의실 A",
    "placeId": 100,
    "timeSlot": "MORNING",
    "maxOccupancy": 30,
    "imageUrls": ["https://image.url/room1.jpg"],
    "keywordIds": [1, 2, 5]
  },
  {
    "roomId": 2,
    "roomName": "소회의실 B",
    "placeId": 100,
    "timeSlot": "AFTERNOON",
    "maxOccupancy": 10,
    "imageUrls": [],
    "keywordIds": [1, 3]
  }
]
```

---

### 4. Get Rooms by Place
특정 Place에 속한 모든 Room을 조회합니다.

**Endpoint**
```http
GET /api/rooms/place/{placeId}
```

**Path Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| placeId | Long | Place ID |

**Success Response**
- Status: 200 OK
- Body: List of RoomSimpleResponse (위 Search API와 동일한 형식)

---

### 5. Get Multiple Rooms
여러 Room의 상세 정보를 한 번에 조회합니다.

**Endpoint**
```http
GET /api/rooms/batch
```

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| ids | List<Long> | Yes | Room ID 목록 |

**Example Request**
```http
GET /api/rooms/batch?ids=1,2,3
```

**Success Response**
- Status: 200 OK
- Body: List of RoomDetailResponse

---

### 6. Delete Room
Room을 삭제합니다.

**Endpoint**
```http
DELETE /api/rooms/{roomId}
```

**Path Parameters**
| Parameter | Type | Description |
|-----------|------|-------------|
| roomId | Long | Room ID |

**Success Response**
- Status: 200 OK
- Body: Deleted Room ID (Long)

**Error Responses**
| Status | Code | Description |
|--------|------|-------------|
| 404 | NOT_FOUND | Room을 찾을 수 없음 |
| 409 | CONFLICT | 삭제할 수 없는 상태 |

---

### 7. Get Keywords
사용 가능한 모든 키워드 목록을 조회합니다.

**Endpoint**
```http
GET /api/rooms/keywords
```

**Success Response**
- Status: 200 OK
```json
{
  "1": {
    "keywordId": 1,
    "keyword": "회의실"
  },
  "2": {
    "keywordId": 2,
    "keyword": "프로젝터"
  },
  "3": {
    "keywordId": 3,
    "keyword": "화이트보드"
  }
}
```

---

## Data Types

### TimeSlot Enum
```
MORNING   - 오전 (06:00 - 12:00)
AFTERNOON - 오후 (12:00 - 18:00)
EVENING   - 저녁 (18:00 - 22:00)
NIGHT     - 심야 (22:00 - 06:00)
```

### Status Enum
```
PENDING - 대기중
ACTIVE  - 활성
INACTIVE - 비활성
```

---

## Validation Rules

### Room Name
- 필수 입력
- 최소 1자 이상
- 공백 문자만으로 구성 불가

### Max Occupancy
- 선택 입력 (null 허용)
- 입력 시 최소 1명 이상

### Further Details
- 최대 7개까지 등록 가능
- 각 항목 최대 500자

### Caution Details
- 최대 8개까지 등록 가능
- 각 항목 최대 500자

### Room Images
- 최대 10개까지 등록 가능
- 지원 형식: JPG, PNG, GIF
- 최대 크기: 5MB per image

---

## Error Codes

| Code | Description |
|------|-------------|
| INVALID_REQUEST | 요청 데이터가 유효하지 않음 |
| NOT_FOUND | 요청한 리소스를 찾을 수 없음 |
| CONFLICT | 리소스 충돌 (중복 등) |
| INTERNAL_ERROR | 서버 내부 오류 |
| VALIDATION_FAILED | 유효성 검증 실패 |

---

## Rate Limiting
- 인증되지 않은 요청: 100 requests/minute
- 인증된 요청: 1000 requests/minute

---

## Pagination
검색 API는 향후 페이지네이션이 추가될 예정입니다.

예정된 파라미터:
- page: 페이지 번호 (0-based)
- size: 페이지 크기 (기본값: 20)
- sort: 정렬 기준 (예: roomName,asc)

---

## Webhooks
Room 생성/삭제 시 이벤트가 Kafka를 통해 발행됩니다.

### Room Created Event
```json
{
  "eventType": "ROOM_CREATED",
  "roomId": 1,
  "placeId": 100,
  "timeSlot": "MORNING",
  "timestamp": "2024-11-29T10:00:00Z"
}
```

### Room Deleted Event
```json
{
  "eventType": "ROOM_DELETED",
  "roomId": 1,
  "timestamp": "2024-11-29T10:00:00Z"
}
```

---

## Changelog

### Version 2.0.0 (2024-11-29)
- Added maxOccupancy field to Room entity
- Added minOccupancy filter to search API
- Improved test coverage

### Version 1.0.0 (2024-11-01)
- Initial release
- Basic CRUD operations for Room
- Keyword mapping functionality
- Event publishing via Kafka