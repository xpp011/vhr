package cn.xpp011.vhr.service;

import cn.xpp011.vhr.mapper.PoliticsstatusMapper;
import cn.xpp011.vhr.model.Politicsstatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoliticsstatusService {
    @Autowired
    PoliticsstatusMapper politicsstatusMapper;
    public List<Politicsstatus> getPoliticsstatus() {
        return politicsstatusMapper.getPoliticsstatus();
    }
}
