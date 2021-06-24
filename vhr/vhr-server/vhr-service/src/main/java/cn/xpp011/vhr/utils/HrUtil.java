package cn.xpp011.vhr.utils;


import cn.xpp011.vhr.model.Hr;
import org.springframework.security.core.context.SecurityContextHolder;

public class HrUtil {
    //得到当前登录hr的用户信息
    public static Hr getHr(){
        return ((Hr) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
