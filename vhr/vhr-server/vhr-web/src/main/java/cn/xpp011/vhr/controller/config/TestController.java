package cn.xpp011.vhr.controller.config;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/test")
    public String test(){
        return "测试";
    }

    @RequestMapping("/employee/basic/test")
    public String test1(){
        return "测试/employee/basic/test";
    }

    @RequestMapping("/employee/advanced/test")
    public String test2(){
        return "测试/employee/advanced/test";
    }
}
