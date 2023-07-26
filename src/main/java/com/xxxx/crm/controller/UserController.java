package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;

    /**
     * ⽤户登录
     * @param userName
     * @param userPwd
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public ResultInfo userLogin (String userName, String userPwd) {
        ResultInfo resultInfo = new ResultInfo();
        UserModel userModel = userService.userLogin(userName, userPwd);
        resultInfo.setResult(userModel);
        return resultInfo;
//        // 通过 try catch 捕获 Service 层抛出的异常
//        try {
//            // 调⽤Service层的登录⽅法，得到返回的⽤户对象
//            UserModel userModel = userService.userLogin(userName, userPwd);
//            /**
//             * 登录成功后，有两种处理：
//             * 1. 将⽤户的登录信息存⼊ Session （ 问题：重启服务器，Session 失效，客户端需要重复登录 ）
//             * 2. 将⽤户信息返回给客户端，由客户端（Cookie）保存
//             */
//            // 将返回的UserModel对象设置到 ResultInfo 对象中
//            resultInfo.setResult(userModel);
//        } catch (ParamsException e) { // ⾃定义异常
//            e.printStackTrace();
//            // 设置状态码和提示信息
//            resultInfo.setCode(e.getCode());
//            resultInfo.setMsg(e.getMsg());
//        } catch (Exception e) {
//            e.printStackTrace();
//            resultInfo.setCode(500);
//            resultInfo.setMsg("操作失败！");
//        }

    }

    /**
     * ⽤户密码修改
     * @param request
     * @param oldPwd
     * @param newPwd
     * @param confirmPassword
     * @return
     */
    @PostMapping("/updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,String oldPwd,
                                         String newPwd,String confirmPassword){
        ResultInfo resultInfo = new ResultInfo();
        // 获取userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 调⽤Service层的密码修改⽅法
        userService.updateUserPassword(userId,oldPwd,newPwd,confirmPassword);
        return resultInfo;
        // 通过 try catch 捕获 Service 层抛出的异常
//        try {
//            // 获取userId
//            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
//            // 调⽤Service层的密码修改⽅法
//            userService.updateUserPassword(userId,oldPwd,newPwd,confirmPassword);
//        }catch (ParamsException p){
//            p.printStackTrace();
//            // 设置状态码和提示信息
//            resultInfo.setCode(p.getCode());
//            resultInfo.setMsg(p.getMsg());
//        }catch (Exception e){
//            e.printStackTrace();
//            resultInfo.setCode(500);
//            resultInfo.setMsg("操作失败！");
//        }

    }

    /**
     * 修改密码
     * @return
     */
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    /**
     * 查询所有的销售⼈员
     * @return
     */
    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales() {
        return userService.queryAllSales();
    }

    /**
     * 多条件查询⽤户数据
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryUserByParams(UserQuery userQuery) {
        return userService.queryByParamsForTable(userQuery);
    }

    /**
     * 进⼊⽤户⻚⾯
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    /**
     * 添加⽤户
     * @param user
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo saveUser(User user) {
        userService.saveUser(user);
        return success("⽤户添加成功！");
    }

    /**
     * 打开添加或修改用户的页面
     *
     *
     * 乐字节：专注线上IT培训
     * 答疑老师微信：lezijie
     * @param
     * @return java.lang.String
     */
    @RequestMapping("toAddOrUpdateUserPage")
    public String toAddOrUpdateUserPage(Integer id, HttpServletRequest request) {

        // 判断id是否为空，不为空表示更新操作，查询用户对象
        if (id != null) {
            // 通过id查询用户对象
            User user = userService.selectByPrimaryKey(id);
            // 将数据设置到请求域中
            request.setAttribute("userInfo",user);
        }

        return "user/add_update";
    }

    /**
     * 更新⽤户
     * @param user
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user) {
        userService.updateUser(user);
        return success("⽤户更新成功！");
    }

    /**
     * 用户删除
     *
     *
     * 乐字节：专注线上IT培训
     * 答疑老师微信：lezijie
     * @param ids
     * @return com.xxxx.crm.base.ResultInfo
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids) {

        userService.deleteByIds(ids);

        return success("用户删除成功！");
    }
}

