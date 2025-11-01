[STORY] Room 검색 API 구현

## 배경

사용자가 다양한 조건으로 합주실을 검색할 수 있어야 한다.
PlaceID로 특정 공간의 합주실 목록을 조회하거나, 키워드나 이름으로 검색하거나, 여러 Room을 한번에 조회할 수 있어야 한다.

## 수용 기준(AC)

- [ ] PlaceID로 검색 (GET /api/rooms?placeId={placeId})
	- 해당 Place에 속한 모든 Room 조회
	- 페이징 지원
- [ ] Keyword로 검색 (GET /api/rooms?keyword={keyword})
	- Room에 연결된 키워드로 검색
	- 여러 키워드 OR 조건 지원 (keyword=WiFi&keyword=주차)
	- 페이징 지원
- [ ] 이름으로 검색 (GET /api/rooms?name={name})
	- Room 이름 부분 일치 검색 (LIKE %name%)
	- 페이징 지원
- [ ] ID 배열로 배치 조회 (GET /api/rooms/batch?ids=1,2,3,4,5)
	- 최대 100개까지 한번에 조회
	- 존재하지 않는 ID는 결과에서 제외
	- 순서 보장 (요청한 ID 순서대로 반환)
- [ ] 복합 조건 검색 지원 (placeId + keyword 등)
- [ ] 검색 결과에 페이징 메타데이터 포함 (totalElements, totalPages, currentPage)

## 디자인/계약 링크

- API 명세: docs/api/room-search-api-spec.md
- 검색 성능 요구사항: 응답 시간 500ms 이하

## 구현 메모/리스크

- Keyword 검색은 N+1 문제 발생 가능 → Fetch Join 사용
- 이름 검색은 인덱스 고려 (roomName 컬럼에 인덱스 추가)
- 배치 조회는 IN 쿼리 사용, 너무 많은 ID는 성능 저하 가능성
- QueryDSL 또는 Specification 사용 고려

## 연결된 Epic

Epic: Room 서비스 핵심 기능 구현
