package com.callor.hello.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.callor.hello.dao.RoleDao;
import com.callor.hello.dao.UserDao;
import com.callor.hello.models.RoleVO;
import com.callor.hello.models.UserVO;
import com.callor.hello.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	private final UserDao userDao;
	private final RoleDao roleDao;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
		super();
		this.userDao = userDao;
		this.roleDao = roleDao;
		this.passwordEncoder = passwordEncoder;
	}

	@Autowired
	public void creat_table() {

		try {
			userDao.create_user_table(null);
			roleDao.create_role_table(null);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/*
	 * 회원가입 신청된 사용자 정보를 tbl_users, tbl_role 테이블에 insert 하기 1. tbl_users 에 데이터가 있는지
	 * 확인 2. 없으면 이 사용자는 "ROLE_ADMIN" 권한을 부여한다 3. 또한 "ROLE_USER" 권한도 같이 부여한다 4. 만약
	 * tbl_users 테이블에 데이터가 있으면 5. 이 사용자는 ROLE_USER 권한만 갖는다
	 */
	@Transactional
	@Override
	public UserVO createUser(UserVO userVO) throws DataAccessException {
		log.debug("USER {}", userVO.toString());
		String encPassword = passwordEncoder.encode(userVO.getPassword());
		userVO.setPassword(encPassword);
		List<UserVO> userList = userDao.selectAll();
		List<RoleVO> roles = new ArrayList<>();
		if (userList.size() > 0) {
			// 이미 회원 가입된 데이터가 있음
			roles.add(RoleVO.builder().r_username(userVO.getUsername()).r_role("ROLE_USER").build());
		} else {
			// 회원 가입된 데이터가 없음
			roles.add(RoleVO.builder().r_username(userVO.getUsername()).r_role("ROLE_ADMIN").build());
			roles.add(RoleVO.builder().r_username(userVO.getUsername()).r_role("ROLE_USER").build());
		}

		int ret = userDao.insert(userVO);
		ret += roleDao.insert(roles);

		return userVO;
	}

	@Override
	public UserVO findByUser(String username) {
		return null;
	}

}
