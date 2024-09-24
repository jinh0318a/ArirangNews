package com.callor.news.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.callor.news.dao.NewsDao;
import com.callor.news.models.NewsList;
import com.callor.news.models.NewsVO;
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
		try {
			// 데이터베이스에서 모든 뉴스 목록 가져오기
			List<NewsVO> newsList = newsDao.selectAll();

			// 모델에 뉴스 리스트 추가
			model.addAttribute("newsList", newsList);
			return "news/list";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "뉴스 목록을 가져오는 데 실패했습니다.");
			return "news/error"; // 오류 페이지로 리다이렉트
		}
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(String word, Model model) {
		// 먼저 데이터베이스에서 해당 검색어에 대한 뉴스가 있는지 확인
		List<NewsVO> existingNewsList = newsDao.findByKeyword(word);

		if (!existingNewsList.isEmpty()) {
			// 결과가 존재하면 바로 반환
			model.addAttribute("word", word);
			model.addAttribute("newsList", existingNewsList);
			return "news/search";
		}

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

			for (NewsVO news : newsList) {
				try {
					// 타이틀과 설명 번역
					String translatedTitle = translate
							.translate(news.getTitle(), Translate.TranslateOption.targetLanguage("ko"))
							.getTranslatedText();
					String translatedDescription = translate
							.translate(news.getDescription(), Translate.TranslateOption.targetLanguage("ko"))
							.getTranslatedText();

					// 번역된 내용을 news 객체에 설정
					news.setTitle(translatedTitle); // 원래 title 필드에 번역된 제목 저장
					news.setDescription(translatedDescription); // 원래 description 필드에 번역된 설명 저장

					// 중복 체크 후 저장
					if (!newsDao.exists(news.getTitle())) {
						newsDao.insert(news); // 번역된 내용이 저장된 news 객체 저장
					}
				} catch (Exception e) {
					e.printStackTrace();
					// 예외 처리 (예: 기본 타이틀 및 설명 사용)
				}
			}

			model.addAttribute("word", word);
			model.addAttribute("newsList", newsList); // 번역된 newsList 전달
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

	@RequestMapping(value = "/article/{n_no}", method = RequestMethod.GET)
	public String getArticle(@PathVariable("n_no") String n_no, Model model) {
		NewsVO article = newsDao.findByNO(n_no);
		if (article != null) {
			model.addAttribute("article", article);
			return "news/article";
		} else {
			return "news/error";
		}
	}

}
