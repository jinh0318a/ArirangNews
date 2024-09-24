package com.callor.news.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.callor.news.dao.NewsDao;
import com.callor.news.models.NewsVO;
import com.callor.news.service.NewsService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;

@Controller
public class HomeController {

	private final NewsService newsService;
	private final NewsDao newsDao;

	public HomeController(NewsService newsService, NewsDao newsDao) {
		super();
		this.newsService = newsService;
		this.newsDao = newsDao;
	}

	@PostConstruct
	public void init() {
		List<NewsVO> newNewsList = newsService.getNews();

		newNewsList = newNewsList.stream().filter(news -> !news.getUrl().contains("https://removed.com"))
				.collect(Collectors.toList());

		// 번역 서비스 설정
		String jsonPath = "C:/Users/KMS203/Desktop/news-project-436506-6f6c011f864a.json";
		try {
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
			Translate translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();

			for (NewsVO news : newNewsList) {
				try {
					// 타이틀과 설명 번역
					String translatedTitle = translate
							.translate(news.getTitle(), Translate.TranslateOption.targetLanguage("ko"))
							.getTranslatedText();
					String translatedDescription = translate
							.translate(news.getDescription(), Translate.TranslateOption.targetLanguage("ko"))
							.getTranslatedText();

					// 번역된 내용을 원래 필드에 저장
					news.setTitle(translatedTitle);
					news.setDescription(translatedDescription);

					// 중복 체크 후 데이터베이스에 저장
					if (!newsDao.exists(news.getTitle())) {
						newsDao.insert(news);
					}
				} catch (Exception e) {
					e.printStackTrace();
					// 예외 처리
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		// 홈 페이지에 필요한 데이터 추가
		List<NewsVO> newsList = newsDao.selectAll(); // DB에서 모든 뉴스 가져오기
		model.addAttribute("newsList", newsList);
		return "home";
	}

}
