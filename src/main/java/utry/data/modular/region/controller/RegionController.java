package utry.data.modular.region.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.common.BusinessException;
import utry.core.common.LoginInfoParams;
import utry.core.log.UtryLogger;
import utry.core.log.UtryLoggerFactory;
import utry.data.modular.region.controller.dto.RegionComplaintDto;
import utry.data.modular.region.service.RegionService;
import utry.data.modular.technicalQuality.controller.TechnicalQualityController;
import utry.data.modular.technicalQuality.utils.Column;
import utry.data.modular.technicalQuality.utils.ExcelTool;
import utry.data.modular.technicalQuality.utils.TitleEntity;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Semaphore;


/**
 * 大区服务业务Controller
 * @author wanlei
 */
@RestController
@RequestMapping("region")
@Api(tags = "大区服务业务")
public class RegionController extends CommonController {
    /**
     * 最大信号量，例如此处1，生成环境可以做成可配置项，通过注入方式进行注入
     */
    private static final int MAX_SEMAPHORE = 12;

    private static final UtryLogger LOGGER = UtryLoggerFactory.getLogger(TechnicalQualityController.class);
    /**
     * 获取信号量最大等待时间
     */
    private static int TIME_OUT = 20;

    /**
     * Semaphore主限流，全局就行
     */
    private static final Semaphore SEMAPHORE = new Semaphore(MAX_SEMAPHORE, false);
    @Resource
    private RegionService regionService;

    @ApiOperation(value = "30分钟及时预约率", notes = "30分钟及时预约率")
    @PostMapping("/timely")
    public RetResult timely(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.timely(map);
    }
    @ApiOperation(value = "30分钟及时预约率图表", notes = "30分钟及时预约率图表")
    @PostMapping("/timelyMap")
    public RetResult timelyMap(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.timelyMap(map);
    }
    @ApiOperation(value = "30分钟及时预约率图表", notes = "30分钟及时预约率图表")
    @PostMapping("/timelyPie")
    public RetResult timelyPie(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.timelyPie(map);
    }
    @ApiOperation(value = "预约准时上门率", notes = "预约准时上门率")
    @PostMapping("/punctuality")
    public RetResult punctuality(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.punctuality(map);
    }
    @ApiOperation(value = "预约准时上门率图表", notes = "预约准时上门率图表")
    @PostMapping("/punctualityMap")
    public RetResult punctualityMap(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.punctualityMap(map);
    }
    @ApiOperation(value = "TAT平均服务完成时长", notes = "TAT平均服务完成时长")
    @PostMapping("/average")
    public RetResult average(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.average(map);
    }
    @ApiOperation(value = "TAT平均服务完成时长饼图", notes = "TAT平均服务完成时长饼图")
    @PostMapping("/averagePie")
    public RetResult averagePie(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.averagePie(map);
    }
    @ApiOperation(value = "TAT平均服务完成时长图表", notes = "TAT平均服务完成时长图表")
    @PostMapping("/averageMap")
    public RetResult averageMap(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.averageMap(map);
    }
    @ApiOperation(value = "投诉7天解决率", notes = "投诉7天解决率")
    @PostMapping("/solve")
    public RetResult solve(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.solve(map);
    }
    @ApiOperation(value = "投诉7天解决率图表", notes = "投诉7天解决率图表")
    @PostMapping("/solveMap")
    public RetResult solveMap(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.solveMap(map);
    }
    @ApiOperation(value = "一次修复率", notes = "一次修复率")
    @PostMapping("/repair")
    public RetResult repair(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.repair(map);
    }
    @ApiOperation(value = "品类一次修复率柱图", notes = "品类一次修复率柱图")
    @PostMapping("/repairBar")
    public RetResult repairBar(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.repairBar(map);
    }
    @ApiOperation(value = "一次修复率图表", notes = "一次修复率图表")
    @PostMapping("/repairMap")
    public RetResult repairMap(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.repairMap(map);
    }
    @ApiOperation(value = "2天维修达成率", notes = "2天维修达成率")
    @PostMapping("/maintain1")
    public RetResult maintain(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.maintain(map);
    }
    @ApiOperation(value = "2天维修达成率图表", notes = "2天维修达成率图表")
    @PostMapping("/maintainMap1")
    public RetResult maintainMap(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.maintainMap(map);
    }
    @ApiOperation(value = "N+1投诉解决方案提交率", notes = "N+1投诉解决方案提交率")
    @PostMapping("/scheme")
    public RetResult scheme(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.scheme(map);
    }
    @ApiOperation(value = "N+1投诉解决方案提交率图表", notes = "N+1投诉解决方案提交率图表")
    @PostMapping("/schemeMap")
    public RetResult schemeMap(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.schemeMap(map);
    }
    @ApiOperation(value = "排名", notes = "排名")
    @PostMapping("/ranking")
    public RetResult ranking(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.ranking(map);
    }
    @ApiOperation(value = "地图数据", notes = "地图数据")
    @PostMapping("/mapData")
    public RetResult mapData(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.mapData(map);
    }
    @ApiOperation(value = "大区管理列表", notes = "大区管理列表")
    @PostMapping("/regionManage")
    public RetResult regionManage(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.regionManage(map);
    }
    @ApiOperation(value = "工程师管理列表", notes = "工程师管理列表")
    @PostMapping("/engineerManage")
    public RetResult engineerManage(@RequestBody Map map) throws InterruptedException{
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        /*RetResult rr = null;
        // 使用阻塞Acquire，如果获取不到就快速返回失败
        if (!(SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))) {
            return RetResponse.makeErrRsp("当前系统繁忙，请您稍后再试！");
        }
        try {
            rr = regionService.engineerManage(map);
            // 执行你的业务逻辑
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 一定要释放，否则导致接口假死无法处理请求
            SEMAPHORE.release();
        }*/
        return regionService.engineerManage(map);
    }
    @ApiOperation(value = "服务门店管理列表", notes = "服务门店管理列表")
    @PostMapping("/storeManage")
    public RetResult storeManage(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.storeManage(map);
    }
    @ApiOperation(value = "TAB页值", notes = "TAB页值")
    @PostMapping("/tabValue")
    public RetResult tabValue(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.tabValue(map);
    }
    @ApiOperation(value = "上门服务异常监控", notes = "上门服务异常监控")
    @PostMapping("/visitMonitoring")
    //public RetResult<Object> visitMonitoring(@RequestBody RegionVisitMonitoringRequest request){
    public RetResult<Object> visitMonitoring(@RequestBody Map map) throws InterruptedException {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        //return regionService.visitMonitoring(request);
        /*RetResult rr = null;
        // 使用阻塞Acquire，如果获取不到就快速返回失败
        if (!(SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))) {
            return RetResponse.makeErrRsp("当前系统繁忙，请您稍后再试！");
        }
        try {
            rr = regionService.visitMonitoring1(map);
            // 执行你的业务逻辑
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 一定要释放，否则导致接口假死无法处理请求
            SEMAPHORE.release();
        }*/
        return regionService.visitMonitoring1(map);
    }
    @ApiOperation(value = "送修服务异常监控", notes = "送修服务异常监控")
    @PostMapping("/giveMonitoring")
    //public RetResult<Object> giveMonitoring(@RequestBody RegionVisitMonitoringRequest request){
    public RetResult<Object> giveMonitoring(@RequestBody Map map) throws InterruptedException {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        //return regionService.giveMonitoring(request);
        /*RetResult rr = null;
        // 使用阻塞Acquire，如果获取不到就快速返回失败
        if (!(SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))) {
            return RetResponse.makeErrRsp("当前系统繁忙，请您稍后再试！");
        }
        try {
            rr = regionService.giveMonitoring1(map);
            // 执行你的业务逻辑
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 一定要释放，否则导致接口假死无法处理请求
            SEMAPHORE.release();
        }*/
        return regionService.giveMonitoring1(map);
    }
    @ApiOperation(value = "寄修服务异常监控", notes = "寄修服务异常监控")
    @PostMapping("/sendMonitoring")
    //public RetResult<Object> sendMonitoring(@RequestBody RegionVisitMonitoringRequest request){
    public RetResult<Object> sendMonitoring(@RequestBody Map map) throws InterruptedException {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        //return regionService.sendMonitoring(request);
        /*RetResult rr = null;
        // 使用阻塞Acquire，如果获取不到就快速返回失败
        if (!(SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))) {
            return RetResponse.makeErrRsp("当前系统繁忙，请您稍后再试！");
        }
        try {
            rr = regionService.sendMonitoring1(map);
            // 执行你的业务逻辑
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 一定要释放，否则导致接口假死无法处理请求
            SEMAPHORE.release();
        }*/
        return regionService.sendMonitoring1(map);
    }
    @ApiOperation(value = "派工单详情", notes = "派工单详情")
    @PostMapping("/dispatchingDetail")
    public RetResult dispatchingDetail(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.dispatchingDetail(map);
    }
    @ApiOperation(value = "流转历史", notes = "流转历史")
    @PostMapping("/transferInformation")
    public RetResult transferInformation(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.transferInformation(map);
    }
    @ApiOperation(value = "改约率", notes = "改约率")
    @PostMapping("/reschedule")
    public RetResult reschedule(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.reschedule(map);
    }
    @ApiOperation(value = "详情列表", notes = "详情列表")
    @PostMapping("/allList")
    public RetResult allList(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.allList(map);
    }
    @ApiOperation(value = "更新率", notes = "更新率")
    @PostMapping("/updateAll")
    public RetResult updateAll(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.updateAll(map);
    }
    @ApiOperation(value = "更新TAT时长", notes = "更新率")
    @PostMapping("/updateTAT")
    public RetResult updateTAT(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.updateTAT(map);
    }
    @ApiOperation(value = "补偿更新二次上门标签", notes = "更新率")
    @PostMapping("/updateTwoUp")
    public RetResult updateTwoUp(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.updateTwoUp(map);
    }

    @ApiOperation(value = "批量设置大区", notes = "批量设置大区")
    @PostMapping("/regionAll")
    public RetResult regionAll(){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.regionAll();
    }

    @ApiOperation(value = "处理乱序派工单", notes = "处理乱序派工单")
    @PostMapping("/error")
    public RetResult error(){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try {
            regionService.error();
            return RetResponse.makeOKRsp();
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }

    @ApiOperation(value = "投诉类型下拉框", notes = "投诉类型下拉框")
    @RequestMapping(value = "/selectComplaintType", method = RequestMethod.POST)
    public RetResult selectComplaintType(@RequestBody RegionComplaintDto complaintDto) {
        List<String> message = regionService.selectComplaintType(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "内部投诉7天解决率", notes = "内部投诉7天解决率")
    @RequestMapping(value = "/innerSevenDaySolveRate", method = RequestMethod.POST)
    public RetResult innerSevenDaySolveRate(@RequestBody RegionComplaintDto complaintDto) {
        Map<String,Object> message = regionService.innerSevenDaySolveRate(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "N+1解决方案及时提交率", notes = "N+1解决方案及时提交率")
    @RequestMapping(value = "/complaintSolveSubmissionRate", method = RequestMethod.POST)
    public RetResult complaintSolveSubmissionRate(@RequestBody RegionComplaintDto complaintDto) {
        Map<String,Object> message = regionService.complaintSolveSubmissionRate(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    /*大屏接口*/
    @ApiOperation(value = "全国各省TAT4天达成率", notes = "全国各省TAT4天达成率")
    @PostMapping("/subApi/provinceTAT4AchievementRate")
    public RetResult provinceTAT4AchievementRate(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.provinceTAT4AchievementRate(map);
    }
    @ApiOperation(value = "全年各月30分钟及时预约率", notes = "全年各月30分钟及时预约率")
    @PostMapping("/subApi/thirtyMinuteAppointmentsRate")
    public RetResult thirtyMinuteAppointmentsRate(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.thirtyMinuteAppointmentsRate(map);
    }
    @ApiOperation(value = "全年各月首次预约上门准时率", notes = "全年各月首次预约上门准时率")
    @PostMapping("/subApi/firstOnDoorAppointmentsRate")
    public RetResult firstOnDoorAppointmentsRate(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.firstOnDoorAppointmentsRate(map);
    }
    @ApiOperation(value = "全年各月服务完成 1~4TAT达成率", notes = "全年各月服务完成1~4TAT达成率")
    @PostMapping("/subApi/tatNServiceCompletionRate")
    public RetResult tatNServiceCompletionRate(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.tatNServiceCompletionRate(map);
    }
    @ApiOperation(value = "全年各月投诉N+n方案确认情况", notes = "全年各月投诉N+n方案确认情况")
    @PostMapping("/subApi/nDaysComplaintHandleData")
    public RetResult nDaysComplaintHandleData(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.nDaysComplaintHandleData(map);
    }
    @ApiOperation(value = "直管业绩综合排行榜", notes = "直管业绩综合排行榜")
    @PostMapping("/subApi/directManagementAreaScore")
    public RetResult directManagementAreaScore(@RequestBody Map map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        //LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.directManagementAreaScore(map);
    }

    @PostMapping("/exportRegionList")
    @ApiOperation("导出---大区管理")
    @ResponseBody
    public void exportEngineerList(HttpServletResponse response,@RequestBody Map map) {
        List<Map> list = regionService.exportEngineerList(map);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("accountingArea", "大区名称");
        headerMap.put("adminName", "区管姓名");
        headerMap.put("accountingCenter", "核算中心");
        headerMap.put("amount", "期间单量");
        headerMap.put("timelyPercentage", "30分钟预约及时率");
        headerMap.put("firstPunctualityPercentage", "首次预约准时率");
        headerMap.put("averagePercentage", "TAT4天达成率");
        headerMap.put("avgDay", "TAT平均服务完成时长");
        headerMap.put("solvePercentage", "投诉七天解决率");
        headerMap.put("repairPercentage", "一次性修复率");
        headerMap.put("totalScore", "总分");
        List<TitleEntity> titleList = new ArrayList<>();
        String pid = "-1";
        int i = 0;
        for(Map.Entry<String, String> entry:headerMap.entrySet()){
            TitleEntity entity = new TitleEntity(String.valueOf(i), pid, entry.getValue(), entry.getKey());
            titleList.add(entity);
            i++;
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取操作时间
        String operationTime = dateFormat.format(new Date());
        // 添加行内数据
        ExcelTool excelTool = new ExcelTool("大区管理"+operationTime+".xlsx",20,20, null, "大区管理列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, list,true);
        } catch (Exception e) {
            LOGGER.error("大区管理导出失败", e);
            e.printStackTrace();
            throw new BusinessException("导出失败");
        }
    }

}
