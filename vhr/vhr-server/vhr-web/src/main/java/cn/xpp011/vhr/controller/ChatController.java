package cn.xpp011.vhr.controller;

import cn.xpp011.vhr.model.Hr;
import cn.xpp011.vhr.model.ResponseBean;
import cn.xpp011.vhr.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    HrService hrService;

    @GetMapping("/hrs")
    public ResponseBean getAllHrsExceptCurrentHr(){
        List<Hr> list=hrService.getAllHrsExceptCurrentHr();
        if (list!=null){
            return ResponseBean.ok(null,list);
        }
        return ResponseBean.error("联系人查询失败");
    }
}
