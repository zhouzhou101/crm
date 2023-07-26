package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Permission;
import com.xxxx.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service
public class RoleService extends BaseService<Role,Integer> {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;

    /**
     * 查询⻆⾊列表
     * @return
     */
    public List<Map<String, Object>> queryAllRoles(Integer userId){

        return roleMapper.queryAllRoles(userId);
    }

    /**
     * 添加角色
     * @param role
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(Role role){
        /*参数校验*/
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"用户姓名不能为空!");
        //通过角色名称查询角色记录
        Role temp = roleMapper.selectByRoleName(role.getRoleName());
        //判断角色记录是否存在
        AssertUtil.isTrue(temp != null,"角色记录存在!");

        /*设置参数的默认值*/
        //是否有效
        role.setIsValid(1);
        //创建时间
        role.setCreateDate(new Date());
        //修改时间
        role.setUpdateDate(new Date());

        /*执行添加操作,判断受影响的行数*/
        AssertUtil.isTrue(roleMapper.insertSelective(role)<1,"角色添加失败!");
    }

    /**
     * 修改角色
     *  1. 参数校验
     *      角色ID    非空，且数据存在
     *      角色名称   非空，名称唯一
     *  2. 设置参数的默认值
     *      修改时间
     *  3. 执行更新操作，判断受影响的行数
     *
     *
     *
     * 乐字节：专注线上IT培训
     * 答疑老师微信：lezijie
     * @param role
     * @return void
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role) {
        /* 1. 参数校验 */
        // 角色ID    非空，且数据存在
        AssertUtil.isTrue(null == role.getId(), "待更新记录不存在！");
        // 通过角色ID查询角色记录
        Role temp = roleMapper.selectByPrimaryKey(role.getId());
        // 判断角色记录是否存在
        AssertUtil.isTrue(null == temp, "待更新记录不存在");

        // 角色名称   非空，名称唯一
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "角色名称不能为空！");
        // 通过角色名称查询角色记录
        temp = roleMapper.selectByRoleName(role.getRoleName());
        // 判断角色记录是否存在（如果不存在，表示可使用；如果存在，且角色ID与当前更新的角色ID不一致，表示角色名称不可用）
        AssertUtil.isTrue(null != temp && (!temp.getId().equals(role.getId())), "角色名称已存在，不可使用！");

        /* 2. 设置参数的默认值 */
        role.setUpdateDate(new Date());

        /* 3. 执行更新操作，判断受影响的行数 */
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) < 1, "修改角色失败！");
    }

    /**
     * 删除角色
     *  1. 参数校验
     *      角色ID    非空，数据存在
     *  2. 设置相关参数的默认
     *      是否有效    0（删除记录）
     *      修改时间    系统默认时间
     *  3. 执行更新操作，判断受影响的行数
     *
     * 乐字节：专注线上IT培训
     * 答疑老师微信：lezijie
     * @param roleId
     * @return void
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer roleId) {
        // 判断角色ID是否为空
        AssertUtil.isTrue(null == roleId, "待删除记录不存在！");
        // 通过角色ID查询角色记录
        Role role = roleMapper.selectByPrimaryKey(roleId);
        // 判断角色记录是否存在
        AssertUtil.isTrue(null == role, "待删除记录不存在！");

        // 设置删除状态
        role.setIsValid(0);
        role.setUpdateDate(new Date());

        // 执行更新操作
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) < 1, "角色删除失败！");
    }

    public void addGrant(Integer[] mids, Integer roleId) {
        /**
         * 核⼼表-t_permission t_role(校验⻆⾊存在)
         * 如果⻆⾊存在原始权限 删除⻆⾊原始权限
         * 然后添加⻆⾊新的权限 批量添加权限记录到t_permission
         */
        Role temp =selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null==roleId||null==temp,"待授权的⻆⾊不存在!");
        int count = permissionMapper.countPermissionByRoleId(roleId);
        if(count>0){
            AssertUtil.isTrue(permissionMapper.deletePermissionsByRoleId(roleId)<count,"权限分配失败!");
        }
        if(null !=mids && mids.length>0){
            List<Permission> permissions=new ArrayList<Permission>();
            for (Integer mid : mids) {
                Permission permission=new Permission();
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permissions.add(permission);
            }
            permissionMapper.insertBatch(permissions);
        }
    }
}
