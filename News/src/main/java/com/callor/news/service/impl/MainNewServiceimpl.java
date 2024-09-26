package com.callor.news.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.callor.news.config.APIConfig;
import com.callor.news.dao.MainNewsDao;
import com.callor.news.models.MainNewsList;
import com.callor.news.models.MainNewsVO;
import com.callor.news.service.MainNewsService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;

@Service
public class MainNewServiceimpl implements MainNewsService {

	private final MainNewsDao mainNewsDao;

	public MainNewServiceimpl(MainNewsDao mainNewsDao) {
		super();
		this.mainNewsDao = mainNewsDao;
	}

	@Override
	public List<MainNewsVO> getMainNews() {
		String apiURL = APIConfig.Arirang_API + APIConfig.Arirang_API_KEY;

		URI mainURI;
		try {
			mainURI = new URI(apiURL);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add((request, body, execution) -> {
			ClientHttpResponse response = execution.execute(request, body);
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			return response;
		});

		ResponseEntity<MainNewsList> newsListEntity;
		try {
			newsListEntity = restTemplate.exchange(mainURI, HttpMethod.GET, null, MainNewsList.class);
		} catch (RestClientException e) {
			e.printStackTrace();
			return null;
		}

		List<MainNewsVO> mainNewsList = newsListEntity.getBody().getItems();

		// Google Translate 설정
		String jsonPath = "C:/Users/KMS203/Desktop/news-project-436506-6f6c011f864a.json";
		GoogleCredentials credentials;
		try {
			credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load Google credentials", e);
		}
		Translate translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();

		// 제목 번역 및 DB에 저장
		for (MainNewsVO news : mainNewsList) {
			try {
				String translatedTitle = translate
						.translate(news.getTitle(), Translate.TranslateOption.targetLanguage("ko")).getTranslatedText();
				news.setTitle(translatedTitle);

				String translatedContent = translate
						.translate(news.getContent(), Translate.TranslateOption.targetLanguage("ko"))
						.getTranslatedText();
				news.setContent(translatedContent);
				if (!mainNewsDao.exists(news.getTitle())) {
					mainNewsDao.insert(news);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return mainNewsList;
	}

}
