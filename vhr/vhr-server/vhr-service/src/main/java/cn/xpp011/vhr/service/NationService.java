package cn.xpp011.vhr.service;

import cn.xpp011.vhr.mapper.NationMapper;
import cn.xpp011.vhr.model.Nation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NationService {

    @Autowired
    NationMapper nationMapper;

    public List<Nation> getNations() {
        return nationMapper.getNations();
    }
}
