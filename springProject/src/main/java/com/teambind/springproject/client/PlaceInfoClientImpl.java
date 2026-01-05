package com.teambind.springproject.client;

import com.teambind.springproject.dto.external.PlaceInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
public class PlaceInfoClientImpl implements PlaceInfoClient {

	private final RestTemplate restTemplate;
	private final String baseUrl;

	public PlaceInfoClientImpl(
			RestTemplate restTemplate,
			@Value("${place-info.url}") String url,
			@Value("${place-info.port}") int port) {
		this.restTemplate = restTemplate;
		this.baseUrl = url + ":" + port;
	}

	@Override
	public Optional<PlaceInfoResponse> getPlaceInfo(Long placeId) {
		String url = baseUrl + "/api/v1/places/" + placeId;

		try {
			ResponseEntity<PlaceInfoResponse> response = restTemplate.getForEntity(
					url,
					PlaceInfoResponse.class
			);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				return Optional.of(response.getBody());
			}

			log.warn("Place Info API returned non-success status: {}", response.getStatusCode());
			return Optional.empty();

		} catch (RestClientException e) {
			log.error("Failed to fetch place info for placeId={}: {}", placeId, e.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public Optional<String> getPlaceOwnerId(Long placeId) {
		return getPlaceInfo(placeId)
				.map(PlaceInfoResponse::getUserId);
	}
}
