package com.callor.news.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.callor.news.models.UserVO;

public interface UserDao {

	@Select("select * from tbl_users")
	public List<UserVO> selectAll();

	@Select("select * from tbl_users where username = #{username}")
	public UserVO findById(String username);

	@Select("select * from tbl_users where username = #{username}")
	public List<UserVO> findByEmail(String email);
	
	public void create_user_table(String dumy);

	public int insert(UserVO userVO);
}
