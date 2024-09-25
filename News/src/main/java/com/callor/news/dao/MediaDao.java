package com.callor.news.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.callor.news.models.MediaVO;

public interface MediaDao {

	@Select("select * from tbl_media where id = #{id}")
	public MediaVO findById(String id);

	@Insert("insert into tbl_media (id, name, description, url, category, language, country) values (#{id}, #{name}, #{description}, #{url}, #{category}, #{language}, #{country})")
	public int insert(MediaVO mediaVO);

	@Select("select * from tbl_media")
	public List<MediaVO> findByAll();
}
