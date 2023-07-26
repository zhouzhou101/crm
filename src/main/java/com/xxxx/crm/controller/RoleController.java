package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.service.RoleService;
import com.xxxx.crm.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
    @Resource
    private RoleService roleService;

    @PostMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }

    @RequestMapping("index")
    public String index(){
        return "role/role";
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> userList(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }
    @RequestMapping("toAddOrUpdateRolePage")
    public String toAddOrUpdateRolePage(HttpServletRequest request,String id){
        if (id != null && id != "") {
            Role role = roleService.selectByPrimaryKey(Integer.parseInt(id));
            request.setAttribute("role",role);
        }
        return "role/add_update";
    }

    @PostMapping("add")
    @ResponseBody
    public ResultInfo addRole(Role role){
        roleService.addRole(role);
        return success("添加成功");
    }

    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("更新成功");
    }

    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer id){
        roleService.deleteRole(id);
        return success("删除成功");
    }

    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer roleId,HttpServletRequest request){
        request.setAttribute("roleId",roleId);
        return "role/grant";
    }


    /**
     * 角色授权
     *
     *
     * 乐字节：专注线上IT培训
     * 答疑老师微信：lezijie
     * @param roleId
     * @param mIds
     * @return com.xxxx.crm.base.ResultInfo
     */
    @PostMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer roleId, Integer[] mIds) {

        roleService.addGrant(mIds, roleId);

        return success("角色授权成功！");
    }
}
