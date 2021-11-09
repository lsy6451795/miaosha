package com.syliu.miaosha.service;

import com.syliu.miaosha.dao.UserDao;
import com.syliu.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    public User getById(int id){
        User user = userDao.getById(id);
        return user;
    }
//    @Transactional
    public boolean tx() {
        User user1 = new User(2, "222");
        User user2 = new User(1, "333");
        userDao.insert(user1);
        userDao.insert(user2);
        return true;
    }


}
