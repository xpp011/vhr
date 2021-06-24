package cn.xpp011.vhr.mapper;

import cn.xpp011.vhr.model.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper {
    List<Role> getAllRoles();

    Integer insertRole(Role role);

    Integer deleteRoleById(Integer rid);
}