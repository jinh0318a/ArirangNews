package com.callor.hello.news.service;

import java.util.List;

import com.callor.hello.models.NewsVO;

public interface NewsService {

	public List<NewsVO> getNews();

	public void saveNews(List<NewsVO> newsList);
	
}
