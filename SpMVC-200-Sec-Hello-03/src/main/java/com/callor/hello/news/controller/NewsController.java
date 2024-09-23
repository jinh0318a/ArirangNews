package com.callor.hello.news.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.callor.hello.dao.NewsDao;
import com.callor.hello.models.NewsVO;
import com.callor.hello.news.service.NewsService;

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

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {

		List<NewsVO> newNewsList = newsService.getNews();
		newsService.saveNews(newNewsList);

		List<NewsVO> newsList = newsDao.selectAll();
		model.addAttribute("newsList", newsList);

		return "news/list";
	}

}
