package com.teambind.springproject.event.publisher;

import com.teambind.springproject.event.events.Event;
import com.teambind.springproject.util.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisher {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final JsonUtil jsonUtil;
	
	public void publish(Event event) {
		String json = jsonUtil.toJson(event);
		kafkaTemplate.send(event.getTopic(), json);
	}
}
