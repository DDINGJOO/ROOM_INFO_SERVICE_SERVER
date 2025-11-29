# Room Info Service Server

Room 정보를 관리하는 MSA 기반 서비스로, DDD(Domain-Driven Design)와 CQRS 패턴을 적용한 Spring Boot 애플리케이션입니다.

## 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 3.5.7
- **Database**: PostgreSQL
- **Message Queue**: Apache Kafka
- **Query DSL**: QueryDSL 5.0.0
- **Build Tool**: Gradle 8.14.3
- **Test**: JUnit 5, Mockito, H2 (테스트용)

## 프로젝트 구조

```
springProject/
├── src/
│   ├── main/
│   │   ├── java/com/teambind/springproject/
│   │   │   ├── controller/        # REST API 컨트롤러 (CQRS 분리)
│   │   │   │   ├── RoomCommandController.java
│   │   │   │   └── RoomQueryController.java
│   │   │   ├── service/          # 비즈니스 로직 (CQRS 분리)
│   │   │   │   ├── RoomCommandService.java
│   │   │   │   └── RoomQueryService.java
│   │   │   ├── repository/       # 데이터 접근 계층
│   │   │   │   ├── RoomCommandRepository.java
│   │   │   │   ├── RoomQueryRepository.java
│   │   │   │   └── RoomQueryRepositoryImpl.java
│   │   │   ├── entity/           # JPA 엔티티 (도메인 모델)
│   │   │   │   └── RoomInfo.java
│   │   │   ├── dto/              # 데이터 전송 객체
│   │   │   │   ├── command/      # 명령 DTO
│   │   │   │   ├── query/        # 조회 DTO
│   │   │   │   ├── request/      # 요청 DTO
│   │   │   │   ├── response/     # 응답 DTO
│   │   │   │   └── projection/   # 프로젝션 인터페이스
│   │   │   ├── event/            # 이벤트 처리
│   │   │   │   ├── events/       # 도메인 이벤트
│   │   │   │   └── publisher/    # 이벤트 발행
│   │   │   ├── mapper/           # DTO-Entity 매핑
│   │   │   └── util/             # 유틸리티 클래스
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── application-dev.yaml
│   │       ├── application-prod.yaml
│   │       └── db/migration/      # DB 마이그레이션 스크립트
│   └── test/                     # 테스트 코드
│       ├── java/
│       └── resources/
└── build.gradle
```

## 주요 기능

### 1. Room 관리
- **생성**: 새로운 Room 정보 등록
- **조회**: 다양한 조건으로 Room 검색
- **삭제**: Room 정보 삭제

### 2. Room 속성
- 기본 정보: roomName, placeId, status, timeSlot
- **최대 수용 인원 (maxOccupancy)**: Room이 수용할 수 있는 최대 인원
- 추가 정보: furtherDetails (최대 7개)
- 주의 사항: cautionDetails (최대 8개)
- 이미지: roomImages (최대 10개)
- 키워드: keywords (다대다 관계)

### 3. 검색 기능
- Room 이름으로 검색
- Place ID로 검색
- 키워드로 검색
- **최소 수용 인원으로 검색** (minOccupancy)
- 복합 조건 검색 지원

## API 엔드포인트

### Command API (쓰기 작업)

#### Room 생성
```http
POST /api/rooms
Content-Type: application/json

{
  "roomName": "Conference Room A",
  "placeId": 100,
  "timeSlot": "MORNING",
  "maxOccupancy": 20,
  "furtherDetails": ["WiFi 제공", "프로젝터 설치"],
  "cautionDetails": ["음식물 반입 금지"],
  "keywordIds": [1, 2, 3]
}
```

#### Room 삭제
```http
DELETE /api/rooms/{roomId}
```

### Query API (읽기 작업)

#### Room 상세 조회
```http
GET /api/rooms/{roomId}
```

#### Room 검색
```http
GET /api/rooms/search?roomName={name}&placeId={id}&minOccupancy={min}&keywordIds={ids}
```
- `minOccupancy`: 최소 수용 인원 (optional)
- 모든 파라미터는 선택적이며, 복합 조건 검색 가능

#### Place별 Room 조회
```http
GET /api/rooms/place/{placeId}
```

#### 다중 Room 조회
```http
GET /api/rooms/batch?ids=1,2,3
```

#### 키워드 목록 조회
```http
GET /api/rooms/keywords
```

## 아키텍처 특징

### 1. DDD (Domain-Driven Design)
- Rich Domain Model: 비즈니스 로직을 엔티티 내부에 캡슐화
- Value Objects: FurtherDetail, CautionDetail 등
- Aggregate Root: RoomInfo가 관련 엔티티들의 일관성 보장
- Domain Events: RoomCreatedEvent, RoomDeletedEvent

### 2. CQRS (Command Query Responsibility Segregation)
- Command와 Query 분리로 읽기/쓰기 최적화
- 각각 독립적인 Controller, Service, Repository

### 3. Event-Driven Architecture
- Kafka를 통한 비동기 이벤트 처리
- 서비스 간 느슨한 결합

### 4. Clean Architecture
- 계층 간 명확한 책임 분리
- 의존성 역전 원칙 적용
- 테스트 가능한 구조

## 빌드 및 실행

### 요구사항
- JDK 17 이상
- PostgreSQL 12 이상
- Apache Kafka 2.8 이상

### 빌드
```bash
./gradlew clean build
```

### 테스트
```bash
./gradlew test
```

### 실행
```bash
# 개발 환경
./gradlew bootRun --args='--spring.profiles.active=dev'

# 운영 환경
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## 테스트

### 테스트 구조
- **Unit Tests**: 각 계층별 단위 테스트
- **Integration Tests**: Repository 통합 테스트
- **Service Tests**: 비즈니스 로직 테스트
- **Controller Tests**: API 엔드포인트 테스트

### 테스트 실행
```bash
# 전체 테스트
./gradlew test

# 특정 테스트 실행
./gradlew test --tests "RoomInfoMaxOccupancyTest"

# 테스트 커버리지 확인
./gradlew test jacocoTestReport
```

### BDD 스타일 테스트
Given-When-Then 패턴으로 작성된 가독성 높은 테스트:
```java
@Test
@DisplayName("Given valid maxOccupancy When creating room Then should save successfully")
void shouldCreateRoomWithMaxOccupancy() {
    // Given
    RoomCreateCommand command = ...

    // When
    Long roomId = roomCommandService.createRoom(command);

    // Then
    assertThat(roomId).isNotNull();
}
```

## 데이터베이스

### 마이그레이션
SQL 스크립트 위치: `src/main/resources/db/migration/`

최근 추가된 마이그레이션:
- `V2__add_max_occupancy_to_room_info.sql`: maxOccupancy 컬럼 추가

### 주요 테이블
- `room_info`: Room 기본 정보
- `room_image`: Room 이미지
- `further_detail`: 추가 정보
- `caution_detail`: 주의 사항
- `room_options_mapper`: Room-Keyword 매핑
- `keyword`: 키워드 마스터

## 환경 설정

### application.yaml
```yaml
spring:
  profiles:
    active: dev
```

### application-dev.yaml
개발 환경 설정 (PostgreSQL, Kafka)

### application-prod.yaml
운영 환경 설정

### application-test.yaml
테스트 환경 설정 (H2 인메모리 DB)

## 기여 가이드

### 코드 스타일
- Google Java Style Guide 준수
- SOLID 원칙 적용
- DDD 패턴 준수

### 커밋 메시지 형식
```
[TYPE] 간단한 설명

상세 설명
- 변경 사항 1
- 변경 사항 2

Closes #이슈번호
```

Types: FEAT, FIX, REFACTOR, TEST, DOCS, CHORE, PERF

## 라이선스
Private Repository

## 연락처
- Repository: https://github.com/DDINGJOO/ROOM_INFO_SERVICE_SERVER
- Issues: https://github.com/DDINGJOO/ROOM_INFO_SERVICE_SERVER/issues