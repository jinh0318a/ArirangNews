package com.callor.news.controller;

import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.callor.news.dao.MainNewsDao;
import com.callor.news.models.MainNewsVO;
import com.callor.news.service.MainNewsService;

@Controller
public class HomeController {

	private final MainNewsService mainNewsService;
	private final MainNewsDao mainNewsDao;

	public HomeController(MainNewsService mainNewsService, MainNewsDao mainNewsDao) {
		super();
		this.mainNewsService = mainNewsService;
		this.mainNewsDao = mainNewsDao;
	}

//	@PostConstruct
//	public void init() {
//		List<NewsVO> newNewsList = newsService.getNews();
//
//		newNewsList = newNewsList.stream().filter(news -> !news.getUrl().contains("https://removed.com"))
//				.collect(Collectors.toList());
//
//		// 번역 서비스 설정
//		String jsonPath = "C:/Users/KMS203/Desktop/news-project-436506-6f6c011f864a.json";
//		try {
//			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
//			Translate translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
//
//			for (NewsVO news : newNewsList) {
//				try {
//					// 타이틀과 설명 번역
//					String translatedTitle = translate
//							.translate(news.getTitle(), Translate.TranslateOption.targetLanguage("ko"))
//							.getTranslatedText();
//					String translatedDescription = translate
//							.translate(news.getDescription(), Translate.TranslateOption.targetLanguage("ko"))
//							.getTranslatedText();
//
//					// 번역된 내용을 원래 필드에 저장
//					news.setTitle(translatedTitle);
//					news.setDescription(translatedDescription);
//
//					// 중복 체크 후 데이터베이스에 저장
//					if (!newsDao.exists(news.getTitle())) {
//						newsDao.insert(news);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					// 예외 처리
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		mainNewsService.getMainNews();
		List<MainNewsVO> newsList = mainNewsDao.selectLatest();
		model.addAttribute("newsList", newsList);
		return "home";
//		return "home :: homeContent";
	}

	
}
