package utry.data.modular.complaints.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.modular.baseConfig.dao.TargetCoreConfigDao;
import utry.data.modular.baseConfig.dto.IndicatorDTO;
import utry.data.modular.common.dto.TemplateQueryDataDto;
import utry.data.modular.common.service.CommonTemplateService;
import utry.data.modular.complaints.dao.ComplaintsDao;
import utry.data.modular.complaints.dto.ComplaintDto;
import utry.data.modular.complaints.service.ComplaintsService;
import utry.data.modular.ccBoard.hotLineAgent.service.HotOrderFollowProcessService;
import utry.data.modular.indicatorWarning.service.IndicatorWarningService;
import utry.data.modular.settleManagement.utils.DateUtil;
import utry.data.util.HttpClientUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *投诉直辖业务接口实现
 */
@Service
public class ComplaintsServiceImpl implements ComplaintsService {

    @Resource
    private ComplaintsDao complaintsDao;

    @Resource
    private TargetCoreConfigDao targetCoreConfigDao;

    @Resource
    private SysConfServiceImpl sysConfService;

    @Resource
    private IndicatorWarningService indicatorWarningService;

    @Resource
    private HotOrderFollowProcessService hotOrderFollowProcessService;

    @Resource
    private CommonTemplateService commonTemplateService;

    /**
     * 投诉处理单推送SPI业务接口
     * @param map
     * @return
     */
    @Override
    public RetResult complaintDetail(Map map) {
        //动作
        String action = "";
        //投诉单号
        String complaintNumber = "";
        if(map.get("action")!=null){
            action = (String) map.get("action");
        }else{
            return RetResponse.makeErrRsp("动作不能为空！");
        }
        if(map.get("complaintNumber")!=null){
            complaintNumber = (String) map.get("complaintNumber");
        }else{
            return RetResponse.makeErrRsp("投诉单号不能为空！");
        }
        //通过投诉单号查询投诉单
        Map complaint = complaintsDao.complaintById(complaintNumber);
        if("新增".equals(action)){
            if(complaint!=null){
                return RetResponse.makeRsp(401,"已存在该投诉单号！");
            }
            complaintsDao.complaintDetailAdd(map);
        }else if("重启".equals(action)) {
            if (complaint == null) {
                return RetResponse.makeErrRsp("找不到该投诉单！");
            }
            complaintsDao.complaintDetailEdit(map);
            String dispatchingOrder = (String) map.get("dispatchingOrder");
            if(dispatchingOrder!=null){
                complaintsDao.solveEligibleToNull(dispatchingOrder);
            }
        }else {
            return RetResponse.makeErrRsp("该操作无法理解！");
        }
        //投诉留言处理
        if(map.get("complaintMessage")!=null){
            List<Map> complaintMessageList = (List<Map>) map.get("complaintMessage");
            if(complaintMessageList!=null&&complaintMessageList.size()>0){
                //给每条记录绑定当前投诉单号
                for (Map map1:complaintMessageList){
                    map1.put("complaintNumber",complaintNumber);
                }
                if("重启".equals(action)) {
                    complaintsDao.complaintMessageDel(complaintNumber);
                }
                //插入
                complaintsDao.complaintMessageAdd(complaintMessageList);
            }
        }
        //投诉处理明细处理
        if(map.get("complaintProcessDetail")!=null){
            List<Map> complaintProcessDetailList = (List<Map>) map.get("complaintProcessDetail");
            if(complaintProcessDetailList!=null&&complaintProcessDetailList.size()>0) {
                //给每条记录绑定当前投诉单号
                for (Map map1 : complaintProcessDetailList) {
                    map1.put("complaintNumber", complaintNumber);
                }
                if("重启".equals(action)) {
                    complaintsDao.complaintProcessDetailDel(complaintNumber);
                }
                //插入
                complaintsDao.complaintProcessDetailAdd(complaintProcessDetailList);
                //获取需要通知的投诉单号和部门
                Map<String, List<Map<String, String>>> noticeMap = complaintUpdateAdd(map);
                List<Map<String, String>> noticeList = noticeMap.get("noticeList");
                List<Map<String, String>> updateNoticeList = noticeMap.get("updateNoticeList");

                if (noticeList.size() > 0) {
                    //站内信通知
                    indicatorWarningService.stationLetter(noticeList);
                }

                if (updateNoticeList.size() > 0) {
                    //更新升级记录通知标志
                    complaintsDao.updateUpdateNotice(updateNoticeList);
                }
            }
        }
        return RetResponse.makeOKRsp();
    }

    /**
     * 热线服务单推送SPI业务接口
     * @param map
     * @return
     */
    @Override
    public RetResult hotLineDetail(Map map) {
        //热线编号
        String hotlineNumber = "";
        if(map.get("hotlineNumber")!=null){
            hotlineNumber = (String) map.get("hotlineNumber");
        }else{
            return RetResponse.makeErrRsp("热线编号不能为空！");
        }

        //通过热线编号查询热线服务单
        Map hotLine = complaintsDao.hotLineById(hotlineNumber);
        if(hotLine==null){
            complaintsDao.hotLineAdd(map);
        }else{
            complaintsDao.hotLineEdit(map);
        }

        // 热线服务单修改人员历史存储
        hotOrderFollowProcessService.insertHotOrderFollowProcess(map);

        //通过热线编号查询投诉表中是否存在投诉单号
        String complaintNumber = complaintsDao.selectComplainNumberByHotlineNumber(hotlineNumber);
        if (complaintNumber != null && !"".equals(complaintNumber)) {
            try {
                //投诉处理单单独拉取
                complaintAlonePull(complaintNumber);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        return RetResponse.makeOKRsp();
    }

    @Override
    public RetResult complaintAlonePull(String hotlineNumber) {
        //投诉处理单拉取（API）接口地址
        String url = "/GetComplaintDeal";
        //默认状态码
        int code = 400;
        Map map = new HashMap();
        Map data = new HashMap<>();
        try {
            String[] notFinishIds = {hotlineNumber};
            //传入单号集合调用接口获取数据
            map.put("complaintNumber",notFinishIds);
            //调用接口获取数据
            data = httpClient(url,map);
            code = (Integer) data.get("code");
            if(code==200) {
                //成功获取数据并且有数据则修改投诉单内容
                List<Map> complaintList = (List<Map>) data.get("data");
                if (complaintList != null && complaintList.size() > 0) {
                    //遍历返回的投诉单内容
                    for (Map complaint : complaintList) {
                        //更新投诉单内容
                        complaintsDao.complaintDetailEdit(complaint);
                        //获取投诉留言并添加投诉单号并插入投诉留言表信息
                        List<Map> complaintMessageList = (List<Map>) complaint.get("complaintMessage");
                        if (complaintMessageList != null && complaintMessageList.size() > 0) {
                            for (Map map1 : complaintMessageList) {
                                map1.put("complaintNumber", complaint.get("complaintNumber"));
                            }
                            complaintsDao.complaintMessageDel((String) complaint.get("complaintNumber"));
                            complaintsDao.complaintMessageAdd(complaintMessageList);
                        }
                        //获取投诉处理明细并添加投诉单号并插入投诉处理明细表信息
                        List<Map> complaintProcessList = (List<Map>) complaint.get("complaintProcessDetail");
                        if (complaintProcessList != null && complaintProcessList.size() > 0) {
                            for (Map map1 : complaintProcessList) {
                                map1.put("complaintNumber", complaint.get("complaintNumber"));
                            }
                            complaintsDao.complaintProcessDetailDel((String) complaint.get("complaintNumber"));
                            complaintsDao.complaintProcessDetailAdd(complaintProcessList);
                        }

                        //获取需要通知的投诉单号和部门
                        Map<String, List<Map<String, String>>> noticeMap = complaintUpdateAdd(complaint);
                        List<Map<String, String>> noticeList = noticeMap.get("noticeList");
                        List<Map<String, String>> updateNoticeList = noticeMap.get("updateNoticeList");

                        if (noticeList.size() > 0) {
                            //站内信通知
                            indicatorWarningService.stationLetter(noticeList);
                        }

                        if (updateNoticeList.size() > 0) {
                            //更新升级记录通知标志
                            complaintsDao.updateUpdateNotice(updateNoticeList);
                        }

                    }
                }
            }
        }catch (Exception e){
            //报错输出信息
            code = 400;
            data.put("message",e.getMessage());
            e.printStackTrace();
        }
        return RetResponse.makeRsp(code,(String)data.get("message"),data.get("data"));
    }

    /**
     * 履历信息业务推送接口
     * @param resumeList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public RetResult resumeDetail(List<Map<String,Object>> resumeList) {

        try {
            //新增履历信息业务
            complaintsDao.resumeDetail(resumeList);
        }catch (Exception e) {
            return RetResponse.makeErrRsp(e.getMessage());
        }

        return RetResponse.makeOKRsp();
    }

    //访问硕德接口统一方法
    Map httpClient(String url,Map param){
        //获取接口访问地址
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        url = IP + url;
        String postResult = "";
        Map map = new HashMap();
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(param));
            JSONObject jsonObject = JSONObject.parseObject(postResult);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            if("T".equals(map.get("RESULT"))){
                map.put("code",200);
            }else{
                map.put("code",400);
                map.put("message",map.get("ERRMSG"));
            }
        } catch (Exception e) {
            //e.printStackTrace();
            map.put("code",400);
            map.put("message",e.getMessage());
        }
        return map;
    }

    /**
     * 投诉升级记录添加
     * @param map
     */
    @Override
    public Map<String, List<Map<String, String>>> complaintUpdateAdd(Map map) {
        //通过核算中心查询部门信息
        List<Map<String,Object>> deptInfo = complaintsDao.selectDeptInfoByCenterCode((String)map.get("accountingCenterCode"));

        String complaintNumber = (String)map.get("complaintNumber");
        //通过投诉单号查询投诉升级信息
        List<Map> complaintUpdateList = complaintsDao.selectComplaintUpdateByNumber(complaintNumber);
        List<Map> complaintProcessDetailList = (List<Map>) map.get("complaintProcessDetail");

        //需要通知的投诉单号及部门
        List<Map<String,String>> noticeList = new ArrayList<>();
        //需要更新通知状态的投诉单号及时间
        List<Map<String,String>> updateNoticeList = new ArrayList<>();

        if (complaintUpdateList != null && complaintUpdateList.size() > 0) {
            //存放需要新增到升级记录表的数据
            List<Map> insertUpgradeRecord = new ArrayList<>();

            for (Map map1 : complaintProcessDetailList) {
                List<Map> collect = complaintUpdateList.stream().filter(a -> map1.get("completionProcessingTime").equals(a.get("completionProcessingTime"))).collect(Collectors.toList());

                if (collect.size() > 0) {
                    //存在于升级记录表并且满足通知的条件
                    if (collect.get(0).get("upgradeDepartmentId") != null && !"".equals(collect.get(0).get("upgradeDepartmentId"))
                        && "0".equals(collect.get(0).get("notice"))) {
                        Map<String,String> noticeMap = new HashMap<>();
                        Map<String,String> updateNoticeMap = new HashMap<>();
                        noticeMap.put("complaintNumber",complaintNumber);
                        noticeMap.put("departmentNumber",collect.get(0).get("upgradeDepartmentId").toString());
                        updateNoticeMap.put("complaintNumber",complaintNumber);
                        updateNoticeMap.put("completionProcessingTime",collect.get(0).get("completionProcessingTime").toString());
                        updateNoticeList.add(updateNoticeMap);
                        noticeList.add(noticeMap);
                    }else {
                        if ("0".equals(collect.get(0).get("notice"))) {
                            if (map1.get("upgradeDepartment") != null && !"".equals(map1.get("upgradeDepartment"))) {
                                //绑定部门编号
                                List<Map<String, Object>> dept = deptInfo.stream().filter(a -> map1.get("upgradeDepartment").equals(a.get("departmentName"))).collect(Collectors.toList());
                                if (dept.size() > 0) {
                                    map1.put("upgradeDepartmentId",dept.get(0).get("departmentNumber"));

                                    if(map1.get("upgradeDepartmentId") != null && "".equals(map1.get("upgradeDepartmentId"))) {
                                        Map<String,String> updateNoticeMap = new HashMap<>();
                                        Map<String,String> noticeMap = new HashMap<>();
                                        noticeMap.put("complaintNumber",complaintNumber);
                                        noticeMap.put("departmentNumber",map1.get("upgradeDepartmentId").toString());
                                        updateNoticeMap.put("complaintNumber",complaintNumber);
                                        updateNoticeMap.put("upgradeDepartmentId",map1.get("upgradeDepartmentId").toString());
                                        updateNoticeMap.put("upgradeDepartment",map1.get("upgradeDepartment").toString());
                                        updateNoticeMap.put("completionProcessingTime",map1.get("completionProcessingTime").toString());
                                        updateNoticeList.add(updateNoticeMap);
                                        noticeList.add(noticeMap);
                                    }
                                }
                            }
                        }
                    }
                }else {
                    //不存在于升级记录
                    map1.put("complaintNumber", complaintNumber);
                    //绑定部门编号
                    if (map1.get("upgradeDepartment") != null && !"".equals(map1.get("upgradeDepartment"))) {
                        List<Map<String, Object>> dept = deptInfo.stream().filter(a -> map1.get("upgradeDepartment").equals(a.get("departmentName"))).collect(Collectors.toList());
                        if (dept.size() > 0) {
                            map1.put("upgradeDepartmentId",dept.get(0).get("departmentNumber"));
                        }
                    }
                    //满足通知的条件
                    if (map1.get("upgradeDepartmentId") != null && !"".equals(map1.get("upgradeDepartmentId"))) {
                        Map<String,String> updateNoticeMap = new HashMap<>();
                        Map<String,String> noticeMap = new HashMap<>();
                        noticeMap.put("complaintNumber",complaintNumber);
                        noticeMap.put("departmentNumber",map1.get("upgradeDepartmentId").toString());
                        updateNoticeMap.put("complaintNumber",complaintNumber);
                        updateNoticeMap.put("completionProcessingTime",map1.get("completionProcessingTime").toString());
                        updateNoticeList.add(updateNoticeMap);
                        noticeList.add(noticeMap);
                    }
                    insertUpgradeRecord.add(map1);
                }
            }

            if (insertUpgradeRecord.size() > 0) {
                //插入升级记录
                complaintsDao.complaintRecordAdd(insertUpgradeRecord);
            }

        }else {
            //投诉处理升级记录新增
            if(map.get("complaintProcessDetail")!=null){
                if(complaintProcessDetailList!=null&&complaintProcessDetailList.size()>0) {
                    //给每条记录绑定当前投诉单号
                    for (Map map1 : complaintProcessDetailList) {
                        map1.put("complaintNumber", complaintNumber);
                        //绑定部门编号
                        if (map1.get("upgradeDepartment") != null && !"".equals(map1.get("upgradeDepartment"))) {
                            List<Map<String, Object>> dept = deptInfo.stream().filter(a -> map1.get("upgradeDepartment").equals(a.get("departmentName"))).collect(Collectors.toList());
                            if (dept.size() > 0) {
                                map1.put("upgradeDepartmentId",dept.get(0).get("departmentNumber"));
                            }
                        }
                        //满足通知的条件
                        if (map1.get("upgradeDepartmentId") != null && !"".equals(map1.get("upgradeDepartmentId"))) {
                            Map<String,String> updateNoticeMap = new HashMap<>();
                            Map<String,String> noticeMap = new HashMap<>();
                            noticeMap.put("complaintNumber",complaintNumber);
                            noticeMap.put("departmentNumber",map1.get("upgradeDepartmentId").toString());
                            updateNoticeMap.put("complaintNumber",complaintNumber);
                            updateNoticeMap.put("completionProcessingTime",map1.get("completionProcessingTime").toString());
                            updateNoticeList.add(updateNoticeMap);
                            noticeList.add(noticeMap);
                        }
                    }
                    //插入升级记录
                    complaintsDao.complaintRecordAdd(complaintProcessDetailList);
                }
            }
        }

        Map<String,List<Map<String,String>>> notice = new HashMap<>();
        notice.put("noticeList",noticeList);
        notice.put("updateNoticeList",updateNoticeList);

        return notice;
    }

    /**
     * 投诉7天解决率
     * @param complaintDto
     * @return
     */
    @Override
    public Map<String, Object> sevenDaySolveRate(ComplaintDto complaintDto) {

        //查询投诉7天解决率和投诉件数
        Map<String,Object> sevenDaySolve = complaintsDao.sevenDaySolveRate(complaintDto);
        //分组查询每天的解决率
        List<Map<String,Object>> daysSolve = complaintsDao.daysSolveRate(complaintDto);

        //存放所有的七天解决率数据
        List<Map<String,Object>> sevenDaysSolveList = new ArrayList<>();

        List<String> betweenDate = DateUtil.getBetweenDate(complaintDto.getBeginDate(),complaintDto.getEndDate());

        for (String date : betweenDate) {
            List<Map<String, Object>> solveRate = daysSolve.stream().filter(a -> date.equals(a.get("complaintStartTime").toString())).collect(Collectors.toList());
            if (solveRate.size() > 0) {
                sevenDaysSolveList.add(solveRate.get(0));
            }else {
                Map<String,Object> map = new HashMap<>();
                map.put("complaintStartTime",date);
                map.put("sevenSolveRate",0.0);
                sevenDaysSolveList.add(map);
            }
        }

        sevenDaySolve.put("daysSolve",sevenDaysSolveList);

        //获取当月目标
        List<IndicatorDTO> target = targetCoreConfigDao.select("complaint",complaintDto.getEndDate().substring(0,7));
        if (!target.isEmpty()) {
            for (IndicatorDTO indicatorDTO : target) {
                if ("complaintRate".equals(indicatorDTO.getIndicatorCode())) {
                    sevenDaySolve.put("target",indicatorDTO.getIndicatorValue());
                }
            }
            if (!sevenDaySolve.containsKey("target")) {
                sevenDaySolve.put("target",0.0);
            }
        }else {
            sevenDaySolve.put("target",0.0);
        }

        if ("5".equals(complaintDto.getDateRange())) {

            //如果是本月，查询上个月数据并计算环比
            try {
                //开始时间结束时间月份-1
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(complaintDto.getBeginDate()));
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                String firstDayOfMonth = DateUtil.getFirstDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
                String lastDayOfMonth = DateUtil.getLastDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

                //设置上个月的开始日期和结束日期
                complaintDto.setBeginDate(firstDayOfMonth);
                complaintDto.setEndDate(lastDayOfMonth);

                //查询上个月的投诉7天解决率
                Map<String,Object> lastMonthSolve = complaintsDao.sevenDaySolveRate(complaintDto);

                if ("0.0%".equals(lastMonthSolve.get("sevenSolveRate").toString())) {
                    sevenDaySolve.put("chainCompare","0.0%");
                }else {
                    //计算环比
                    float currentMonth = Float.parseFloat(sevenDaySolve.get("sevenSolveRate").toString().substring(0, sevenDaySolve.get("sevenSolveRate").toString().length() - 1));
                    float lastMonth = Float.parseFloat(lastMonthSolve.get("sevenSolveRate").toString().substring(0, lastMonthSolve.get("sevenSolveRate").toString().length() - 1));

                    String chainCompare = String.format("%.1f", (currentMonth - lastMonth)/lastMonth * 100).concat("%");

                    sevenDaySolve.put("chainCompare",chainCompare);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

        return sevenDaySolve;
    }

    /**
     * 投诉率
     * @param complaintDto
     * @return
     */
    @Override
    public Map<String, Object> complaintRate(ComplaintDto complaintDto) {

        //查询投诉率
        Map<String,Object> complaintRate = complaintsDao.complaintRate(complaintDto);

        //分组查询每天的投诉率
        List<Map<String,Object>> daysComplaintRate = complaintsDao.daysComplaintRate(complaintDto);

        //存放所有的投诉率数据
        List<Map<String,Object>> complaintRateList = new ArrayList<>();

        List<String> betweenDate = DateUtil.getBetweenDate(complaintDto.getBeginDate(),complaintDto.getEndDate());

        for (String date : betweenDate) {
            List<Map<String, Object>> complaint = daysComplaintRate.stream().filter(a -> date.equals(a.get("complaintStartTime").toString())).collect(Collectors.toList());
            if (complaint.size() > 0) {
                complaintRateList.add(complaint.get(0));
            }else {
                Map<String,Object> map = new HashMap<>();
                map.put("complaintStartTime",date);
                map.put("complaintRate",0.00);
                complaintRateList.add(map);
            }
        }

        complaintRate.put("daysComplaintRate",complaintRateList);

        //获取目标
        List<IndicatorDTO> target = targetCoreConfigDao.select("complaint",complaintDto.getEndDate().substring(0,7));
        if (!target.isEmpty()) {
            for (IndicatorDTO indicatorDTO : target) {
                if ("warningRate".equals(indicatorDTO.getIndicatorCode())) {
                    complaintRate.put("target",indicatorDTO.getIndicatorValue());
                }
            }
            if (!complaintRate.containsKey("target")) {
                complaintRate.put("target",0.00);
            }
        }else {
            complaintRate.put("target",0.00);
        }

        if ("5".equals(complaintDto.getDateRange())) {

            //如果是本月，查询上个月数据并计算环比
            try {
                //开始时间结束时间月份-1
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(complaintDto.getBeginDate()));
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                String firstDayOfMonth = DateUtil.getFirstDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
                String lastDayOfMonth = DateUtil.getLastDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

                //设置上个月的开始日期和结束日期
                complaintDto.setBeginDate(firstDayOfMonth);
                complaintDto.setEndDate(lastDayOfMonth);

                //查询上个月的投诉7天解决率
                Map<String,Object> lastMonthComplaintRate = complaintsDao.complaintRate(complaintDto);

                if ("0.00%".equals(lastMonthComplaintRate.get("complaintRate").toString())) {
                    complaintRate.put("chainCompare","0.00%");
                }else {
                    //计算环比
                    float currentMonth = Float.parseFloat(complaintRate.get("complaintRate").toString().substring(0, complaintRate.get("complaintRate").toString().length() - 1));
                    float lastMonth = Float.parseFloat(lastMonthComplaintRate.get("complaintRate").toString().substring(0, lastMonthComplaintRate.get("complaintRate").toString().length() - 1));

                    String chainCompare = String.format("%.2f", (currentMonth - lastMonth)/lastMonth * 100).concat("%");

                    complaintRate.put("chainCompare",chainCompare);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        return complaintRate;
    }
    
    /**
     * 投诉率 - 下钻页面之投诉率
     * @param complaintDto
     * @return
     */
    @Override
    public Map<String, Object> complaintRateRate(ComplaintDto complaintDto) {
    	// 根据按日聚合还是按月聚合转换时间
    	complaintDto = getFilterDate(complaintDto);
        //查询投诉率
        Map<String,Object> complaintRate = complaintsDao.complaintRate(complaintDto);
        
        // 先区分是按日聚合还是按月聚合
        if("1".equals(complaintDto.getPolymerizeWay())) {
        	// 按月聚合逻辑
        	List<Map<String,Object>> daysComplaintRate = complaintsDao.monthComplaintRate(complaintDto);
        	//存放所有的投诉率数据
            List<Map<String,Object>> complaintRateList = new ArrayList<>();
            List<String> betweenDate = DateUtil.getBetweenMonth(complaintDto.getBeginDate(),complaintDto.getEndDate());
            for (String date : betweenDate) {
                List<Map<String, Object>> complaint = daysComplaintRate.stream().filter(a -> date.equals(a.get("complaintStartTime").toString())).collect(Collectors.toList());
                if (complaint.size() > 0) {
                    complaintRateList.add(complaint.get(0));
                }else {
                    Map<String,Object> map = new HashMap<>();
                    map.put("complaintStartTime",date);
                    map.put("complaintRate",0.00);
                    complaintRateList.add(map);
                }
            }
            complaintRate.put("daysComplaintRate",complaintRateList);
        } else {
        	// 按日聚合逻辑
        	//分组查询每天的投诉率
            List<Map<String,Object>> daysComplaintRate = complaintsDao.daysComplaintRate(complaintDto);
            //存放所有的投诉率数据
            List<Map<String,Object>> complaintRateList = new ArrayList<>();
            List<String> betweenDate = DateUtil.getBetweenDate(complaintDto.getBeginDate(),complaintDto.getEndDate());
            for (String date : betweenDate) {
                List<Map<String, Object>> complaint = daysComplaintRate.stream().filter(a -> date.equals(a.get("complaintStartTime").toString())).collect(Collectors.toList());
                if (complaint.size() > 0) {
                    complaintRateList.add(complaint.get(0));
                }else {
                    Map<String,Object> map = new HashMap<>();
                    map.put("complaintStartTime",date);
                    map.put("complaintRate",0.00);
                    complaintRateList.add(map);
                }
            }
            complaintRate.put("daysComplaintRate",complaintRateList);
        }

        //获取目标
        List<IndicatorDTO> target = targetCoreConfigDao.select("complaint",complaintDto.getEndDate().substring(0,7));
        if (!target.isEmpty()) {
            for (IndicatorDTO indicatorDTO : target) {
                if ("warningRate".equals(indicatorDTO.getIndicatorCode())) {
                    complaintRate.put("target",indicatorDTO.getIndicatorValue());
                }
            }
            if (!complaintRate.containsKey("target")) {
                complaintRate.put("target",0.00);
            }
        }else {
            complaintRate.put("target",0.00);
        }

        //如果是本月，查询当月目标，上个月数据并计算环比
        if ("本月".equals(complaintDto.getDateRange())) {

            // 如果是本月，查询上个月数据并计算环比
            Calendar calendar = Calendar.getInstance();
            // 开始时间结束时间月份-1,设置为上个月时间
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            String firstDayOfLastMonth = DateUtil.getFirstDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
            String lastDayOfLastMonth = DateUtil.getLastDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

            //设置上个月的开始日期和结束日期
            complaintDto.setBeginDate(firstDayOfLastMonth);
            complaintDto.setEndDate(lastDayOfLastMonth);

            //查询上个月的投诉解决率
            Map<String,Object> lastMonthComplaintRate = complaintsDao.complaintRate(complaintDto);
            if ("0.00%".equals(lastMonthComplaintRate.get("complaintRate").toString())) {
                complaintRate.put("chainCompare","0.00%");
            }else {
                //计算环比
                float currentMonth = Float.parseFloat(complaintRate.get("complaintRate").toString().substring(0, complaintRate.get("complaintRate").toString().length() - 1));
                float lastMonth = Float.parseFloat(lastMonthComplaintRate.get("complaintRate").toString().substring(0, lastMonthComplaintRate.get("complaintRate").toString().length() - 1));
                String chainCompare = String.format("%.2f", (currentMonth - lastMonth)/lastMonth * 100).concat("%");
                complaintRate.put("chainCompare",chainCompare);
            }

            //分组查询上个月每天的投诉率
            List<Map<String,Object>> daysComplaintRateLastMonth = complaintsDao.daysComplaintRate(complaintDto);
            //存放上个月所有的投诉率数据
            List<Map<String,Object>> complaintRateListLastMonth = new ArrayList<>();
            List<String> betweenDatedaysComplaintRateLastMonth = DateUtil.getBetweenDate(complaintDto.getBeginDate(),complaintDto.getEndDate());
            for (String date : betweenDatedaysComplaintRateLastMonth) {
                List<Map<String, Object>> complaint = daysComplaintRateLastMonth.stream().filter(a -> date.equals(a.get("complaintStartTime").toString())).collect(Collectors.toList());
                if (complaint.size() > 0) {
                    complaintRateListLastMonth.add(complaint.get(0));
                }else {
                    Map<String,Object> map = new HashMap<>();
                    map.put("complaintStartTime",date);
                    map.put("complaintRate",0.00);
                    complaintRateListLastMonth.add(map);
                }
            }
            complaintRate.put("daysComplaintRateLastMonth", complaintRateListLastMonth);
        }

        return complaintRate;
    }
    
    /**
     * 投诉率 - 下钻页面之投诉原因
     * @param complaintDto
     * @return
     */
    @Override
    public Object complaintRateReason(ComplaintDto complaintDto) {
    	// 根据按日聚合还是按月聚合转换时间
    	complaintDto = getFilterDate(complaintDto);
    	// 根据投诉类型和分类分组查询获得数据
    	List<Map<String, Object>> complaintReason = complaintsDao.complaintRateReason(complaintDto);
    	// 组装返回数据格式
    	Map<String, Map<String, Object>> dataMap = new HashMap<>(16);
    	for(Map<String, Object> complaint : complaintReason) {
    		
    		String complaintType = (String) complaint.get("complaintType");
    		String complaintClassify = (String) complaint.get("complaintClassify");
    		long number = (long) complaint.get("number");
    		
    		Map<String, Object> complaintTypeObj = dataMap.get(complaintType);
    		// 第一次遇见类型
    		if(null == complaintTypeObj) {
    			complaintTypeObj = new HashMap<>(16);
    			complaintTypeObj.put("complaintType", complaintType);
    			complaintTypeObj.put("typeNumber", number);
    			Map<String, Object> complaintClassifyMap = new HashMap<>(16);
    			complaintClassifyMap.put(complaintClassify, number);
    			complaintTypeObj.put("complaintClassify", complaintClassifyMap);
    			dataMap.put(complaintType, complaintTypeObj);
    		} else {
    			complaintTypeObj.put("typeNumber", (long) complaintTypeObj.get("typeNumber") + number);
    			@SuppressWarnings("unchecked")
				Map<String, Object> complaintClassifyMap = (Map<String, Object>) complaintTypeObj.get("complaintClassify");
    			complaintClassifyMap.put(complaintClassify, number);
    		}
    	}
    	return dataMap.values();
    }
    
    /**
     * 投诉率 - 下钻页面之产品品类投诉率
     * @param complaintDto
     * @return
     */
    @Override
    public List<Map<String, Object>> complaintRateProductCategory(ComplaintDto complaintDto) {
    	// 根据按日聚合还是按月聚合转换时间
    	complaintDto = getFilterDate(complaintDto);
    	// 根据投诉类型和分类分组查询获得数据
    	List<Map<String, Object>> complaintProductCategory = complaintsDao.complaintRateProductCategory(complaintDto);
		// 先按升序排好
    	Collections.sort(complaintProductCategory, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
                if (Double.parseDouble(arg0.get("number").toString()) - Double.parseDouble(arg1.get("number").toString()) >= 0) {
                    return 1;
                }else {
                    return -1;
                }
			}
    	});
    	if(null != complaintDto.getOrderQuery() && complaintDto.getOrderQuery().equals("desc")) {
    		Collections.reverse(complaintProductCategory);
    	}
    	return complaintProductCategory;
    }
    
    /**
     * 投诉率 - 下钻页面之大区投诉率
     * @param complaintDto
     * @return
     */
    @Override
    public List<Map<String, Object>> complaintRateAccountingCenter(ComplaintDto complaintDto) {
    	// 根据按日聚合还是按月聚合转换时间
    	complaintDto = getFilterDate(complaintDto);
    	// 根据投诉类型和分类分组查询获得数据
    	List<Map<String, Object>> complaintAccountingCenter = complaintsDao.complaintRateAccountingCenter(complaintDto);
        complaintAccountingCenter = complaintAccountingCenter.stream().filter(a -> a.get("accountingArea") != null && !"".equals(a.get("accountingArea"))).collect(Collectors.toList());
    	// 先按升序排好
    	Collections.sort(complaintAccountingCenter, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
                if (Double.parseDouble(arg0.get("number").toString()) - Double.parseDouble(arg1.get("number").toString()) >= 0) {
                    return 1;
                }else {
                    return -1;
                }
			}
    	});
    	if(null != complaintDto.getOrderQuery() && complaintDto.getOrderQuery().equals("desc")) {
    		Collections.reverse(complaintAccountingCenter);
    	}
    	return complaintAccountingCenter;
    }
    
    /**
     * 投诉率 - 下钻页面之地域(省份)投诉率
     * @param complaintDto
     * @return
     */
    @Override
    public List<Map<String, Object>> complaintRateProvinces(ComplaintDto complaintDto) {
    	// 根据投诉类型和分类分组查询获得数据
    	List<Map<String, Object>> complaintProvinces = complaintsDao.complaintRateProvinces(complaintDto);
    	// 先按升序排好
    	Collections.sort(complaintProvinces, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
                if (Double.parseDouble(arg0.get("number").toString()) - Double.parseDouble(arg1.get("number").toString()) >= 0) {
                    return 1;
                }else {
                    return -1;
                }
			}
    	});
    	if(null != complaintDto.getOrderQuery() && complaintDto.getOrderQuery().equals("desc")) {
    		Collections.reverse(complaintProvinces);
    	}
    	return complaintProvinces;
    }
    

    /**
     * 未结案
     * @param complaintDto
     * @return
     */
    @Override
    public Map<String, Object> notOverCase(ComplaintDto complaintDto) {

        //查询未结案数据
        List<Map<String,Object>> notOverCaseList = complaintsDao.notOverCase(complaintDto);

        //查询未结案总数
        Map<String,Object> notOverCase = complaintsDao.notOverCaseNumber(complaintDto);

        notOverCase.put("complaintClassify",notOverCaseList);

        return notOverCase;
    }

    /**
     * 全国投诉地图
     * @param complaintDto
     * @return
     */
    @Override
    public Map<String, Object> nationalComplaintMap(ComplaintDto complaintDto) {

        Map<String,Object> complainProportion = null;

        List<Map<String,Object>> complain = null;

        if ("1".equals(complaintDto.getComplaintFlag())) {
            //查询投诉量占比
            complainProportion = complaintsDao.selectComplainNumberProportion(complaintDto);
        }else {
            //按省份查询投诉7天解决率占比
            complainProportion = complaintsDao.selectComplainSolveProportion(complaintDto);
        }

        //按省份查询投诉量或投诉7天解决率
        complain = complaintsDao.selectComplainNumberByProvince(complaintDto);
        if (complain != null && complain.size() > 0) {
            for (Map<String, Object> map : complain) {
                if (map.get("name").toString().contains("内蒙古") || map.get("name").toString().contains("黑龙江")) {
                    map.put("name",map.get("name").toString().substring(0,3));
                }else {
                    map.put("name",map.get("name").toString().substring(0,2));
                }
            }

            //判断达标不达标
            if ("2".equals(complaintDto.getComplaintFlag())) {
                int mapTarget = 0;
                //获取当月目标
                List<IndicatorDTO> target = targetCoreConfigDao.select("complaint",complaintDto.getEndDate().substring(0,7));
                if (!target.isEmpty()) {
                    for (IndicatorDTO indicatorDTO : target) {
                        if ("complaintRate".equals(indicatorDTO.getIndicatorCode())) {
                            mapTarget = Integer.parseInt(indicatorDTO.getIndicatorValue());
                        }
                    }
                }

                int finalMapTarget = mapTarget;
                complain.forEach(e->{
                    if (new BigDecimal(NullToZero(e.get("value"))).
                            compareTo(new BigDecimal(NullToZero(finalMapTarget))) > 0) {
                        e.put("target", "1");
                    } else {
                        e.put("target", "0");
                    }
                });
            }
        }
        complainProportion.put("complain",complain);

        return complainProportion;
    }

    /**
     * 空转换为0
     * @param object
     * @return
     */
    public String NullToZero(Object object){
        if(object!=null && StringUtils.isNotEmpty(object.toString()) && !"-".equals(object.toString())){
            return object.toString();
        }else {
            return "0";
        }
    }

    /**
     * 排名
     * @param complaintDto
     * @return
     */
    @Override
    public List<Map<String, Object>> rankMap(ComplaintDto complaintDto) {

        List<Map<String,Object>> complainRank = null;

        if ("1".equals(complaintDto.getComplaintFlag())) {
            //查询投诉量排名
            complainRank = complaintsDao.selectComplainNumberRank(complaintDto);
        }else {
            //查询投诉7天解决率排名
            complainRank = complaintsDao.selectComplainSolveRank(complaintDto);
        }

        //筛选剔除空数据
        complainRank = complainRank.stream().filter(a -> a.get("name") != null && !"".equals(a.get("name"))).collect(Collectors.toList());

        if("1".equals(complaintDto.getSort())){
            complainRank.sort(Comparator.comparing((Map<String,Object> m) -> (Double.parseDouble(m.get("rankNumber")==null?"-1":m.get("rankNumber").toString()))));
        }else {
            complainRank.sort(Comparator.comparing((Map<String,Object> m) -> (Double.parseDouble(m.get("rankNumber")==null?"-1":m.get("rankNumber").toString()))).reversed());
        }

        if ("2".equals(complaintDto.getComplaintFlag())) {
            for (Map<String, Object> map : complainRank) {
                map.put("rankNumber",map.get("rankNumber") + "%");
            }
        }

        return complainRank;
    }

    /**
     * 投诉异常监控
     * @param complaintDto
     * @return
     */
    @Override
    public List<Map<String, Object>> complaintAbnormalMonitor(ComplaintDto complaintDto) {

        //存放节点状态信息list
        List<Map<String,Object>> stateDataList = new ArrayList<>();

        //投诉异常监控
        List<Map<String,Object>> complaintAbnormalMonitor = complaintsDao.complaintAbnormalMonitor(complaintDto);

        for (int i = 0; i < 5; i++) {
            Map<String, Object> stateMap = new HashMap<>();
            if (i == 0) {
                if (complaintAbnormalMonitor.isEmpty()) {
                    stateMap.put("nodeName", "处理单创建");
                    stateMap.put("nodeNumber", 0);
                    stateMap.put("governComplaint", 0);
                    stateMap.put("mediaComplaint", 0);
                    stateMap.put("timeOutNumber", 0);
                }else {
                    for (int j = 0; j < complaintAbnormalMonitor.size(); j++) {
                        Map<String, Object> statementMap = complaintAbnormalMonitor.get(j);
                        if (statementMap.containsValue("处理单创建")) {
                            stateMap.put("nodeName", "处理单创建");
                            stateMap.put("nodeNumber", statementMap.get("nodeNumber"));
                            stateMap.put("governComplaint", statementMap.get("governComplaint"));
                            stateMap.put("mediaComplaint", statementMap.get("mediaComplaint"));
                            stateMap.put("timeOutNumber", statementMap.get("timeOutNumber"));
                            break;
                        }else {
                            if (j == complaintAbnormalMonitor.size() - 1) {
                                stateMap.put("nodeName", "处理单创建");
                                stateMap.put("nodeNumber", 0);
                                stateMap.put("governComplaint", 0);
                                stateMap.put("mediaComplaint", 0);
                                stateMap.put("timeOutNumber", 0);
                            }
                        }
                    }
                }
            } else if (i == 1) {
                if (complaintAbnormalMonitor.isEmpty()) {
                    stateMap.put("nodeName", "解决方案提交");
                    stateMap.put("nodeNumber", 0);
                    stateMap.put("governComplaint", 0);
                    stateMap.put("mediaComplaint", 0);
                    stateMap.put("timeOutNumber", 0);
                    stateMap.put("stateTime", 0);
                }else {
                    for (int j = 0; j < complaintAbnormalMonitor.size(); j++) {
                        Map<String, Object> statementMap = complaintAbnormalMonitor.get(j);
                        if (statementMap.containsValue("解决方案提交")) {
                            stateMap.put("nodeName", "解决方案提交");
                            stateMap.put("nodeNumber", statementMap.get("nodeNumber"));
                            stateMap.put("governComplaint", statementMap.get("governComplaint"));
                            stateMap.put("mediaComplaint", statementMap.get("mediaComplaint"));
                            stateMap.put("timeOutNumber", statementMap.get("timeOutNumber"));
                            stateMap.put("stateTime", statementMap.get("submissionTime"));
                            break;
                        } else {
                            if (j == complaintAbnormalMonitor.size() - 1) {
                                stateMap.put("nodeName", "解决方案提交");
                                stateMap.put("nodeNumber", 0);
                                stateMap.put("governComplaint", 0);
                                stateMap.put("mediaComplaint", 0);
                                stateMap.put("timeOutNumber", 0);
                                stateMap.put("stateTime", 0);
                            }
                        }
                    }
                }
            } else if (i == 2) {
                if (complaintAbnormalMonitor.isEmpty()) {
                    stateMap.put("nodeName", "处理升级");
                    stateMap.put("nodeNumber", 0);
                    stateMap.put("governComplaint", 0);
                    stateMap.put("mediaComplaint", 0);
                    stateMap.put("timeOutNumber", 0);
                    stateMap.put("stateTime", 0);
                }else {
                    for (int j = 0; j < complaintAbnormalMonitor.size(); j++) {
                        Map<String, Object> statementMap = complaintAbnormalMonitor.get(j);
                        if (statementMap.containsValue("处理升级")) {
                            stateMap.put("nodeName", "处理升级");
                            stateMap.put("nodeNumber", statementMap.get("nodeNumber"));
                            stateMap.put("governComplaint", statementMap.get("governComplaint"));
                            stateMap.put("mediaComplaint", statementMap.get("mediaComplaint"));
                            stateMap.put("timeOutNumber", statementMap.get("timeOutNumber"));
                            stateMap.put("stateTime", statementMap.get("upgradeTime"));
                            break;
                        } else {
                            if (j == complaintAbnormalMonitor.size() - 1) {
                                stateMap.put("nodeName", "处理升级");
                                stateMap.put("nodeNumber", 0);
                                stateMap.put("governComplaint", 0);
                                stateMap.put("mediaComplaint", 0);
                                stateMap.put("timeOutNumber", 0);
                                stateMap.put("stateTime", 0);
                            }
                        }
                    }
                }
            } else if (i == 3) {
                if (complaintAbnormalMonitor.isEmpty()) {
                    stateMap.put("nodeName", "方案落地");
                    stateMap.put("nodeNumber", 0);
                    stateMap.put("governComplaint", 0);
                    stateMap.put("mediaComplaint", 0);
                    stateMap.put("timeOutNumber", 0);
                    stateMap.put("stateTime", 0);
                }else {
                    for (int j = 0; j < complaintAbnormalMonitor.size(); j++) {
                        Map<String, Object> statementMap = complaintAbnormalMonitor.get(j);
                        if (statementMap.containsValue("方案落地")) {
                            stateMap.put("nodeName", "方案落地");
                            stateMap.put("nodeNumber", statementMap.get("nodeNumber"));
                            stateMap.put("governComplaint", statementMap.get("governComplaint"));
                            stateMap.put("mediaComplaint", statementMap.get("mediaComplaint"));
                            stateMap.put("timeOutNumber", statementMap.get("timeOutNumber"));
                            stateMap.put("stateTime", statementMap.get("landTime"));
                            break;
                        } else {
                            if (j == complaintAbnormalMonitor.size() - 1) {
                                stateMap.put("nodeName", "方案落地");
                                stateMap.put("nodeNumber", 0);
                                stateMap.put("governComplaint", 0);
                                stateMap.put("mediaComplaint", 0);
                                stateMap.put("timeOutNumber", 0);
                                stateMap.put("stateTime", 0);
                            }
                        }
                    }
                }
            } else {
                if (complaintAbnormalMonitor.isEmpty()) {
                    stateMap.put("nodeName", "CC回访结案");
                    stateMap.put("nodeNumber", 0);
                    stateMap.put("governComplaint", 0);
                    stateMap.put("mediaComplaint", 0);
                    stateMap.put("stateTime", 0);
                }else {
                    for (int j = 0; j < complaintAbnormalMonitor.size(); j++) {
                        Map<String, Object> statementMap = complaintAbnormalMonitor.get(j);
                        if (statementMap.containsValue("CC回访结案")) {
                            stateMap.put("nodeName", "CC回访结案");
                            stateMap.put("nodeNumber", statementMap.get("nodeNumber"));
                            stateMap.put("governComplaint", statementMap.get("governComplaint"));
                            stateMap.put("mediaComplaint", statementMap.get("mediaComplaint"));
                            stateMap.put("stateTime", statementMap.get("endTime"));
                            break;
                        } else {
                            if (j == complaintAbnormalMonitor.size() - 1) {
                                stateMap.put("nodeName", "CC回访结案");
                                stateMap.put("nodeNumber", 0);
                                stateMap.put("governComplaint", 0);
                                stateMap.put("mediaComplaint", 0);
                                stateMap.put("stateTime", 0);
                            }
                        }
                    }
                }
            }
            stateDataList.add(stateMap);
        }
        return stateDataList;
    }

    /**
     * 查询单据列表
     * @param complaintDto
     * @return
     */
    @Override
    public Object selectDocumentsList(ComplaintDto complaintDto,Integer pageNum,Integer pageSize) {

        //查询单据列表
        List<Map<String,Object>> documentsList = complaintsDao.selectDocumentsList(complaintDto);

        DecimalFormat df = new DecimalFormat("0.00");//保留两位小数
        for (Map<String, Object> map : documentsList) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                if ("totalDays".equals(key)) {
                    map.put(key,df.format(Double.parseDouble(map.get(key).toString())));
                }
            }
        }

        if (complaintDto.getScreenList() != null && complaintDto.getScreenList().size() > 0) {
            //将筛选和排序条件拼接
            documentsList = getQueryField(complaintDto,documentsList);

            //分页
            int startRow = pageNum * pageSize - pageSize;
            int size = documentsList.size();
            documentsList = documentsList.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("list", documentsList);
            jsonObject.put("total", size);

            return jsonObject;
        }

        return documentsList;
    }

    /**
     * 投诉原因分析
     * @param complaintDto
     * @return
     */
    @Override
    public List<Map<String, Object>> complaintReasonAnalysis(ComplaintDto complaintDto) {

        complaintDto = getFilterDate(complaintDto);

        List<Map<String,Object>> complainReasonList = complaintsDao.complaintReasonAnalysis(complaintDto);
        for (Map<String, Object> map : complainReasonList) {
            HashMap<String, Object> classifyMap = new HashMap<>();
//            JSONObject jsonObject = JSON.parseObject(map.get("complaintClassify").toString());
//            Map complaintClassify = new Gson().fromJson(map.get("complaintClassify").toString(), map.getClass());
//            HashMap hashMap = JSON.parseObject(map.get("complaintClassify").toString(), HashMap.class);
//            String complaintClassify = "{" + map.get("complaintClassify") + "}";
            String[] classifies = map.get("complaintClassify").toString().split(",");
            for (int i = 0; i < classifies.length; i++) {
                String[] split = classifies[i].split(":");
                classifyMap.put(split[0],split[1]);
            }
//            Map hash = JSONObject.parseObject(complaintClassify,Map.class);
            map.put("complaintClassify",classifyMap);
        }
        return complainReasonList;
    }

    /**
     * 内部投诉7天解决率
     * @param complaintDto
     * @return
     */
    @Override
    public Map<String, Object> innerSevenDaySolveRate(ComplaintDto complaintDto) {

        complaintDto = getFilterDate(complaintDto);

        //按月或按日查询投诉7天解决率和投诉件数
        Map<String,Object> sevenDaySolve = complaintsDao.sevenDaySolveRate(complaintDto);
        //分组查询每月或每天的解决率
        List<Map<String,Object>> averageSolve = complaintsDao.averageSolveRate(complaintDto);

        //存放所有的七天解决率数据
        List<Map<String,Object>> sevenDaysSolveList = new ArrayList<>();

        List<String> betweenDate = null;
        if ("1".equals(complaintDto.getPolymerizeWay())) {
            betweenDate = DateUtil.getBetweenMonth(complaintDto.getBeginDate(),complaintDto.getEndDate());
        }else {
            betweenDate = DateUtil.getBetweenDate(complaintDto.getBeginDate(),complaintDto.getEndDate());
        }

        for (String date : betweenDate) {
            List<Map<String, Object>> solveRate = averageSolve.stream().filter(a -> date.equals(a.get("complaintStartTime").toString())).collect(Collectors.toList());
            if (solveRate.size() > 0) {
                sevenDaysSolveList.add(solveRate.get(0));
            }else {
                Map<String,Object> map = new HashMap<>();
                map.put("complaintStartTime",date);
                map.put("sevenSolveRate",0.0);
                sevenDaysSolveList.add(map);
            }
        }

        sevenDaySolve.put("daysSolve",sevenDaysSolveList);

        //获取当月目标
        List<IndicatorDTO> target = targetCoreConfigDao.select("complaint",complaintDto.getEndDate().substring(0,7));
        if (!target.isEmpty()) {
            for (IndicatorDTO indicatorDTO : target) {
                if ("complaintRate".equals(indicatorDTO.getIndicatorCode())) {
                    sevenDaySolve.put("target",indicatorDTO.getIndicatorValue());
                }
            }
            if (!sevenDaySolve.containsKey("target")) {
                sevenDaySolve.put("target",0.0);
            }
        }else {
            sevenDaySolve.put("target",0.0);
        }

        if ("本月".equals(complaintDto.getDateRange())) {

            //如果是本月，查询上个月数据并计算环比
            try {
                //开始时间结束时间月份-1
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(complaintDto.getBeginDate()));
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                String firstDayOfMonth = DateUtil.getFirstDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
                String lastDayOfMonth = DateUtil.getLastDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

                //设置上个月的开始日期和结束日期
                complaintDto.setBeginDate(firstDayOfMonth);
                complaintDto.setEndDate(lastDayOfMonth);

                //查询上个月的投诉7天解决率
                Map<String,Object> lastMonthSolve = complaintsDao.sevenDaySolveRate(complaintDto);

                if ("0.0%".equals(lastMonthSolve.get("sevenSolveRate").toString())) {
                    sevenDaySolve.put("chainCompare","0.0%");
                }else {
                    //计算环比
                    float currentMonth = Float.parseFloat(sevenDaySolve.get("sevenSolveRate").toString().substring(0, sevenDaySolve.get("sevenSolveRate").toString().length() - 1));
                    float lastMonth = Float.parseFloat(lastMonthSolve.get("sevenSolveRate").toString().substring(0, lastMonthSolve.get("sevenSolveRate").toString().length() - 1));

                    String chainCompare = String.format("%.1f", (currentMonth - lastMonth)/lastMonth * 100).concat("%");

                    sevenDaySolve.put("chainCompare",chainCompare);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

        return sevenDaySolve;
    }

    /**
     * N+1解决方案及时提交率
     * @param complaintDto
     * @return
     */
    @Override
    public Map<String, Object> complaintSolveSubmissionRate(ComplaintDto complaintDto) {

        complaintDto = getFilterDate(complaintDto);

        //按月或按日查询N+1解决方案及时提交率
        List<Map<String,Object>> daysSubmissionRate = complaintsDao.daysSubmissionRate(complaintDto);

        //查询N+1解决方案及时提交率
        Map<String, Object> submissionRate = complaintsDao.complaintSolveSubmissionRate(complaintDto);

        //存放所有的N+1提交率数据
        List<Map<String,Object>> submissionRateList = new ArrayList<>();

        List<String> betweenDate = null;
        if ("1".equals(complaintDto.getPolymerizeWay())) {
            betweenDate = DateUtil.getBetweenMonth(complaintDto.getBeginDate(),complaintDto.getEndDate());
        }else {
            betweenDate = DateUtil.getBetweenDate(complaintDto.getBeginDate(),complaintDto.getEndDate());
        }

        for (String date : betweenDate) {
            List<Map<String, Object>> solveRate = daysSubmissionRate.stream().filter(a -> date.equals(a.get("complaintStartTime").toString())).collect(Collectors.toList());
            if (solveRate.size() > 0) {
                submissionRateList.add(solveRate.get(0));
            }else {
                Map<String,Object> map = new HashMap<>();
                map.put("complaintStartTime",date);
                map.put("submissionRate",0.0);
                submissionRateList.add(map);
            }
        }

        submissionRate.put("daysSubmissionRate", submissionRateList);

        if ("本月".equals(complaintDto.getDateRange())) {
            //如果是本月，查询上个月数据并计算环比
            try {
                //开始时间结束时间月份-1
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(complaintDto.getBeginDate()));
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                String firstDayOfMonth = DateUtil.getFirstDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
                String lastDayOfMonth = DateUtil.getLastDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

                //设置上个月的开始日期和结束日期
                complaintDto.setBeginDate(firstDayOfMonth);
                complaintDto.setEndDate(lastDayOfMonth);

                //查询上个月的N+1解决方案及时提交率
                Map<String,Object> lastMonthSubmission = complaintsDao.complaintSolveSubmissionRate(complaintDto);

                if ("0.0%".equals(lastMonthSubmission.get("submissionRate").toString())) {
                    submissionRate.put("chainCompare","0.0%");
                }else {
                    //计算环比
                    float currentMonth = Float.parseFloat(submissionRate.get("submissionRate").toString().substring(0, submissionRate.get("submissionRate").toString().length() - 1));
                    float lastMonth = Float.parseFloat(lastMonthSubmission.get("submissionRate").toString().substring(0, lastMonthSubmission.get("submissionRate").toString().length() - 1));

                    String chainCompare = String.format("%.1f", (currentMonth - lastMonth)/lastMonth * 100).concat("%");

                    submissionRate.put("chainCompare",chainCompare);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        return submissionRate;
    }

    /**
     * 投诉来源分析
     * @param complaintDto
     * @return
     */
    @Override
    public List<Map<String, Object>> complaintSourceAnalysis(ComplaintDto complaintDto) {

        complaintDto = getFilterDate(complaintDto);

        //投诉来源分析
        List<Map<String,Object>> sourceAnalysis = complaintsDao.complaintSourceAnalysis(complaintDto);

        return sourceAnalysis;
    }

    /**
     * 品类投诉分析
     * @param complaintDto
     * @return
     */
    @Override
    public List<Map<String, Object>> categoryComplaintAnalysis(ComplaintDto complaintDto) {

        complaintDto = getFilterDate(complaintDto);

        //品类投诉分析
        List<Map<String,Object>> categoryAnalysis = complaintsDao.categoryComplaintAnalysis(complaintDto);

        return categoryAnalysis;
    }

    /**
     * 全部大区
     * @return
     */
    @Override
    public List<Map<String, Object>> selectAllRegion() {
        return complaintsDao.selectAllRegion();
    }

    /**
     * 根据投诉单号查询投诉详情
     * @return
     */
    @Override
    public Map<String, Object> selectComplaintDetailByNumber(Map<String,Object> map) {
        Map<String, Object> complaintDetail = complaintsDao.selectComplaintDetailByNumber(map);

        String phoneDescribe = complaintDetail.get("phoneDescribe").toString().replace("\r\n", "\n").replace("\\r\\n","\n").replace("\r", "\n");
        complaintDetail.put("phoneDescribe",phoneDescribe);

        //存放时间轴数据
        List<Map<String,Object>> stateList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Map<String,Object> stateMap = new HashMap<>();
            if (i == 0) {
                stateMap.put("nodeName","处理单创建");
                stateMap.put("stateTime",complaintDetail.get("complaintStartTime"));
                stateMap.put("isComplete","1");
            }
            if (i == 1) {
                if ("".equals(complaintDetail.get("solutionSubmissionTime"))) {
                    if ("".equals(complaintDetail.get("complaintEndTime"))) {
                        stateMap.put("nodeName","解决方案提交");
                        stateMap.put("stateTime","预计" + complaintDetail.get("predictSubmissionTime").toString() + "完成");
                        stateMap.put("isComplete","0");
                    }else {
                        stateMap.put("nodeName","解决方案提交");
                        stateMap.put("stateTime",complaintDetail.get("solutionSubmissionTime"));
                        stateMap.put("isComplete","0");
                    }
                }else {
                    stateMap.put("nodeName","解决方案提交");
                    stateMap.put("stateTime",complaintDetail.get("solutionSubmissionTime"));
                    stateMap.put("isComplete","1");
                }
            }
            if (i == 2) {
                stateMap.put("nodeName","处理升级");
                stateMap.put("stateTime",complaintDetail.get("upgradeTime"));
                if ("".equals(complaintDetail.get("upgradeTime"))) {
                    stateMap.put("isComplete","0");
                }else {
                    stateMap.put("isComplete","1");
                }
            }
            if (i == 3) {
                stateMap.put("nodeName","方案落地");
                stateMap.put("stateTime",complaintDetail.get("solutionLandingTime"));
                if ("".equals(complaintDetail.get("solutionLandingTime"))) {
                    stateMap.put("isComplete","0");
                }else {
                    stateMap.put("isComplete","1");
                }
            }
            if (i == 4) {
                stateMap.put("nodeName","CC回访结案");
                stateMap.put("stateTime",complaintDetail.get("complaintEndTime"));
                if ("".equals(complaintDetail.get("complaintEndTime"))) {
                    stateMap.put("isComplete","0");
                }else {
                    stateMap.put("isComplete","1");
                }
            }
            stateList.add(stateMap);
        }

        complaintDetail.put("stateList",stateList);

        return complaintDetail;
    }

    /**
     * 根据投诉单号查询投诉留言信息
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> selectComplaintMessageByNumber(Map<String, Object> map) {
        return complaintsDao.selectComplaintMessageByNumber(map);
    }

    /**
     * 根据投诉单号查询投诉处理明细
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> selectComplaintProcessDetailByNumber(Map<String, Object> map) {
        return complaintsDao.selectComplaintProcessDetailByNumber(map);
    }

    /**
     * 全省投诉地图
     * @param complaintDto
     * @return
     */
    @Override
    public Map<String, Object> provinceComplaintMap(ComplaintDto complaintDto) {

        Map<String,Object> complainProportion = null;

        List<Map<String,Object>> complain = null;

        if ("1".equals(complaintDto.getComplaintFlag())) {
            //查询投诉量占比
            complainProportion = complaintsDao.selectComplainNumberProportion(complaintDto);
        }else {
            //按市查询投诉7天解决率占比
            complainProportion = complaintsDao.selectComplainSolveProportionByCity(complaintDto);
        }

        //按市查询投诉量或投诉7天解决率
        complain = complaintsDao.selectComplainNumberByCity(complaintDto);

        if (complain != null && complain.size() > 0) {
            //判断达标不达标
            if ("2".equals(complaintDto.getComplaintFlag())) {
                int mapTarget = 0;
                //获取当月目标
                List<IndicatorDTO> target = targetCoreConfigDao.select("complaint",complaintDto.getEndDate().substring(0,7));
                if (!target.isEmpty()) {
                    for (IndicatorDTO indicatorDTO : target) {
                        if ("complaintRate".equals(indicatorDTO.getIndicatorCode())) {
                            mapTarget = Integer.parseInt(indicatorDTO.getIndicatorValue());
                        }
                    }
                }

                int finalMapTarget = mapTarget;
                complain.forEach(e->{
                    if (new BigDecimal(NullToZero(e.get("value"))).
                            compareTo(new BigDecimal(NullToZero(finalMapTarget))) > 0) {
                        e.put("target", "1");
                    } else {
                        e.put("target", "0");
                    }
                });
            }
        }

        complainProportion.put("complain",complain);

        return complainProportion;
    }

    /**
     * 门店排名
     * @param complaintDto
     * @return
     */
    @Override
    public List<Map<String, Object>> storeRank(ComplaintDto complaintDto) {

        List<Map<String,Object>> complainRank = complaintsDao.selectComplainByStore(complaintDto);

        if (complainRank != null && complainRank.size() > 0) {
            if("1".equals(complaintDto.getSort())){
                complainRank.sort(Comparator.comparing((Map<String,Object> m) -> (Double.parseDouble(m.get("rankNumber")==null?"-1":m.get("rankNumber").toString()))));
            }else {
                complainRank.sort(Comparator.comparing((Map<String,Object> m) -> (Double.parseDouble(m.get("rankNumber")==null?"-1":m.get("rankNumber").toString()))).reversed());
            }
        }

//        if ("1".equals(complaintDto.getComplaintFlag())) {
//            //查询投诉量排名
//            complainRank = complaintsDao.selectComplainNumberRank(complaintDto);
//        }else {
//            //查询投诉7天解决率排名
//            complainRank = complaintsDao.selectComplainSolveRank(complaintDto);
//            for (Map<String, Object> map : complainRank) {
//                map.put("rankNumber",map.get("rankNumber") + "%");
//            }
//        }
        return complainRank;
    }

    /**
     * 对外接口投诉7天解决率
     * @param map
     * @return
     */
    @Override
    public Map<String,Object> apiSevenSolveRate(Map<String,Object> map) {

        ComplaintDto complaintDto = getCondition(map);

        complaintDto.setBeginDate(map.get("startTime").toString());
        complaintDto.setEndDate(map.get("endTime").toString());
        complaintDto.setComplaintType(map.get("complaintType") == null || "".equals(map.get("complaintType")) ? null : map.get("complaintType").toString());
        complaintDto.setDateRange("5");

        return sevenDaySolveRate(complaintDto);

    }

    /**
     * 投诉7天解决率排行
     * @param map
     * @return
     */
    @Override
    public List<Map<String,Object>> apiSevenSolveRank(Map<String,Object> map) {

        ComplaintDto complaintDto = getCondition(map);

        complaintDto.setBeginDate(map.get("startTime") == null || "".equals(map.get("startTime")) ? null : map.get("startTime").toString());
        complaintDto.setEndDate(map.get("endTime") == null || "".equals(map.get("endTime")) ? null : map.get("endTime").toString());
        complaintDto.setSort(map.get("sort") == null || "".equals(map.get("sort")) ? "1" : map.get("sort").toString());
        complaintDto.setComplaintType(map.get("complaintType") == null || "".equals(map.get("complaintType")) ? null : map.get("complaintType").toString());
        complaintDto.setComplaintFlag("2");
        complaintDto.setRankFlag("2");

        List<Map<String, Object>> list = rankMap(complaintDto);

        int key = 20;
        if (list.size() < 20) {
            key = list.size();
        }

        if ("1".equals(complaintDto.getSort())) {
            for (int i = 0; i < key; i++) {
                list.get(i).put("rank",key-i);
            }
        }else {
            for (int i = 0; i < key; i++) {
                list.get(i).put("rank",i+1);
            }
        }

        list = list.subList(0, key);

        return list;

    }

    /**
     * 投诉率
     * @param map
     * @return
     */
    @Override
    public Map<String,Object> apiComplaintRate(Map<String,Object> map) {

        ComplaintDto complaintDto = getCondition(map);

        complaintDto.setBeginDate(map.get("startTime") == null || "".equals(map.get("startTime")) ? null : map.get("startTime").toString());
        complaintDto.setEndDate(map.get("endTime") == null || "".equals(map.get("endTime")) ? null : map.get("endTime").toString());
        complaintDto.setComplaintType(map.get("complaintType") == null || "".equals(map.get("complaintType")) ? null : map.get("complaintType").toString());

        return complaintRate(complaintDto);

    }

    /**
     * 未结案
     * @param map
     * @return
     */
    @Override
    public Map<String,Object> apiNotOverCase(Map<String,Object> map) {
        ComplaintDto complaintDto = getCondition(map);

        complaintDto.setBeginDate(map.get("startTime") == null || "".equals(map.get("startTime")) ? null : map.get("startTime").toString());
        complaintDto.setEndDate(map.get("endTime") == null || "".equals(map.get("endTime")) ? null : map.get("endTime").toString());

        return notOverCase(complaintDto);
    }

    /**
     * 投诉异常监控
     * @param map
     * @return
     */
    @Override
    public List<Map<String,Object>> apiComplaintAbnormalMonitor(Map<String,Object> map) {

        ComplaintDto complaintDto = getCondition(map);

        complaintDto.setBeginDate(map.get("startTime") == null || "".equals(map.get("startTime")) ? null : map.get("startTime").toString());
        complaintDto.setEndDate(map.get("endTime") == null || "".equals(map.get("endTime")) ? null : map.get("endTime").toString());

        return complaintAbnormalMonitor(complaintDto);

    }

    /**
     * N+1解决方案提交率
     * @param map
     * @return
     */
    @Override
    public Map<String,Object> apiSolveSubmitRate(Map<String,Object> map) {

        ComplaintDto complaintDto = getCondition(map);

        complaintDto.setBeginDate(map.get("startTime") == null || "".equals(map.get("startTime")) ? null : map.get("startTime").toString());
        complaintDto.setEndDate(map.get("endTime") == null || "".equals(map.get("endTime")) ? null : map.get("endTime").toString());
        complaintDto.setPolymerizeWay("2");
        complaintDto.setDateRange("本月");
//        if (complaintDto.getBeginDate() != null && complaintDto.getEndDate() != null) {
//            if (complaintDto.getBeginDate().substring(0,7).equals(complaintDto.getEndDate().substring(0,7))) {
//                complaintDto.setDateRange("本月");
//            }
//        }

        return complaintSolveSubmissionRate(complaintDto);

    }

    /**
     * N+1解决方案提交率排名
     * @param map
     * @return
     */
    @Override
    public List<Map<String,Object>> apiSolveSubmitRank(Map<String,Object> map) {

        ComplaintDto complaintDto = getCondition(map);

        complaintDto.setBeginDate(map.get("startTime") == null || "".equals(map.get("startTime")) ? null : map.get("startTime").toString());
        complaintDto.setEndDate(map.get("endTime") == null || "".equals(map.get("endTime")) ? null : map.get("endTime").toString());
        complaintDto.setSort(map.get("sort") == null || "".equals(map.get("sort")) ? "1" : map.get("sort").toString());

        List<Map<String,Object>> list = complaintsDao.solveSubmitRank(complaintDto);

        //筛选剔除空数据
        list = list.stream().filter(a -> a.get("name") != null && !"".equals(a.get("name"))).collect(Collectors.toList());

        if("1".equals(complaintDto.getSort())){
            list.sort(Comparator.comparing((Map<String,Object> m) -> (Double.parseDouble(m.get("rankNumber")==null?"-1":m.get("rankNumber").toString()))));
        }else {
            list.sort(Comparator.comparing((Map<String,Object> m) -> (Double.parseDouble(m.get("rankNumber")==null?"-1":m.get("rankNumber").toString()))).reversed());
        }

        int key = 20;
        if (list.size() < 20) {
            key = list.size();
        }

        if ("1".equals(complaintDto.getSort())) {
            for (int i = 0; i < key; i++) {
                list.get(i).put("rank",key-i);
                list.get(i).put("rankNumber",list.get(i).get("rankNumber") + "%");
            }
        }else {
            for (int i = 0; i < key; i++) {
                list.get(i).put("rank",i+1);
                list.get(i).put("rankNumber",list.get(i).get("rankNumber") + "%");
            }
        }

        list = list.subList(0, key);

        return list;

    }

    /**
     * 结算费用及台量
     * @param map
     * @return
     */
    @Override
    public Map<String,Object> apiSettleData(Map<String,Object> map) {

        ComplaintDto complaintDto = getCondition(map);

        complaintDto.setBeginDate(map.get("startTime") == null || "".equals(map.get("startTime")) ? null : map.get("startTime").toString());
        complaintDto.setEndDate(map.get("endTime") == null || "".equals(map.get("endTime")) ? null : map.get("endTime").toString());

        return complaintsDao.apiSettleData(complaintDto);
    }

    public ComplaintDto getCondition(Map<String,Object> map) {
        if (map.get("planId") == null || "".equals(map.get("planId"))) {
            return new ComplaintDto();
        }

        TemplateQueryDataDto templateQueryDto = new TemplateQueryDataDto();
        templateQueryDto.setStartTime(map.get("startTime").toString());
        templateQueryDto.setPlanId(map.get("planId").toString());
        templateQueryDto.setEndTime(map.get("endTime").toString());
        templateQueryDto.setPlanName(map.get("planName").toString());
        JSONObject jsonObject = commonTemplateService.selectDataByKey(templateQueryDto);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }

        ComplaintDto complaintDto = new ComplaintDto();
        complaintDto.setProductTypeCodeList(jsonObject.get("productTypeCodeList") == null ? null : (List<String>) jsonObject.get("productTypeCodeList"));
        return complaintDto;
    }

    public ComplaintDto getFilterDate(ComplaintDto complaintDto) {
        if ("1".equals(complaintDto.getPolymerizeWay())) {
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(complaintDto.getEndDate().concat("-01"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.setTime(date);
            }
            complaintDto.setBeginDate(complaintDto.getBeginDate().concat("-01"));
            String lastDayOfMonth = DateUtil.getLastDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
            complaintDto.setEndDate(lastDayOfMonth);
        }
        return complaintDto;
    }

    public List<String> getDate(ComplaintDto complaintDto) {
        List<String> dateList = new ArrayList<>();
        if (complaintDto.getDateRange() != null) {
            switch (complaintDto.getDateRange()) {
                case "1":
                    dateList = DateUtil.threeDayBefore();
                    break;
                case "2":
                    dateList = DateUtil.SevenDayBefore();
                    break;
                case "3":
                    dateList = DateUtil.MonthBefore();
                    break;
                case "4":
                    dateList = DateUtil.weekRange();
                    break;
                default:
                    String monthFirstDay = DateUtil.getMonthFirstDay();
                    String monthLastDay = DateUtil.getMonthLastDay();
                    dateList.add(monthFirstDay);
                    dateList.add(monthLastDay);
                    break;
            }
        }
        return dateList;
    }

    public List<Map<String, Object>> getQueryField(ComplaintDto complaintDto, List<Map<String,Object>> list) {
        List<String> queryList = new ArrayList<>();
        Set<String> keySet = complaintDto.getScreenList().keySet();
        for (String key : keySet) {
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(complaintDto.getScreenList().get(key)));
            if (jsonObject.get("type") != null && !"".equals(jsonObject.get("type"))) {
//                String query = "";
                if ("0".equals(jsonObject.get("type"))) {
//                    query = key + " = '" + jsonObject.get("value") + "' ";
                    list = list.stream().filter(a -> jsonObject.get("value").toString().equals(a.get(key).toString())).collect(Collectors.toList());
                }else if ("1".equals(jsonObject.get("type"))) {
//                    query = key + " like '%" + jsonObject.get("value") + "%' ";
                    list = list.stream().filter(a -> a.get(key).toString().contains(jsonObject.get("value").toString())).collect(Collectors.toList());
                }else if ("2".equals(jsonObject.get("type"))) {
//                    query = key + " <> '" + jsonObject.get("value") + "'";
                    list = list.stream().filter(a -> !jsonObject.get("value").toString().equals(a.get(key).toString())).collect(Collectors.toList());
                }else if ("between".equals(jsonObject.get("type"))) {
//                    query = key + " &lt;= '" + jsonObject.get("startDate") + "' "
//                            + " and " + key + " &gt;= '" + jsonObject.get("endDate") + "' ";
                    list = list.stream().filter(a -> (jsonObject.get("startDate").toString().compareTo(a.get(key).toString()) <= 0
                            && jsonObject.get("endDate").toString().compareTo(a.get(key).toString()) >= 0)).collect(Collectors.toList());
                }else if ("before".equals(jsonObject.get("type"))) {
//                    query = key + " &lt;= '" + jsonObject.get("value") + "' ";
                    list = list.stream().filter(a -> jsonObject.get("value").toString().compareTo(a.get(key).toString()) > 0).collect(Collectors.toList());
                }else if ("after".equals(jsonObject.get("type"))) {
//                    query = key + " &gt;= '" + jsonObject.get("value") + "' ";
                    list = list.stream().filter(a -> jsonObject.get("value").toString().compareTo(a.get(key).toString()) < 0).collect(Collectors.toList());
                }else if ("day".equals(jsonObject.get("type"))) {
//                    query = key + " = '" + jsonObject.get("value") + "' ";
                    list = list.stream().filter(a -> jsonObject.get("value").toString().equals(a.get(key).toString())).collect(Collectors.toList());
                }else if("=".equals(jsonObject.get("type"))){
                    list = list.stream().filter(a -> (Double.parseDouble(jsonObject.get("value").toString())==Double.parseDouble(a.get(key).toString()))).collect(Collectors.toList());
                }else if(">=".equals(jsonObject.get("type"))){
                    list = list.stream().filter(a -> (Double.parseDouble(jsonObject.get("value").toString())<=Double.parseDouble(a.get(key).toString()))).collect(Collectors.toList());
                }else if(">".equals(jsonObject.get("type"))){
                    list = list.stream().filter(a -> (Double.parseDouble(jsonObject.get("value").toString())<Double.parseDouble(a.get(key).toString()))).collect(Collectors.toList());
                }else if("!=".equals(jsonObject.get("type"))){
                    list = list.stream().filter(a -> (Double.parseDouble(jsonObject.get("value").toString())!=Double.parseDouble(a.get(key).toString()))).collect(Collectors.toList());
                }else if("<=".equals(jsonObject.get("type"))){
                    list = list.stream().filter(a -> (Double.parseDouble(jsonObject.get("value").toString())>=Double.parseDouble(a.get(key).toString()))).collect(Collectors.toList());
                }else if("<".equals(jsonObject.get("type"))){
                    list = list.stream().filter(a -> (Double.parseDouble(jsonObject.get("value").toString())>Double.parseDouble(a.get(key).toString()))).collect(Collectors.toList());
                }
            }

            if (jsonObject.get("sort") != null && !"".equals(jsonObject.get("sort"))) {
                if ("totalDays".equals(key)) {
                    // 先按降序排好
                    if ("desc".equals(jsonObject.get("sort"))) {
                        Collections.sort(list, new Comparator<Map<String, Object>>() {
                            @Override
                            public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
                                if (Double.parseDouble(arg0.get(key).toString()) - Double.parseDouble(arg1.get(key).toString()) >= 0) {
                                    return -1;
                                }else {
                                    return 1;
                                }
                            }
                        });
                    }else {
                        Collections.sort(list, new Comparator<Map<String, Object>>() {
                            @Override
                            public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
                                if (Double.parseDouble(arg0.get(key).toString()) - Double.parseDouble(arg1.get(key).toString()) >= 0) {
                                    return 1;
                                }else {
                                    return -1;
                                }
                            }
                        });
                    }
                }else {
                    // 先按降序排好
                    if ("desc".equals(jsonObject.get("sort"))) {
                        Collections.sort(list, new Comparator<Map<String, Object>>() {
                            @Override
                            public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
                                if (arg0.get(key).toString().compareTo(arg1.get(key).toString()) >= 0) {
                                    return -1;
                                }else {
                                    return 1;
                                }
                            }
                        });
                    }else {
                        Collections.sort(list, new Comparator<Map<String, Object>>() {
                            @Override
                            public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
                                if (arg0.get(key).toString().compareTo(arg1.get(key).toString()) >= 0) {
                                    return 1;
                                }else {
                                    return -1;
                                }
                            }
                        });
                    }
                }
            }
        }
        return list;
    }

}
