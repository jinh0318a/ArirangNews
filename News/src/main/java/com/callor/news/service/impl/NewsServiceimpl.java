package com.callor.news.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.callor.news.config.APIConfig;
import com.callor.news.dao.NewsDao;
import com.callor.news.models.MediaList;
import com.callor.news.models.MediaVO;
import com.callor.news.models.NewsList;
import com.callor.news.models.NewsVO;
import com.callor.news.service.NewsService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

@Service
public class NewsServiceimpl implements NewsService {

	private final NewsDao newsDao;

	public NewsServiceimpl(NewsDao newsDao) {
		super();
		this.newsDao = newsDao;
	}

	public void saveNews(List<NewsVO> newsList) {
		for (NewsVO news : newsList) {
			if (!newsDao.exists(news.getTitle())) { // 중복 체크
				newsDao.insert(news);
			}
		}
	}

	@Override
	public List<NewsVO> getNews() {
		String apiURL = APIConfig.API_URL + APIConfig.API_KEY;

		URI newsURI;
		try {
			newsURI = new URI(apiURL);
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

		ResponseEntity<NewsList> newsListEntity;
		try {
			newsListEntity = restTemplate.exchange(newsURI, HttpMethod.GET, null, NewsList.class);
		} catch (RestClientException e) {
			e.printStackTrace();
			return null;
		}

		List<NewsVO> newsList = newsListEntity.getBody().getArticles();

		return newsList;
	}

	@Override
	public List<NewsVO> getTopHeadlinesByCountry(String country) {
		try {
			String jsonPath = "C:/Users/KMS203/Desktop/news-project-436506-6f6c011f864a.json";

			// Google Translate 설정
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
			Translate translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();

			// API 요청 URL 생성
			String apiKey = APIConfig.API_KEY;
			String apiUrl = String.format("https://newsapi.org/v2/top-headlines?country=%s&apiKey=%s", country, apiKey);

			// 뉴스 API 호출
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<NewsList> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null, NewsList.class);

			if (!response.getStatusCode().is2xxSuccessful()) {
				throw new RuntimeException("Failed to fetch news: " + response.getStatusCode());
			}

			List<NewsVO> newsList = response.getBody().getArticles();
			// 불필요한 URL 필터링
			newsList = newsList.stream().filter(news -> !news.getUrl().contains("https://removed.com"))
					.collect(Collectors.toList());

			// 뉴스 제목 및 설명 번역 후 데이터베이스에 저장
			for (NewsVO news : newsList) {
				try {
					String translatedTitle = translate
							.translate(news.getTitle(), Translate.TranslateOption.targetLanguage("ko"))
							.getTranslatedText();
					String translatedDescription = translate
							.translate(news.getDescription(), Translate.TranslateOption.targetLanguage("ko"))
							.getTranslatedText();

					news.setTitle(translatedTitle);
					news.setDescription(translatedDescription);

					// 중복 체크 후 데이터베이스에 저장
					if (!newsDao.exists(news.getTitle())) {
						newsDao.insert(news);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return newsList;

		} catch (IOException e) {
			throw new RuntimeException("Failed to load Google credentials", e);
		}
	}

	@Override
	public List<NewsVO> searchNews(String word) throws IOException {

		String jsonPath = "C:/Users/KMS203/Desktop/news-project-436506-6f6c011f864a.json";

		// Google Translate 설정
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
		Translate translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();

		// 입력된 검색어 번역
		Translation translation = translate.translate(word, Translate.TranslateOption.targetLanguage("en"));
		String translatedWord = translation.getTranslatedText();

		// API 요청 URL 생성
		String apiKey = APIConfig.API_KEY;
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String apiUrl = String.format(
				"https://newsapi.org/v2/everything?q=%s&from=%s&to=%s&sortBy=popularity&apiKey=%s", translatedWord,
				today, today, apiKey);

		// 뉴스 API 호출
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<NewsList> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null, NewsList.class);

		List<NewsVO> newsList = response.getBody().getArticles();
		// 불필요한 URL 필터링
		newsList = newsList.stream().filter(news -> !news.getUrl().contains("https://removed.com"))
				.collect(Collectors.toList());

		// 뉴스 제목 및 설명 번역 후 데이터베이스에 저장
		for (NewsVO news : newsList) {
			try {
				String translatedTitle = translate
						.translate(news.getTitle(), Translate.TranslateOption.targetLanguage("ko")).getTranslatedText();
				String translatedDescription = translate
						.translate(news.getDescription(), Translate.TranslateOption.targetLanguage("ko"))
						.getTranslatedText();

				news.setTitle(translatedTitle);
				news.setDescription(translatedDescription);

				// 중복 체크 후 데이터베이스에 저장
				if (!newsDao.exists(news.getTitle())) {
					newsDao.insert(news);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 검색된 뉴스 리스트 반환
		return newsDao.findByKeyword(word);
	}

	@Override
	public List<MediaVO> getMedias() {
		String apiURL = "https://newsapi.org/v2/top-headlines/sources?apiKey=" + APIConfig.API_KEY;
		URI newsURI;
		try {
			newsURI = new URI(apiURL);
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

		ResponseEntity<MediaList> mediaListEntity;
		try {
			mediaListEntity = restTemplate.exchange(newsURI, HttpMethod.GET, null, MediaList.class);
		} catch (RestClientException e) {
			e.printStackTrace();
			return null;
		}

		List<MediaVO> mediaList = mediaListEntity.getBody().getSources();

		// Google Translate 설정
		String jsonPath = "C:/Users/KMS203/Desktop/news-project-436506-6f6c011f864a.json";
		GoogleCredentials credentials;
		try {
			credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load Google credentials", e);
		}
		Translate translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();

		// 미디어 설명 번역
		for (MediaVO media : mediaList) {
			try {
				String translatedDescription = translate
						.translate(media.getDescription(), Translate.TranslateOption.targetLanguage("ko"))
						.getTranslatedText();
				media.setDescription(translatedDescription);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return mediaList;
	}

}
