package com.callor.news.models;

import lombok.Setter;

import lombok.Getter;

@Getter
@Setter
public class TranslatedNewsDTO {

	private NewsVO news;
	private String translatedTitle;

	public TranslatedNewsDTO(NewsVO news, String translatedTitle) {
		this.news = news;
		this.translatedTitle = translatedTitle;
	}

}