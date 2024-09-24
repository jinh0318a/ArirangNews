package com.callor.news.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.callor.news.models.NewsVO;
import com.callor.news.service.NewsService;

@Controller
public class HomeController {

	private final NewsService newsService;
	
	
	public HomeController(NewsService newsService) {
		super();
		this.newsService = newsService;
	}


	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		List<NewsVO> newNewsList = newsService.getNews();
		newsService.saveNews(newNewsList);
		return "home";
	}

}
