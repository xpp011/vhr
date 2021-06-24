package cn.xpp011.vhr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.xpp011.vhr.mapper")
public class SpringbootVhrApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootVhrApplication.class, args);
    }


}
