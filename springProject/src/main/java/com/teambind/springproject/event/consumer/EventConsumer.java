package com.teambind.springproject.event.consumer;

import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.event.events.image.ImagesChangeEventWrapper;
import com.teambind.springproject.event.events.image.SequentialImageChangeEvent;
import com.teambind.springproject.repository.RoomCommandRepository;
import com.teambind.springproject.util.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
	private final JsonUtil jsonUtil;
	private final RoomCommandRepository roomCommandRepository;

	@KafkaListener(topics = "room-image-changed", groupId = "room-consumer-group")
	@Transactional
	public void placeImageChanged(String message) {
		try {
			ImagesChangeEventWrapper request = jsonUtil.fromJson(message, ImagesChangeEventWrapper.class);

			if (request == null || request.getReferenceId() == null) {
				log.warn("Received null or invalid image change event: {}", message);
				return;
			}

			String referenceId = request.getReferenceId();
			Long roomId;

			try {
				roomId = Long.parseLong(referenceId);
			} catch (NumberFormatException e) {
				log.error("Invalid room ID format in reference ID: {}", referenceId);
				return;
			}

			// Room 조회
			RoomInfo roomInfo = roomCommandRepository.findById(roomId)
					.orElse(null);

			if (roomInfo == null) {
				log.warn("Room not found for ID: {}", roomId);
				return;
			}

			// 기존 이미지 전체 삭제 (불변 리스트 문제 해결)
			// getRoomImages()는 불변 리스트를 반환하므로 새 ArrayList로 복사
			new ArrayList<>(roomInfo.getRoomImages()).forEach(roomInfo::removeRoomImage);

			// 새로운 이미지 추가 (비어있으면 전체 삭제만 수행됨)
			if (!request.isEmpty()) {
				for (SequentialImageChangeEvent imageEvent : request.getImages()) {
					// 이미지 쌍 검증
					if (!validateImagePair(imageEvent, roomId.toString())) {
						continue;
					}

					// sequence 활용하여 이미지 추가
					Integer eventSequence = imageEvent.getSequence();
					if (eventSequence != null && eventSequence > 0) {
						// sequence가 있으면 지정된 순서로
						roomInfo.addRoomImageWithSequence(
								imageEvent.getImageId(),
								imageEvent.getImageUrl(),
								eventSequence.longValue()
						);
					} else {
						// sequence가 없으면 자동 순서로
						roomInfo.addRoomImage(
								imageEvent.getImageId(),
								imageEvent.getImageUrl()
						);
					}
				}
			}

			// 변경사항 저장
			roomCommandRepository.save(roomInfo);

			log.info("Successfully processed image change for room ID: {}, image count: {}",
					roomId, roomInfo.getRoomImageCount());

		} catch (Exception e) {
			// 역직렬화 실패 또는 처리 중 오류 발생 시 로깅/대응
			log.error("Failed to deserialize or process room-image-changed message: {}", message, e);
			// 필요하면 DLQ 전송이나 재시도 로직 추가
		}
	}

	/**
	 * 이미지 쌍(imageId, imageUrl) 검증
	 *
	 * @param imageEvent 검증할 이미지 이벤트
	 * @param roomId 로깅용 Room ID
	 * @return 유효한 경우 true, 그렇지 않으면 false
	 */
	private boolean validateImagePair(SequentialImageChangeEvent imageEvent, String roomId) {
		if (imageEvent == null) {
			log.warn("Null image event for roomId: {}", roomId);
			return false;
		}

		String imageId = imageEvent.getImageId();
		String imageUrl = imageEvent.getImageUrl();

		// imageId와 imageUrl 둘 다 필수
		if (imageId == null || imageId.trim().isEmpty()) {
			log.warn("Missing imageId in image pair for roomId: {}, imageUrl: {}",
					roomId, imageUrl);
			return false;
		}

		if (imageUrl == null || imageUrl.trim().isEmpty()) {
			log.warn("Missing imageUrl in image pair for roomId: {}, imageId: {}",
					roomId, imageId);
			return false;
		}

		// URL 형식 검증
		if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://") && !imageUrl.startsWith("/")) {
			log.warn("Invalid image URL format for roomId: {}, imageId: {}, imageUrl: {}",
					roomId, imageId, imageUrl);
			return false;
		}

		log.debug("Valid image pair for roomId: {}, imageId: {}, imageUrl: {}, sequence: {}",
				roomId, imageId, imageUrl, imageEvent.getSequence());

		return true;
	}
}
