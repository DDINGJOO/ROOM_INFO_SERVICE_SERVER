package com.teambind.springproject.event.consumer;

import com.teambind.springproject.event.events.image.ImagesChangeEventWrapper;
import com.teambind.springproject.util.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
	private final JsonUtil jsonUtil;
	
	@KafkaListener(topics = "room-image-changed", groupId = "room-consumer-group")
	public void placeImageChanged(String message) {
		try {
			ImagesChangeEventWrapper request = jsonUtil.fromJson(message, ImagesChangeEventWrapper.class);
			// todo:
			// update image logic
		} catch (Exception e) {
			// 역직렬화 실패 또는 처리 중 오류 발생 시 로깅/대응
			log.error("Failed to deserialize or process profile-create-request message: {}", message, e);
			// 필요하면 DLQ 전송이나 재시도 로직 추가
		}
	}
}
