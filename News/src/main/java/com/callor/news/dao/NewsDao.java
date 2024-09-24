package com.callor.news.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.callor.news.models.NewsVO;

public interface NewsDao {

	public int insert(NewsVO newsVO);

	public boolean exists(String title);

	@Select("select * from tbl_news")
	public List<NewsVO> selectAll();

}
