package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    int countPermissionByRoleId(Integer roleId);

    int deletePermissionsByRoleId(Integer roleId);

    List<Integer> queryRoleHasModuleIdsByRoleId(Integer roleId);

    List<String> queryUserHasRolesHasPermissions(Integer userId);

    Integer countPermissionByModuleId(Integer id);

    void deletePermissionByModuleId(Integer id);
}