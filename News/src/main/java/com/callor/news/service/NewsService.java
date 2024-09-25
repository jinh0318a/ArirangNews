package com.callor.news.service;

import java.io.IOException;
import java.util.List;

import com.callor.news.models.NewsVO;

public interface NewsService {

	public List<NewsVO> getNews();

	public void saveNews(List<NewsVO> newsList);

	public List<NewsVO> getTopHeadlinesByCountry(String country) throws IOException;

	public List<NewsVO> searchNews(String word) throws IOException;

}
