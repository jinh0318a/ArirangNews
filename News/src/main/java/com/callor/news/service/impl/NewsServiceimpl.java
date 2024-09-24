package com.callor.news.service.impl;

import java.net.URI;
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
import com.callor.news.models.NewsList;
import com.callor.news.models.NewsVO;
import com.callor.news.service.NewsService;

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

}
