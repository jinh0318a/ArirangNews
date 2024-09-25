package com.callor.news.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TranslatedNewsDTO {

	private NewsVO news;
	private String translatedTitle;

	public TranslatedNewsDTO(NewsVO news, String translatedTitle) {
		this.news = news;
		this.translatedTitle = translatedTitle;
	}

}