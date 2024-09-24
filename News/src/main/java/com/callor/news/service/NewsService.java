package com.callor.news.service;

import java.util.List;

import com.callor.news.models.NewsVO;

public interface NewsService {

	public List<NewsVO> getNews();

	public void saveNews(List<NewsVO> newsList);

}
