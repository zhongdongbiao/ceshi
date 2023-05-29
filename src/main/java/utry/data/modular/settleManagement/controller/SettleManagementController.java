package utry.data.modular.settleManagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import utry.data.base.Page;
import utry.data.modular.settleManagement.dto.ConditionDto;
import utry.data.modular.settleManagement.service.SettleManagementService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/27 14:22
 */
@RestController
@RequestMapping("/settleManage")
@Api(tags = "结算管理数据统计")
public class SettleManagementController {

    @Resource
    private SettleManagementService settleManagementService;


    /**
     * 最大信号量，例如此处1，生成环境可以做成可配置项，通过注入方式进行注入
     * (16核测试环境信号量为3的时候性能最好)
     */
    private static final int MAX_SEMAPHORE = 6;
    /**
     * 获取信号量最大等待时间
     */
    private static int TIME_OUT = 70;

    /**
     * Semaphore主限流，全局就行
     */
    private static final Semaphore selectServiceType_SEMAPHORE = new Semaphore(MAX_SEMAPHORE);
    private static final Semaphore selectIndustrialBusiness_SEMAPHORE = new Semaphore(MAX_SEMAPHORE);
    private static final Semaphore selectFactoryServiceBreach_SEMAPHORE = new Semaphore(MAX_SEMAPHORE);
    private static final Semaphore selectCostAnalysis_SEMAPHORE = new Semaphore(MAX_SEMAPHORE);
    private static final Semaphore selectSettleSummary_SEMAPHORE = new Semaphore(MAX_SEMAPHORE);
    private static final Semaphore selectStatementMonitor_SEMAPHORE = new Semaphore(MAX_SEMAPHORE);

    @ApiOperation(value = "查询结算单汇总", notes = "查询结算单汇总")
    @RequestMapping(value = "/selectSettleSummary", method = RequestMethod.POST)
    public RetResult selectSettleSummary(@RequestBody ConditionDto conditionDto) throws InterruptedException {
        if (!(selectSettleSummary_SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))) {
            return RetResponse.makeErrRsp("数据库压力太大了，请您稍后再试");
        }
        List<Map<String, Object>> message;
        try {
            message = settleManagementService.selectSettleSummary(conditionDto);
        } finally {
            selectSettleSummary_SEMAPHORE.release();
        }
        return RetResponse.makeOKRsp(message);
    }

    /**
     * 限流
     * @param page
     * @return
     */
    @ApiOperation(value = "查询服务类型-费用分析", notes = "查询服务类型-费用分析")
    @RequestMapping(value = "/selectServiceType", method = RequestMethod.POST)
    public RetResult selectServiceType(@RequestBody Page<ConditionDto> page) throws InterruptedException {
        if (!(selectServiceType_SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))) {
            return RetResponse.makeErrRsp("数据库压力太大了，请您稍后再试");
        }
        Object message;
        try {
            message = settleManagementService.selectServiceType(page.getPageData(), page.getPage(), page.getSize());
        } finally {
            selectServiceType_SEMAPHORE.release();
        }
        return RetResponse.makeOKRsp(message);
    }

    /**
     * 限流
     * @param page
     * @return
     */
    @ApiOperation(value = "查询费用分析", notes = "查询费用分析")
    @RequestMapping(value = "/selectCostAnalysis", method = RequestMethod.POST)
    public RetResult selectCostAnalysis(@RequestBody Page<ConditionDto> page) throws InterruptedException {
        if (!(selectCostAnalysis_SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))) {
            return RetResponse.makeErrRsp("数据库压力太大了，请您稍后再试");
        }
        Object message;
        try {
            message = settleManagementService.selectCostAnalysis(page.getPageData(), page.getPage(), page.getSize());
        } finally {
            selectCostAnalysis_SEMAPHORE.release();
        }
        return RetResponse.makeOKRsp(message);
    }

    /**
     * 限流
     * @param page
     * @return
     */
    @ApiOperation(value = "工业/营业费用分析", notes = "工业/营业费用分析")
    @RequestMapping(value = "/selectIndustrialBusiness", method = RequestMethod.POST)
    public RetResult selectIndustrialBusiness(@RequestBody Page<ConditionDto> page) throws InterruptedException {
        if (!(selectIndustrialBusiness_SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))) {
            return RetResponse.makeErrRsp("数据库压力太大了，请您稍后再试");
        }
        Object message;
        try {
            message = settleManagementService.selectIndustrialBusiness(page.getPageData(), page.getPage(), page.getSize());
        } finally {
            selectIndustrialBusiness_SEMAPHORE.release();
        }
        return RetResponse.makeOKRsp(message);
    }

    /**
     * 需限流
     * @param page
     * @return
     */
    @ApiOperation(value = "工厂别服务违约", notes = "工厂别服务违约")
    @RequestMapping(value = "/selectFactoryServiceBreach", method = RequestMethod.POST)
    public RetResult selectFactoryServiceBreach(@RequestBody Page<ConditionDto> page) throws InterruptedException {
        if (!(selectFactoryServiceBreach_SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))) {
            return RetResponse.makeErrRsp("数据库压力太大了，请您稍后再试");
        }
        Object message;
        try {
            message = settleManagementService.selectFactoryServiceBreach(page.getPageData(), page.getPage(), page.getSize());
        } finally {
            selectFactoryServiceBreach_SEMAPHORE.release();
        }
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "结算单流程监控", notes = "结算单流程监控")
    @RequestMapping(value = "/selectStatementMonitor", method = RequestMethod.POST)
    public RetResult selectStatementMonitor(@RequestBody ConditionDto conditionDto) throws InterruptedException {
        if (!(selectStatementMonitor_SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))) {
            return RetResponse.makeErrRsp("数据库压力太大了，请您稍后再试");
        }
        List<Map<String, Object>> message;
        try {
            message = settleManagementService.selectStatementMonitor(conditionDto);
        } finally {
            selectStatementMonitor_SEMAPHORE.release();
        }
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "获取所有的结算对象", notes = "获取所有的结算对象")
    @RequestMapping(value = "/getSettleObjects", method = RequestMethod.GET)
    public RetResult getSettleObjects() {
        List<Map<String, Object>> message = settleManagementService.getSettleObjects();
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "获取日期范围", notes = "获取日期范围")
    @RequestMapping(value = "/getDateRangeList", method = RequestMethod.POST)
    public RetResult getDateRangeList(@RequestBody ConditionDto conditionDto) {
        List<String> dateList = settleManagementService.getDateRangeList(conditionDto);
        return RetResponse.makeOKRsp(dateList);
    }

    @ApiOperation(value = "服务类型-费用分析导出", notes = "结算数据导出")
    @RequestMapping(value = "/serviceTypeExport", method = RequestMethod.POST)
    public void serviceTypeExport(HttpServletResponse response, @RequestBody ConditionDto conditionDto) throws Exception {
        try {
            settleManagementService.serviceTypeExport(response,conditionDto);
        }catch (Exception e) {
            throw new Exception("导出失败");
        }
    }

    @ApiOperation(value = "费用分析导出", notes = "费用分析导出")
    @RequestMapping(value = "/costAnalysisExport", method = RequestMethod.POST)
    public void costAnalysisExport(HttpServletResponse response, @RequestBody ConditionDto conditionDto) throws Exception {
        try {
            settleManagementService.costAnalysisExport(response,conditionDto);
        }catch (Exception e) {
            throw new Exception("导出失败");
        }
    }

    @ApiOperation(value = "工厂/营业费用分析导出", notes = "工厂/营业费用分析")
    @RequestMapping(value = "/industrialBusinessExport", method = RequestMethod.POST)
    public void industrialBusinessExport(HttpServletResponse response, @RequestBody ConditionDto conditionDto) throws Exception {
        try {
            settleManagementService.industrialBusinessExport(response,conditionDto);
        }catch (Exception e) {
            throw new Exception("导出失败");
        }
    }

    @ApiOperation(value = "工厂别服务违约导出", notes = "工厂别服务违约导出")
    @RequestMapping(value = "/factoryServiceBreachExport", method = RequestMethod.POST)
    public void factoryServiceBreachExport(HttpServletResponse response, @RequestBody ConditionDto conditionDto) throws Exception {
        try {
            settleManagementService.factoryServiceBreachExport(response,conditionDto);
        }catch (Exception e) {
            throw new Exception("导出失败");
        }
    }

}
