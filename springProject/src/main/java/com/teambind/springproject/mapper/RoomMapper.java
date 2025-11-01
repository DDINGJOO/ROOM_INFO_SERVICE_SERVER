package com.teambind.springproject.mapper;

import com.teambind.springproject.dto.command.RoomCreateCommand;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.attribute.StringBased.CautionDetail;
import com.teambind.springproject.entity.attribute.StringBased.FurtherDetail;
import com.teambind.springproject.entity.attribute.StringBased.Keyword;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.util.generator.PrimaryKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.teambind.springproject.util.data.InitialTableMapper.keywordMap;

@Component
@RequiredArgsConstructor
public class RoomMapper {
	private final PrimaryKeyGenerator primaryKeyGenerator;
	
	public RoomInfo toEntity(RoomCreateCommand command) {
		RoomInfo roomInfo = RoomInfo.builder()
				.roomId(primaryKeyGenerator.generateLongKey())
				.placeId(command.getPlaceId())
				.roomName(command.getRoomName())
				.status(Status.PENDING)
				.build();
		
		command.getCautionDetails().forEach(caution ->
				roomInfo.addCautionDetail(new CautionDetail(caution)));
		command.getFurtherDetails().forEach(furtherDetail ->
				roomInfo.addFurtherDetail(new FurtherDetail(furtherDetail)));
		
		List<Keyword> keywords = new ArrayList<>();
		command.getKeywordIds().forEach(keywordId ->
		{
			if (keywordMap.get(keywordId) != null)
				keywords.add(keywordMap.get(keywordId));
			else {
				// TODO impl CustomExceptions
				throw new IllegalArgumentException("Keyword with ID " + keywordId + " not found");
			}
		});
		
		keywords.forEach(roomInfo::addKeyword);
		return roomInfo;
	}
}
