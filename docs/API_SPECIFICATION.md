# Room Info Service API 명세서

## 기본 정보

- Base URL: `/api/rooms`
- Content-Type: `application/json`

---

## 1. Room Command APIs

### 1.1 방 생성

**POST** `/api/rooms`

#### Request

```json
{
  "roomName": "string",          // 필수, 방 이름
  "placeId": 0,                  // 필수, Place ID
  "timeSlot": "HOUR",            // 선택, 시간 단위 (HOUR | HALFHOUR)
  "furtherDetails": [            // 선택, 최대 7개
    "string"
  ],
  "cautionDetails": [            // 선택, 최대 8개
    "string"
  ],
  "keywordIds": [                // 선택, 키워드 ID 목록
    0
  ]
}
```

#### Request 필드 설명

| 필드             | 타입             | 필수 | 제약사항     | 설명                               |
|----------------|----------------|----|----------|----------------------------------|
| roomName       | String         | O  | NotBlank | 방 이름                             |
| placeId        | Long           | O  | NotNull  | Place ID                         |
| timeSlot       | TimeSlot       | X  | -        | 시간 단위 (HOUR: 1시간, HALFHOUR: 30분) |
| furtherDetails | List\<String\> | X  | 최대 7개    | 추가 정보 목록                         |
| cautionDetails | List\<String\> | X  | 최대 8개    | 주의 사항 목록                         |
| keywordIds     | List\<Long\>   | X  | -        | 키워드 ID 목록                        |

#### Response

**Status**: `201 Created`

```json
0  // 생성된 방의 ID
```

---

### 1.2 방 삭제

**DELETE** `/api/rooms/{roomId}`

#### Path Parameters

| 파라미터   | 타입   | 필수 | 설명        |
|--------|------|----|-----------|
| roomId | Long | O  | 삭제할 방의 ID |

#### Response

**Status**: `200 OK`

```json
0  // 삭제된 방의 ID
```

---

## 2. Room Query APIs

### 2.1 방 상세 조회

**GET** `/api/rooms/{roomId}`

#### Path Parameters

| 파라미터   | 타입   | 필수 | 설명        |
|--------|------|----|-----------|
| roomId | Long | O  | 조회할 방의 ID |

#### Response

**Status**: `200 OK`

```json
{
  "roomId": 0,
  "roomName": "string",
  "placeId": 0,
  "status": "OPEN",              // OPEN | CLOSE | PENDING
  "timeSlot": "HOUR",            // HOUR | HALFHOUR
  "furtherDetails": [
    "string"
  ],
  "cautionDetails": [
    "string"
  ],
  "imageUrls": [
    "string"
  ],
  "keywordIds": [
    0
  ]
}
```

#### Response 필드 설명

| 필드             | 타입             | 설명                                        |
|----------------|----------------|-------------------------------------------|
| roomId         | Long           | 방 ID                                      |
| roomName       | String         | 방 이름                                      |
| placeId        | Long           | Place ID                                  |
| status         | Status         | 방 상태 (OPEN: 운영중, CLOSE: 종료, PENDING: 대기중) |
| timeSlot       | TimeSlot       | 시간 단위                                     |
| furtherDetails | List\<String\> | 추가 정보 목록                                  |
| cautionDetails | List\<String\> | 주의 사항 목록                                  |
| imageUrls      | List\<String\> | 이미지 URL 목록                                |
| keywordIds     | List\<Long\>   | 키워드 ID 목록                                 |

---

### 2.2 방 검색

**GET** `/api/rooms/search`

#### Query Parameters

| 파라미터       | 타입           | 필수 | 설명                |
|------------|--------------|----|-------------------|
| roomName   | String       | X  | 방 이름 (부분 검색)      |
| keywordIds | List\<Long\> | X  | 키워드 ID 목록 (OR 조건) |
| placeId    | Long         | X  | Place ID          |

#### Query Parameter 예시

```
GET /api/rooms/search?roomName=회의실&keywordIds=1,2,3&placeId=10
```

#### Response

**Status**: `200 OK`

```json
[
  {
    "roomId": 0,
    "roomName": "string",
    "placeId": 0,
    "timeSlot": "HOUR",
    "imageUrls": [
      "string"
    ],
    "keywordIds": [
      0
    ]
  }
]
```

---

### 2.3 Place별 방 목록 조회

**GET** `/api/rooms/place/{placeId}`

#### Path Parameters

| 파라미터    | 타입   | 필수 | 설명       |
|---------|------|----|----------|
| placeId | Long | O  | Place ID |

#### Response

**Status**: `200 OK`

```json
[
  {
    "roomId": 0,
    "roomName": "string",
    "placeId": 0,
    "timeSlot": "HOUR",
    "imageUrls": [
      "string"
    ],
    "keywordIds": [
      0
    ]
  }
]
```

---

### 2.4 방 일괄 조회 (Batch)

**GET** `/api/rooms/batch`

#### Query Parameters

| 파라미터 | 타입           | 필수 | 설명          |
|------|--------------|----|-------------|
| ids  | List\<Long\> | O  | 조회할 방 ID 목록 |

#### Query Parameter 예시

```
GET /api/rooms/batch?ids=1,2,3,4,5
```

#### Response

**Status**: `200 OK`

```json
[
  {
    "roomId": 0,
    "roomName": "string",
    "placeId": 0,
    "status": "OPEN",
    "timeSlot": "HOUR",
    "furtherDetails": [
      "string"
    ],
    "cautionDetails": [
      "string"
    ],
    "imageUrls": [
      "string"
    ],
    "keywordIds": [
      0
    ]
  }
]
```

---

### 2.5 키워드 맵 조회

**GET** `/api/rooms/keywords`

전체 키워드 ID-이름 매핑 정보를 조회합니다.

#### Response

**Status**: `200 OK`

```json
{
  "1": {
    "keywordId": 1,
    "keyword": "조용한"
  },
  "2": {
    "keywordId": 2,
    "keyword": "넓은"
  }
}
```

#### Response 필드 설명

- 응답은 `Map<Long, KeywordResponse>` 형태로 반환됩니다.
- 키(Key): 키워드 ID (Long 타입이지만 JSON에서는 문자열로 표현)
- 값(Value): KeywordResponse 객체
	- `keywordId`: 키워드 ID
	- `keyword`: 키워드 이름

---

## 3. Enum 타입 정의

### 3.1 Status (방 상태)

| 값       | 설명  |
|---------|-----|
| OPEN    | 운영중 |
| CLOSE   | 종료  |
| PENDING | 대기중 |

### 3.2 TimeSlot (시간 단위)

| 값        | 설명     |
|----------|--------|
| HOUR     | 1시간 단위 |
| HALFHOUR | 30분 단위 |

---

## 4. 에러 응답

### 4.1 Validation Error (400 Bad Request)

```json
{
  "timestamp": "2025-01-18T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "방 이름은 필수입니다.",
  "path": "/api/rooms"
}
```

### 4.2 Not Found (404)

```json
{
  "timestamp": "2025-01-18T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Room not found",
  "path": "/api/rooms/999"
}
```

### 4.3 Internal Server Error (500)

```json
{
  "timestamp": "2025-01-18T12:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/rooms"
}
```

---

## 5. 참고사항

### 5.1 컨트롤러 파일 위치

- Command API: `springProject/src/main/java/com/teambind/springproject/controller/RoomCommandController.java:14`
- Query API: `springProject/src/main/java/com/teambind/springproject/controller/RoomQueryController.java:15`

### 5.2 DTO 파일 위치

- Request DTO: `springProject/src/main/java/com/teambind/springproject/dto/request/RoomCreateRequest.java:18`
- Response DTOs:
	- `springProject/src/main/java/com/teambind/springproject/dto/response/RoomDetailResponse.java:16`
	- `springProject/src/main/java/com/teambind/springproject/dto/response/RoomSimpleResponse.java:15`
	- `springProject/src/main/java/com/teambind/springproject/dto/response/KeywordResponse.java:12`
