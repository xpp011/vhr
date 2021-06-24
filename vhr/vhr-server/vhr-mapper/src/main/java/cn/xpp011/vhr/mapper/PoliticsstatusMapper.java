package cn.xpp011.vhr.mapper;

import cn.xpp011.vhr.model.Politicsstatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoliticsstatusMapper {

    List<Politicsstatus> getPoliticsstatus();

}