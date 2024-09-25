package com.callor.news.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MediaVO {
	private String id;
	private String name;
	private String description;
	private String url;
	private String category;
	private String language;
	private String country;
}
