package com.callor.news.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MainNewsList {
	private String resultCode;
	private String resultMsg;
	private String numOfRows;
	private String pageNo;
	private String totalCount;
	private List<MainNewsVO> items;
}
