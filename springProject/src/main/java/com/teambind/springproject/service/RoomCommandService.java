package com.teambind.springproject.service;

import com.teambind.springproject.dto.command.RoomCreateCommand;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.event.events.RoomCreatedEvent;
import com.teambind.springproject.event.publisher.EventPublisher;
import com.teambind.springproject.mapper.RoomMapper;
import com.teambind.springproject.repository.RoomCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
				new RoomCreatedEvent(roomInfo.getRoomId())
		);
		return roomInfo.getRoomId();
	}
	
	@Transactional
	public Long deleteRoom(Long roomId) {
		roomCommandRepository.deleteById(roomId);
		eventPublisher.publish(
				new RoomCreatedEvent(roomId)
		);
		return roomId;
	}
}
