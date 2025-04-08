package com.wm.ai.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wm.ai.mapper.UserMapper;
import com.wm.ai.model.User;
import com.wm.ai.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

}
