package com.xxxx.crm.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 乐字节：专注线上IT培训
 * 答疑老师微信：lezijie
 */
@Service
public class UserService extends BaseService<User, Integer> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;
    /**
     * ⽤户登录
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel userLogin(String userName, String userPwd) {
        // 1. 验证参数
        checkLoginParams(userName, userPwd);
        // 2. 根据⽤户名，查询⽤户对象
        User user = userMapper.queryUserByName(userName);
        // 3. 判断⽤户是否存在 (⽤户对象为空，记录不存在，⽅法结束)
        AssertUtil.isTrue(null == user, "⽤户不存在或已注销！");
        // 4. ⽤户对象不为空（⽤户存在，校验密码。密码不正确，⽅法结束）
        checkLoginPwd(userPwd, user.getUserPwd());
        // 5. 密码正确（⽤户登录成功，返回⽤户的相关信息）
        return buildUserInfo(user);
    }

    /**
     * 构建返回的⽤户信息
     * @param user
     * @return
     */
    private UserModel buildUserInfo(User user) {
        UserModel userModel = new UserModel();
        // 设置⽤户信息
//        userModel.setId(user.getId());
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }
    /**
     * 验证登录密码
     * @param userPwd 前台传递的密码
     * @param upwd 数据库中查询到的密码
     * @return
     */
    private void checkLoginPwd(String userPwd, String upwd) {
        // 数据库中的密码是经过加密的，将前台传递的密码先加密，再与数据库中的密码作⽐较
        userPwd = Md5Util.encode(userPwd);
        // ⽐较密码
        AssertUtil.isTrue(!userPwd.equals(upwd), "⽤户密码不正确！");
    }
    /**
     * 验证⽤户登录参数
     * @param userName
     * @param userPwd
     */
    private void checkLoginParams(String userName, String userPwd) {
        // 判断姓名
        AssertUtil.isTrue(StringUtils.isBlank(userName), "⽤户姓名不能为空！");
        // 判断密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "⽤户密码不能为空！");
    }

    /**
     * ⽤户密码修改
     * 1. 参数校验
     * ⽤户ID：userId ⾮空 ⽤户对象必须存在
     * 原始密码：oldPassword ⾮空 与数据库中密⽂密码保持⼀致
     * 新密码：newPassword ⾮空 与原始密码不能相同
     * 确认密码：confirmPassword ⾮空 与新密码保持⼀致
     * 2. 设置⽤户新密码
     * 新密码进⾏加密处理
     * 3. 执⾏更新操作
     * 受影响的⾏数⼩于1，则表示修改失败
     *
     * 注：在对应的更新⽅法上，添加事务控制
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userId,String oldPwd,
                                   String newPwd,String confirmPassword){
        // 通过userId获取⽤户对象
        User user = userMapper.selectByPrimaryKey(userId);
        // 判断用户记录是否存在
        AssertUtil.isTrue(user == null,"待更新记录不存在");
        // 参数校验
        checkPasswordParams(user,oldPwd,newPwd,confirmPassword);
        // 2. 设置⽤户新密码
        user.setUserPwd(Md5Util.encode(newPwd));
        // 3. 执⾏更新操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"⽤户密码更新失败！");
    }
    /**
     * 验证⽤户密码修改参数
     * ⽤户ID：userId ⾮空 ⽤户对象必须存在
     * 原始密码：oldPassword ⾮空 与数据库中密⽂密码保持⼀致
     * 新密码：newPassword ⾮空 与原始密码不能相同
     * 确认密码：confirmPassword ⾮空 与新密码保持⼀致
     * @param user
     * @param oldPwd
     * @param newPwd
     * @param confirmPassword
     */
    private void checkPasswordParams(User user, String oldPwd, String newPwd, String confirmPassword) {
        // 原始密码 ⾮空验证
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd),"请输⼊原始密码！");
        // 原始密码要与数据库中的密⽂密码保持⼀致
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPwd))),"原始密码不正确！");
        // 新密码 ⾮空校验
        AssertUtil.isTrue(StringUtils.isBlank(newPwd), "请输⼊新密码！");
        // 新密码与原始密码不能相同
        AssertUtil.isTrue(oldPwd.equals(newPwd), "新密码不能与原始密码相同！");
        // 确认密码 ⾮空校验
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword), "请输⼊确认密码！");
        // 新密码要与确认密码保持⼀致
        AssertUtil.isTrue(!(newPwd.equals(confirmPassword)),"新密码与确认密码不一致");
    }

    /**
     * 查询所有的销售⼈员
     * @return
     */
    public List<Map<String, Object>> queryAllSales() {
        return userMapper.queryAllSales();
    }

    /**
     * 添加⽤户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user) {
        // 1. 参数校验
        checkParams(user.getUserName(), user.getEmail(), user.getPhone(),null);
        // 2. 设置默认参数
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        // 3. 执⾏添加，判断结果
        AssertUtil.isTrue(userMapper.insertSelective(user) == null, "⽤户添加失败！");

        relationUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 用户角色关联
     *  添加操作
     *      原始角色不存在
     *          1. 不添加新的角色记录    不操作用户角色表
     *          2. 添加新的角色记录      给指定用户绑定相关的角色记录
     *
     *  更新操作
     *      原始角色不存在
     *          1. 不添加新的角色记录     不操作用户角色表
     *          2. 添加新的角色记录       给指定用户绑定相关的角色记录
     *      原始角色存在
     *          1. 添加新的角色记录       判断已有的角色记录不添加，添加没有的角色记录
     *          2. 清空所有的角色记录     删除用户绑定角色记录
     *          3. 移除部分角色记录       删除不存在的角色记录，存在的角色记录保留
     *          4. 移除部分角色，添加新的角色    删除不存在的角色记录，存在的角色记录保留，添加新的角色
     *
     *   如何进行角色分配？？？
     *      判断用户对应的角色记录存在，先将用户原有的角色记录删除，再添加新的角色记录
     *
     *  删除操作
     *      删除指定用户绑定的角色记录
     *
     *
     * 乐字节：专注线上IT培训
     * 答疑老师微信：lezijie
     * @param userId  用户ID
     * @param roleIds 角色ID
     * @return
     */
    private void relationUserRole(Integer userId, String roleIds) {

        // 通过用户ID查询角色记录
        Integer count = userRoleMapper.countUserRoleByUserId(userId);
        // 判断角色记录是否存在
        if (count > 0) {
            // 如果角色记录存在，则删除该用户对应的角色记录
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "用户角色分配失败！");
        }

        // 判断角色ID是否存在，如果存在，则添加该用户对应的角色记录
        if (StringUtils.isNotBlank(roleIds)) {
            // 将用户角色数据设置到集合中，执行批量添加
            List<UserRole> userRoleList = new ArrayList<>();
            // 将角色ID字符串转换成数组
            String[] roleIdsArray = roleIds.split(",");
            // 遍历数组，得到对应的用户角色对象，并设置到集合中
            for (String roleId : roleIdsArray) {
                UserRole userRole = new UserRole();
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setUserId(userId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                // 设置到集合中
                userRoleList.add(userRole);
            }
            // 批量添加用户角色记录
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList) != userRoleList.size(), "用户角色分配失败！");
        }

    }

    /**
     * 参数校验
     * @param userName
     * @param email
     * @param phone
     */
    private void checkParams(String userName, String email, String phone,Integer userId) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "⽤户名不能为空！");
        // 验证⽤户名是否存在
        User temp = userMapper.queryUserByName(userName);
        AssertUtil.isTrue(null != temp && !(temp.getId().equals(userId)), "该⽤户已存在！");
        AssertUtil.isTrue(StringUtils.isBlank(email), "请输⼊邮箱地址！");
        AssertUtil.isTrue(StringUtils.isBlank(phone), "请输⼊手机号！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "⼿机号码格式不正确！");
    }

    /**
     * 更新⽤户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        // 1. 参数校验
        // 通过id查询⽤户对象
        User temp = userMapper.selectByPrimaryKey(user.getId());
        // 判断对象是否存在
        AssertUtil.isTrue(temp == null, "待更新记录不存在！");
        // 验证参数
        checkParams(user.getUserName(),user.getEmail(),user.getPhone(), user.getId());
        // 2. 设置默认参数
        temp.setUpdateDate(new Date());
        // 3. 执⾏更新，判断结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "⽤户更新失败！");

        relationUserRole(user.getId(), user.getRoleIds());
    }


    /**
     * 用户删除
     *
     *
     * 乐字节：专注线上IT培训
     * 答疑老师微信：lezijie
     * @param ids
     * @return void
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByIds(Integer[] ids) {
        // 判断ids是否为空，长度是否大于0
        AssertUtil.isTrue(ids == null || ids.length == 0, "待删除记录不存在！");
        // 执行删除操作，判断受影响的行数
        AssertUtil.isTrue(userMapper.deleteBatch(ids) != ids.length, "用户删除失败！");

        // 遍历用户ID的数组
        for (Integer userId : ids) {
            // 通过用户ID查询对应的用户角色记录
            Integer count  = userRoleMapper.countUserRoleByUserId(userId);
            // 判断用户角色记录是否存在
            if (count > 0) {
                //  通过用户ID删除对应的用户角色记录
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "删除用户失败！");
            }
        }
    }
}
