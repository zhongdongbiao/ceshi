package utry.data.modular.baseConfig.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utry.core.base.controller.CommonController;
import utry.core.bo.PageBean;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.modular.baseConfig.listener.HumanResCoefListener;
import utry.data.modular.baseConfig.model.*;
import utry.data.modular.baseConfig.service.CcBaseConfigService;
import utry.data.util.ExcelUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;
import utry.data.util.SmsUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 话务中心配置管理类
 *
 * @author lidakai
 */
@Controller
@RequestMapping("/ccBaseConfig")
@Api(tags = "话务中心基础配置管理")
@SuppressWarnings("all")
public class CcBaseConfigController extends CommonController {

    @Resource
    private CcBaseConfigService ccBaseConfigService;
    @Resource
    private SmsUtils smsUtils;
    @Resource
    private SysConfServiceImpl sysConfService;

    /**核心目标*/
    @PostMapping("/selectCoreTargetByPage")
    @ApiOperation(value = "核心目标管理 - 分页查询")
    @ResponseBody
    public RetResult selectCoreTargetByPage(@RequestBody CcCoreTarget ccCoreTarget) {
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        //开启分页插件
        PageBean pageBean = this.getPageBean(ccCoreTarget.getCurrentPage(), ccCoreTarget.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<CcCoreTarget> list = ccBaseConfigService.selectCoreTarget(ccCoreTarget);
        PageInfo<CcCoreTarget> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/saveCoreTarget")
    @ApiOperation("核心目标管理 - 保存（新增/修改）")
    @ResponseBody
    public RetResult saveCoreTarget(@RequestBody CcCoreTarget ccCoreTarget) {
        return ccBaseConfigService.saveCoreTarget(ccCoreTarget);
    }

    @PostMapping("/delCoreTarget")
    @ApiOperation("核心目标管理 - 删除")
    @ResponseBody
    public RetResult delCoreTarget(@RequestBody CcCoreTarget ccCoreTarget) {
        return ccBaseConfigService.delCoreTarget(ccCoreTarget);
    }

    //队列目标管理（队列表需查询数策创建）
    // 添加 编辑
    /**@PostMapping("/saveQueueTarget")
    @ApiOperation("保存队列指标")
    @ResponseBody
    public RetResult saveQueueTarget(@RequestBody QueueTarget queueTarget) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return ccBaseConfigService.saveQueueTarget(queueTarget);
    }*/



    @PostMapping("/selectHumanResCoefByPage")
    @ApiOperation("人力资源系数 - 分页查询")
    @ResponseBody
    public RetResult selectHumanResCoefByPage(@RequestBody HumanResCoef humanResCoef) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(humanResCoef.getCurrentPage(), humanResCoef.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<HumanResCoef> list = ccBaseConfigService.selectHumanResCoefByPage();
        PageInfo<HumanResCoef> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }


    @PostMapping(value = "/exportHumanResCoef")
    @ApiOperation("人力资源系数 - 导出下载模板")
    @ResponseBody
    public void exportHumanResCoef(HttpServletResponse response) {
        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.createNewSheet();
        excelUtil.createRow(0);
        excelUtil.setColumnCell(0, "工号");
        excelUtil.setColumnCell(1, "人力系数");
        excelUtil.setColumnCell(2, "坐席编号");
        try {
            String codedFileName = java.net.URLEncoder.encode("人力系数导入模板.xls", "UTF-8");
            excelUtil.exportExcel(response, codedFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping(value = "/importHumanResCoef")
    @ApiOperation("人力资源系数 - 批量导入")
    @ResponseBody
    public RetResult importHumanResCoef(@RequestParam MultipartFile file) {
        List<HumanResCoef> ls = null;
        try {
            ls = EasyExcel.read(file.getInputStream(), HumanResCoef.class, new HumanResCoefListener()).sheet(0).doReadSync();
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("文件解析错误");
        }
        return ccBaseConfigService.importHumanResCoef(ls);
    }

    @PostMapping("/editHumanResCoef")
    @ApiOperation("人力资源系数 - 编辑")
    @ResponseBody
    public RetResult editHumanResCoef(@RequestBody HumanResCoef humanResCoef) {
        return ccBaseConfigService.editHumanResCoef(humanResCoef);
    }

    @PostMapping("/delHumanResCoef")
    @ApiOperation("人力资源系数 - 删除")
    @ResponseBody
    public RetResult delHumanResCoef(@RequestBody HumanResCoef humanResCoef) {
        return ccBaseConfigService.delHumanResCoef(humanResCoef);
    }


    /**回访/热线队列绑定*/
    @PostMapping("/batchSaveQueueBusiness")
    @ApiOperation(value = "回访/热线队列 - 批量保存",notes = "[{\"queueId\":\"12\",\"businessType\":\"1\"},{\"queueId\":\"13\",\"businessType\":\"1\"}]")
    @ResponseBody
    public RetResult batchSaveQueueBusiness(@RequestBody JSONArray jsonObject) {
        return ccBaseConfigService.saveQueueBusiness(jsonObject);
    }

    @PostMapping("/selectQueueBusinessByPage")
    @ApiOperation(value = "回访/热线队列 - 分页查询",notes = "{\"currentPage\":\"1\",\"pageSize\":\"10\"}")
    @ResponseBody
    public RetResult selectQueueBusinessByPage(@RequestBody JSONObject jsonObject) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(jsonObject.getString("currentPage"), jsonObject.getString("pageSize"));
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<JSONObject> queueBusiness = ccBaseConfigService.selectQueueBusinessByPage(jsonObject);
        PageInfo<JSONObject> pageInfo = new PageInfo<>(queueBusiness);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    /**坐席状态提醒*/
    @PostMapping("/selectAllSeatTimeOut")
    @ApiOperation("坐席状态 - 查询")
    @ResponseBody
    public RetResult selectAllSeatTimeOut() {
        return ccBaseConfigService.selectAllSeatTimeOut();
    }

    @PostMapping("/editSeatTimeOut")
    @ApiOperation("坐席状态 - 编辑")
    @ResponseBody
    public RetResult editSeatTimeOut(@RequestBody SeatStatusReminder seatStatusReminder) {
        return ccBaseConfigService.editSeatTimeOut(seatStatusReminder);
    }

    @PostMapping("/getDic")
    @ApiOperation(value = "字典项获取")
    @ResponseBody
    public RetResult getDic(@RequestParam("dicCode") String dicCode) {
        Map<String, String> inzCountryMap = smsUtils.getDictList(dicCode);
        return RetResponse.makeOKRsp(inzCountryMap);
    }

    @PostMapping("/getSeniorDic")
    @ApiOperation(value = "高级参数获取")
    @ResponseBody
    public RetResult getSeniorDic(@RequestParam("dicCode") String dicCode) {
        String seniorDic = sysConfService.getSystemConfig(dicCode, "100060");
        return RetResponse.makeOKRsp(seniorDic);
    }





}
