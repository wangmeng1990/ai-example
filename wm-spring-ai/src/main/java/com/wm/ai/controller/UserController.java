package com.wm.ai.controller;
import com.wm.ai.service.UserService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

/**
* (user)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/user")
public class UserController {
/**
* 服务对象
*/
    @Autowired
    private UserService userService;


}
