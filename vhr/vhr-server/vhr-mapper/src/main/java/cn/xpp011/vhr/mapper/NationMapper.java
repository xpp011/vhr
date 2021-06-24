package cn.xpp011.vhr.mapper;

import cn.xpp011.vhr.model.Nation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NationMapper {

    List<Nation> getNations();
}