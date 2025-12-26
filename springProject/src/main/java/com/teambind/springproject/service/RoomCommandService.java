package com.teambind.springproject.service;

import com.teambind.springproject.dto.command.RoomCreateCommand;
import com.teambind.springproject.dto.command.RoomUpdateCommand;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.attribute.StringBased.CautionDetail;
import com.teambind.springproject.entity.attribute.StringBased.FurtherDetail;
import com.teambind.springproject.entity.attribute.StringBased.Keyword;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.event.events.RoomCreatedEvent;
import com.teambind.springproject.event.events.RoomDeletedEvent;
import com.teambind.springproject.event.publisher.EventPublisher;
import com.teambind.springproject.exception.RoomNotFoundException;
import com.teambind.springproject.mapper.RoomMapper;
import com.teambind.springproject.repository.RoomCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.teambind.springproject.util.data.InitialTableMapper.keywordMap;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomCommandService {

	private final RoomCommandRepository roomCommandRepository;
	private final RoomMapper roomMapper;
	private final EventPublisher eventPublisher;

	@Transactional
	public Long createRoom(RoomCreateCommand command) {
		RoomInfo roomInfo = roomMapper.toEntity(command);
		roomCommandRepository.save(roomInfo);
		eventPublisher.publish(
				new RoomCreatedEvent(roomInfo.getRoomId(), roomInfo.getPlaceId(), roomInfo.getTimeSlot())
		);
		return roomInfo.getRoomId();
	}

	@Transactional
	public Long updateRoom(Long roomId, RoomUpdateCommand command) {
		RoomInfo roomInfo = roomCommandRepository.findById(roomId)
				.orElseThrow(() -> new RoomNotFoundException(roomId));

		roomInfo.updateRoomName(command.getRoomName());
		roomInfo.updateTimeSlot(command.getTimeSlot());

		if (command.getMaxOccupancy() != null) {
			roomInfo.updateMaxOccupancy(command.getMaxOccupancy());
		}

		if (command.getFurtherDetails() != null) {
			roomInfo.clearFurtherDetails();
			command.getFurtherDetails().forEach(detail ->
					roomInfo.addFurtherDetail(new FurtherDetail(detail)));
		}

		if (command.getCautionDetails() != null) {
			roomInfo.clearCautionDetails();
			command.getCautionDetails().forEach(detail ->
					roomInfo.addCautionDetail(new CautionDetail(detail)));
		}

		if (command.getKeywordIds() != null) {
			roomInfo.clearKeywords();
			List<Keyword> keywords = new ArrayList<>();
			command.getKeywordIds().forEach(keywordId -> {
				if (keywordMap.get(keywordId) != null) {
					keywords.add(keywordMap.get(keywordId));
				} else {
					throw new IllegalArgumentException("Keyword with ID " + keywordId + " not found");
				}
			});
			keywords.forEach(roomInfo::addKeyword);
		}

		return roomId;
	}

	@Transactional
	public Long updateRoomStatus(Long roomId, Status status) {
		RoomInfo roomInfo = roomCommandRepository.findById(roomId)
				.orElseThrow(() -> new RoomNotFoundException(roomId));
		roomInfo.updateStatus(status);
		return roomId;
	}

	@Transactional
	public Long closePendingRoom(Long roomId) {
		RoomInfo roomInfo = roomCommandRepository.findById(roomId)
				.orElseThrow(() -> new RoomNotFoundException(roomId));
		roomInfo.closePendingRoom();
		return roomId;
	}

	@Transactional
	public Long deleteRoom(Long roomId) {
		RoomInfo roomInfo = roomCommandRepository.findById(roomId)
				.orElseThrow(() -> new RoomNotFoundException(roomId));
		roomCommandRepository.deleteById(roomId);
		eventPublisher.publish(
				new RoomDeletedEvent(roomInfo.getRoomId())
		);
		return roomId;
	}
}
