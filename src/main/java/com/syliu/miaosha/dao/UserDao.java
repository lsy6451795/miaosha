package com.syliu.miaosha.dao;

import com.syliu.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface UserDao {
	
	@Select("select * from user where id=#{id}")
	User getById(@Param("id") int id);

	@Select("select * from user")
	List<User> getAll();

	@Insert("insert into user(id, name)values(#{id}, #{name})")
	int insert(User user);
	
}
