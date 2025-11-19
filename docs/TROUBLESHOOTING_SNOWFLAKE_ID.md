# Snowflake ID Kafka 이벤트 전송 정밀도 손실 트러블슈팅

## 문제 상황

### 증상

- Snowflake ID로 생성된 Room ID: `249330686647795700`
- Kafka 이벤트로 발행된 ID: `249330686647795712`
- **12만큼의 차이 발생**

### 원인 분석

#### 1. Snowflake ID 크기

```
Snowflake ID: 249330686647795700
JavaScript MAX_SAFE_INTEGER: 9007199254740991 (2^53 - 1)
```

Snowflake ID가 JavaScript의 안전한 정수 범위(`Number.MAX_SAFE_INTEGER`)를 초과하여 정밀도 손실 발생

#### 2. JSON 직렬화 과정

```
Java Long (64-bit) → JSON Number → Kafka → Consumer (다른 언어/플랫폼)
```

- Jackson ObjectMapper가 Long을 JSON Number로 직렬화
- JSON 파싱 시 JavaScript나 다른 언어에서 53비트 이상의 정밀도를 보장하지 못함
- 결과적으로 하위 비트가 손실되어 다른 값으로 변환

#### 3. 정밀도 손실 예시

```java
Long original = 249330686647795700L;
// JSON 직렬화 → 역직렬화 (JavaScript 기반 환경)
Long parsed = 249330686647795712L;
// 12만큼 차이 발생!
```

## 해결 방법

### 선택한 방안: 이벤트 객체에서 Long → String 변환 (캡슐화)

#### 장점

1. **안전성**: Jackson 전역 설정 변경 없이 이벤트만 독립적으로 수정
2. **명확성**: 변환 책임을 이벤트 객체가 가짐 (객체지향 원칙)
3. **호환성**: JSON Number의 플랫폼 간 호환성 문제 해결
4. **영향 범위 최소화**: 다른 API 응답이나 내부 로직에 영향 없음

#### 구현

**Before:**

```java
public class RoomCreatedEvent extends Event {
	private Long roomId;
	private Long placeId;
	
	public RoomCreatedEvent(Long roomId, Long placeId, TimeSlot timeSlot) {
		super("room-created");
		this.roomId = roomId;
		this.placeId = placeId;
		this.timeSlot = timeSlot;
	}
}
```

**After:**

```java
public class RoomCreatedEvent extends Event {
	private String roomId;  // Long → String
	private String placeId; // Long → String
	
	/**
	 * Long ID를 받아 내부적으로 String으로 변환하여 저장
	 * JavaScript Number 정밀도 손실 방지 (MAX_SAFE_INTEGER: 2^53-1)
	 */
	public RoomCreatedEvent(Long roomId, Long placeId, TimeSlot timeSlot) {
		super("room-created");
		this.roomId = String.valueOf(roomId);  // 변환
		this.placeId = String.valueOf(placeId); // 변환
		this.timeSlot = timeSlot;
	}
}
```

#### Producer (발행)

```java
// RoomCommandService.java - 변경 없음!
roomCommandRepository.save(roomInfo);
eventPublisher.

publish(
    new RoomCreatedEvent(roomInfo.getRoomId(),roomInfo.

getPlaceId(),roomInfo.

getTimeSlot())
		);
```

#### Consumer (구독)

```java
// 다른 서비스에서 수신 시
RoomCreatedEvent event = jsonUtil.fromJson(message, RoomCreatedEvent.class);
Long roomId = Long.parseLong(event.getRoomId());
Long placeId = Long.parseLong(event.getPlaceId());
```

### 검토했지만 채택하지 않은 방안들

#### 1. Jackson 전역 설정 변경

```java
// JsonUtilWithObjectMapper
SimpleModule module = new SimpleModule();
module .addSerializer(Long.class,ToStringSerializer.instance);
		objectMapper.registerModule(module);
```

**문제점**: 모든 Long이 String으로 변환되어 REST API 응답 등에도 영향

#### 2. Snowflake ID 비트 축소 (53비트 이하)

```java
// Timestamp: 35 bits (초 단위)
// Node ID: 8 bits
// Sequence: 10 bits
```

**문제점**:

- 초 단위로 변경 시 처리량이 4000배 감소 (4096/ms → 1024/s)
- 동시 요청 처리 불가능

#### 3. @JsonSerialize 어노테이션

```java

@JsonSerialize(using = ToStringSerializer.class)
private Long roomId;
```

**문제점**: 모든 이벤트 클래스마다 어노테이션 추가 필요, 누락 위험

## 검증 방법

### 1. 단위 테스트

```java

@Test
void snowflakeIdShouldBeSerializedAsString() {
	Long originalId = 249330686647795700L;
	RoomCreatedEvent event = new RoomCreatedEvent(originalId, 123L, TimeSlot.MORNING);
	
	String json = jsonUtil.toJson(event);
	assertThat(json).contains("\"249330686647795700\""); // String으로 직렬화
	
	RoomCreatedEvent parsed = jsonUtil.fromJson(json, RoomCreatedEvent.class);
	assertThat(parsed.getRoomId()).isEqualTo("249330686647795700");
}
```

### 2. Kafka 통합 테스트

```java

@Test
void kafkaEventShouldPreserveSnowflakeId() {
	Long originalId = snowflake.nextId();
	
	// 발행
	eventPublisher.publish(new RoomCreatedEvent(originalId, 123L, TimeSlot.MORNING));
	
	// 수신 대기
	ConsumerRecord<String, String> record = kafkaTestUtils.getSingleRecord("room-created");
	RoomCreatedEvent event = jsonUtil.fromJson(record.value(), RoomCreatedEvent.class);
	
	// 정밀도 검증
	assertThat(Long.parseLong(event.getRoomId())).isEqualTo(originalId);
}
```

## 관련 이슈

- #[ISSUE_NUMBER] Snowflake ID Kafka 전송 시 정밀도 손실 수정

## 참고 자료

- [MDN: Number.MAX_SAFE_INTEGER](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/MAX_SAFE_INTEGER)
- [JSON RFC 8259 - Number 타입의 한계](https://datatracker.ietf.org/doc/html/rfc8259#section-6)
- [Snowflake ID 스펙](https://github.com/twitter-archive/snowflake)
