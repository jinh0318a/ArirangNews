package com.callor.news.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewsVO {
	private int n_no;
	private String title;
	private String description;
	private String url;
	private String urlToImage;
	private String publishedAt;	

}