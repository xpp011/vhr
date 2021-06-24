package cn.xpp011.vhr.controller;

import cn.xpp011.vhr.model.ResponseBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/toLogin")
    public ResponseBean login(){
        return ResponseBean.error("未登录,请前往登录");
    }
}
