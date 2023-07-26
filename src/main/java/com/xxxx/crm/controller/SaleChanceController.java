package com.xxxx.crm.controller;

import com.xxxx.crm.annoation.RequirePermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    /**
     * 多条件分⻚查询营销机会
     *
     * @param
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    @RequirePermission(code = "101001")
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQueryquery,
                                                       Integer flag,HttpServletRequest request) {
        // 查询参数 flag=1 代表当前查询为开发计划数据，设置查询分配⼈参数
        if (null != flag && flag == 1) {
            //查询客户开发计划
            //设置分配状态
            saleChanceQueryquery.setState(StateStatus.STATED.getType());
            // 获取当前登录⽤户的ID
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQueryquery.setAssignMan(userId);
        }
        return saleChanceService.querySaleChanceByParams(saleChanceQueryquery);
    }

    @RequestMapping("/index")
    public String index() {
        return "saleChance/sale_chance";
    }

    /**
     * 添加营销机会数据
     *
     * @param request
     * @param saleChance
     * @return
     */
    @PostMapping ("add")
    @ResponseBody
    public ResultInfo addSaleChance(HttpServletRequest request, SaleChance saleChance) {
        // 从cookie中获取⽤户姓名
        String userName = CookieUtil.getCookieValue(request, "userName");
        // 设置营销机会的创建⼈
        saleChance.setCreateMan(userName);
        // 添加营销机会的数据
        saleChanceService.addSaleChance(saleChance);
        return success("营销机会数据添加成功！");
    }

    /**
     * 修改营销机会数据
     *
     *
     * @param saleChance
     * @return
     */
    @PostMapping ("update")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance) {
        // 添加营销机会的数据
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据修改成功！");
    }

    /**
     * 进入添加/修改营销机会数据页面
     * <p>
     * <p>
     * 乐字节：专注线上IT培训
     * 答疑老师微信：lezijie
     *
     * @param
     * @return java.lang.String
     */
    @RequestMapping("toSaleChancePage")
    public String toSaleChancePage(Integer saleChanceId, HttpServletRequest request) {
        // 判断saleChanceId是否为空
        if (saleChanceId != null) {
            // 通过ID查询营销机会数据
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(saleChanceId);
            // 将数据设置到请求域中
            request.setAttribute("saleChance",saleChance);
        }

        return "saleChance/add_update";
    }

    /**
     * 删除营销机会数据
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance (Integer[] ids) {
        // 删除营销机会的数据
        saleChanceService.deleteBatch(ids);
        return success("营销机会数据删除成功！");
    }

    @RequestMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){
        saleChanceService.updateSaleChanceDevResult(id,devResult);
        return success("开发状态更新成功");
    }
}