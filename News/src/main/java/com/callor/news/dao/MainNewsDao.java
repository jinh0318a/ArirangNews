package com.callor.news.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.callor.news.models.MainNewsVO;

public interface MainNewsDao {

	public int insert(MainNewsVO mainNewsVO);
	
	public boolean exists(String title);

	@Select("select * from tbl_main order by broadcast_date desc limit 20")
	public List<MainNewsVO> selectLatest();

	@Select("select * from tbl_main where m_no = #{id}")
	public MainNewsVO findByNo(String id);

}
