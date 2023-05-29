package utry.data.modular.ccBoard.hotline.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import utry.core.common.BusinessException;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.hotline.dao.HotlineDao;
import utry.data.modular.ccBoard.hotline.service.HotlineService;
import utry.data.modular.ccBoard.hotline.util.DateUtil;
import utry.data.modular.ccBoard.hotline.vo.Evaluate;
import utry.data.modular.ccBoard.hotline.vo.HotlineVo;
import utry.data.modular.ccBoard.visit.bo.SatisfactionRate;
import utry.data.modular.technicalQuality.utils.Column;
import utry.data.modular.technicalQuality.utils.ExcelTool;
import utry.data.modular.technicalQuality.utils.TitleEntity;
import utry.data.util.HttpClientUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 热线实现类
 * @Author zh
 * @Date 2022/11/2 13:24
 */
@Service
public class HotlineServiceImpl implements HotlineService {

    @Resource
    private HotlineDao hotlineDao;

    @Resource
    private SysConfServiceImpl sysConfService;

    /**
     * 热线项目数据概览
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> hotlineData(Map<String, Object> map) {

        map.put("dayStartTime",map.get("endDate").toString().concat(" 00:00:00"));
        map.put("dayEndTime",map.get("endDate").toString().concat(" 23:59:59"));

        //获取当周时间段
        List<String> weekList = DateUtil.getWeekDate(map.get("endDate").toString());
        map.put("weekStartTime",weekList.get(0));
        map.put("weekEndTime",weekList.get(1));

        //获取当月时间段
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(map.get("endDate").toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        String firstDayOfMonth = DateUtil.getFirstDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        String lastDayOfMonth = DateUtil.getLastDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

        map.put("monthStartTime",firstDayOfMonth.concat(" 00:00:00"));
        map.put("monthEndTime",lastDayOfMonth.concat(" 23:59:59"));

        map = getAccountingCenter(map);

        //查询热线服务总量及服务投诉量
        Map<String,Object> hotMap = hotlineDao.selectHotlineData(map);

        return hotMap;
    }

    /**
     * 热线项目服务类型
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> hotlineServiceType(Map<String, Object> map) {

        if (map.get("beginDate") != null && !"".equals(map.get("beginDate"))) {
            map.put("beginDate",map.get("beginDate").toString().concat(" 00:00:00"));
        }
        if (map.get("endDate") != null && !"".equals(map.get("endDate"))) {
            map.put("endDate",map.get("endDate").toString().concat(" 23:59:59"));
        }

        map = getAccountingCenter(map);

        List<Map<String,Object>> hotlineList = null;

        //产品品类维度
        if ("1".equals(map.get("dimension"))) {
            hotlineList = hotlineDao.hotlineServiceTypeByCategory(map);
        }else {
            //热线服务单维度
            hotlineList = hotlineDao.hotlineServiceTypeByType(map);
        }

        if (map.get("flag") != null && !"".equals(map.get("flag"))) {
            hotlineList = hotlineListExportHandle(hotlineList,map);
            return hotlineList;
        }
        return chartDataHandle(hotlineList,map);
    }

    /**
     * 已受理工单未跟进
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> acceptWorkFollow(Map<String, Object> map) {

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        map.put("currentDate",sdf.format(new Date()));

        if (map.get("beginDate") != null && !"".equals(map.get("beginDate"))) {
            map.put("beginDate",map.get("beginDate").toString().concat(" 00:00:00"));
        }

        if (map.get("endDate") != null && !"".equals(map.get("endDate"))) {
            if (map.get("beginDate") != null && !"".equals(map.get("beginDate"))) {
                if (map.get("endDate").toString().compareTo(map.get("beginDate").toString().substring(0,10)) == 0
                    && map.get("endDate").toString().compareTo(map.get("currentDate").toString()) == 0) {
                    map.put("beginDate",DateUtil.getMonthFirstDay().concat(" 00:00:00"));
                    map.put("endDate",DateUtil.getMonthLastDay().concat(" 23:59:59"));
                }else {
                    map.put("endDate",map.get("endDate").toString().concat(" 23:59:59"));
                }
            }else {
                map.put("endDate",map.get("endDate").toString().concat(" 23:59:59"));
            }
        }

        map = getAccountingCenter(map);

        return hotlineDao.acceptWorkFollow(map);

    }

    /**
     * 客户评价
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> customerEvaluation(Map<String, Object> map) {

        if (map.get("beginDate") != null && !"".equals(map.get("beginDate"))) {
            map.put("beginDate",map.get("beginDate").toString().concat(" 00:00:00"));
        }
        if (map.get("endDate") != null && !"".equals(map.get("endDate"))) {
            map.put("endDate",map.get("endDate").toString().concat(" 23:59:59"));
        }

        //查询录音文件信息
        List<Evaluate> callList = hotlineDao.selectRecordFile(map);

        List<String> recordList = callList.stream().filter(a -> a.getRecordFileName() != null && !"".equals(a.getRecordFileName())).collect(Collectors.toList())
                .stream().map(Evaluate::getRecordFileName).collect(Collectors.toList());

        map.put("recordList",recordList);

        //查询热线信息
        List<Evaluate> hotlineList = hotlineDao.selectHotline(map);

        //使用stream流把list1和list2根据属性recordFileName合并一个list集合
        List<Evaluate> list = callList.stream().peek(m -> hotlineList.stream().filter(m2-> Objects.equals(m.getRecordFileName(),m2.getRecordFileName())).forEach(m2-> {
            m.setProductCategory(m2.getProductCategory());
            m.setServiceType(m2.getServiceType());
            m.setServiceDetails(m2.getServiceDetails());
        })).collect(Collectors.toList());

        if (map.get("serviceType") != null && !"".equals(map.get("serviceType"))) {
            list = list.stream().filter(a -> a.getServiceType() != null && !"".equals(a.getServiceType())).collect(Collectors.toList());
        }

        if (map.get("serviceDetails") != null && !"".equals(map.get("serviceDetails"))) {
            list = list.stream().filter(a -> a.getServiceDetails() != null && !"".equals(a.getServiceDetails())).collect(Collectors.toList());
        }

        if (map.get("productCategoryCODE") != null && !"".equals(map.get("productCategoryCODE"))) {
            list = list.stream().filter(a -> a.getProductCategory() != null && !"".equals(a.getProductCategory())).collect(Collectors.toList());
        }

        //存放返回的数据
        List<Map<String,Object>> returnList = new ArrayList<>();

        Map<String,Object> commentMap = new HashMap<>();
        commentMap.put("-1","客户未输入");
        commentMap.put("1","满意");
        commentMap.put("2","基本满意");
        commentMap.put("3","客服人员服务态度不满意");
        commentMap.put("4","解决方案不满意");
        commentMap.put("5","产品品质不满意");

        if (map.get("comment") != null && !"".equals(map.get("comment"))) {

            List<Evaluate> collect = list.stream().filter(a -> a.getComment() != null && !"".equals(a.getComment())).collect(Collectors.toList());

            Map<String, Long> groupMap = collect.stream().collect(Collectors.groupingBy(Evaluate::getComment, Collectors.counting()));

            for (String key : groupMap.keySet()) {
                Map<String,Object> map2 = new HashMap<>();

                map2.put("comment",key);
                map2.put("name",commentMap.get(key));
                map2.put("value",groupMap.get(key));

                returnList.add(map2);

            }

            if (map.get("rightFlag") == null || "".equals(map.get("rightFlag"))) {
                if (collect.size() != list.size()) {
                    Map<String,Object> map1 = new HashMap<>();

                    map1.put("comment",null);
                    map1.put("name","未转满意度评价");
                    map1.put("value",list.size() - collect.size());

                    returnList.add(map1);
                }
            }

        }else {

            List<Evaluate> collect = list.stream().filter(a -> a.getComment() == null || "".equals(a.getComment())).collect(Collectors.toList());

            Map<String,Object> map1 = new HashMap<>();
            map1.put("name","未转满意度评价");
            map1.put("value",collect.size());

            returnList.add(map1);

            Map<String,Object> map2 = new HashMap<>();
            map2.put("name","转满意度评价");
            map2.put("value",list.size() - collect.size());

            returnList.add(map2);

        }

        return returnList;

//        List<Map<String,Object>> list = hotlineDao.customerEvaluation(map);
//
//        if (map.get("comment") != null && !"".equals(map.get("comment"))) {
//            return list;
//        }else {
//
//            List<Map<String,Object>> returnList = new ArrayList<>();
//
//            for (Map<String, Object> map1 : list) {
//                if (returnList.size() > 0) {
//                    List<Map<String, Object>> collect = returnList.stream().filter(a -> map1.get("name").equals(a.get("name"))).collect(Collectors.toList());
//                    if (collect.size() > 0) {
//                        Map<String, Object> oldMap = collect.get(0);
//                        returnList.remove(oldMap);
//                        oldMap.put("value",Integer.parseInt(map1.get("value").toString()) + Integer.parseInt(oldMap.get("value").toString()));
//                        returnList.add(oldMap);
//                    }else {
//                        returnList.add(map1);
//                    }
//                }else {
//                    returnList.add(map1);
//                }
//            }
//
//            return returnList;
//
//        }

    }

    /**
     * 投诉分析
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> complaintsAnalysis(Map<String, Object> map) {

        List<Map<String,Object>> analysisList = hotlineDao.complaintsAnalysis(map);

        for (Map<String, Object> analysisMap : analysisList) {
            HashMap<String, Object> classifyMap = new HashMap<>();
            if (analysisMap.get("subsidiaryLedger") != null && !"".equals(analysisMap.get("subsidiaryLedger"))) {
                String[] classifies = analysisMap.get("subsidiaryLedger").toString().split(",");
                for (int i = 0; i < classifies.length; i++) {
                    String[] split = classifies[i].split(":");
                    classifyMap.put(split[0],split[1]);
                }
            }else {
                classifyMap.put(analysisMap.get("serviceDetails").toString(),analysisMap.get("typeNumber"));
            }
            analysisMap.put("subsidiaryLedger",classifyMap);
        }

        return analysisList;
    }

    /**
     * 话务明细
     * @param map
     * @return
     */
    @Override
    public Object callDetail(Map<String, Object> map,Integer pageNum,Integer pageSize) {


        if (map.get("beginDate") != null && !"".equals(map.get("beginDate"))) {
            map.put("beginDate",map.get("beginDate").toString().concat(" 00:00:00"));
        }
        if (map.get("endDate") != null && !"".equals(map.get("endDate"))) {
            map.put("endDate",map.get("endDate").toString().concat(" 23:59:59"));
        }

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        map.put("currentDate",sdf.format(new Date()));

        //查询通话记录信息
        List<HotlineVo> callList = hotlineDao.selectCallRecord(map);

        //若服务类型、服务明细、产品品类为空，则条件都是数策表
//        if (map.get("serviceType") != null && !"".equals(map.get("serviceType"))
//            && map.get("serviceDetails") != null && !"".equals(map.get("serviceDetails"))
//            && map.get("productCategoryCODE") != null && !"".equals(map.get("productCategoryCODE"))) {
//            //分页
//            int startRow = pageNum * pageSize - pageSize;
//            int size = callList.size();
//            callList = callList.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());
//        }

        List<String> recordList = callList.stream().map(HotlineVo::getRecordFileName).collect(Collectors.toList())
                .stream().filter(a -> a != null && !"".equals(a)).collect(Collectors.toList());

        map.put("recordList",recordList);

        List<HotlineVo> hotlineList = hotlineDao.callDetail(map);

        //使用stream流把list1和list2根据属性recordFileName合并一个list集合
        List<HotlineVo> list = callList.stream().peek(m -> hotlineList.stream().filter(m2-> Objects.equals(m.getRecordFileName(),m2.getRecordFileName())).forEach(m2-> {
            m.setSystemState(m2.getSystemState());
            m.setHotlineNumber(m2.getHotlineNumber());
            m.setIsFollow(m2.getIsFollow());
            m.setLastFollowTime(m2.getLastFollowTime());
            m.setProductCategory(m2.getProductCategory());
            m.setProductType(m2.getProductType());
            m.setServiceType(m2.getServiceType());
            m.setServiceDetails(m2.getServiceDetails());
        })).collect(Collectors.toList());

        if (map.get("serviceType") != null && !"".equals(map.get("serviceType"))) {
            list = list.stream().filter(a -> a.getServiceType() != null && !"".equals(a.getServiceType())).collect(Collectors.toList());
        }

        if (map.get("serviceDetails") != null && !"".equals(map.get("serviceDetails"))) {
            list = list.stream().filter(a -> a.getServiceDetails() != null && !"".equals(a.getServiceDetails())).collect(Collectors.toList());
        }

        if (map.get("productCategoryCODE") != null && !"".equals(map.get("productCategoryCODE"))) {
            list = list.stream().filter(a -> a.getProductCategory() != null && !"".equals(a.getProductCategory())).collect(Collectors.toList());
        }

        //查询出队列名称
        List<Map<String,Object>> queueList = hotlineDao.selectQueueName(map);

        //若导出，不分页
        if (map.get("isPage") != null && !"".equals(map.get("isPage"))) {
            list.forEach(a -> {
                if (a.getDeptId() != null && !"".equals(a.getDeptId())) {
                    List<Map<String, Object>> collect = queueList.stream().filter(t -> a.getDeptId().equals(t.get("queueId"))).collect(Collectors.toList());
                    if (collect.size() > 0) {
                        a.setDeptId(collect.get(0).get("queueName") != null ? collect.get(0).get("queueName").toString() : "");
                    }
                }
            });

            return list;
        }

        //分页
        int startRow = pageNum * pageSize - pageSize;
        int size = list.size();
        list = list.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());

        list.forEach(a -> {
            if (a.getDeptId() != null && !"".equals(a.getDeptId())) {
                List<Map<String, Object>> collect = queueList.stream().filter(t -> a.getDeptId().equals(t.get("queueId"))).collect(Collectors.toList());
                if (collect.size() > 0) {
                    a.setDeptId(collect.get(0).get("queueName") != null ? collect.get(0).get("queueName").toString() : "");
                }
            }
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list", list);
        jsonObject.put("total", size);

        return jsonObject;

    }

    /**
     * 热线服务单详情
     * @param map
     * @return
     */
    @Override
    public Map<String,Object> hotlineServiceDetail(Map<String, Object> map) throws Exception {

        if (map.get("hotlineNumber") == null || "".equals(map.get("hotlineNumber"))) {
            throw new Exception("热线单号为空");
        }

        return hotlineDao.hotlineServiceDetail(map);
    }

    /**
     * 未完成处理单
     * @param map
     * @return
     */
    @Override
    public Object noFinishOrder(Map<String, Object> map) throws Exception {

        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = "/UnFinishPendingInfo";
        Map<Object,Object> param = new HashMap<>();
        List<String> centerList = hotlineDao.getAccountingCenter(map);
        if (centerList.size() > 1) {
            param.put("accountCenter","");
        }else {
            param.put("accountCenter",centerList.get(0));
        }
        String postResult = "";
        List<Map> dataList = new ArrayList<>();
        try {
            postResult = HttpClientUtil.CNPost(IP + url,HttpClientUtil.getParam(param));
            JSONObject jsonObject = JSONObject.parseObject(postResult);
            if ("T".equals(jsonObject.get("RESULT"))) {
                String dataStr = jsonObject.get("data").toString();
                dataList = JSON.parseArray(dataStr,Map.class);
            }else {
                throw new Exception(jsonObject.get("ERRMSG").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return dataList;
    }

    /**
     * 热线服务单导出
     * @param response
     * @param map
     * @return
     */
    @Override
    public void hotlineChartExport(HttpServletResponse response, Map<String,Object> map) throws Exception {

        Object obj = null;

        if (map.get("flag") != null && !"".equals(map.get("flag"))) {
            switch (map.get("flag").toString()) {
                case "1":
                case "2":
                case "3":
                    obj = hotlineServiceType(map);
                    break;
                case "4":
                    obj = noFinishOrder(map);
                    break;
                case "5":
                    obj = acceptWorkFollow(map);
                    break;
                default:
                    break;
            }

            //图表导出
            if (obj != null) {
                List<Map> dataList = (List<Map>) obj;

                moreChartExport(dataList,map,response);
            }else {
                throw new Exception("数据为null");
            }

        }else {
            throw new Exception("导出页面类型为空");
        }

    }

    /**
     * 话务明细导出
     * @param response
     * @param map
     */
    @Override
    public void callDetailExport(HttpServletResponse response, Map<String,Object> map) throws Exception{

        //不分页
        map.put("isPage","1");

        List<HotlineVo> hotlineVos = (List<HotlineVo>) callDetail(map,1,10);

        hotlineVos = hotlineVos.stream().peek(a -> {
                        a.setIsFollow("1".equals(a.getIsFollow()) ? "是":"否");
                        a.setTenStandard("1".equals(a.getTenStandard()) ? "是":"否");
                    }).collect(Collectors.toList());

        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("hotlineNumber", "热线服务单号");
        headerMap.put("systemState", "状态");
        headerMap.put("isFollow", "当日是否跟进");
        headerMap.put("lastFollowTime", "最近跟进时间");
        headerMap.put("callTime", "呼叫时间");
        headerMap.put("tenStandard", "10s率达标");
        headerMap.put("deptId", "部门");
        headerMap.put("productCategory", "产品品类");
        headerMap.put("productType", "产品类型");
        headerMap.put("serviceType", "服务类型");
        headerMap.put("serviceDetails", "服务明细");
        headerMap.put("customerEvaluation", "客户评价");
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
        ExcelTool excelTool = new ExcelTool("话务明细"+operationTime+".xlsx",20,20, null, "话务明细列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, hotlineVos,true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("导出失败");
        }

    }

    /**
     * 已受理未跟进列表
     * @param map
     * @return
     */
    @Override
    public Object acceptWorkFollowList(Map<String, Object> map,Integer pageNum,Integer pageSize) {


        if (map.get("beginDate") != null && !"".equals(map.get("beginDate"))) {
            map.put("beginDate", map.get("beginDate").toString().concat(" 00:00:00"));
        }
        if (map.get("endDate") != null && !"".equals(map.get("endDate"))) {
            map.put("endDate", map.get("endDate").toString().concat(" 23:59:59"));
        }

//        List<Map<String,Object>> list = hotlineDao.acceptWorkFollowList(map);

        return null;
    }

    public void moreChartExport(List<Map> dataList,Map<String,Object> conditionMap,HttpServletResponse response) throws Exception{

        conditionMap = getAccountingCenter(conditionMap);

        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        switch (conditionMap.get("flag").toString()) {
            case "1":
                //查询出所有的服务类型
                List<Map<String, Object>> serviceTypeList = hotlineDao.getServiceType(conditionMap);
                headerMap.put("type","产品品类");
                headerMap.put("totalNumber","总数量");
                int i = 1;
                for (Map<String, Object> map : serviceTypeList) {
                    headerMap.put("st" + i + "number", (map.get("serviceType") != null ? map.get("serviceType").toString() : "") + "数量");
                    headerMap.put("st" + i + "number2", (map.get("serviceType") != null ? map.get("serviceType").toString() : "") + "受理量");
                    i++;
                }
                break;
            case "2":
                headerMap.put("type","产品品类");
                headerMap.put("totalNumber","总数量");
                //查询出所有的服务明细
                List<Map<String, Object>> serviceDetailsList = hotlineDao.getServiceDetails(conditionMap);
                int j = 1;
                for (Map<String, Object> map : serviceDetailsList) {
                    headerMap.put("sd" + j + "number", (map.get("serviceDetails") != null ? map.get("serviceDetails").toString() : "") + "数量");
                    headerMap.put("sd" + j + "number2", (map.get("serviceDetails") != null ? map.get("serviceDetails").toString() : "") + "受理量");
                    j++;
                }
                break;
            case "3":
                headerMap.put("type","服务类型");
                headerMap.put("totalNumber","总数量");
                //查询出所有的服务类型
                List<Map<String, Object>> serviceDetailList = hotlineDao.getServiceDetails(conditionMap);
                int k = 1;
                for (Map<String, Object> map : serviceDetailList) {
                    headerMap.put("sd" + k + "number", (map.get("serviceDetails") != null ? map.get("serviceDetails").toString() : "") + "数量");
                    headerMap.put("sd" + k + "number2", (map.get("serviceDetails") != null ? map.get("serviceDetails").toString() : "") + "受理量");
                    k++;
                }
                break;
            case "4":
                headerMap.put("name","名称");
                headerMap.put("count","数量");
                break;
            case "5":
                headerMap.put("dayDate","时间");
                headerMap.put("number","数量");
                break;
            default:
                break;
        }
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
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("1","产品品类维度");
        paramMap.put("2","产品品类维度下钻");
        paramMap.put("3","热线服务单维度");
        paramMap.put("4","未完成待处理");
        paramMap.put("5","已受理工单未跟进");
        ExcelTool excelTool  = new ExcelTool(paramMap.get(conditionMap.get("flag").toString())+operationTime+".xlsx",20,20, null, paramMap.get(conditionMap.get("flag").toString()).toString());
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, dataList,true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("导出失败");
        }
    }

    /**
     * 图表数据处理
     * @param hotlineList
     * @return
     */
    public List<Map<String,Object>> chartDataHandle(List<Map<String,Object>> hotlineList,Map<String,Object> map) {

        if (hotlineList == null) {
            return new ArrayList<>();
        }

        //存放所有服务类型map
        Map<String, Object> typeMap = new HashMap<>();

        if (map.get("dimension") != null && "1".equals(map.get("dimension"))) {
            if (map.get("serviceType") != null && !"".equals(map.get("serviceType"))) {
                //查询所有的服务明细
                List<Map<String,Object>> serviceDetailsList = hotlineDao.getServiceDetails(map);
                int i = 1;
                for (Map<String, Object> serviceMap : serviceDetailsList) {
                    typeMap.put("sd" + i, serviceMap.get("serviceDetails"));
                    i++;
                }
            }else {
                //查询所有的服务类型
                List<Map<String, Object>> serviceTypeList = hotlineDao.getServiceType(map);
                int i = 1;
                for (Map<String, Object> serviceMap : serviceTypeList) {
                    typeMap.put("st" + i, serviceMap.get("serviceType"));
                    i++;
                }
            }
        }else {
            //查询所有的服务明细
            List<Map<String,Object>> serviceDetailsList = hotlineDao.getServiceDetails(map);
            int i = 1;
            for (Map<String, Object> serviceMap : serviceDetailsList) {
                typeMap.put("sd" + i, serviceMap.get("serviceDetails"));
                i++;
            }
        }


        //存放最后返回的数据
        List<Map<String,Object>> list = new ArrayList<>();

        for (Map<String, Object> hotMap : hotlineList) {
            //判断是否有数据存在
            if (list.size() > 0) {
                //判断是否有重复的serviceType
                List<Map<String, Object>> collect = list.stream().filter(a -> hotMap.get("type").equals(a.get("type"))).collect(Collectors.toList());
                Map<String, Object> newMap;
                if (collect.size() > 0) {
                    newMap = collect.get(0);
                    list.remove(newMap);
                    List<Map<String,Object>> newList = (List<Map<String,Object>>)newMap.get("list");
                    hotMap.remove("type");
                    hotMap.remove("productCategoryCODE");
                    hotMap.remove("list");

                    for (Map<String, Object> objectMap : newList) {
                        if (objectMap.get("label").equals(hotMap.get("label"))) {
                            objectMap.put("number",hotMap.get("number"));
                            objectMap.put("number2",hotMap.get("number2"));
                        }
                    }

//                    List<Map<String, Object>> repeatList = newList.stream().filter(a -> a.get("label").equals(hotMap.get("label"))).collect(Collectors.toList());
//                    if (repeatList.size() > 0) {
//                        newList.remove(repeatList.get(0));
//                        newList.add(hotMap);
//                    }
                    newMap.put("list",newList);
                    newMap.put("totalNumber",Integer.parseInt(newMap.get("totalNumber").toString()) + Integer.parseInt(hotMap.get("number").toString()));
                }else {
                    newMap = new HashMap<>();
                    newMap.put("type",hotMap.get("type"));
                    newMap.put("productCategoryCODE",hotMap.get("productCategoryCODE"));
                    hotMap.remove("type");
                    hotMap.remove("productCategoryCODE");
                    newMap.put("totalNumber",hotMap.get("number"));
                    List<Map<String,Object>> innerList = new ArrayList<>();
                    for (String key : typeMap.keySet()) {
                        Map<String,Object> otherMap = new HashMap<>();
                        if (hotMap.get("label").equals(typeMap.get(key))) {
                            innerList.add(hotMap);
                        }else {
                            otherMap.put("label",typeMap.get(key));
                            otherMap.put("number",0);
                            otherMap.put("number2",0);
                            innerList.add(otherMap);
                        }
                    }
                    newMap.put("list",innerList);
                }
                list.add(newMap);
            }else {
                Map<String,Object> newMap = new HashMap<>();
                newMap.put("type",hotMap.get("type"));
                newMap.put("productCategoryCODE",hotMap.get("productCategoryCODE"));
                hotMap.remove("type");
                hotMap.remove("productCategoryCODE");
                newMap.put("totalNumber",hotMap.get("number"));
                List<Map<String,Object>> innerList = new ArrayList<>();
                for (String key : typeMap.keySet()) {
                    Map<String,Object> otherMap = new HashMap<>();
                    if (hotMap.get("label").equals(typeMap.get(key))) {
                        innerList.add(hotMap);
                    }else {
                        otherMap.put("label",typeMap.get(key));
                        otherMap.put("number",0);
                        otherMap.put("number2",0);
                        innerList.add(otherMap);
                    }
                }
                newMap.put("list",innerList);
                list.add(newMap);
            }
        }

        if (list.size() > 0) {
//            List<Map<String,Object>> firstList = (List<Map<String,Object>>)list.get(0).get("list");
//            List<Map<String,Object>> firstZeroList = firstList.stream().filter(a -> "0".equals(a.get("number").toString())).collect(Collectors.toList());
//
//            //存放需要移除的map
//            List<Map<String,Object>> removeList = new ArrayList<>();
//
//            for (Map<String, Object> zeroMap : firstZeroList) {
//                int removeFlag = 0;
//                for (int i = 0; i < list.size(); i++) {
//                    if (i != 0) {
//                        List<Map<String,Object>> backList = (List<Map<String,Object>>)list.get(i).get("list");
//                        List<Map<String, Object>> collect = backList.stream().filter(a -> zeroMap.get("label").equals(a.get("label"))
//                                                            && "0".equals(a.get("number").toString())).collect(Collectors.toList());
//                        if (collect.size() > 0) {
//                            removeFlag++;
//                        }
//                    }
//                }
//                if (removeFlag == list.size() - 1) {
//                    removeList.add(zeroMap);
//                }
//            }

//            for (Map<String, Object> allMap : list) {
//                List<Map<String,Object>> lastList = (List<Map<String,Object>>)allMap.get("list");
//                allMap.remove("list");
////                lastList.removeAll(removeList);
//                //排序
//                Collections.sort(lastList, new Comparator<Map<String, Object>>() {
//                    @Override
//                    public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
//                        if (Integer.parseInt(arg0.get("number").toString()) - Integer.parseInt(arg1.get("number").toString()) >= 0) {
//                            return -1;
//                        }else {
//                            return 1;
//                        }
//                    }
//                });
//                allMap.put("list",lastList);
//            }

            //排序
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
                    if (Integer.parseInt(arg0.get("totalNumber").toString()) - Integer.parseInt(arg1.get("totalNumber").toString()) >= 0) {
                        return -1;
                    }else {
                        return 1;
                    }
                }
            });

        }

        return list;

    }

    public List<Map<String,Object>> hotlineListExportHandle(List<Map<String,Object>> list,Map<String,Object> paramMap) {
        //存放返回的数据
        List<Map<String,Object>> returnList = new ArrayList<>();

        //存放所有服务类型map
        Map<String, Object> typeMap = new HashMap<>();
        if ("1".equals(paramMap.get("flag").toString())) {
            //查询所有的服务类型
            List<Map<String, Object>> serviceTypeList = hotlineDao.getServiceType(paramMap);
            int i = 1;
            for (Map<String, Object> map : serviceTypeList) {
                typeMap.put("st" + i, map.get("serviceType"));
                i++;
            }
        }else {
            //查询所有的服务明细
            List<Map<String,Object>> serviceDetailsList = hotlineDao.getServiceDetails(paramMap);
            int i = 1;
            for (Map<String, Object> map : serviceDetailsList) {
                typeMap.put("sd" + i, map.get("serviceDetails"));
                i++;
            }
        }

        for (Map<String, Object> map : list) {
            if (returnList.size() > 0) {
                List<Map<String, Object>> collect = returnList.stream().filter(a -> a.get("type").equals(map.get("type"))).collect(Collectors.toList());
                if (collect.size() > 0) {
                    Map<String, Object> map1 = collect.get(0);
                    returnList.remove(map1);
                    for (String s : typeMap.keySet()) {
                        if (map.get("label").equals(typeMap.get(s))) {
                            map1.put(s,typeMap.get(s));
                            map1.put(s + "number",map.get("number"));
                            map1.put(s + "number2",map.get("number2"));
                            map1.put("totalNumber",Integer.parseInt(map1.get("totalNumber").toString()) + Integer.parseInt(map.get("number").toString()));
                        }
                    }
                    returnList.add(map1);
                }else {
                    for (String s : typeMap.keySet()) {
                        map.put(s,typeMap.get(s));
                        map.put(s + "number",map.get("number"));
                        map.put(s + "number2",map.get("number2"));
                        map.put("totalNumber",map.get("number"));
                    }
                    returnList.add(map);
                }

            }else {
                for (String s : typeMap.keySet()) {
                    map.put(s,typeMap.get(s));
                    map.put(s + "number",map.get("number"));
                    map.put(s + "number2",map.get("number2"));
                    map.put("totalNumber",map.get("number"));
                }
                returnList.add(map);
            }
        }

        return returnList;
    }

    /**
     * 根据队列id获取核算中心
     * @param map
     * @return
     */
    public Map<String, Object> getAccountingCenter(Map<String,Object> map) {
        List<String> deptIds = hotlineDao.getAccountingCenter(map);
        map.put("deptIds",deptIds);
        return map;
    }

}
