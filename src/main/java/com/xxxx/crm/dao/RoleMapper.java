package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Role;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {


    // 查询⻆⾊列表

    @MapKey(value = "id")
    public List<Map<String,Object>> queryAllRoles(Integer userId);

    Role selectByRoleName(String roleName);
}