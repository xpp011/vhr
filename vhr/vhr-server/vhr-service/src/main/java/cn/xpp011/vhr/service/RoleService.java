package cn.xpp011.vhr.service;

import cn.xpp011.vhr.mapper.MenuRoleMapper;
import cn.xpp011.vhr.mapper.RoleMapper;
import cn.xpp011.vhr.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    MenuRoleMapper menuRoleMapper;

    public List<Role> getRoleAll() {
        return roleMapper.getAllRoles();
    }

    public List<Integer> getMidsByRid(Integer rid) {
        return menuRoleMapper.getMidsByRid(rid);
    }

    //对应修改角色可访问资源   一个个去遍历表里那个被选中，那个没有被选中  很麻烦
    //所以这里采用统一删除角色的可访问资源 在去统一插入传进来的mids，这样方便很多
    //但是比较危险  注意事务管理
    @Transactional
    public Integer doUpdateMidsByRid(Integer rid, Integer[] mids) {
        menuRoleMapper.deleteMidsByRid(rid);
        return  menuRoleMapper.insertMidsByRid(rid,mids);
    }

    public Integer deleteMidsByRid(Integer rid){
        return menuRoleMapper.deleteMidsByRid(rid);
    }

    public Integer addRole(Role role) {
        if (!role.getName().startsWith("ROLE_")){
            role.setName("ROLE_"+role.getName());
        }
        return roleMapper.insertRole(role);
    }

    //在删除角色时 同时也删除角色在可访问资源表的记录 操作较危险  加事务
    @Transactional
    public Integer deleteRoleById(Integer rid) {
        Integer i=roleMapper.deleteRoleById(rid);
        //删除在可访问资源表的记录
        menuRoleMapper.deleteMidsByRid(rid);
        return i;
    }
}
