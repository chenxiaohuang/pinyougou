package com.pinyougou.manager.controller;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 用户名
 * @description 和登陆相关的控制器 类型
 * @date 2019/8/6 0006
 */

@RestController//包含@ResponeseBoby+@Controller
@RequestMapping("login")
public class LoginController {

    @RequestMapping("/name")
    public Map name(){
        String name=SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = new HashMap();
        map.put("loginName",name);
        return map;
    }
}
