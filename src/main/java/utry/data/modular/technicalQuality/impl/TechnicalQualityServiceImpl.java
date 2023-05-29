package utry.data.modular.technicalQuality.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import utry.core.common.BusinessException;
import utry.core.log.UtryLogger;
import utry.core.log.UtryLoggerFactory;
import utry.data.modular.baseConfig.dao.QualityFeedbackConfigDao;
import utry.data.modular.baseConfig.dto.IndicatorUserDTO;
import utry.data.modular.baseConfig.dto.QualityFeedbackDTO;
import utry.data.modular.technicalQuality.dao.TechnicalQualityCopyDao;
import utry.data.modular.technicalQuality.dto.*;
import utry.data.modular.technicalQuality.dao.TechnicalQualityDao;
import utry.data.modular.technicalQuality.service.TechnicalQualityService;
import utry.data.modular.technicalQuality.utils.LineChartDateUtil;
import utry.data.util.BeautifyTimeUtil;
import utry.data.util.TimeTaskUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 技术品质实现类
 *
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class TechnicalQualityServiceImpl implements TechnicalQualityService {

    private static final UtryLogger LOGGER = UtryLoggerFactory.getLogger(TimeTaskUtil.class);
    @Resource
    private RedisTemplate<String,?> redisTemplate;
    @Resource
    private TechnicalQualityDao technicalQualityDao;
    @Resource
    private QualityFeedbackConfigDao qualityFeedbackConfigDao;
    @Resource
    private TechnicalQualityCopyDao technicalQualityCopyDao;
    /**
     * 保存筛选条件
     */
    @Override
    public void saveOption(JSONObject jsonObject) {
        String key;
        ListOperations<String, JSONObject> listOps = (ListOperations<String, JSONObject>) redisTemplate.opsForList();
        try {
            key = jsonObject.getString("businessCode") + ":" + jsonObject.getString("accountId");
        }catch(Exception e){
            throw new BusinessException("获取不到当前登录用户");
        }
        // 单条配置，存到队尾
        listOps.rightPush(key, jsonObject);
//        // 如果key没设置缓存 设置永久有效
//        if(redisUtils.getExpire(key)!=0){
//            redisTemplate.expire(key, -1, TimeUnit.SECONDS);
//        }
    }

    /**
     * 查询全部选项
     */
    @Override
    public List<JSONObject> selectOptions(JSONObject jsonObject) {
        ListOperations<String, JSONObject> listOps = (ListOperations<String, JSONObject>) redisTemplate.opsForList();
        String key = jsonObject.getString("businessCode")+":"+jsonObject.getString("accountId");
        // 取出所有list配置
        return listOps.range(key, 0, -1);
    }

    /**
     * 设置首选项
     */
    @Override
    public void setFirstOption(List<JSONObject> jsonObjectList) {
        String key = jsonObjectList.get(0).getString("businessCode")+":"+ jsonObjectList.get(0).getString("accountId");
        redisTemplate.delete(key);
        ListOperations<String, JSONObject> listOps = (ListOperations<String, JSONObject>) redisTemplate.opsForList();
        listOps.rightPushAll(key,jsonObjectList);
    }

    /**
     * 删除选项
     */
    @Override
    public void deleteOption(List<JSONObject> jsonObjectList) {
        ListOperations<String, JSONObject> listOps = (ListOperations<String, JSONObject>) redisTemplate.opsForList();
        String key = jsonObjectList.get(0).getString("businessCode")+":"+ jsonObjectList.get(0).getString("accountId");
        long size = listOps.size(key);
        redisTemplate.delete(key);
        if(size==1&&jsonObjectList.size()==1){
            return;
        }
        listOps.rightPushAll(key,jsonObjectList);
    }

    /***
    * @Description: 品质单审核作业时长页面列表
    * @Param: approvalDurationQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.ApprovalDurationDTO>
    * @Author: WJ
    * @Date: 2022-05-11
    */
    @Override
    public List<ApprovalDurationDTO> selectApprovalDuration(ApprovalDurationQueryDTO approvalDurationQueryDTO) {
        List<ConditionDTO> list = new ArrayList<>();
        ConditionDTO auditDuration = approvalDurationQueryDTO.getAuditDuration();
        list.add(auditDuration);
        //注入参数校验
        paramsValid(list);
        List<ApprovalDurationDTO> approvalDurationDTOS = technicalQualityDao.selectApprovalDuration(approvalDurationQueryDTO);
        // 开启mybatis缓存后，repairRateDTOS会被缓存，因此不能原地修改，建一个新list
        List<ApprovalDurationDTO> formatList = new ArrayList<>();
        for(ApprovalDurationDTO oriDTO: approvalDurationDTOS){
            ApprovalDurationDTO formatDTO = new ApprovalDurationDTO();
            BeanUtils.copyProperties(oriDTO, formatDTO);
            formatDTO.setAuditDuration(BeautifyTimeUtil.secondToHour(Double.parseDouble(formatDTO.getAuditDuration())));
            formatList.add(formatDTO);
        }
        return formatList;
    }

    /***
     * @Description: 导出品质单审核作业时长页面列表
     * @Param: approvalDurationQueryDTO
     * @return: java.util.List<utry.data.modular.technicalQuality.dto.ApprovalDurationDTO>
     * @Author: WJ
     * @Date: 2022-05-11
     */
    @Override
    public List<ApprovalDurationDTO> exportApprovalDuration(ApprovalDurationQueryDTO approvalDurationQueryDTO) {
        List<ConditionDTO> list = new ArrayList<>();
        ConditionDTO auditDuration = approvalDurationQueryDTO.getAuditDuration();
        list.add(auditDuration);
        //注入参数校验
        paramsValid(list);
        List<ApprovalDurationDTO> approvalDurationDTOS = technicalQualityCopyDao.selectApprovalDuration(approvalDurationQueryDTO);
        if(CollectionUtils.isNotEmpty(approvalDurationDTOS)) {
            approvalDurationDTOS.stream().filter(item -> {
                item.setAuditDuration(BeautifyTimeUtil.secondToHour(Double.parseDouble(item.getAuditDuration())));
                return true;
            }).collect(Collectors.toList());;
        }
        return approvalDurationDTOS;
    }

    /***
    * @Description: 品质单审核作业时长页面时间轴
    * @Param: approvalDurationQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.ApprovalDurationTimeDTO>
    * @Author: WJ
    * @Date: 2022-05-11
    */
    @Override
    public ApprovalDurationTimeDTO selectApprovalDurationTime(ApprovalDurationQueryDTO approvalDurationQueryDTO) {
        ApprovalDurationTimeDTO approvalDurationTimeDTO = technicalQualityDao.selectApprovalDurationTime(approvalDurationQueryDTO);
//        String reviewTime = BeautifyTimeUtil.secondToDate(Double.parseDouble(approvalDurationTimeDTO.getReviewTime()));
//        String auditTime = BeautifyTimeUtil.secondToDate(Double.parseDouble(approvalDurationTimeDTO.getAuditTime()));
//        String closeOrderTime = BeautifyTimeUtil.secondToDate(Double.parseDouble(approvalDurationTimeDTO.getCloseOrderTime()));
//        if (StringUtils.isEmpty(reviewTime)||"0.00".equals(reviewTime)){
//            reviewTime = "0分钟";
//        }
//        if (StringUtils.isEmpty(auditTime)||"0.00".equals(auditTime)){
//            auditTime = "0分钟";
//        }
//        if (StringUtils.isEmpty(closeOrderTime)||"0.00".equals(closeOrderTime)){
//            closeOrderTime = "0分钟";
//        }
//        approvalDurationTimeDTO.setReviewTime(reviewTime);
//        approvalDurationTimeDTO.setAuditTime(auditTime);
//        approvalDurationTimeDTO.setCloseOrderTime(closeOrderTime);
        return approvalDurationTimeDTO;
    }

    /***
    * @Description: 新品上市资料七天内完备率页面
    * @Param: completionRateQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.CompletionRateDTO>
    * @Author: WJ
    * @Date: 2022-05-13
    */
    @Override
    public List<CompletionRateDTO> selectCompletionRate(CompletionRateQueryDTO completionRateQueryDTO) {
        //参数校验
        List<ConditionDTO> list = new ArrayList<>();
        ConditionDTO completionRate = completionRateQueryDTO.getCompletionRate();
        ConditionDTO count = completionRateQueryDTO.getCount();
        list.add(completionRate);
        list.add(count);
        //注入参数校验
        paramsValid(list);
        List<CompletionRateDTO> completionRateDTOS = technicalQualityDao.selectCompletionRate(completionRateQueryDTO);
        return completionRateDTOS;
    }

    /***
     * @Description: 导出新品上市资料七天内完备率页面
     * @Param: completionRateQueryDTO
     * @return: java.util.List<utry.data.modular.technicalQuality.dto.CompletionRateDTO>
     * @Author: WJ
     * @Date: 2022-05-13
     */
    @Override
    public List<CompletionRateDTO> exportCompletionRate(CompletionRateQueryDTO completionRateQueryDTO) {
        //参数校验
        List<ConditionDTO> list = new ArrayList<>();
        ConditionDTO completionRate = completionRateQueryDTO.getCompletionRate();
        ConditionDTO count = completionRateQueryDTO.getCount();
        list.add(completionRate);
        list.add(count);
        //注入参数校验
        paramsValid(list);
        List<CompletionRateDTO> completionRateDTOS = technicalQualityCopyDao.selectCompletionRate(completionRateQueryDTO);
        return completionRateDTOS;
    }

    /***
    * @Description: 一次性修复率时间轴
    * @Param: repairRateQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.RepairRateTimeDTO>
    * @Author: WJ
    * @Date: 2022-05-14
    */
    @Override
    public RepairRateTimeDTO selectRepairRateTime(RepairRateQueryDTO repairRateQueryDTO) {
        if (StringUtils.isNotEmpty(repairRateQueryDTO.getStartTime())&&StringUtils.isNotEmpty(repairRateQueryDTO.getEndTime())) {
            repairRateQueryDTO.setStartTime(repairRateQueryDTO.getStartTime() + " 00:00:00");
            repairRateQueryDTO.setEndTime(repairRateQueryDTO.getEndTime() + " 23:59:59");
        }
        String avg;
        List<Map<String,Object>> list = technicalQualityDao.selectRepairRateTime(repairRateQueryDTO);
       /* List<Map<String,Object>> pendingList = technicalQualityDao.selectPendingOrder(repairRateQueryDTO);
        if (CollectionUtils.isNotEmpty(pendingList)){
            list.addAll(pendingList);
        }*/
        RepairRateTimeDTO repairRateTimeDTO = new RepairRateTimeDTO();
        if(CollectionUtils.isNotEmpty(list)){
            for (Map<String,Object> map:list){
                if(map!=null){
                   String codeName = String.valueOf(map.get("nodeName"));
                   String count = String.valueOf(map.get("count"));
                   switch (codeName){
                       case "上门":
                           repairRateTimeDTO.setVisitsNum(count);
                           break;
                       case "挂单":
                           repairRateTimeDTO.setPendingOrderNum(count);
                           break;
                       case "解挂":
                           repairRateTimeDTO.setCancelOrderNum(count);
                           break;
                       case "服务完成":
                           avg = String.valueOf(map.get("avg"));
                           if(StringUtils.isNotEmpty(avg)&&(!"null".equals(avg))) {
                               avg = BeautifyTimeUtil.secondToDate(Double.parseDouble(avg));
                               repairRateTimeDTO.setTurnoverTime(avg);
                           }
                           repairRateTimeDTO.setServiceFinishNum(count);
                           break;
                       default:
                           break;
                   }
                }
            }
        }
        if (StringUtils.isEmpty(repairRateTimeDTO.getTurnoverTime())||"0.00".equals(repairRateTimeDTO.getTurnoverTime())){
            repairRateTimeDTO.setTurnoverTime("0分钟");
        }
        if (StringUtils.isEmpty(repairRateTimeDTO.getCancelOrderNum())){
            repairRateTimeDTO.setCancelOrderNum("0");
        }
        if (StringUtils.isEmpty(repairRateTimeDTO.getServiceFinishNum())){
            repairRateTimeDTO.setServiceFinishNum("0");
        }
        if (StringUtils.isEmpty(repairRateTimeDTO.getPendingOrderNum())){
            repairRateTimeDTO.setPendingOrderNum("0");
        }
        if (StringUtils.isEmpty(repairRateTimeDTO.getVisitsNum())){
            repairRateTimeDTO.setVisitsNum("0");
        }
        return repairRateTimeDTO;
    }

    /***
    * @Description: 通过品类新品上市资料七天内完备率页面
    * @Param: completionRateByCategoryQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.CompletionRateByCategoryDTO>
    * @Author: WJ
    * @Date: 2022-05-16
    */
    @Override
    public List<CompletionRateByCategoryDTO> selectCompletionRateByCategory(CompletionRateByCategoryQueryDTO completionRateByCategoryQueryDTO) {
        if(StringUtils.isEmpty(completionRateByCategoryQueryDTO.getProductCategoryCode())){
            throw new BusinessException("参数校验异常");
        }
        return technicalQualityDao.selectCompletionRateByCategory(completionRateByCategoryQueryDTO);
    }

    /***
     * @Description: 导出通过品类新品上市资料七天内完备率页面
     * @Param: completionRateByCategoryQueryDTO
     * @return: java.util.List<utry.data.modular.technicalQuality.dto.CompletionRateByCategoryDTO>
     * @Author: WJ
     * @Date: 2022-05-16
     */
    @Override
    public List<CompletionRateByCategoryDTO> exportCompletionRateByCategory(CompletionRateByCategoryQueryDTO completionRateByCategoryQueryDTO) {
        if(StringUtils.isEmpty(completionRateByCategoryQueryDTO.getProductCategoryCode())){
            throw new BusinessException("参数校验异常");
        }
        return technicalQualityCopyDao.selectCompletionRateByCategory(completionRateByCategoryQueryDTO);
    }

    /***
    * @Description: 查询服务单列表
    * @Param: repairRateQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.RepairRateDTO>
    * @Author: WJ
    * @Date: 2022-05-17
    */
    @Override
    public List<RepairRateDTO> selectServiceList(RepairRateQueryDTO repairRateQueryDTO) {
        if (StringUtils.isNotEmpty(repairRateQueryDTO.getStartTime())&&StringUtils.isNotEmpty(repairRateQueryDTO.getEndTime())) {
            if(7==repairRateQueryDTO.getStartTime().length()){
                repairRateQueryDTO.setStartTime(getFirstDayOfMonth(repairRateQueryDTO.getStartTime()));
                repairRateQueryDTO.setEndTime(getLastDayOfMonth(repairRateQueryDTO.getEndTime()));
            }
            repairRateQueryDTO.setStartTime(repairRateQueryDTO.getStartTime() + " 00:00:00");
            repairRateQueryDTO.setEndTime(repairRateQueryDTO.getEndTime() + " 23:59:59");
        }
        List<ConditionDTO> list = new ArrayList<>();
        ConditionDTO turnoverTime = repairRateQueryDTO.getTurnoverTime();
        ConditionDTO visitsNumber = repairRateQueryDTO.getVisitsNumber();
        list.add(turnoverTime);
        list.add(visitsNumber);
        //注入参数校验
        paramsValid(list);
        List<RepairRateDTO> repairRateDTOS = technicalQualityDao.selectServiceList(repairRateQueryDTO);
        if(CollectionUtils.isEmpty(repairRateDTOS)){
            return repairRateDTOS;
        }
        // 开启mybatis缓存后，repairRateDTOS会被缓存，因此不能原地修改，建一个新list
        List<RepairRateDTO> formatList = new ArrayList<>();
        for(RepairRateDTO oriDTO: repairRateDTOS){
            RepairRateDTO formatDTO = new RepairRateDTO();
            BeanUtils.copyProperties(oriDTO, formatDTO);
            // 时间拼上天/秒
            if(StringUtils.isNotEmpty(formatDTO.getTurnoverTime())) {
                formatDTO.setTurnoverTime(BeautifyTimeUtil.HourToDate(Double.parseDouble(formatDTO.getTurnoverTime())));
            }
            if("已完成".equals(formatDTO.getSystemState())||"已提交".equals(formatDTO.getSystemState())) {
                formatList.add(formatDTO);
                continue;
            }
            // 停留时间格式化
            if(StringUtils.isNotEmpty(formatDTO.getTime())) {
                formatDTO.setSystemState(formatDTO.getSystemState()+"（当前已停留"+BeautifyTimeUtil.secondToDate(Double.parseDouble(formatDTO.getTime()))+"）");
            }else {
                formatDTO.setSystemState(formatDTO.getSystemState()+"（当前已停留0分钟）");
            }
            formatList.add(formatDTO);
        }
        return formatList;
    }

    /***
    * @Description: 一次性修复率折线图
    * @Param: lineChartQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.LineChartDTO>
    * @Author: WJ
    * @Date: 2022-05-18
    */
    @Override
    public Map<String,List<LineChartDTO>> selectRepairRateLineChart(LineChartQueryDTO lineChartQueryDTO) {
        Map<String,List<LineChartDTO>> map = new HashMap<>();
        List<LineChartDTO> thisMonth = new ArrayList<>();
        List<LineChartDTO> lastMonth = new ArrayList<>();
        List<LineChartDTO> lineChartDTOS = new ArrayList<>();
        if(!("0".equals(lineChartQueryDTO.getPolymerization())||"1".equals(lineChartQueryDTO.getPolymerization()))){
            throw new BusinessException("请选择聚合方式！");
        }
        List<LineChartDTO> list = technicalQualityDao.selectRepairRateLineChart(lineChartQueryDTO);
        if("0".equals(lineChartQueryDTO.getPolymerization())){
            //年月日
            lineChartDTOS = LineChartDateUtil.getBetweenDate(lineChartQueryDTO.getStartTime(),lineChartQueryDTO.getEndTime());
        }else {
            //年月
            lineChartDTOS = LineChartDateUtil.getBetweenMonth(lineChartQueryDTO.getStartTime(),lineChartQueryDTO.getEndTime());
        }
        //给折线图填充日期
        fillDate(map, thisMonth, lineChartDTOS, list, "thisMonth");
        if("本月".equals(lineChartQueryDTO.getAggregateDate())){
            String startTime,endTime;
            //将当前时间转换成时间处理类
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            //定义时间格式
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //查询上月信息
            calendar.add(Calendar.MONTH, -1);   // -1表示上个月，0表示本月，1表示下个月，上下月份以此类型
            //结束时间本月最后一天24点
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            endTime = dateFormat.format(calendar.getTime());
            //当日开始时间本月第一天0点
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            startTime = dateFormat.format(calendar.getTime());
            lineChartQueryDTO.setStartTime(startTime);
            lineChartQueryDTO.setEndTime(endTime);
            if("0".equals(lineChartQueryDTO.getPolymerization())){
                //年月日
                lineChartDTOS = LineChartDateUtil.getBetweenDate(lineChartQueryDTO.getStartTime(),lineChartQueryDTO.getEndTime());
            }else {
                //年月
                lineChartDTOS = LineChartDateUtil.getBetweenMonth(lineChartQueryDTO.getStartTime(),lineChartQueryDTO.getEndTime());
            }
            List<LineChartDTO> oldList = technicalQualityDao.selectRepairRateLineChart(lineChartQueryDTO);
            //给折线图填充日期
            fillDate(map, lastMonth, lineChartDTOS, oldList, "lastMonth");
        }
        return map;
    }

    /**
    * @Description: 给折线图填充日期
    * @Param:
    * @return: void
    * @Author: WJ
    * @Date: 2022-06-01
    */
    private void fillDate(Map<String, List<LineChartDTO>> map, List<LineChartDTO> thisMonth, List<LineChartDTO> lineChartDTOS, List<LineChartDTO> list, String name) {
        if (CollectionUtils.isNotEmpty(list)) {
            for (LineChartDTO lineChartDTO : lineChartDTOS) {
                LineChartDTO newLine = new LineChartDTO();
                newLine.setTime(lineChartDTO.getTime());
                for (LineChartDTO own : list) {
                    if (lineChartDTO.getTime().equals(own.getTime())) {
                        newLine.setRepairRate(own.getRepairRate());
                        break;
                    }
                }
                thisMonth.add(newLine);
            }
            map.put(name, thisMonth);
        } else {
            map.put(name, lineChartDTOS);
        }
    }

    /***
    * @Description: 故障分析饼图---未筛选
    * @Param: pieChartQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.PieChartDTO>
    * @Author: WJ
    * @Date: 2022-05-18
    */
    @Override
    public List<PieChartDTO> selectRepairRatePieChart(PieChartQueryDTO pieChartQueryDTO) {
        if (StringUtils.isNotEmpty(pieChartQueryDTO.getStartTime())&&StringUtils.isNotEmpty(pieChartQueryDTO.getEndTime())) {
            if(7==pieChartQueryDTO.getStartTime().length()){
                pieChartQueryDTO.setStartTime(getFirstDayOfMonth(pieChartQueryDTO.getStartTime()));
                pieChartQueryDTO.setEndTime(getLastDayOfMonth(pieChartQueryDTO.getEndTime()));
            }
            pieChartQueryDTO.setStartTime(pieChartQueryDTO.getStartTime() + " 00:00:00");
            pieChartQueryDTO.setEndTime(pieChartQueryDTO.getEndTime() + " 23:59:59");
        }
        List<ConditionDTO> list = new ArrayList<>();
        ConditionDTO repairNum = pieChartQueryDTO.getRepairNum();
        ConditionDTO replaceNum = pieChartQueryDTO.getReplaceNum();
        list.add(repairNum);
        list.add(replaceNum);
        List<PieChartDTO> pieChartDTOS = technicalQualityDao.selectRepairRatePieChart(pieChartQueryDTO);
        return pieChartDTOS;
    }

    /***
     * @Description: 故障分析饼图---未筛选
     * @Param: pieChartQueryDTO
     * @return: java.util.List<utry.data.modular.technicalQuality.dto.PieChartDTO>
     * @Author: WJ
     * @Date: 2022-05-18
     */
    @Override
    public List<FaultCauseDTO> selectPie(PieChartQueryDTO pieChartQueryDTO) {
        if (StringUtils.isNotEmpty(pieChartQueryDTO.getStartTime())&&StringUtils.isNotEmpty(pieChartQueryDTO.getEndTime())) {
            if(7==pieChartQueryDTO.getStartTime().length()){
                pieChartQueryDTO.setStartTime(getFirstDayOfMonth(pieChartQueryDTO.getStartTime()));
                pieChartQueryDTO.setEndTime(getLastDayOfMonth(pieChartQueryDTO.getEndTime()));
            }
            pieChartQueryDTO.setStartTime(pieChartQueryDTO.getStartTime() + " 00:00:00");
            pieChartQueryDTO.setEndTime(pieChartQueryDTO.getEndTime() + " 23:59:59");
        }
        List<ConditionDTO> list = new ArrayList<>();
        ConditionDTO repairNum = pieChartQueryDTO.getRepairNum();
        ConditionDTO replaceNum = pieChartQueryDTO.getReplaceNum();
        list.add(repairNum);
        list.add(replaceNum);
        List<FaultCauseDTO> faultCauseDTO = technicalQualityDao.selectFaultCause(pieChartQueryDTO);
        return faultCauseDTO;
    }

    @Override
    public List<IndicatorUserDTO> selectThisMonth(UserTypeQueryDTO userTypeQueryDTO) {
        if(7==userTypeQueryDTO.getMonth().length()){
            userTypeQueryDTO.setMonth(getLastDayOfMonth(userTypeQueryDTO.getMonth()));
            }
            userTypeQueryDTO.setMonth(userTypeQueryDTO.getMonth() + " 00:00:00");
        if(StringUtils.isEmpty(userTypeQueryDTO.getAccountId())){
            return technicalQualityDao.selectTargetMonthIndicator(userTypeQueryDTO.getMonth());
        }
        return technicalQualityDao.selectUserMonthIndicator(userTypeQueryDTO.getAccountId(),userTypeQueryDTO.getMonth());
    }

    @Override
    public ApprovalDurationTimeDTO selectDetailApprovalDurationTime(DetailTimeDTO detailTimeDTO) {
        return technicalQualityDao.selectDetailApprovalDurationTime(detailTimeDTO);
    }

    /***
    * @Description: 故障分析饼图---筛选
    * @Param: pieChartQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.PieChartDTO>
    * @Author: WJ
    * @Date: 2022-05-19
    */
    @Override
    public List<PieChartDTO> selectPartByFaultCause(PieChartQueryDTO pieChartQueryDTO) {
        if (StringUtils.isNotEmpty(pieChartQueryDTO.getStartTime())&&StringUtils.isNotEmpty(pieChartQueryDTO.getEndTime())) {
            if(7==pieChartQueryDTO.getStartTime().length()){
                pieChartQueryDTO.setStartTime(getFirstDayOfMonth(pieChartQueryDTO.getStartTime()));
                pieChartQueryDTO.setEndTime(getLastDayOfMonth(pieChartQueryDTO.getEndTime()));
            }
            pieChartQueryDTO.setStartTime(pieChartQueryDTO.getStartTime() + " 00:00:00");
            pieChartQueryDTO.setEndTime(pieChartQueryDTO.getEndTime() + " 23:59:59");
        }
        return technicalQualityDao.selectPartByFaultCause(pieChartQueryDTO);
    }

    /***
    * @Description: 查询最新月份一次性修复率
    * @return: java.lang.String
    * @Author: WJ
    * @Date: 2022-05-20
    */
    @Override
    public String selectThreshold() {
        return technicalQualityDao.selectThreshold();
    }

    /**
     * 两数相除，获取百分比格式的结果
     * @param a 被除数
     * @param b 除数
     * @return 百分比格式结果
     */
    private String getPercent(int a, int b){
        String ans = new BigDecimal((float) a*100 / b).setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
        return ans + "%";
    }

    /**
     * 增加产品品类的全部统计
     * @param oriDTO 原始数据
     * @param eligibleCount 该品类达标量
     * @param unEligibleCount 该品类不达标量
     * @param statisticList 统计列表
     */
    private void addCategoryTotal(EngineerCopyDTO oriDTO, int eligibleCount, int unEligibleCount,
                                  List<EngineerExportDTO> statisticList){
        EngineerExportDTO totalDto = new EngineerExportDTO();
        BeanUtils.copyProperties(oriDTO, totalDto);
        // 添加产品类型统计数据到列表
        totalDto.setEligible(eligibleCount);
        totalDto.setUnEligible(unEligibleCount);
        totalDto.setTotal(eligibleCount+unEligibleCount);
        totalDto.setRepairRate(getPercent(eligibleCount, totalDto.getTotal()));
        totalDto.setProductType("全部");
        statisticList.add(totalDto);
    }

    /**
     * 增加工程师的全部统计
     * @param oriDTO 原始数据
     * @param eligibleCount 该工程师达标量
     * @param unEligibleCount 该工程师不达标量
     * @param statisticList 统计列表
     */
    private void addEngineerTotal(EngineerCopyDTO oriDTO, int eligibleCount, int unEligibleCount,
                                  List<EngineerExportDTO> statisticList){
        EngineerExportDTO totalDto = new EngineerExportDTO();
        BeanUtils.copyProperties(oriDTO, totalDto);
        // 添加工程师统计数据到列表
        totalDto.setEligible(eligibleCount);
        totalDto.setUnEligible(unEligibleCount);
        totalDto.setTotal(eligibleCount+unEligibleCount);
        totalDto.setRepairRate(getPercent(eligibleCount, totalDto.getTotal()));
        totalDto.setProductCategory("全部");
        totalDto.setProductType("全部");
        statisticList.add(totalDto);
    }

    /***
    * @Description: 工程师管理导出
    * @Param: exportConditionDTO
    * @return: void
    * @Author: WJ
    * @Date: 2022-05-20
    */
    @Override
    public List<EngineerExportDTO> exportEngineerList(EngineerQueryDTO engineerQueryDTO) {
        if (StringUtils.isNotEmpty(engineerQueryDTO.getStartTime())&&StringUtils.isNotEmpty(engineerQueryDTO.getEndTime())) {
            engineerQueryDTO.setStartTime(engineerQueryDTO.getStartTime() + " 00:00:00");
            engineerQueryDTO.setEndTime(engineerQueryDTO.getEndTime() + " 23:59:59");
        }
        List<ConditionDTO> list = new ArrayList<>();
        ConditionDTO repairRate = engineerQueryDTO.getRepairRate();
        ConditionDTO total = engineerQueryDTO.getTotal();
        ConditionDTO eligible = engineerQueryDTO.getEligible();
        ConditionDTO unEligible = engineerQueryDTO.getUnEligible();
        list.add(repairRate);
        list.add(total);
        list.add(eligible);
        list.add(unEligible);
        // 注入参数校验
        paramsValid(list);
        // 查询筛选后的工程师
        List<String> engineers = technicalQualityCopyDao.selectEngineers(engineerQueryDTO);
        if(CollectionUtils.isEmpty(engineers)){
            throw new BusinessException("工程师列表查询为空，导出失败！");
        }
        // 查询全部工程师明细
        engineerQueryDTO.setEngineers(engineers);
        List<EngineerCopyDTO> engineerDTOList = technicalQualityCopyDao.selectEngineer(engineerQueryDTO);

        // 统计列表
        List<EngineerExportDTO> statisticList = new ArrayList<>();
        Set<String> engineerSet = new HashSet<>(engineers);
        engineers.clear();
        // 上次遍历到的工程师明细，工程师id，产品品类
        String lastEngineerId = "";
        String lastCategory = "";
        // 品类的达标和未达标统计，人员的达标和未达标统计
        int eligibleCount = 0;
        int unEligibleCount = 0;
        int personEligibleCount = 0;
        int personUnEligibleCount = 0;
        /*
         * 逆序遍历工程师列表，添加品类统计和人员统计数据
         */
        for(int i=engineerDTOList.size()-1; i>=0; i--){
            EngineerCopyDTO e = engineerDTOList.get(i);
            if(!engineerSet.contains(e.getEngineerId())){
                continue;
            }
            boolean newEngineer = StringUtils.isNotEmpty(lastEngineerId) && !lastEngineerId.equals(e.getEngineerId());
            boolean newCategory = StringUtils.isNotEmpty(lastCategory) && !lastCategory.equals(e.getEngineerId()+e.getProductCategory());
            // 如果遍历到一个新的产品品类，则增加一个品类统计，并重置品类统计
            if(newCategory){
                addCategoryTotal(engineerDTOList.get(i+1), eligibleCount, unEligibleCount, statisticList);
                eligibleCount = 0;
                unEligibleCount = 0;
            }
            // 如果遍历到一个新的工程师，则增加一个工程师统计，并重置工程师统计
            if(newEngineer){
                addEngineerTotal(engineerDTOList.get(i+1), personEligibleCount, personUnEligibleCount, statisticList);
                personEligibleCount = 0;
                personUnEligibleCount = 0;
            }
            EngineerExportDTO totalDto = new EngineerExportDTO();
            BeanUtils.copyProperties(e, totalDto);
            lastEngineerId = e.getEngineerId();
            lastCategory = e.getEngineerId() + e.getProductCategory();
            // 累加当前品类的达标、未达标数
            eligibleCount += e.getEligible();
            unEligibleCount += e.getUnEligible();
            // 累加当前工程师的达标、未达标数
            personEligibleCount += e.getEligible();
            personUnEligibleCount += e.getUnEligible();
            // copy到统计列表
            statisticList.add(totalDto);
        }
        // 加上第一条数据的品类和工程师统计
        addCategoryTotal(engineerDTOList.get(0), eligibleCount, unEligibleCount, statisticList);
        addEngineerTotal(engineerDTOList.get(0), personEligibleCount, personUnEligibleCount, statisticList);

        engineerDTOList.clear();
        Collections.reverse(statisticList);
        int limit = statisticList.size()>engineerQueryDTO.getNumber()? engineerQueryDTO.getNumber(): statisticList.size();
        return statisticList.subList(0, limit);
    }

    /***
    * @Description: 导出---全年一次性修复率
    * @Param: exportConditionDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.ExportRepairRateDTO>
    * @Author: WJ
    * @Date: 2022-05-21
    */
    @Override
    public List<ExportRepairRateDTO> exportRepairRateByYear(ExportConditionDTO exportConditionDTO) {
        if (StringUtils.isNotEmpty(exportConditionDTO.getStartTime())&&StringUtils.isNotEmpty(exportConditionDTO.getEndTime())) {
            if(7==exportConditionDTO.getStartTime().length()){
                exportConditionDTO.setStartTime(getFirstDayOfMonth(exportConditionDTO.getStartTime()));
                exportConditionDTO.setEndTime(getLastDayOfMonth(exportConditionDTO.getEndTime()));
            }
        }
        //定义时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<ExportRepairRateDTO> newList = new ArrayList<>();
        String startTime,endTime;
        //将当前时间转换成时间处理类
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(exportConditionDTO.getEndTime()));
        } catch (ParseException e) {
            LOGGER.error("时间转换异常");
            e.printStackTrace();
        }
        //查询本年信息
        calendar.add(Calendar.YEAR, 0);
        //结束时间本月最后一天
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        endTime = dateFormat.format(calendar.getTime());
        //当日开始时间本月第一天
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
        startTime = dateFormat.format(calendar.getTime());
        exportConditionDTO.setEndTime(endTime+ " 23:59:59");
        exportConditionDTO.setStartTime(startTime + " 00:00:00");
        //全年一次性修复率
        List<ExportRepairRateDTO> list = technicalQualityDao.exportRepairRateByYear(exportConditionDTO);
        //每个月的一次性修复率
        for(ExportRepairRateDTO exportRepairRateDTO : list){
//            exportRepairRateDTO.setStartTime(startTime);
//            exportRepairRateDTO.setEndTime(endTime);
            exportConditionDTO.setProductTypeCode(exportRepairRateDTO.getProductTypeCode());
//            exportRepairRateDTO.setModelList(exportConditionDTO.getModelList());
//            exportRepairRateDTO.setProductCategoryList(exportConditionDTO.getProductCategoryList());
            ExportRepairRateDTO e = new ExportRepairRateDTO();
            e.setProductCategory(exportRepairRateDTO.getProductCategory());
            e.setProductCategoryCode(exportRepairRateDTO.getProductCategoryCode());
            e.setRepairRate(exportRepairRateDTO.getRepairRate());
            e.setEligible(exportRepairRateDTO.getEligible());
            e.setTotal(exportRepairRateDTO.getTotal());
            e.setProductType(exportRepairRateDTO.getProductType());
            e.setProductTypeCode(exportRepairRateDTO.getProductTypeCode());
            List<ExportRepairRateByMonthDTO> monthList = technicalQualityCopyDao.exportRepairRateByMonth(exportConditionDTO);
            if(CollectionUtils.isNotEmpty(monthList)){
                for(ExportRepairRateByMonthDTO exportRepairRateByMonthDTO:monthList){
                    String time = exportRepairRateByMonthDTO.getTime().substring(5, 7);
                    String repair = exportRepairRateByMonthDTO.getRepairRate();
                    switch (time) {
                        case "01":
                           e.setOne(repair);
                            break;
                        case "02":
                            e.setTwo(repair);
                            break;
                        case "03":
                            e.setThree(repair);
                            break;
                        case "04":
                            e.setFour(repair);
                            break;
                        case "05":
                            e.setFive(repair);
                            break;
                        case "06":
                            e.setSix(repair);
                            break;
                        case "07":
                            e.setSeven(repair);
                            break;
                        case "08":
                            e.setEight(repair);
                            break;
                        case "09":
                            e.setNine(repair);
                            break;
                        case "10":
                            e.setTen(repair);
                            break;
                        case "11":
                            e.setEleven(repair);
                            break;
                        case "12":
                            e.setTwelve(repair);
                            break;
                        default:
                            break;
                    }
                }
            }
            newList.add(e);
        }
        if(CollectionUtils.isEmpty(newList)){
            throw new BusinessException("产品类型别一次性修复率列表为空，导出失败！");
        }
        return newList;
    }

    /***
    * @Description: 导出---服务单列表
    * @Param: repairRateQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.RepairRateDTO>
    * @Author: WJ
    * @Date: 2022-05-26
    */
    @Override
    public List<RepairRateDTO> exportServiceList(RepairRateQueryDTO repairRateQueryDTO) {
        if (StringUtils.isNotEmpty(repairRateQueryDTO.getStartTime())&&StringUtils.isNotEmpty(repairRateQueryDTO.getEndTime())) {
            repairRateQueryDTO.setStartTime(repairRateQueryDTO.getStartTime() + " 00:00:00");
            repairRateQueryDTO.setEndTime(repairRateQueryDTO.getEndTime() + " 23:59:59");
        }
        List<ConditionDTO> list = new ArrayList<>();
        ConditionDTO turnoverTime = repairRateQueryDTO.getTurnoverTime();
        ConditionDTO visitsNumber = repairRateQueryDTO.getVisitsNumber();
        list.add(turnoverTime);
        list.add(visitsNumber);
        //注入参数校验
        paramsValid(list);
        List<RepairRateDTO> repairRateDTOS = technicalQualityCopyDao.selectServiceList(repairRateQueryDTO);
        if(CollectionUtils.isNotEmpty(repairRateDTOS)) {
            repairRateDTOS.stream().filter(item -> {
                if(StringUtils.isNotEmpty(item.getTurnoverTime())) {
                    item.setTurnoverTime(BeautifyTimeUtil.HourToDate(Double.parseDouble(item.getTurnoverTime())));
                }
                if(!("已完成".equals(item.getSystemState())||"已提交".equals(item.getSystemState()))){
                    if(StringUtils.isNotEmpty(item.getTime())) {
                        item.setSystemState(item.getSystemState()+"（当前已停留"+BeautifyTimeUtil.secondToDate(Double.parseDouble(item.getTime()))+"）");
                    }else {
                        item.setSystemState(item.getSystemState()+"（当前已停留0分钟）");
                    }
                }
                return true;
            }).collect(Collectors.toList());;
        }
        return repairRateDTOS;
    }

    /**
     * 查询品类树
     */
    @Override
    public List<TreeDTO> selectCategoryTree() {
        String key = "category";
        ListOperations<String, TreeDTO> listOps = (ListOperations<String, TreeDTO>) redisTemplate.opsForList();
        if(redisTemplate.hasKey(key)){
            return listOps.range(key, 0, -1);
        }
        //初始化层级
        List<TreeDTO> newList;
        synchronized (this) {
            if(redisTemplate.hasKey(key)){
                return listOps.range(key, 0, -1);
            }
            int level = 2;
            //查询所有品类-类型-型号信息
            List<TreeDTO> treeDTOS = technicalQualityDao.selectCategoryInformation();
            //查询根节点
            List<TreeDTO> categoryRootDTOS = technicalQualityDao.selectRoot();
            //初始化列表
            List<TreeDTO> list = new ArrayList<>();
            //遍历根节点
            for (TreeDTO root : categoryRootDTOS) {
                root.setLabel("1");
                //寻找这个根节点菜单下的子节点菜单
                findChilds(root, treeDTOS,level);
                //添加到根节点的列表中
                list.add(root);
            }
            newList = new ArrayList<>();
            TreeDTO treeDTO = new TreeDTO();
            treeDTO.setPid("000");
            treeDTO.setId("0");
            treeDTO.setLabel("0");
            treeDTO.setName("全部");
            treeDTO.setChildren(list);
            newList.add(treeDTO);
            if(redisTemplate.hasKey(key)){
                return listOps.range(key, 0, -1);
            }
            listOps.rightPushAll(key,newList);
        }
        return newList;
    }

    /***
    * @Description: 获取担当
    * @Param: technicalQualityQueryDTO
    * @return: utry.data.modular.technicalQuality.dto.TechnicalQualityQueryDTO
    * @Author: WJ
    * @Date: 2022-05-30
    */
    @Override
    public TechnicalQualityQueryDTO getUsers(TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        if (StringUtils.isNotEmpty(technicalQualityQueryDTO.getStartTime())&&StringUtils.isNotEmpty(technicalQualityQueryDTO.getEndTime())) {
            technicalQualityQueryDTO.setStartTime(technicalQualityQueryDTO.getStartTime() + " 00:00:00");
            technicalQualityQueryDTO.setEndTime(technicalQualityQueryDTO.getEndTime() + " 23:59:59");
        }
        List<TechnicalQualityUserDTO> list;
        //获取聚合时间
        String aggregateDate = technicalQualityQueryDTO.getAggregateDate();
        //所有担当
        list = technicalQualityDao.selectUserByType(technicalQualityQueryDTO);
        technicalQualityQueryDTO.setTechnicalQualityUserDTOS(list);
        //遍历担当并计算指标值
        return technicalQualityQueryDTO;
    }

    /***
    * @Description: 获取担当详情
    * @Param:
    * @return: utry.data.modular.technicalQuality.dto.TechnicalQualityUserDTO
    * @Author: WJ
    * @Date: 2022-05-30
    */
    @Override
    public TechnicalQualityUserDTO getUserInfo(TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        if(CollectionUtils.isEmpty(technicalQualityQueryDTO.getTechnicalQualityUserDTOS())){
            return null;
        }
        if (StringUtils.isNotEmpty(technicalQualityQueryDTO.getStartTime())&&StringUtils.isNotEmpty(technicalQualityQueryDTO.getEndTime())) {
            technicalQualityQueryDTO.setStartTime(technicalQualityQueryDTO.getStartTime() + " 00:00:00");
            technicalQualityQueryDTO.setEndTime(technicalQualityQueryDTO.getEndTime() + " 23:59:59");
        }
        //将当前时间转换成时间处理类
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //定义时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //创建开始时间结束时间
        String startTime = null;
        String endTime = null;
        //查询担当详情
        TechnicalQualityUserDTO technicalQualityUserDTO = technicalQualityQueryDTO.getTechnicalQualityUserDTOS().get(0);
        TechnicalQualityUserDTO userDTO = new TechnicalQualityUserDTO();
        userDTO.setAccountId(technicalQualityUserDTO.getAccountId());
        userDTO.setName(technicalQualityUserDTO.getName());
        //查询具体目标值
        List<IndicatorUserDTO> indicatorUserDTOS;
        indicatorUserDTOS = technicalQualityDao.selectMonthIndicator(technicalQualityUserDTO.getAccountId(),technicalQualityQueryDTO.getEndTime());
        userDTO.setList(indicatorUserDTOS);
        //查询担当详情
        List<String> types = technicalQualityDao.selectUserType(technicalQualityUserDTO.getAccountId());
        //查询本月一次性修复率
        CalculateDTO calculateDTO = new CalculateDTO();
        calculateDTO.setStartTime(technicalQualityQueryDTO.getStartTime());
        calculateDTO.setEndTime(technicalQualityQueryDTO.getEndTime());
        calculateDTO.setList(types);
        String repairRate = (String)calculateRepairRate(calculateDTO).get("REPAIR");
        String approvalDuration = (String)calculateApprovalDuration(calculateDTO).get("TIME");
        String completionRate = (String)calculateCompletionRate(calculateDTO).get("RATE");
        userDTO.setRepairRate(repairRate);
        //查询本月品质单审核作业时长
        userDTO.setApprovalDuration(approvalDuration);
        //查询本月新品上市资料七天完备率
        userDTO.setCompletionRate(completionRate);
        //查询符合类型条件的关联担当
        if("本月".equals(technicalQualityQueryDTO.getAggregateDate())){
                //查询上月信息
                technicalQualityQueryDTO.setAggregateDate("上月");
                calendar.add(Calendar.MONTH, -1);   // -1表示上个月，0表示本月，1表示下个月，上下月份以此类型
                //结束时间本月最后一天24点
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                endTime = dateFormat.format(calendar.getTime());
                //当日开始时间本月第一天0点
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                startTime = dateFormat.format(calendar.getTime());
                calculateDTO.setStartTime(startTime+" 00:00:00");
                calculateDTO.setEndTime(endTime+" 23:59:59");
                //计算上个月环比
                String previousRepairRate = (String)calculateRepairRate(calculateDTO).get("REPAIR");
                String previousApprovalDuration = (String)calculateApprovalDuration(calculateDTO).get("TIME");
                float f1 = Float.parseFloat(repairRate);
                float f2 = Float.parseFloat(approvalDuration);
                float f3 = Float.parseFloat(previousRepairRate);
                float f4 = Float.parseFloat(previousApprovalDuration);
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                if(f3!=0){
                    String repairRateChainRatio = decimalFormat.format((f1-f3) /f3 * 100);
                    userDTO.setRepairRateChainRatio(repairRateChainRatio);
                }else{
                    userDTO.setRepairRateChainRatio("0.00");
                }
                if(f4!=0){
                    String approvalDurationChainRatio = decimalFormat.format((f2-f4) /f4 * 100);
                    userDTO.setApprovalDurationChainRatio(approvalDurationChainRatio);
                }else{
                    userDTO.setApprovalDurationChainRatio("0.00");
                }
            return userDTO;
            }
            return userDTO;
    }
    /**
     * 模板显示
     */
    @Override
    public List<DistrictTemplateDTO> selectDistrictTemplate() {
        String key = "district";
        ListOperations<String, DistrictTemplateDTO> listOps = (ListOperations<String, DistrictTemplateDTO>) redisTemplate.opsForList();
        if(redisTemplate.hasKey(key)){
            return listOps.range(key, 0, -1);
        }

        //父节点传参---后端
        List<DistrictTemplateQueryDTO> categoryTemplate = technicalQualityDao.selectCategoryTemplate();
        //子节点回显到树---前端
        List<DistrictTemplateQueryDTO> typeTemplate = technicalQualityDao.selectTypeTemplate();

        List<DistrictTemplateDTO> list = new ArrayList<>();
        Map<String, List<DistrictTemplateQueryDTO>> categoryTemplateMap;
        Map<String, List<DistrictTemplateQueryDTO>> typeTemplateMap = new HashMap<>();

        if(CollectionUtils.isNotEmpty(typeTemplate)){
            typeTemplateMap = typeTemplate.stream().collect(Collectors.groupingBy(DistrictTemplateQueryDTO::getName));
        }

        if(CollectionUtils.isNotEmpty(categoryTemplate)){
            categoryTemplateMap = categoryTemplate.stream().collect(Collectors.groupingBy(DistrictTemplateQueryDTO::getName));
            for (String name:categoryTemplateMap.keySet()){
                DistrictTemplateDTO dto = new DistrictTemplateDTO();
                dto.setName(name);
                List<DistrictTemplateQueryDTO> categoryList = categoryTemplateMap.get(name);
                List<DistrictTemplateQueryDTO> typeList = typeTemplateMap.get(name);
                List<String> category = new ArrayList<>();
                List<String> type = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(categoryList)){
                    for(DistrictTemplateQueryDTO categoryDTO : categoryList){
                        String code = categoryDTO.getCode();
                        category.add(code);
                    }

                }
                dto.setProductCategoryCode(category);
                if(CollectionUtils.isNotEmpty(typeList)){
                    for(DistrictTemplateQueryDTO typeDTO : typeList){
                        String code = typeDTO.getCode();
                        type.add(code);
                    }
                    dto.setList(type);
                }
                list.add(dto);
            }
        }
        if(redisTemplate.hasKey(key)){
            return listOps.range(key, 0, -1);
        }
        listOps.rightPushAll(key,list);
        return list;
    }

    /**
        * @Description 查询符合品类条件的担当详情
        * @param technicalQualityQueryDTO
        * @return java.util.List<utry.data.modular.technicalQuality.dto.TechnicalQualityUserDTO>
        * @author WJ
        * @date 2022-05-07 11:33
    */
    @Override
    public List<TechnicalQualityUserDTO> selectUserInfo(TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        if (StringUtils.isNotEmpty(technicalQualityQueryDTO.getStartTime())&&StringUtils.isNotEmpty(technicalQualityQueryDTO.getEndTime())) {
            technicalQualityQueryDTO.setStartTime(technicalQualityQueryDTO.getStartTime() + " 00:00:00");
            technicalQualityQueryDTO.setEndTime(technicalQualityQueryDTO.getEndTime() + " 23:59:59");
        }
        List<TechnicalQualityUserDTO> list;
        List<TechnicalQualityUserDTO> newList = new ArrayList<>();
        //获取聚合时间
        String aggregateDate = technicalQualityQueryDTO.getAggregateDate();
        //将当前时间转换成时间处理类
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //定义时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //创建开始时间结束时间
        String startTime = null;
        String endTime = null;
        //查询符合类型条件的关联担当
        if("本月".equals(aggregateDate)){
            //筛选配置过目标的担当
            list = technicalQualityDao.selectUserByConfig(technicalQualityQueryDTO);
            if(CollectionUtils.isEmpty(list)){
                return null;
            }
            //查询担当详情
            for(TechnicalQualityUserDTO technicalQualityUserDTO : list){
                List<IndicatorUserDTO> indicatorUserDTOS;
                TechnicalQualityUserDTO userDTO = new TechnicalQualityUserDTO();
                userDTO.setAccountId(technicalQualityUserDTO.getAccountId());
                userDTO.setName(technicalQualityUserDTO.getName());
                //查询具体目标值
                indicatorUserDTOS = technicalQualityDao.selectMonthIndicator(technicalQualityUserDTO.getAccountId(),technicalQualityQueryDTO.getEndTime());
                userDTO.setList(indicatorUserDTOS);
                List<String> types = technicalQualityDao.selectUserType(technicalQualityUserDTO.getAccountId());
                //查询本月一次性修复率
                CalculateDTO calculateDTO = new CalculateDTO();
                calculateDTO.setStartTime(technicalQualityQueryDTO.getStartTime());
                calculateDTO.setEndTime(technicalQualityQueryDTO.getEndTime());
                calculateDTO.setList(types);
                String repairRate = (String)calculateRepairRate(calculateDTO).get("REPAIR");
                String approvalDuration = (String)calculateApprovalDuration(calculateDTO).get("TIME");
                String completionRate = (String)calculateCompletionRate(calculateDTO).get("RATE");
                userDTO.setRepairRate(repairRate);
                //查询本月品质单审核作业时长
                userDTO.setApprovalDuration(approvalDuration);
                //查询本月新品上市资料七天完备率
                userDTO.setCompletionRate(completionRate);
                //查询上月信息
                technicalQualityQueryDTO.setAggregateDate("上月");
                calendar.add(Calendar.MONTH, -1);   // -1表示上个月，0表示本月，1表示下个月，上下月份以此类型
                //结束时间本月最后一天24点
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                endTime = dateFormat.format(calendar.getTime());
                //当日开始时间本月第一天0点
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                startTime = dateFormat.format(calendar.getTime());
                calculateDTO.setStartTime(startTime+" 00:00:00");
                calculateDTO.setEndTime(endTime+" 23:59:59");
                //计算上个月环比
                String previousRepairRate = (String)calculateRepairRate(calculateDTO).get("REPAIR");
                String previousApprovalDuration = (String)calculateApprovalDuration(calculateDTO).get("TIME");
                float f1 = Float.parseFloat(repairRate);
                float f2 = Float.parseFloat(approvalDuration);
                float f3 = Float.parseFloat(previousRepairRate);
                float f4 = Float.parseFloat(previousApprovalDuration);
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                if(f3!=0){
                    String repairRateChainRatio = decimalFormat.format((f1-f3) /f3 * 100);
                    userDTO.setRepairRateChainRatio(repairRateChainRatio);
                }else{
                    userDTO.setRepairRateChainRatio("0.00");
                }
                if(f4!=0){
                    String approvalDurationChainRatio = decimalFormat.format((f2-f4) /f4 * 100);
                    userDTO.setApprovalDurationChainRatio(approvalDurationChainRatio);
                }else{
                    userDTO.setApprovalDurationChainRatio("0.00");
                }
                newList.add(userDTO);
            }
        }else {
            //筛选符合类型的担当
            list = technicalQualityDao.selectUserByType(technicalQualityQueryDTO);
            //查询担当详情
            for(TechnicalQualityUserDTO technicalQualityUserDTO : list) {
                TechnicalQualityUserDTO userDTO = new TechnicalQualityUserDTO();
                userDTO.setAccountId(technicalQualityUserDTO.getAccountId());
                userDTO.setName(technicalQualityUserDTO.getName());
                List<String> types = technicalQualityDao.selectUserType(technicalQualityUserDTO.getAccountId());
                //查询一次性修复率
                CalculateDTO calculateDTO = new CalculateDTO();
                calculateDTO.setStartTime(technicalQualityQueryDTO.getStartTime());
                calculateDTO.setEndTime(technicalQualityQueryDTO.getEndTime());
                calculateDTO.setList(types);
                String repairRate = (String)calculateRepairRate(calculateDTO).get("REPAIR");
                String approvalDuration = (String)calculateApprovalDuration(calculateDTO).get("TIME");
                String completionRate = (String)calculateCompletionRate(calculateDTO).get("RATE");
                userDTO.setRepairRate(repairRate);
                //查询品质单审核作业时长
                userDTO.setApprovalDuration(approvalDuration);
                //查询新品上市资料七天完备率
                userDTO.setCompletionRate(completionRate);
                newList.add(userDTO);
            }
        }
        //遍历担当并计算指标值
        return newList;
    }


    /***
    * @Description: 查询符合类型条件的核心详情
    * @Param: technicalQualityQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.TechnicalQualityUserDTO>
    * @Author: WJ
    * @Date: 2022-05-07
    */
    @Override
    public List<TechnicalQualityUserDTO> selectTargetInfo(TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        if (StringUtils.isNotEmpty(technicalQualityQueryDTO.getStartTime())&&StringUtils.isNotEmpty(technicalQualityQueryDTO.getEndTime())) {
            if(7==technicalQualityQueryDTO.getStartTime().length()){
                technicalQualityQueryDTO.setStartTime(getFirstDayOfMonth(technicalQualityQueryDTO.getStartTime()));
                technicalQualityQueryDTO.setEndTime(getLastDayOfMonth(technicalQualityQueryDTO.getEndTime()));
            }
            technicalQualityQueryDTO.setStartTime(technicalQualityQueryDTO.getStartTime() + " 00:00:00");
            technicalQualityQueryDTO.setEndTime(technicalQualityQueryDTO.getEndTime() + " 23:59:59");
        }
        technicalQualityQueryDTO.setList(technicalQualityDao.selectAllUserType());
        List<TechnicalQualityUserDTO> newList = new ArrayList<>();
        TechnicalQualityUserDTO userDTO = new TechnicalQualityUserDTO();
        List<IndicatorUserDTO> indicatorUserDTOS;
        //获取聚合时间
        String aggregateDate = technicalQualityQueryDTO.getAggregateDate();
        //将当前时间转换成时间处理类
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //定义时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //创建开始时间结束时间
        String startTime = null;
        String endTime = null;
        //查询具体目标值
        indicatorUserDTOS = technicalQualityDao.selectTargetMonthIndicator(technicalQualityQueryDTO.getEndTime());
        userDTO.setList(indicatorUserDTOS);
        //查询一次性修复率
        CalculateDTO calculateDTO = new CalculateDTO();
        calculateDTO.setStartTime(technicalQualityQueryDTO.getStartTime());
        calculateDTO.setEndTime(technicalQualityQueryDTO.getEndTime());
        calculateDTO.setList(technicalQualityQueryDTO.getList());
        String repairRate = (String)calculateRepairRate(calculateDTO).get("REPAIR");
        String approvalDuration = (String)calculateApprovalDuration(calculateDTO).get("TIME");
        String completionRate = (String)calculateCompletionRate(calculateDTO).get("RATE");
        userDTO.setRepairRate(repairRate);
        //查询品质单审核作业时长
        userDTO.setApprovalDuration(approvalDuration);
        //查询新品上市资料七天完备率
        userDTO.setCompletionRate(completionRate);
        if("本月".equals(technicalQualityQueryDTO.getAggregateDate())){
            //计算环比
            technicalQualityQueryDTO.setAggregateDate("上月");
            //查询上月信息
            calendar.add(Calendar.MONTH, -1);   // -1表示上个月，0表示本月，1表示下个月，上下月份以此类型
            //结束时间本月最后一天24点
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            endTime = dateFormat.format(calendar.getTime());
            //当日开始时间本月第一天0点
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            startTime = dateFormat.format(calendar.getTime());
            calculateDTO.setStartTime(startTime+" 00:00:00");
            calculateDTO.setEndTime(endTime+" 23:59:59");
            //计算上个月环比
            String previousRepairRate = (String)calculateRepairRate(calculateDTO).get("REPAIR");
            String previousApprovalDuration = (String)calculateApprovalDuration(calculateDTO).get("TIME");
            float f1 = Float.parseFloat(repairRate);
            float f2 = Float.parseFloat(approvalDuration);
            float f3 = Float.parseFloat(previousRepairRate);
            float f4 = Float.parseFloat(previousApprovalDuration);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            if(f3!=0){
                String repairRateChainRatio = decimalFormat.format((f1-f3) /f3 * 100);
                userDTO.setRepairRateChainRatio(repairRateChainRatio);
            }else{
                userDTO.setRepairRateChainRatio("0.00");
            }
            if(f4!=0){
                String approvalDurationChainRatio = decimalFormat.format((f2-f4) /f4 * 100);
                userDTO.setApprovalDurationChainRatio(approvalDurationChainRatio);
            }else{
                userDTO.setApprovalDurationChainRatio("0.00");
            }
        }
        newList.add(userDTO);
        return newList;
    }

    /***
     * @Description: 查询符合类型条件的核心详情
     * @Param: technicalQualityQueryDTO
     * @return: java.util.List<utry.data.modular.technicalQuality.dto.TechnicalQualityUserDTO>
     * @Author: WJ
     * @Date: 2022-05-07
     */
    @Override
    public List<TechnicalQualityUserDTO> selectTargetInfoCopy(TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        if (StringUtils.isNotEmpty(technicalQualityQueryDTO.getStartTime())&&StringUtils.isNotEmpty(technicalQualityQueryDTO.getEndTime())) {
            if(7==technicalQualityQueryDTO.getStartTime().length()){
                technicalQualityQueryDTO.setStartTime(getFirstDayOfMonth(technicalQualityQueryDTO.getStartTime()));
                technicalQualityQueryDTO.setEndTime(getLastDayOfMonth(technicalQualityQueryDTO.getEndTime()));
            }
            technicalQualityQueryDTO.setStartTime(technicalQualityQueryDTO.getStartTime() + " 00:00:00");
            technicalQualityQueryDTO.setEndTime(technicalQualityQueryDTO.getEndTime() + " 23:59:59");
        }
        List<TechnicalQualityUserDTO> newList = new ArrayList<>();
        TechnicalQualityUserDTO userDTO = new TechnicalQualityUserDTO();
        List<IndicatorUserDTO> indicatorUserDTOS;
        //获取聚合时间
        String aggregateDate = technicalQualityQueryDTO.getAggregateDate();
        //将当前时间转换成时间处理类
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //定义时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //创建开始时间结束时间
        String startTime = null;
        String endTime = null;
        //查询具体目标值
        indicatorUserDTOS = technicalQualityDao.selectTargetMonthIndicator(technicalQualityQueryDTO.getEndTime());
        userDTO.setList(indicatorUserDTOS);
        //查询一次性修复率
        CalculateDTO calculateDTO = new CalculateDTO();
        calculateDTO.setStartTime(technicalQualityQueryDTO.getStartTime());
        calculateDTO.setEndTime(technicalQualityQueryDTO.getEndTime());
        calculateDTO.setList(technicalQualityQueryDTO.getList());
        calculateDTO.setProductCategoryList(technicalQualityQueryDTO.getProductCategoryList());
        calculateDTO.setModelList(technicalQualityQueryDTO.getModelList());
        String repairRate = (String)calculateRepairRate(calculateDTO).get("REPAIR");
        String approvalDuration = (String)calculateApprovalDuration(calculateDTO).get("TIME");
        String completionRate = (String)calculateCompletionRate(calculateDTO).get("RATE");
        userDTO.setRepairRate(repairRate);
        //查询品质单审核作业时长
        userDTO.setApprovalDuration(approvalDuration);
        //查询新品上市资料七天完备率
        userDTO.setCompletionRate(completionRate);
        if("本月".equals(technicalQualityQueryDTO.getAggregateDate())){
            //计算环比
            technicalQualityQueryDTO.setAggregateDate("上月");
            //查询上月信息
            calendar.add(Calendar.MONTH, -1);   // -1表示上个月，0表示本月，1表示下个月，上下月份以此类型
            //结束时间本月最后一天24点
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            endTime = dateFormat.format(calendar.getTime());
            //当日开始时间本月第一天0点
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            startTime = dateFormat.format(calendar.getTime());
            calculateDTO.setStartTime(startTime+" 00:00:00");
            calculateDTO.setEndTime(endTime+" 23:59:59");
            //计算上个月环比
            String previousRepairRate = (String)calculateRepairRate(calculateDTO).get("REPAIR");
            String previousApprovalDuration = (String)calculateApprovalDuration(calculateDTO).get("TIME");
            float f1 = Float.parseFloat(repairRate);
            float f2 = Float.parseFloat(approvalDuration);
            float f3 = Float.parseFloat(previousRepairRate);
            float f4 = Float.parseFloat(previousApprovalDuration);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            if(f3!=0){
                String repairRateChainRatio = decimalFormat.format((f1-f3) /f3 * 100);
                userDTO.setRepairRateChainRatio(repairRateChainRatio);
            }else{
                userDTO.setRepairRateChainRatio("0.00");
            }
            if(f4!=0){
                String approvalDurationChainRatio = decimalFormat.format((f2-f4) /f4 * 100);
                userDTO.setApprovalDurationChainRatio(approvalDurationChainRatio);
            }else{
                userDTO.setApprovalDurationChainRatio("0.00");
            }
        }
        newList.add(userDTO);
        return newList;
    }

    /***
    * @Description: 工程师管理-带筛选
    * @Param: technicalQualityQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.EngineerDTO>
    * @Author: WJ
    * @Date: 2022-05-07
    */
    @Override
    public List<EngineerDTO> selectEngineer(EngineerQueryDTO engineerQueryDTO) {
        if (StringUtils.isNotEmpty(engineerQueryDTO.getStartTime())&&StringUtils.isNotEmpty(engineerQueryDTO.getEndTime())) {
            if(7==engineerQueryDTO.getStartTime().length()){
                engineerQueryDTO.setStartTime(getFirstDayOfMonth(engineerQueryDTO.getStartTime()));
                engineerQueryDTO.setEndTime(getLastDayOfMonth(engineerQueryDTO.getEndTime()));
            }
            engineerQueryDTO.setStartTime(engineerQueryDTO.getStartTime() + " 00:00:00");
            engineerQueryDTO.setEndTime(engineerQueryDTO.getEndTime() + " 23:59:59");
        }
        List<ConditionDTO> list = new ArrayList<>();
        ConditionDTO repairRate = engineerQueryDTO.getRepairRate();
        ConditionDTO total = engineerQueryDTO.getTotal();
        ConditionDTO eligible = engineerQueryDTO.getEligible();
        ConditionDTO unEligible = engineerQueryDTO.getUnEligible();
        list.add(repairRate);
        list.add(total);
        list.add(eligible);
        list.add(unEligible);
        //注入参数校验
        paramsValid(list);
        return technicalQualityDao.selectEngineer(engineerQueryDTO);
    }

    /***
    * @Description: 工程师管理参数校验
    * @return: void
    * @Author: WJ
    * @Date: 2022-05-10
    */
    private void paramsValid(List<ConditionDTO> list) {
        String[] arrays = {"=", ">=", ">", "<>", "<=", "<", ""};
        String[] arraysSort = {"DESC","ASC",""};
        for(ConditionDTO c : list){
            if (c!=null){
                if (StringUtils.isNotEmpty(c.getNumberType())) {
                    if (!params(arrays, c.getNumberType())) {
                        throw new BusinessException("参数校验异常");
                    }
                }
                if (StringUtils.isNotEmpty(c.getSort())) {
                    if (!params(arraysSort, c.getSort())) {
                        throw new BusinessException("参数校验异常");
                    }
                }
            }
        }
    }

    /***
    * @Description: 校验参数是否合法
    * @Param:
    * @return: boolean
    * @Author: WJ
    * @Date: 2022-05-10
    */
    public boolean params(String [] arr, String targetValue){
        for(String s:arr){
            if(s.equals(targetValue)) {
                return true;
            }
        }
        return false;
    }

    /***
    * @Description: 一次性修复率/类型别/大区别
    * @Param: technicalQualityQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.EngineerDTO>
    * @Author: WJ
    * @Date: 2022-05-09
    */
    @Override
    public List<RepairRateHistogramDTO> selectRepairRate(TechnicalQualityQueryDTO technicalQualityQueryDTO) {
        if (StringUtils.isNotEmpty(technicalQualityQueryDTO.getStartTime())&&StringUtils.isNotEmpty(technicalQualityQueryDTO.getEndTime())) {
            if(7==technicalQualityQueryDTO.getStartTime().length()){
                technicalQualityQueryDTO.setStartTime(getFirstDayOfMonth(technicalQualityQueryDTO.getStartTime()));
                technicalQualityQueryDTO.setEndTime(getLastDayOfMonth(technicalQualityQueryDTO.getEndTime()));
            }
            technicalQualityQueryDTO.setStartTime(technicalQualityQueryDTO.getStartTime() + " 00:00:00");
            technicalQualityQueryDTO.setEndTime(technicalQualityQueryDTO.getEndTime() + " 23:59:59");
        }
        if(StringUtils.isEmpty(technicalQualityQueryDTO.getSort())){
            technicalQualityQueryDTO.setSort("DESC");
        }else{
            String[] arrays = {"DESC","ASC",""};
            if(!params(arrays,technicalQualityQueryDTO.getSort())){
                throw new BusinessException("参数校验异常");
            }
        }
        if("0".equals(technicalQualityQueryDTO.getType())){
            return technicalQualityDao.selectRepairRateByType(technicalQualityQueryDTO);
        }
        if("1".equals(technicalQualityQueryDTO.getType())){
            return technicalQualityDao.selectRepairRateByAccounting(technicalQualityQueryDTO);
        }
        return null;
    }

    /***
    * @Description: 一次性修复率-工程师品类
    * @Param: engineerQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.EngineerDTO>
    * @Author: WJ
    * @Date: 2022-05-09
    */
    @Override
    public List<EngineerDTO> selectEngineerCategory(EngineerDTO engineerDTO) {
        if (StringUtils.isNotEmpty(engineerDTO.getStartTime())&&StringUtils.isNotEmpty(engineerDTO.getEndTime())) {
            if(7==engineerDTO.getStartTime().length()){
                engineerDTO.setStartTime(getFirstDayOfMonth(engineerDTO.getStartTime()));
                engineerDTO.setEndTime(getLastDayOfMonth(engineerDTO.getEndTime()));
            }
            engineerDTO.setStartTime(engineerDTO.getStartTime() + " 00:00:00");
            engineerDTO.setEndTime(engineerDTO.getEndTime() + " 23:59:59");
        }
        return technicalQualityDao.selectEngineerCategory(engineerDTO);
    }

    /***
    * @Description: 一次性修复率-工程师类型
    * @Param: engineerQueryDTO
    * @return: java.util.List<utry.data.modular.technicalQuality.dto.EngineerDTO>
    * @Author: WJ
    * @Date: 2022-05-09
    */
    @Override
    public List<EngineerDTO> selectEngineerType(EngineerDTO engineerDTO) {
        if (StringUtils.isNotEmpty(engineerDTO.getStartTime())&&StringUtils.isNotEmpty(engineerDTO.getEndTime())) {
            if(7==engineerDTO.getStartTime().length()){
                engineerDTO.setStartTime(getFirstDayOfMonth(engineerDTO.getStartTime()));
                engineerDTO.setEndTime(getLastDayOfMonth(engineerDTO.getEndTime()));
            }
            engineerDTO.setStartTime(engineerDTO.getStartTime() + " 00:00:00");
            engineerDTO.setEndTime(engineerDTO.getEndTime() + " 23:59:59");
        }
        return technicalQualityDao.selectEngineerType(engineerDTO);
    }

    /***
    * @Description: 产品资料旧数据查询
    * @return: java.util.List<utry.data.modular.partsManagement.model.ProductInformation>
    * @Author: WJ
    * @Date: 2022-05-10
    */
    @Override
    public List<QualityFeedbackDTO> queryOldInformation() {
        return technicalQualityDao.queryOldInformation();
    }

    /***
    * @Description: 产品资料旧数据更新
    * @Param: olds
    * @return: void
    * @Author: WJ
    * @Date: 2022-05-10
    */
    @Override
    public void updateInformation(List<QualityFeedbackDTO> list) {
        if(CollectionUtils.isNotEmpty(list)){
            qualityFeedbackConfigDao.updateTime(list);
        }
    }

    /**
     * @description 品质单审核作业平均时长查询
     * @param calculateDTO
     * @return void
     * @author WJ
     * @date 2021-12-03 13:16
     */
    @Override
    public String getApprovalDurationAvgTime(CalculateDTO calculateDTO) {
        if (StringUtils.isNotEmpty(calculateDTO.getStartTime())&&StringUtils.isNotEmpty(calculateDTO.getEndTime())) {
            calculateDTO.setStartTime(calculateDTO.getStartTime() + " 00:00:00");
            calculateDTO.setEndTime(calculateDTO.getEndTime() + " 23:59:59");
        }
        return technicalQualityDao.getApprovalDurationAvgTime(calculateDTO);
    }

    /***
    * @Description: 查询人员类型
    * @Param:
    * @return: java.util.List<java.lang.String>
    * @Author: WJ
    * @Date: 2022-06-08
    */
    @Override
    public List<String> selectType(String accountId) {
        if(StringUtils.isEmpty(accountId)){
            return technicalQualityDao.selectAllUserType();
        }
        return technicalQualityDao.selectUserType(accountId);
    }

    /**
     * @description 品质单审核作业时长查询
     * @param calculateDTO
     * @return void
     * @author WJ
     * @date 2021-12-03 13:16
     */
    private Map<String,Object> calculateApprovalDuration(CalculateDTO calculateDTO) {
        return technicalQualityDao.calculateApprovalDuration(calculateDTO);
    }

    /**
     * @description 新品上市资料七天完备率查询
     * @param calculateDTO
     * @return void
     * @author WJ
     * @date 2021-12-03 13:16
     */
    private Map<String,Object> calculateCompletionRate(CalculateDTO calculateDTO) {
        return technicalQualityDao.calculateCompletionRate(calculateDTO);
    }

    /**
     * @description 一次性修复率查询
     * @param calculateDTO
     * @return void
     * @author WJ
     * @date 2021-12-03 13:16
     */
    private Map<String,Object> calculateRepairRate(CalculateDTO calculateDTO) {
        return technicalQualityDao.calculateRepairRate(calculateDTO);
    }

    /**
     * @description 采用递归方法，遍历成树级结构
     * @param root
     * @param list
     * @return void
     * @author WJ
     * @date 2021-12-03 13:16
     */
    private void findChilds(TreeDTO root, List<TreeDTO> list,int label) {
        //初始化集合
        List<TreeDTO> childlist = new ArrayList<>();
        Set<String> set = new HashSet<>();
        //遍历所有数据，找到是入参父节点的子节点的数据，然后加到childlist集合中。
        for (TreeDTO treeDTO :
                list) {
            if (root.getId().equals(treeDTO.getPid())){
                if (set.add(treeDTO.getId())) {
                    treeDTO.setLabel(label + "");
                    childlist.add(treeDTO);
                }
            }
        }
        //若子节点不存在，那么就不必再遍历子节点中的子节点了 直接返回。
        if (childlist.size() == 0){
            return;
        }
        //设置父节点的子节点列表
        root.setChildren(childlist);
        label++;
        //若子节点存在，接着递归调用该方法，寻找子节点的子节点。
        for (TreeDTO childs :
                childlist) {
            findChilds(childs, list,label);
        }
    }

    /**
     * 获得该月第一天
     * @return
     */
    public static String getFirstDayOfMonth(String s){
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM").parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer year=cal.get(Calendar.YEAR);//获取年
        Integer month = cal.get(Calendar.MONTH)+1;//获取月（月份从0开始，如果按照中国的习惯，需要加一）
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    /**
     * 获得该月最后一天
     * @return
     */
    public static String getLastDayOfMonth(String s){
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM").parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer year=cal.get(Calendar.YEAR);//获取年
        Integer month = cal.get(Calendar.MONTH)+1;//获取月（月份从0开始，如果按照中国的习惯，需要加一）
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
    }
}
