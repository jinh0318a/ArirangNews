package com.callor.hello.user.service;

import org.springframework.dao.DataAccessException;

import com.callor.hello.models.UserVO;

public interface UserService {

	public UserVO createUser(UserVO userVO) throws DataAccessException;

	public UserVO findByUser(String username);

}
