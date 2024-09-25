package com.callor.news.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.callor.news.models.NewsVO;

public interface NewsDao {

	public int insert(NewsVO newsVO);

	public boolean exists(String title);

	@Select("select * from tbl_news order by publishedAt desc")
	public List<NewsVO> selectAll();

	@Select("select * from tbl_news where n_no = #{id}")
	public NewsVO findByNO(String id);

	@Select("SELECT * FROM tbl_news WHERE title LIKE CONCAT('%', #{word}, '%') OR description LIKE CONCAT('%', #{word}, '%') order by publishedAt desc")
	public List<NewsVO> findByKeyword(String word);

}
