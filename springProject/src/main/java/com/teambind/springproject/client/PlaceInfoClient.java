package com.teambind.springproject.client;

import com.teambind.springproject.dto.external.PlaceInfoResponse;

import java.util.Optional;

public interface PlaceInfoClient {

	Optional<PlaceInfoResponse> getPlaceInfo(Long placeId);

	Optional<String> getPlaceOwnerId(Long placeId);
}
