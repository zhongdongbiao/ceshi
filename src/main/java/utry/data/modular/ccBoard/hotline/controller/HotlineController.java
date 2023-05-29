package utry.data.modular.ccBoard.hotline.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import utry.core.common.LoginInfoParams;
import utry.data.base.Page;
import utry.data.modular.ccBoard.hotline.service.HotlineService;
import utry.data.modular.ccBoard.hotline.vo.HotlineVo;
import utry.data.modular.ccBoard.visit.dto.ExportConditionDto;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Description 热线数据
 * @Author zh
 * @Date 2022/11/2 13:21
 */
@RestController
@RequestMapping("/hotline")
@Api(tags = "热线数据")
public class HotlineController {

    @Resource
    private HotlineService hotlineService;

    @ApiOperation(value = "热线项目数据概览", notes = "热线项目数据概览")
    @RequestMapping(value = "/hotlineData", method = RequestMethod.POST)
    public RetResult hotlineData(@RequestBody Map<String,Object> map) {
        Map<String,Object> message = hotlineService.hotlineData(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "热线项目服务类型", notes = "热线项目服务类型")
    @RequestMapping(value = "/hotlineServiceType", method = RequestMethod.POST)
    public RetResult hotlineServiceType(@RequestBody Map<String,Object> map) {
        List<Map<String,Object>> message = hotlineService.hotlineServiceType(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "已受理工单未跟进", notes = "已受理工单未跟进")
    @RequestMapping(value = "/acceptWorkFollow", method = RequestMethod.POST)
    public RetResult acceptWorkFollow(@RequestBody Map<String,Object> map) {
        List<Map<String,Object>> message = hotlineService.acceptWorkFollow(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "客户评价", notes = "客户评价")
    @RequestMapping(value = "/customerEvaluation", method = RequestMethod.POST)
    public RetResult customerEvaluation(@RequestBody Map<String,Object> map) {
        List<Map<String,Object>> message = hotlineService.customerEvaluation(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "投诉分析", notes = "投诉分析")
    @RequestMapping(value = "/complaintsAnalysis", method = RequestMethod.POST)
    public RetResult complaintsAnalysis(@RequestBody Map<String,Object> map) {
        List<Map<String,Object>> message = hotlineService.complaintsAnalysis(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "话务明细", notes = "话务明细")
    @RequestMapping(value = "/callDetail", method = RequestMethod.POST)
    public RetResult callDetail(@RequestBody Page<Map<String,Object>> page) {
        Object message = hotlineService.callDetail(page.getPageData(),page.getPage(),page.getSize());
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "热线服务单详情", notes = "热线服务单详情")
    @RequestMapping(value = "/hotlineServiceDetail", method = RequestMethod.POST)
    public RetResult hotlineServiceDetail(@RequestBody Map<String,Object> map) {
        try {
            Map<String,Object> message = hotlineService.hotlineServiceDetail(map);
            return RetResponse.makeOKRsp(message);
        }catch (Exception e) {
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }

    @ApiOperation(value = "未完成处理单", notes = "未完成处理单")
    @RequestMapping(value = "/noFinishOrder", method = RequestMethod.POST)
    public RetResult noFinishOrder(@RequestBody Map<String,Object> map) {
        try {
            Object message = hotlineService.noFinishOrder(map);
            return RetResponse.makeOKRsp(message);
        }catch (Exception e) {
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }

    @ApiOperation(value = "热线图表导出", notes = "热线服务单导出")
    @RequestMapping(value = "/hotlineChartExport", method = RequestMethod.POST)
    public RetResult hotlineChartExport(HttpServletResponse response, @RequestBody Map<String,Object> map) {
        try {
            hotlineService.hotlineChartExport(response,map);
            return RetResponse.makeOKRsp();
        }catch (Exception e) {
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }

    @ApiOperation(value = "话务明细导出", notes = "话务明细导出")
    @RequestMapping(value = "/callDetailExport", method = RequestMethod.POST)
    public RetResult callDetailExport(HttpServletResponse response, @RequestBody Map<String,Object> map) {
        try {
            hotlineService.callDetailExport(response,map);
            return RetResponse.makeOKRsp();
        }catch (Exception e) {
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }

    @ApiOperation(value = "已受理未跟进列表", notes = "已受理未跟进列表")
    @RequestMapping(value = "/acceptWorkFollowList", method = RequestMethod.POST)
    public RetResult acceptWorkFollowList(@RequestBody Page<Map<String,Object>> page) {
        Object message = hotlineService.acceptWorkFollowList(page.getPageData(),page.getPage(),page.getSize());
        return RetResponse.makeOKRsp(message);
    }

}
