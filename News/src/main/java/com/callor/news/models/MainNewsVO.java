package com.callor.news.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MainNewsVO {
	private int m_no;
	private String title;
	private String content;
	private String news_url;
	private String thum_url;
	private String broadcast_date;
}
