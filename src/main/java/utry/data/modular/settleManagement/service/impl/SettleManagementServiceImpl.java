package utry.data.modular.settleManagement.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import utry.core.common.BusinessException;
import utry.data.modular.settleManagement.dao.MissSettleManagementDao;
import utry.data.modular.settleManagement.dao.SettleManagementDao;
import utry.data.modular.settleManagement.dto.ConditionDto;
import utry.data.modular.settleManagement.service.SettleManagementService;
import utry.data.modular.settleManagement.utils.DateUtil;
import utry.data.util.ExcelUtil;
import utry.data.util.RetResponse;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/27 15:49
 */
@Service
public class SettleManagementServiceImpl implements SettleManagementService {

    @Resource
    private SettleManagementDao settleManagementDao;

    @Resource
    private MissSettleManagementDao missSettleManagementDao;

    /**
     * 根据条件查询结算管理信息
     * @param conditionDto
     * @return
     */
    @Override
    public List<Map<String,Object>> selectSettleSummary(ConditionDto conditionDto) {
        //查询出结算数据
        List<Map<String,Object>> settleSummary = null;
        List<Map<String,Object>> missSettleSummary = null;
        //将快捷时间转换成起止日期
        if (conditionDto.getBeginDate() == null && conditionDto.getEndDate() == null) {
            conditionDto.setBeginDate(getDate(conditionDto).get(0));
            conditionDto.setEndDate(getDate(conditionDto).get(1));
        }

//        if ("1".equals(conditionDto.getPolymerizeWay())) {
//            try {
//                //开始时间结束时间月份-1
//                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                Calendar calendar = Calendar.getInstance();
//                //设置开始月份
//                calendar.setTime(dateFormat.parse(conditionDto.getBeginDate().concat("-01")));
//                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
//                conditionDto.setBeginDate(dateFormat.format(calendar.getTime()).substring(0,7));
//                //设置结束月份
//                calendar.setTime(dateFormat.parse(conditionDto.getEndDate().concat("-01")));
//                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
//                conditionDto.setEndDate(dateFormat.format(calendar.getTime()).substring(0,7));
//
//                if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())) {
//                    //设置选择月份
//                    calendar.setTime(dateFormat.parse(conditionDto.getSelectMonth().concat("-01")));
//                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
//                    conditionDto.setSelectMonth(dateFormat.format(calendar.getTime()).substring(0,7));
//                }
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        //当结束日期大于等于当月
        if (conditionDto.getEndDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) >= 0) {
            //当开始日期小于当月时
            if (conditionDto.getBeginDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) < 0) {
                //查询结算费用汇总数据
                settleSummary = settleManagementDao.selectSettleSummary(conditionDto);
                //查询未结算费用汇总数据
                missSettleSummary = missSettleManagementDao.selectMissSettleSummary(conditionDto);
                //若按日聚合，则会出现已结算数据和未结算数据重叠的问题，合并
                if ("2".equals(conditionDto.getPolymerizeWay())) {
                    for (Map<String, Object> map : settleSummary) {
                        List<Map<String, Object>> collect = missSettleSummary.stream().filter(a -> map.get("settleDate").equals(a.get("settleDate"))).collect(Collectors.toList());
                        if (collect.size() > 0) {
                            Set set = collect.get(0).keySet();
                            Iterator iterator = set.iterator();
                            while (iterator.hasNext()) {
                                String key = iterator.next().toString();
                                if (("settleDate").equals(key)) {
                                    continue;
                                }
                                map.put(key,Double.parseDouble(map.get(key).toString()) + Double.parseDouble(collect.get(0).get(key).toString()));
                            }
                        }
                    }
                }else {
                    settleSummary.addAll(missSettleSummary);
                }
            }else {
                //查询未结算费用汇总数据
                settleSummary = missSettleManagementDao.selectMissSettleSummary(conditionDto);
            }
        }else {
            //查询结算费用汇总数据
            settleSummary = settleManagementDao.selectSettleSummary(conditionDto);
        }

        //存放所有的结算数据
//        List<Map<String,Object>> settleList = new ArrayList<>();
//        List<String> betweenDate = null;
//        if ("1".equals(conditionDto.getPolymerizeWay())) {
//            betweenDate = DateUtil.getBetweenMonth(conditionDto.getBeginDate(),conditionDto.getEndDate());
//        }else {
//            betweenDate = DateUtil.getBetweenDate(conditionDto.getBeginDate(),conditionDto.getEndDate());
//        }
//        for (String date : betweenDate) {
//            List<Map<String, Object>> settleDate = settleSummary.stream().filter(a -> date.equals(a.get("settleDate").toString())).collect(Collectors.toList());
//            if (settleDate.size() > 0) {
//                settleList.add(settleDate.get(0));
//            }else {
//                Map<String,Object> map = new HashMap<>();
//                map.put("settleDate",date);
//                map.put("maintain",0);
//                map.put("install",0);
//                map.put("noDoor",0);
//                map.put("identify",0);
//                map.put("cash",0);
//                map.put("replacement",0);
//                map.put("businessLoss",0);
//                map.put("recall",0);
//                map.put("boardMaintain",0);
//                map.put("libraryMaintain",0);
//                settleList.add(map);
//            }
//        }

        if ("1".equals(conditionDto.getShowDimension())) {
            DecimalFormat df = new DecimalFormat("0.00");//保留两位小数
            for (Map<String, Object> map : settleSummary) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    if ("settleDate".equals(key)) {
                        continue;
                    }
                    map.put(key,df.format(Double.parseDouble(map.get(key).toString())));
                }
            }
        }

        return settleSummary;
    }

    /**
     * 查询服务类型-费用分析
     * @param conditionDto
     * @return
     */
    @Override
    public Object selectServiceType(ConditionDto conditionDto,Integer pageNum,Integer pageSize) {
        //服务类型数据
        List<Map<String,Object>> serviceTypeData = null;
        //将快捷时间转换成起止日期
        if (conditionDto.getBeginDate() == null && conditionDto.getEndDate() == null) {
            conditionDto.setBeginDate(getDate(conditionDto).get(0));
            conditionDto.setEndDate(getDate(conditionDto).get(1));
        }

        //当结束日期大于等于当月
        if (conditionDto.getEndDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) >= 0) {
            //当开始日期小于当月时
            if (conditionDto.getBeginDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) <0) {
                if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                && conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //当选择月份为当月时，查询未结算的服务数据
                    serviceTypeData = missSettleManagementDao.selectMissServiceType(conditionDto);
                }else if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && !conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询结算的服务数据
                    serviceTypeData = settleManagementDao.selectServiceType(conditionDto);
                }else {
                    //查询已结算的和未结算的服务数据
                    serviceTypeData = settleManagementDao.selectAllServiceType(conditionDto);
                }
            }else {
                //当选择月份为当月时，查询未结算的服务数据
                serviceTypeData = missSettleManagementDao.selectMissServiceType(conditionDto);
            }
        }else {
            //查询结算的服务数据
            serviceTypeData = settleManagementDao.selectServiceType(conditionDto);
        }

        if ("1".equals(conditionDto.getShowDimension())) {
            DecimalFormat df = new DecimalFormat("0.00");//保留两位小数
            for (Map<String, Object> map : serviceTypeData) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    if ("province".equals(key) || "adminName".equals(key) || "settleObjectName".equals(key)) {
                        continue;
                    }
                    map.put(key,df.format(Double.parseDouble(map.get(key).toString())));
                }
            }
        }

        if ("2".equals(conditionDto.getChartFlag())) {
            if (conditionDto.getScreenList() != null && conditionDto.getScreenList().size() > 0) {
                //将筛选和排序条件拼接
                serviceTypeData = getQueryField(conditionDto,serviceTypeData);
            }
            //分页
            int startRow = pageNum * pageSize - pageSize;
            int size = serviceTypeData.size();
            serviceTypeData = serviceTypeData.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("list", serviceTypeData);
            jsonObject.put("total", size);

            return jsonObject;
        }

        StringBuilder classify = new StringBuilder();
        if ("1".equals(conditionDto.getClassifyDimension())) {
            classify.append("adminName");
        }else if ("2".equals(conditionDto.getClassifyDimension())){
            classify.append("province");
        }else {
            classify.append("settleObjectName");
        }

        //按月聚合下才有环比和同比
        if ("1".equals(conditionDto.getPolymerizeWay()) && "1".equals(conditionDto.getChartFlag())) {
            if (conditionDto.getSelectMonth() != null && conditionDto.getSelectMonth().compareTo(DateUtil.getMonthFirstDay().substring(0,7)) < 0) {
                try {

                    conditionDto.setSelectYear(conditionDto.getSelectMonth().substring(0,4));
                    //查询出服务选中年月的数据
                    List<Map<String,Object>> currentMonthList = settleManagementDao.selectServiceTypeMonth(conditionDto);

                    String month = conditionDto.getSelectMonth();
                    //开始时间结束时间月份-1
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateFormat.parse(month.concat("-01")));
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                    //设置上个月的日期
                    conditionDto.setSelectMonth(dateFormat.format(calendar.getTime()).substring(0,7));
                    //查询出服务上个月的数据
                    List<Map<String,Object>> lastMonthList = settleManagementDao.selectServiceTypeMonth(conditionDto);

                    //设置去年的年份
                    calendar.setTime(dateFormat.parse(month.concat("-01")));
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                    conditionDto.setSelectMonth(dateFormat.format(calendar.getTime()).substring(0,7));
                    //查询出服务上个年同月的数据
                    List<Map<String,Object>> lastYearList = settleManagementDao.selectServiceTypeMonth(conditionDto);

                    //存放环比同比数据
                    List<Map<String, Object>> monthCompareList = getCompareList(lastMonthList, currentMonthList, classify);
                    List<Map<String, Object>> yearCompareList = getCompareList(lastYearList, currentMonthList, classify);

                    for (Map<String, Object> serviceType : serviceTypeData) {
                        List<Map<String, Object>> monthCompare = monthCompareList.stream().filter(a -> serviceType.get(classify.toString()).equals(a.get(classify.toString()))).collect(Collectors.toList());
                        if (monthCompare.size() > 0) {
                            serviceType.put("monthCompare",monthCompare.get(0).get("compare"));
                        }else {
                            serviceType.put("monthCompare",0.00);
                        }
                        List<Map<String, Object>> yearCompare = yearCompareList.stream().filter(a -> serviceType.get(classify.toString()).equals(a.get(classify.toString()))).collect(Collectors.toList());
                        if (yearCompare.size() > 0) {
                            serviceType.put("yearCompare",yearCompare.get(0).get("compare"));
                        }else {
                            serviceType.put("yearCompare",0.00);
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return serviceTypeData;
    }

    /**
     * 根据条件查询费用分析
     * @param conditionDto
     * @return
     */
    @Override
    public Object selectCostAnalysis(ConditionDto conditionDto,Integer pageNum,Integer pageSize) {
        //费用分析数据
        List<Map<String,Object>> costAnalysisData = null;
        //将快捷时间转换成起止日期
        if (conditionDto.getBeginDate() == null && conditionDto.getEndDate() == null) {
            conditionDto.setBeginDate(getDate(conditionDto).get(0));
            conditionDto.setEndDate(getDate(conditionDto).get(1));
        }

        //当结束日期大于等于当月
        if (conditionDto.getEndDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) >= 0) {
            //当开始日期小于当月时
            if (conditionDto.getBeginDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) <0) {
                if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询未结算的服务数据
                    costAnalysisData = missSettleManagementDao.selectMissCostAnalysis(conditionDto);
                }else if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && !conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询结算的服务数据
                    costAnalysisData = settleManagementDao.selectCostAnalysis(conditionDto);
                }else {
                    //查询结算和已结算的服务数据
                    costAnalysisData = settleManagementDao.selectAllCostAnalysis(conditionDto);
                }
            }else {
                //查询结算的服务数据
                costAnalysisData = settleManagementDao.selectCostAnalysis(conditionDto);
            }
        }else {
            //查询结算的服务数据
            costAnalysisData = settleManagementDao.selectCostAnalysis(conditionDto);
        }

        DecimalFormat df = new DecimalFormat("0.00");//保留两位小数
        for (Map<String, Object> map : costAnalysisData) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                if ("province".equals(key) || "adminName".equals(key) || "settleObjectName".equals(key)) {
                    continue;
                }
                map.put(key,df.format(Double.parseDouble(map.get(key).toString())));
            }
        }

        if ("2".equals(conditionDto.getChartFlag())) {
            if (conditionDto.getScreenList() != null && conditionDto.getScreenList().size() > 0) {
                //将筛选和排序条件拼接
                costAnalysisData = getQueryField(conditionDto,costAnalysisData);
            }
            //分页
            int startRow = pageNum * pageSize - pageSize;
            int size = costAnalysisData.size();
            costAnalysisData = costAnalysisData.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("list", costAnalysisData);
            jsonObject.put("total", size);

            return jsonObject;
        }

        StringBuilder classify = new StringBuilder();
        if ("1".equals(conditionDto.getClassifyDimension())) {
            classify.append("adminName");
        }else if ("2".equals(conditionDto.getClassifyDimension())){
            classify.append("province");
        }else {
            classify.append("settleObjectName");
        }

        //按月聚合下才有环比和同比
        if ("1".equals(conditionDto.getPolymerizeWay()) && "1".equals(conditionDto.getChartFlag())) {
            if (conditionDto.getSelectMonth() != null && conditionDto.getSelectMonth().compareTo(DateUtil.getMonthFirstDay().substring(0,7)) < 0) {
                try {

                    conditionDto.setSelectYear(conditionDto.getSelectMonth().substring(0,4));
                    //查询出费用分析选中年月的数据
                    List<Map<String,Object>> currentMonthList = settleManagementDao.selectCostAnalysisMonth(conditionDto);

                    String month = conditionDto.getSelectMonth();
                    //开始时间结束时间月份-1
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateFormat.parse(month.concat("-01")));
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                    //设置上个月的日期
                    conditionDto.setSelectMonth(dateFormat.format(calendar.getTime()).substring(0,7));
                    //查询出费用分析上个月的数据
                    List<Map<String,Object>> lastMonthList = settleManagementDao.selectCostAnalysisMonth(conditionDto);

                    //设置去年的年份
                    calendar.setTime(dateFormat.parse(month.concat("-01")));
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                    conditionDto.setSelectMonth(dateFormat.format(calendar.getTime()).substring(0,7));
                    //查询出费用分析上个年同月的数据
                    List<Map<String,Object>> lastYearList = settleManagementDao.selectCostAnalysisMonth(conditionDto);

                    //存放环比同比数据
                    List<Map<String, Object>> monthCompareList = getCompareList(lastMonthList, currentMonthList, classify);
                    List<Map<String, Object>> yearCompareList = getCompareList(lastYearList, currentMonthList, classify);

                    for (Map<String, Object> costAnalysis : costAnalysisData) {
                        List<Map<String, Object>> monthCompare = monthCompareList.stream().filter(a -> costAnalysis.get(classify.toString()).equals(a.get(classify.toString()))).collect(Collectors.toList());
                        if (monthCompare.size() > 0) {
                            costAnalysis.put("monthCompare",monthCompare.get(0).get("compare"));
                        }else {
                            costAnalysis.put("monthCompare",0.00);
                        }
                        List<Map<String, Object>> yearCompare = yearCompareList.stream().filter(a -> costAnalysis.get(classify.toString()).equals(a.get(classify.toString()))).collect(Collectors.toList());
                        if (yearCompare.size() > 0) {
                            costAnalysis.put("yearCompare",yearCompare.get(0).get("compare"));
                        }else {
                            costAnalysis.put("yearCompare",0.00);
                        }
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return costAnalysisData;
    }

    /**
     * 工厂/营业费用分析
     * @param conditionDto
     * @return
     */
    @Override
    public Object selectIndustrialBusiness(ConditionDto conditionDto,Integer pageNum,Integer pageSize) {
        //工厂/营业费用分析数据
        List<Map<String,Object>> industrialBusinessData = null;
        //将快捷时间转换成起止日期
        if (conditionDto.getBeginDate() == null && conditionDto.getEndDate() == null) {
            conditionDto.setBeginDate(getDate(conditionDto).get(0));
            conditionDto.setEndDate(getDate(conditionDto).get(1));
        }

        //当结束日期大于等于当月
        if (conditionDto.getEndDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) >= 0) {
            //当开始日期小于当月时
            if (conditionDto.getBeginDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) <0) {
                if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询出未结算的工厂/营业费用分析数据
                    industrialBusinessData = missSettleManagementDao.selectMissIndustrialBusiness(conditionDto);
                }else if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && !conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询已结算的工厂/营业费用分析数据
                    industrialBusinessData = settleManagementDao.selectIndustrialBusiness(conditionDto);
                }else {
                    //查询已结算和未结算的工厂/营业费用分析
                    industrialBusinessData = settleManagementDao.selectAllIndustrialBusiness(conditionDto);
                }
            }else {
                //查询已结算的工厂/营业费用分析数据
                industrialBusinessData = settleManagementDao.selectIndustrialBusiness(conditionDto);
            }
        }else {
            //查询已结算的工厂/营业费用分析数据
            industrialBusinessData = settleManagementDao.selectIndustrialBusiness(conditionDto);
        }

        if ("1".equals(conditionDto.getShowDimension())) {
            DecimalFormat df = new DecimalFormat("0.00");//保留两位小数
            for (Map<String, Object> map : industrialBusinessData) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    if ("province".equals(key) || "adminName".equals(key) || "settleObjectName".equals(key)) {
                        continue;
                    }
                    map.put(key,df.format(Double.parseDouble(map.get(key).toString())));
                }
            }
        }

        if ("2".equals(conditionDto.getChartFlag())) {
            if (conditionDto.getScreenList() != null && conditionDto.getScreenList().size() > 0) {
                //将筛选和排序条件拼接
                industrialBusinessData = getQueryField(conditionDto,industrialBusinessData);
            }
            //分页
            int startRow = pageNum * pageSize - pageSize;
            int size = industrialBusinessData.size();
            industrialBusinessData = industrialBusinessData.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("list", industrialBusinessData);
            jsonObject.put("total", size);

            return jsonObject;
        }

        StringBuilder classify = new StringBuilder();
        if ("1".equals(conditionDto.getClassifyDimension())) {
            classify.append("adminName");
        }else if ("2".equals(conditionDto.getClassifyDimension())){
            classify.append("province");
        }else {
            classify.append("settleObjectName");
        }

        //按月聚合下才有环比和同比
        if ("1".equals(conditionDto.getPolymerizeWay()) && "1".equals(conditionDto.getChartFlag())) {
            if (conditionDto.getSelectMonth() != null && conditionDto.getSelectMonth().compareTo(DateUtil.getMonthFirstDay().substring(0,7)) < 0) {
                try {

                    conditionDto.setSelectYear(conditionDto.getSelectMonth().substring(0,4));
                    //查询出工厂/营业费用分析选中年月的数据
                    List<Map<String,Object>> currentMonthList = settleManagementDao.selectIndustrialBusinessMonth(conditionDto);

                    String month = conditionDto.getSelectMonth();
                    //开始时间结束时间月份-1
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateFormat.parse(month.concat("-01")));
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                    //设置上个月的日期
                    conditionDto.setSelectMonth(dateFormat.format(calendar.getTime()).substring(0,7));
                    //查询出工厂/营业费用分析上个月的数据
                    List<Map<String,Object>> lastMonthList = settleManagementDao.selectIndustrialBusinessMonth(conditionDto);

                    //设置去年的年份
                    calendar.setTime(dateFormat.parse(month.concat("-01")));
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                    conditionDto.setSelectMonth(dateFormat.format(calendar.getTime()).substring(0,7));
                    //查询出工厂/营业费用分析上个年同月的数据
                    List<Map<String,Object>> lastYearList = settleManagementDao.selectIndustrialBusinessMonth(conditionDto);

                    //存放环比同比数据
                    List<Map<String, Object>> monthCompareList = getCompareList(lastMonthList, currentMonthList, classify);
                    List<Map<String, Object>> yearCompareList = getCompareList(lastYearList, currentMonthList, classify);

                    for (Map<String, Object> industrialBusiness : industrialBusinessData) {
                        List<Map<String, Object>> monthCompare = monthCompareList.stream().filter(a -> industrialBusiness.get(classify.toString()).equals(a.get(classify.toString()))).collect(Collectors.toList());
                        if (monthCompare.size() > 0) {
                            industrialBusiness.put("monthCompare",monthCompare.get(0).get("compare"));
                        }else {
                            industrialBusiness.put("monthCompare",0.00);
                        }
                        List<Map<String, Object>> yearCompare = yearCompareList.stream().filter(a -> industrialBusiness.get(classify.toString()).equals(a.get(classify.toString()))).collect(Collectors.toList());
                        if (yearCompare.size() > 0) {
                            industrialBusiness.put("yearCompare",yearCompare.get(0).get("compare"));
                        }else {
                            industrialBusiness.put("yearCompare",0.00);
                        }
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return industrialBusinessData;
    }

    /**
     * 工厂别服务违约
     * @param conditionDto
     * @return
     */
    @Override
    public Object selectFactoryServiceBreach(ConditionDto conditionDto,Integer pageNum,Integer pageSize) {
        //工厂别服务违约数据
        List<Map<String,Object>> factoryServiceData = null;
        //将快捷时间转换成起止日期
        if (conditionDto.getBeginDate() == null && conditionDto.getEndDate() == null) {
            conditionDto.setBeginDate(getDate(conditionDto).get(0));
            conditionDto.setEndDate(getDate(conditionDto).get(1));
        }

        //当结束日期大于等于当月
        if (conditionDto.getEndDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) >= 0) {
            //当开始日期小于当月时
            if (conditionDto.getBeginDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) <0) {
                if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询出未结算的工厂别服务违约数据
                    factoryServiceData = missSettleManagementDao.selectMissFactoryServiceBreach(conditionDto);
                }else if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && !conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询已结算的工厂别服务违约数据
                    factoryServiceData = settleManagementDao.selectFactoryServiceBreach(conditionDto);
                }else {
                    //查询已结算和未结算的工厂别服务违约数据
                    factoryServiceData = settleManagementDao.selectAllFactoryServiceBreach(conditionDto);
                }
            }else {
                //查询已结算的工厂别服务违约数据
                factoryServiceData = settleManagementDao.selectFactoryServiceBreach(conditionDto);
            }
        }else {
            //查询已结算的工厂别服务违约数据
            factoryServiceData = settleManagementDao.selectFactoryServiceBreach(conditionDto);
        }

        if ("1".equals(conditionDto.getShowDimension())) {
            DecimalFormat df = new DecimalFormat("0.00");//保留两位小数
            for (Map<String, Object> map : factoryServiceData) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    if ("province".equals(key) || "adminName".equals(key) || "settleObjectName".equals(key)) {
                        continue;
                    }
                    map.put(key,df.format(Double.parseDouble(map.get(key).toString())));
                }
            }
        }

        if ("2".equals(conditionDto.getChartFlag())) {
            if (conditionDto.getScreenList() != null && conditionDto.getScreenList().size() > 0) {
                //将筛选和排序条件拼接
                factoryServiceData = getQueryField(conditionDto,factoryServiceData);
            }
            //分页
            int startRow = pageNum * pageSize - pageSize;
            int size = factoryServiceData.size();
            factoryServiceData = factoryServiceData.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("list", factoryServiceData);
            jsonObject.put("total", size);

            return jsonObject;
        }

        StringBuilder classify = new StringBuilder();
        if ("1".equals(conditionDto.getClassifyDimension())) {
            classify.append("adminName");
        }else if ("2".equals(conditionDto.getClassifyDimension())){
            classify.append("province");
        }else {
            classify.append("settleObjectName");
        }

        //按月聚合下才有环比和同比
        if ("1".equals(conditionDto.getPolymerizeWay()) && "1".equals(conditionDto.getChartFlag())) {
            if (conditionDto.getSelectMonth() != null && conditionDto.getSelectMonth().compareTo(DateUtil.getMonthFirstDay().substring(0,7)) < 0) {
                try {

                    conditionDto.setSelectYear(conditionDto.getSelectMonth().substring(0,4));
                    //查询出工厂别服务违约选中年月的数据
                    List<Map<String,Object>> currentMonthList = settleManagementDao.selectFactoryServiceMonth(conditionDto);

                    String month = conditionDto.getSelectMonth();
                    //开始时间结束时间月份-1
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateFormat.parse(month.concat("-01")));
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                    //设置上个月的日期
                    conditionDto.setSelectMonth(dateFormat.format(calendar.getTime()).substring(0,7));
                    //查询出工厂别服务违约上个月的数据
                    List<Map<String,Object>> lastMonthList = settleManagementDao.selectFactoryServiceMonth(conditionDto);

                    //设置去年的年份
                    calendar.setTime(dateFormat.parse(month.concat("-01")));
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                    conditionDto.setSelectMonth(dateFormat.format(calendar.getTime()).substring(0,7));
                    //查询出工厂别服务违约上个年同月的数据
                    List<Map<String,Object>> lastYearList = settleManagementDao.selectFactoryServiceMonth(conditionDto);

                    //存放环比同比数据
                    List<Map<String, Object>> monthCompareList = getCompareList(lastMonthList, currentMonthList, classify);
                    List<Map<String, Object>> yearCompareList = getCompareList(lastYearList, currentMonthList, classify);

                    for (Map<String, Object> factoryService : factoryServiceData) {
                        List<Map<String, Object>> monthCompare = monthCompareList.stream().filter(a -> factoryService.get(classify.toString()).equals(a.get(classify.toString()))).collect(Collectors.toList());
                        if (monthCompare.size() > 0) {
                            factoryService.put("monthCompare",monthCompare.get(0).get("compare"));
                        }else {
                            factoryService.put("monthCompare",0.00);
                        }
                        List<Map<String, Object>> yearCompare = yearCompareList.stream().filter(a -> factoryService.get(classify.toString()).equals(a.get(classify.toString()))).collect(Collectors.toList());
                        if (yearCompare.size() > 0) {
                            factoryService.put("yearCompare",yearCompare.get(0).get("compare"));
                        }else {
                            factoryService.put("yearCompare",0.00);
                        }
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return factoryServiceData;
    }

    /**
     * 结算单流程监控
     * @param conditionDto
     * @return
     */
    @Override
    public List<Map<String, Object>> selectStatementMonitor(ConditionDto conditionDto) {
        //工厂别服务违约数据
        List<Map<String,Object>> statementMonitorData = null;
        //将快捷时间转换成起止日期
        if (conditionDto.getBeginDate() == null && conditionDto.getEndDate() == null) {
            conditionDto.setBeginDate(getDate(conditionDto).get(0));
            conditionDto.setEndDate(getDate(conditionDto).get(1));
        }

        if ("1".equals(conditionDto.getPolymerizeWay())) {
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(conditionDto.getEndDate().concat("-01"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.setTime(date);
            }
            conditionDto.setBeginDate(conditionDto.getBeginDate().concat("-01"));
            String lastDayOfMonth = DateUtil.getLastDayOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
            conditionDto.setEndDate(lastDayOfMonth);
        }

        //存放传给前端的流程监控数据
        List<Map<String,Object>> stateDataList = new ArrayList<>();
        //查询结算单流程监控
        statementMonitorData = settleManagementDao.selectStatementMonitor(conditionDto);

        //对结算单状态list集合进行排序
        for (int i = 0; i < 5; i++) {
            Map<String,Object> stateMap = new HashMap<>();
            if (i == 0) {
                if (statementMonitorData.isEmpty()) {
                    stateMap.put("state","服务完成");
                    stateMap.put("stateNumber",0);
                }else {
                    for (int j = 0; j < statementMonitorData.size(); j++) {
                        Map<String, Object> statementMap = statementMonitorData.get(j);
                        if (statementMap.containsValue("服务完成")) {
                            stateMap.put("state","服务完成");
                            stateMap.put("stateNumber",statementMap.get("finishNumber"));
                            break;
                        }else {
                            if (j == statementMonitorData.size() - 1) {
                                stateMap.put("state","服务完成");
                                stateMap.put("stateNumber",0);
                            }
                        }
                    }
                }
            }else if (i == 1) {
                if (statementMonitorData.isEmpty()) {
                    stateMap.put("state","已提交");
                    stateMap.put("stateNumber",0);
                    stateMap.put("stateTime",0);
                }else {
                    for (int j = 0; j < statementMonitorData.size(); j++) {
                        Map<String, Object> statementMap = statementMonitorData.get(j);
                        if (statementMap.containsValue("已提交")) {
                            stateMap.put("state","已提交");
                            stateMap.put("stateNumber",statementMap.get("submitNumber"));
                            stateMap.put("stateTime",statementMap.get("finishAvgTime"));
                            break;
                        }else {
                            if (j == statementMonitorData.size() - 1) {
                                stateMap.put("state","已提交");
                                stateMap.put("stateNumber",0);
                                stateMap.put("stateTime",0);
                            }
                        }
                    }
                }
            }else if (i == 2) {
                if (statementMonitorData.isEmpty()) {
                    stateMap.put("state","待结算");
                    stateMap.put("stateNumber",0);
                    stateMap.put("stateTime",0);
                }else {
                    for (int j = 0; j < statementMonitorData.size(); j++) {
                        Map<String, Object> statementMap = statementMonitorData.get(j);
                        if (statementMap.containsValue("待结算")) {
                            stateMap.put("state","待结算");
                            stateMap.put("stateNumber",statementMap.get("notSettleNumber"));
                            stateMap.put("stateTime",statementMap.get("notSettleAvgTime"));
                            break;
                        }else {
                            if (j == statementMonitorData.size() - 1) {
                                stateMap.put("state","待结算");
                                stateMap.put("stateNumber",0);
                                stateMap.put("stateTime",0);
                            }
                        }
                    }
                }
            }else if (i == 3) {
                if (statementMonitorData.isEmpty()) {
                    stateMap.put("state","已结算");
                    stateMap.put("stateNumber",0);
                    stateMap.put("stateTime",0);
                }else {
                    for (int j = 0; j < statementMonitorData.size(); j++) {
                        Map<String, Object> statementMap = statementMonitorData.get(j);
                        if (statementMap.containsValue("已结算")) {
                            stateMap.put("state","已结算");
                            stateMap.put("stateNumber",statementMap.get("settledNumber"));
                            stateMap.put("stateTime",statementMap.get("settleAvgTime"));
                            break;
                        }else {
                            if (j == statementMonitorData.size() - 1) {
                                stateMap.put("state","已结算");
                                stateMap.put("stateNumber",0);
                                stateMap.put("stateTime",0);
                            }
                        }
                    }
                }
            }else {
                if (statementMonitorData.isEmpty()) {
                    stateMap.put("state","不结算");
                    stateMap.put("stateNumber",0);
                    stateMap.put("stateTime",0);
                }else {
                    for (int j = 0; j < statementMonitorData.size(); j++) {
                        Map<String, Object> statementMap = statementMonitorData.get(j);
                        if (statementMap.containsValue("不结算")) {
                            stateMap.put("state","不结算");
                            stateMap.put("stateNumber",statementMap.get("noSettleNumber"));
                            stateMap.put("stateTime",statementMap.get("noSettleAvgTime"));
                            break;
                        }else {
                            if (j == statementMonitorData.size() - 1) {
                                stateMap.put("state","不结算");
                                stateMap.put("stateNumber",0);
                                stateMap.put("stateTime",0);
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
     * 获取所有的结算对象
     * @return
     */
    @Override
    public List<Map<String, Object>> getSettleObjects() {
        List<Map<String, Object>> settleObjects = settleManagementDao.getSettleObjects();
        settleObjects = settleObjects.stream().filter(a -> a.get("settleObjectName") != null && !"".equals(a.get("settleObjectName"))).collect(Collectors.toList());
        return settleObjects;
    }

    /**
     * 获取日期范围
     * @param conditionDto
     * @return
     */
    @Override
    public List<String> getDateRangeList(ConditionDto conditionDto) {
        List<String> date = getDate(conditionDto);
        List<String> dateList = new ArrayList<>();
        if ("1".equals(conditionDto.getPolymerizeWay())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
            try {
                Calendar min = Calendar.getInstance();
                Calendar max = Calendar.getInstance();
                min.setTime(sdf.parse(date.get(0)));
                min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
                max.setTime(sdf.parse(date.get(1)));
                max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
                Calendar curr = min;
                while (curr.before(max)) {
                    dateList.add(sdf.format(curr.getTime()));
                    curr.add(Calendar.MONTH, 1);
                }
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date start = dateFormat.parse(date.get(0));
                Date end = dateFormat.parse(date.get(1));

                Calendar tempStart = Calendar.getInstance();
                tempStart.setTime(start);

                Calendar tempEnd = Calendar.getInstance();
                tempEnd.setTime(end);
                tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
                while (tempStart.before(tempEnd)) {
                    dateList.add(dateFormat.format(tempStart.getTime()));
                    tempStart.add(Calendar.DAY_OF_YEAR, 1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dateList;
    }

    /**
     * 服务类型-费用分析导出
     * @param response
     * @param conditionDto
     */
    @Override
    public void serviceTypeExport(HttpServletResponse response, ConditionDto conditionDto) {

        //服务类型数据
        List<Map<String,Object>> serviceTypeData = null;

        //当结束日期大于等于当月
        if (conditionDto.getEndDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) >= 0) {
            //当开始日期小于当月时
            if (conditionDto.getBeginDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) <0) {
                if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //当选择月份为当月时，查询未结算的服务数据
                    serviceTypeData = missSettleManagementDao.selectMissServiceType(conditionDto);
                }else if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && !conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询结算的服务数据
                    serviceTypeData = settleManagementDao.selectServiceType(conditionDto);
                }else {
                    //查询已结算的和未结算的服务数据
                    serviceTypeData = settleManagementDao.selectAllServiceType(conditionDto);
                }
            }else {
                //当选择月份为当月时，查询未结算的服务数据
                serviceTypeData = missSettleManagementDao.selectMissServiceType(conditionDto);
            }
        }else {
            //查询结算的服务数据
            serviceTypeData = settleManagementDao.selectServiceType(conditionDto);
        }

        if (conditionDto.getScreenList() != null && conditionDto.getScreenList().size() > 0) {
            //将筛选和排序条件拼接
            serviceTypeData = getQueryField(conditionDto,serviceTypeData);
        }

        //分页
        int size = conditionDto.getExportNumber();
        serviceTypeData = serviceTypeData.stream().skip(0).limit(size).collect(Collectors.toList());

        if ("1".equals(conditionDto.getShowDimension())) {
            DecimalFormat df = new DecimalFormat("##,##0.00");//保留两位小数
            for (Map<String, Object> map : serviceTypeData) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    if ("province".equals(key) || "adminName".equals(key) || "settleObjectName".equals(key)) {
                        continue;
                    }
                    map.put(key,df.format(Double.parseDouble(map.get(key).toString())));
                }
            }
        }

        StringBuilder classify = new StringBuilder();
        StringBuilder classifyName = new StringBuilder();
        if ("1".equals(conditionDto.getClassifyDimension())) {
            classify.append("adminName");
            classifyName.append("区管名称");
        }else if ("2".equals(conditionDto.getClassifyDimension())){
            classify.append("province");
            classifyName.append("省份名称");
        }else {
            classify.append("settleObjectName");
            classifyName.append("工厂名称");
        }

        // 固定表头
        String[] headers = {classifyName.toString(), "安装", "维修", "鉴定", "非上门", "现金补偿", "换机作业单", "商损", "召回", "P板维修"};
        // 生成工作表，设置表名和列名
        HSSFWorkbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("服务类型-费用分析");
        HSSFCellStyle cellStyle = getHeadStyle(book);
        Row row = sheet.createRow(0);
        Cell cell;
        for (int i = 0; i < headers.length; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(headers[i]);
        }
        for (int i = 0; i < serviceTypeData.size(); i++) {
            row = sheet.createRow(i + 1);//创建表格行
            row.createCell(0).setCellValue(serviceTypeData.get(i).get(classify.toString()).toString());
            row.createCell(1).setCellValue(serviceTypeData.get(i).get("install").toString());
            row.createCell(2).setCellValue(serviceTypeData.get(i).get("maintain").toString());
            row.createCell(3).setCellValue(serviceTypeData.get(i).get("identify").toString());
            row.createCell(4).setCellValue(serviceTypeData.get(i).get("noDoor").toString());
            row.createCell(5).setCellValue(serviceTypeData.get(i).get("cash").toString());
            row.createCell(6).setCellValue(serviceTypeData.get(i).get("replacement").toString());
            row.createCell(7).setCellValue(serviceTypeData.get(i).get("businessLoss").toString());
            row.createCell(8).setCellValue(serviceTypeData.get(i).get("recall").toString());
            row.createCell(9).setCellValue(serviceTypeData.get(i).get("boardMaintain").toString());
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        //获取操作时间
        String operationTime = dateFormat.format(new Date());
        // 导出excel模板
        String fileName = "服务类型-费用分析 " + operationTime + ".xls";
        ExcelUtil.exportExcel(response,book,fileName);
    }

    /**
     * 费用分析导出
     * @param response
     * @param conditionDto
     */
    @Override
    public void costAnalysisExport(HttpServletResponse response, ConditionDto conditionDto) {
        //费用分析数据
        List<Map<String,Object>> costAnalysisData = null;

        //当结束日期大于等于当月
        if (conditionDto.getEndDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) >= 0) {
            //当开始日期小于当月时
            if (conditionDto.getBeginDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) <0) {
                if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询未结算的服务数据
                    costAnalysisData = missSettleManagementDao.selectMissCostAnalysis(conditionDto);
                }else if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && !conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询结算的服务数据
                    costAnalysisData = settleManagementDao.selectCostAnalysis(conditionDto);
                }else {
                    //查询结算和已结算的服务数据
                    costAnalysisData = settleManagementDao.selectAllCostAnalysis(conditionDto);
                }
            }else {
                //查询结算的服务数据
                costAnalysisData = settleManagementDao.selectCostAnalysis(conditionDto);
            }
        }else {
            //查询结算的服务数据
            costAnalysisData = settleManagementDao.selectCostAnalysis(conditionDto);
        }

        if (conditionDto.getScreenList() != null && conditionDto.getScreenList().size() > 0) {
            //将筛选和排序条件拼接
            costAnalysisData = getQueryField(conditionDto,costAnalysisData);
        }

        //分页
        int size = conditionDto.getExportNumber();
        costAnalysisData = costAnalysisData.stream().skip(0).limit(size).collect(Collectors.toList());

        DecimalFormat df = new DecimalFormat("##,##0.00");//保留两位小数
        for (Map<String, Object> map : costAnalysisData) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                if ("province".equals(key) || "adminName".equals(key) || "settleObjectName".equals(key)) {
                    continue;
                }
                map.put(key,df.format(Double.parseDouble(map.get(key).toString())));
            }
        }

        StringBuilder classify = new StringBuilder();
        StringBuilder classifyName = new StringBuilder();
        if ("1".equals(conditionDto.getClassifyDimension())) {
            classify.append("adminName");
            classifyName.append("区管名称");
        }else if ("2".equals(conditionDto.getClassifyDimension())){
            classify.append("province");
            classifyName.append("省份名称");
        }else {
            classify.append("settleObjectName");
            classifyName.append("工厂名称");
        }

        // 固定表头
        String[] headers = {classifyName.toString(), "上门费", "人工费", "远程费", "超标准费", "拉修费", "鉴定费", "补贴费", "拆装费", "配送费", "召回费", "物流费", "奖励费用", "赔偿费用", "调整费用"};
        // 生成工作表，设置表名和列名
        HSSFWorkbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("费用分析");
        HSSFCellStyle cellStyle = getHeadStyle(book);
        Row row = sheet.createRow(0);
        Cell cell;
        for (int i = 0; i < headers.length; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(headers[i]);
        }
        for (int i = 0; i < costAnalysisData.size(); i++) {
            row = sheet.createRow(i + 1);//创建表格行
            row.createCell(0).setCellValue(costAnalysisData.get(i).get(classify.toString()).toString());
            row.createCell(1).setCellValue(costAnalysisData.get(i).get("doorCost").toString());
            row.createCell(2).setCellValue(costAnalysisData.get(i).get("artificialCost").toString());
            row.createCell(3).setCellValue(costAnalysisData.get(i).get("remoteCost").toString());
            row.createCell(4).setCellValue(costAnalysisData.get(i).get("excessiveCost").toString());
            row.createCell(5).setCellValue(costAnalysisData.get(i).get("repairCost").toString());
            row.createCell(6).setCellValue(costAnalysisData.get(i).get("authenticateCost").toString());
            row.createCell(7).setCellValue(costAnalysisData.get(i).get("subsidyCost").toString());
            row.createCell(8).setCellValue(costAnalysisData.get(i).get("dismountCost").toString());
            row.createCell(9).setCellValue(costAnalysisData.get(i).get("deliveryCost").toString());
            row.createCell(10).setCellValue(costAnalysisData.get(i).get("recallCost").toString());
            row.createCell(11).setCellValue(costAnalysisData.get(i).get("logisticCost").toString());
            row.createCell(12).setCellValue(costAnalysisData.get(i).get("rewardCost").toString());
            row.createCell(13).setCellValue(costAnalysisData.get(i).get("compensateCost").toString());
            row.createCell(14).setCellValue(costAnalysisData.get(i).get("adjustCost").toString());
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        //获取操作时间
        String operationTime = dateFormat.format(new Date());
        // 导出excel模板
        String fileName = "费用分析 " + operationTime + ".xls";
        ExcelUtil.exportExcel(response,book,fileName);
    }

    /**
     * 工厂/营业费用分析导出
     * @param response
     * @param conditionDto
     */
    @Override
    public void industrialBusinessExport(HttpServletResponse response, ConditionDto conditionDto) {
        //工厂/营业费用分析数据
        List<Map<String,Object>> industrialBusinessData = null;

        //当结束日期大于等于当月
        if (conditionDto.getEndDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) >= 0) {
            //当开始日期小于当月时
            if (conditionDto.getBeginDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) <0) {
                if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询出未结算的工厂/营业费用分析数据
                    industrialBusinessData = missSettleManagementDao.selectMissIndustrialBusiness(conditionDto);
                }else if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && !conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询已结算的工厂/营业费用分析数据
                    industrialBusinessData = settleManagementDao.selectIndustrialBusiness(conditionDto);
                }else {
                    //查询已结算和未结算的工厂/营业费用分析
                    industrialBusinessData = settleManagementDao.selectAllIndustrialBusiness(conditionDto);
                }
            }else {
                //查询已结算的工厂/营业费用分析数据
                industrialBusinessData = settleManagementDao.selectIndustrialBusiness(conditionDto);
            }
        }else {
            //查询已结算的工厂/营业费用分析数据
            industrialBusinessData = settleManagementDao.selectIndustrialBusiness(conditionDto);
        }

        if (conditionDto.getScreenList() != null && conditionDto.getScreenList().size() > 0) {
            //将筛选和排序条件拼接
            industrialBusinessData = getQueryField(conditionDto,industrialBusinessData);
        }

        //分页
        int size = conditionDto.getExportNumber();
        industrialBusinessData = industrialBusinessData.stream().skip(0).limit(size).collect(Collectors.toList());

        if ("1".equals(conditionDto.getShowDimension())) {
            DecimalFormat df = new DecimalFormat("##,##0.00");//保留两位小数
            for (Map<String, Object> map : industrialBusinessData) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    if ("province".equals(key) || "adminName".equals(key) || "settleObjectName".equals(key)) {
                        continue;
                    }
                    map.put(key,df.format(Double.parseDouble(map.get(key).toString())));
                }
            }
        }

        StringBuilder classify = new StringBuilder();
        StringBuilder classifyName = new StringBuilder();
        if ("1".equals(conditionDto.getClassifyDimension())) {
            classify.append("adminName");
            classifyName.append("区管名称");
        }else if ("2".equals(conditionDto.getClassifyDimension())){
            classify.append("province");
            classifyName.append("省份名称");
        }else {
            classify.append("settleObjectName");
            classifyName.append("工厂名称");
        }

        // 固定表头
        String[] headers = {classifyName.toString(), "安装", "维修", "鉴定", "非上门", "现金补偿", "换机作业单", "商损", "召回", "P板维修"};
        // 生成工作表，设置表名和列名
        HSSFWorkbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("工厂-营业费用分析");
        HSSFCellStyle cellStyle = getHeadStyle(book);
        Row row = sheet.createRow(0);
        Cell cell;
        for (int i = 0; i < headers.length; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(headers[i]);
        }
        for (int i = 0; i < industrialBusinessData.size(); i++) {
            row = sheet.createRow(i + 1);//创建表格行
            row.createCell(0).setCellValue(industrialBusinessData.get(i).get(classify.toString()).toString());
            row.createCell(1).setCellValue(industrialBusinessData.get(i).get("install").toString());
            row.createCell(2).setCellValue(industrialBusinessData.get(i).get("maintain").toString());
            row.createCell(3).setCellValue(industrialBusinessData.get(i).get("identify").toString());
            row.createCell(4).setCellValue(industrialBusinessData.get(i).get("noDoor").toString());
            row.createCell(5).setCellValue(industrialBusinessData.get(i).get("cash").toString());
            row.createCell(6).setCellValue(industrialBusinessData.get(i).get("replacement").toString());
            row.createCell(7).setCellValue(industrialBusinessData.get(i).get("businessLoss").toString());
            row.createCell(8).setCellValue(industrialBusinessData.get(i).get("recall").toString());
            row.createCell(9).setCellValue(industrialBusinessData.get(i).get("boardMaintain").toString());
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        //获取操作时间
        String operationTime = dateFormat.format(new Date());
        // 导出excel模板
        String fileName = "工厂-营业费用分析 " + operationTime + ".xls";
        ExcelUtil.exportExcel(response,book,fileName);
    }

    /**
     * 工厂别服务违约导出
     * @param response
     * @param conditionDto
     */
    @Override
    public void factoryServiceBreachExport(HttpServletResponse response, ConditionDto conditionDto) {
        //工厂别服务违约数据
        List<Map<String,Object>> factoryServiceData = null;

        //当结束日期大于等于当月
        if (conditionDto.getEndDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) >= 0) {
            //当开始日期小于当月时
            if (conditionDto.getBeginDate().substring(0,7).compareTo(DateUtil.getMonthFirstDay().substring(0,7)) <0) {
                if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询出未结算的工厂别服务违约数据
                    factoryServiceData = missSettleManagementDao.selectMissFactoryServiceBreach(conditionDto);
                }else if (conditionDto.getSelectMonth() != null && !"".equals(conditionDto.getSelectMonth())
                        && !conditionDto.getSelectMonth().equals(DateUtil.getMonthFirstDay().substring(0,7))) {
                    //查询已结算的工厂别服务违约数据
                    factoryServiceData = settleManagementDao.selectFactoryServiceBreach(conditionDto);
                }else {
                    //查询已结算和未结算的工厂别服务违约数据
                    factoryServiceData = settleManagementDao.selectAllFactoryServiceBreach(conditionDto);
                }
            }else {
                //查询已结算的工厂别服务违约数据
                factoryServiceData = settleManagementDao.selectFactoryServiceBreach(conditionDto);
            }
        }else {
            //查询已结算的工厂别服务违约数据
            factoryServiceData = settleManagementDao.selectFactoryServiceBreach(conditionDto);
        }

        if (conditionDto.getScreenList() != null && conditionDto.getScreenList().size() > 0) {
            //将筛选和排序条件拼接
            factoryServiceData = getQueryField(conditionDto,factoryServiceData);
        }

        //分页
        int size = conditionDto.getExportNumber();
        factoryServiceData = factoryServiceData.stream().skip(0).limit(size).collect(Collectors.toList());

        if ("1".equals(conditionDto.getShowDimension())) {
            DecimalFormat df = new DecimalFormat("##,##0.00");//保留两位小数
            for (Map<String, Object> map : factoryServiceData) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    if ("province".equals(key) || "adminName".equals(key) || "settleObjectName".equals(key)) {
                        continue;
                    }
                    map.put(key,df.format(Double.parseDouble(map.get(key).toString())));
                }
            }
        }

        StringBuilder classify = new StringBuilder();
        StringBuilder classifyName = new StringBuilder();
        if ("1".equals(conditionDto.getClassifyDimension())) {
            classify.append("adminName");
            classifyName.append("区管名称");
        }else if ("2".equals(conditionDto.getClassifyDimension())){
            classify.append("province");
            classifyName.append("省份名称");
        }else {
            classify.append("settleObjectName");
            classifyName.append("工厂名称");
        }

        // 生成工作表，设置表名和列名
        HSSFWorkbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("工厂别服务违约");
        HSSFCellStyle cellStyle = getHeadStyle(book);
        Row row = sheet.createRow(0);
        Cell cell;
        if ("1".equals(conditionDto.getShowDimension())) {
            // 固定表头
            String[] headers = {classifyName.toString(), "超标准费用", "违约驳回", "以换代修"};
            for (int i = 0; i < headers.length; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(headers[i]);
            }
        }else {
            // 固定表头
            String[] headers = {classifyName.toString(), "超标准费用", "违约驳回", "以换代修", "购机发票申请驳回"};
            for (int i = 0; i < headers.length; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(headers[i]);
            }
        }
        for (int i = 0; i < factoryServiceData.size(); i++) {
            row = sheet.createRow(i + 1);//创建表格行
            row.createCell(0).setCellValue(factoryServiceData.get(i).get(classify.toString()).toString());
            row.createCell(1).setCellValue(factoryServiceData.get(i).get("excessive").toString());
            row.createCell(2).setCellValue(factoryServiceData.get(i).get("serviceBreach").toString());
            row.createCell(3).setCellValue(factoryServiceData.get(i).get("changeRepair").toString());
            if ("2".equals(conditionDto.getShowDimension())) {
                row.createCell(4).setCellValue(factoryServiceData.get(i).get("purchaseReject").toString());
            }
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取操作时间
        String operationTime = dateFormat.format(new Date());
        // 导出excel模板
        String fileName = "工厂别服务违约 " + operationTime + ".xls";
        ExcelUtil.exportExcel(response,book,fileName);
    }

    public HSSFCellStyle getHeadStyle(HSSFWorkbook book) {
        HSSFCellStyle style = book.createCellStyle();
        style.setWrapText(true);//自动换行
        style.setAlignment(HorizontalAlignment.CENTER);// 左右居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
        // 设置单元格的背景颜色为淡蓝色
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置单元格边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        // 设置字体,大小
        HSSFFont font = book.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public List<Map<String,Object>> getCompareList(List<Map<String,Object>> lastList,List<Map<String,Object>> currentList,StringBuilder classify) {
        List<Map<String,Object>> compareList = new ArrayList<>();
        if (lastList.isEmpty()) {
            for (Map<String, Object> map : currentList) {
                Map<String,Object> compareMap = new HashMap<>();
                compareMap.put(classify.toString(),map.get(classify.toString()));
                compareMap.put("compare",0.00);
                compareList.add(compareMap);
            }
        }
        if (!lastList.isEmpty()) {
            lastList = lastList.stream().filter(a -> a.get(classify.toString()) != null && !"".equals(a.get(classify.toString()))).collect(Collectors.toList());
        }
        if (!currentList.isEmpty()) {
            currentList = currentList.stream().filter(a -> a.get(classify.toString()) != null && !"".equals(a.get(classify.toString()))).collect(Collectors.toList());
        }

        for (Map<String, Object> map : currentList) {
            Map<String,Object> compareMap = new HashMap<>();
            List<Map<String, Object>> collect = lastList.stream().filter(a -> map.get(classify.toString()).equals(a.get(classify.toString()))).collect(Collectors.toList());
            if (collect.size() > 0) {
                compareMap.put(classify.toString(),map.get(classify.toString()));
                if ((int)(Double.parseDouble(collect.get(0).get("total").toString())) == 0) {
                    compareMap.put("compare",0.00);
                }
                if ((int)(Double.parseDouble(collect.get(0).get("total").toString())) != 0) {
                    //计算环比
                    float current = Float.parseFloat(map.get("total").toString());
                    float last = Float.parseFloat(collect.get(0).get("total").toString());
                    String compare = String.format("%.2f", (current - last)/last * 100);
                    compareMap.put("compare",Float.parseFloat(compare));
                }
            }else {
                compareMap.put(classify.toString(),map.get(classify.toString()));
                compareMap.put("compare",0.00);
            }
            compareList.add(compareMap);
        }
        return compareList;
    }

    public List<String> getDate(ConditionDto conditionDto) {
        List<String> dateList = new ArrayList<>();
        if ("1".equals(conditionDto.getPolymerizeWay())) {
            if (conditionDto.getDateRange() != null) {
                switch (conditionDto.getDateRange()) {
                    case "1":
                        dateList = DateUtil.pastHalfYear(new Date());
                        break;
                    case "2":
                        dateList = DateUtil.pastYear(new Date());
                        break;
                    case "3":
                        String startQuarter = DateUtil.getStartQuarter(new Date()).substring(0,7);
                        String lastQuarter = DateUtil.getLastQuarter(new Date()).substring(0,7);
                        dateList.add(startQuarter);
                        dateList.add(lastQuarter);
                        break;
                    case "4":
                        String seasonStartDate = DateUtil.getSeasonStartDate(new Date()).substring(0,7);
                        String seasonEndDate = DateUtil.getSeasonEndDate(new Date()).substring(0,7);
                        dateList.add(seasonStartDate);
                        dateList.add(seasonEndDate);
                        break;
                    default:
                        String beginDate = DateUtil.currentYearFirst(new Date()).substring(0,7);
                        String endDate = DateUtil.currentYearLast(new Date()).substring(0,7);
                        dateList.add(beginDate);
                        dateList.add(endDate);
                        break;
                }
            }
        }else {
            if (conditionDto.getDateRange() != null) {
                switch (conditionDto.getDateRange()) {
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
        }
        return dateList;
    }

    public List<Map<String,Object>> getQueryField(ConditionDto conditionDto,List<Map<String,Object>> list) {
        List<String> queryList = new ArrayList<>();
        Set<String> keySet = conditionDto.getScreenList().keySet();
        for (String key : keySet) {
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(conditionDto.getScreenList().get(key)));
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
                    list = list.stream().filter(a -> (jsonObject.get("startDate").toString().compareTo(a.get(key).toString().substring(0,10)) <= 0
                        && jsonObject.get("endDate").toString().compareTo(a.get(key).toString().substring(0,10)) >= 0)).collect(Collectors.toList());
                }else if ("before".equals(jsonObject.get("type"))) {
//                    query = key + " &lt;= '" + jsonObject.get("value") + "' ";
                    list = list.stream().filter(a -> jsonObject.get("value").toString().compareTo(a.get(key).toString().substring(0,10)) > 0).collect(Collectors.toList());
                }else if ("after".equals(jsonObject.get("type"))) {
//                    query = key + " &gt;= '" + jsonObject.get("value") + "' ";
                    list = list.stream().filter(a -> jsonObject.get("value").toString().compareTo(a.get(key).toString().substring(0,10)) < 0).collect(Collectors.toList());
                }else if ("day".equals(jsonObject.get("type"))) {
//                    query = key + " = '" + jsonObject.get("value") + "' ";
                    list = list.stream().filter(a -> jsonObject.get("value").toString().equals(a.get(key).toString().substring(0,10))).collect(Collectors.toList());
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
//                else {
//                    //数字类型
//                    query = key + "+0 " + jsonObject.get("type") + jsonObject.get("value") + "+0 ";
//                }
//                queryList.add(query);
            }

            if (jsonObject.get("sort") != null && !"".equals(jsonObject.get("sort"))) {
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
//                String order = key + " " + jsonObject.get("sort");
//                conditionDto.setOrderQuery(order);
            }
        }
//        conditionDto.setScreenQuery(queryList);
        return list;
    }

}
