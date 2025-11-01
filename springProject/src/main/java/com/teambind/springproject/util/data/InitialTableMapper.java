package com.teambind.springproject.util.data;

import com.teambind.springproject.entity.attribute.StringBased.Keyword;
import com.teambind.springproject.repository.KeywordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitialTableMapper {
	public static HashMap<Integer, Keyword> keywordMap = new HashMap<>();
	private final KeywordRepository keywordRepository;
	
	@PostConstruct
	public void init() {
		log.info("Initializing keyword map");
	}
	
	@Scheduled(cron = "0 0 6 * * *")
	public void initKeywordMap() {
		log.info("Initializing keyword map");
		keywordMap.clear();
		List<Keyword> keywords = keywordRepository.findAll();
		
		for (Keyword keyword : keywords) {
			keywordMap.put(keyword.getKeywordId().intValue(), keyword);
		}
		log.info("Keyword map initialized");
	}
}
