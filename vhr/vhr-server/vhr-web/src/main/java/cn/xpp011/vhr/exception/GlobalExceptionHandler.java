package cn.xpp011.vhr.exception;

import cn.xpp011.vhr.model.ResponseBean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

//开启全局异常监听
@RestControllerAdvice
public class GlobalExceptionHandler {

    //指定处理Sql的异常
    @ExceptionHandler(SQLException.class)
    public ResponseBean SqlException(SQLException e){
        if (e instanceof SQLIntegrityConstraintViolationException){
            return ResponseBean.error("该数据正在被使用,删除失败");
        }
        return ResponseBean.error("数据库操作失败,请联系系统管理员");
    }

}
