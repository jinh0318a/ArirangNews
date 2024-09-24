package com.callor.news.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.callor.news.dao.NewsDao;
import com.callor.news.models.NewsList;
import com.callor.news.models.NewsVO;
import com.callor.news.models.TranslatedNewsDTO;
import com.callor.news.service.NewsService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

@Controller
@RequestMapping("/news")
public class NewsController {

	private final NewsService newsService;
	private final NewsDao newsDao;

	public NewsController(NewsService newsService, NewsDao newsDao) {
		super();
		this.newsService = newsService;
		this.newsDao = newsDao;
	}

	@RequestMapping(value = "/getNews", method = RequestMethod.GET)
	public String getNews() {
		List<NewsVO> newNewsList = newsService.getNews();
		newsService.saveNews(newNewsList);
		return "news/list";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		// 서비스 계정 JSON 파일 경로
		String jsonPath = "C:/Users/KMS203/Desktop/news-project-436506-6f6c011f864a.json"; // 서비스 계정 키 파일 경로

		try {
			// Credentials 객체 생성
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
			Translate translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();

			List<NewsVO> newsList = newsDao.selectAll();
			List<TranslatedNewsDTO> translatedNewsList = new ArrayList<>();

			for (NewsVO news : newsList) {
				try {
					Translation translation = translate.translate(news.getTitle(),
							Translate.TranslateOption.targetLanguage("ko"));
					translatedNewsList.add(new TranslatedNewsDTO(news, translation.getTranslatedText()));

				} catch (Exception e) {
					e.printStackTrace();
					translatedNewsList.add(new TranslatedNewsDTO(news, news.getTitle()));
				}
			}

			System.out.println(translatedNewsList.get(0).getNews().getUrlToImage());

			model.addAttribute("newsList", translatedNewsList);
			return "news/list";
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", "Failed to load credentials");
			return "news/error"; // 오류 페이지로 리다이렉트
		}
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(String word, Model model) {
		String jsonPath = "C:/Users/KMS203/Desktop/news-project-436506-6f6c011f864a.json";

		try {
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
			Translate translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();

			Translation translation = translate.translate(word, Translate.TranslateOption.targetLanguage("en"));
			String translatedWord = translation.getTranslatedText();

			String apiKey = "c580bafbd4364f818016dd270e8a6fb5";
			String apiUrl = String.format("https://newsapi.org/v2/everything?q=%s&apiKey=%s", translatedWord, apiKey);

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<NewsList> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null, NewsList.class);

			List<NewsVO> newsList = response.getBody().getArticles();
			List<TranslatedNewsDTO> translatedNewsList = new ArrayList<>();

			for (NewsVO news : newsList) {
				try {
					Translation titleTranslation = translate.translate(news.getTitle(),
							Translate.TranslateOption.targetLanguage("ko"));
					translatedNewsList.add(new TranslatedNewsDTO(news, titleTranslation.getTranslatedText()));
					if (!newsDao.exists(news.getTitle())) { // 중복 체크
						newsDao.insert(news);
					}
				} catch (Exception e) {
					e.printStackTrace();
					translatedNewsList.add(new TranslatedNewsDTO(news, news.getTitle()));
				}
			}

			model.addAttribute("word", word);
			model.addAttribute("newsList", translatedNewsList);
			return "news/search";

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", "자격 증명 로드 실패");
			return "news/error";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "뉴스 검색 실패");
			return "news/error";
		}
	}

}
