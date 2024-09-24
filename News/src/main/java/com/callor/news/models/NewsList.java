package com.callor.news.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsList {
	private String status;
	private int totalResults;
	private List<NewsVO> articles;

}
