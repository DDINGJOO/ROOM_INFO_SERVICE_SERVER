package com.teambind.springproject.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teambind.springproject.dto.query.RoomSearchQuery;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.teambind.springproject.entity.QRoomInfo.roomInfo;
import static com.teambind.springproject.entity.attribute.StringBased.QCautionDetail.cautionDetail;
import static com.teambind.springproject.entity.attribute.StringBased.QFurtherDetail.furtherDetail;
import static com.teambind.springproject.entity.attribute.image.QRoomImage.roomImage;
import static com.teambind.springproject.entity.attribute.keyword.QRoomOptionsMapper.roomOptionsMapper;

@Repository
@RequiredArgsConstructor
public class RoomQueryRepositoryImpl implements RoomQueryRepository {
	
	private final JPAQueryFactory queryFactory;
	
	@Override
	public List<RoomInfo> findAllByQuery(RoomSearchQuery query) {
		List<Long> roomIds;

		if (query.getKeywordIds() != null && !query.getKeywordIds().isEmpty()) {
			roomIds = queryFactory
					.select(roomInfo.roomId)
					.from(roomInfo)
					.join(roomInfo.roomOptions, roomOptionsMapper)
					.where(
							roomNameContains(query.getRoomName()),
							placeIdEq(query.getPlaceId()),
							roomIdsIn(query.getRoomIds()),
							minOccupancyGoe(query.getMinOccupancy()),
							statusEq(query.getStatus()),
							roomOptionsMapper.keyword.keywordId.in(query.getKeywordIds())
					)
					.groupBy(roomInfo.roomId)
					.having(roomOptionsMapper.keyword.keywordId.countDistinct().eq((long) query.getKeywordIds().size()))
					.fetch();
		} else {
			roomIds = queryFactory
					.select(roomInfo.roomId)
					.from(roomInfo)
					.where(
							roomNameContains(query.getRoomName()),
							placeIdEq(query.getPlaceId()),
							roomIdsIn(query.getRoomIds()),
							minOccupancyGoe(query.getMinOccupancy()),
							statusEq(query.getStatus())
					)
					.fetch();
		}

		if (roomIds.isEmpty()) {
			return List.of();
		}

		return fetchRoomsWithAllDetails(roomIds);
	}
	
	@Override
	public Optional<RoomInfo> findByIdWithDetails(Long roomId) {
		List<RoomInfo> results = fetchRoomsWithAllDetails(List.of(roomId));
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}
	
	@Override
	public List<RoomInfo> findByIdsWithDetails(List<Long> roomIds) {
		if (roomIds == null || roomIds.isEmpty()) {
			return List.of();
		}
		return fetchRoomsWithAllDetails(roomIds);
	}
	
	private List<RoomInfo> fetchRoomsWithAllDetails(List<Long> roomIds) {
		List<RoomInfo> rooms = queryFactory
				.selectFrom(roomInfo)
				.distinct()
				.where(roomInfo.roomId.in(roomIds))
				.fetch();
		
		queryFactory
				.selectFrom(roomImage)
				.where(roomImage.roomInfo.roomId.in(roomIds))
				.fetch();
		
		queryFactory
				.selectFrom(roomOptionsMapper)
				.join(roomOptionsMapper.keyword).fetchJoin()
				.where(roomOptionsMapper.roomInfo.roomId.in(roomIds))
				.fetch();
		
		queryFactory
				.selectFrom(furtherDetail)
				.where(furtherDetail.roomInfo.roomId.in(roomIds))
				.fetch();
		
		queryFactory
				.selectFrom(cautionDetail)
				.where(cautionDetail.roomInfo.roomId.in(roomIds))
				.fetch();
		
		return rooms;
	}
	
	private BooleanExpression roomNameContains(String roomName) {
		return roomName != null ? roomInfo.roomName.containsIgnoreCase(roomName) : null;
	}
	
	private BooleanExpression placeIdEq(Long placeId) {
		return placeId != null ? roomInfo.placeId.eq(placeId) : null;
	}
	
	private BooleanExpression roomIdsIn(List<Long> roomIds) {
		return roomIds != null && !roomIds.isEmpty()
				? roomInfo.roomId.in(roomIds)
				: null;
	}

	private BooleanExpression minOccupancyGoe(Integer minOccupancy) {
		return minOccupancy != null ? roomInfo.maxOccupancy.goe(minOccupancy) : null;
	}

	private BooleanExpression statusEq(Status status) {
		return status != null ? roomInfo.status.eq(status) : null;
	}
}
