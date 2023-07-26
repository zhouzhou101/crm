package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 乐字节：专注线上IT培训
 * 答疑老师微信：lezijie
 */

public interface UserMapper extends BaseMapper<User, Integer> {

    // 通过用户名查询用户记录，返回用户对象
    public User queryUserByName(String userName);

    // 查询所有的销售⼈员
    public List<Map<String, Object>> queryAllSales();
}