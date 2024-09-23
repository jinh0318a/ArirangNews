package com.callor.hello.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.callor.hello.models.RoleVO;

public interface RoleDao {

	public void create_role_table(String dumy);

	public int insert(List<RoleVO> roles);

	@Select("select * from tbl_roles where r_username = #{username}")
	public List<RoleVO> findeByUsername(String username);
}
