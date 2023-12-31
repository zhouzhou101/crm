package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.model.TreeModle;
import com.xxxx.crm.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {
    //查询所有的资源列表
    public List<TreeModle> queryAllModules();

    //查询所有资源数据
    public List<Module> queryModuleList();

    // 通过层级与模块名查询资源对象
    Module queryModuleByGradeAndModuleName(@Param("grade") Integer grade, @Param("moduleName") String moduleName);

    // 通过层级与URL查询资源对象
    Module queryModuleByGradeAndUrl(@Param("grade")Integer grade, @Param("url")String url);

    // 通过权限码查询资源对象
    Module queryModuleByOptValue(String optValue);

    Integer queryModuleByParentId(Integer id);
}