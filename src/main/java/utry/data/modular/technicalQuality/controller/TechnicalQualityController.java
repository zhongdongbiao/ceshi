package utry.data.modular.technicalQuality.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.bo.PageBean;
import utry.core.common.BusinessException;
import utry.core.common.LoginInfoParams;
import utry.core.log.UtryLogger;
import utry.core.log.UtryLoggerFactory;
import utry.core.sysConfig.ISysConfService;
import utry.data.modular.baseConfig.service.QualityFeedbackService;
import utry.data.modular.technicalQuality.dto.*;
import utry.data.modular.technicalQuality.service.TechnicalQualityService;
import utry.data.modular.technicalQuality.utils.Column;
import utry.data.modular.technicalQuality.utils.ExcelTool;
import utry.data.modular.technicalQuality.utils.TitleEntity;
import utry.data.util.DateConditionUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;
import utry.data.util.TimeTaskUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 技术品质Controller
 *
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Controller
@RequestMapping("/technicalQuality")
@Api(tags = "技术品质Controller")
public class TechnicalQualityController extends CommonController {
    private static final UtryLogger LOGGER = UtryLoggerFactory.getLogger(TechnicalQualityController.class);

    @Autowired
    ISysConfService iSysConfService;
    @Resource
    private TechnicalQualityService technicalQualityService;
    @Resource
    private QualityFeedbackService qualityFeedbackService;

    @PostMapping("/saveOption")
    @ApiOperation("保存筛选条件")
    @ResponseBody
    public RetResult saveOption(@RequestBody JSONObject jsonObject) {
        technicalQualityService.saveOption(jsonObject);
        return RetResponse.makeOKRsp();
    }

    @PostMapping("/selectOptions")
    @ApiOperation("查询所有选项")
    @ResponseBody
    public RetResult selectOptions(@RequestBody JSONObject jsonObject) {
        List<JSONObject> list = technicalQualityService.selectOptions(jsonObject);
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/deleteOption")
    @ApiOperation("删除选项")
    @ResponseBody
    public RetResult deleteOption(@RequestBody List<JSONObject> jsonObjectList) {
        technicalQualityService.deleteOption(jsonObjectList);
        return RetResponse.makeOKRsp();
    }

    @PostMapping("/setFirstOption")
    @ApiOperation("设置首选项")
    @ResponseBody
    public RetResult setFirstOption(@RequestBody List<JSONObject> jsonObjectList) {
        technicalQualityService.setFirstOption(jsonObjectList);
        return RetResponse.makeOKRsp();
    }

    @PostMapping("/selectCategoryTree")
    @ApiOperation("查询全部品类-类型-型号树")
    @ResponseBody
    public RetResult selectCategoryTree() {
        List<TreeDTO> list = technicalQualityService.selectCategoryTree();
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/selectDistrictTemplate")
    @ApiOperation("查询大区模板")
    @ResponseBody
    public RetResult selectDistrictTemplate() {
        List<DistrictTemplateDTO> list = technicalQualityService.selectDistrictTemplate();
        return RetResponse.makeOKRsp(list);
    }

/*    @PostMapping("/selectUserInfo")
    @ApiOperation("查询符合类型条件的担当核心详情")
    @ResponseBody
    public RetResult selectUserInfo(@RequestBody TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        List<TechnicalQualityUserDTO> uesrList = technicalQualityService.selectUserInfo(technicalQualityQueryDTO);
        return RetResponse.makeOKRsp(uesrList);
    }*/

    @PostMapping("/getUsers")
    @ApiOperation("获取担当")
    @ResponseBody
    public RetResult getUsers(@RequestBody TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        TechnicalQualityQueryDTO uesrList = technicalQualityService.getUsers(technicalQualityQueryDTO);
        return RetResponse.makeOKRsp(uesrList);
    }

    @PostMapping("/getUserInfo")
    @ApiOperation("获取担当详情")
    @ResponseBody
    public RetResult getUserInfo(@RequestBody TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        TechnicalQualityUserDTO uesrList = technicalQualityService.getUserInfo(technicalQualityQueryDTO);
        return RetResponse.makeOKRsp(uesrList);
    }

    @PostMapping("/selectTargetInfo")
    @ApiOperation("查询符合类型条件的核心详情")
    @ResponseBody
    public RetResult selectTargetInfo(@RequestBody TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        List<TechnicalQualityUserDTO> userList = technicalQualityService.selectTargetInfo(technicalQualityQueryDTO);
        return RetResponse.makeOKRsp(userList);
    }

    @PostMapping("/selectTargetInfoCopy")
    @ApiOperation("查询符合类型条件的核心详情")
    @ResponseBody
    public RetResult selectTargetInfoCopy(@RequestBody TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        List<TechnicalQualityUserDTO> userList = technicalQualityService.selectTargetInfoCopy(technicalQualityQueryDTO);
        return RetResponse.makeOKRsp(userList);
    }

    @PostMapping("/selectRepairRate")
    @ApiOperation("一次性修复率/类型别/大区别")
    @ResponseBody
    public RetResult selectRepairRate(@RequestBody TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        List<RepairRateHistogramDTO> engineerDTOS = technicalQualityService.selectRepairRate(technicalQualityQueryDTO);
        return RetResponse.makeOKRsp(engineerDTOS);
    }

    @PostMapping("/selectEngineer/{currentPage}/{pageSize}")
    @ApiOperation("工程师管理-带筛选")
    @ResponseBody
    public RetResult selectEngineer(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                    @PathVariable @ApiParam(value = "当前页数") String currentPage, @RequestBody EngineerQueryDTO engineerQueryDTO) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<EngineerDTO> engineerQueryDTOS = technicalQualityService.selectEngineer(engineerQueryDTO);
        PageInfo<EngineerDTO> pageInfo = new PageInfo<>(engineerQueryDTOS);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/selectEngineerCategory")
    @ApiOperation("工程师管理-品类")
    @ResponseBody
    public RetResult selectEngineerCategory(@RequestBody EngineerDTO engineerDTO) {
        List<EngineerDTO> engineerQueryDTOS = technicalQualityService.selectEngineerCategory(engineerDTO);
        return RetResponse.makeOKRsp(engineerQueryDTOS);
    }

    @PostMapping("/selectEngineerType")
    @ApiOperation("工程师管理-类型")
    @ResponseBody
    public RetResult selectEngineerType(@RequestBody EngineerDTO engineerDTO) {
        List<EngineerDTO> engineerQueryDTOS = technicalQualityService.selectEngineerType(engineerDTO);
        return RetResponse.makeOKRsp(engineerQueryDTOS);
    }

    @PostMapping("/selectAccount")
    @ApiOperation("获取当前登录用户")
    @ResponseBody
    public RetResult selectEngineerType() {
        Map<String, Object> map = new HashMap<>();
        map.put("accountId", LoginInfoParams.getAccountID());
        map.put("loginName", LoginInfoParams.getLoginName());
        map.put("realName", LoginInfoParams.getRealName());
        return RetResponse.makeOKRsp(map);
    }

    @PostMapping("/selectThreshold")
    @ApiOperation("查询最新月份一次性修复率目标")
    @ResponseBody
    public RetResult selectThreshold() {
        return RetResponse.makeOKRsp(technicalQualityService.selectThreshold());
    }

    @PostMapping("/selectThisMonth")
    @ApiOperation("获取最新月目标/担当/非担当")
    @ResponseBody
    public RetResult selectThisMonth(@RequestBody UserTypeQueryDTO userTypeQueryDTO) {
        return RetResponse.makeOKRsp(technicalQualityService.selectThisMonth(userTypeQueryDTO));
    }

    @PostMapping("/selectType")
    @ApiOperation("查询人员类型/查询全部人员类型")
    @ResponseBody
    public RetResult selectType(@RequestBody UserTypeQueryDTO userTypeQueryDTO) {
        return RetResponse.makeOKRsp(technicalQualityService.selectType(userTypeQueryDTO.getAccountId()));
    }

    @PostMapping("/selectApprovalDuration/{currentPage}/{pageSize}")
    @ApiOperation("品质单审核作业时长页面列表")
    @ResponseBody
    public RetResult selectApprovalDuration(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                            @PathVariable @ApiParam(value = "当前页数") String currentPage, @RequestBody ApprovalDurationQueryDTO approvalDurationQueryDTO) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<ApprovalDurationDTO> approvalDurationDTOS = technicalQualityService.selectApprovalDuration(approvalDurationQueryDTO);
        PageInfo<ApprovalDurationDTO> pageInfo = new PageInfo<>(approvalDurationDTOS);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/selectApprovalDurationTime")
    @ApiOperation("品质单审核作业时长页面时间轴")
    @ResponseBody
    public RetResult selectApprovalDurationTime(@RequestBody ApprovalDurationQueryDTO approvalDurationQueryDTO) {
        ApprovalDurationTimeDTO approvalDurationTimeDTO = technicalQualityService.selectApprovalDurationTime(approvalDurationQueryDTO);
        return RetResponse.makeOKRsp(approvalDurationTimeDTO);
    }

    @PostMapping("/selectDetailApprovalDurationTime")
    @ApiOperation("品质单审核作业时长详情时间轴")
    @ResponseBody
    public RetResult selectDetailApprovalDurationTime(@RequestBody DetailTimeDTO detailTimeDTO) {
        ApprovalDurationTimeDTO approvalDurationTimeDTO = technicalQualityService.selectDetailApprovalDurationTime(detailTimeDTO);
        return RetResponse.makeOKRsp(approvalDurationTimeDTO);
    }

    @PostMapping("/getApprovalDurationAvgTime")
    @ApiOperation("品质单审核平均作业时长获取")
    @ResponseBody
    public RetResult getApprovalDurationAvgTime(@RequestBody CalculateDTO calculateDTO) {
        return RetResponse.makeOKRsp(technicalQualityService.getApprovalDurationAvgTime(calculateDTO));
    }

    @PostMapping("/selectCompletionRate/{currentPage}/{pageSize}")
    @ApiOperation("新品上市资料七天内完备率页面")
    @ResponseBody
    public RetResult selectCompletionRate(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                          @PathVariable @ApiParam(value = "当前页数") String currentPage,@RequestBody CompletionRateQueryDTO completionRateQueryDTO) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<CompletionRateDTO> list = technicalQualityService.selectCompletionRate(completionRateQueryDTO);
        PageInfo<CompletionRateDTO> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/selectCompletionRateByCategory/{currentPage}/{pageSize}")
    @ApiOperation("新品上市资料七天内完备率页面---型号")
    @ResponseBody
    public RetResult selectCompletionRateByCategory(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                                    @PathVariable @ApiParam(value = "当前页数") String currentPage,@RequestBody CompletionRateByCategoryQueryDTO completionRateByCategoryQueryDTO) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<CompletionRateByCategoryDTO> completionRateByCategoryDTOS = technicalQualityService.selectCompletionRateByCategory(completionRateByCategoryQueryDTO);
        PageInfo<CompletionRateByCategoryDTO> pageInfo = new PageInfo<>(completionRateByCategoryDTOS);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/selectRepairRateTime")
    @ApiOperation("一次性修复率时间轴")
    @ResponseBody
    public RetResult selectRepairRateTime(@RequestBody RepairRateQueryDTO repairRateQueryDTO) {
        RepairRateTimeDTO repairRateTimeDTO = technicalQualityService.selectRepairRateTime(repairRateQueryDTO);
        return RetResponse.makeOKRsp(repairRateTimeDTO);
    }

    @PostMapping("/selectServiceList/{currentPage}/{pageSize}")
    @ApiOperation("服务单列表")
    @ResponseBody
    public RetResult selectServiceList(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                       @PathVariable @ApiParam(value = "当前页数") String currentPage, @RequestBody RepairRateQueryDTO repairRateQueryDTO) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<RepairRateDTO> repairRateDTOS = technicalQualityService.selectServiceList(repairRateQueryDTO);
        PageInfo<RepairRateDTO> pageInfo = new PageInfo<>(repairRateDTOS);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/selectRepairRateLineChart")
    @ApiOperation("一次性修复率折线图")
    @ResponseBody
    public RetResult selectRepairRateLineChart(@RequestBody LineChartQueryDTO lineChartQueryDTO) {
        Map<String, List<LineChartDTO>> map = technicalQualityService.selectRepairRateLineChart(lineChartQueryDTO);
        return RetResponse.makeOKRsp(map);
    }

    @PostMapping("/selectPartPieChart/{currentPage}/{pageSize}")
    @ApiOperation("故障分析---未筛选列表")
    @ResponseBody
    public RetResult selectPartPieChart(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                        @PathVariable @ApiParam(value = "当前页数") String currentPage, @RequestBody PieChartQueryDTO pieChartQueryDTO) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<PieChartDTO> faultCauseDTO = technicalQualityService.selectRepairRatePieChart(pieChartQueryDTO);
        PageInfo<PieChartDTO> pageInfo = new PageInfo<>(faultCauseDTO);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/selectPie")
    @ApiOperation("故障分析饼图")
    @ResponseBody
    public RetResult selectPie(@RequestBody PieChartQueryDTO pieChartQueryDTO) {
        List<FaultCauseDTO> faultCauseDTO = technicalQualityService.selectPie(pieChartQueryDTO);
        return RetResponse.makeOKRsp(faultCauseDTO);
    }

    @PostMapping("/selectPartByFaultCause/{currentPage}/{pageSize}")
    @ApiOperation("故障分析饼图---筛选")
    @ResponseBody
    public RetResult selectPartByFaultCause(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                            @PathVariable @ApiParam(value = "当前页数") String currentPage, @RequestBody PieChartQueryDTO pieChartQueryDTO) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<PieChartDTO> list = technicalQualityService.selectPartByFaultCause(pieChartQueryDTO);
        PageInfo<PieChartDTO> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/exportEngineerList")
    @ApiOperation("导出---工程师管理")
    @ResponseBody
    public void exportEngineerList(HttpServletResponse response, @RequestBody EngineerQueryDTO engineerQueryDTO) {
        if(StringUtils.isEmpty(engineerQueryDTO.getAllExport())){
            engineerQueryDTO.setAllExport("1");
        }
        if("1".equals(engineerQueryDTO.getAllExport())){
            ConditionDTO conditionDTO = new ConditionDTO();
            conditionDTO.setSort("");
            engineerQueryDTO.setEngineerId(null);
            engineerQueryDTO.setEngineerName(null);
            engineerQueryDTO.setEngineerId(null);
            engineerQueryDTO.setStoreName(null);
            engineerQueryDTO.setAdminName(null);
            engineerQueryDTO.setRepairRate(conditionDTO);
            engineerQueryDTO.setTotal(conditionDTO);
            engineerQueryDTO.setEligible(conditionDTO);
            engineerQueryDTO.setUnEligible(conditionDTO);
        }
        List<EngineerExportDTO> list = technicalQualityService.exportEngineerList(engineerQueryDTO);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("engineerName", "工程师名称");
        headerMap.put("productCategory", "产品品类");
        headerMap.put("productType", "产品类型");
        headerMap.put("engineerId", "工程师编号");
        headerMap.put("storeName", "所属门店");
        headerMap.put("adminName", "对应区管");
        headerMap.put("repairRate", "一次性修复率");
        headerMap.put("total", "服务总量");
        headerMap.put("eligible", "达标总量");
        headerMap.put("unEligible", "未达标总量");
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
        // 设置第7列为整数，第8列为保留两位小数
        Map<Integer, String> otherFormatMap = new HashMap<>();
//        otherFormatMap.put(7, "0.00_ ");
        otherFormatMap.put(7, "0_ ");
        otherFormatMap.put(8, "0_ ");
        otherFormatMap.put(9, "0_ ");
        // 添加行内数据
        ExcelTool excelTool = new ExcelTool("工程师列表"+operationTime+".xls",20,20, otherFormatMap, "工程师列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, list,true);
        } catch (Exception e) {
            LOGGER.error("工程师管理导出失败", e);
            throw new BusinessException("导出失败");
        }
    }

    @PostMapping("/exportRepairRateByYear")
    @ApiOperation("导出---全年一次性修复率")
    @ResponseBody
    public void exportRepairRateByYear(HttpServletResponse response, @RequestBody ExportConditionDTO exportConditionDTO) {
        List<ExportRepairRateDTO> list = technicalQualityService.exportRepairRateByYear(exportConditionDTO);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("productTypeCode", "产品类型代码");
        headerMap.put("productCategoryCode", "产品品类代码");
        headerMap.put("productCategory", "产品品类");
        headerMap.put("productType", "产品类型");
        headerMap.put("total", "全年服务总量");
        headerMap.put("eligible", "全年达标总量");
        headerMap.put("repairRate", "全年累计一次性修复率");
        headerMap.put("map", "一次修复率月别推移信息");
        Map<String, String> bodyMap = new LinkedHashMap<>();
        bodyMap.put("one", "1");
        bodyMap.put("two", "2");
        bodyMap.put("three", "3");
        bodyMap.put("four", "4");
        bodyMap.put("five", "5");
        bodyMap.put("six", "6");
        bodyMap.put("seven", "7");
        bodyMap.put("eight", "8");
        bodyMap.put("nine", "9");
        bodyMap.put("ten", "10");
        bodyMap.put("eleven", "11");
        bodyMap.put("twelve", "12");
        //设置一级表头
        List<TitleEntity> titleList = new ArrayList<>();
        String pid = "-1";
        int i = 0;
        for(Map.Entry<String, String> entry:headerMap.entrySet()){
            TitleEntity entity = new TitleEntity(String.valueOf(i), pid, entry.getValue(), entry.getKey());
            titleList.add(entity);
            i++;
        }
        for(Map.Entry<String, String> entry:bodyMap.entrySet()){
            TitleEntity entity = new TitleEntity(String.valueOf(i), "7", entry.getValue(), entry.getKey());
            titleList.add(entity);
            i++;
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取操作时间
        String operationTime = dateFormat.format(new Date());
        // 设置第7列为整数，第8列为保留两位小数
        Map<Integer, String> otherFormatMap = new HashMap<>();
//        otherFormatMap.put(7, "0.00_ ");
        otherFormatMap.put(4, "0_ ");
        otherFormatMap.put(5, "0_ ");
        // 添加行内数据
        ExcelTool excelTool = new ExcelTool("产品类型别一次性修复率"+operationTime+".xls",20,20, otherFormatMap, "产品类型别一次性修复率");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, list,true);
        } catch (Exception e) {
            LOGGER.error("产品类型别一次性修复率导出失败", e);
            throw new BusinessException("导出失败");
        }
    }

    @PostMapping("/exportCompletionRateCategoryList")
    @ApiOperation("导出---七天完备率产品品类别")
    @ResponseBody
    public void exportCompletionRateList(HttpServletResponse response, @RequestBody CompletionRateQueryDTO completionRateQueryDTO) {
        if(StringUtils.isEmpty(completionRateQueryDTO.getAllExport())){
            completionRateQueryDTO.setAllExport("1");
        }
        if("1".equals(completionRateQueryDTO.getAllExport())){
            ConditionDTO conditionDTO = new ConditionDTO();
            conditionDTO.setSort("");
            completionRateQueryDTO.setCompletionRate(conditionDTO);
            completionRateQueryDTO.setName(null);
            completionRateQueryDTO.setProductCategory(null);
            completionRateQueryDTO.setCount(conditionDTO);
        }
        List<CompletionRateDTO> list = technicalQualityService.exportCompletionRate(completionRateQueryDTO);
        // 固定表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("productCategory", "产品品类");
        headerMap.put("name", "担当");
        headerMap.put("completionRate", "新品上市资料7天内完备率");
        headerMap.put("count", "关联产品量");
        List<TitleEntity> titleList = new ArrayList<>();
        String pid = "-1";
        int i = 0;
        for(Map.Entry<String, String> entry:headerMap.entrySet()){
            TitleEntity entity = new TitleEntity(String.valueOf(i), pid, entry.getValue(), entry.getKey());
            titleList.add(entity);
            i++;
        }
        Map<Integer, String> otherFormatMap = new HashMap<>();
        otherFormatMap.put(3, "0_ ");
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取操作时间
        String operationTime = dateFormat.format(new Date());
        ExcelTool excelTool = new ExcelTool("新品上市资料7天内完备率列表"+operationTime+".xls",20,20, otherFormatMap, "新品上市资料7天内完备率列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, list,true);
        } catch (Exception e) {
            LOGGER.error("新品上市资料7天内完备率列表导出失败", e);
            throw new BusinessException("导出失败");
        }
    }

    @PostMapping("/exportCompletionRateModelList")
    @ApiOperation("导出---七天完备率产品型号")
    @ResponseBody
    public void exportCompletionRateModelList(HttpServletResponse response, @RequestBody CompletionRateByCategoryQueryDTO completionRateByCategoryQueryDTO) {
        if(StringUtils.isEmpty(completionRateByCategoryQueryDTO.getAllExport())){
            completionRateByCategoryQueryDTO.setAllExport("1");
        }
        if("1".equals(completionRateByCategoryQueryDTO.getAllExport())){
            DateConditionUtil dateConditionUtil = new DateConditionUtil();
            dateConditionUtil.setSort("");
            completionRateByCategoryQueryDTO.setProductModel(null);
            completionRateByCategoryQueryDTO.setProductType(null);
            completionRateByCategoryQueryDTO.setName(null);
            completionRateByCategoryQueryDTO.setListingDate(dateConditionUtil);
            completionRateByCategoryQueryDTO.setManualTime(dateConditionUtil);
            completionRateByCategoryQueryDTO.setServiceManualTime(dateConditionUtil);
        }
        List<CompletionRateByCategoryDTO> list = technicalQualityService.exportCompletionRateByCategory(completionRateByCategoryQueryDTO);
        // 固定表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("productModel", "产品型号");
        headerMap.put("productType", "产品类型");
        headerMap.put("name", "担当");
        headerMap.put("listingDate", "新品发布日期");
        headerMap.put("manualTime", "新品说明书上传日期");
        headerMap.put("serviceManualTime", "新品维修手册上传日期");
        headerMap.put("flag", "达标标志");
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
        ExcelTool excelTool = new ExcelTool("新品上市资料7天内产品类型别完备率列表"+operationTime+".xls",20,20, null, "新品上市资料7天内产品类型别完备率列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, list,true);
        } catch (Exception e) {
            LOGGER.error("新品上市资料7天内产品类型别完备率列表导出失败", e);
            throw new BusinessException("导出失败");
        }
    }

    @PostMapping("/exportApprovalDuration")
    @ApiOperation("导出---品质审核单")
    @ResponseBody
    public void exportServiceList(HttpServletResponse response, @RequestBody ApprovalDurationQueryDTO approvalDurationQueryDTO) {
        if(StringUtils.isEmpty(approvalDurationQueryDTO.getAllExport())){
            approvalDurationQueryDTO.setAllExport("1");
        }
        if("1".equals(approvalDurationQueryDTO.getAllExport())){
            ConditionDTO conditionDTO = new ConditionDTO();
            conditionDTO.setSort("");
            DateConditionUtil dateConditionUtil = new DateConditionUtil();
            dateConditionUtil.setSort("");
            approvalDurationQueryDTO.setProductModel(null);
            approvalDurationQueryDTO.setIfException(null);
            approvalDurationQueryDTO.setSystemState(null);
            approvalDurationQueryDTO.setManageNumber(null);
            approvalDurationQueryDTO.setDocumentDate(dateConditionUtil);
            approvalDurationQueryDTO.setServiceNumber(null);
            approvalDurationQueryDTO.setServiceStoreName(null);
            approvalDurationQueryDTO.setProductCategory(null);
            approvalDurationQueryDTO.setProductSeries(null);
            approvalDurationQueryDTO.setFactoryName(null);
            approvalDurationQueryDTO.setAuditDuration(conditionDTO);
        }
        List<ApprovalDurationDTO> list = technicalQualityService.exportApprovalDuration(approvalDurationQueryDTO);
        // 固定表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("systemState", "状态");
        headerMap.put("manageNumber", "管理编号");
        headerMap.put("documentDate", "单据日期");
        headerMap.put("serviceNumber", "服务单号");
        headerMap.put("serviceStoreName", "服务店名称");
        headerMap.put("productCategory", "产品品类");
        headerMap.put("productSeries", "产品系列");
        headerMap.put("productModel", "产品型号");
        headerMap.put("factoryName", "工厂名称");
        headerMap.put("auditDuration", "审核时长");
        headerMap.put("manufacturingDate", "制造日期");
        headerMap.put("purchaseDate", "购买日期");
        headerMap.put("productSymptom", "产品故障现象");
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
        ExcelTool excelTool = new ExcelTool("品质审核单列表"+operationTime+".xls",20,20, null, "品质审核单列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, list,true);
        } catch (Exception e) {
            LOGGER.error("品质审核单列表导出失败", e);
            throw new BusinessException("导出失败");
        }
    }

    @PostMapping("/exportServiceList")
    @ApiOperation("导出---服务单列表")
    @ResponseBody
    public void exportServiceList(HttpServletResponse response, @RequestBody RepairRateQueryDTO repairRateQueryDTO) {
        if(StringUtils.isEmpty(repairRateQueryDTO.getAllExport())){
            repairRateQueryDTO.setAllExport("1");
        }
        if("1".equals(repairRateQueryDTO.getAllExport())){
            ConditionDTO conditionDTO = new ConditionDTO();
            conditionDTO.setSort("");
            DateConditionUtil dateConditionUtil = new DateConditionUtil();
            dateConditionUtil.setSort("");
            repairRateQueryDTO.setStoreName(null);
            repairRateQueryDTO.setIfException(null);
            repairRateQueryDTO.setDispatchingOrder(null);
            repairRateQueryDTO.setEngineerName(null);
            repairRateQueryDTO.setEngineerId(null);
            repairRateQueryDTO.setDispatchingTime(dateConditionUtil);
            repairRateQueryDTO.setAppointmentTime(dateConditionUtil);
            repairRateQueryDTO.setFinishTime(conditionDTO);
            repairRateQueryDTO.setTurnoverTime(conditionDTO);
            repairRateQueryDTO.setVisitsNumber(conditionDTO);
            repairRateQueryDTO.setAccountingArea(null);
            repairRateQueryDTO.setArea(null);
            repairRateQueryDTO.setServiceType(null);
            repairRateQueryDTO.setSystemState(null);
            repairRateQueryDTO.setProductCategory(null);
        }
        List<RepairRateDTO> list = technicalQualityService.exportServiceList(repairRateQueryDTO);
        // 固定表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("storeName", "服务店名称");
        headerMap.put("engineerName", "工程师姓名");
        headerMap.put("engineerId", "工程师编号");
        headerMap.put("productCategory", "产品品类");
        headerMap.put("dispatchingOrder", "派工单号");
        headerMap.put("dispatchingTime", "派工时间");
        headerMap.put("appointmentTime", "预约时间");
        headerMap.put("visitsTime", "上门时间");
        headerMap.put("finishTime", "完成时间");
        headerMap.put("visitsNumber", "维修次数");
        headerMap.put("accountingArea", "所属大区");
        headerMap.put("area", "地区");
        headerMap.put("serviceType", "服务类型");
        headerMap.put("systemState", "当前状态");
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
        ExcelTool excelTool = new ExcelTool("服务单列表"+operationTime+".xls",20,20, null, "服务单列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, list,true);
        } catch (Exception e) {
            LOGGER.error("服务单列表导出失败", e);
            throw new BusinessException("导出失败");
        }
    }
}
