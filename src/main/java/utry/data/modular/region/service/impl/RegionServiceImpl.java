package utry.data.modular.region.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.bytebuddy.dynamic.DynamicType;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.modular.aop.LogAspectForSongXia;
import utry.data.modular.baseConfig.dao.TargetCoreConfigDao;
import utry.data.modular.baseConfig.dao.TargetUserConfigDao;
import utry.data.modular.baseConfig.dto.IndicatorDTO;
import utry.data.modular.baseConfig.dto.IndicatorUserDTO;
import utry.data.modular.baseConfig.service.TargetCoreConfigService;
import utry.data.modular.baseConfig.service.TargetUserConfigService;
import utry.data.modular.common.dto.TemplateQueryDataDto;
import utry.data.modular.common.service.CommonTemplateService;
import utry.data.modular.complaints.dao.ComplaintsDao;
import utry.data.modular.complaints.service.ComplaintsService;
import utry.data.modular.indicatorWarning.service.IndicatorWarningService;
import utry.data.modular.region.controller.dto.RegionComplaintDto;
import utry.data.modular.region.controller.dto.RegionVisitMonitoringRequest;
import utry.data.modular.region.dao.RegionDao;
import utry.data.modular.region.service.RegionService;
import utry.data.modular.settleManagement.utils.DateUtil;
import utry.data.util.HttpClientUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 大区服务业务接口实现
 */
@Service
public class RegionServiceImpl implements RegionService {

    //大区服务
    @Resource
    private RegionDao regionDao;
    //投诉单
    @Resource
    private ComplaintsDao complaintsDao;
    //系统配置（获取硕德的IP）
    @Resource
    private SysConfServiceImpl sysConfService;
    //获取当前月目标
    @Resource
    private TargetCoreConfigService targetCoreConfigService;
    //获取当前月大区目标
    @Resource
    private TargetUserConfigService targetUserConfigService;
    //获取指定月目标
    @Resource
    private TargetCoreConfigDao targetCoreConfigDao;

    @Resource
    private ComplaintsService complaintsService;

    @Resource
    private IndicatorWarningService indicatorWarningService;

    @Resource
    private CommonTemplateService commonTemplateService;

    //获取指定月目标
    @Resource
    private TargetUserConfigDao targetUserConfigDao;
    /**
     * -------------------SPI业务-----------------------
     */

    /**
     * 服务单详情推送（SPI）业务实现
     * @param map
     * @return
     */
    @Override
    public RetResult serviceDetail(Map map) {
        try {
            //动作
            String action = "";
            if (map.get("action") != null) {
                action = (String) map.get("action");
            } else {
                return RetResponse.makeErrRsp("动作不能为空！");
            }
            //派工单号
            String dispatchingOrder = "";
            if (map.get("dispatchingOrder") != null) {
                dispatchingOrder = (String) map.get("dispatchingOrder");
            } else {
                return RetResponse.makeErrRsp("派工单号不能为空！");
            }
            //根据派工单号查询本地数据
            Map dispatching = regionDao.serviceDetailById(dispatchingOrder);
            int error = 0;
            if(dispatching!=null){
                error = (int) dispatching.get("error");
            }
            //业务时间
            String serviceTime = map.get("serviceTime") != null ? (String) map.get("serviceTime") : null;
            //流转记录相关参数
            Map<String, String> transfer = new HashMap<>();
            transfer.put("associatedNumber", dispatchingOrder);//关联单号
            transfer.put("operationTime", serviceTime);//操作时间
            transfer.put("operationContent", action);//操作内容
            transfer.put("relatedData", JSON.toJSONString(map));//相关数据
            //保存流转操作信息
            regionDao.transferSave(transfer);
            if(error==0){
                //初始服务类型判断
                if(map.get("serviceType")!=null){
                    if("维修".equals(map.get("serviceType"))||"安装".equals(map.get("serviceType"))||"非上门维修".equals(map.get("serviceType"))){
                        if(dispatching==null||dispatching.get("firstServiceType")==null){
                            map.put("firstServiceType",map.get("serviceType"));
                        }
                    }
                }
                //判断动作类型
                if ("工单创建".equals(action)) {
                    if (dispatching != null) {
                        return RetResponse.makeRsp(401,"已存在该派工单号！");
                    }
                    //工单创建没有serviceTime参数，以派工时间代替
                    //transfer.put("operationTime", (String) map.get("dispatchingTime"));
                    //新增派工单信息
                    regionDao.serviceDetailAdd(map);
                }else{
                    //非工单创建推送数据，先拉取派工单信息保存再取执行推送的内容
                    if (dispatching == null) {
                        regionDao.errorSave(dispatchingOrder);
                        return RetResponse.makeErrRsp("找不到派工单！");
                    }
               /* Map mapt = new HashMap();
                mapt.put("associatedNumber",dispatchingOrder);
                List<Map> ti = regionDao.transferInformation(mapt);
                if(ti!=null&&ti.size()>0){*/
                    //判断推送时间是否大于更新时间
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long ul = sdf.parse((String) dispatching.get("updateTime")).getTime();
                        long nl = sdf.parse(serviceTime).getTime();
                        if (nl < ul) {
                            regionDao.errorSave(dispatchingOrder);
                            return RetResponse.makeOKRsp();
                        }
                    }catch (Exception e){

                    }
                    //}

                    if ("区域确认".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成考核开始时间
                        map.put("startTimeOfAssessment", serviceTime);
                        map.put("accountingArea", "-");
                        map.put("accountingAreaCode", "-");
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("确认转移".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成考核开始时间
                        map.put("startTimeOfAssessment", serviceTime);
                        map.put("accountingArea", "-");
                        map.put("accountingAreaCode", "-");
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("寄件签收".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成考核开始时间和寄件门店签收时间
                        map.put("startTimeOfAssessment", serviceTime);
                        map.put("shopDeliveryTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("接收".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成接收时间
                        map.put("receiptTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("预约".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成预约操作时间
                        map.put("appointmentOperationTime", serviceTime);
                        //预约时间和预约时间段同步首次预约时间和首次预约时间段
                        map.put("firstAppointmentTime", map.get("appointmentTime"));
                        map.put("firstAppointmentTimeSection", map.get("appointmentTimeSection"));
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("到达现场".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成上门时间
                        map.put("visitTime", serviceTime);
                        //判断是否有首次上门时间，如果没有则同步上门时间和首次上门时间
                        if (dispatching.get("firstVisitTime") == null) {
                            map.put("firstVisitTime", serviceTime);
                        }
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("还件签收".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成还件签收时间
                        map.put("customerDeliveryTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("服务完成".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成完成时间
                        map.put("finishTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);

                        //完成的派工单拉取派工单详情信息，更新整个派工单及相关子表信息
                        //设置调用接口参数
                        Map map1 = new HashMap<>();
                        map1.put("dispatchingOrder",dispatchingOrder);
                        //调用派工单详情拉取接口
                        RetResult rr = dispatchingDetailApi(map1);
                        if(rr.getCode()==200){
                            //成功获取派工单详情
                            Map dispatchingDetail = (Map) rr.getData();
                            if(dispatchingDetail!=null){
                                //更新派工单信息
                                regionDao.serviceDetailEdit(dispatchingDetail);
                                //提取调换部件信息并批量更新至调换部件信息表中
                                List<Map> replacePart = (List<Map>) dispatchingDetail.get("replacePart");
                                if(replacePart!=null&&replacePart.size()>0){
                                    regionDao.replaceAdds(replacePart,dispatchingOrder);
                                }
                                //提取维修部件信息并批量更新至维修部件信息表中
                                List<Map> repairPart = (List<Map>) dispatchingDetail.get("repairPart");
                                if(repairPart!=null&&repairPart.size()>0){
                                    regionDao.repairAdds(repairPart,dispatchingOrder);
                                }
                            }
                        }
                    } else if ("还件确认".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成门店还件时间
                        map.put("shopReturnTime", serviceTime);
                        //将业务时间转换成完成时间
                        map.put("finishTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("以换代修".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成以换代修提交时间和完成时间
                        map.put("exchangeMaintain", serviceTime);
                        map.put("finishTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("作废申请".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    }else if ("拒绝作废".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    }else if ("确认作废".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成作废时间
                        map.put("cancellationTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("改派".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成改派时间
                        map.put("modificationDispatchTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("改约".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成改约时间
                        map.put("modificationAppointmentTime", serviceTime);
                        //获取现有改约次数并+1，如果为空则0+1
                        Integer rescheduleFrequency = 0;
                        if (dispatching.get("rescheduleFrequency") != null && "".equals(dispatching.get("rescheduleFrequency"))) {
                            rescheduleFrequency = Integer.parseInt((String) dispatching.get("rescheduleFrequency"));
                        }
                        rescheduleFrequency++;
                        map.put("rescheduleFrequency", rescheduleFrequency);
                        map.put("firstPunctualityEligible", "0");
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("服务类型变更".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        map.put("fwlxbg","t");
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("派单".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成派工操作时间
                        map.put("dispatchingOperationTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("提交".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成提交时间
                        map.put("submissionTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("还件揽收".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //将业务时间转换成还件揽收时间
                        map.put("returnCollectionTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("核算状态变更".equals(action)) {
                        if (dispatching == null) {
                            return RetResponse.makeErrRsp("找不到派工单！");
                        }
                        //根据核算状态分别将时间写入对应状态的时间
                        if("已结算".equals(map.get("calculationState"))){
                            map.put("settledTime", serviceTime);//已结算时间
                        }else if("不结算".equals(map.get("calculationState"))){
                            map.put("noSettledTime", serviceTime);//不结算时间
                        }else if("未结算".equals(map.get("calculationState"))){
                            map.put("notSettledTime", serviceTime);//未结算时间
                        }
                        //将业务时间转换成核算时间
                        map.put("calculationTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else {
                        return RetResponse.makeErrRsp("该操作无法理解！");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp(e.getMessage());
        }
        return RetResponse.makeOKRsp();
    }

    /**
     * 挂单解挂（SPI）业务实现
     * @param map
     * @return
     */
    @Override
    public RetResult pendingOrder(Map map) {
        try {
            //动作
            String action = "";
            if (map.get("action") != null) {
                action = (String) map.get("action");
            } else {
                return RetResponse.makeErrRsp("动作不能为空！");
            }
            //派工单号
            String dispatchingOrder = "";
            if (map.get("dispatchingOrder") != null) {
                dispatchingOrder = (String) map.get("dispatchingOrder");
            } else {
                return RetResponse.makeErrRsp("派工单号不能为空！");
            }
            //业务时间
            String serviceTime = "";
            if (map.get("serviceTime") != null) {
                serviceTime = (String) map.get("serviceTime");
            } else {
                return RetResponse.makeErrRsp("业务时间不能为空！");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //流转记录相关参数
            Map<String, String> transfer = new HashMap<>();
            transfer.put("associatedNumber", dispatchingOrder);//关联单号
            transfer.put("operationTime", serviceTime);//操作时间
            transfer.put("operationContent", action);//操作内容
            transfer.put("relatedData", JSON.toJSONString(map));//相关数据
            //保存流转记录
            regionDao.transferSave(transfer);
            //根据派工单号查出派工单信息
            Map dis = regionDao.serviceDetailById(dispatchingOrder);
            //判断更新时间
            if(dis!=null){
                try {
                    long ul = sdf.parse((String) dis.get("updateTime")).getTime();
                    long nl = sdf.parse(serviceTime).getTime();
                    if (nl < ul) {
                        regionDao.errorSave(dispatchingOrder);
                        return RetResponse.makeOKRsp();
                    }
                }catch (Exception e){
                    //return RetResponse.makeRsp(400, "业务时间格式错误！");
                }
            }
            //获取相关挂单记录
            List<Map> pendingList = regionDao.pendingOrderList(map);
            Map map1 = new HashMap<>();
            map1.put("dispatchingOrder",dispatchingOrder);
            //判断动作
            if ("挂单".equals(action)) {
                //判断如果有未接挂记录，不能新增新的挂单
                if (pendingList == null || pendingList.size() == 0 || pendingList.get(0).get("finishOrderTime") != null) {
                    if(pendingList!=null&&pendingList.size()>0){
                        String t1 = (String) pendingList.get(0).get("finishOrderTime");
                        String t2 = (String) map.get("serviceTime");
                        Date start = sdf.parse(t1);
                        Date end = sdf.parse(t2);
                        if(start.getTime()>end.getTime()){
                            return RetResponse.makeRsp(401,"挂单时间早于上次解挂时间！");
                        }
                    }

                    //挂单新增挂单记录
                regionDao.pendingOrderAdd(map);
                map1.put("pendingState","挂单");
                try {
                    //修改派工单挂单状态为派工状态
                    regionDao.serviceDetailEdit(map1);
                }catch (Exception e){
                    e.printStackTrace();
                    return RetResponse.makeRsp(401,"找不到派工单！");
                }

                }else{
                    return RetResponse.makeErrRsp("有未解挂记录！");
                }
            } else if ("解挂".equals(action)) {
                //判断最近一条记录的解挂时间是否为空，不为空就没有挂单
                if (pendingList == null || pendingList.size() == 0 || (pendingList.get(0).get("finishOrderTime") != null&&!"".equals(pendingList.get(0).get("finishOrderTime")))) {
                    return RetResponse.makeErrRsp("没有需要解挂信息！");
                }
                //因为查询倒序排序，第一条记录为最近的时间，获取id并修改该条记录的解挂时间
                map.put("pendingId", pendingList.get(0).get("pendingId"));

                //解挂时间要在挂单时间之后
                String t1 = (String) pendingList.get(0).get("pendingOrderTime");
                String t2 = (String) map.get("serviceTime");
                Date start = sdf.parse(t1);
                Date end = sdf.parse(t2);
                if(start.getTime()>end.getTime()){
                    return RetResponse.makeRsp(401,"解挂时间早于挂单时间！");
                }
                regionDao.pendingOrderEdit(map);


                //修改派工单主表挂单状态为解挂
                if(dis!=null){
                    Integer pendingTime = 0;
                    //Integer pendingTime = (Integer) dis.get("pendingTime");
                    if(dis.get("pendingTime")!=null){
                        pendingTime =Integer.parseInt((String) dis.get("pendingTime"));
                    }
                    pendingTime = pendingTime + (int)((end.getTime()-start.getTime())/1000);
                    map1.put("pendingState","解挂");
                    map1.put("pendingTime",pendingTime);
                    //如果是上门服务，日期不是同一天修改二次上门次数
                    if(
                            (
                                    "维修".equals(dis.get("serviceType"))
                                            ||"安装".equals(dis.get("serviceType"))
                                            ||"维修".equals(dis.get("firstServiceType"))
                                            ||"安装".equals(dis.get("firstServiceType"))
                            )&&
                                    (!t2.substring(0,10).equals(t1.substring(0,10)))
                    ){
                        Integer towUp = 0;
                        if(dis.get("towUp")!=null){
                            towUp = Integer.parseInt((String) dis.get("towUp"));
                        }
                        towUp ++;
                        map1.put("towUp",towUp);
                    }
                    regionDao.serviceDetailEdit(map1);
                }
            } else {
                return RetResponse.makeErrRsp("该操作无法理解！");
            }

        }catch (Exception e){
            return RetResponse.makeErrRsp(e.getMessage());
        }
        return RetResponse.makeOKRsp();
    }

    /**
     * 二次上门认定推送（SPI）业务实现
     * @param map
     * @return
     */
    @Override
    public RetResult secondDoor(Map map) {
        try {
        //判断必要参数派工单号是否存在
        if(map.get("dispatchingOrder")==null||"".equals(map.get("dispatchingOrder"))){
            return RetResponse.makeErrRsp("派工单号不能为空！");
        }
        //根据派工单号查询是否存在该记录，存在即修改，不存在即插入
        Map secondDoor = regionDao.secondDoorById(map);
        if(secondDoor==null){
            regionDao.secondDoorAdd(map);
            Map dis = regionDao.serviceDetailById((String) map.get("dispatchingOrder"));
            //非上门服务单，二次上门认定修改派工单二次上门次数
            if(dis !=null&&("非上门维修".equals(dis.get("serviceType"))||"非上门维修".equals(dis.get("firstServiceType")))){
                Map map1 = new HashMap<>();
                map1.put("dispatchingOrder",map.get("dispatchingOrder"));
                map1.put("towUp","1");
                map1.put("repairEligible","0");
                regionDao.serviceDetailEdit(map1);
            }

        }else {
            regionDao.secondDoorEdit(map);
        }
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp(e.getMessage());
        }
        return RetResponse.makeOKRsp();
    }


    /**
     * -------------------API业务-----------------------
     */

    /**
     * 全量获取服务门店信息（API）
     * @param map
     * @return
     */
    @Override
    public RetResult storeApi(Map map) {
        //全量获取服务门店信息（API）请求地址
        String url = "/GetStoreInformation";
        //默认状态
        int code = 400;
        Map data = new HashMap<>();
        try {
            //访问接口获取数据
            data = httpClient(url,map);
            //取出返回结果状态码
            code = (Integer) data.get("code");
            //200为成功
            if(code==200){
                //获取全部服务店信息
                List<Map> storeList1 = (List<Map>) data.get("data");
                for(Map map1:storeList1){
                    //规范处理营业开始和结束时间
                    String openingTime = (String) map1.get("openingTime");
                    String closingTime = (String) map1.get("closingTime");
                    String[] a = openingTime.split(":");
                    String[] b = closingTime.split(":");
                    if(a!=null&&a.length==3&&a[0].length()==1){
                        openingTime = "0"+openingTime;
                    }else if(a!=null&&a.length==2){
                        if(a[0].length()==1){
                            openingTime = "0"+openingTime;
                        }
                        openingTime = openingTime+":00";
                    }else if(a!=null&&a.length==1){
                        openingTime = null;
                    }
                    if(b!=null&&b.length==3&&b[0].length()==1){
                        closingTime = "0"+closingTime;
                    }else if(b!=null&&b.length==2){
                        if(b[0].length()==1){
                            closingTime = "0"+closingTime;
                        }
                        closingTime = closingTime+":00";
                    }else if(b!=null&&b.length<2){
                        closingTime = null;
                    }
                    map1.put("openingTime",openingTime);
                    map1.put("closingTime",closingTime);
                }
                if(storeList1!=null&&storeList1.size()>0){
                    //返回数据有本地没有（新增）
                    List<Map> addList = new ArrayList<>();
                    //返回数据有本地有（修改）
                    List<Map> upList = new ArrayList<>();
                    //返回数据没有本地有（逻辑删除）
                    List<Map> delList = new ArrayList<>();
                    //查询本地
                    List<Map> storeList2 = regionDao.storeList();
                    //复制本地数据到删除数据集合，默认全删除
                    //BeanUtils.copyProperties(storeList2,delList);
                    delList =storeList2.stream().collect(Collectors.toList());
                    for(Map store1 : storeList1){
                        //默认设置当前记录为新增
                        boolean isAdd = true;
                        for(Map store2 : storeList2){
                            //判断返回服务店是否存在本地
                            if(store1.get("storeNumber").equals(store2.get("storeNumber"))&&store1.get("accountingCenterCode").equals(store2.get("accountingCenterCode"))){
                                //存在本地的数据加入修改集合
                                upList.add(store1);
                                //删除要删除集合中的要修改的记录
                                delList.remove(store2);
                                //将新增的状态修改为不新增
                                isAdd=false;
                                break;
                            }
                        }
                        //对比后还是新增状态的数据放入新增数据
                        if(isAdd){
                            addList.add(store1);
                        }
                    }
                    if(addList.size()>0) {
                        //新增新增集内数据
                        regionDao.storeAdds(addList);
                    }
                    if(upList.size()>0) {
                        //修改修改集内数据
                        regionDao.storeUpdates(upList);
                    }
                    if(delList.size()>0) {
                        //逻辑删除删除数据集内的记录
                        regionDao.storeDels(delList);
                    }
                }
            }
        }catch (Exception e){
            //报错将状态码改为错误400，并给出错误信息
            code = 400;
            data.put("message",e.getMessage());
        }
        return RetResponse.makeRsp(code,(String)data.get("message"),data.get("data"));
    }

    /**
     * 工程师管理信息获取（API）
     * @param map
     * @return
     */
    @Override
    public RetResult engineerManagementApi(Map map) {
        //工程师管理信息获取（API）接口地址
        String url = "/GetEngineerInformation";
        //默认状态码
        int code = 400;
        Map data = new HashMap<>();
        try {
            //接口获取数据
            data = httpClient(url,map);
            //提取状态码
            code = (Integer) data.get("code");
        }catch (Exception e){
            code = 400;
            data.put("message",e.getMessage());
        }
        //将数据与总记录条数取出按需要格式返回
        Map map1 = new HashMap<>();
        map1.put("data",data.get("data"));
        map1.put("total",data.get("total"));
        return RetResponse.makeRsp(code,(String)data.get("message"),map1);
    }

    /**
     * 派工单详情获取（API）
     * @param map
     * @return
     */
    @Override
    public RetResult dispatchingDetailApi(Map map) {
        //派工单详情获取（API）请求地址
        String url = "/GetDispatchDetail";
        //默认状态码
        int code = 400;
        Map data = new HashMap<>();
        try {
            //调用接口获取数据
            data = httpClient(url,map);
            //提取状态码
            code = (Integer) data.get("code");
        }catch (Exception e){
            //报错返回错误信息
            code = 400;
            data.put("message",e.getMessage());
        }
        return RetResponse.makeRsp(code,(String)data.get("message"),data.get("data"));
    }

    /**
     * 投诉处理单拉取（API）
     * @param map
     * @return
     */
    @Override
    public RetResult complaintHandlingApi(Map map) {
        //投诉处理单拉取（API）接口地址
        String url = "/GetComplaintDeal";
        //默认状态码
        int code = 400;
        Map data = new HashMap<>();
        try {
            //查询所有未结案投诉单号
            String[] notFinishIds = complaintsDao.notFinishIds();
            if(notFinishIds!=null&&notFinishIds.length>0){
                for (int i=0;i<notFinishIds.length;i=i+10){
                    int j =i+10;
                    if(j>notFinishIds.length){
                        j=notFinishIds.length;
                    }
                    //传入单号集合调用接口获取数据
                    map.put("complaintNumber",Arrays.copyOfRange(notFinishIds,i,j));
                    //调用接口获取数据
                    data = httpClient(url,map);
                    code = (Integer) data.get("code");
                    if(code==200){
                        //成功获取数据并且有数据则修改投诉单内容
                        List<Map> complaintList = (List<Map>) data.get("data");
                        if(complaintList!=null&&complaintList.size()>0){
                            //遍历返回的投诉单内容
                            for (Map complaint : complaintList){
                                //更新投诉单内容
                                complaintsDao.complaintDetailEdit(complaint);
                                //获取投诉留言并添加投诉单号并插入投诉留言表信息
                                List<Map> complaintMessageList = (List<Map>) complaint.get("complaintMessage");
                                complaintsDao.complaintMessageDel((String) complaint.get("complaintNumber"));
                                if(complaintMessageList!=null&&complaintMessageList.size()>0){
                                    for(Map map1:complaintMessageList){
                                        map1.put("complaintNumber",complaint.get("complaintNumber"));
                                    }
                                    complaintsDao.complaintMessageAdd(complaintMessageList);
                                }
                                //获取投诉处理明细并添加投诉单号并插入投诉处理明细表信息
                                List<Map> complaintProcessList = (List<Map>) complaint.get("complaintProcessDetail");
                                complaintsDao.complaintProcessDetailDel((String) complaint.get("complaintNumber"));
                                if(complaintProcessList!=null&&complaintProcessList.size()>0){
                                    for(Map map1:complaintProcessList){
                                        map1.put("complaintNumber",complaint.get("complaintNumber"));
                                    }
                                    complaintsDao.complaintProcessDetailAdd(complaintProcessList);
                                }

                                //获取需要通知的投诉单号和部门
                                Map<String, List<Map<String, String>>> noticeMap = complaintsService.complaintUpdateAdd(complaint);
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
     * -------------------本地业务-----------------------
     */

    /**
     * 30分钟及时预约率
     * @param map
     * @return
     */
    @Override
    public RetResult timely(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        BigDecimal bfb = new BigDecimal(100);
        //获取时间范围内率
        Map rate = regionDao.timely(map);
       if(rate==null){
            rate = new HashMap<>();
        }
       //通过分子分母计算率
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
            rate.put("rate","0");
        }else{
            BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
            BigDecimal last = new BigDecimal( rate.get("total").toString());
            rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
        }

        returnMap.put("rate",rate);
        //获取时间范围内每日率（折线图）
        List<Map> dateRate =  regionDao.dateTimely(map);
        //给没有数据的日期补零并排序
        dateRate = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateRate);
        returnMap.put("dateRate",dateRate);
        returnMap.put("target", getIndicator(map,"timelyRatw"));
        returnMap.put("QOQ", "");
        try {
            //修改本月日期为上月日期
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                map = lastMonth(map);
                //统计上月率
                Map lastRate = regionDao.timely(map);
                if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                try{
                    //上月分子
                    BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                    //上月分母
                    BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                    //本月率
                    BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                    //计算上月率
                    BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    //计算同比上月
                    BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }catch (Exception e){
                    returnMap.put("QOQ", "0");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     * 30分钟及时预约率图表
     * @param map
     * @return
     */
    @Override
    public RetResult timelyMap(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        BigDecimal bfb = new BigDecimal(100);
        //获取时间范围内率
        Map rate = regionDao.timely(map);
       if(rate==null){
            rate = new HashMap<>();
        }
        //通过分子分母计算率
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
            rate.put("rate","0");
        }else{
            BigDecimal now =new BigDecimal( rate.get("eligible").toString()) ;
            BigDecimal last = new BigDecimal(rate.get("total").toString());
            rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
        }
        returnMap.put("rate",rate);
        //获取时间范围内每日率（折线图）
        List<Map> dateRate =  regionDao.dateTimely(map);
        //给没有数据的日期补零并排序
        dateRate = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateRate);
        returnMap.put("dateRate",dateRate);
        returnMap.put("target", getIndicator(map,"timelyRatw"));
        try {

            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                //修改本月日期为上月日期
                map = lastMonth(map);
                //获取时间范围上月每日率（折线图）
                List<Map> lastDateTimely =  regionDao.dateTimely(map);
                //给没有数据的日期补零并排序
                lastDateTimely = dateAll((String) map.get("startTime"), (String) map.get("endTime"),lastDateTimely);
                returnMap.put("lastDateRate",lastDateTimely);
                //统计上月率
                Map lastRate = regionDao.timely(map);
                if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                try{
                    //上月分子
                    BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                    //上月分母
                    BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                    //本月率
                    BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                    //计算上月率
                    BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    //计算同比上月
                    BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }catch (Exception e){
                    returnMap.put("QOQ", "0");
                }

            }else{
                returnMap.put("QOQ", "");
                //returnMap.put("target", "");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     * 30分钟及时预约率饼图
     * @param map
     * @return
     */
    @Override
    public RetResult timelyPie(Map map) {
        try {
            //处理日期
            map = dateConversion(map);
            Map returnMap = new HashMap<>();
            //获取时间范围内率
            List<Long> timelyList = regionDao.timelyPie(map);
            List<Map> timelyPie = new ArrayList<>();
            timelyList = timelyList.stream().filter(m -> m!=null).collect(Collectors.toList());
            Map map1 = new HashMap<>();
            map1.put("name","30分钟内");
            map1.put("value",timelyList.stream().filter(m -> m<30).collect(Collectors.toList()).size());
            Map map2 = new HashMap<>();
            map2.put("name","30~60分钟");
            map2.put("value",timelyList.stream().filter(m -> m>=30&&m<=60).collect(Collectors.toList()).size());
            Map map3 = new HashMap<>();
            map3.put("name","大于60分钟");
            map3.put("value",timelyList.stream().filter(m -> m>60).collect(Collectors.toList()).size());
            timelyPie.add(map1);
            timelyPie.add(map2);
            timelyPie.add(map3);
            List<Map> punctualityPie = regionDao.punctualityPie(map);
            returnMap.put("timelyPie",timelyPie);
            returnMap.put("punctualityPie",punctualityPie);
            return RetResponse.makeOKRsp(returnMap);
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }

    @Override
    public RetResult updateTwoUp(Map map) {
        try {
            Integer count1 = regionDao.updateTwoUp1(map);
            Integer count2 = regionDao.updateTwoUp2(map);
            return RetResponse.makeOKRsp("更新：上门"+count1+ "条记录；非上门"+count2+"条记录");
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }

    @Override
    public RetResult provinceTAT4AchievementRate(Map map) {
        List<Map> m = regionDao.getMapDate();
        Map mp = new HashMap<>();
        for (Map p : m){
            mp.put(p.get("areaCode"),p.get("areaName"));
        }
        map.put("FIELDS","rdd.provincesCode");
        map.put("WHERES","rdd.accountingCenterCode = 'CS01' OR rdd.accountingCenterCode = 'CS02'");
        List<Map> indexList = regionDao.indexListT(map);
        indexList = indexList.stream().filter(a -> a.get("provincesCode")!=null&&!"".equals(a.get("provincesCode"))).collect(Collectors.toList());
        if(indexList!=null){
            DecimalFormat g2=new DecimalFormat("0.00000");
            for (Map map1:indexList) {
                String code = g2.format(Integer.valueOf((String) map1.get("provincesCode"))).replace(".","").substring(0,6);
                String provincesName = (String) mp.get(code);
                map1.put("provinceCode",code);
                map1.put("provinceName",provincesName);
                try{
                    BigDecimal fz =new BigDecimal(map1.get("averageEligible").toString()) ;
                    BigDecimal fm = new BigDecimal(map1.get("averageTotal").toString());
                    BigDecimal lv = fz.divide(fm,4,BigDecimal.ROUND_HALF_UP);
                    map1.put("dataValue",lv.toString());
                }catch (Exception e){
                    map1.put("dataValue","0.0000");
                }
                map1.put("provinceTat4AchievementAmount",map1.get("averageEligible"));
                map1.put("totalTat4AchievementAmount",map1.get("averageTotal"));
            }
        }

        return RetResponse.makeOKRsp(indexList);
    }

    @Override
    public RetResult thirtyMinuteAppointmentsRate(Map map) {
        map.put("FIELDS","LEFT(rdd.dispatchingTime,7) AS date");
        map.put("GROUPFIELDS","LEFT(rdd.dispatchingTime,7)");
        map.put("from","timelyPercentage");
        map.put("WHERES","rdd.accountingCenterCode = 'CS01' OR rdd.accountingCenterCode = 'CS02'");
        List<Map> indexList = regionDao.indexList(map);
        indexList = indexList.stream().filter(a -> a.get("date")!=null&&!"".equals(a.get("date"))).collect(Collectors.toList());
        if(indexList!=null){
            for (Map map1:indexList) {
                try{
                    BigDecimal fz =new BigDecimal(map1.get("timelyEligible").toString()) ;
                    BigDecimal fm = new BigDecimal(map1.get("smTotal").toString());
                    BigDecimal lv = fz.divide(fm,4,BigDecimal.ROUND_HALF_UP);
                    map1.put("dataValue",lv.toString());
                }catch (Exception e){
                    map1.put("dataValue","0.0000");
                }
                map1.put("monthThirtyMinuteAmount",map1.get("timelyEligible"));
                map1.put("totalThirtyMinuteAmount",map1.get("smTotal"));
            }
        }
        String startTime = (String) map.get("startTime");
        String endTime = (String) map.get("endTime");
        List<Map> list = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(startTime.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endTime.substring(0, 10));
        long between = ChronoUnit.MONTHS.between(startDate, endDate);
        if (between > 0) {
            Set<String> dateAll = Stream.iterate(startDate, e -> e.plusMonths(1))
                    .limit(between + 1)
                    .map(LocalDate::toString)
                    .collect(Collectors.toSet());
            //遍历日期，获取对应日期的数据，无则补0，并统计保存
            for(String e : dateAll){
                Optional<Map> i = indexList.stream().filter(item -> e.substring(0, 7).equals(item.get("date"))).findFirst();
                if(i.isPresent()){
                    list.add(i.get());
                }else {
                    Map map1 =new HashMap<>();
                    map1.put("date",e.substring(0, 7));
                    map1.put("dataValue","0.0000");
                    map1.put("monthThirtyMinuteAmount",0);
                    map1.put("totalThirtyMinuteAmount",0);
                    list.add(map1);
                }
            }
            list.sort(Comparator.comparing((Map m) -> (m.get("date").toString())));
        }else{
            list = indexList;
        }
        return RetResponse.makeOKRsp(list);
    }

    @Override
    public RetResult firstOnDoorAppointmentsRate(Map map) {
        map.put("FIELDS","LEFT(rdd.dispatchingTime,7) AS date");
        map.put("GROUPFIELDS","LEFT(rdd.dispatchingTime,7)");
        map.put("from","firstPunctualityPercentage");
        map.put("WHERES","rdd.accountingCenterCode = 'CS01' OR rdd.accountingCenterCode = 'CS02'");
        List<Map> indexList = regionDao.indexList(map);
        indexList = indexList.stream().filter(a -> a.get("date")!=null&&!"".equals(a.get("date"))).collect(Collectors.toList());
        if(indexList!=null){
            for (Map map1:indexList) {
                try{
                    BigDecimal fz =new BigDecimal(map1.get("firstPunctualityEligible").toString()) ;
                    BigDecimal fm = new BigDecimal(map1.get("smTotal").toString());
                    BigDecimal lv = fz.divide(fm,4,BigDecimal.ROUND_HALF_UP);
                    map1.put("dataValue",lv.toString());
                }catch (Exception e){
                    map1.put("dataValue","0.0000");
                }
                map1.put("monthFirstOnDoorAmount",map1.get("firstPunctualityEligible"));
                map1.put("totalFirstOnDoorAmount",map1.get("smTotal"));
            }
        }
        String startTime = (String) map.get("startTime");
        String endTime = (String) map.get("endTime");
        List<Map> list = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(startTime.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endTime.substring(0, 10));
        long between = ChronoUnit.MONTHS.between(startDate, endDate);
        if (between > 0) {
            Set<String> dateAll = Stream.iterate(startDate, e -> e.plusMonths(1))
                    .limit(between + 1)
                    .map(LocalDate::toString)
                    .collect(Collectors.toSet());
            //遍历日期，获取对应日期的数据，无则补0，并统计保存
            for(String e : dateAll){
                Optional<Map> i = indexList.stream().filter(item -> e.substring(0, 7).equals(item.get("date"))).findFirst();
                if(i.isPresent()){
                    list.add(i.get());
                }else {
                    Map map1 =new HashMap<>();
                    map1.put("date",e.substring(0, 7));
                    map1.put("dataValue","0.0000");
                    map1.put("monthFirstOnDoorAmount",0);
                    map1.put("totalFirstOnDoorAmount",0);
                    list.add(map1);
                }
            }
            list.sort(Comparator.comparing((Map m) -> (m.get("date").toString())));
        }else{
            list = indexList;
        }
        return RetResponse.makeOKRsp(list);
    }

    @Override
    public RetResult tatNServiceCompletionRate(Map map) {
        List<Map> indexList = regionDao.tatNServiceCompletionRate(map);
        String startTime = (String) map.get("startTime");
        String endTime = (String) map.get("endTime");
        List<Map> list = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(startTime.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endTime.substring(0, 10));
        long between = ChronoUnit.MONTHS.between(startDate, endDate);
        if (between > 0) {
            Set<String> dateAll = Stream.iterate(startDate, e -> e.plusMonths(1))
                    .limit(between + 1)
                    .map(LocalDate::toString)
                    .collect(Collectors.toSet());
            //遍历日期，获取对应日期的数据，无则补0，并统计保存
            for(String e : dateAll){
                Optional<Map> i = indexList.stream().filter(item -> e.substring(0, 7).equals(item.get("date"))).findFirst();
                if(i.isPresent()){
                    list.add(i.get());
                }else {
                    Map map1 =new HashMap<>();
                    map1.put("date",e.substring(0, 7));
                    map1.put("dataValue","0.0000");
                    map1.put("monthTatNServiceCompletionAmout",0);
                    map1.put("totalTatNServiceCompletionAmout",0);
                    list.add(map1);
                }
            }
            list.sort(Comparator.comparing((Map m) -> (m.get("date").toString())));
        }else{
            list = indexList;
        }
        return RetResponse.makeOKRsp(list);
    }

    @Override
    public RetResult nDaysComplaintHandleData(Map map) {
        Integer days = (Integer)map.get("days");
        List<Map> indexList = new ArrayList<>();
        if(days==1){
            indexList = regionDao.nDaysComplaintHandleData1(map);
        }else if(days==7){
            indexList = regionDao.nDaysComplaintHandleData7(map);
        }
        String startTime = (String) map.get("startTime");
        String endTime = (String) map.get("endTime");
        List<Map> list = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(startTime.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endTime.substring(0, 10));
        long between = ChronoUnit.MONTHS.between(startDate, endDate);
        if (between > 0) {
            Set<String> dateAll = Stream.iterate(startDate, e -> e.plusMonths(1))
                    .limit(between + 1)
                    .map(LocalDate::toString)
                    .collect(Collectors.toSet());
            //遍历日期，获取对应日期的数据，无则补0，并统计保存
            for(String e : dateAll){
                Optional<Map> i = indexList.stream().filter(item -> e.substring(0, 7).equals(item.get("date"))).findFirst();
                if(i.isPresent()){
                    list.add(i.get());
                }else {
                    Map map1 =new HashMap<>();
                    map1.put("date",e.substring(0, 7));
                    map1.put("dataValue","0.0000");
                    map1.put("totalComplaintAmount",0);
                    map1.put("monthComplaintAmount",0);
                    list.add(map1);
                }
            }
            list.sort(Comparator.comparing((Map m) -> (m.get("date").toString())));
        }else{
            list = indexList;
        }
        return RetResponse.makeOKRsp(list);
    }

    @Override
    public RetResult directManagementAreaScore(Map map) {
        try{
        map.put("FIELDS","rdd.accountingAreaCode,rdd.accountingArea,da.adminName");
        map.put("GROUPFIELDS","rdd.accountingAreaCode");
        map.put("complaintType","服务投诉");
        map.put("WHERES","rdd.accountingCenterCode = 'CS01' OR rdd.accountingCenterCode = 'CS02'");
        Map finalMap = map;
        Map finalMap1 = new HashMap<>(map);
        Future<List<Map>> submit1 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
            List<Map> list = regionDao.indexList(finalMap);
            list = list.stream().filter(a -> a.get("accountingAreaCode")!=null&&!"".equals(a.get("accountingAreaCode"))).collect(Collectors.toList());
            list.stream().forEach(a ->{
                a.put("average","");
                a.put("averagePercentage","");
                a.put("avgDay","");
                a.put("solvePercentage","");
            });
            return list;
        });
        Future<List<Map>> submit2 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
            List<Map> list = regionDao.indexListT(finalMap);
            list = list.stream().filter(a -> a.get("accountingAreaCode")!=null&&!"".equals(a.get("accountingAreaCode"))).collect(Collectors.toList());
            list.stream().forEach(a ->{
                a.put("timelyPercentage","");
                a.put("firstPunctualityPercentage","");
                a.put("solvePercentage","");
                a.put("repairPercentage","");
            });
            return list;
        });
        Future<List<Map>> submit3 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
            finalMap1.put("FIELDS","ch.accountingAreaCode,ch.accountingArea,da.adminName");
            finalMap1.put("GROUPFIELDS","rdd.accountingAreaCode");
            finalMap1.put("WHERES","ch.accountingCenterCode = 'CS01' OR ch.accountingCenterCode = 'CS02'");
            List<Map> list = regionDao.indexListS(finalMap1);
            list = list.stream().filter(a -> a.get("accountingAreaCode")!=null&&!"".equals(a.get("accountingAreaCode"))).collect(Collectors.toList());
            list.stream().forEach(a ->{
                a.put("timelyPercentage","");
                a.put("firstPunctualityPercentage","");
                a.put("repairPercentage","");
                a.put("average","");
                a.put("averagePercentage","");
                a.put("avgDay","");
            });
            return list;
        });
        List<Map> indexList = submit1.get();
        List<Map> indexListT = submit2.get();
        List<Map> indexListS = submit3.get();
        List<Map> finalIndexList = indexList;
        indexList = indexListT.stream().map(a -> finalIndexList.stream()
                        .filter(b -> b.get("accountingAreaCode").toString().equals(a.get("accountingAreaCode")))
                        .findFirst().map(b -> {
                            b.put("average",a.get("average"));
                            b.put("averagePercentage",a.get("averagePercentage"));
                            b.put("avgDay",a.get("avgDay"));
                            b.put("averageEligible",a.get("averageEligible"));
                            b.put("averageTotal",a.get("averageTotal"));
                            b.put("tt",1);
                            return b;
                        }).orElse(a))
                .filter(Objects::nonNull).collect(Collectors.toList());
        List<Map> a = finalIndexList.stream().filter(m->m.get("tt")==null).collect(Collectors.toList());
        indexList.addAll(a);
        if(CollectionUtils.isNotEmpty(indexListS)) {
            for(Map c:indexListS){
                Optional<Map> i = indexList.stream().filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode"))).findFirst();
                if(i.isPresent()){
                    Map b = i.get();
                    b.put("solveEligible", c.get("solveEligible"));
                    b.put("solveTotal", c.get("solveTotal"));
                    b.put("solvePercentage", c.get("solvePercentage"));
                }else {
                    indexList.add(c);
                }
            }
        }
        for(Map m:indexList){
            m.put("accountingAreaName",m.get("accountingArea"));
            m.put("accountingAreaManagerName",m.get("adminName"));
            if(m.get("timelyEligible")==null||m.get("smTotal")==null||"0".equals(m.get("timelyEligible"))||"0".equals(m.get("smTotal"))){
                m.put("thirtyMinuteAppointmentsRate","0.0000");
            }else {
                BigDecimal fz =new BigDecimal(m.get("timelyEligible").toString()) ;
                BigDecimal fm = new BigDecimal(m.get("smTotal").toString());
                BigDecimal lv = fz.divide(fm,4,BigDecimal.ROUND_HALF_UP);
                m.put("thirtyMinuteAppointmentsRate",lv.toString());
            }
            if(m.get("firstPunctualityEligible")==null||m.get("smTotal")==null||"0".equals(m.get("firstPunctualityEligible"))||"0".equals(m.get("smTotal"))){
                m.put("firstOnDoorAppointmentsRate","0.0000");
            }else {
                BigDecimal fz =new BigDecimal(m.get("firstPunctualityEligible").toString()) ;
                BigDecimal fm = new BigDecimal(m.get("smTotal").toString());
                BigDecimal lv = fz.divide(fm,4,BigDecimal.ROUND_HALF_UP);
                m.put("firstOnDoorAppointmentsRate",lv.toString());
            }
            if(m.get("averageEligible")==null||m.get("averageTotal")==null||(Double) m.get("averageEligible")==0.0||"0".equals(m.get("averageTotal"))){
                m.put("tat4AchievementRate","0.0000");
            }else {
                BigDecimal fz =new BigDecimal(m.get("averageEligible").toString()) ;
                BigDecimal fm = new BigDecimal(m.get("averageTotal").toString());
                BigDecimal lv = fz.divide(fm,4,BigDecimal.ROUND_HALF_UP);
                m.put("tat4AchievementRate",lv.toString());
            }
            if(m.get("solveEligible")==null||m.get("solveTotal")==null||"0".equals(m.get("solveEligible"))||"0".equals(m.get("solveTotal"))){
                m.put("sevenDaysComplaintRate","0.0000");
            }else {
                BigDecimal fz =new BigDecimal(m.get("solveEligible").toString()) ;
                BigDecimal fm = new BigDecimal(m.get("solveTotal").toString());
                BigDecimal lv = null;
                try {
                    lv = fz.divide(fm,4, BigDecimal.ROUND_HALF_UP);
                } catch (Exception e) {
                    e.printStackTrace();
                    lv = new BigDecimal(0);
                }
                m.put("sevenDaysComplaintRate",lv.toString());
            }

        }
        return RetResponse.makeOKRsp(indexList);
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }

    @Override
    public List<Map> exportEngineerList(Map map) {
        try {
            map = dateConversion(map);
            map.put("FIELDS","rss.accountingCenter,rdd.accountingAreaCode,rdd.accountingArea,da.adminName,COUNT(1) AS amount");
            map.put("GROUPFIELDS","rdd.accountingAreaCode");
            Map finalMap = map;
            Future<List<Map>> submit1 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list = regionDao.indexList(finalMap);
                list = list.stream().filter(a -> a.get("accountingArea")!=null&&!"".equals(a.get("accountingArea"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("average","");
                    a.put("averagePercentage","");
                    a.put("avgDay","");
                    a.put("solvePercentage","");
                });
                return list;
            });
            Future<List<Map>> submit2 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list = regionDao.indexListT(finalMap);
                list = list.stream().filter(a -> a.get("accountingArea")!=null&&!"".equals(a.get("accountingArea"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("timelyPercentage","");
                    a.put("firstPunctualityPercentage","");
                    a.put("solvePercentage","");
                    a.put("repairPercentage","");
                });
                return list;
            });
            Future<List<Map>> submit3 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                Map finalMap1 = new HashMap<>(finalMap);
                finalMap1.put("FIELDS","rss.accountingCenter,ch.accountingAreaCode,ch.accountingArea,da.adminName,COUNT(1) AS amount");
                finalMap1.put("GROUPFIELDS","ch.accountingAreaCode");
                List<Map> list = regionDao.indexListS(finalMap1);
                list = list.stream().filter(a -> a.get("accountingArea")!=null&&!"".equals(a.get("accountingArea"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("timelyPercentage","");
                    a.put("firstPunctualityPercentage","");
                    a.put("repairPercentage","");
                    a.put("average","");
                    a.put("averagePercentage","");
                    a.put("avgDay","");
                });
                return list;
            });
            List<Map> exportList = submit1.get();
            List<Map> indexListT = submit2.get();
            List<Map> indexListS = submit3.get();
            List<Map> finalIndexList = exportList;
            exportList = indexListT.stream().map(a -> finalIndexList.stream()
                            .filter(b -> b.get("accountingAreaCode").toString().equals(a.get("accountingAreaCode")))
                            .findFirst().map(b -> {
                                b.put("average",a.get("average"));
                                b.put("averagePercentage",a.get("averagePercentage"));
                                b.put("avgDay",a.get("avgDay"));
                                b.put("tt",1);
                                return b;
                            }).orElse(a))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            List<Map> a = finalIndexList.stream().filter(m->m.get("tt")==null).collect(Collectors.toList());
            exportList.addAll(a);
            if(CollectionUtils.isNotEmpty(indexListS)) {
                for(Map c:indexListS){
                    Optional<Map> i = exportList.stream().filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode"))).findFirst();
                    if(i.isPresent()){
                        Map b = i.get();
                        b.put("solveEligible", c.get("solveEligible"));
                        b.put("solveTotal", c.get("solveTotal"));
                        b.put("solvePercentage", c.get("solvePercentage"));
                    }else {
                        exportList.add(c);
                    }
                }
            }
            for (Map newMap : exportList) {
                if("".equals(newMap.get("timelyPercentage"))){
                    newMap.put("timelyPercentage","-");
                }
                if("".equals(newMap.get("firstPunctualityPercentage"))){
                    newMap.put("firstPunctualityPercentage","-");
                }
                if("".equals(newMap.get("averagePercentage"))){
                    newMap.put("averagePercentage","-");
                }
                if("".equals(newMap.get("avgDay"))){
                    newMap.put("avgDay","-");
                }
                if("".equals(newMap.get("solvePercentage"))){
                    newMap.put("solvePercentage","-");
                }
                if("".equals(newMap.get("repairPercentage"))){
                    newMap.put("repairPercentage","-");
                }
                newMap.put("totalScore",new BigDecimal(String.valueOf(0.25*Double.parseDouble("-".equals(newMap.get("timelyPercentage")) || newMap.get("timelyPercentage")==null ? "100" :newMap.get("timelyPercentage").toString())
                +0.25*Double.parseDouble("-".equals(newMap.get("firstPunctualityPercentage")) || newMap.get("firstPunctualityPercentage")==null ? "100" :newMap.get("firstPunctualityPercentage").toString())
                        +0.25*Double.parseDouble("-".equals(newMap.get("averagePercentage")) || newMap.get("averagePercentage")==null ? "100" :newMap.get("averagePercentage").toString())
                        +0.25*Double.parseDouble("-".equals(newMap.get("solvePercentage")) || newMap.get("solvePercentage")==null ? "100" :newMap.get("solvePercentage").toString())))
                        .setScale(2, RoundingMode.HALF_UP));
            }
            return exportList;
        }catch (Exception e){
            RetResponse.makeErrRsp(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RetResult regionAll() {
    try{
        regionDao.dispatchingRegionAll();
        regionDao.complaintRegionAll();
        return RetResponse.makeOKRsp();
    }catch (Exception e){
        e.printStackTrace();
        return RetResponse.makeErrRsp(e.getMessage());
    }
    }

    @Override
    public RetResult error()throws Exception {
        List<String> ids = regionDao.errorList();
        if(ids!=null&&ids.size()>0){
            for(String id:ids){
                try {
                    String r = newDispatching(id);
                    //输出乱序执行结果
                    System.out.println("乱序处理：" + id + "结果：" + r);
                }catch (Exception e){
                    System.out.println("乱序处理：" + id + "结果：" + e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    public RetResult totalRanking(Map map) {
        List<Map> returnList = new ArrayList<>();
        schemeToCondition(map);
        //指标类型：总分、预约、上门、TAT、一次、投诉、N+1
        String targetType = (String) map.get("targetType");
        //范围类型：大区、服务店、工程师
        String scopeType = (String) map.get("scopeType");

        //排序
        String sortType = (String) map.get("sortType");
        String key = "total";
        Map sort = new HashMap<>();
        sort.put("sortHandle",sortType);
        switch (targetType){
            case "预约":
                key = "timelyPercentage";
                break;
            case "上门":
                key = "firstPunctualityPercentage";
                break;
            case "TAT":
                key = "averagePercentage";
                break;
            case "一次":
                key = "repairPercentage";
                break;
            case "投诉":
                key = "solvePercentage";
                break;
            case "N+1":
                key = "schemePercentage";
                break;
        }
        sort.put("sortName",key);
        map.put("sort",sort);

        
        try {
            /**
             * 查询数据
             */
            String group = "accountingArea";
            if("服务店".equals(scopeType)){
                group = "storeName";
                map.put("FIELDS","rdd.storeNumber,rdd.storeName,rdd.accountingCenterCode,rdd.accountingArea,rdd.accountingAreaCode,reg.region");
                map.put("TABLES","LEFT JOIN (SELECT rdd3.storeNumber,CONCAT(rdd3.provinces,rdd3.city)AS region FROM t_region_service_store rdd3)reg ON reg.storeNumber = rss.storeNumber");
                map.put("GROUPFIELDS","rdd.storeNumber,rdd.accountingCenterCode");
            } else if ("工程师".equals(scopeType)) {
                group = "engineerName";
                map.put("FIELDS","rdd.engineerId,rdd.engineerName,rdd.accountingAreaCode,rdd.accountingArea,rdd.storeName,rdd.storeNumber,rdd.accountingCenterCode,COUNT(1) AS amount");
                map.put("GROUPFIELDS","rdd.engineerId,rdd.accountingAreaCode,rdd.storeNumber");
            }else {
                //装载查询字段
                map.put("FIELDS","rss.accountingCenter,rdd.accountingAreaCode,rdd.accountingArea,da.adminName,COUNT(1) AS amount");
                //装载分组字段
                map.put("GROUPFIELDS","rdd.accountingAreaCode");
            }
            //多线程查询
            Map finalMap = map;
            //查询30分钟预约及时率、首次预约准时上门率、预约准时上门率、一次修复率
            String finalGroup = group;
            Future<List<Map>> submit1 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list = regionDao.indexList(finalMap);
                list = list.stream().filter(a -> a.get(finalGroup)!=null&&!"".equals(a.get(finalGroup))).collect(Collectors.toList());
                //追加平均时长、TAT、投诉7天解决率字段
                list.stream().forEach(a ->{
                    a.put("average","");
                    a.put("averagePercentage","");
                    a.put("avgDay","");
                    a.put("solvePercentage","");
                    a.put("schemePercentage","");
                });
                return list;
            });
            //查询平均时长、TAT
            Future<List<Map>> submit2 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list = regionDao.indexListT(finalMap);
                list = list.stream().filter(a -> a.get(finalGroup)!=null&&!"".equals(a.get(finalGroup))).collect(Collectors.toList());
                //追加30分钟预约及时率、首次预约准时上门率、预约准时上门率、一次修复率、投诉7天解决率字段
                list.stream().forEach(a ->{
                    a.put("timelyPercentage","");
                    a.put("firstPunctualityPercentage","");
                    a.put("solvePercentage","");
                    a.put("schemePercentage","");
                    a.put("repairPercentage","");
                });
                return list;
            });
            //查询投诉7天解决率
            Future<List<Map>> submit3 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                //投诉7天解决率主表不一样需要替换查询字段，防止多线程不安全，复制条件
                Map finalMap1 = new HashMap<>(finalMap);
                if("服务店".equals(scopeType)){
                    finalMap1.put("FIELDS","ch.storeNumber,ch.storeName,ch.accountingCenterCode,ch.accountingArea,ch.accountingAreaCode,reg.region");
                    finalMap1.put("TABLES","LEFT JOIN (SELECT rdd3.complaintNumber,CONCAT(rdd3.provinces,rdd3.city)AS region FROM t_complaint_handling rdd3)reg ON reg.complaintNumber = ch.complaintNumber");
                    finalMap1.put("GROUPFIELDS","ch.storeNumber,ch.accountingCenterCode");
                } else if ("工程师".equals(scopeType)) {
                    finalMap1.put("FIELDS","rdd.engineerId,rdd.engineerName,ch.accountingAreaCode,ch.accountingArea,ch.storeName,ch.storeNumber,ch.accountingCenterCode,COUNT(1) AS amount");
                    finalMap1.put("GROUPFIELDS","rdd.engineerId,ch.accountingAreaCode,ch.storeNumber");
                }else {
                    finalMap1.put("FIELDS","rss.accountingCenter,ch.accountingAreaCode,ch.accountingArea,da.adminName,COUNT(1) AS amount");
                    finalMap1.put("GROUPFIELDS","ch.accountingAreaCode");
                }

                List<Map> list = regionDao.indexListS(finalMap1);
                list = list.stream().filter(a -> a.get(finalGroup)!=null&&!"".equals(a.get(finalGroup))).collect(Collectors.toList());
                //追加30分钟预约及时率、首次预约准时上门率、预约准时上门率、一次修复率、平均时长、TAT
                list.stream().forEach(a ->{
                    a.put("timelyPercentage","");
                    a.put("firstPunctualityPercentage","");
                    a.put("repairPercentage","");
                    a.put("average","");
                    a.put("averagePercentage","");
                    a.put("avgDay","");
                });
                return list;
            });
            List<Map> indexList = submit1.get();
            List<Map> indexListT = submit2.get();
            List<Map> indexListS = submit3.get();
            List<Map> finalIndexList = indexList;
            /**
             * 合并数据
             */
            if("服务店".equals(scopeType)){
                indexList = indexListT.stream().map(a -> finalIndexList.stream()
                                .filter(b -> b.get("accountingAreaCode").toString().equals(a.get("accountingAreaCode"))&&b.get("storeNumber").toString().equals(a.get("storeNumber")))
                                .findFirst().map(b -> {
                                    b.put("average",a.get("average"));
                                    b.put("averagePercentage",a.get("averagePercentage"));
                                    b.put("avgDay",a.get("avgDay"));
                                    b.put("tt",1);
                                    return b;
                                }).orElse(a))
                        .filter(Objects::nonNull).collect(Collectors.toList());
                List<Map> a = finalIndexList.stream().filter(m->m.get("tt")==null).collect(Collectors.toList());
                indexList.addAll(a);
                if(CollectionUtils.isNotEmpty(indexListS)) {
                    for(Map c:indexListS){
                        Optional<Map> i = indexList.stream().filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode"))&&b.get("storeNumber").toString().equals(c.get("storeNumber"))).findFirst();
                        if(i.isPresent()){
                            Map b = i.get();
                            b.put("solveEligible", c.get("solveEligible"));
                            b.put("solveTotal", c.get("solveTotal"));
                            b.put("solvePercentage", c.get("solvePercentage"));
                        }else {
                            indexList.add(c);
                        }
                    }
                }
            } else if ("工程师".equals(scopeType)) {
                indexList = indexListT.stream().map(a -> finalIndexList.stream()
                                .filter(b -> b.get("accountingAreaCode").toString().equals(a.get("accountingAreaCode").toString())&&b.get("storeNumber").toString().equals(a.get("storeNumber").toString())&&b.get("engineerId").toString().equals(a.get("engineerId").toString()))
                                .findFirst().map(b -> {
                                    b.put("average",a.get("average"));
                                    b.put("averagePercentage",a.get("averagePercentage"));
                                    b.put("avgDay",a.get("avgDay"));
                                    b.put("tt",1);
                                    return b;
                                }).orElse(a))
                        .filter(Objects::nonNull).collect(Collectors.toList());
                List<Map> a = finalIndexList.stream().filter(m->m.get("tt")==null).collect(Collectors.toList());
                indexList.addAll(a);
                if(CollectionUtils.isNotEmpty(indexListS)) {
                    for(Map c:indexListS){
                        Optional<Map> i = indexList.stream().filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode").toString()) && b.get("storeNumber").toString().equals(c.get("storeNumber").toString()) && b.get("engineerId").toString().equals(c.get("engineerId").toString())).findFirst();
                        if(i.isPresent()){
                            Map b = i.get();
                            b.put("solveEligible", c.get("solveEligible"));
                            b.put("solveTotal", c.get("solveTotal"));
                            b.put("solvePercentage", c.get("solvePercentage"));
                        }else {
                            indexList.add(c);
                        }
                    }
                }
            }else {
                indexList = indexListT.stream().map(a -> finalIndexList.stream()
                                .filter(b -> b.get("accountingAreaCode").toString().equals(a.get("accountingAreaCode")))
                                .findFirst().map(b -> {
                                    b.put("average",a.get("average"));
                                    b.put("averagePercentage",a.get("averagePercentage"));
                                    b.put("avgDay",a.get("avgDay"));
                                    b.put("tt",1);
                                    return b;
                                }).orElse(a))
                        .filter(Objects::nonNull).collect(Collectors.toList());
                List<Map> a = finalIndexList.stream().filter(m->m.get("tt")==null).collect(Collectors.toList());
                indexList.addAll(a);
                if(CollectionUtils.isNotEmpty(indexListS)) {
                    for(Map c:indexListS){
                        Optional<Map> i = indexList.stream().filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode"))).findFirst();
                        if(i.isPresent()){
                            Map b = i.get();
                            b.put("solveEligible", c.get("solveEligible"));
                            b.put("solveTotal", c.get("solveTotal"));
                            b.put("solvePercentage", c.get("solvePercentage"));
                        }else {
                            indexList.add(c);
                        }
                    }
                }
            }

            String finalKey = key;
            /**
             * 处理数据
             */
            //总分计算总分
            if("total".equals(key)){
                //计算总分
                indexList.stream().forEach(item ->{
                    BigDecimal divide = new BigDecimal("4");
                    BigDecimal timelyPercentage = new BigDecimal(item.get("timelyPercentage")==null||item.get("timelyPercentage").equals("")?"0":item.get("timelyPercentage").toString());
                    BigDecimal firstPunctualityPercentage = new BigDecimal(item.get("firstPunctualityPercentage")==null||item.get("firstPunctualityPercentage").equals("")?"0":item.get("firstPunctualityPercentage").toString());
                    BigDecimal averagePercentage = new BigDecimal(item.get("averagePercentage")==null||item.get("averagePercentage").equals("")?"0":item.get("averagePercentage").toString());
                    BigDecimal solvePercentage = new BigDecimal(item.get("solvePercentage")==null||item.get("solvePercentage").equals("")?"0":item.get("solvePercentage").toString());
                    timelyPercentage = timelyPercentage.divide(divide,2,BigDecimal.ROUND_HALF_UP);
                    firstPunctualityPercentage = firstPunctualityPercentage.divide(divide,2,BigDecimal.ROUND_HALF_UP);
                    averagePercentage = averagePercentage.divide(divide,2,BigDecimal.ROUND_HALF_UP);
                    solvePercentage = solvePercentage.divide(divide,2,BigDecimal.ROUND_HALF_UP);
                    item.put("total",timelyPercentage.add(firstPunctualityPercentage).add(averagePercentage).add(solvePercentage).toString());
                });
            }else {
                //其他标签取其他值
                indexList.stream().forEach(item ->{
                    item.put("ratio",item.get(finalKey));
                });
            }

            //非大区取前20，后20
            if("total".equals(key)){
                indexList.sort(Comparator.comparing((Map m) -> (Double.parseDouble(m.get(finalKey)==null || "".equals(m.get(finalKey))?"-1":m.get(finalKey).toString()))).reversed());
                for(int i = 0 ;i<indexList.size();i++){
                    indexList.get(i).put("ranking",i+1);
                }
                //过滤筛选条件
                indexList = fieldQuery(map,indexList);
            }else {
                indexList = indexList.stream().filter(a -> a.get(finalKey)!=null&&!"".equals(a.get(finalKey))&&!"-".equals(a.get(finalKey))).collect(Collectors.toList());
                //过滤筛选条件
                indexList = fieldQuery(map,indexList);
                int s = indexList.size();
                int end = 20;
                if(end>indexList.size()){
                    end = indexList.size();
                }
                for(int i=0;i<end;i++){
                    if(sortType.equals("desc")){
                        indexList.get(i).put("ranking",i+1);
                    }else{
                        indexList.get(i).put("ranking",s-i);
                    }
                }
                indexList = indexList.subList(0, end);
            }
            listToList(indexList,map);
            returnList = indexList;

        }catch (Exception e){
            RetResponse.makeErrRsp(e.getMessage());
            e.printStackTrace();
        }
        return RetResponse.makeOKRsp(returnList);
    }

    @Override
    public RetResult target(Map map) {
        /**
         * 初始化数据
         */
        schemeToCondition(map);
        //指标类型：预约、上门、TAT、一次、投诉、N+1
        String targetType = (String) map.get("targetType");
        String punctualityType = (String) map.get("punctualityType");
        BigDecimal bfb = new BigDecimal(100);
        Map rate = new HashMap<>();

        /**
         * 查询数据
         */
        switch (targetType){
            case "预约":
                rate = regionDao.timely(map);
                //写入目标
                rate.put("target", getIndicator(map,"timelyRatw"));
                break;
            case "上门":
                if("首次预约".equals(punctualityType)){
                    rate = regionDao.fristPunctuality(map);
                    //写入目标
                    rate.put("target", getIndicator(map,"firstPunctualRate"));
                }else {
                    rate = regionDao.punctuality(map);
                    //写入目标
                    rate.put("target", getIndicator(map,"secondPunctualRate"));
                }
                break;
            case "TAT":
                rate = regionDao.average(map);
                rate.put("avgDay",hoursToDay(rate.get("average")));
                //写入目标
                rate.put("target", getIndicator(map,"avgTimeRate"));
                String AvgTimeRate =  getIndicator(map,"avgTimeDay");
                rate.put("target3", AvgTimeRate);
                rate.put("target2", hoursToDay(AvgTimeRate));
                break;
            case "一次":
                rate = regionDao.repair(map);
                //写入目标
                rate.put("target", getIndicator(map,"repairRate"));
                break;
            case "投诉":
                rate = regionDao.solve(map);
                //写入目标
                rate.put("target", getIndicator(map,"resolutionRate"));
                break;
            case "N+1":
                rate = regionDao.scheme(map);
                //写入目标
                rate.put("target", getIndicator(map,"subRate"));
                break;
        }
        //通过分子分母计算率
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
            rate.put("rate","0");
        }else{
            BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
            BigDecimal last = new BigDecimal( rate.get("total").toString());
            rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
        }

        //修改本月日期为上月日期
        map = lastMonth(map);
        //统计上月率
        Map lastRate = new HashMap<>();
        switch (targetType){
            case "预约":
                lastRate = regionDao.timely(map);
                break;
            case "上门":
                if("首次预约".equals(punctualityType)){
                    lastRate = regionDao.fristPunctuality(map);
                }else {
                    lastRate = regionDao.punctuality(map);
                }
                break;
            case "TAT":
                lastRate = regionDao.average(map);
                break;
            case "一次":
                lastRate = regionDao.repair(map);
                break;
            case "投诉":
                lastRate = regionDao.solve(map);
                break;
            case "N+1":
                lastRate = regionDao.scheme(map);
                break;
        }
        try{
            //上月分子
            BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
            //上月分母
            BigDecimal last = new BigDecimal(lastRate.get("total").toString());
            //本月率
            BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
            //计算上月率
            BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
            //计算同比上月
            BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
            rate.put("QOQ", QOQ.toString());
        }catch (Exception e){
            rate.put("QOQ", "0");
        }
        return RetResponse.makeOKRsp(rate);
    }

    @Override
    public RetResult lineChart(Map map) {
        /**
         * 初始化数据
         */
        schemeToCondition(map);
        //指标类型：预约、上门、TAT、一次、投诉、N+1
        String targetType = (String) map.get("targetType");
        String punctualityType = (String) map.get("punctualityType");
        List<Map> dateRate = new ArrayList<>();
        switch (targetType){
            case "预约":
                dateRate =  regionDao.dateTimely(map);
                break;
            case "上门":
                if("首次预约".equals(punctualityType)){
                    dateRate =  regionDao.fristDatePunctuality(map);
                }else {
                    dateRate =  regionDao.datePunctuality(map);
                }
                break;
            case "TAT":
                dateRate =  regionDao.dateAverage(map);
                break;
            case "一次":
                dateRate =  regionDao.dateRepair(map);
                break;
            case "投诉":
                dateRate =  regionDao.dateSolve(map);
                break;
            case "N+1":
                dateRate =  regionDao.dateScheme(map);
                break;
        }
        //给没有数据的日期补零并排序
        dateRate = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateRate);
        return RetResponse.makeOKRsp(dateRate);
    }

    @Transactional(rollbackFor = {Exception.class})
    public String newDispatching(String id)throws Exception{
        Map par = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        par.put("associatedNumber",id);
        List<Map> transfer = regionDao.transferInformation(par);
        if(transfer!=null&&transfer.size()>0){
            regionDao.dispatchingDel(id);
            boolean ok = true;
            for(int i=(transfer.size()-1);i>=0;i--){
                Map tra = transfer.get(i);
                // json字符串转JSONObject对象
                JSONObject jsonObject = JSONObject.parseObject((String) tra.get("relatedData"));
                Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
                HashMap<String, Object> map = new HashMap<>();
                while (iterator.hasNext()){
                    Map.Entry<String, Object> next = iterator.next();
                    map.put(next.getKey(), next.getValue().toString());
                }
                //动作
                String action = "";
                if (map.get("action") != null) {
                    action = (String) map.get("action");
                } else {
                    ok = false;
                    System.out.println("动作不能为空！");
                    break;
                }
                //派工单号
                String dispatchingOrder = "";
                if (map.get("dispatchingOrder") != null) {
                    dispatchingOrder = (String) map.get("dispatchingOrder");
                } else {
                    ok = false;
                    System.out.println("派工单号不能为空！");
                    break;
                }
                //根据派工单号查询本地数据
                Map dispatching = regionDao.serviceDetailById(dispatchingOrder);
                //业务时间
                String serviceTime = map.get("serviceTime") != null ? (String) map.get("serviceTime") : null;
                //初始服务类型判断
                if(map.get("serviceType")!=null){
                    if("维修".equals(map.get("serviceType"))||"安装".equals(map.get("serviceType"))||"非上门维修".equals(map.get("serviceType"))){
                        if(dispatching==null||dispatching.get("firstServiceType")==null){
                            map.put("firstServiceType",map.get("serviceType"));
                        }
                    }
                }
                //判断动作类型
                if ("工单创建".equals(action)) {
                    if (dispatching != null) {
                        return "已存在该派工单号！";
                    }
                    //工单创建没有serviceTime参数，以派工时间代替
                    //transfer.put("operationTime", (String) map.get("dispatchingTime"));
                    //新增派工单信息
                    regionDao.serviceDetailAdd(map);
                }else{
                    //非工单创建推送数据，先拉取派工单信息保存再取执行推送的内容
                    if (dispatching == null) {
                        ok = false;
                        System.out.println("找不到派工单！");
                        break;
                    }
                    if ("区域确认".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成考核开始时间
                        map.put("startTimeOfAssessment", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("确认转移".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成考核开始时间
                        map.put("startTimeOfAssessment", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("寄件签收".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成考核开始时间和寄件门店签收时间
                        map.put("startTimeOfAssessment", serviceTime);
                        map.put("shopDeliveryTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("接收".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成接收时间
                        map.put("receiptTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("预约".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成预约操作时间
                        map.put("appointmentOperationTime", serviceTime);
                        //预约时间和预约时间段同步首次预约时间和首次预约时间段
                        map.put("firstAppointmentTime", map.get("appointmentTime"));
                        map.put("firstAppointmentTimeSection", map.get("appointmentTimeSection"));
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("到达现场".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成上门时间
                        map.put("visitTime", serviceTime);
                        //判断是否有首次上门时间，如果没有则同步上门时间和首次上门时间
                        if (dispatching.get("firstVisitTime") == null) {
                            map.put("firstVisitTime", serviceTime);
                        }
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("还件签收".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成还件签收时间
                        map.put("customerDeliveryTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("服务完成".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成完成时间
                        map.put("finishTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);

                        //完成的派工单拉取派工单详情信息，更新整个派工单及相关子表信息
                        //设置调用接口参数
                        Map map1 = new HashMap<>();
                        map1.put("dispatchingOrder",dispatchingOrder);
                        //调用派工单详情拉取接口
                        RetResult rr = dispatchingDetailApi(map1);
                        if(rr.getCode()==200){
                            //成功获取派工单详情
                            Map dispatchingDetail = (Map) rr.getData();
                            if(dispatchingDetail!=null){
                                //更新派工单信息
                                regionDao.serviceDetailEdit(dispatchingDetail);
                                //提取调换部件信息并批量更新至调换部件信息表中
                                List<Map> replacePart = (List<Map>) dispatchingDetail.get("replacePart");
                                if(replacePart!=null&&replacePart.size()>0){
                                    regionDao.replaceAdds(replacePart,dispatchingOrder);
                                }
                                //提取维修部件信息并批量更新至维修部件信息表中
                                List<Map> repairPart = (List<Map>) dispatchingDetail.get("repairPart");
                                if(repairPart!=null&&repairPart.size()>0){
                                    regionDao.repairAdds(repairPart,dispatchingOrder);
                                }
                            }
                        }
                    } else if ("还件确认".equals(action)) {
                        if (dispatching == null) {
                            ok=false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成门店还件时间
                        map.put("shopReturnTime", serviceTime);
                        //将业务时间转换成完成时间
                        map.put("finishTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("以换代修".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成以换代修提交时间和完成时间
                        map.put("exchangeMaintain", serviceTime);
                        map.put("finishTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("作废申请".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    }else if ("拒绝作废".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    }else if ("确认作废".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成作废时间
                        map.put("cancellationTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("改派".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成改派时间
                        map.put("modificationDispatchTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("改约".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成改约时间
                        map.put("modificationAppointmentTime", serviceTime);
                        //获取现有改约次数并+1，如果为空则0+1
                        Integer rescheduleFrequency = 0;
                        if (dispatching.get("rescheduleFrequency") != null && "".equals(dispatching.get("rescheduleFrequency"))) {
                            rescheduleFrequency = Integer.parseInt((String) dispatching.get("rescheduleFrequency"));
                        }
                        rescheduleFrequency++;
                        map.put("rescheduleFrequency", rescheduleFrequency);
                        map.put("firstPunctualityEligible", "0");
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("服务类型变更".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        map.put("fwlxbg","t");
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("派单".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成派工操作时间
                        map.put("dispatchingOperationTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("提交".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成提交时间
                        map.put("submissionTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("还件揽收".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //将业务时间转换成还件揽收时间
                        map.put("returnCollectionTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    } else if ("核算状态变更".equals(action)) {
                        if (dispatching == null) {
                            ok = false;
                            System.out.println("找不到派工单！");
                            break;
                        }
                        //根据核算状态分别将时间写入对应状态的时间
                        if("已结算".equals(map.get("calculationState"))){
                            map.put("settledTime", serviceTime);//已结算时间
                        }else if("不结算".equals(map.get("calculationState"))){
                            map.put("noSettledTime", serviceTime);//不结算时间
                        }else if("未结算".equals(map.get("calculationState"))){
                            map.put("notSettledTime", serviceTime);//未结算时间
                        }
                        //将业务时间转换成核算时间
                        map.put("calculationTime", serviceTime);
                        //修改派工单信息
                        regionDao.serviceDetailEdit(map);
                    }else if ("挂单".equals(action)) {
                        List<Map> pendingList = regionDao.pendingOrderList(map);
                        Map map1 = new HashMap<>();
                        map1.put("dispatchingOrder",dispatchingOrder);
                        //判断如果有未接挂记录，不能新增新的挂单
                        if (pendingList == null || pendingList.size() == 0 || pendingList.get(0).get("finishOrderTime") != null) {
                            if(pendingList!=null&&pendingList.size()>0){
                                String t1 = (String) pendingList.get(0).get("finishOrderTime");
                                String t2 = (String) map.get("serviceTime");
                                Date start = sdf.parse(t1);
                                Date end = sdf.parse(t2);
                                if(start.getTime()>end.getTime()){
                                    ok = false;
                                    System.out.println("挂单时间早于上次解挂时间！");
                                    break;
                                }
                            }

                            //挂单新增挂单记录
                            regionDao.pendingOrderAdd(map);
                            map1.put("pendingState","挂单");
                            try {
                                //修改派工单挂单状态为派工状态
                                regionDao.serviceDetailEdit(map1);
                            }catch (Exception e){
                                e.printStackTrace();
                                ok = false;
                                System.out.println("找不到派工单！");
                                break;
                            }

                        }else{
                            ok = false;
                            System.out.println("有未解挂记录！");
                            break;
                        }
                    } else if ("解挂".equals(action)) {
                        List<Map> pendingList = regionDao.pendingOrderList(map);
                        Map map1 = new HashMap<>();
                        map1.put("dispatchingOrder",dispatchingOrder);
                        //判断最近一条记录的解挂时间是否为空，不为空就没有挂单
                        if (pendingList == null || pendingList.size() == 0 || (pendingList.get(0).get("finishOrderTime") != null&&!"".equals(pendingList.get(0).get("finishOrderTime")))) {
                            ok = false;
                            System.out.println("没有需要解挂信息！");
                            break;
                        }
                        //因为查询倒序排序，第一条记录为最近的时间，获取id并修改该条记录的解挂时间
                        map.put("pendingId", pendingList.get(0).get("pendingId"));

                        //解挂时间要在挂单时间之后
                        String t1 = (String) pendingList.get(0).get("pendingOrderTime");
                        String t2 = (String) map.get("serviceTime");
                        Date start = sdf.parse(t1);
                        Date end = sdf.parse(t2);
                        if(start.getTime()>end.getTime()){
                            return "解挂时间早于挂单时间！";
                        }
                        regionDao.pendingOrderEdit(map);


                        //修改派工单主表挂单状态为解挂
                        if(dispatching!=null){
                            Integer pendingTime = 0;
                            //Integer pendingTime = (Integer) dis.get("pendingTime");
                            if(dispatching.get("pendingTime")!=null){
                                pendingTime =Integer.parseInt((String) dispatching.get("pendingTime"));
                            }
                            pendingTime = pendingTime + (int)((end.getTime()-start.getTime())/1000);
                            map1.put("pendingState","解挂");
                            map1.put("pendingTime",pendingTime);
                            //如果是上门服务，日期不是同一天修改二次上门次数
                            if(
                                    (
                                            "维修".equals(dispatching.get("serviceType"))
                                                    ||"安装".equals(dispatching.get("serviceType"))
                                                    ||"维修".equals(dispatching.get("firstServiceType"))
                                                    ||"安装".equals(dispatching.get("firstServiceType"))
                                    )&&
                                            (!t2.substring(0,10).equals(t1.substring(0,10)))
                            ){
                                Integer towUp = 0;
                                if(dispatching.get("towUp")!=null){
                                    towUp = Integer.parseInt((String) dispatching.get("towUp"));
                                }
                                towUp ++;
                                map1.put("towUp",towUp);
                            }
                            regionDao.serviceDetailEdit(map1);
                        }
                    } else {
                        //return RetResponse.makeErrRsp("该操作无法理解！");
                    }
                }
            }
            if(ok){
                regionDao.errorDel(id);
            }
        }
        return "ok";
    }

    /**
     * 30分钟及时预约率
     * @param map
     * @return
     */
    @Override
    public RetResult timely1(Map map) {
        try {
            //处理日期
            String startTime = "";
            String endTime = "";
            if (isBlank(map.get("startTime"))) {
                startTime = map.get("startTime") + " 00:00:00";
                map.put("startTime", startTime);
            } else {
                return RetResponse.makeErrRsp("没有startTime参数");
            }
            if (isBlank(map.get("endTime"))) {
                endTime = map.get("endTime") + " 24:00:00";
                map.put("endTime", endTime);
            } else {
                return RetResponse.makeErrRsp("没有endTime参数");
            }
            //返回内容
            Map returnMap = new HashMap<>();
            Map rate = new HashMap<>();
            List<Map> dateRate = new ArrayList<>();
            Map<String, Integer> ls = new HashMap<>();
            int total = 0;
            String target = "";
            String QoQ = "";
            //查询数据
            List<Map> lists = regionDao.lists(map);
            //计算符合和总数
            lists = lists.stream().filter(o -> (!"自接".equals(o.get("dispatchingSource")) && !"非上门维修".equals(o.get("serviceType")))).collect(Collectors.toList());
            total = lists.size();
            SimpleDateFormat g1 = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
            SimpleDateFormat g2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = new GregorianCalendar();

            for (Map d : lists) {
                try {
                    String dispatchingTime = d.get("dispatchingTime").toString().substring(0, 10);
                    ls.put(dispatchingTime + "t", ls.get(dispatchingTime + "t") == null ? 1 : ls.get(dispatchingTime + "t") + 1);
                    //获取考核开始时间
                    String startTimeOfAssessmentstr = (String) d.get("startTimeOfAssessment");
                    Date startTimeOfAssessment = g2.parse(startTimeOfAssessmentstr);
                    //获取考核开始日期
                    Date appointmentOperationTime = g2.parse((String) d.get("appointmentOperationTime"));
                    startTimeOfAssessmentstr = startTimeOfAssessmentstr.substring(0, 11);
                    //拼接考核开始日期和营业开始时间转换成当天开始营业时间
                    Date openingTime = g1.parse(startTimeOfAssessmentstr + d.get("openingTime"));
                    //拼接考核开始日期和营业结束时间转换成当天结束营业时间
                    Date closingTime = g1.parse(startTimeOfAssessmentstr + d.get("closingTime"));
                    if (startTimeOfAssessment.getTime() < openingTime.getTime()) {
                        //如果考核开始时间早于当天营业开始时间
                        if (appointmentOperationTime.getTime() - openingTime.getTime() <= 60 * 60 * 1000) {
                            //如果预约操作时间-营业开始时间小于60分钟，则及时
                            d.put("tt", "1");
                            ls.put(dispatchingTime + "e", ls.get(dispatchingTime + "e") == null ? 1 : ls.get(dispatchingTime + "e") + 1);
                        }
                    } else if (startTimeOfAssessment.getTime() > closingTime.getTime()) {
                        //如果考核开始时间晚于当天营业结束时间，将开始营业时间往后增加一天
                        calendar.setTime(openingTime);
                        calendar.add(calendar.DATE, 1); //把日期往后增加一天,整数  往后推,负数往前移动
                        openingTime = calendar.getTime();
                        if (appointmentOperationTime.getTime() - openingTime.getTime() <= 60 * 60 * 1000) {
                            //如果预约操作时间-营业开始时间小于60分钟，则及时
                            d.put("tt", "1");
                            ls.put(dispatchingTime + "e", ls.get(dispatchingTime + "e") == null ? 1 : ls.get(dispatchingTime + "e") + 1);
                        }
                    } else if (appointmentOperationTime.getTime() - startTimeOfAssessment.getTime() <= 30 * 60 * 1000) {
                        //如果预约操作时间-考核开始时间小于30分钟，则及时
                        d.put("tt", "1");
                        ls.put(dispatchingTime + "e", ls.get(dispatchingTime + "e") == null ? 1 : ls.get(dispatchingTime + "e") + 1);
                    }
                } catch (Exception e) {

                }
            }
            Integer eligible = lists.stream().mapToInt(e -> e.get("tt") == null ? 0 : Integer.parseInt(e.get("tt").toString())).sum();
            rate.put("total", total+"");
            rate.put("eligible", eligible+"");
            String rateStr = "0";
            BigDecimal bfb = new BigDecimal(100);
            if (total > 0) {
                BigDecimal totalBd = new BigDecimal(total);
                BigDecimal eligibleBd = new BigDecimal(eligible);
                BigDecimal rateBd = eligibleBd.multiply(bfb).divide(totalBd, 1, BigDecimal.ROUND_HALF_UP);
                rateStr = rateBd.toString();
            }
            rate.put("rate", rateStr);
            returnMap.put("rate", rate);
            //转换日期生成开始日期到结束日期的连续日期结合
            LocalDate startDate = LocalDate.parse(startTime.substring(0, 10));
            LocalDate endDate = LocalDate.parse(endTime.substring(0, 10));
            long between = ChronoUnit.DAYS.between(startDate, endDate);
            if (between > 0) {
                Set<String> dateAll = Stream.iterate(startDate, e -> e.plusDays(1))
                        .limit(between + 1)
                        .map(LocalDate::toString)
                        .collect(Collectors.toSet());
                //遍历日期，获取对应日期的数据，无则补0，并统计保存
                for (String dt : dateAll) {
                    Map d = new HashMap<>();
                    d.put("dispatchingTime", dt);
                    Integer t1 = ls.get(dt + "t");
                    Integer e1 = ls.get(dt + "e");
                    t1=t1 == null ? 0 : t1;
                    e1=e1 == null ? 0 : e1;
                    d.put("total", t1+"");
                    d.put("eligible",  e1+"");
                    if (t1 != null && t1 > 0) {
                        BigDecimal t = new BigDecimal(t1);
                        BigDecimal e = new BigDecimal(e1);
                        d.put("rate", e.multiply(bfb).divide(t, 1, BigDecimal.ROUND_HALF_UP).toString());
                    } else {
                        d.put("rate", "0");
                    }
                    dateRate.add(d);
                }
            }
            returnMap.put("dateRate", dateRate);
            //如果是本月统计环比
            if (map.get("aggregateDate") != null && "本月".equals(map.get("aggregateDate"))) {
                //查询本月目标
                target = getIndicator(map, "timelyRatw");
                //改变日期为上月月初和上月月底
                getLastMonth(map);
                Map rateLast = new HashMap<>();
                List<Map> dateRateLast = new ArrayList<>();
                ls = new HashMap<>();
                total = 0;
                //查询数据
                lists = regionDao.lists(map);
                //计算符合和总数
                lists = lists.stream().filter(o -> (!"自接".equals(o.get("dispatchingSource")) && !"非上门维修".equals(o.get("serviceType")))).collect(Collectors.toList());
                total = lists.size();
                for (Map d : lists) {
                    try {
                        String dispatchingTime = d.get("dispatchingTime").toString().substring(0, 10);
                        ls.put(dispatchingTime + "t", ls.get(dispatchingTime + "t") == null ? 1 : ls.get(dispatchingTime + "t") + 1);
                        //获取考核开始时间
                        String startTimeOfAssessmentstr = (String) d.get("startTimeOfAssessment");
                        Date startTimeOfAssessment = g2.parse(startTimeOfAssessmentstr);
                        //获取考核开始日期
                        Date appointmentOperationTime = g2.parse((String) d.get("appointmentOperationTime"));
                        startTimeOfAssessmentstr = startTimeOfAssessmentstr.substring(0, 11);
                        //拼接考核开始日期和营业开始时间转换成当天开始营业时间
                        Date openingTime = g1.parse(startTimeOfAssessmentstr + d.get("openingTime"));
                        //拼接考核开始日期和营业结束时间转换成当天结束营业时间
                        Date closingTime = g1.parse(startTimeOfAssessmentstr + d.get("closingTime"));
                        if (startTimeOfAssessment.getTime() < openingTime.getTime()) {
                            //如果考核开始时间早于当天营业开始时间
                            if (appointmentOperationTime.getTime() - openingTime.getTime() <= 60 * 60 * 1000) {
                                //如果预约操作时间-营业开始时间小于60分钟，则及时
                                d.put("tt", "1");
                                ls.put(dispatchingTime + "e", ls.get(dispatchingTime + "e") == null ? 1 : ls.get(dispatchingTime + "e") + 1);
                            }
                        } else if (startTimeOfAssessment.getTime() > closingTime.getTime()) {
                            //如果考核开始时间晚于当天营业结束时间，将开始营业时间往后增加一天
                            calendar.setTime(openingTime);
                            calendar.add(calendar.DATE, 1); //把日期往后增加一天,整数  往后推,负数往前移动
                            openingTime = calendar.getTime();
                            if (appointmentOperationTime.getTime() - openingTime.getTime() <= 60 * 60 * 1000) {
                                //如果预约操作时间-营业开始时间小于60分钟，则及时
                                d.put("tt", "1");
                                ls.put(dispatchingTime + "e", ls.get(dispatchingTime + "e") == null ? 1 : ls.get(dispatchingTime + "e") + 1);
                            }
                        } else if (appointmentOperationTime.getTime() - startTimeOfAssessment.getTime() <= 30 * 60 * 1000) {
                            //如果预约操作时间-考核开始时间小于30分钟，则及时
                            d.put("tt", "1");
                            ls.put(dispatchingTime + "e", ls.get(dispatchingTime + "e") == null ? 1 : ls.get(dispatchingTime + "e") + 1);
                        }
                    } catch (Exception e) {

                    }
                }
                eligible = lists.stream().mapToInt(e -> e.get("tt") == null ? 0 : Integer.parseInt(e.get("tt").toString())).sum();
                rateLast.put("total", total+"");
                rateLast.put("eligible", eligible+"");
                String rateLastStr = "0";
                if (total > 0) {
                    BigDecimal totalBd = new BigDecimal(total);
                    BigDecimal eligibleBd = new BigDecimal(eligible);
                    BigDecimal rateBd = eligibleBd.multiply(bfb).divide(totalBd, 1, BigDecimal.ROUND_HALF_UP);
                    rateLastStr = rateBd.toString();
                }
                rateLast.put("rate", rateLastStr);
                returnMap.put("lastRate", rateLast);
                //转换日期生成开始日期到结束日期的连续日期结合
                startDate = LocalDate.parse(map.get("startTime").toString().substring(0, 10));
                endDate = LocalDate.parse(map.get("endTime").toString().substring(0, 10));
                between = ChronoUnit.DAYS.between(startDate, endDate);
                if (between > 0) {
                    Set<String> dateAll = Stream.iterate(startDate, e -> e.plusDays(1))
                            .limit(between + 1)
                            .map(LocalDate::toString)
                            .collect(Collectors.toSet());
                    //遍历日期，获取对应日期的数据，无则补0，并统计保存
                    for (String dt : dateAll) {
                        Map d = new HashMap<>();
                        d.put("dispatchingTime", dt);
                        Integer t1 = ls.get(dt + "t");
                        Integer e1 = ls.get(dt + "e");
                        t1=t1 == null ? 0 : t1;
                        e1=e1 == null ? 0 : e1;
                        d.put("total", t1+"");
                        d.put("eligible", e1+"");
                        if (t1 != null && !"0".equals(t1.toString())) {
                            BigDecimal t = new BigDecimal(t1);
                            BigDecimal e = new BigDecimal(e1);
                            d.put("rate", e.multiply(bfb).divide(t, 1, BigDecimal.ROUND_HALF_UP).toString());
                        } else {
                            d.put("rate", "0");
                        }
                        dateRateLast.add(d);
                    }
                }
                returnMap.put("lastDateRate", dateRateLast);
                //判断除数和被除数有为0的，直接环比为0，不然计算的时候报错
                if (rateLast.get("rate")==null||"0".equals(rateLast.get("rate"))) {
                    QoQ = "0";
                } else {
                    BigDecimal now = new BigDecimal((String) rate.get("rate"));
                    BigDecimal last = new BigDecimal((String) rateLast.get("rate"));
                    //(本月率-上月率)/上月率*100,算出环比
                    BigDecimal QOQ = ((now.subtract(last)).multiply(bfb).divide(last, 1, BigDecimal.ROUND_HALF_UP));
                    QoQ = QOQ.toString();
                }
            }
            returnMap.put("QOQ", QoQ);
            returnMap.put("target", target);
            return RetResponse.makeOKRsp(returnMap);
        }catch (Exception e){
            return RetResponse.makeRsp(400,e.toString().replaceAll("\r\n",""),map);
        }
    }
    /**
     *预约准时上门率
     * @param map
     * @return
     */
    @Override
    public RetResult punctuality(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        BigDecimal bfb = new BigDecimal(100);
        //判断是否首次
        String punctualityType = (String) map.get("punctualityType");
        if("首次预约".equals(punctualityType)){
            //获取时间范围内率
            Map rate = regionDao.fristPunctuality(map);
           if(rate==null){
            rate = new HashMap<>();
        }
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
                rate.put("rate","0");
            }else{
                BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
                BigDecimal last = new BigDecimal( rate.get("total").toString());
                rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
            }
            returnMap.put("rate",rate);
            //获取时间范围内日率
            List<Map> datePunctuality =  regionDao.fristDatePunctuality(map);
            //给没有数据的日期补零并排序
            datePunctuality = dateAll((String) map.get("startTime"), (String) map.get("endTime"),datePunctuality);
            returnMap.put("dateRate",datePunctuality);
            returnMap.put("target", getIndicator(map,"firstPunctualRate"));
            try {
                if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                    map = lastMonth(map);
                    Map lastRate = regionDao.fristPunctuality(map);
                    if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                    try{
                        //上月分子
                        BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                        //上月分母
                        BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                        //本月率
                        BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                        //计算上月率
                        BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                        //计算同比上月
                        BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                        returnMap.put("QOQ", QOQ.toString());
                    }catch (Exception e){
                        returnMap.put("QOQ", "0");
                    }
                }else{
                    returnMap.put("QOQ", "");
                    //returnMap.put("target", "");
                }
            }catch (Exception e){

            }
        }else{
            //获取时间范围内率
            Map rate = regionDao.punctuality(map);
           if(rate==null){
            rate = new HashMap<>();
        }
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
                rate.put("rate","0");
            }else{
                BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
                BigDecimal last = new BigDecimal( rate.get("total").toString());
                rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
            }
            returnMap.put("rate",rate);
            //获取时间范围内日率
            List<Map> datePunctuality =  regionDao.datePunctuality(map);
            //给没有数据的日期补零并排序
            datePunctuality = dateAll((String) map.get("startTime"), (String) map.get("endTime"),datePunctuality);
            returnMap.put("dateRate",datePunctuality);
            returnMap.put("target", getIndicator(map,"secondPunctualRate"));
            try {

                //如果是本月统计环比
                if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                    map = lastMonth(map);
                    Map lastRate = regionDao.punctuality(map);
                    if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                    try{
                        //上月分子
                        BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                        //上月分母
                        BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                        //本月率
                        BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                        //计算上月率
                        BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                        //计算同比上月
                        BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                        returnMap.put("QOQ", QOQ.toString());
                    }catch (Exception e){
                        returnMap.put("QOQ", "0");
                    }
                }else{
                    returnMap.put("QOQ", "");
                    //returnMap.put("target", "");
                }
            }catch (Exception e){

            }
        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *预约准时上门率
     * @param map
     * @return
     */
    @Override
    public RetResult punctualityMap(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        BigDecimal bfb = new BigDecimal(100);
        //判断是否首次
        String punctualityType = (String) map.get("punctualityType");
        if("首次预约".equals(punctualityType)){
            //获取时间范围内率
            Map rate = regionDao.fristPunctuality(map);
           if(rate==null){
            rate = new HashMap<>();
        }
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
                rate.put("rate","0");
            }else{
                BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
                BigDecimal last = new BigDecimal( rate.get("total").toString());
                rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
            }
            returnMap.put("rate",rate);
            //获取时间范围内日率
            List<Map> datePunctuality =  regionDao.fristDatePunctuality(map);
            //给没有数据的日期补零并排序
            datePunctuality = dateAll((String) map.get("startTime"), (String) map.get("endTime"),datePunctuality);
            returnMap.put("dateRate",datePunctuality);
            returnMap.put("target", getIndicator(map,"firstPunctualRate"));
            try {

                //如果是本月统计环比
                if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                    map = lastMonth(map);
                    //获取时间范围开始和结束时间减一个月日率
                    List<Map> lastDatePunctuality =  regionDao.fristDatePunctuality(map);
                    //给没有数据的日期补零并排序
                    lastDatePunctuality = dateAll((String) map.get("startTime"), (String) map.get("endTime"),lastDatePunctuality);
                    returnMap.put("lastDateRate",lastDatePunctuality);
                    Map lastRate = regionDao.fristPunctuality(map);
                    if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                    try{
                        //上月分子
                        BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                        //上月分母
                        BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                        //本月率
                        BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                        //计算上月率
                        BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                        //计算同比上月
                        BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                        returnMap.put("QOQ", QOQ.toString());
                    }catch (Exception e){
                        returnMap.put("QOQ", "0");
                    }
                }else{
                    returnMap.put("QOQ", "");
                    //returnMap.put("target", "");
                }
            }catch (Exception e){

            }
        }else{
            //获取时间范围内率
            Map rate = regionDao.punctuality(map);
           if(rate==null){
            rate = new HashMap<>();
        }
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
                rate.put("rate","0");
            }else{
                BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
                BigDecimal last = new BigDecimal( rate.get("total").toString());
                rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
            }
            returnMap.put("rate",rate);
            //获取时间范围内日率
            List<Map> datePunctuality =  regionDao.datePunctuality(map);
            //给没有数据的日期补零并排序
            datePunctuality = dateAll((String) map.get("startTime"), (String) map.get("endTime"),datePunctuality);
            returnMap.put("dateRate",datePunctuality);
            returnMap.put("target", getIndicator(map,"secondPunctualRate"));
            try {

                //如果是本月统计环比
                if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                    map = lastMonth(map);
                    //获取时间范围开始和结束时间减一个月日率
                    List<Map> lastDatePunctuality =  regionDao.datePunctuality(map);
                    lastDatePunctuality = dateAll((String) map.get("startTime"), (String) map.get("endTime"),lastDatePunctuality);
                    returnMap.put("lastDateRate",lastDatePunctuality);
                    Map lastRate = regionDao.punctuality(map);
                    if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                    try{
                        //上月分子
                        BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                        //上月分母
                        BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                        //本月率
                        BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                        //计算上月率
                        BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                        //计算同比上月
                        BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                        returnMap.put("QOQ", QOQ.toString());
                    }catch (Exception e){
                        returnMap.put("QOQ", "0");
                    }
                }else{
                    returnMap.put("QOQ", "");
                    //returnMap.put("target", "");
                }
            }catch (Exception e){

            }
        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *预约准时上门率
     * @param map
     * @return
     */
    @Override
    public RetResult punctuality1(Map map) {
        //处理日期
        String startTime = "";
        String endTime = "";
        if (isBlank(map.get("startTime"))) {
            startTime = map.get("startTime") + " 00:00:00";
            map.put("startTime", startTime);
        } else {
            return RetResponse.makeErrRsp("没有startTime参数");
        }
        if (isBlank(map.get("endTime"))) {
            endTime = map.get("endTime") + " 24:00:00";
            map.put("endTime", endTime);
        } else {
            return RetResponse.makeErrRsp("没有endTime参数");
        }
        //返回内容
        Map returnMap = new HashMap<>();
        Map rate = new HashMap<>();
        List<Map> dateRate = new ArrayList<>();
        Map<String, Integer> ls = new HashMap<>();
        int total = 0;
        String target = "";
        String QoQ = "";
        BigDecimal bfb = new BigDecimal(100);
        //查询数据
        List<Map> lists = regionDao.lists(map);
        //计算符合和总数
        lists = lists.stream().filter(o -> ("已上门,已服务,已完成".indexOf(o.get("systemState")+"")!=-1 && !"非上门维修".equals(o.get("serviceType")))).collect(Collectors.toList());
        SimpleDateFormat g1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat g2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = new GregorianCalendar();
        //默认为预约前缀
        String frista="a";//预约
        String fristv="v";//上门
        //如果是首次预约切换成首次预约前缀
        if("首次预约".equals(map.get("punctualityType"))){
            frista="firstA";//首次预约
            fristv="firstV";//首次上门
            //查询挂单隔天的并转map，方便对比
            List<Map> twoUp = regionDao.twoUp(map);
            Map twoMap = // 覆盖key相同的值，
                    twoUp.stream().collect(Collectors.toMap(s->s.get("dispatchingOrder"),s->s.get("two")));
            //查询二次上门认定表存在的并转map，方便对比
            List<Map> twoUp2 = regionDao.twoUp2(map);
            Map twoMap2 = // 覆盖key相同的值，
                    twoUp2.stream().collect(Collectors.toMap(s->s.get("dispatchingOrder"),s->s.get("two")));
            //删除二次上门
            lists.removeIf(filter->("非上门维修".equals(filter.get("serviceType"))&&twoMap2.get(filter.get("dispatchingOrder"))!=null)||(!("非上门维修".equals(filter.get("serviceType")))&&twoMap.get(filter.get("dispatchingOrder"))!=null));
        }
        total = lists.size();
        for(Map d : lists){
            try {
                //根据预约前缀计算是否准时
                String dispatchingTime = d.get("dispatchingTime").toString().substring(0, 10);
                ls.put(dispatchingTime + "t", ls.get(dispatchingTime + "t") == null ? 1 : ls.get(dispatchingTime + "t") + 1);
                Date startAppointmentTime = g1.parse(((String) d.get(frista+"ppointmentTimeSection")).substring(0,16));
                Date endAppointmentTime = g1.parse(((String) d.get(frista+"ppointmentTimeSection")).substring(0,11)+((String) d.get(frista+"ppointmentTimeSection")).substring(d.get(frista+"ppointmentTimeSection").toString().length()-5));
                Date visitTime = g2.parse((String) d.get(fristv+"isitTime"));
                if(visitTime.getTime()>=startAppointmentTime.getTime()&&visitTime.getTime()<=endAppointmentTime.getTime()){
                    d.put("tt", "1");
                    ls.put(dispatchingTime + "e", ls.get(dispatchingTime + "e") == null ? 1 : ls.get(dispatchingTime + "e") + 1);
                }
            }catch (Exception e){

            }
        }
        //统计分子
        Integer eligible = lists.stream().mapToInt(e -> e.get("tt") == null ? 0 : Integer.parseInt(e.get("tt").toString())).sum();
        rate.put("total", total+"");
        rate.put("eligible", eligible+"");
        //计算率
        String rateStr = "0";
        if (total > 0) {
            BigDecimal totalBd = new BigDecimal(total);
            BigDecimal eligibleBd = new BigDecimal(eligible);
            BigDecimal rateBd = eligibleBd.multiply(bfb).divide(totalBd, 1, BigDecimal.ROUND_HALF_UP);
            rateStr = rateBd.toString();
        }
        rate.put("rate", rateStr);
        returnMap.put("rate", rate);
        //转换日期生成开始日期到结束日期的连续日期结合
        LocalDate startDate = LocalDate.parse(startTime.substring(0, 10));
        LocalDate endDate = LocalDate.parse(endTime.substring(0, 10));
        long between = ChronoUnit.DAYS.between(startDate, endDate);
        if (between > 0) {
            Set<String> dateAll = Stream.iterate(startDate, e -> e.plusDays(1))
                    .limit(between + 1)
                    .map(LocalDate::toString)
                    .collect(Collectors.toSet());
            //遍历日期，获取对应日期的数据，无则补0，并统计保存
            for (String dt : dateAll) {
                Map d = new HashMap<>();
                d.put("dispatchingTime", dt);
                Integer t1 = ls.get(dt + "t");
                Integer e1 = ls.get(dt + "e");
                t1=t1 == null ? 0 : t1;
                e1=e1 == null ? 0 : e1;
                d.put("total", t1+"");
                d.put("eligible",  e1+"");
                if (t1 != null && t1 > 0) {
                    BigDecimal t = new BigDecimal(t1);
                    BigDecimal e = new BigDecimal(e1);
                    d.put("rate", e.multiply(bfb).divide(t, 1, BigDecimal.ROUND_HALF_UP).toString());
                } else {
                    d.put("rate", "0");
                }
                dateRate.add(d);
            }
        }
        returnMap.put("dateRate", dateRate);
        //如果是本月统计环比
        if (map.get("aggregateDate") != null && "本月".equals(map.get("aggregateDate"))) {
            //查询本月目标
            if("首次预约".equals(map.get("punctualityType"))){
                target = getIndicator(map, "firstPunctualRate");
            }else{
                target = getIndicator(map, "secondPunctualRate");
            }
            //改变日期为上月月初和上月月底
            getLastMonth(map);
            Map rateLast = new HashMap<>();
            List<Map> dateRateLast = new ArrayList<>();
            ls = new HashMap<>();
            total = 0;
            //查询数据
            lists = regionDao.lists(map);
            //计算符合和总数
            lists = lists.stream().filter(o -> ("已上门,已服务,已完成".indexOf(o.get("systemState")+"")!=-1 && !"非上门维修".equals(o.get("serviceType")))).collect(Collectors.toList());
            if("首次预约".equals(map.get("punctualityType"))){
                //查询挂单隔天的并转map，方便对比
                List<Map> twoUp = regionDao.twoUp(map);
                Map twoMap = // 覆盖key相同的值，
                        twoUp.stream().collect(Collectors.toMap(s->s.get("dispatchingOrder"),s->s.get("two")));
                //查询二次上门认定表存在的并转map，方便对比
                List<Map> twoUp2 = regionDao.twoUp2(map);
                Map twoMap2 = // 覆盖key相同的值，
                        twoUp2.stream().collect(Collectors.toMap(s->s.get("dispatchingOrder"),s->s.get("two")));
                //删除二次上门
                lists.removeIf(filter->("非上门维修".equals(filter.get("serviceType"))&&twoMap2.get(filter.get("dispatchingOrder"))!=null)||(!("非上门维修".equals(filter.get("serviceType")))&&twoMap.get(filter.get("dispatchingOrder"))!=null));
            }
            total = lists.size();
            for(Map d : lists){
                try {
                    //根据预约前缀计算是否准时
                    String dispatchingTime = d.get("dispatchingTime").toString().substring(0, 10);
                    ls.put(dispatchingTime + "t", ls.get(dispatchingTime + "t") == null ? 1 : ls.get(dispatchingTime + "t") + 1);
                    Date startAppointmentTime = g1.parse(((String) d.get(frista+"ppointmentTimeSection")).substring(0,16));
                    Date endAppointmentTime = g1.parse(((String) d.get(frista+"ppointmentTimeSection")).substring(0,11)+((String) d.get(frista+"ppointmentTimeSection")).substring(d.get(frista+"ppointmentTimeSection").toString().length()-5));
                    Date visitTime = g2.parse((String) d.get(fristv+"isitTime"));
                    if(visitTime.getTime()>=startAppointmentTime.getTime()&&visitTime.getTime()<=endAppointmentTime.getTime()){
                        d.put("tt", "1");
                        ls.put(dispatchingTime + "e", ls.get(dispatchingTime + "e") == null ? 1 : ls.get(dispatchingTime + "e") + 1);
                    }
                }catch (Exception e){

                }
            }
            //统计分子
            eligible = lists.stream().mapToInt(e -> e.get("tt") == null ? 0 : Integer.parseInt(e.get("tt").toString())).sum();
            rateLast.put("total", total+"");
            rateLast.put("eligible", eligible+"");
            String rateLastStr = "0";
            //计算上月率
            if (total > 0) {
                BigDecimal totalBd = new BigDecimal(total);
                BigDecimal eligibleBd = new BigDecimal(eligible);
                BigDecimal rateBd = eligibleBd.multiply(bfb).divide(totalBd, 1, BigDecimal.ROUND_HALF_UP);
                rateLastStr = rateBd.toString();
            }
            rateLast.put("rate", rateLastStr);
            returnMap.put("lastRate", rateLast);
            //转换日期生成开始日期到结束日期的连续日期结合
            startDate = LocalDate.parse(map.get("startTime").toString().substring(0, 10));
            endDate = LocalDate.parse(map.get("endTime").toString().substring(0, 10));
            between = ChronoUnit.DAYS.between(startDate, endDate);
            if (between > 0) {
                Set<String> dateAll = Stream.iterate(startDate, e -> e.plusDays(1))
                        .limit(between + 1)
                        .map(LocalDate::toString)
                        .collect(Collectors.toSet());
                //遍历日期，获取对应日期的数据，无则补0，并统计保存
                for (String dt : dateAll) {
                    Map d = new HashMap<>();
                    d.put("dispatchingTime", dt);
                    Integer t1 = ls.get(dt + "t");
                    Integer e1 = ls.get(dt + "e");
                    t1=t1 == null ? 0 : t1;
                    e1=e1 == null ? 0 : e1;
                    d.put("total", t1+"");
                    d.put("eligible", e1+"");
                    if (t1 != null && !"0".equals(t1.toString())) {
                        BigDecimal t = new BigDecimal(t1);
                        BigDecimal e = new BigDecimal(e1);
                        d.put("rate", e.multiply(bfb).divide(t, 1, BigDecimal.ROUND_HALF_UP).toString());
                    } else {
                        d.put("rate", "0");
                    }
                    dateRateLast.add(d);
                }
            }
            returnMap.put("lastDateRate", dateRateLast);
            //判断除数和被除数有为0的，直接环比为0，不然计算的时候报错
            if (rateLast.get("rate")==null||"0".equals(rateLast.get("rate"))) {
                QoQ = "0";
            } else {
                BigDecimal now = new BigDecimal((String) rate.get("rate"));
                BigDecimal last = new BigDecimal((String) rateLast.get("rate"));
                //(本月率-上月率)/上月率*100,算出环比
                BigDecimal QOQ = ((now.subtract(last)).multiply(bfb).divide(last, 1, BigDecimal.ROUND_HALF_UP));
                QoQ = QOQ.toString();
            }
        }
        returnMap.put("QOQ", QoQ);
        returnMap.put("target", target);
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *TAT平均服务完成时长
     * @param map
     * @return
     */
    @Override
    public RetResult average(Map map) {
        try {
            //处理日期
            map = dateConversion(map);
            Map returnMap = new HashMap<>();
            BigDecimal bfb = new BigDecimal(100);
            //获取时间范围内率
            Map rate = regionDao.average(map);
           if(rate==null){
            rate = new HashMap<>();
        }
           //计算率
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
            rate.put("rate","0");
        }else{
            BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
            BigDecimal last = new BigDecimal( rate.get("total").toString());
            rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
        }
        //小时数转*天*小时格式
            rate.put("avgDay",hoursToDay(rate.get("average")));
            returnMap.put("rate",rate);
            //获取时间范围内日率
            List<Map> dateAverage =  regionDao.dateAverage(map);
            dateAverage = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateAverage);
            returnMap.put("dateRate",dateAverage);
            returnMap.put("target", getIndicator(map,"avgTimeRate"));
            String AvgTimeRate =  getIndicator(map,"avgTimeDay");
            returnMap.put("target3", AvgTimeRate);
            returnMap.put("target2", hoursToDay(AvgTimeRate));
            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                map = lastMonth(map);
                Map lastRate = regionDao.average(map);
                if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                try{
                    //上月分子
                    BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                    //上月分母
                    BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                    //本月率
                    BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                    //计算上月率
                    BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    //计算同比上月
                    BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }catch (Exception e){
                    returnMap.put("QOQ", "0");
                }

            }else{
                returnMap.put("QOQ", "");
            }
            return RetResponse.makeOKRsp(returnMap);
        }catch (Exception e){
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }
    /**
     *TAT平均服务完成时长饼图
     * @param map
     * @return
     */
    @Override
    public RetResult averagePie(Map map) {
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        List<Map> causePie = regionDao.causePie(map);
        List<Map> noPendingPie = regionDao.noPendingPie(map);
        returnMap.put("timelyPie",causePie);
        returnMap.put("punctualityPie",noPendingPie);
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *TAT平均服务完成时长图表
     * @param map
     * @return
     */
    @Override
    public RetResult averageMap(Map map) {
        try {
            //处理日期
            map = dateConversion(map);
            Map returnMap = new HashMap<>();
            BigDecimal bfb = new BigDecimal(100);

            //获取时间范围内率
            Map rate = regionDao.average(map);
           if(rate==null){
            rate = new HashMap<>();
        }
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
                rate.put("rate","0");
            }else{
                BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
                BigDecimal last = new BigDecimal( rate.get("total").toString());
                rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
            }
            rate.put("avgDay",hoursToDay(rate.get("average")));
            returnMap.put("rate",rate);
            //获取时间范围内日率
            List<Map> dateAverage =  regionDao.dateAverage(map);
            dateAverage = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateAverage);
            returnMap.put("dateRate",dateAverage);
            returnMap.put("target", getIndicator(map,"avgTimeRate"));
            String AvgTimeRate =  getIndicator(map,"avgTimeDay");
            returnMap.put("target3", AvgTimeRate);
            returnMap.put("target2", hoursToDay(AvgTimeRate));

            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                map = lastMonth(map);
                //获取时间范围开始和结束时间减一个月日率
                List<Map> lastDateAverage =  regionDao.dateAverage(map);
                lastDateAverage = dateAll((String) map.get("startTime"), (String) map.get("endTime"),lastDateAverage);
                returnMap.put("lastDateRate",lastDateAverage);
                Map lastRate = regionDao.average(map);
                if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                try{
                    //上月分子
                    BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                    //上月分母
                    BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                    //本月率
                    BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                    //计算上月率
                    BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    //计算同比上月
                    BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }catch (Exception e){
                    returnMap.put("QOQ", "0");
                }


            }else{
                returnMap.put("QOQ", "");
            }
            return RetResponse.makeOKRsp(returnMap);
        }catch (Exception e){
            return RetResponse.makeErrRsp(e.getMessage());
        }

    }
    /**
     *TAT平均服务完成时长
     * @param map
     * @return
     */
    @Override
    public RetResult average1(Map map) {
        try {
            //处理日期
            String startTime = "";
            String endTime = "";
            if (isBlank(map.get("startTime"))) {
                startTime = map.get("startTime") + " 00:00:00";
                map.put("startTime", startTime);
            } else {
                return RetResponse.makeErrRsp("没有startTime参数");
            }
            if (isBlank(map.get("endTime"))) {
                endTime = map.get("endTime") + " 24:00:00";
                map.put("endTime", endTime);
            } else {
                return RetResponse.makeErrRsp("没有endTime参数");
            }
            //返回内容
            Map returnMap = new HashMap<>();
            Map rate = new HashMap<>();
            List<Map> dateRate = new ArrayList<>();
            Map<String, Long> ls = new HashMap<>();
            int total = 0;
            String target = "";
            String target2 = "";
            String target3 = "";
            String QoQ = "";
            BigDecimal bfb = new BigDecimal(100);
            SimpleDateFormat g1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = new GregorianCalendar();
            //查询数据
            List<Map> lists = regionDao.lists(map);
            total = lists.size();
            List<Map> gdLongTime = regionDao.gdLongTime(map);
            Map gdLongTimeMap = // 覆盖key相同的值，
                    gdLongTime.stream().collect(Collectors.toMap(s->s.get("dispatchingOrder"),s->s.get("longTime")));
            //计算符合和总数
            for(Map d:lists){
                try {
                    String dispatchingTime = d.get("dispatchingTime").toString().substring(0, 10);
                    ls.put(dispatchingTime + "t", ls.get(dispatchingTime + "t") == null ? 1 : ls.get(dispatchingTime + "t") + 1);
                    long longTime = 0;
                    Date start = g1.parse((String) d.get("startTimeOfAssessment"));
                    if("非上门维修".equals(d.get("serviceType"))){
                        Date end = g1.parse((String) (d.get("exchangeMaintain")==null||"".equals(d.get("exchangeMaintain"))?d.get("exchangeMaintain"):d.get("customerDeliveryTime")));
                        longTime = end.getTime()-start.getTime();
                    }else{
                        Date end = g1.parse((String) d.get("finishTime"));
                        long gdTime = gdLongTimeMap.get(d.get("dispatchingOrder"))==null?0:(long)gdLongTimeMap.get(d.get("dispatchingOrder"))*1000;
                        longTime = end.getTime()-start.getTime()-gdTime;
                    }
                    d.put("average", longTime);
                    ls.put(dispatchingTime + "a", ls.get(dispatchingTime + "a") == null ? longTime : ls.get(dispatchingTime + "a") + longTime);
                    if(longTime<=4*24*60*60*1000){
                        d.put("tt", "1");
                        ls.put(dispatchingTime + "e", ls.get(dispatchingTime + "e") == null ? 1 : ls.get(dispatchingTime + "e") + 1);
                    }

                }catch (Exception e){

                }
            }
            Integer eligible = lists.stream().mapToInt(e -> e.get("tt") == null ? 0 : Integer.parseInt(e.get("tt").toString())).sum();
            int average = (int)((lists.stream().mapToDouble(e -> e.get("average") == null ? 0.0 : Double.parseDouble(e.get("average").toString())).average()).getAsDouble()/1000/60/60);
            rate.put("total", total+"");
            rate.put("eligible", eligible+"");
            rate.put("average", average+"");
            String avgDay = "";
            int days = average/24;
            if(days>0){
                avgDay = days +"天";
            }
            avgDay = average%24 +"小时";
            rate.put("avgDay", avgDay);
            String rateStr = "0";
            if (total > 0) {
                BigDecimal totalBd = new BigDecimal(total);
                BigDecimal eligibleBd = new BigDecimal(eligible);
                BigDecimal rateBd = eligibleBd.multiply(bfb).divide(totalBd, 1, BigDecimal.ROUND_HALF_UP);
                rateStr = rateBd.toString();
            }
            rate.put("rate", rateStr);
            returnMap.put("rate", rate);
            //转换日期生成开始日期到结束日期的连续日期结合
            LocalDate startDate = LocalDate.parse(startTime.substring(0, 10));
            LocalDate endDate = LocalDate.parse(endTime.substring(0, 10));
            long between = ChronoUnit.DAYS.between(startDate, endDate);
            if (between > 0) {
                Set<String> dateAll = Stream.iterate(startDate, e -> e.plusDays(1))
                        .limit(between + 1)
                        .map(LocalDate::toString)
                        .collect(Collectors.toSet());
                //遍历日期，获取对应日期的数据，无则补0，并统计保存
                for (String dt : dateAll) {
                    Map d = new HashMap<>();
                    d.put("dispatchingTime", dt);
                    Long t1 = ls.get(dt + "t");
                    Long e1 = ls.get(dt + "e");
                    t1=t1 == null ? 0 : t1;
                    e1=e1 == null ? 0 : e1;
                    d.put("total", t1+"");
                    d.put("eligible",  e1+"");
                    if (t1 != null && t1 > 0) {
                        BigDecimal t = new BigDecimal(t1);
                        BigDecimal e = new BigDecimal(e1);
                        d.put("rate", e.multiply(bfb).divide(t, 1, BigDecimal.ROUND_HALF_UP).toString());
                    } else {
                        d.put("rate", "0");
                    }
                    dateRate.add(d);
                }
            }
            returnMap.put("dateRate", dateRate);
            target = getIndicator(map, "avgTimeRate");
            target3 = getIndicator(map, "avgTime");
            if (map.get("aggregateDate") != null && "本月".equals(map.get("aggregateDate"))) {

                target2 = "";
                if(target3!=null) {
                    int aa = Integer.parseInt(target3);
                    days = aa / 24;
                    if (days > 0) {
                        target2 = days + "天";
                    }
                    target2 = aa % 24 + "小时";
                }
                //改变日期为上月月初和上月月底
                getLastMonth(map);
                Map rateLast = new HashMap<>();
                List<Map> dateRateLast = new ArrayList<>();
                ls = new HashMap<>();
                total = 0;
                //查询数据
                lists = regionDao.lists(map);
                total = lists.size();
                gdLongTime = regionDao.gdLongTime(map);
                gdLongTimeMap = // 覆盖key相同的值，
                        gdLongTime.stream().collect(Collectors.toMap(s->s.get("dispatchingOrder"),s->s.get("longTime")));
                //计算符合和总数
                for(Map d:lists){
                    try {
                        String dispatchingTime = d.get("dispatchingTime").toString().substring(0, 10);
                        ls.put(dispatchingTime + "t", ls.get(dispatchingTime + "t") == null ? 1 : ls.get(dispatchingTime + "t") + 1);
                        long longTime = 0;
                        Date start = g1.parse((String) d.get("startTimeOfAssessment"));
                        if("非上门维修".equals(d.get("serviceType"))){
                            Date end = g1.parse((String) (d.get("exchangeMaintain")==null||"".equals(d.get("exchangeMaintain"))?d.get("exchangeMaintain"):d.get("customerDeliveryTime")));
                            longTime = end.getTime()-start.getTime();
                        }else{
                            Date end = g1.parse((String) d.get("finishTime"));
                            long gdTime = gdLongTimeMap.get(d.get("dispatchingOrder"))==null?0:(long)gdLongTimeMap.get(d.get("dispatchingOrder"))*1000;
                            longTime = end.getTime()-start.getTime()-gdTime;
                        }
                        d.put("average", longTime);
                        ls.put(dispatchingTime + "a", ls.get(dispatchingTime + "a") == null ? longTime : ls.get(dispatchingTime + "a") + longTime);
                        if(longTime<=4*24*60*60*1000){
                            d.put("tt", "1");
                            ls.put(dispatchingTime + "e", ls.get(dispatchingTime + "e") == null ? 1 : ls.get(dispatchingTime + "e") + 1);
                        }

                    }catch (Exception e){

                    }
                }
                eligible = lists.stream().mapToInt(e -> e.get("tt") == null ? 0 : Integer.parseInt(e.get("tt").toString())).sum();
                average = (int)((lists.stream().mapToDouble(e -> e.get("average") == null ? 0.0 : Double.parseDouble(e.get("average").toString())).average()).getAsDouble()/1000/60/60);
                rateLast.put("total", total+"");
                rateLast.put("eligible", eligible+"");
                rateLast.put("average", average+"");
                avgDay = "";
                days = average/24;
                if(days>0){
                    avgDay = days +"天";
                }
                avgDay = average%24 +"小时";
                rateLast.put("avgDay", avgDay);
                String rateLastStr = "0";
                if (total > 0) {
                    BigDecimal totalBd = new BigDecimal(total);
                    BigDecimal eligibleBd = new BigDecimal(eligible);
                    BigDecimal rateBd = eligibleBd.multiply(bfb).divide(totalBd, 1, BigDecimal.ROUND_HALF_UP);
                    rateLastStr = rateBd.toString();
                }
                rateLast.put("rate", rateLastStr);
                returnMap.put("lastRate", rateLast);
                //转换日期生成开始日期到结束日期的连续日期结合
                startDate = LocalDate.parse(map.get("startTime").toString().substring(0, 10));
                endDate = LocalDate.parse(map.get("endTime").toString().substring(0, 10));
                between = ChronoUnit.DAYS.between(startDate, endDate);
                if (between > 0) {
                    Set<String> dateAll = Stream.iterate(startDate, e -> e.plusDays(1))
                            .limit(between + 1)
                            .map(LocalDate::toString)
                            .collect(Collectors.toSet());
                    //遍历日期，获取对应日期的数据，无则补0，并统计保存
                    for (String dt : dateAll) {
                        Map d = new HashMap<>();
                        d.put("dispatchingTime", dt);
                        Long t1 = ls.get(dt + "t");
                        Long e1 = ls.get(dt + "e");
                        t1=t1 == null ? 0 : t1;
                        e1=e1 == null ? 0 : e1;
                        d.put("total", t1+"");
                        d.put("eligible", e1+"");
                        if (t1 != null && !"0".equals(t1.toString())) {
                            BigDecimal t = new BigDecimal(t1);
                            BigDecimal e = new BigDecimal(e1);
                            d.put("rate", e.multiply(bfb).divide(t, 1, BigDecimal.ROUND_HALF_UP).toString());
                        } else {
                            d.put("rate", "0");
                        }
                        dateRateLast.add(d);
                    }
                }
                returnMap.put("lastDateRate", dateRateLast);
                //判断除数和被除数有为0的，直接环比为0，不然计算的时候报错
                if (rateLast.get("rate")==null||"0".equals(rateLast.get("rate"))) {
                    QoQ = "0";
                } else {
                    BigDecimal now = new BigDecimal((String) rate.get("rate"));
                    BigDecimal last = new BigDecimal((String) rateLast.get("rate"));
                    //(本月率-上月率)/上月率*100,算出环比
                    BigDecimal QOQ = ((now.subtract(last)).multiply(bfb).divide(last, 1, BigDecimal.ROUND_HALF_UP));
                    QoQ = QOQ.toString();
                }
            }
            returnMap.put("QOQ", QoQ);
            returnMap.put("target", target);
            returnMap.put("target2", target2);
            returnMap.put("target3", target3);
            return RetResponse.makeOKRsp(returnMap);
        }catch (Exception e){
            return RetResponse.makeErrRsp(e.getMessage());
        }

    }
    /**
     *投诉7天解决率
     * @param map
     * @return
     */
    @Override
    public RetResult solve(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        BigDecimal bfb = new BigDecimal(100);
        //获取时间范围内率
        Map rate = regionDao.solve(map);
       if(rate==null){
            rate = new HashMap<>();
        }
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
            rate.put("rate","0");
        }else{
            BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
            BigDecimal last = new BigDecimal( rate.get("total").toString());
            rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
        }
        returnMap.put("rate",rate);
        //获取时间范围内日率
        List<Map> dateSolve =  regionDao.dateSolve(map);
        dateSolve = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateSolve);
        returnMap.put("dateRate",dateSolve);
        returnMap.put("target", getIndicator(map,"resolutionRate"));
        try {

            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                //开始时间结束时间月份-1
                map = lastMonth(map);
                Map lastRate = regionDao.solve(map);
                if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                try{
                    //上月分子
                    BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                    //上月分母
                    BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                    //本月率
                    BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                    //计算上月率
                    BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    //计算同比上月
                    BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }catch (Exception e){
                    returnMap.put("QOQ", "0");
                }


            }else{
                returnMap.put("QOQ", "");
                //returnMap.put("target", "");
            }
        }catch (Exception e){

        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *投诉7天解决率图表
     * @param map
     * @return
     */
    @Override
    public RetResult solveMap(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        BigDecimal bfb = new BigDecimal(100);
        //获取时间范围内率
        Map rate = regionDao.solve(map);
       if(rate==null){
            rate = new HashMap<>();
        }
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
            rate.put("rate","0");
        }else{
            BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
            BigDecimal last = new BigDecimal( rate.get("total").toString());
            rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
        }
        returnMap.put("rate",rate);
        //获取时间范围内日率
        List<Map> dateSolve =  regionDao.dateSolve(map);
        dateSolve = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateSolve);
        returnMap.put("dateRate",dateSolve);
        try {
            returnMap.put("target", getIndicator(map,"resolutionRate"));
            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                //开始时间结束时间月份-1
                map = lastMonth(map);
                //获取时间范围开始和结束时间减一个月日率
                List<Map> lastDateSolve =  regionDao.dateSolve(map);
                lastDateSolve = dateAll((String) map.get("startTime"), (String) map.get("endTime"),lastDateSolve);
                returnMap.put("lastDateRate",lastDateSolve);
                Map lastRate = regionDao.solve(map);
                if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                try{
                    //上月分子
                    BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                    //上月分母
                    BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                    //本月率
                    BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                    //计算上月率
                    BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    //计算同比上月
                    BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }catch (Exception e){
                    returnMap.put("QOQ", "0");
                }


            }else{
                returnMap.put("QOQ", "");
                //returnMap.put("target", "");
            }

        }catch (Exception e){

        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *投诉7天解决率
     * @param map
     * @return
     */
    @Override
    public RetResult solve1(Map map) {
        try{
            //处理日期
            String startTime = "";
            String endTime = "";
            if (isBlank(map.get("startTime"))) {
                startTime = map.get("startTime") + " 00:00:00";
                map.put("startTime", startTime);
            } else {
                return RetResponse.makeErrRsp("没有startTime参数");
            }
            if (isBlank(map.get("endTime"))) {
                endTime = map.get("endTime") + " 24:00:00";
                map.put("endTime", endTime);
            } else {
                return RetResponse.makeErrRsp("没有endTime参数");
            }
            //返回内容
            Map returnMap = new HashMap<>();
            Map rate = new HashMap<>();
            List<Map> dateRate = new ArrayList<>();
            Map<String, Long> ls = new HashMap<>();
            int total = 0;
            String target = "";
            String QoQ = "";
            BigDecimal bfb = new BigDecimal(100);
            SimpleDateFormat g1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = new GregorianCalendar();
            //查询数据
            List<Map> lists = regionDao.complaintLists(map);
            total = lists.size();
            for(Map d : lists){
                try {
                    String dispatchingTime = d.get("dispatchingTime").toString().substring(0, 10);
                    ls.put(dispatchingTime + "t", ls.get(dispatchingTime + "t") == null ? 1 : ls.get(dispatchingTime + "t") + 1);
                    long longTime = 0;
                    Date start = g1.parse((String) d.get("complaintStartTime"));
                    Date end = g1.parse((String)d.get("complaintEndTime"));
                    longTime = end.getTime()-start.getTime();
                    if(d.get("exceptionDay")!=null){
                        Double ex = Double.parseDouble((String) d.get("exceptionDay"));
                        longTime = longTime -(long) (ex*24*60*60*1000);
                    }
                    if(longTime<=7*24*60*60*1000){
                        d.put("tt", "1");
                        ls.put(dispatchingTime + "e", ls.get(dispatchingTime + "e") == null ? 1 : ls.get(dispatchingTime + "e") + 1);
                    }

                }catch (Exception e){

                }
            }
            return RetResponse.makeOKRsp(returnMap);
        }catch (Exception e){
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }

    /**
     *一次修复率
     * @param map
     * @return
     */
    @Override
    public RetResult repair(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        BigDecimal bfb = new BigDecimal(100);
        //获取时间范围内率
        Map rate = regionDao.repair(map);
       if(rate==null){
            rate = new HashMap<>();
        }
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
            rate.put("rate","0");
        }else{
            BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
            BigDecimal last = new BigDecimal( rate.get("total").toString());
            rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
        }
        returnMap.put("rate",rate);
        //获取时间范围内日率
        List<Map> dateRepair =  regionDao.dateRepair(map);
        dateRepair = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateRepair);
        returnMap.put("dateRate",dateRepair);
        returnMap.put("target", getIndicator(map,"repairRate"));
        try {

            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                //开始时间结束时间月份-1
                map = lastMonth(map);
                Map lastRate = regionDao.repair(map);
                if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                try{
                    //上月分子
                    BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                    //上月分母
                    BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                    //本月率
                    BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                    //计算上月率
                    BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    //计算同比上月
                    BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }catch (Exception e){
                    returnMap.put("QOQ", "0");
                }

            }else{
                returnMap.put("QOQ", "");
                //returnMap.put("target", "");
            }
        }catch (Exception e){

        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *品类一次修复率柱图
     * @param map
     * @return
     */
    @Override
    public RetResult repairBar(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        List<Map> repairBar = regionDao.repairBar(map);
        returnMap.put("repairBar",repairBar);
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *一次修复率图表
     * @param map
     * @return
     */
    @Override
    public RetResult repairMap(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        BigDecimal bfb = new BigDecimal(100);
        //获取时间范围内率
        Map rate = regionDao.repair(map);
       if(rate==null){
            rate = new HashMap<>();
        }
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
            rate.put("rate","0");
        }else{
            BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
            BigDecimal last = new BigDecimal( rate.get("total").toString());
            rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
        }
        returnMap.put("rate",rate);
        //获取时间范围内日率
        List<Map> dateRepair =  regionDao.dateRepair(map);
        dateRepair = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateRepair);
        returnMap.put("dateRate",dateRepair);
        returnMap.put("target", getIndicator(map,"repairRate"));
        try {

            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                //开始时间结束时间月份-1
                map = lastMonth(map);
                //获取时间范围开始和结束时间减一个月日率
                List<Map> lastDateRepair =  regionDao.dateRepair(map);
                lastDateRepair = dateAll((String) map.get("startTime"), (String) map.get("endTime"),lastDateRepair);
                returnMap.put("lastDateRate",lastDateRepair);
                Map lastRate = regionDao.repair(map);
                if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                try{
                    //上月分子
                    BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                    //上月分母
                    BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                    //本月率
                    BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                    //计算上月率
                    BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    //计算同比上月
                    BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }catch (Exception e){
                    returnMap.put("QOQ", "0");
                }
            }else{
                returnMap.put("QOQ", "");
                //returnMap.put("target", "");
            }
        }catch (Exception e){

        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *2天维修达成率
     * @param map
     * @return
     */
    @Override
    public RetResult maintain(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        //获取时间范围内率
        Map maintain = regionDao.maintain(map);
        returnMap.put("rate",maintain);
        //获取时间范围内日率
        List<Map> dateMaintain =  regionDao.dateMaintain(map);
        dateMaintain = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateMaintain);
        returnMap.put("dateRate",dateMaintain);
        returnMap.put("target", getIndicator(map,"maintenanceRate"));
        try {

            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                //开始时间结束时间月份-1
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse((String) map.get("startTime")));
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                map.put("startTime",dateFormat.format(calendar.getTime()));
                calendar.setTime(dateFormat.parse((String) map.get("endTime")));
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                map.put("endTime",dateFormat.format(calendar.getTime()));
                //获取时间范围开始和结束时间减一个月日率
                /*List<Map> lastDateMaintain =  regionDao.dateMaintain(map);
                lastDateMaintain = dateAll((String) map.get("startTime"), (String) map.get("endTime"),lastDateMaintain);
                returnMap.put("lastDateRate",lastDateMaintain);*/
                Map lastMaintain = regionDao.maintain(map);
                if("0".equals(lastMaintain.get("rate"))){
                    returnMap.put("QOQ", "0");
                }else {
                    BigDecimal now =new BigDecimal((String) maintain.get("rate")) ;
                    BigDecimal last = new BigDecimal((String) lastMaintain.get("rate"));
                    BigDecimal bfb = new BigDecimal(100);
                    BigDecimal QOQ = ((now.subtract(last)).multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }


            }else{
                returnMap.put("QOQ", "");
                //returnMap.put("target", "");
            }
        }catch (Exception e){

        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *2天维修达成率图表
     * @param map
     * @return
     */
    @Override
    public RetResult maintainMap(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        //获取时间范围内率
        Map maintain = regionDao.maintain(map);
        returnMap.put("rate",maintain);
        //获取时间范围内日率
        List<Map> dateMaintain =  regionDao.dateMaintain(map);
        dateMaintain = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateMaintain);
        returnMap.put("dateRate",dateMaintain);
        returnMap.put("target", getIndicator(map,"maintenanceRate"));
        try {

            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                //开始时间结束时间月份-1
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse((String) map.get("startTime")));
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                map.put("startTime",dateFormat.format(calendar.getTime()));
                calendar.setTime(dateFormat.parse((String) map.get("endTime")));
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                map.put("endTime",dateFormat.format(calendar.getTime()));
                //获取时间范围开始和结束时间减一个月日率
                List<Map> lastDateMaintain =  regionDao.dateMaintain(map);
                lastDateMaintain = dateAll((String) map.get("startTime"), (String) map.get("endTime"),lastDateMaintain);
                returnMap.put("lastDateRate",lastDateMaintain);
                Map lastMaintain = regionDao.maintain(map);
                if("0".equals(lastMaintain.get("rate"))){
                    returnMap.put("QOQ", "0");
                }else {
                    BigDecimal now =new BigDecimal((String) maintain.get("rate")) ;
                    BigDecimal last = new BigDecimal((String) lastMaintain.get("rate"));
                    BigDecimal bfb = new BigDecimal(100);
                    BigDecimal QOQ = ((now.subtract(last)).multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }

            }else{
                returnMap.put("QOQ", "");
                //returnMap.put("target", "");
            }
        }catch (Exception e){

        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *N+1投诉解决方案提交率
     * @param map
     * @return
     */
    @Override
    public RetResult scheme(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        BigDecimal bfb = new BigDecimal(100);
        //获取时间范围内率
        Map rate = regionDao.scheme(map);
       if(rate==null){
            rate = new HashMap<>();
        }
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
            rate.put("rate","0");
        }else{
            BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
            BigDecimal last = new BigDecimal( rate.get("total").toString());
            rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
        }
        returnMap.put("rate",rate);
        //获取时间范围内日率
        List<Map> dateScheme =  regionDao.dateScheme(map);
        dateScheme = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateScheme);
        returnMap.put("dateRate",dateScheme);
        returnMap.put("target", getIndicator(map,"subRate"));
        try {

            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                //开始时间结束时间月份-1
                map = lastMonth(map);
                //获取时间范围开始和结束时间减一个月日率
               /* List<Map> lastDateScheme =  regionDao.dateScheme(map);
                lastDateScheme = dateAll((String) map.get("startTime"), (String) map.get("endTime"),lastDateScheme);
                returnMap.put("lastDateRate",lastDateScheme);*/
                Map lastRate = regionDao.scheme(map);
                if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                try{
                    //上月分子
                    BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                    //上月分母
                    BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                    //本月率
                    BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                    //计算上月率
                    BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    //计算同比上月
                    BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }catch (Exception e){
                    returnMap.put("QOQ", "0");
                }

            }else{
                returnMap.put("QOQ", "");
                //returnMap.put("target", "");
            }
        }catch (Exception e){

        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     *N+1投诉解决方案提交率图表
     * @param map
     * @return
     */
    @Override
    public RetResult schemeMap(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        BigDecimal bfb = new BigDecimal(100);
        //获取时间范围内率
        Map rate = regionDao.scheme(map);
       if(rate==null){
            rate = new HashMap<>();
        }
        if(rate.get("eligible")==null||rate.get("total")==null||"0".equals(rate.get("eligible"))||"0".equals(rate.get("total"))){
            rate.put("rate","0");
        }else{
            BigDecimal now =new BigDecimal(rate.get("eligible").toString()) ;
            BigDecimal last = new BigDecimal( rate.get("total").toString());
            rate.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
        }
        returnMap.put("rate",rate);
        //获取时间范围内日率
        List<Map> dateScheme =  regionDao.dateScheme(map);
        dateScheme = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateScheme);
        returnMap.put("dateRate",dateScheme);
        returnMap.put("target", getIndicator(map,"subRate"));
        try {

            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                //开始时间结束时间月份-1
                map = lastMonth(map);
                //获取时间范围开始和结束时间减一个月日率
                List<Map> lastDateScheme =  regionDao.dateScheme(map);
                lastDateScheme = dateAll((String) map.get("startTime"), (String) map.get("endTime"),lastDateScheme);
                returnMap.put("lastDateRate",lastDateScheme);
                Map lastRate = regionDao.scheme(map);
                if(lastRate==null){
                    lastRate = new HashMap<>();
                }
                try{
                    //上月分子
                    BigDecimal now =new BigDecimal(lastRate.get("eligible").toString()) ;
                    //上月分母
                    BigDecimal last = new BigDecimal(lastRate.get("total").toString());
                    //本月率
                    BigDecimal by =new BigDecimal(rate.get("rate").toString()) ;
                    //计算上月率
                    BigDecimal sy = (now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    //计算同比上月
                    BigDecimal QOQ = ((by.subtract(sy)).multiply(bfb).divide(sy,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }catch (Exception e){
                    returnMap.put("QOQ", "0");
                }

            }else{
                returnMap.put("QOQ", "");
                //returnMap.put("target", "");
            }
        }catch (Exception e){

        }
        return RetResponse.makeOKRsp(returnMap);
    }
    /**
     * 排名
     * @param map
     * @return
     */
    @Override
    public RetResult ranking(Map map) {
        //处理日期
        map = dateConversion(map);
        Map ranking = new HashMap();
        if(map.get("from")==null||"".equals(map.get("from"))){
            return RetResponse.makeErrRsp("from参数不能为空");
        }
        String from = (String) map.get("from");
        String tabName = (String) map.get("tabName");
        if("换件维修TAT".equals(tabName)){
            map.put("serviceType","维修");
            map.put("maintainWay","调换");
        }else if("不换件维修TAT".equals(tabName)){
            map.put("serviceType","维修");
            map.put("maintainWay","检修");
        }else if("快速安装TAT".equals(tabName)){
            map.put("serviceType","安装");
        }else if("鉴定TAT".equals(tabName)){
            map.put("serviceType","鉴定");
        }else if("非上门服务TAT".equals(tabName)){
            map.put("serviceType","非上门维修");
        }else if("首次预约准时上门率".equals(tabName)){
            from = "firstPunctualityPercentage";
            map.put("from",from);
        }

        String toal = from.replace("Percentage","Total");
        try{
            if(map.get("provincesCode")==null||map.get("provincesCode").toString().trim().length()==0) {
                map.put("FIELDS", "da.adminName,rdd.accountingAreaCode,rdd.accountingArea");
                map.put("GROUPFIELDS","rdd.accountingAreaCode");
                List<Map> areaList = new ArrayList<>();
                if("averagePercentage".equals(from)){
                    areaList = regionDao.indexListT(map);
                }else if("solvePercentage".equals(from)||"schemePercentage".equals(from)){
                    map.put("FIELDS", "da.adminName,ch.accountingAreaCode,ch.accountingArea");
                    map.put("GROUPFIELDS","ch.accountingAreaCode");
                    areaList = regionDao.indexListS(map);
                }else {
                    areaList = regionDao.indexList(map);
                }
                areaList = areaList.stream().filter(a -> a.get("accountingArea")!=null&&!"".equals(a.get("accountingArea"))).collect(Collectors.toList());
                areaList = areaList.stream().filter(a->a.get(toal)!=null&&!"0".equals(a.get(toal).toString())&&!"0.0".equals(a.get(toal).toString())).collect(Collectors.toList());
                for (Map map1 : areaList) {
                    map1.put("vv1", NullToZero(map1.get(from)));
                }
                if("1".equals(map.get("sort"))){
                    areaList.sort(Comparator.comparing((Map m) -> (Double.parseDouble(m.get("vv1")==null?"-1":m.get("vv1").toString()))));
                }else {
                    areaList.sort(Comparator.comparing((Map m) -> (Double.parseDouble(m.get("vv1")==null?"-1":m.get("vv1").toString()))).reversed());
                }
                ranking.put("areaList", areaList);
            }
            map.put("FIELDS","rdd.storeNumber,rdd.storeName,rdd.accountingCenterCode,da.adminName,rdd.accountingArea");
            map.put("GROUPFIELDS","rdd.storeNumber,rdd.accountingCenterCode");
            List<Map> storeList = new ArrayList<>();
            if("averagePercentage".equals(from)){
                storeList = regionDao.indexListT(map);
            }else if("solvePercentage".equals(from)||"schemePercentage".equals(from)){
                map.put("FIELDS",map.get("FIELDS").toString().replace("rdd.","ch."));
                map.put("GROUPFIELDS",map.get("GROUPFIELDS").toString().replace("rdd.","ch."));
                storeList = regionDao.indexListS(map);
            }else {
                storeList = regionDao.indexList(map);
            }
            storeList = storeList.stream().filter(a -> a.get("storeName")!=null&&!"".equals(a.get("storeName"))).collect(Collectors.toList());
            storeList = storeList.stream().filter(a->a.get(toal)!=null&&!"0".equals(a.get(toal).toString())&&!"0.0".equals(a.get(toal).toString())).collect(Collectors.toList());
            for(Map map1:storeList){
                map1.put("vv1",NullToZero(map1.get(from)));
            }
            if("1".equals(map.get("sort"))){
                storeList.sort(Comparator.comparing((Map m) -> (Double.parseDouble(m.get("vv1")==null?"-1":m.get("vv1").toString()))));
            }else {
                storeList.sort(Comparator.comparing((Map m) -> (Double.parseDouble(m.get("vv1")==null?"-1":m.get("vv1").toString()))).reversed());
            }
            ranking.put("storeList",storeList);
            return RetResponse.makeOKRsp(ranking);
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("参数错误");
        }

    }
    /**
     * 地图数据
     * @param map
     * @return
     */
    @Override
    public RetResult mapData(Map map) {
        map = dateConversion(map);
        String from = (String) map.get("from");
        List<Map> m = regionDao.getMapDate();
        Map mp = new HashMap<>();
        for (Map p : m){
            mp.put(p.get("areaCode"),p.get("areaName"));
        }
        String key = "";
        String NOTNULL = "provincesCode";
        if(map.get("provincesCode")==null||"".equals(map.get("provincesCode"))){
            key = "provincesCode";
            map.put("FIELDS","rdd.provincesCode");
            NOTNULL = "provincesCode";

        }else{
            if("11".equals(map.get("provincesCode"))||"12".equals(map.get("provincesCode"))||"31".equals(map.get("provincesCode"))||"50".equals(map.get("provincesCode"))){
                key = "countyCode";
                map.put("FIELDS","rdd.countyCode");
                NOTNULL = "countyCode";
            }else {
                key = "cityCode";
                map.put("FIELDS","rdd.cityCode");
                //map.put("GROUPFIELDS","rdd.cityCode");
                NOTNULL = "cityCode";
            }

        }

        String tabName = (String) map.get("tabName");
        if("换件维修TAT".equals(tabName)){
            map.put("serviceType","维修");
            map.put("maintainWay","调换");
        }else if("不换件维修TAT".equals(tabName)){
            map.put("serviceType","维修");
            map.put("maintainWay","检修");
        }else if("快速安装TAT".equals(tabName)){
            map.put("serviceType","安装");
        }else if("鉴定TAT".equals(tabName)){
            map.put("serviceType","鉴定");
        }else if("非上门服务TAT".equals(tabName)){
            map.put("serviceType","非上门维修");
        }else if("首次预约准时上门率".equals(tabName)){
            from = "firstPunctualityPercentage";
            map.put("from",from);
        }
        List<Map> indexList = new ArrayList<>();
        if("averagePercentage".equals(from)){
            indexList = regionDao.indexListT(map);
        }else if("solvePercentage".equals(from)||"schemePercentage".equals(from)){
            map.put("FIELDS",map.get("FIELDS").toString().replace("rdd.","ch."));
            indexList = regionDao.indexListS(map);
        }else {
            indexList = regionDao.indexList(map);
        }
        String finalNOTNULL = NOTNULL;
        //地图数据
        indexList = indexList.stream().filter(a -> a.get(finalNOTNULL)!=null&&!"".equals(a.get(finalNOTNULL))).collect(Collectors.toList());

        Map<String, Object> targeMap = new HashMap<>();
        targeMap.put("endTime",  map.get("endTime"));
        Map<String, String> indicatorName = new HashMap<>();
        indicatorName.put("timelyPercentage","timelyRatw");
        indicatorName.put("firstPunctualityPercentage","firstPunctualRate");
        //tat
        indicatorName.put("punctualityPercentage","secondPunctualRate");
        indicatorName.put("averagePercentage","avgTimeDay");
        indicatorName.put("solvePercentage","resolutionRate");
        indicatorName.put("repairPercentage","repairRate");
        indicatorName.put("schemePercentage","subRate");
        //达标判断
        String finalFrom = from;
        indexList.stream().forEach(e->{
            if("averagePercentage".equals(finalFrom)){
                if(!"".equals(getIndicator(targeMap, "avgTimeDay"))) {
                    if (new BigDecimal(NullToZero(e.get("average"))).
                            compareTo(new BigDecimal(NullToZero(getIndicator(targeMap, indicatorName.get(finalFrom))))) > 0) {
                        e.put("target", "0");
                    } else {
                        e.put("target", "1");
                    }
                }else {
                    e.put("target","1");
                }
            }else{
                if(new BigDecimal(NullToZero(e.get(finalFrom))).
                        compareTo(new BigDecimal(NullToZero(getIndicator(targeMap, indicatorName.get(finalFrom)))))>=0){
                    e.put("target","1");
                }else {
                    e.put("target","0");
                }
            }
        });
        //地图右下角占比
        List<Map> mapList = new ArrayList<>();
        int v1 = 0;
        int v2 = 0;
        int v3 = 0;
        int v4 = 0;
        if(indexList!=null){
            for (Map map1:indexList) {
                Map map2 = new HashMap<>();
                String[] strs = new String[3];
                strs[0] = (String) map1.get("longitude");
                strs[1] = (String) map1.get("latitude");
                DecimalFormat g2 = new DecimalFormat("0.00000");
                if (map.get("provincesCode") == null || "".equals(map.get("provincesCode"))) {
                    String code = g2.format(Integer.valueOf((String) map1.get(key))).replace(".", "").substring(0, 6);
                    String provinces = (String) mp.get(code);
                    if (provinces != null && (provinces.indexOf("内蒙古") != -1 || provinces.indexOf("黑龙江") != -1)) {
                        provinces = provinces.substring(0, 3);
                    } else if (provinces != null && provinces.length() > 2) {
                        provinces = provinces.substring(0, 2);
                    }
                    map2.put("name", provinces);
                } else {
                    String code = g2.format(Integer.valueOf((String) map1.get(key))).replace(".", "").substring(0, 6);
                    map2.put("name", mp.get(code));
                }
                if ("averagePercentage".equals(from)) {
                    if ("".equals(String.valueOf(map1.get("average")))) {
                        continue;
                    }
                    strs[2] = map1.get("average").toString();
                    Double vv = Double.parseDouble(strs[2]);
                    if (vv < 24 && map2.get("name") != null) {
                        v1++;
                    } else if (vv <= 3 * 24  && map2.get("name") != null) {
                        v2++;
                    } else if (vv <= 7 * 24  && map2.get("name") != null) {
                        v3++;
                    } else if (vv > 7 * 24  && map2.get("name") != null) {
                        v4++;
                    }
                } else if (from != null && !"".equals(from)) {
                    if ("".equals(String.valueOf(map1.get(from)))) {
                        continue;
                    }
                    strs[2] = (String) map1.get(from);
                    Double vv = Double.parseDouble(NullToZero(strs[2]));
                    if (vv < 80  && map2.get("name") != null) {
                        v1++;
                    } else if (vv <= 90 && map2.get("name") != null) {
                        v2++;
                    } else if (vv <= 95 && map2.get("name") != null) {
                        v3++;
                    } else if (vv <= 100 && map2.get("name") != null) {
                        v4++;
                    }
                }

                map2.put("value", strs[2] + "");
                map2.put("target", map1.get("target"));
                if (map2.get("name") != null) {
                    mapList.add(map2);
                }
            }
        }
        List<Map> statistics = new ArrayList<>();
        BigDecimal h = new BigDecimal(100);
        BigDecimal a ;
        BigDecimal b = new BigDecimal(mapList.size());
        if("averagePercentage".equals(from)){
            Map a1 = new HashMap();
            a1.put("person","小于1天");
            a = new BigDecimal(v1);
            a1.put("value",(mapList.size()==0?0:a.multiply(h).divide(b,1,BigDecimal.ROUND_HALF_UP).toString()+"%"));
            statistics.add(a1);
            Map a2 = new HashMap();
            a2.put("person","1天~3天");
            a = new BigDecimal(v2);
            a2.put("value",(mapList.size()==0?0:a.multiply(h).divide(b,1,BigDecimal.ROUND_HALF_UP).toString()+"%"));
            statistics.add(a2);
            Map a3 = new HashMap();
            a3.put("person","4天~7天");
            a = new BigDecimal(v3);
            a3.put("value",(mapList.size()==0?0:a.multiply(h).divide(b,1,BigDecimal.ROUND_HALF_UP).toString()+"%"));
            statistics.add(a3);
            Map a4 = new HashMap();
            a4.put("person","7天以上");
            a = new BigDecimal(v4);
            a4.put("value",(mapList.size()==0?0:a.multiply(h).divide(b,1,BigDecimal.ROUND_HALF_UP).toString()+"%"));
            statistics.add(a4);
        }else{
            Map a1 = new HashMap();
            a1.put("person","<80%");
            a = new BigDecimal(v1);
            a1.put("value",(mapList.size()==0?0:a.multiply(h).divide(b,1,BigDecimal.ROUND_HALF_UP).toString()+"%"));
            statistics.add(a1);
            Map a2 = new HashMap();
            a2.put("person","80%~90%");
            a = new BigDecimal(v2);
            a2.put("value",(mapList.size()==0?0:a.multiply(h).divide(b,1,BigDecimal.ROUND_HALF_UP).toString()+"%"));
            statistics.add(a2);
            Map a3 = new HashMap();
            a3.put("person","91%~95%");
            a = new BigDecimal(v3);
            a3.put("value",(mapList.size()==0?0:a.multiply(h).divide(b,1,BigDecimal.ROUND_HALF_UP).toString()+"%"));
            statistics.add(a3);
            Map a4 = new HashMap();
            a4.put("person","96%~100%");
            a = new BigDecimal(v4);
            a4.put("value",(mapList.size()==0?0:a.multiply(h).divide(b,1,BigDecimal.ROUND_HALF_UP).toString()+"%"));
            statistics.add(a4);
        }
        Map returnMap = new HashMap<>();
        returnMap.put("mapList",mapList);
        returnMap.put("statistics",statistics);
        return RetResponse.makeOKRsp(returnMap);
    }

    /**
     * 大区管理列表
     * @param map
     * @return
     */
    @Override
    public RetResult regionManage(Map map) {
        JSONObject resJson = new JSONObject();
        try {
            map = dateConversion(map);
            map.put("FIELDS","rss.accountingCenter,rdd.accountingAreaCode,rdd.accountingArea,da.adminName,COUNT(1) AS amount");
            map.put("GROUPFIELDS","rdd.accountingAreaCode");
            Map finalMap = map;
            Future<List<Map>> submit1 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list = regionDao.indexList(finalMap);
                list = list.stream().filter(a -> a.get("accountingArea")!=null&&!"".equals(a.get("accountingArea"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("average","");
                    a.put("averagePercentage","");
                    a.put("avgDay","");
                    a.put("solvePercentage","");
                });
                return list;
            });
            Future<List<Map>> submit2 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list = regionDao.indexListT(finalMap);
                list = list.stream().filter(a -> a.get("accountingArea")!=null&&!"".equals(a.get("accountingArea"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("timelyPercentage","");
                    a.put("firstPunctualityPercentage","");
                    a.put("solvePercentage","");
                    a.put("repairPercentage","");
                });
                return list;
            });
            Future<List<Map>> submit3 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                Map finalMap1 = new HashMap<>(finalMap);
                finalMap1.put("FIELDS","rss.accountingCenter,ch.accountingAreaCode,ch.accountingArea,da.adminName,COUNT(1) AS amount");
                finalMap1.put("GROUPFIELDS","ch.accountingAreaCode");
                List<Map> list = regionDao.indexListS(finalMap1);
                list = list.stream().filter(a -> a.get("accountingArea")!=null&&!"".equals(a.get("accountingArea"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("timelyPercentage","");
                    a.put("firstPunctualityPercentage","");
                    a.put("repairPercentage","");
                    a.put("average","");
                    a.put("averagePercentage","");
                    a.put("avgDay","");
                });
                return list;
            });
            List<Map> indexList = submit1.get();
            List<Map> indexListT = submit2.get();
            List<Map> indexListS = submit3.get();
            List<Map> finalIndexList = indexList;
            indexList = indexListT.stream().map(a -> finalIndexList.stream()
                            .filter(b -> b.get("accountingAreaCode").toString().equals(a.get("accountingAreaCode")))
                            .findFirst().map(b -> {
                                b.put("average",a.get("average"));
                                b.put("averagePercentage",a.get("averagePercentage"));
                                b.put("avgDay",a.get("avgDay"));
                                b.put("tt",1);
                                return b;
                            }).orElse(a))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            List<Map> a = finalIndexList.stream().filter(m->m.get("tt")==null).collect(Collectors.toList());
            indexList.addAll(a);
            if(CollectionUtils.isNotEmpty(indexListS)) {
//                List<Map> finalIndexList1 = indexList;
//                indexList = indexListS.stream().map(c -> finalIndexList1.stream()
//                                .filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode").toString()) && b.get("storeNumber").toString().equals(c.get("storeNumber").toString()) && b.get("engineerId").toString().equals(c.get("engineerId").toString()))
//                                .findFirst().map(b -> {
//                                    b.put("solveEligible", c.get("solveEligible"));
//                                    b.put("solveTotal", c.get("solveTotal"));
//                                    b.put("solvePercentage", c.get("solvePercentage"));
//                                    b.put("ttt", 1);
//                                    return b;
//                                }).orElse(c))
//                        .filter(Objects::nonNull).collect(Collectors.toList());
                for(Map c:indexListS){
                    Optional<Map> i = indexList.stream().filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode"))).findFirst();
                    if(i.isPresent()){
                        Map b = i.get();
                        b.put("solveEligible", c.get("solveEligible"));
                        b.put("solveTotal", c.get("solveTotal"));
                        b.put("solvePercentage", c.get("solvePercentage"));
                    }else {
                        indexList.add(c);
                    }
                }
//                List<Map> b = finalIndexList1.stream().filter(m -> m.get("ttt") == null).collect(Collectors.toList());
//                indexList.addAll(b);
            }
//            List<Map> finalIndexList1 = indexList;
//            indexList = indexListS.stream().map(c -> finalIndexList1.stream()
//                            .filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode")))
//                            .findFirst().map(b -> {
//                                b.put("solveEligible",c.get("solveEligible"));
//                                b.put("solveTotal",c.get("solveTotal"));
//                                b.put("solvePercentage",c.get("solvePercentage"));
//                                b.put("ttt",1);
//                                return b;
//                            }).orElse(c))
//                    .filter(Objects::nonNull).collect(Collectors.toList());
//            List<Map> b = finalIndexList1.stream().filter(m->m.get("ttt")==null).collect(Collectors.toList());
//            indexList.addAll(b);
            indexList = fieldQuery(map,indexList);
            resJson.put("count", indexList.size());
            Integer page = (Integer)map.get("page");
            if(page==null){
                page = 1;
            }
            int end = (page-1)*10+10;
            if(end>indexList.size()){
                end = indexList.size();
            }
            indexList = indexList.subList((page-1)*10, end);
            listToList(indexList,map);
            resJson.put("data", indexList);

        }catch (Exception e){
            RetResponse.makeErrRsp(e.getMessage());
            e.printStackTrace();
        }
        return RetResponse.makeOKRsp(resJson);
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


    public List<Map> listToList(List<Map> indexList,Map map){
        for (Map newMap:indexList
        ) {
            Map<String, Object> targeMap = new HashMap<>();
            targeMap.put("endTime",map.get("endTime"));
            targeMap.put("accountingAreaCode",newMap.get("accountingAreaCode"));
            // 判断分子是否为0 为0就设值为正常
            if(newMap.get("timelyPercentage")!=null &&!"".equals(newMap.get("timelyPercentage").toString())){
                if(new BigDecimal(NullToZero(newMap.get("timelyPercentage"))).
                        compareTo(new BigDecimal(NullToZero(getIndicator(targeMap, "timelyRatw"))))>=0){
                    newMap.put("timelyPercentageState","1");
                }else {
                    newMap.put("timelyPercentageState","0");
                }
            }else {
                newMap.put("timelyPercentageState","1");
            }
            // 判断分子是否为0 为0就设值为正常
            if(newMap.get("firstPunctualityPercentage")!=null &&!"".equals(newMap.get("firstPunctualityPercentage").toString())){
                if(new BigDecimal(NullToZero(newMap.get("firstPunctualityPercentage"))).
                        compareTo(new BigDecimal(NullToZero(getIndicator(targeMap, "firstPunctualRate"))))>=0){
                    newMap.put("firstPunctualityPercentageState","1");
                }else {
                    newMap.put("firstPunctualityPercentageState","0");
                }
            }else {
                newMap.put("firstPunctualityPercentageState","1");
            }
            // 判断分子是否为0或者目标是否为空 为0就设值为正常
            if(newMap.get("average")!=null &&!"".equals(getIndicator(targeMap, "avgTimeDay")) && !"".equals(newMap.get("average").toString())){
                if(new BigDecimal(NullToZero(newMap.get("average"))).
                        compareTo(new BigDecimal(NullToZero(getIndicator(targeMap, "avgTimeDay"))))>0){
                    newMap.put("averageState","0");
                }else {
                    newMap.put("averageState","1");
                }
            }else {
                newMap.put("averageState","1");
            }

            // 判断分子是否为0 为0就设值为正常
            if(newMap.get("solvePercentage")!=null && !"".equals(newMap.get("solvePercentage").toString())){
                if(new BigDecimal(NullToZero(newMap.get("solvePercentage"))).
                        compareTo(new BigDecimal(NullToZero(getIndicator(targeMap, "resolutionRate"))))>=0){
                    newMap.put("solvePercentageState","1");
                }else {
                    newMap.put("solvePercentageState","0");
                }
            }else {
                newMap.put("solvePercentageState","1");
            }

            // 判断分子是否为0 为0就设值为正常
            if(newMap.get("repairPercentage")!=null &&!"".equals(newMap.get("repairPercentage").toString())){
                if(new BigDecimal(NullToZero(newMap.get("repairPercentage"))).
                        compareTo(new BigDecimal(NullToZero(getIndicator(targeMap, "repairRate"))))>=0){
                    newMap.put("repairPercentageState","1");
                }else {
                    newMap.put("repairPercentageState","0");
                }
            }else {
                newMap.put("repairPercentageState","1");
            }
            // 判断分子是否为0 为0就设值为正常
            if(newMap.get("averagePercentage")!=null &&!"".equals(newMap.get("averagePercentage").toString())){
                if(new BigDecimal(NullToZero(newMap.get("averagePercentage"))).
                        compareTo(new BigDecimal(NullToZero(getIndicator(targeMap, "avgTimeRate"))))>=0){
                    newMap.put("averagePercentageState","1");
                }else {
                    newMap.put("averagePercentageState","0");
                }
            }else {
                newMap.put("averagePercentageState","1");
            }
        }
        return indexList;
    }

    /**
     * 工程师管理列表
     * @param map
     * @return
     */
    @Override
    public RetResult engineerManage(Map map) {
        JSONObject resJson = new JSONObject();
        try {
            map = dateConversion(map);
            map.put("FIELDS","rdd.engineerId,rdd.engineerName,rdd.accountingAreaCode,rdd.accountingArea,rdd.storeName,rdd.storeNumber,rdd.accountingCenterCode,COUNT(1) AS amount");
            map.put("GROUPFIELDS","rdd.engineerId,rdd.accountingAreaCode,rdd.storeNumber");
            Map finalMap = map;
            Map finalMap1 = new HashMap<>(map);
            Future<List<Map>> submit1 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                //查其他
                List<Map> list = regionDao.indexList(finalMap);
                list = list.stream().filter(a -> a.get("engineerName")!=null&&!"".equals(a.get("engineerName"))&&a.get("accountingAreaCode")!=null&&!"".equals(a.get("accountingAreaCode"))&&a.get("accountingAreaCode")!=null&&!"".equals(a.get("accountingAreaCode"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("average","");
                    a.put("averagePercentage","");
                    a.put("avgDay","");
                    a.put("solvePercentage","");
                });
                return list;
            });
            Future<List<Map>> submit2 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                //查TAT
                List<Map> list = regionDao.indexListT(finalMap);
                list = list.stream().filter(a -> a.get("engineerName")!=null&&!"".equals(a.get("engineerName"))&&a.get("accountingAreaCode")!=null&&!"".equals(a.get("accountingAreaCode"))&&a.get("accountingAreaCode")!=null&&!"".equals(a.get("accountingAreaCode"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("timelyPercentage","");
                    a.put("firstPunctualityPercentage","");
                    a.put("solvePercentage","");
                    a.put("repairPercentage","");
                    a.put("amount","");
                });
                return list;
            });
            Future<List<Map>> submit3 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                finalMap1.put("FIELDS","rdd.engineerId,rdd.engineerName,ch.accountingAreaCode,ch.accountingArea,ch.storeName,ch.storeNumber,ch.accountingCenterCode,COUNT(1) AS amount");
                finalMap1.put("GROUPFIELDS","rdd.engineerId,ch.accountingAreaCode,ch.storeNumber");
                //查投诉
                List<Map> list = regionDao.indexListS(finalMap1);
                list = list.stream().filter(a -> a.get("engineerName")!=null&&!"".equals(a.get("engineerName"))&&a.get("accountingAreaCode")!=null&&!"".equals(a.get("accountingAreaCode"))&&a.get("accountingAreaCode")!=null&&!"".equals(a.get("accountingAreaCode"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("timelyPercentage","");
                    a.put("firstPunctualityPercentage","");
                    a.put("repairPercentage","");
                    a.put("average","");
                    a.put("averagePercentage","");
                    a.put("avgDay","");
                    a.put("amount","");
                });
                return list;
            });
            List<Map> indexList = submit1.get();
            List<Map> indexListT = submit2.get();
            List<Map> indexListS = submit3.get();
            List<Map> finalIndexList = indexList;
            indexList = indexListT.stream().map(a -> finalIndexList.stream()
                            .filter(b -> b.get("accountingAreaCode").toString().equals(a.get("accountingAreaCode").toString())&&b.get("storeNumber").toString().equals(a.get("storeNumber").toString())&&b.get("engineerId").toString().equals(a.get("engineerId").toString()))
                            .findFirst().map(b -> {
                                b.put("average",a.get("average"));
                                b.put("averagePercentage",a.get("averagePercentage"));
                                b.put("avgDay",a.get("avgDay"));
                                b.put("tt",1);
                                return b;
                            }).orElse(a))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            List<Map> a = finalIndexList.stream().filter(m->m.get("tt")==null).collect(Collectors.toList());
            indexList.addAll(a);
            if(CollectionUtils.isNotEmpty(indexListS)) {
//                List<Map> finalIndexList1 = indexList;
//                indexList = indexListS.stream().map(c -> finalIndexList1.stream()
//                                .filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode").toString()) && b.get("storeNumber").toString().equals(c.get("storeNumber").toString()) && b.get("engineerId").toString().equals(c.get("engineerId").toString()))
//                                .findFirst().map(b -> {
//                                    b.put("solveEligible", c.get("solveEligible"));
//                                    b.put("solveTotal", c.get("solveTotal"));
//                                    b.put("solvePercentage", c.get("solvePercentage"));
//                                    b.put("ttt", 1);
//                                    return b;
//                                }).orElse(c))
//                        .filter(Objects::nonNull).collect(Collectors.toList());
                for(Map c:indexListS){
                    Optional<Map> i = indexList.stream().filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode").toString()) && b.get("storeNumber").toString().equals(c.get("storeNumber").toString()) && b.get("engineerId").toString().equals(c.get("engineerId").toString())).findFirst();
                    if(i.isPresent()){
                        Map b = i.get();
                        b.put("solveEligible", c.get("solveEligible"));
                        b.put("solveTotal", c.get("solveTotal"));
                        b.put("solvePercentage", c.get("solvePercentage"));
                    }else {
                        indexList.add(c);
                    }
                }
//                List<Map> b = finalIndexList1.stream().filter(m -> m.get("ttt") == null).collect(Collectors.toList());
//                indexList.addAll(b);
            }
            indexList = fieldQuery(map,indexList);
            resJson.put("count", indexList.size());
            Integer page = (Integer)map.get("page");
            if(page==null){
                page = 1;
            }
            int end = (page-1)*10+10;
            if(end>indexList.size()){
                end = indexList.size();
            }
            indexList = indexList.subList((page-1)*10, end);
            listToList(indexList,map);
            resJson.put("data", indexList);

        }catch (Exception e){
            RetResponse.makeErrRsp(e.getMessage());
            e.printStackTrace();
        }
        return RetResponse.makeOKRsp(resJson);
    }
    /**
     * 上门服务异常监控
     * @param map
     * @return
     */
    @Override
    public synchronized RetResult<Object> visitMonitoring1(Map map) {
        map = dateConversion(map);
        /*SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String nowDay = sdfd.format(now);
        String nowTime = sdft.format(now);
        map.put("WHERE","(rdd.serviceType = '维修' OR rdd.serviceType = '安装' OR (rdd.serviceType = '鉴定' AND (rdd.firstServiceType = '维修' OR rdd.firstServiceType = '安装')))");

        Map finalMap = map;
        List<Map> list = new ArrayList<>();
        List<Map> orderList = new ArrayList<>();
        try {

            Future<List<Map>> submit1 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list1 = regionDao.allList(finalMap);
                return list1;
            });
            Future<List<Map>> submit2 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list1 = regionDao.orderList(finalMap);
                return list1;
            });
             list = submit1.get();
            orderList = submit2.get();
        }catch (Exception e){
            e.printStackTrace();
        }

        //仅显示投诉单筛选
        *//*if(list!=null&&map!=null&&map.get("isComplaint")!=null&&"1".equals(map.get("isComplaint"))){
            List<String> isComplaint = regionDao.isComplaint(map);
            list = list.stream().filter(o -> isComplaint.contains(o.get("dispatchingOrder"))).collect(Collectors.toList());
        }*//*
        //仅异常单筛选
        if(list!=null&&map!=null&&map.get("isErr")!=null&&"1".equals(map.get("isErr"))) {
            list = list.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        }
        Map fuWuTiJiaoMap = new HashMap<>();
        fuWuTiJiaoMap.put("title","服务提交");
        //筛选提交时间不为空的为服务提交
        List<Map> childList = list.stream().filter(o -> o.get("submissionTime")!=null).collect(Collectors.toList());
        //删除服务提交单，防止后续重复统计
        //list.removeAll(childList);
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("submissionTime")==null).collect(Collectors.toList());

        //筛选出的记录条数为单数
        fuWuTiJiaoMap.put("count",childList==null?0:childList.size());
        List<Map> errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        fuWuTiJiaoMap.put("outTimeCount",errList==null?0:errList.size());
        //求完成时间到提交时间的平均分钟数
        long diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("finishTime"),s.get("submissionTime"),sdft)).sum();
        fuWuTiJiaoMap.put("diffMinutes",diffMinutes);


        Map fuWuWanChengMap = new HashMap<>();
        fuWuWanChengMap.put("title","服务完成");
        //筛选完成时间不为空的为服务完成
        childList = list.stream().filter(o -> o.get("finishTime")!=null).collect(Collectors.toList());
        //删除服务完成单，防止后续重复统计
        //list.removeAll(childList);
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("finishTime")==null).collect(Collectors.toList());
        fuWuWanChengMap.put("count",childList==null?0:childList.size());
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        fuWuWanChengMap.put("outTimeCount",errList==null?0:errList.size());
        //求首次上门时间到完成时间减去挂单时长的平均分钟数
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("firstVisitTime"),s.get("finishTime"),sdft)-(s.get("pendingTime")==null?0L:Long.parseLong(s.get("pendingTime").toString())/60)).sum();
        fuWuWanChengMap.put("diffMinutes",diffMinutes);

        Map jieGuaMap = new HashMap<>();
        jieGuaMap.put("title","解挂");
        //筛选挂单状态等于解挂的为解挂
        childList = list.stream().filter(o -> "解挂".equals(o.get("pendingState"))).collect(Collectors.toList());
        //删除解挂单，防止后续重复统计
        //list.removeAll(childList);
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> !"解挂".equals(o.get("pendingState"))).collect(Collectors.toList());
        jieGuaMap.put("count",childList==null?0:childList.size());
        diffMinutes = childList.stream().mapToLong(s ->s.get("pendingTime")==null?0L:Long.parseLong(s.get("pendingTime").toString())/60).sum();
        jieGuaMap.put("diffMinutes",diffMinutes);
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        jieGuaMap.put("outTimeCount",errList==null?0:errList.size());

        Map lingJianMap = new HashMap<>();
        lingJianMap.put("title","零件供应");
        //查询作业订单


        //将作业订单的值赋值给派工单
        List<Map> finalOrderList = orderList;
        list.stream().forEach(e -> {
            Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
            if(i.isPresent()){
                e.put("orderStartTime",i.get().get("orderStartTime"));
                e.put("appropriateInvestTime",i.get().get("appropriateInvestTime"));
            }else {
                e.put("orderStartTime",null);
                e.put("appropriateInvestTime",null);
            }
        });

        //筛选到件时间不为空的为零件供应
        childList = list.stream().filter(o -> o.get("appropriateInvestTime")!=null).collect(Collectors.toList());
        //删除筛选单，防止后续重复统计
        //list.removeAll(childList);
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("appropriateInvestTime")==null).collect(Collectors.toList());
        lingJianMap.put("count",childList==null?0:childList.size());
        //求作业订单开始时间到到货签收时间的平均分钟数
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("orderStartTime"),s.get("appropriateInvestTime"),sdft)).sum();
        lingJianMap.put("diffMinutes",diffMinutes);
        //childList = childList.stream().filter(o -> stoelong(o.get("appropriateInvestTime")!=null&&o.get("appropriateInvestTime").toString().length()>10?o.get("appropriateInvestTime").toString().substring(0,10):o.get("appropriateInvestTime"),nowDay,sdfd)>1440).collect(Collectors.toList());
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        lingJianMap.put("outTimeCount",errList==null?0:errList.size());


        Map zuoYeMap = new HashMap<>();
        zuoYeMap.put("title","作业订单");
        //筛选作业订单开始时间不为空的为作业订单
        childList = list.stream().filter(o -> o.get("orderStartTime")!=null).collect(Collectors.toList());
        //删除筛选单，防止后续重复统计
        //list.removeAll(childList);
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
        zuoYeMap.put("count",childList==null?0:childList.size());
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        zuoYeMap.put("outTimeCount",errList==null?0:errList.size());
        zuoYeMap.put("diffMinutes",0);


        Map guaDanMap = new HashMap<>();
        guaDanMap.put("title","挂单");
        //筛选作业订单开始时间不为空的为作业订单
        childList = list.stream().filter(o -> "挂单".equals(o.get("pendingState"))).collect(Collectors.toList());
        //删除筛选单，防止后续重复统计
        //list.removeAll(childList);
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> !"挂单".equals(o.get("pendingState"))).collect(Collectors.toList());
        guaDanMap.put("count",childList==null?0:childList.size());
        guaDanMap.put("diffMinutes",0);
        //childList = childList.stream().filter(o -> stoelong(o.get("visitTime"),nowDay,sdfd)>1440).collect(Collectors.toList());
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        guaDanMap.put("outTimeCount",errList==null?0:errList.size());


        Map shangMenMap = new HashMap<>();
        shangMenMap.put("title","上门");
        //筛选作业订单开始时间不为空的为作业订单
        childList = list.stream().filter(o -> o.get("firstVisitTime")!=null).collect(Collectors.toList());
        //删除筛选单，防止后续重复统计
        //list.removeAll(childList);
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("firstVisitTime")==null).collect(Collectors.toList());
        shangMenMap.put("count",childList==null?0:childList.size());
        diffMinutes =  childList.stream().mapToLong(s ->stoelong(s.get("appointmentOperationTime"),s.get("firstVisitTime"),sdft)).sum();
        shangMenMap.put("diffMinutes",diffMinutes);
        //childList = childList.stream().filter(o -> stoelong(o.get("visitTime"),nowDay,sdfd)>1440).collect(Collectors.toList());
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        shangMenMap.put("outTimeCount",errList==null?0:errList.size());


        Map yuYueMap = new HashMap<>();
        yuYueMap.put("title","预约");
        //筛选预约时间
        childList = list.stream().filter(o -> o.get("appointmentOperationTime")!=null).collect(Collectors.toList());
        //删除筛选单，防止后续重复统计
        //list.removeAll(childList);
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("appointmentOperationTime")==null).collect(Collectors.toList());
        yuYueMap.put("count",childList==null?0:childList.size());
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("receiptTime"),s.get("appointmentOperationTime"),sdft)).sum();
        yuYueMap.put("diffMinutes",diffMinutes);

        //派工时间到当前时间超过30分钟的超时预警
        //childList = list.stream().filter(o -> stoelong(o.get("dispatchingTime"),nowTime,sdft)>30L).collect(Collectors.toList());
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        yuYueMap.put("outTimeCount",errList==null?0:errList.size());


        Map jieDanMap = new HashMap<>();
        jieDanMap.put("title","接单");
        //筛选接收时间
        childList = list.stream().filter(o -> o.get("receiptTime")!=null).collect(Collectors.toList());
        //删除筛选单，防止后续重复统计
        //list.removeAll(childList);
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("receiptTime")==null).collect(Collectors.toList());
        jieDanMap.put("count",childList==null?0:childList.size());
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        jieDanMap.put("outTimeCount",errList==null?0:errList.size());
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("dispatchingTime"),s.get("receiptTime"),sdft)).sum();
        jieDanMap.put("diffMinutes",diffMinutes);


        Map paiGongMap = new HashMap<>();
        paiGongMap.put("title","派工");
        //剩下的都给派工
        paiGongMap.put("count",list==null?0:list.size());
        paiGongMap.put("outTimeCount",0);
        paiGongMap.put("diffMinutes",0);*/
        List<Map> zzlist = regionDao.visitMonitoring(map);
        // 上门服务异常监控
        map.put("serviceWay","1");
        Integer outTimeCount = regionDao.getOutTimeCount(map);

        Map paiGongMap = new HashMap<>();
        Optional<Map> i = zzlist.stream().filter(x->x.get("title")!=null && "派工".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            paiGongMap=i.get();
        }else {
            paiGongMap.put("title","派工");
            paiGongMap.put("count",0);
            paiGongMap.put("outTimeCount",0);
            paiGongMap.put("diffMinutes",0);
        }

        Map jieDanMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "接单".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            jieDanMap=i.get();
        }else {
            jieDanMap.put("title","接单");
            jieDanMap.put("count",0);
            jieDanMap.put("outTimeCount",0);
            jieDanMap.put("diffMinutes",0);
        }
        Map yuYueMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "预约".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            yuYueMap=i.get();
        }else {
            yuYueMap.put("title","预约");
            yuYueMap.put("count",0);
            yuYueMap.put("outTimeCount",0);
            yuYueMap.put("diffMinutes",0);
        }

        Map shangMenMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "上门".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            shangMenMap=i.get();
        }else {
            shangMenMap.put("title","上门");
            shangMenMap.put("count",0);
            shangMenMap.put("outTimeCount",0);
            shangMenMap.put("diffMinutes",0);
        }

        Map guaDanMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "挂单".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            guaDanMap=i.get();
        }else {
            guaDanMap.put("title","挂单");
            guaDanMap.put("count",0);
            guaDanMap.put("outTimeCount",0);
            guaDanMap.put("diffMinutes",0);
        }


        Map zuoYeMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "作业订单".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            zuoYeMap=i.get();
        }else {
            zuoYeMap.put("title","作业订单");
            zuoYeMap.put("count",0);
            zuoYeMap.put("outTimeCount",0);
            zuoYeMap.put("diffMinutes",0);
        }

        Map lingJianMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "零件供应".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            lingJianMap=i.get();
        }else {
            lingJianMap.put("title","零件供应");
            lingJianMap.put("count",0);
            lingJianMap.put("outTimeCount",0);
            lingJianMap.put("diffMinutes",0);
        }

        Map jieGuaMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "解挂".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            jieGuaMap=i.get();
        }else {
            jieGuaMap.put("title","解挂");
            jieGuaMap.put("count",0);
            jieGuaMap.put("outTimeCount",0);
            jieGuaMap.put("diffMinutes",0);
        }

        Map fuWuWanChengMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "服务完成".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            fuWuWanChengMap=i.get();
        }else {
            fuWuWanChengMap.put("title","服务完成");
            fuWuWanChengMap.put("count",0);
            fuWuWanChengMap.put("outTimeCount",0);
            fuWuWanChengMap.put("diffMinutes",0);
        }
        Map fuWuTiJiaoMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "服务提交".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            fuWuTiJiaoMap=i.get();
            fuWuTiJiaoMap.put("outTimeCount",outTimeCount);
        }else {
            fuWuTiJiaoMap.put("title","服务提交");
            fuWuTiJiaoMap.put("count",0);
            fuWuTiJiaoMap.put("outTimeCount",0);
            fuWuTiJiaoMap.put("diffMinutes",0);
        }


        List<Map<String, Object>> result = new ArrayList<>();
        result.add(paiGongMap);
        result.add(jieDanMap);
        result.add(yuYueMap);
        result.add(shangMenMap);
        result.add(guaDanMap);
        result.add(zuoYeMap);
        result.add(lingJianMap);
        result.add(jieGuaMap);
        result.add(fuWuWanChengMap);
        result.add(fuWuTiJiaoMap);
        return RetResponse.makeOKRsp(result);
    }
    /**
     * 上门服务异常监控
     * @param request
     * @return
     */
    @Override
    public RetResult<Object> visitMonitoring(RegionVisitMonitoringRequest request) {

    	Map<String, Object> requestParam = null;
    	if(null == request) {
    		requestParam = new HashMap<>();
    	} else {
    		requestParam = request.objectToMap();
    	}
    	requestParam.put("productTypeCodeList", requestParam.get("productTypeCode"));
    	requestParam.remove("productTypeCode");
    	requestParam.put("productCategoryCodeList", requestParam.get("productCategoryCode"));
    	requestParam.remove("productCategoryCode");
    	BigDecimal sixtySeconds = new BigDecimal("60");
    	BigDecimal diffSeconds = BigDecimal.ZERO;
    	List<String> serviceTypeList = new ArrayList<String>();
    	serviceTypeList.add("维修");
    	serviceTypeList.add("安装");
    	serviceTypeList.add("鉴定");
    	requestParam.put("serviceTypeIn", serviceTypeList);
    	// 派工
    	requestParam.put("stateKind", "paiGong");
    	Long paiGongCount = regionDao.visitMonitoringOnlyCount(requestParam);
    	Map<String, Object> paiGongMap = new HashMap<>();
    	paiGongMap.put("title", "派工");
    	paiGongMap.put("count", paiGongCount);

    	// 接单
    	requestParam.put("stateKind", "jieDan");
    	Map<String, Object> jieDanMap = regionDao.visitMonitoring4JieDan(requestParam);
    	jieDanMap.put("title", "接单");
    	if(null != jieDanMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) jieDanMap.get("TimediffSecond");
        	jieDanMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		jieDanMap.put("diffMinutes", 0);
    	}

    	// 预约
    	requestParam.put("stateKind", "yuYue");
    	Map<String, Object> yuYueMap = regionDao.visitMonitoring4YuYue(requestParam);
    	yuYueMap.put("title", "预约");
    	if(null != yuYueMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) yuYueMap.get("TimediffSecond");
        	yuYueMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		yuYueMap.put("diffMinutes", 0);
    	}

    	// 上门
    	requestParam.put("stateKind", "shangMen");
    	Map<String, Object> shangMenMap = regionDao.visitMonitoring4ShangMen(requestParam);
    	shangMenMap.put("title", "上门");
    	if(null != shangMenMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) shangMenMap.get("TimediffSecond");
    		shangMenMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		shangMenMap.put("diffMinutes", 0);
    	}
        //上门单
       /* List<Map> list = regionDao.allList(requestParam);
        list = list.stream().filter(o -> ("安装".equals(o.get("serviceType"))||"维修".equals(o.get("serviceType"))||("鉴定".equals(o.get("serviceType"))&&("安装".equals(o.get("firstServiceType"))||"维修".equals(o.get("firstServiceType")) ) ) )).collect(Collectors.toList());
        //作业单
        List<String> OrderReceipt = regionDao.OrderReceipt(requestParam);
        Integer guaDanCount = list.stream().filter(o -> (o.get("visitTime")!=null&&"挂单".equals(o.get("pendingState"))&&!OrderReceipt.contains(o.get("dispatchingOrder")))).collect(Collectors.toList()).size();
*/
        // 挂单
    	requestParam.put("stateKind", "guaDan");
    	Long guaDanCount = regionDao.visitMonitoringOnlyCount(requestParam);
    	Map<String, Object> guaDanMap = new HashMap<>();
    	guaDanMap.put("title", "挂单");
    	guaDanMap.put("count", guaDanCount);

    	// 作业订单
    	requestParam.put("stateKind", "zuoYe");
    	Long zuoYeCount = regionDao.visitMonitoringOnlyCount(requestParam);
        /*//作业单没收到全部货的
        List<String> isOrder = regionDao.isOrder(requestParam);
        Integer zuoYeCount = list.stream().filter(o -> (o.get("visitTime")!=null&&"挂单".equals(o.get("pendingState"))&&isOrder.contains(o.get("dispatchingOrder")))).collect(Collectors.toList()).size();
*/
    	Map<String, Object> zuoYeMap = new HashMap<>();
    	zuoYeMap.put("title", "作业订单");
    	zuoYeMap.put("count", zuoYeCount);

    	// 零件供应：零件供应是服务单收货单收货确认时间 - 作业订单时间
    	requestParam.put("stateKind", "lingJian");
    	Map<String, Object> lingJianMap = regionDao.visitMonitoring4LingJian(requestParam);
        /*//全部到货的派工单号
        List<String> isReceipt = regionDao.isReceipt(requestParam);
        Integer lj = list.stream().filter(o -> (o.get("visitTime")!=null&&"挂单".equals(o.get("pendingState"))&&isReceipt.contains(o.get("dispatchingOrder")))).collect(Collectors.toList()).size();
        lingJianMap.put("count",lj);*/

    	lingJianMap.put("title", "零件供应");
    	if(null !=  lingJianMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) lingJianMap.get("TimediffSecond");
    		lingJianMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		lingJianMap.put("diffMinutes", 0);
    	}

    	// 解挂
    	requestParam.put("stateKind", "jieGua");
    	Long jieGuaCount = regionDao.visitMonitoringOnlyCount(requestParam);
    	Map<String, Object> jieGuaMap = new HashMap<>();
    	jieGuaMap.put("title", "解挂");
    	jieGuaMap.put("count", jieGuaCount);
    	// 服务完成
    	requestParam.put("stateKind", "fuWuWanCheng");
    	Map<String, Object> fuWuWanChengMap = regionDao.visitMonitoring4FuWuWanCheng(requestParam);
    	fuWuWanChengMap.put("title", "服务完成");
    	if(null !=  fuWuWanChengMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) fuWuWanChengMap.get("TimediffSecond");
    		fuWuWanChengMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		fuWuWanChengMap.put("diffMinutes", 0);
    	}
    	// 服务提交
    	requestParam.put("stateKind", "fuWuTiJiao");
    	Map<String, Object> fuWuTiJiaoMap = regionDao.visitMonitoring4FuWuTiJiao(requestParam);
    	fuWuTiJiaoMap.put("title", "服务提交");
    	if(null != fuWuTiJiaoMap.get("TimediffSecond")) {
	    	diffSeconds = (BigDecimal) fuWuTiJiaoMap.get("TimediffSecond");
	    	fuWuTiJiaoMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		fuWuTiJiaoMap.put("diffMinutes", 0);
    	}

    	// 计算查询的结束时间
    	LocalDateTime now = LocalDateTime.now();
    	// 获取半个小时前的时间
    	LocalDateTime halfHourAgo = now.minusMinutes(30);
    	// 今天0点时间
    	LocalDateTime todayAgo = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
    	// 1天前时间
    	LocalDateTime oneDayAgo = now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    	// 4天前时间
    	LocalDateTime fourDaysAgo = now.minusDays(4).withHour(0).withMinute(0).withSecond(0).withNano(0);

    	// 超时订单与当前接口选择的时间无关
    	requestParam.remove("startTime");
    	// 超时标准 : 半个小时超时标准
    	requestParam.put("endTime", halfHourAgo.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    	// 超时订单 - 接单
        requestParam.put("stateKind", "paiGong");
        Long paiGongCountOutTime = regionDao.visitMonitoringOnlyCount(requestParam);
        paiGongMap.put("outTimeCount", paiGongCountOutTime);

    	// 超时标准: 今天之前超时标准
    	requestParam.put("endTime", todayAgo.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		// 超时订单 - 预约
    	requestParam.put("stateKind", "yuYue");
    	Map<String, Object> yuYueMapOutTime = regionDao.visitMonitoring4YuYue(requestParam);
    	yuYueMap.put("outTimeCount", yuYueMapOutTime.get("count"));

    	// 超时标准: 一天之前
    	requestParam.put("endTime", oneDayAgo.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    	// 超时订单 - 上门
    	requestParam.put("stateKind", "shangMen");
    	Map<String, Object> shangMenMapOutTime = regionDao.visitMonitoring4ShangMen(requestParam);
    	shangMenMap.put("outTimeCount", shangMenMapOutTime.get("count"));

    	// 超时订单 - 挂单
    	requestParam.put("stateKind", "guaDan");
    	Long guaDanCountOutTime = regionDao.visitMonitoringOnlyCount(requestParam);
    	guaDanMap.put("outTimeCount", guaDanCountOutTime);

    	// 超时标准： 四天前
    	requestParam.put("endTime", fourDaysAgo.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    	// 超时订单 - 零件供应
    	requestParam.put("stateKind", "lingJian");
    	Map<String, Object> lingJianMapOutTime = regionDao.visitMonitoring4LingJian(requestParam);
		lingJianMap.put("outTimeCount", lingJianMapOutTime.get("count"));
		// 超时订单 - 解挂
    	requestParam.put("stateKind", "jieGua");
    	Long jieGuaCountOutTime = regionDao.visitMonitoringOnlyCount(requestParam);
		jieGuaMap.put("outTimeCount", jieGuaCountOutTime);

    	// 暂无超时说法的节点
    	jieDanMap.put("outTimeCount", 0);
    	zuoYeMap.put("outTimeCount", 0);
    	fuWuWanChengMap.put("outTimeCount", 0);
    	fuWuTiJiaoMap.put("outTimeCount", 0);

    	List<Map<String, Object>> result = new ArrayList<>(16);
    	result.add(paiGongMap);
    	result.add(jieDanMap);
    	result.add(yuYueMap);
    	result.add(shangMenMap);
    	result.add(guaDanMap);
    	result.add(zuoYeMap);
    	result.add(lingJianMap);
    	result.add(jieGuaMap);
    	result.add(fuWuWanChengMap);
    	result.add(fuWuTiJiaoMap);

        return RetResponse.makeOKRsp(result);
    }

    @Override
    public synchronized RetResult<Object> giveMonitoring1(Map map) {
        map = dateConversion(map);
        /*SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String nowDay = sdfd.format(now);
        String nowTime = sdft.format(now);
        map.put("WHERE","rdd.serviceWay='送修' AND (rdd.serviceType = '非上门维修' OR (rdd.serviceType = '鉴定' AND rdd.firstServiceType = '非上门维修'))");
        Map finalMap = map;
        List<Map> list = new ArrayList<>();
        List<Map> orderList = new ArrayList<>();
        try {

            Future<List<Map>> submit1 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list1 = regionDao.allList(finalMap);
                return list1;
            });
            Future<List<Map>> submit2 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list1 = regionDao.orderList(finalMap);
                return list1;
            });
            list = submit1.get();
            orderList = submit2.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        //list = list.stream().filter(o -> ("送修".equals(o.get("serviceWay"))&&("非上门维修".equals(o.get("serviceType"))||("鉴定".equals(o.get("serviceType"))&&"非上门维修".equals(o.get("firstServiceType")))))).collect(Collectors.toList());
        //仅显示投诉单筛选
        *//*if(list!=null&&map!=null&&map.get("isComplaint")!=null&&"1".equals(map.get("isComplaint"))){
            List<String> isComplaint = regionDao.isComplaint(map);
            list = list.stream().filter(o -> isComplaint.contains(o.get("dispatchingOrder"))).collect(Collectors.toList());
        }*//*
        //仅异常单筛选
        if(list!=null&&map!=null&&map.get("isErr")!=null&&"1".equals(map.get("isErr"))) {
            list = list.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        }
        Map fuWuTiJiaoMap = new HashMap<>();
        fuWuTiJiaoMap.put("title","服务提交");
        //筛选对应时间不为空的
        List<Map> childList = list.stream().filter(o -> o.get("submissionTime")!=null).collect(Collectors.toList());
        //反向筛选结果防止同一单重复计算（删除速度比较慢）
        list = list.stream().filter(o -> o.get("submissionTime")==null).collect(Collectors.toList());
        //筛选出的记录条数为单数
        fuWuTiJiaoMap.put("count",childList==null?0:childList.size());
        //计算总时长，前端会除数量得到平均值
        long diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("finishTime"),s.get("submissionTime"),sdft)).sum();
        fuWuTiJiaoMap.put("diffMinutes",diffMinutes);
        //超时单
        List<Map> errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        fuWuTiJiaoMap.put("outTimeCount",errList==null?0:errList.size());

        Map fuWuWanChengMap = new HashMap<>();
        fuWuWanChengMap.put("title","服务完成");
        //筛选对应时间不为空的
        childList = list.stream().filter(o -> o.get("finishTime")!=null).collect(Collectors.toList());
        //反向筛选结果防止同一单重复计算（删除速度比较慢）
        list = list.stream().filter(o -> o.get("finishTime")==null).collect(Collectors.toList());
        fuWuWanChengMap.put("count",childList==null?0:childList.size());
        //计算总时长，前端会除数量得到平均值
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("shopReturnTime"),s.get("finishTime"),sdft)).sum();
        fuWuWanChengMap.put("diffMinutes",diffMinutes);
        //超时单
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        fuWuWanChengMap.put("outTimeCount",errList==null?0:errList.size());

        Map huanJianMap = new HashMap<>();
        huanJianMap.put("title","还件");
        //筛选对应时间不为空的
        childList = list.stream().filter(o -> o.get("shopReturnTime")!=null).collect(Collectors.toList());
        //反向筛选结果防止同一单重复计算（删除速度比较慢）
        list = list.stream().filter(o -> o.get("shopReturnTime")==null).collect(Collectors.toList());
        huanJianMap.put("count",childList==null?0:childList.size());
        //计算总时长，前端会除数量得到平均值
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("dispatchingTime"),s.get("shopReturnTime"),sdft)).sum();
        huanJianMap.put("diffMinutes",diffMinutes);
        //超时单
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        huanJianMap.put("outTimeCount",errList==null?0:errList.size());

        Map lingJianMap = new HashMap<>();
        lingJianMap.put("title","零件供应");
        //查询作业订单

        //将作业订单的值赋值给派工单
        List<Map> finalOrderList = orderList;
        list.stream().forEach(e -> {
            Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
            if(i.isPresent()){
                e.put("orderStartTime",i.get().get("orderStartTime"));
                e.put("appropriateInvestTime",i.get().get("appropriateInvestTime"));
            }else {
                e.put("orderStartTime",null);
                e.put("appropriateInvestTime",null);
            }
        });
        //筛选到件时间不为空的为零件供应
        childList = list.stream().filter(o -> o.get("appropriateInvestTime")!=null).collect(Collectors.toList());
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("appropriateInvestTime")==null).collect(Collectors.toList());
        lingJianMap.put("count",childList==null?0:childList.size());
        //求作业订单开始时间到到货签收时间的平均分钟数
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("orderStartTime"),s.get("appropriateInvestTime"),sdft)).sum();
        lingJianMap.put("diffMinutes",diffMinutes);
        //childList = childList.stream().filter(o -> stoelong(o.get("appropriateInvestTime")!=null&&o.get("appropriateInvestTime").toString().length()>10?o.get("appropriateInvestTime").toString().substring(0,10):o.get("appropriateInvestTime"),nowDay,sdfd)>1440).collect(Collectors.toList());
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        lingJianMap.put("outTimeCount",errList==null?0:errList.size());

        Map zuoYeMap = new HashMap<>();
        zuoYeMap.put("title","作业订单");
        //筛选作业订单开始时间不为空的为作业订单
        childList = list.stream().filter(o -> o.get("orderStartTime")!=null).collect(Collectors.toList());
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
        zuoYeMap.put("count",childList==null?0:childList.size());
        zuoYeMap.put("diffMinutes",0);
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        zuoYeMap.put("outTimeCount",errList==null?0:errList.size());

        Map feiShangMenMap = new HashMap<>();
        feiShangMenMap.put("title","非上门服务单");
        //筛选作业订单开始时间不为空的为作业订单
        childList = list.stream().filter(o -> o.get("serviceNumber")!=null).collect(Collectors.toList());
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("serviceNumber")==null).collect(Collectors.toList());
        feiShangMenMap.put("count",childList==null?0:childList.size());
        feiShangMenMap.put("diffMinutes",0);
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        feiShangMenMap.put("outTimeCount",errList==null?0:errList.size());

        Map paiGongMap = new HashMap<>();
        paiGongMap.put("title","派工");
        //剩下的都给派工
        paiGongMap.put("count",list==null?0:list.size());
        paiGongMap.put("diffMinutes",0);
        paiGongMap.put("outTimeCount",0);*/
        List<Map> zzlist = regionDao.giveMonitoring(map);
        // 送修服务异常监控
        map.put("serviceWay","2");
        Integer outTimeCount = regionDao.getOutTimeCount(map);

        Map paiGongMap = new HashMap<>();
        Optional<Map> i = zzlist.stream().filter(x->x.get("title")!=null && "派工".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            paiGongMap=i.get();
        }else {
            paiGongMap.put("title","派工");
            paiGongMap.put("count",0);
            paiGongMap.put("outTimeCount",0);
            paiGongMap.put("diffMinutes",0);
        }
        Map feiShangMenMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "非上门服务单".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            feiShangMenMap=i.get();
        }else {
            feiShangMenMap.put("title","非上门服务单");
            feiShangMenMap.put("count",0);
            feiShangMenMap.put("outTimeCount",0);
            feiShangMenMap.put("diffMinutes",0);
        }
        Map zuoYeMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "作业订单".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            zuoYeMap=i.get();
        }else {
            zuoYeMap.put("title","作业订单");
            zuoYeMap.put("count",0);
            zuoYeMap.put("outTimeCount",0);
            zuoYeMap.put("diffMinutes",0);
        }
        Map lingJianMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "零件供应".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            lingJianMap=i.get();
        }else {
            lingJianMap.put("title","零件供应");
            lingJianMap.put("count",0);
            lingJianMap.put("outTimeCount",0);
            lingJianMap.put("diffMinutes",0);
        }
        Map huanJianMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "还件".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            huanJianMap=i.get();
        }else {
            huanJianMap.put("title","还件");
            huanJianMap.put("count",0);
            huanJianMap.put("outTimeCount",0);
            huanJianMap.put("diffMinutes",0);
        }
        Map fuWuWanChengMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "服务完成".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            fuWuWanChengMap=i.get();
        }else {
            fuWuWanChengMap.put("title","服务完成");
            fuWuWanChengMap.put("count",0);
            fuWuWanChengMap.put("outTimeCount",0);
            fuWuWanChengMap.put("diffMinutes",0);
        }
        Map fuWuTiJiaoMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "服务提交".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            fuWuTiJiaoMap=i.get();
            fuWuTiJiaoMap.put("outTimeCount",outTimeCount);
        }else {
            fuWuTiJiaoMap.put("title","服务提交");
            fuWuTiJiaoMap.put("count",0);
            fuWuTiJiaoMap.put("outTimeCount",0);
            fuWuTiJiaoMap.put("diffMinutes",0);
        }
        List<Map<String, Object>> result = new ArrayList<>(16);
        result.add(paiGongMap);
        result.add(feiShangMenMap);
        result.add(zuoYeMap);
        result.add(lingJianMap);
        result.add(huanJianMap);
        result.add(fuWuWanChengMap);
        result.add(fuWuTiJiaoMap);

        return RetResponse.makeOKRsp(result);
    }
    /**
     * 送修服务异常监控
     * @param request
     * @return
     */
    @Override
    public RetResult<Object> giveMonitoring(RegionVisitMonitoringRequest request) {
    	Map<String, Object> requestParam = null;
    	if(null == request) {
    		requestParam = new HashMap<>();
    	} else {
    		requestParam = request.objectToMap();
    	}
    	requestParam.put("productTypeCodeList", requestParam.get("productTypeCode"));
    	requestParam.remove("productTypeCode");

    	BigDecimal sixtySeconds = new BigDecimal("60");
    	BigDecimal diffSeconds = BigDecimal.ZERO;
    	//requestParam.put("serviceType", "非上门维修");
    	requestParam.put("serviceWay", "送修");
    	// 派工
    	requestParam.put("stateKind", "paiGong");
    	Long paiGongCount = regionDao.visitMonitoringOnlyCount(requestParam);
    	Map<String, Object> paiGongMap = new HashMap<>();
    	paiGongMap.put("title", "派工");
    	paiGongMap.put("count", paiGongCount);
    	// 接单(非上门服务单)
    	requestParam.put("stateKind", "jieDan");
    	Map<String, Object> jieDanMap = regionDao.giveMonitoring4JieDan(requestParam);

        /*//送修单
        List<Map> list = regionDao.allList(requestParam);
        list = list.stream().filter(o -> ("送修".equals(o.get("serviceWay"))&&("非上门维修".equals(o.get("serviceType"))||("鉴定".equals(o.get("serviceType"))&&"非上门维修".equals(o.get("firstServiceType")))))).collect(Collectors.toList());
*/
       /* //所以作业单
        List<String> OrderReceipt = regionDao.OrderReceipt(requestParam);
        Integer fc = list.stream().filter(o -> (o.get("receiptTime")!=null&&o.get("shopReturnTime")==null&&!OrderReceipt.contains(o.get("dispatchingOrder")))).collect(Collectors.toList()).size();
        jieDanMap.put("count",fc);*/


        jieDanMap.put("title", "非上门服务单");
    	if(null != jieDanMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) jieDanMap.get("TimediffSecond");
        	jieDanMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		jieDanMap.put("diffMinutes", 0);
    	}
    	// 作业订单
    	requestParam.put("stateKind", "zuoYe");
    	Map<String, Object> zuoYeMap = regionDao.giveMonitoring4ZuoYe(requestParam);
       /* //作业订单没签收
        List<String> isOrder = regionDao.isOrder(requestParam);
        Integer zc = list.stream().filter(o -> (o.get("receiptTime")!=null&&o.get("shopReturnTime")==null&&isOrder.contains(o.get("dispatchingOrder")))).collect(Collectors.toList()).size();
        zuoYeMap.put("count",zc);*/

        zuoYeMap.put("title", "作业订单");
    	if(null != zuoYeMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) zuoYeMap.get("TimediffSecond");
    		zuoYeMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		zuoYeMap.put("diffMinutes", 0);
    	}
    	// 零件供应：零件供应是服务单收货单收货确认时间 - 作业订单时间
    	requestParam.put("stateKind", "lingJian");
    	Map<String, Object> lingJianMap = regionDao.giveMonitoring4LingJian(requestParam);

       /* //到货签收的
        List<String> isReceipt = regionDao.isReceipt(requestParam);
        Integer lc = list.stream().filter(o -> (o.get("receiptTime")!=null&&o.get("shopReturnTime")==null&&isReceipt.contains(o.get("dispatchingOrder")))).collect(Collectors.toList()).size();
        lingJianMap.put("count",lc);*/

    	lingJianMap.put("title", "零件供应");
    	if(null != lingJianMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) lingJianMap.get("TimediffSecond");
    		lingJianMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		lingJianMap.put("diffMinutes", 0);
    	}
    	// 还件
    	requestParam.put("stateKind", "huanJian");
    	Map<String, Object> huanJianMap = regionDao.giveMonitoring4HuanJian(requestParam);
    	huanJianMap.put("title", "还件");
    	if(null != huanJianMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) huanJianMap.get("TimediffSecond");
    		huanJianMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		huanJianMap.put("diffMinutes", 0);
    	}
    	// 服务完成
    	requestParam.put("stateKind", "fuWuWanCheng");
    	Map<String, Object> fuWuWanChengMap = regionDao.giveMonitoring4FuWuWanCheng(requestParam);
    	fuWuWanChengMap.put("title", "服务完成");
    	if(null != fuWuWanChengMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) fuWuWanChengMap.get("TimediffSecond");
    		fuWuWanChengMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		fuWuWanChengMap.put("diffMinutes", 0);
    	}
    	// 服务提交
    	requestParam.put("stateKind", "fuWuTiJiao");
    	Map<String, Object> fuWuTiJiaoMap = regionDao.giveMonitoring4FuWuTiJiao(requestParam);
    	fuWuTiJiaoMap.put("title", "服务提交");
    	if(null != fuWuTiJiaoMap.get("TimediffSecond")) {
	    	diffSeconds = (BigDecimal) fuWuTiJiaoMap.get("TimediffSecond");
	    	fuWuTiJiaoMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		fuWuTiJiaoMap.put("diffMinutes", 0);
    	}

    	// 4天前时间
    	LocalDate fourDaysAgo = LocalDate.now().minusDays(4);

    	// 超时订单与当前接口选择的时间无关
    	requestParam.remove("startTime");
    	// 超时标准 : 四天前
    	requestParam.put("endTime", fourDaysAgo.format(DateTimeFormatter.ISO_LOCAL_DATE));
    	// 超时订单 - 派工
        requestParam.put("stateKind", "paiGong");
        Long paiGongCountOutTime = regionDao.visitMonitoringOnlyCount(requestParam);
        paiGongMap.put("outTimeCount", paiGongCountOutTime);
        // 超时订单 - 零件供应
    	requestParam.put("stateKind", "lingJian");
    	Map<String, Object> lingJianMapOutTime = regionDao.giveMonitoring4LingJian(requestParam);
		lingJianMap.put("outTimeCount", lingJianMapOutTime.get("count"));
		// 超时订单 - 还件
    	requestParam.put("stateKind", "huanJian");
    	Map<String, Object> huanJianMapOutTime = regionDao.giveMonitoring4HuanJian(requestParam);
    	huanJianMap.put("outTimeCount", huanJianMapOutTime.get("count"));

    	// 暂无超时说法的节点
    	jieDanMap.put("outTimeCount", 0);
    	zuoYeMap.put("outTimeCount", 0);
    	fuWuWanChengMap.put("outTimeCount", 0);
    	fuWuTiJiaoMap.put("outTimeCount", 0);

    	List<Map<String, Object>> result = new ArrayList<>();
    	result.add(paiGongMap);
    	result.add(jieDanMap);
    	result.add(zuoYeMap);
    	result.add(lingJianMap);
    	result.add(huanJianMap);
    	result.add(fuWuWanChengMap);
    	result.add(fuWuTiJiaoMap);

        return RetResponse.makeOKRsp(result);
    }
    /**
     * 寄修服务异常监控
     * @param map
     * @return
     */
    @Override
    public synchronized RetResult<Object> sendMonitoring1(Map map) {
        map = dateConversion(map);
        /*SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String nowDay = sdfd.format(now);
        String nowTime = sdft.format(now);
        map.put("WHERE","rdd.serviceWay='寄修' AND (rdd.serviceType = '非上门维修' OR (rdd.serviceType = '鉴定' AND rdd.firstServiceType = '非上门维修'))");
        Map finalMap = map;
        List<Map> list = new ArrayList<>();
        List<Map> orderList = new ArrayList<>();
        try {

            Future<List<Map>> submit1 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list1 = regionDao.allList(finalMap);
                return list1;
            });
            Future<List<Map>> submit2 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list1 = regionDao.orderList(finalMap);
                return list1;
            });
            list = submit1.get();
            orderList = submit2.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        //list = list.stream().filter(o -> ("寄修".equals(o.get("serviceWay"))&&("非上门维修".equals(o.get("serviceType"))||("鉴定".equals(o.get("serviceType"))&&"非上门维修".equals(o.get("firstServiceType")))))).collect(Collectors.toList());
        //仅显示投诉单筛选
        *//*if(list!=null&&map!=null&&map.get("isComplaint")!=null&&"1".equals(map.get("isComplaint"))){
            List<String> isComplaint = regionDao.isComplaint(map);
            list = list.stream().filter(o -> isComplaint.contains(o.get("dispatchingOrder"))).collect(Collectors.toList());
        }*//*
        //仅异常单筛选
        if(list!=null&&map!=null&&map.get("isErr")!=null&&"1".equals(map.get("isErr"))) {
            list = list.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        }
        Map fuWuTiJiaoMap = new HashMap<>();
        fuWuTiJiaoMap.put("title","服务提交");
        //筛选对应时间不为空的
        List<Map> childList = list.stream().filter(o -> o.get("submissionTime")!=null).collect(Collectors.toList());
        //反向筛选结果防止同一单重复计算（删除速度比较慢）
        list = list.stream().filter(o -> o.get("submissionTime")==null).collect(Collectors.toList());
        //筛选出的记录条数为单数
        fuWuTiJiaoMap.put("count",childList==null?0:childList.size());
        //计算总时长，前端会除数量得到平均值
        long diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("finishTime"),s.get("submissionTime"),sdft)).sum();
        fuWuTiJiaoMap.put("diffMinutes",diffMinutes);
        //超时单
        List<Map> errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        fuWuTiJiaoMap.put("outTimeCount",errList==null?0:errList.size());

        Map fuWuWanChengMap = new HashMap<>();
        fuWuWanChengMap.put("title","还件签收&服务完成");
        //筛选对应时间不为空的
        childList = list.stream().filter(o -> o.get("finishTime")!=null||o.get("customerDeliveryTime")!=null).collect(Collectors.toList());
        //反向筛选结果防止同一单重复计算（删除速度比较慢）
        list = list.stream().filter(o -> o.get("finishTime")==null&&o.get("customerDeliveryTime")==null).collect(Collectors.toList());
        fuWuWanChengMap.put("count",childList==null?0:childList.size());
        //计算总时长，前端会除数量得到平均值
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("returnCollectionTime"),s.get("finishTime"),sdft)).sum();
        fuWuWanChengMap.put("diffMinutes",diffMinutes);
        //超时单
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        fuWuWanChengMap.put("outTimeCount",errList==null?0:errList.size());

        Map huanJianLanShouMap = new HashMap<>();
        huanJianLanShouMap.put("title","还件揽收");
        //筛选对应时间不为空的
        childList = list.stream().filter(o -> o.get("returnCollectionTime")!=null).collect(Collectors.toList());
        //反向筛选结果防止同一单重复计算（删除速度比较慢）
        list = list.stream().filter(o -> o.get("returnCollectionTime")==null).collect(Collectors.toList());
        huanJianLanShouMap.put("count",childList==null?0:childList.size());
        //计算总时长，前端会除数量得到平均值
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("shopReturnTime"),s.get("returnCollectionTime"),sdft)).sum();
        huanJianLanShouMap.put("diffMinutes",diffMinutes);
        //超时单
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        huanJianLanShouMap.put("outTimeCount",errList==null?0:errList.size());

        Map huanJianMap = new HashMap<>();
        huanJianMap.put("title","还件");
        //筛选对应时间不为空的
        childList = list.stream().filter(o -> o.get("shopReturnTime")!=null).collect(Collectors.toList());
        //反向筛选结果防止同一单重复计算（删除速度比较慢）
        list = list.stream().filter(o -> o.get("shopReturnTime")==null).collect(Collectors.toList());
        huanJianMap.put("count",childList==null?0:childList.size());
        //计算总时长，前端会除数量得到平均值
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("shopDeliveryTime"),s.get("shopReturnTime"),sdft)).sum();
        huanJianMap.put("diffMinutes",diffMinutes);
        //超时单
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        huanJianMap.put("outTimeCount",errList==null?0:errList.size());

        Map lingJianMap = new HashMap<>();
        lingJianMap.put("title","零件供应");
        //查询作业订单
        //将作业订单的值赋值给派工单
        List<Map> finalOrderList = orderList;
        list.stream().forEach(e -> {
            Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
            if(i.isPresent()){
                e.put("orderStartTime",i.get().get("orderStartTime"));
                e.put("appropriateInvestTime",i.get().get("appropriateInvestTime"));
            }else {
                e.put("orderStartTime",null);
                e.put("appropriateInvestTime",null);
            }
        });
        //筛选到件时间不为空的为零件供应
        childList = list.stream().filter(o -> o.get("appropriateInvestTime")!=null).collect(Collectors.toList());
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("appropriateInvestTime")==null).collect(Collectors.toList());
        lingJianMap.put("count",childList==null?0:childList.size());
        //求作业订单开始时间到到货签收时间的平均分钟数
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("orderStartTime"),s.get("appropriateInvestTime"),sdft)).sum();
        lingJianMap.put("diffMinutes",diffMinutes);
        //childList = childList.stream().filter(o -> stoelong(o.get("appropriateInvestTime")!=null&&o.get("appropriateInvestTime").toString().length()>10?o.get("appropriateInvestTime").toString().substring(0,10):o.get("appropriateInvestTime"),nowDay,sdfd)>1440).collect(Collectors.toList());
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        lingJianMap.put("outTimeCount",errList==null?0:errList.size());

        Map zuoYeMap = new HashMap<>();
        zuoYeMap.put("title","作业订单");
        //筛选作业订单开始时间不为空的为作业订单
        childList = list.stream().filter(o -> o.get("orderStartTime")!=null).collect(Collectors.toList());
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
        zuoYeMap.put("count",childList==null?0:childList.size());
        zuoYeMap.put("diffMinutes",0);
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        zuoYeMap.put("outTimeCount",errList==null?0:errList.size());

        Map feiShangMenMap = new HashMap<>();
        feiShangMenMap.put("title","非上门服务单");
        //筛选作业订单开始时间不为空的为作业订单
        childList = list.stream().filter(o -> o.get("serviceNumber")!=null).collect(Collectors.toList());
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("serviceNumber")==null).collect(Collectors.toList());
        feiShangMenMap.put("count",childList==null?0:childList.size());
        feiShangMenMap.put("diffMinutes",0);
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        feiShangMenMap.put("outTimeCount",errList==null?0:errList.size());

        Map jiXiuDaoJianMap = new HashMap<>();
        jiXiuDaoJianMap.put("title","寄修到件");
        //筛选作业订单开始时间不为空的为作业订单
        childList = list.stream().filter(o -> o.get("shopDeliveryTime")!=null).collect(Collectors.toList());
        //流筛选出差集并覆盖
        list = list.stream().filter(o -> o.get("shopDeliveryTime")==null).collect(Collectors.toList());
        jiXiuDaoJianMap.put("count",childList==null?0:childList.size());
        diffMinutes = childList.stream().mapToLong(s ->stoelong(s.get("dispatchingTime"),s.get("shopDeliveryTime"),sdft)).sum();
        jiXiuDaoJianMap.put("diffMinutes",diffMinutes);
        errList = childList.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
        jiXiuDaoJianMap.put("outTimeCount",errList==null?0:errList.size());

        Map paiGongMap = new HashMap<>();
        paiGongMap.put("title","派工");
        //剩下的都给派工
        paiGongMap.put("count",list==null?0:list.size());
        paiGongMap.put("diffMinutes",0);
        paiGongMap.put("outTimeCount",0);*/
        List<Map> zzlist = regionDao.sendMonitoring(map);
        // 寄修服务异常监控
        map.put("serviceWay","3");
        Integer outTimeCount = regionDao.getOutTimeCount(map);

        Map paiGongMap = new HashMap<>();
        paiGongMap.put("title","派工");
        paiGongMap.put("count",0);
        paiGongMap.put("outTimeCount",0);
        paiGongMap.put("diffMinutes",0);

        Map jiXiuDaoJianMap = new HashMap<>();
        jiXiuDaoJianMap.put("title","寄修到件");
        jiXiuDaoJianMap.put("count",0);
        jiXiuDaoJianMap.put("outTimeCount",0);
        jiXiuDaoJianMap.put("diffMinutes",0);

        Map feiShangMenMap = new HashMap<>();
        feiShangMenMap.put("title","非上门服务单");
        feiShangMenMap.put("count",0);
        feiShangMenMap.put("outTimeCount",0);
        feiShangMenMap.put("diffMinutes",0);

        Map zuoYeMap = new HashMap<>();
        zuoYeMap.put("title","作业订单");
        zuoYeMap.put("count",0);
        zuoYeMap.put("outTimeCount",0);
        zuoYeMap.put("diffMinutes",0);

        Map lingJianMap = new HashMap<>();
        lingJianMap.put("title","零件供应");
        lingJianMap.put("count",0);
        lingJianMap.put("outTimeCount",0);
        lingJianMap.put("diffMinutes",0);

        Map huanJianMap = new HashMap<>();
        huanJianMap.put("title","还件");
        huanJianMap.put("count",0);
        huanJianMap.put("outTimeCount",0);
        huanJianMap.put("diffMinutes",0);

        Map huanJianLanShouMap = new HashMap<>();
        huanJianLanShouMap.put("title","还件揽收");
        huanJianLanShouMap.put("count",0);
        huanJianLanShouMap.put("outTimeCount",0);
        huanJianLanShouMap.put("diffMinutes",0);

        Map fuWuWanChengMap = new HashMap<>();
        fuWuWanChengMap.put("title","还件签收&服务完成");
        fuWuWanChengMap.put("count",0);
        fuWuWanChengMap.put("outTimeCount",0);
        fuWuWanChengMap.put("diffMinutes",0);

        Map fuWuTiJiaoMap = new HashMap<>();
        fuWuTiJiaoMap.put("title","服务提交");
        fuWuTiJiaoMap.put("count",0);
        fuWuTiJiaoMap.put("outTimeCount",0);
        fuWuTiJiaoMap.put("diffMinutes",0);


        for(Map x:zzlist){
            if(x.get("title")!=null){
                switch (x.get("title").toString()){
                    case "派工":
                        paiGongMap=x;
                        break;
                    case "寄修到件":
                        jiXiuDaoJianMap=x;
                        break;
                    case "非上门服务单":
                        feiShangMenMap=x;
                        break;
                    case "作业订单":
                        zuoYeMap=x;
                        break;
                    case "零件供应":
                        lingJianMap=x;
                        break;
                    case "还件":
                        huanJianMap=x;
                        break;
                    case "还件揽收":
                        huanJianLanShouMap=x;
                        break;
                    case "服务完成":
                        fuWuWanChengMap=x;
                        fuWuWanChengMap.put("title","还件签收&服务完成");
                        break;
                    case "服务提交":
                        fuWuTiJiaoMap=x;
                        fuWuTiJiaoMap.put("outTimeCount",outTimeCount);
                        break;
                }
            }
        }

       /* Optional<Map> i = zzlist.stream().filter(x->x.get("title")!=null && "派工".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            paiGongMap=i.get();
        }else {
            paiGongMap.put("title","派工");
            paiGongMap.put("count",0);
            paiGongMap.put("outTimeCount",0);
            paiGongMap.put("diffMinutes",0);
        }*/

        /*i = zzlist.stream().filter(x->x.get("title")!=null && "寄修到件".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            jiXiuDaoJianMap=i.get();
        }else {
            jiXiuDaoJianMap.put("title","寄修到件");
            jiXiuDaoJianMap.put("count",0);
            jiXiuDaoJianMap.put("outTimeCount",0);
            jiXiuDaoJianMap.put("diffMinutes",0);
        }*/


        /*i = zzlist.stream().filter(x->x.get("title")!=null && "非上门服务单".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            feiShangMenMap=i.get();
        }else {
            feiShangMenMap.put("title","非上门服务单");
            feiShangMenMap.put("count",0);
            feiShangMenMap.put("outTimeCount",0);
            feiShangMenMap.put("diffMinutes",0);
        }*/

        /*Map zuoYeMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "作业订单".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            zuoYeMap=i.get();
        }else {
            zuoYeMap.put("title","作业订单");
            zuoYeMap.put("count",0);
            zuoYeMap.put("outTimeCount",0);
            zuoYeMap.put("diffMinutes",0);
        }*/
        /*Map lingJianMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "零件供应".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            lingJianMap=i.get();
        }else {
            lingJianMap.put("title","零件供应");
            lingJianMap.put("count",0);
            lingJianMap.put("outTimeCount",0);
            lingJianMap.put("diffMinutes",0);
        }*/
        /*Map huanJianMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "还件".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            huanJianMap=i.get();
        }else {
            huanJianMap.put("title","还件");
            huanJianMap.put("count",0);
            huanJianMap.put("outTimeCount",0);
            huanJianMap.put("diffMinutes",0);
        }*/
        /*Map huanJianLanShouMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "还件揽收".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            huanJianLanShouMap=i.get();
        }else {
            huanJianLanShouMap.put("title","还件揽收");
            huanJianLanShouMap.put("count",0);
            huanJianLanShouMap.put("outTimeCount",0);
            huanJianLanShouMap.put("diffMinutes",0);
        }*/
        /*Map fuWuWanChengMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "服务完成".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            fuWuWanChengMap=i.get();
            fuWuWanChengMap.put("title","还件签收&服务完成");
        }else {
            fuWuWanChengMap.put("title","还件签收&服务完成");
            fuWuWanChengMap.put("count",0);
            fuWuWanChengMap.put("outTimeCount",0);
            fuWuWanChengMap.put("diffMinutes",0);
        }*/
       /* Map fuWuTiJiaoMap = new HashMap<>();
        i = zzlist.stream().filter(x->x.get("title")!=null && "服务提交".equals(x.get("title"))).findFirst();
        if(i.isPresent()){
            fuWuTiJiaoMap=i.get();
            fuWuTiJiaoMap.put("outTimeCount",outTimeCount);
        }else {
            fuWuTiJiaoMap.put("title","服务提交");
            fuWuTiJiaoMap.put("count",0);
            fuWuTiJiaoMap.put("outTimeCount",0);
            fuWuTiJiaoMap.put("diffMinutes",0);
        }*/

        List<Map<String, Object>> result = new ArrayList<>();
        result.add(paiGongMap);
        result.add(jiXiuDaoJianMap);
        result.add(feiShangMenMap);
        result.add(zuoYeMap);
        result.add(lingJianMap);
        result.add(huanJianMap);
        result.add(huanJianLanShouMap);
        result.add(fuWuWanChengMap);
        result.add(fuWuTiJiaoMap);

        return RetResponse.makeOKRsp(result);
    }
    /**
     * 寄修服务异常监控
     * @param request
     * @return
     */
    @Override
    public RetResult<Object> sendMonitoring(RegionVisitMonitoringRequest request) {
    	Map<String, Object> requestParam = null;
    	if(null == request) {
    		requestParam = new HashMap<>();
    	} else {
    		requestParam = request.objectToMap();
    	}
    	requestParam.put("productTypeCodeList", requestParam.get("productTypeCode"));
    	requestParam.remove("productTypeCode");
    	BigDecimal sixtySeconds = new BigDecimal("60");
    	BigDecimal diffSeconds = BigDecimal.ZERO;
    	requestParam.put("serviceType", "非上门维修");
    	requestParam.put("serviceWay", "寄修");
    	// 派工
    	requestParam.put("stateKind", "paiGongMap");
    	Long paiGongCount = regionDao.visitMonitoringOnlyCount(requestParam);
    	Map<String, Object> paiGongMap = new HashMap<>();
    	paiGongMap.put("title", "派工");
    	paiGongMap.put("count", paiGongCount);

    	// 寄修到件
    	requestParam.put("stateKind", "jiXiu");
    	Map<String, Object> jiXiuMap = regionDao.sendMonitoring4JiXiu(requestParam);
    	jiXiuMap.put("title", "寄修到件");
    	if(null != jiXiuMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) jiXiuMap.get("TimediffSecond");
    		jiXiuMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		jiXiuMap.put("diffMinutes", 0);
    	}

       /* //寄修单
        List<Map> list = regionDao.allList(requestParam);
        list = list.stream().filter(o -> ("寄修".equals(o.get("serviceWay"))&&("非上门维修".equals(o.get("serviceType"))||("鉴定".equals(o.get("serviceType"))&&"非上门维修".equals(o.get("firstServiceType")))))).collect(Collectors.toList());
*/
        // 接单((非上门服务单))
    	requestParam.put("stateKind", "jieDan");
    	Map<String, Object> jieDanMap = regionDao.sendMonitoring4JieDan(requestParam);

       /* List<String> OrderReceipt = regionDao.OrderReceipt(requestParam);
        Integer fc = list.stream().filter(o -> (o.get("receiptTime")!=null&&o.get("shopReturnTime")==null&&!OrderReceipt.contains(o.get("dispatchingOrder")))).collect(Collectors.toList()).size();
        jieDanMap.put("count",fc);*/


    	jieDanMap.put("title", "非上门服务单");
    	if(null !=jieDanMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) jieDanMap.get("TimediffSecond");
        	jieDanMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		jieDanMap.put("diffMinutes", 0);
    	}
    	// 作业订单
    	requestParam.put("stateKind", "zuoYe");
    	Map<String, Object> zuoYeMap = regionDao.sendMonitoring4ZuoYe(requestParam);

       /* List<String> isOrder = regionDao.isOrder(requestParam);
        Integer zc = list.stream().filter(o -> (o.get("receiptTime")!=null&&o.get("shopReturnTime")==null&&isOrder.contains(o.get("dispatchingOrder")))).collect(Collectors.toList()).size();
        zuoYeMap.put("count",zc);*/

    	zuoYeMap.put("title", "作业订单");
    	if(null !=zuoYeMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) zuoYeMap.get("TimediffSecond");
    		zuoYeMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		zuoYeMap.put("diffMinutes", 0);
    	}
    	// 零件供应
    	requestParam.put("stateKind", "lingJian");
    	Map<String, Object> lingJianMap = regionDao.sendMonitoring4LingJian(requestParam);

       /* List<String> isReceipt = regionDao.isReceipt(requestParam);
        Integer lc = list.stream().filter(o -> (o.get("receiptTime")!=null&&o.get("shopReturnTime")==null&&isReceipt.contains(o.get("dispatchingOrder")))).collect(Collectors.toList()).size();
        lingJianMap.put("count",lc);*/

        lingJianMap.put("title", "零件供应");
    	if(null != lingJianMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) lingJianMap.get("TimediffSecond");
    		lingJianMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		lingJianMap.put("diffMinutes", 0);
    	}

    	// 还件
    	requestParam.put("stateKind", "huanJian");
    	Map<String, Object> huanJianMap = regionDao.sendMonitoring4HuanJian(requestParam);
    	huanJianMap.put("title", "还件");
    	if(null != huanJianMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) huanJianMap.get("TimediffSecond");
    		huanJianMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		huanJianMap.put("diffMinutes", 0);
    	}
    	// 还件揽收
    	requestParam.put("stateKind", "lingJianLanShou");
    	Map<String, Object> huanJianLanShouMap = regionDao.sendMonitoring4HuanJianLanShou(requestParam);
    	huanJianLanShouMap.put("title", "还件揽收");
    	if(null != huanJianLanShouMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal)huanJianLanShouMap.get("TimediffSecond");
    		huanJianLanShouMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		huanJianLanShouMap.put("diffMinutes", 0);
    	}
    	// 服务完成
    	requestParam.put("stateKind", "fuWuWanCheng");
    	Map<String, Object> fuWuWanChengMap = regionDao.sendMonitoring4FuWuWanCheng(requestParam);
    	fuWuWanChengMap.put("title", "换件签收&服务完成");
    	if(null != fuWuWanChengMap.get("TimediffSecond")) {
    		diffSeconds = (BigDecimal) fuWuWanChengMap.get("TimediffSecond");
    		fuWuWanChengMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		fuWuWanChengMap.put("diffMinutes", 0);
    	}
    	// 服务提交
    	requestParam.put("stateKind", "fuWuTiJiao");
    	Map<String, Object> fuWuTiJiaoMap = regionDao.sendMonitoring4FuWuTiJiao(requestParam);
    	fuWuTiJiaoMap.put("title", "服务提交");
    	if(null != fuWuTiJiaoMap.get("TimediffSecond")) {
	    	diffSeconds = (BigDecimal) fuWuTiJiaoMap.get("TimediffSecond");
	    	fuWuTiJiaoMap.put("diffMinutes", diffSeconds.divide(sixtySeconds, BigDecimal.ROUND_DOWN));
    	} else {
    		fuWuTiJiaoMap.put("diffMinutes", 0);
    	}

    	// 获取四天前的时间用来处理超时时间
    	LocalDate fourDaysAgo = LocalDate.now().minusDays(4);

    	requestParam.put("endTime", fourDaysAgo.format(DateTimeFormatter.ISO_LOCAL_DATE));

    	// 超时订单 - 派工
    	requestParam.put("stateKind", "paiGongMap");
    	Long paiGongCountOutTime = regionDao.visitMonitoringOnlyCount(requestParam);
    	paiGongMap.put("outTimeCount", paiGongCountOutTime);
    	// 超时订单 - 寄修
		requestParam.put("stateKind", "jiXiu");
    	Map<String, Object> jiXiuMapOutTime = regionDao.sendMonitoring4JiXiu(requestParam);
    	jiXiuMap.put("outTimeCount", jiXiuMapOutTime.get("count"));
    	// 超时订单 - 接单(非上门服务单)
    	requestParam.put("stateKind", "jieDan");
    	Map<String, Object> jieDanMapOutTime = regionDao.sendMonitoring4JieDan(requestParam);
    	jieDanMap.put("outTimeCount", jieDanMapOutTime.get("count"));
    	// 超时订单 - 零件供应
    	requestParam.put("stateKind", "lingJian");
    	Map<String, Object> lingJianMapOutTime = regionDao.sendMonitoring4LingJian(requestParam);
		lingJianMap.put("outTimeCount", lingJianMapOutTime.get("count"));
		// 超时订单 - 还件
    	requestParam.put("stateKind", "huanJian");
    	Map<String, Object> huanJianMapOutTime = regionDao.sendMonitoring4HuanJian(requestParam);
    	huanJianMap.put("outTimeCount", huanJianMapOutTime.get("count"));
    	// 超时订单 - 还件揽收
    	requestParam.put("stateKind", "lingJianLanShou");
    	Map<String, Object> huanJianLanShouMapOutTime = regionDao.sendMonitoring4HuanJianLanShou(requestParam);
    	huanJianLanShouMap.put("outTimeCount", huanJianLanShouMapOutTime.get("count"));

    	// 暂无超时说法的节点
    	jieDanMap.put("outTimeCount", 0);
    	zuoYeMap.put("outTimeCount", 0);
    	fuWuWanChengMap.put("outTimeCount", 0);
    	fuWuTiJiaoMap.put("outTimeCount", 0);

    	List<Map<String, Object>> result = new ArrayList<>(16);
    	result.add(paiGongMap);
    	result.add(jiXiuMap);
    	result.add(jieDanMap);
    	result.add(zuoYeMap);
    	result.add(lingJianMap);
    	result.add(huanJianMap);
    	result.add(huanJianLanShouMap);
    	result.add(fuWuWanChengMap);
    	result.add(fuWuTiJiaoMap);

        return RetResponse.makeOKRsp(result);
    }

    /**
     * TAB页值
     * @param map
     * @return
     */
    @Override
    public RetResult tabValue(Map map) {
        map = dateConversion(map);
        Map tabValue = regionDao.tabValue(map);
        Map tabValue1 = regionDao.tabValue1(map);
        List<Map<String,Object>> tabValue2 = regionDao.tabValue2(map);
        List<Map<String,Object>> tabValue3 = regionDao.tabValue3(map);
        //将list里的全部投诉默认排列在list第一个位置
        if(CollectionUtils.isNotEmpty(tabValue2)){
            for(int index = 0 , length = tabValue2.size() ; index < length ; index++){
                if("全部投诉".equals(tabValue2.get(index).get("complaintType"))){
                    Map<String,Object> t2 = tabValue2.get(0);
                    tabValue2.set(0, tabValue2.get(index));
                    tabValue2.set(index, t2);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(tabValue3)){
            for(int index = 0 , length = tabValue3.size() ; index < length ; index++){
                if("全部投诉".equals(tabValue3.get(index).get("complaintType"))){
                    Map<String,Object> t3 = tabValue3.get(0);
                    tabValue3.set(0, tabValue3.get(index));
                    tabValue3.set(index, t3);
                }
            }
        }
        tabValue.put("firstPunctualityPercentage",tabValue1.get("firstPunctualityPercentage"));
        tabValue.put("punctualityPercentage",tabValue1.get("punctualityPercentage"));
        tabValue.put("solvePercentage",tabValue2);
        tabValue.put("schemePercentage",tabValue3);
        return RetResponse.makeOKRsp(tabValue);
    }

    /**
     * 派工单详情
     * @param map
     * @return
     */
    @Override
    public RetResult dispatchingDetail(Map map) {
        if(map.get("dispatchingOrder")==null){
            return RetResponse.makeErrRsp("派工单号不能为空！");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dispatchingOrder = (String) map.get("dispatchingOrder");
        //查询派工单信息
        Map dispatchingDetail = regionDao.dispatchingDetail(map);
        if(dispatchingDetail==null){
            return RetResponse.makeErrRsp("找不到派工单！");
        }
        //获取派工单状态
        String systemState = (String) dispatchingDetail.get("systemState");
        List<Map> parts = regionDao.getPartsByOrder(dispatchingOrder);
        List<String> documentNumbers = new ArrayList<>();
        if(parts!=null){
            for (Map pa : parts){
                documentNumbers.add((String) pa.get("documentNumber"));
            }
        }
        String dispatchingTime = (String) dispatchingDetail.get("dispatchingTime");
        if("已完成".equals(systemState)||"已提交".equals(systemState)){
            //查询调换部件信息
            List<Map> replaceList = regionDao.replaceList(dispatchingOrder);
            dispatchingDetail.put("replacePart",replaceList);
            //查询检修部件信息
            List<Map> repairList = regionDao.repairList(dispatchingOrder);
            dispatchingDetail.put("repairPart",repairList);

        }else {
            RetResult rr = this.dispatchingDetailApi(map);
            if(rr.getData() == null){
                //查询调换部件信息
                List<Map> replaceList = regionDao.replaceList(dispatchingOrder);
                dispatchingDetail.put("replacePart",replaceList);
                //查询检修部件信息
                List<Map> repairList = regionDao.repairList(dispatchingOrder);
                dispatchingDetail.put("repairPart",repairList);
            }else{
                dispatchingDetail = (Map) rr.getData();
                net.sf.json.JSONObject b = net.sf.json.JSONObject.fromObject(dispatchingDetail);
                System.out.println("--------------拉取硕德信息"+b.toString()+"----------------");
            }
        }
        dispatchingDetail.put("documentNumbers",documentNumbers);

        boolean yy = true;
        boolean yw = true;
        //进度条
        List<Map> pmgressBar = new ArrayList<>();
        if("安装".equals(dispatchingDetail.get("serviceType"))
                ||"维修".equals(dispatchingDetail.get("serviceType"))
                ||("鉴定".equals(dispatchingDetail.get("serviceType"))&&("安装".equals(dispatchingDetail.get("firstServiceType"))||"维修".equals(dispatchingDetail.get("firstServiceType"))))
        ){
            Map map1 = new HashMap<>();
            Date dispatchingDate = null;

            System.out.println("--------------进入上门时间轴"+dispatchingTime+"----------------");
            try {
                dispatchingDate = sdf.parse(dispatchingTime);
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("--------------派工时间："+dispatchingTime+"----------------");
            map1.put("派工",dispatchingTime);
            //pmgressBar.add(map1);
            Map map2 = new HashMap<>();
            map2.put("接单",dispatchingDetail.get("receiptTime"));
            System.out.println("--------------接单："+map2.get("接单")+"-"+yy+"----------------");
            //pmgressBar.add(map2);
            Map map3 = new HashMap<>();
            System.out.println("--------------预约条件："+dispatchingDetail.get("appointmentOperationTime")+"-"+(dispatchingDetail.get("appointmentOperationTime")!=null&&!"".equals(dispatchingDetail.get("appointmentOperationTime")))+"----------------");
            if(dispatchingDetail.get("appointmentOperationTime")!=null&&!"".equals(dispatchingDetail.get("appointmentOperationTime"))){
                map3.put("预约",dispatchingDetail.get("appointmentOperationTime"));
                yy=false;
                System.out.println("--------------预约："+map3.get("预约")+"-"+yy+"----------------");
            }

            Map map4 = new HashMap<>();
            map4.put("上门",dispatchingDetail.get("visitTime"));
            System.out.println("--------------上门："+map4.get("上门")+"-"+yy+"----------------");
            //pmgressBar.add(map4);
            Map map5 = new HashMap<>();
            map5.put("挂单","");
            //获取相关挂单记录
            List<Map> pendingList = regionDao.pendingOrderList(map);
            if(pendingList!=null&&pendingList.size()>0){
                map5.put("挂单",pendingList.get(0).get("pendingOrderTime"));
                yy = false;
                System.out.println("--------------挂单："+map5.get("挂单")+"-"+yy+"----------------");
            }
            //pmgressBar.add(map5);
            Map map6 = new HashMap<>();
            map6.put("作业订单","");
            Map map7 = new HashMap<>();
            map7.put("零件供应","");
            if(parts!=null&&parts.size()>0){
                map6.put("作业订单",parts.get(0).get("orderStartTime"));
                map7.put("零件供应",parts.get(0).get("appropriateInvestTime"));
                yy = false;
                System.out.println("--------------作业订单："+map6.get("作业订单")+"-"+yy+"----------------");
                System.out.println("--------------零件供应："+map7.get("零件供应")+"-"+yy+"----------------");
            }
            //pmgressBar.add(map6);
            //pmgressBar.add(map7);
            Map map8 = new HashMap<>();
            map8.put("解挂","");
            if(pendingList!=null&&pendingList.size()>0){
                map8.put("解挂",pendingList.get(0).get("finishOrderTime"));
                System.out.println("--------------解挂："+map8.get("解挂")+"-"+yy+"----------------");
            }
            //pmgressBar.add(map8);
            Map map9 = new HashMap<>();
            if(dispatchingDetail.get("TATFinishTime")!=null&&!"".equals(dispatchingDetail.get("TATFinishTime").toString().trim()) ){
                map9.put("服务完成",dispatchingDetail.get("TATFinishTime"));
                yw = false;
                yy = false;
                System.out.println("--------------服务完成1："+map9.get("服务完成")+"-"+yy+"----------------");
            }else if(dispatchingDetail.get("finishTime")!=null&&!"".equals(dispatchingDetail.get("finishTime").toString().trim())){
                map9.put("服务完成",dispatchingDetail.get("finishTime"));
                yw = false;
                yy = false;
                System.out.println("--------------服务完成2："+map9.get("服务完成")+"-"+yy+"----------------");
            }

            Map map10 = new HashMap<>();
            map10.put("服务提交",dispatchingDetail.get("submissionTime"));
            System.out.println("--------------服务提交："+map10.get("服务提交")+"-"+yy+"----------------");
            //pmgressBar.add(map10);
            System.out.println("--------------预计预约条件："+(yy&&dispatchingDetail.get("submissionTime")==null&&dispatchingDetail.get("visitTime")==null)+"----------------");
            if(yy&&(dispatchingDetail.get("submissionTime")==null||"".equals(dispatchingDetail.get("submissionTime").toString().trim()))&&(dispatchingDetail.get("visitTime")==null||"".equals(dispatchingDetail.get("visitTime").toString().trim()))) {
                map3.put("预约", "预计" + sdf.format(new Date(dispatchingDate.getTime() + (30 * 60 * 1000))) + "完成");
                System.out.println("--------------预计预约："+map3.get("预约")+"-"+yy+"----------------");
            }
            //pmgressBar.add(map3);
            if(yw&&(dispatchingDetail.get("submissionTime")==null||"".equals(dispatchingDetail.get("submissionTime").toString().trim()))){
                map9.put("服务完成","预计"+sdf.format(new Date(dispatchingDate .getTime() + (4*24*60*60*1000)))+"完成");
                System.out.println("--------------预计服务完成："+map9.get("服务完成")+"-"+yy+"----------------");
            }
            pmgressBar.add(map1);
            pmgressBar.add(map2);
            pmgressBar.add(map3);
            pmgressBar.add(map4);
            pmgressBar.add(map5);
            pmgressBar.add(map6);
            pmgressBar.add(map7);
            pmgressBar.add(map8);
            pmgressBar.add(map9);
            pmgressBar.add(map10);
            JSONArray a = JSONArray.fromObject(pmgressBar);
            System.out.println("--------------结束存储："+a.toString()+"----------------");
            dispatchingDetail.put("pmgressBar",pmgressBar);
        }else if("送修".equals(dispatchingDetail.get("serviceWay"))&&
                (
                        "非上门维修".equals(dispatchingDetail.get("serviceType"))
                                || ("鉴定".equals(dispatchingDetail.get("serviceType"))&&"非上门维修".equals(dispatchingDetail.get("firstServiceType")))
                )
        ){
            Map map1 = new HashMap<>();
            Date dispatchingDate = null;
            try {
                dispatchingDate = sdf.parse(dispatchingTime);
            }catch (Exception e){
                e.printStackTrace();
            }
            map1.put("派工",dispatchingTime);
            //pmgressBar.add(map1);
            Map map2 = new HashMap<>();
            map2.put("非上门服务单",dispatchingDetail.get("receiptTime"));
            //pmgressBar.add(map2);
            Map map3 = new HashMap<>();
            map3.put("作业订单","");
            Map map4 = new HashMap<>();
            map4.put("零件供应","");
            if(parts!=null&&parts.size()>0){
                map3.put("作业订单",parts.get(0).get("orderStartTime"));
                map4.put("零件供应",parts.get(0).get("appropriateInvestTime"));
            }
            //pmgressBar.add(map3);
            //pmgressBar.add(map4);
            Map map5 = new HashMap<>();
            map5.put("还件",dispatchingDetail.get("shopReturnTime"));
            //pmgressBar.add(map5);
            Map map6 = new HashMap<>();
            if(dispatchingDetail.get("TATFinishTime")!=null&&!"".equals(dispatchingDetail.get("TATFinishTime")) ){
                map6.put("服务完成",dispatchingDetail.get("TATFinishTime"));
                yw = false;
            }else if(dispatchingDetail.get("finishTime")!=null &&!"".equals(dispatchingDetail.get("finishTime"))){
                map6.put("服务完成",dispatchingDetail.get("finishTime"));
                yw = false;
            }
            Map map7 = new HashMap<>();
            map7.put("服务提交",dispatchingDetail.get("submissionTime"));
            //pmgressBar.add(map7);
            if(yw&&(dispatchingDetail.get("submissionTime")==null||"".equals(dispatchingDetail.get("submissionTime").toString().trim()))){
                map6.put("服务完成","预计"+sdf.format(new Date(dispatchingDate .getTime() + (4*24*60*60*1000)))+"完成");
            }
            pmgressBar.add(map1);
            pmgressBar.add(map2);
            pmgressBar.add(map3);
            pmgressBar.add(map4);
            pmgressBar.add(map5);
            pmgressBar.add(map6);
            pmgressBar.add(map7);
            dispatchingDetail.put("pmgressBar",pmgressBar);
        }else if("寄修".equals(dispatchingDetail.get("serviceWay"))&&
                (
                        "非上门维修".equals(dispatchingDetail.get("serviceType"))
                                || ("鉴定".equals(dispatchingDetail.get("serviceType"))&&"非上门维修".equals(dispatchingDetail.get("firstServiceType")))
                )
        ){
            Map map1 = new HashMap<>();

            Date dispatchingDate = null;
            try {
                dispatchingDate = sdf.parse(dispatchingTime);
            }catch (Exception e){
                e.printStackTrace();
            }
            map1.put("派工",dispatchingTime);
            //pmgressBar.add(map1);
            Map map2 = new HashMap<>();
            map2.put("寄修到件",dispatchingDetail.get("shopDeliveryTime"));
            //pmgressBar.add(map2);
            Map map3 = new HashMap<>();
            map3.put("非上门服务单",dispatchingDetail.get("receiptTime"));
            //pmgressBar.add(map3);
            Map map4 = new HashMap<>();
            map4.put("作业订单","");
            Map map5 = new HashMap<>();
            map5.put("零件供应","");
            if(parts!=null&&parts.size()>0){
                map4.put("作业订单",parts.get(0).get("orderStartTime"));
                map5.put("零件供应",parts.get(0).get("appropriateInvestTime"));
            }
            //pmgressBar.add(map4);
            //pmgressBar.add(map5);
            Map map6 = new HashMap<>();
            map6.put("还件",dispatchingDetail.get("shopReturnTime"));
            //pmgressBar.add(map6);
            Map map7 = new HashMap<>();
            map7.put("还件揽收",dispatchingDetail.get("returnCollectionTime"));
            //pmgressBar.add(map7);
            Map map8 = new HashMap<>();
            if(dispatchingDetail.get("TATFinishTime")!=null &&!"".equals(dispatchingDetail.get("TATFinishTime")) && (dispatchingDetail.get("appointmentOperationTime")==null||"".equals(dispatchingDetail.get("appointmentOperationTime")))){
                map8.put("服务完成",dispatchingDetail.get("TATFinishTime"));
                yw = false;
            }else if(dispatchingDetail.get("finishTime")!=null&&!"".equals(dispatchingDetail.get("finishTime"))&& (dispatchingDetail.get("appointmentOperationTime")==null||"".equals(dispatchingDetail.get("appointmentOperationTime")))){
                map8.put("服务完成",dispatchingDetail.get("finishTime"));
                yw = false;
            }
            Map map9 = new HashMap<>();
            map9.put("服务提交",dispatchingDetail.get("submissionTime"));
            //pmgressBar.add(map9);
            if(yw&&(dispatchingDetail.get("submissionTime")==null||"".equals(dispatchingDetail.get("submissionTime").toString().trim()))){
                map8.put("服务完成","预计"+sdf.format(new Date(dispatchingDate .getTime() + (4*24*60*60*1000)))+"完成");
            }
            pmgressBar.add(map1);
            pmgressBar.add(map2);
            pmgressBar.add(map3);
            pmgressBar.add(map4);
            pmgressBar.add(map5);
            pmgressBar.add(map6);
            pmgressBar.add(map7);
            pmgressBar.add(map8);
            pmgressBar.add(map9);
            dispatchingDetail.put("pmgressBar",pmgressBar);
        }
        return RetResponse.makeOKRsp(dispatchingDetail);
    }

    /**
     * 流转历史
     * @param map
     * @return
     */
    @Override
    public RetResult transferInformation(Map map) {
        if(map.get("associatedNumber")==null&&"".equals(map.get("associatedNumber"))){
            RetResponse.makeErrRsp("关联单号不能为空！");
        }
        try {
            List<Map> transferInformation = regionDao.transferInformation(map);
            map.put("dispatchingOrder",map.get("associatedNumber"));
            Map dis = regionDao.dispatchingDetail(map);
            if (transferInformation == null) {
                transferInformation = new ArrayList<>();
            }
            transferInformation.sort(Comparator.comparing((Map m) -> (objToTime(m.get("operationTime")))));
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long lastTime = null;
            if (transferInformation.size() > 0) {
                transferInformation.get(0).put("elapsedTime", "");
                transferInformation.get(0).put("remark", "(开始)");
                lastTime = dateFormat.parse((String) transferInformation.get(0).get("operationTime")).getTime();
                transferInformation.get(0).remove("relatedData");
            }
            for (int i = 1; i < transferInformation.size(); i++) {
                Map ti = transferInformation.get(i);
                String operationContent = (String) ti.get("operationContent");
                if("挂单".equals(operationContent)){
                    JSONObject js = JSONObject.parseObject((String) ti.get("relatedData"));
                    ti.put("remark", "挂单原因:"+js.get("pendingOrderCause"));
                }else {
                    Long time = dateFormat.parse((String) ti.get("operationTime")).getTime();
                    if(lastTime!=null&&time!=null){
                        Long elapsedTime = time-lastTime;
                        lastTime = time;
                        if("预约".equals(operationContent)&&dis.get("timelyEligible")!=null){
                            if("1".equals(dis.get("timelyEligible"))){
                                ti.put("remark", "(达标)");
                            }else if("0".equals(dis.get("timelyEligible"))){
                                ti.put("remark", "(未达标)");
                            }
                        }
                        /*int days = (int) (elapsedTime/(1000*60*60*24));
                        elapsedTime = elapsedTime - (days*1000*60*60*24);
                        int hours =(int) (elapsedTime/(1000*60*60));
                        elapsedTime = elapsedTime - (hours*1000*60*60);
                        int minutes =(int) (elapsedTime/(1000*60));
                        String elapsed = "历时";
                        if(days>0){
                            elapsed = elapsed + days +"天";
                        }
                        if(hours>0){
                            elapsed = elapsed + hours +"小时";
                        }
                        if(minutes>0){
                            elapsed = elapsed + minutes +"分钟";
                        }*/
                        ti.put("elapsedTime", elapsedTime/60000);

                    }
                }
                ti.remove("relatedData");
            }
            return RetResponse.makeOKRsp(transferInformation);
        }catch (Exception e){
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }
    /**
     * 服务门店管理列表
     * @param map
     * @return
     */
    @Override
    public RetResult storeManage(Map map) {
        JSONObject resJson = new JSONObject();
        try {
            //map = fieldQuery(map);
            map = dateConversion(map);
            map.put("FIELDS","rdd.storeNumber,rdd.storeName,rdd.accountingCenterCode,rdd.accountingArea,rdd.accountingAreaCode,reg.region");
            map.put("TABLES","LEFT JOIN (SELECT rdd3.storeNumber,CONCAT(rdd3.provinces,rdd3.city)AS region FROM t_region_service_store rdd3)reg ON reg.storeNumber = rss.storeNumber");
            map.put("GROUPFIELDS","rdd.storeNumber,rdd.accountingCenterCode");
            Map finalMap = map;
            Map finalMap1 = new HashMap<>(map);
            Future<List<Map>> submit1 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list = regionDao.indexList(finalMap);
                list = list.stream().filter(a -> a.get("storeName")!=null&&!"".equals(a.get("storeName"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("average","");
                    a.put("averagePercentage","");
                    a.put("avgDay","");
                    a.put("solvePercentage","");
                });
                return list;
            });
            Future<List<Map>> submit2 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                List<Map> list = regionDao.indexListT(finalMap);
                list = list.stream().filter(a -> a.get("storeName")!=null&&!"".equals(a.get("storeName"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("timelyPercentage","");
                    a.put("firstPunctualityPercentage","");
                    a.put("solvePercentage","");
                    a.put("repairPercentage","");
                });
                return list;
            });
            Future<List<Map>> submit3 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                finalMap1.put("FIELDS","ch.storeNumber,ch.storeName,ch.accountingCenterCode,ch.accountingArea,ch.accountingAreaCode,reg.region");
                finalMap1.put("TABLES","LEFT JOIN (SELECT rdd3.complaintNumber,CONCAT(rdd3.provinces,rdd3.city)AS region FROM t_complaint_handling rdd3)reg ON reg.complaintNumber = ch.complaintNumber");
                finalMap1.put("GROUPFIELDS","ch.storeNumber,ch.accountingCenterCode");
                List<Map> list = regionDao.indexListS(finalMap1);
                list = list.stream().filter(a -> a.get("storeName")!=null&&!"".equals(a.get("storeName"))).collect(Collectors.toList());
                list.stream().forEach(a ->{
                    a.put("timelyPercentage","");
                    a.put("firstPunctualityPercentage","");
                    a.put("repairPercentage","");
                    a.put("average","");
                    a.put("averagePercentage","");
                    a.put("avgDay","");
                });
                return list;
            });
            List<Map> indexList = submit1.get();
            List<Map> indexListT = submit2.get();
            List<Map> indexListS = submit3.get();
            List<Map> finalIndexList = indexList;
            indexList = indexListT.stream().map(a -> finalIndexList.stream()
                            .filter(b -> b.get("accountingAreaCode").toString().equals(a.get("accountingAreaCode"))&&b.get("storeNumber").toString().equals(a.get("storeNumber")))
                            .findFirst().map(b -> {
                                b.put("average",a.get("average"));
                                b.put("averagePercentage",a.get("averagePercentage"));
                                b.put("avgDay",a.get("avgDay"));
                                b.put("tt",1);
                                return b;
                            }).orElse(a))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            List<Map> a = finalIndexList.stream().filter(m->m.get("tt")==null).collect(Collectors.toList());
            indexList.addAll(a);
            if(CollectionUtils.isNotEmpty(indexListS)) {
//                List<Map> finalIndexList1 = indexList;
//                indexList = indexListS.stream().map(c -> finalIndexList1.stream()
//                                .filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode").toString()) && b.get("storeNumber").toString().equals(c.get("storeNumber").toString()) && b.get("engineerId").toString().equals(c.get("engineerId").toString()))
//                                .findFirst().map(b -> {
//                                    b.put("solveEligible", c.get("solveEligible"));
//                                    b.put("solveTotal", c.get("solveTotal"));
//                                    b.put("solvePercentage", c.get("solvePercentage"));
//                                    b.put("ttt", 1);
//                                    return b;
//                                }).orElse(c))
//                        .filter(Objects::nonNull).collect(Collectors.toList());
                for(Map c:indexListS){
                    Optional<Map> i = indexList.stream().filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode"))&&b.get("storeNumber").toString().equals(c.get("storeNumber"))).findFirst();
                    if(i.isPresent()){
                        Map b = i.get();
                        b.put("solveEligible", c.get("solveEligible"));
                        b.put("solveTotal", c.get("solveTotal"));
                        b.put("solvePercentage", c.get("solvePercentage"));
                    }else {
                        indexList.add(c);
                    }
                }
//                List<Map> b = finalIndexList1.stream().filter(m -> m.get("ttt") == null).collect(Collectors.toList());
//                indexList.addAll(b);
            }
//            List<Map> finalIndexList1 = indexList;
//            indexList = indexListS.stream().map(c -> finalIndexList1.stream()
//                            .filter(b -> b.get("accountingAreaCode").toString().equals(c.get("accountingAreaCode"))&&b.get("storeNumber").toString().equals(c.get("storeNumber")))
//                            .findFirst().map(b -> {
//                                b.put("solveEligible",c.get("solveEligible"));
//                                b.put("solveTotal",c.get("solveTotal"));
//                                b.put("solvePercentage",c.get("solvePercentage"));
//                                b.put("ttt",1);
//                                return b;
//                            }).orElse(c))
//                    .filter(Objects::nonNull).collect(Collectors.toList());
//            List<Map> b = finalIndexList1.stream().filter(m->m.get("ttt")==null).collect(Collectors.toList());
//            indexList.addAll(b);
            indexList = fieldQuery(map,indexList);
            resJson.put("count", indexList.size());
            Integer page = (Integer)map.get("page");
            if(page==null){
                page = 1;
            }
            int end = (page-1)*10+10;
            if(end>indexList.size()){
                end = indexList.size();
            }
            indexList = indexList.subList((page-1)*10, end);
            listToList(indexList,map);
            resJson.put("data", indexList);

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp(e.getMessage());
        }
        return RetResponse.makeOKRsp(resJson);
    }
    /**
     * 改约率
     * @param map
     * @return
     */
    @Override
    public RetResult reschedule(Map map) {
        //处理日期
        map = dateConversion(map);
        Map returnMap = new HashMap<>();
        //获取时间范围内率
        Map rate = regionDao.reschedule(map);
        returnMap.put("rate",rate);
        //获取时间范围内日率
        List<Map> dateRate =  regionDao.dateReschedule(map);
        //给没有数据的日期补零并排序
        dateRate = dateAll((String) map.get("startTime"), (String) map.get("endTime"),dateRate);
        returnMap.put("dateRate",dateRate);
        try {
            //开始时间结束时间月份-1
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse((String) map.get("startTime")));
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            map.put("startTime",dateFormat.format(calendar.getTime()));
            calendar.setTime(dateFormat.parse((String) map.get("endTime")));
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            map.put("endTime",dateFormat.format(calendar.getTime()));
            //获取时间范围开始和结束时间减一个月日率
            /*List<Map> lastDateRate =  regionDao.dateReschedule(map);
            lastDateRate = dateAll((String) map.get("startTime"), (String) map.get("endTime"),lastDateRate);
            returnMap.put("lastDateRate",lastDateRate);*/
            //如果是本月统计环比
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))) {
                Map lastRate = regionDao.reschedule(map);
                if("0".equals(lastRate.get("rate"))){
                    returnMap.put("QOQ", "0");
                }else {
                    BigDecimal now =new BigDecimal((String) rate.get("rate")) ;
                    BigDecimal last = new BigDecimal((String) lastRate.get("rate"));
                    BigDecimal bfb = new BigDecimal(100);
                    BigDecimal QOQ = ((now.subtract(last)).multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP));
                    returnMap.put("QOQ", QOQ.toString());
                }
                returnMap.put("target", getIndicator(map,"reductionRate"));
            }else{
                returnMap.put("QOQ", "");
                returnMap.put("target", "");
            }
        }catch (Exception e){

        }
        return RetResponse.makeOKRsp(returnMap);
    }

    @Override
    public RetResult updateAll(Map map) {
        long start = System.currentTimeMillis();
        long st = 0L;
        long en = 0L;
        Date startTime = null;
        Date endTime = null;
        int v1 = 0;
        int t1 = 0;
        int f1 = 0;
        int p1 = 0;
        int a1 = 0;
        int s1 = 0;
        int r1 = 0;
        int m1 = 0;
        int c1 = 0;
        int num = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String ret = "";
        String ret1 = "";
       /* if(map.get("startTime")==null||map.get("endTime")==null){
            endTime = new Date();
            calendar.setTime(endTime);
            calendar.add(Calendar.YEAR, -1);
            startTime = calendar.getTime();
            ret = "没有开始或结束时间，默认执行"+sdf.format(startTime)+"-"+sdf.format(endTime)+"范围派工单标签更新：";
        }else{
            try{
                startTime = sdf.parse((String) map.get("startTime"));
                endTime = sdf.parse((String) map.get("endTime"));
                ret = "执行"+sdf.format(startTime)+"-"+sdf.format(endTime)+"范围派工单标签更新：";
            }catch (Exception e){
                return RetResponse.makeErrRsp("请输入yyyy-MM-dd HH:mm:ss格式日期");
            }
        }
        if(startTime.getTime()>endTime.getTime()){
            return RetResponse.makeErrRsp("开始时间不能大于结束时间");
        }*/
        //while (true){
           /* if(startTime.getTime()>=endTime.getTime()){
                break;
            }*/
            /*st = System.currentTimeMillis();
            map.put("startTime",sdf.format(startTime));
            calendar.setTime(startTime);
            calendar.add(Calendar.DATE, 5);
            startTime = calendar.getTime();
            if(startTime.getTime()>endTime.getTime()){
                map.put("endTime",sdf.format(endTime));
            }else {
                map.put("endTime",sdf.format(startTime));
            }
            ret1 = ret1 +map.get("startTime")+"-"+map.get("endTime")+":";*/
        ret1 = ret1 + "完成时间更新：";
        try {
            num = regionDao.TATFinishTime(map);
            v1 = v1 + num;
            ret1 = ret1 + num +"条；";
        }catch (Exception e1){
            ret1 = ret1 + "报错；";
        }
            ret1 = ret1 + "平均服务时长更新：";
            try {
                num = regionDao.averageTime(map);
                v1 = v1 + num;
                ret1 = ret1 + num +"条；";
            }catch (Exception e1){
                ret1 = ret1 + "报错；";
            }
            ret1 = ret1 + "30分钟预约及时率更新：";
            try {
                num = regionDao.timelyEligible(map);
                t1 = t1 + num;
                ret1 = ret1 + num +"条；";
            }catch (Exception e1){
                ret1 = ret1 + "报错；";
            }
            ret1 = ret1 + "首次预约准时上门率更新：";
            try {
                num = regionDao.firstPunctualityEligible(map);
                f1 = f1 + num;
                ret1 = ret1 + num +"条；";
            }catch (Exception e1){
                ret1 = ret1 + "报错；";
            }
            ret1 = ret1 + "预约准时上门率更新：";
            try {
                num = regionDao.punctualityEligible(map);
                p1 = p1 + num;
                ret1 = ret1 + num +"条；";
            }catch (Exception e1){
                ret1 = ret1 + "报错；";
            }
            ret1 = ret1 + "TAT4天达成率更新：";
            try {
                num = regionDao.averageEligible(map);
                a1 = a1 + num;
                ret1 = ret1 + num +"条；";
            }catch (Exception e1){
                ret1 = ret1 + "报错；";
            }
            ret1 = ret1 + "投诉7天解决率更新：";
            try {
                num = regionDao.solveEligible(map);
                s1 = s1 + num;
                ret1 = ret1 + num +"条；";
            }catch (Exception e1){
                ret1 = ret1 + "报错；";
            }
            ret1 = ret1 + "一次修复率更新：";
            try {
                num = regionDao.repairEligible(map);
                r1 = r1 + num;
                ret1 = ret1 + num +"条；";
            }catch (Exception e1){
                ret1 = ret1 + "报错；";
            }
            /*ret1 = ret1 + "2天维修达成率更新：";
            try {
                num = regionDao.maintainEligible(map);
                m1 = m1 + num;
                ret1 = ret1 + num +"条；";
            }catch (Exception e1){
                ret1 = ret1 + "报错；";
            }*/
            ret1 = ret1 + "N+1投诉解决方案提交率更新：";
            try {
                num = regionDao.schemeEligible(map);
                c1 = c1 + num;
                ret1 = ret1 + num +"条。";
            }catch (Exception e1){
                ret1 = ret1 + "报错。";
            }
            en = System.currentTimeMillis();
            ret1 = ret1 + "耗时"+(en-st)+"毫秒。\r\n";
        //}
        long end = System.currentTimeMillis();
        int i = (int) (end - start);
        String time = "";
        if(i>1000*60*60){
            time = time + (i/(1000*60*60))+"小时";
        }
        if(i>1000*60){
            time = time + (i%(1000*60*60)/(1000*60))+"分钟";
        }
        if(i>1000){
            time = time + (i%(1000*60)/1000)+"秒";
        }
        time = time + (i%1000)+"毫秒。\r\n";
        String rr = ret+"平均服务时长共"+v1+"条；30分钟预约及时率共"+t1+"条；首次预约准时上门率共"+f1+"条；预约准时上门率共"+p1+"条；TAT4天达成率更新共"+a1+"条；投诉7天解决率共"+s1+"条；一次修复率共"+r1+"条；2天维修达成率共"+m1+"条；N+1投诉解决方案提交率"+c1+"条；共耗时"+time+ret1;
        System.out.println(rr);
        return RetResponse.makeOKRsp(rr);
    }

    @Override
    public RetResult updateTAT(Map map) {

        long start = System.currentTimeMillis();
        long st = 0L;
        long en = 0L;
        Date startTime = null;
        Date endTime = null;
        int t1 = 0;
        int f1 = 0;
        int p1 = 0;
        int a1 = 0;
        int s1 = 0;
        int r1 = 0;
        int m1 = 0;
        int c1 = 0;
        int num = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String ret = "";
        String ret1 = "";
        if(map.get("startTime")==null||map.get("endTime")==null){
            endTime = new Date();
            calendar.setTime(endTime);
            calendar.add(Calendar.YEAR, -1);
            startTime = calendar.getTime();
            ret = "没有开始或结束时间，默认执行"+sdf.format(startTime)+"-"+sdf.format(endTime)+"范围派工单标签更新：";
        }else{
            try{
                startTime = sdf.parse((String) map.get("startTime"));
                endTime = sdf.parse((String) map.get("endTime"));
                ret = "执行"+sdf.format(startTime)+"-"+sdf.format(endTime)+"范围派工单标签更新：";
            }catch (Exception e){
                return RetResponse.makeErrRsp("请输入yyyy-MM-dd HH:mm:ss格式日期");
            }
        }
        if(startTime.getTime()>endTime.getTime()){
            return RetResponse.makeErrRsp("开始时间不能大于结束时间");
        }
        while (true){
            if(startTime.getTime()>=endTime.getTime()){
                break;
            }
            st = System.currentTimeMillis();
            map.put("startTime",sdf.format(startTime));
            calendar.setTime(startTime);
            calendar.add(Calendar.DATE, 5);
            startTime = calendar.getTime();
            if(startTime.getTime()>endTime.getTime()){
                map.put("endTime",sdf.format(endTime));
            }else {
                map.put("endTime",sdf.format(startTime));
            }
            ret1 = ret1 +map.get("startTime")+"-"+map.get("endTime")+":";
            ret1 = ret1 + "TAT平均服务时长更新：";
            try {
                num = regionDao.averageTime(map);
                t1 = t1 + num;
                ret1 = ret1 + num +"条；";
            }catch (Exception e1){
                ret1 = ret1 + "报错；";
            }

            en = System.currentTimeMillis();
            ret1 = ret1 + "耗时"+(en-st)+"毫秒。\r\n";
        }
        long end = System.currentTimeMillis();
        int i = (int) (end - start);
        String time = "";
        if(i>1000*60*60){
            time = time + (i/(1000*60*60))+"小时";
        }
        if(i>1000*60){
            time = time + (i%(1000*60*60)/(1000*60))+"分钟";
        }
        if(i>1000){
            time = time + (i%(1000*60)/1000)+"秒";
        }
        time = time + (i%1000)+"毫秒。\r\n";
        return RetResponse.makeOKRsp(ret+"TAT平均服务时长更新共"+t1+"条；共耗时"+time+ret1);
    }

    @Override
    public RetResult allList(Map map) {
        JSONObject resJson = new JSONObject();
        try{
            if(map.get("startTime")==null||"".equals(map.get("startTime"))||map.get("endTime")==null||"".equals(map.get("endTime"))){
                return RetResponse.makeErrRsp("请传入时间范围！");
            }
            //处理日期
            map = dateConversion(map);
            //处理前端传的错误from参数
            if(map.get("from")!=null){
                map.put("from",map.get("from").toString().replace("Percentage","Eligible"));
            }
            String where = "";
            if(map.get("serviceType")!=null&&"上门".equals(map.get("serviceType"))){
                where+=" AND (rdd.serviceType = '维修' OR rdd.serviceType = '安装' OR (rdd.serviceType = '鉴定' AND (rdd.firstServiceType = '维修' OR rdd.firstServiceType = '安装')))";
            }else if(map.get("serviceType")!=null&&"送修".equals(map.get("serviceType"))){
                where+=" AND rdd.serviceWay='送修' AND (rdd.serviceType = '非上门维修' OR (rdd.serviceType = '鉴定' AND rdd.firstServiceType = '非上门维修'))";
            }else if(map.get("serviceType")!=null&&"寄修".equals(map.get("serviceType"))){
                where+=" AND rdd.serviceWay='寄修' AND (rdd.serviceType = '非上门维修' OR (rdd.serviceType = '鉴定' AND rdd.firstServiceType = '非上门维修'))";
            }
            //一些指标需要去除来源为自接，营销中心，前置渠道
            if(map.get("from")!=null&&("timelyEligible".equals(map.get("from"))||"punctualityEligible".equals(map.get("from"))||"fristPunctualityEligible".equals(map.get("from"))||"averageEligible".equals(map.get("from"))||"repairEligible".equals(map.get("from")))){
                where+=" AND dispatchingSource not in ('自接','营销中心','前置渠道')";
            }
            //某些指标只要上门单
            if(map.get("from")!=null&&("timelyEligible".equals(map.get("from"))||"punctualityEligible".equals(map.get("from"))||"fristPunctualityEligible".equals(map.get("from")))){
                where+=" AND (rdd.serviceType = '维修' OR rdd.serviceType = '安装' OR (rdd.serviceType = '鉴定' AND (rdd.firstServiceType = '维修' OR rdd.firstServiceType = '安装')))";
            }
            //某些指标只要维修单
            if(map.get("from")!=null&&("repairEligible".equals(map.get("from")))){
                where+=" AND (rdd.serviceType = '维修' OR rdd.serviceType = '非上门维修' )";
            }
            map = fieldQuery(map);
            where = where.replaceFirst("AND","");
            map.put("WHERE",where);
            if("还件签收&服务完成".equals(map.get("nodeName"))){
                map.put("nodeName","服务完成");
            }
            //查询数据
            Integer count = regionDao.allListCount(map);
            resJson.put("count", count);
            Integer page = (Integer)map.get("page");
            if(page==null){
                page = 1;
                map.put("page",page);
            }
            map.put("pageStart",(page-1)*10);
            List<Map> list = regionDao.allList(map);
            /*if(map.get("serviceType")!=null&&"上门".equals(map.get("serviceType"))){
                list = list.stream().filter(o -> ("安装".equals(o.get("serviceType"))||"维修".equals(o.get("serviceType"))||("鉴定".equals(o.get("serviceType"))&&("安装".equals(o.get("firstServiceType"))||"维修".equals(o.get("firstServiceType")))))).collect(Collectors.toList());
            }else if(map.get("serviceType")!=null&&"送修".equals(map.get("serviceType"))){
                list = list.stream().filter(o -> ("送修".equals(o.get("serviceWay"))&&("非上门维修".equals(o.get("serviceType"))||("鉴定".equals(o.get("serviceType"))&&"非上门维修".equals(o.get("firstServiceType")))))).collect(Collectors.toList());
            }else if(map.get("serviceType")!=null&&"寄修".equals(map.get("serviceType"))){
                list = list.stream().filter(o -> ("寄修".equals(o.get("serviceWay"))&&("非上门维修".equals(o.get("serviceType"))||("鉴定".equals(o.get("serviceType"))&&"非上门维修".equals(o.get("firstServiceType")))))).collect(Collectors.toList());
            }*/

            //增加地区
//            list.stream().forEach(m ->m.put("region",(m.get("provinces")==null?"":m.get("provinces").toString())+(m.get("city")==null?"":m.get("city").toString())));

            //增加流转时间
//            list.stream().forEach(m ->getTransferTime(m));


            /*//一些指标需要去除来源为自接，营销中心，前置渠道
            if(map.get("from")!=null&&("timelyEligible".equals(map.get("from"))||"punctualityEligible".equals(map.get("from"))||"fristPunctualityEligible".equals(map.get("from"))||"averageEligible".equals(map.get("from"))||"repairEligible".equals(map.get("from")))){
                list = list.stream().filter(o -> !("自接".equals(o.get("dispatchingSource"))||"营销中心".equals(o.get("dispatchingSource"))||"前置渠道".equals(o.get("dispatchingSource")))).collect(Collectors.toList());
            }
            //某些指标只要上门单
            if(map.get("from")!=null&&("timelyEligible".equals(map.get("from"))||"punctualityEligible".equals(map.get("from"))||"fristPunctualityEligible".equals(map.get("from")))){
                list = list.stream().filter(o -> ("安装".equals(o.get("serviceType"))||"维修".equals(o.get("serviceType"))||("鉴定".equals(o.get("serviceType"))&&("安装".equals(o.get("firstServiceType"))||"维修".equals(o.get("firstServiceType")) ) ) )).collect(Collectors.toList());
            }
            //某些指标只要维修单
            if(map.get("from")!=null&&("repairEligible".equals(map.get("from")))){
                list = list.stream().filter(o -> ("维修".equals(o.get("serviceType"))||"非上门维修".equals(o.get("serviceType")))).collect(Collectors.toList());
            }*/
            //某些指标只要投诉单
//            if(map.get("from")!=null&&(("solveEligible".equals(map.get("from")))||"schemeEligible".equals(map.get("from")))){
//                List<String> isComplaint = regionDao.isComplaint(map);
//                list = list.stream().filter(o -> isComplaint.contains(o.get("dispatchingOrder"))).collect(Collectors.toList());
//            }
            //平均时长要去掉没打标签的
            /*if(map.get("from")!=null&&("averageEligible".equals(map.get("from")))||("solveEligible".equals(map.get("from")))||("repairEligible".equals(map.get("from")))||("schemeEligible".equals(map.get("from")))){
                String from = map.get("from").toString();
                list = list.stream().filter(o -> o.get(from)!=null).collect(Collectors.toList());
            }*/
         /*   //仅显示投诉单筛选
            if(map.get("isComplaint")!=null&&"1".equals(map.get("isComplaint"))){
                List<String> isComplaint = regionDao.isComplaint(map);
                list = list.stream().filter(o -> isComplaint.contains(o.get("dispatchingOrder"))).collect(Collectors.toList());
            }*/
            //列筛选
            /*list = fieldQuery(map,list);*/
            //异常筛选
            /*list = errQuery(map,list);*/
            //时间轴节点
            /*list = nodeQuery(map,list);*/
            //总记录数
            /*resJson.put("count", list.size());
            //分页
            Integer page = (Integer)map.get("page");
            if(page==null){
                page = 1;
            }
            int end = (page-1)*10+10;
            if(end>list.size()){
                end = list.size();
            }
            list = list.subList((page-1)*10, end);*/
            //存返回结果
            resJson.put("data", list);
            return RetResponse.makeOKRsp(resJson);
        }catch (Exception e){
            return RetResponse.makeErrRsp(e.getMessage());
        }
    }

    /**
     * 内部投诉7天解决率
     * @param complaintDto
     * @return
     */
    @Override
    public Map<String, Object> innerSevenDaySolveRate(RegionComplaintDto complaintDto) {

        //查询投诉7天解决率和投诉件数
        Map<String,Object> sevenDaySolve = regionDao.sevenDaySolveRate(complaintDto);
        //分组查询每天的解决率
        List<Map<String,Object>> averageSolve = regionDao.averageSolveRate(complaintDto);

        //存放所有的七天解决率数据
        List<Map<String,Object>> sevenDaysSolveList = new ArrayList<>();

        List<String> betweenDate = null;
        LocalDate toDay = LocalDate.now();
        LocalDate endDate = LocalDate.parse(complaintDto.getEndDate());
        if(endDate.isAfter(toDay)){
            endDate = toDay;
        }
        betweenDate = DateUtil.getBetweenDate(complaintDto.getBeginDate(),endDate.toString());

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

        //获取目标
        Map<String, Object> map = new HashMap<>();
        map.put("endTime",complaintDto.getEndDate());
        if(StringUtils.isNotEmpty(complaintDto.getAccountingAreaCode())){
            map.put("accountingAreaCode",complaintDto.getAccountingAreaCode());
        }
        if(StringUtils.isNotEmpty(complaintDto.getStoreNumber())){
            map.put("storeNumber",complaintDto.getStoreNumber());
        }
        sevenDaySolve.put("target", getIndicator(map,"resolutionRate"));

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
                Map<String,Object> lastMonthSolve = regionDao.sevenDaySolveRate(complaintDto);

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
    public Map<String, Object> complaintSolveSubmissionRate(RegionComplaintDto complaintDto) {

        //按月或按日查询N+1解决方案及时提交率
        List<Map<String,Object>> daysSubmissionRate = regionDao.daysSubmissionRate(complaintDto);

        //查询N+1解决方案及时提交率
        Map<String, Object> submissionRate = regionDao.complaintSolveSubmissionRate(complaintDto);

        //存放所有的N+1提交率数据
        List<Map<String,Object>> submissionRateList = new ArrayList<>();

        List<String> betweenDate = null;
        if ("1".equals(complaintDto.getPolymerizeWay())) {
            betweenDate = DateUtil.getBetweenMonth(complaintDto.getBeginDate(),complaintDto.getEndDate());
        }else {
            LocalDate toDay = LocalDate.now();
            LocalDate endDate = LocalDate.parse(complaintDto.getEndDate());
            if(endDate.isAfter(toDay)){
                endDate = toDay;
            }
            betweenDate = DateUtil.getBetweenDate(complaintDto.getBeginDate(),endDate.toString());
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
        //获取目标
        Map<String, Object> map = new HashMap<>();
        map.put("endTime",complaintDto.getEndDate());
        if(StringUtils.isNotEmpty(complaintDto.getAccountingAreaCode())){
            map.put("accountingAreaCode",complaintDto.getAccountingAreaCode());
        }
        if(StringUtils.isNotEmpty(complaintDto.getStoreNumber())){
            map.put("storeNumber",complaintDto.getStoreNumber());
        }
        submissionRate.put("target", getIndicator(map,"subRate"));

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
                Map<String,Object> lastMonthSubmission = regionDao.complaintSolveSubmissionRate(complaintDto);

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
     * 查询投诉类型
     * @param
     * @return
     */
    @Override
    public List<String> selectComplaintType(RegionComplaintDto complaintDto) {
        return regionDao.selectComplaintType(complaintDto);
    }

    /**
     * ------------------- 通用方法 -----------------------
     */

    //访问硕德接口统一方法
    Map httpClient(String url,Map param){
        //获取接口访问地址
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        url = IP + url;
        String postResult = "";
        Map map = new HashMap();
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(param));
            postResult = postResult.replace("\\", "\\\\");
            char[] temp = postResult.toCharArray();
            int n = temp.length;
            for (int i = 0; i < n; i++) {
                if (temp[i] == ':' && temp[i + 1] == '"') {
                    for (int j = i + 2; j < n; j++) {
                        if (temp[j] == '"') {
                            if ((temp[j + 1] != ',' && temp[j + 1] != '}') || (temp[j + 1] == ',' && temp[j + 2] != '"')) {
                                temp[j] = '”';
                            } else if (temp[j + 1] == ',' || temp[j + 1] == '}') {
                                break;
                            }
                        }
                    }
                }
            }
            postResult = new String(temp);
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

    //处理标签聚合的
    Map dateConversion(Map map){
        if(map.get("startTime")!=null&&!"".equals(map.get("startTime"))){
            map.put("startTime",map.get("startTime")+" 00:00:00");
        }
        if(map.get("endTime")!=null&&!"".equals(map.get("endTime"))){
            map.put("endTime",map.get("endTime")+" 23:59:59");
        }
        return map;
    }
//    小时数转*天*小时格式
    String hoursToDay(Object timeStr){
        String ret = "";
        try{
            Integer time = (int)Double.parseDouble(timeStr.toString());
            int day = time/24;
            int hours = time%24;
            if(day>0){
                ret= ret+day+"天";
            }
            if(hours>0) {
                ret = ret + hours + "小时";
            }
        }catch (Exception e){
        }
        return ret;
    }
    //计算两个日期的时长（分钟）
    private long stoelong(Object ss,Object es,SimpleDateFormat sdf){

        try {
            Date sd = sdf.parse(ss.toString());
            Date ed = sdf.parse(es.toString());
            long lt = ed.getTime()-sd.getTime();
            lt = lt / 60000;
            return lt;
        }catch (Exception e){
            return 0;
        }
    }
    //写入流转时间和当前停留时间
    private void getTransferTime(Map map){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date ed = new Date();
            if(map.get("TATFinishTime")!=null&&!"".equals(map.get("TATFinishTime"))){
                ed = sdf.parse(map.get("TATFinishTime").toString());
            }
            Date sd = sdf.parse(map.get("dispatchingTime").toString());
            long lt = ed.getTime()-sd.getTime();
            lt = lt / 3600000;
            map.put("transferTime",""+lt);
        }catch (Exception e){
            map.put("transferTime","");
        }
        if(!"已完成".equals(map.get("systemState"))&&!"已提交".equals(map.get("systemState"))){
            try {
                Date ed = new Date();
                Date sd = sdf.parse(map.get("updateTime").toString());
                long lt = ed.getTime()-sd.getTime();
                lt = lt / 3600000;
                map.put("longTime","当前已停留"+lt+"小时");
            }catch (Exception e){
                map.put("longTime","");
            }
        }

    }
    //后端处理异常监控轴节点筛选
    private List<Map> nodeQuery(Map map,List<Map> lists) {
        if(lists!=null&&map!=null&&map.get("nodeName")!=null&&!"".equals(map.get("nodeName"))&&map.get("serviceType")!=null&&!"".equals(map.get("serviceType"))) {
            String nodeName = map.get("nodeName").toString();
            String serviceType = map.get("serviceType").toString();
            if("上门".equals(serviceType)){
                if("派工".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("receiptTime")==null&&o.get("appointmentOperationTime")==null&&o.get("firstVisitTime")==null&&o.get("pendingState")==null&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
                }else if("接单".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("receiptTime")!=null&&o.get("appointmentOperationTime")==null&&o.get("firstVisitTime")==null&&o.get("pendingState")==null&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
                }else if("预约".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("appointmentOperationTime")!=null&&o.get("firstVisitTime")==null&&o.get("pendingState")==null&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
                }else if("上门".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("firstVisitTime")!=null&&o.get("pendingState")==null&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
                }else if("挂单".equals(nodeName)){
                    lists = lists.stream().filter(o -> ("挂单".equals(o.get("pendingState"))&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
                }else if("作业订单".equals(nodeName)){
                    lists = lists.stream().filter(o -> (!"解挂".equals(o.get("pendingState"))&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    orderList = orderList.stream().filter(o-> o.get("appropriateInvestTime")==null).collect(Collectors.toList());
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")!=null).collect(Collectors.toList());
                }else if("零件供应".equals(nodeName)){
                    lists = lists.stream().filter(o -> (!"解挂".equals(o.get("pendingState"))&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    orderList = orderList.stream().filter(o-> o.get("appropriateInvestTime")!=null).collect(Collectors.toList());
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("appropriateInvestTime",i.get().get("appropriateInvestTime"));
                        }else {
                            e.put("appropriateInvestTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("appropriateInvestTime")!=null).collect(Collectors.toList());
                }else if("解挂".equals(nodeName)){
                    lists = lists.stream().filter(o -> ("解挂".equals(o.get("pendingState"))&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                }else if("服务完成".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("finishTime")!=null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                }else if("服务提交".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("submissionTime")!=null)).collect(Collectors.toList());
                }
            }else if("送修".equals(serviceType)){
                if("派工".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("serviceNumber")==null&&o.get("shopReturnTime")==null&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
                }else if("非上门服务单".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("serviceNumber")!=null&&o.get("shopReturnTime")==null&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
                }else if("作业订单".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("shopReturnTime")==null&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    orderList = orderList.stream().filter(o-> o.get("appropriateInvestTime")==null).collect(Collectors.toList());
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")!=null).collect(Collectors.toList());
                }else if("零件供应".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("shopReturnTime")==null&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    orderList = orderList.stream().filter(o-> o.get("appropriateInvestTime")!=null).collect(Collectors.toList());
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("appropriateInvestTime",i.get().get("appropriateInvestTime"));
                        }else {
                            e.put("appropriateInvestTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("appropriateInvestTime")!=null).collect(Collectors.toList());
                }else if("还件".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("shopReturnTime")!=null&&o.get("finishTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                }else if("服务完成".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("finishTime")!=null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                }else if("服务提交".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("submissionTime")!=null)).collect(Collectors.toList());
                }
            }else if("寄修".equals(serviceType)){
                if("派工".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("shopDeliveryTime")==null&&o.get("serviceNumber")==null&&o.get("shopReturnTime")==null&&o.get("returnCollectionTime")==null&&o.get("finishTime")==null&&o.get("customerDeliveryTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    List<Map> orderList = regionDao.orderList(map);
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
                }else if("寄修到件".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("shopDeliveryTime")!=null&&o.get("serviceNumber")==null&&o.get("shopReturnTime")==null&&o.get("returnCollectionTime")==null&&o.get("finishTime")==null&&o.get("customerDeliveryTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    List<Map> orderList = regionDao.orderList(map);
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
                }else if("非上门服务单".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("serviceNumber")!=null&&o.get("shopReturnTime")==null&&o.get("returnCollectionTime")==null&&o.get("finishTime")==null&&o.get("customerDeliveryTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")==null).collect(Collectors.toList());
                }else if("作业订单".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("shopReturnTime")==null&&o.get("returnCollectionTime")==null&&o.get("finishTime")==null&&o.get("customerDeliveryTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    orderList = orderList.stream().filter(o-> o.get("appropriateInvestTime")==null).collect(Collectors.toList());
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("orderStartTime",i.get().get("orderStartTime"));
                        }else {
                            e.put("orderStartTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("orderStartTime")!=null).collect(Collectors.toList());
                }else if("零件供应".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("shopReturnTime")==null&&o.get("returnCollectionTime")==null&&o.get("finishTime")==null&&o.get("customerDeliveryTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                    //查询作业订单
                    List<Map> orderList = regionDao.orderList(map);
                    orderList = orderList.stream().filter(o-> o.get("appropriateInvestTime")!=null).collect(Collectors.toList());
                    List<Map> finalOrderList = orderList;
                    lists.stream().forEach(e -> {
                        Optional<Map> i = finalOrderList.stream().filter(item -> item.get("dispatchingOrder").equals(e.get("dispatchingOrder"))).findFirst();
                        if(i.isPresent()){
                            e.put("appropriateInvestTime",i.get().get("appropriateInvestTime"));
                        }else {
                            e.put("appropriateInvestTime",null);
                        }
                    });
                    lists = lists.stream().filter(o -> o.get("appropriateInvestTime")!=null).collect(Collectors.toList());
                }else if("还件".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("shopReturnTime")!=null&&o.get("returnCollectionTime")==null&&o.get("finishTime")==null&&o.get("customerDeliveryTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                }else if("还件揽收".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("returnCollectionTime")!=null&&o.get("finishTime")==null&&o.get("customerDeliveryTime")==null&&o.get("submissionTime")==null)).collect(Collectors.toList());
                }else if("还件签收&服务完成".equals(nodeName)){
                    lists = lists.stream().filter(o -> ((o.get("finishTime")!=null||o.get("customerDeliveryTime")!=null)&&o.get("submissionTime")==null)).collect(Collectors.toList());
                }else if("服务提交".equals(nodeName)){
                    lists = lists.stream().filter(o -> (o.get("submissionTime")!=null)).collect(Collectors.toList());
                }
            }
        }
        return lists;
    }
    //后端异常单筛选
    private List<Map> errQuery(Map map,List<Map> lists) {
        if(lists!=null&&map!=null&&map.get("isErr")!=null&&"1".equals(map.get("isErr"))&&map.get("from")!=null&&!"".equals(map.get("from"))) {
            String from = map.get("from").toString();
            if("ALL".equalsIgnoreCase(from)){
                lists = lists.stream().filter(o -> ("0".equals(o.get("timelyEligible"))||"0".equals(o.get("punctualityEligible"))||"0".equals(o.get("averageEligible"))||"0".equals(o.get("solveEligible"))||"0".equals(o.get("repairEligible"))||"0".equals(o.get("schemeEligible")) )).collect(Collectors.toList());
            }else{
                lists = lists.stream().filter(o -> ("0".equals(o.get(from)))).collect(Collectors.toList());
            }
        }
        return lists;
    }
    //字段查询将中文转成条件语句
    private List<Map> fieldQuery(Map map,List<Map> lists) {
        if(lists!=null&&map!=null) {
            List<Map> queryList = (List<Map>) map.get("queryList");
            if (queryList != null) {
                for (Map map1 : queryList) {
                    String queryHandle = (String) map1.get("queryHandle");
                    String q = (String) map1.get("queryName");
                    if ("avgDay".equals(q)) {
                        q = "average";
                    }
                    String queryName = q;
                    String queryValue = (String) map1.get("queryValue");
                    //String where = "";
                    if ("0".equals(queryHandle)) {
                        //where = " "+queryName+" = '"+queryValue+"' ";
                        lists = lists.stream().filter(o -> (queryValue.equals(o.get(queryName)))).collect(Collectors.toList());
                    } else if ("1".equals(queryHandle)) {
                        //where = " "+queryName+" LIKE '%"+queryValue+"%' ";
                        lists = lists.stream().filter(o -> ((o.get(queryName)==null?"":o.get(queryName).toString()).indexOf(queryValue) != -1)).collect(Collectors.toList());
                    } else if ("2".equals(queryHandle)) {
                        //where = " "+queryName+" <> "+queryValue+" ";
                        lists = lists.stream().filter(o -> (!queryValue.equals(o.get(queryName)))).collect(Collectors.toList());
                    } else if ("=".equals(queryHandle)) {
                        //where = " "+queryName+"+0 = "+queryValue+"+0 ";
                        lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null || "".equals(o.get(queryName)) ? "-1" : o.get(queryName).toString()) == Double.parseDouble(queryValue))).collect(Collectors.toList());
                    } else if (">=".equals(queryHandle)) {
                        //where = " "+queryName+"+0 >= "+queryValue+"+0 ";
                        lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null || "".equals(o.get(queryName)) ? "-1" : o.get(queryName).toString()) >= Double.parseDouble(queryValue))).collect(Collectors.toList());
                    } else if (">".equals(queryHandle)) {
                        //where = " "+queryName+"+0 > "+queryValue+"+0 ";
                        lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null || "".equals(o.get(queryName)) ? "-1" : o.get(queryName).toString()) > Double.parseDouble(queryValue))).collect(Collectors.toList());
                    } else if ("!=".equals(queryHandle)) {
                        //where = " "+queryName+"+0 != "+queryValue+"+0 ";
                        lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null || "".equals(o.get(queryName)) ? "-1" : o.get(queryName).toString()) != Double.parseDouble(queryValue))).collect(Collectors.toList());
                    } else if ("<=".equals(queryHandle)) {
                        //where = " "+queryName+"+0 <= "+queryValue+"+0 ";
                        lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null || "".equals(o.get(queryName)) ? "-1" : o.get(queryName).toString()) <= Double.parseDouble(queryValue))).collect(Collectors.toList());
                    } else if ("<".equals(queryHandle)) {
                        //where = " "+queryName+"+0 < "+queryValue+"+0 ";
                        lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null || "".equals(o.get(queryName)) ? "-1" : o.get(queryName).toString()) < Double.parseDouble(queryValue))).collect(Collectors.toList());
                    } else if ("早于".equals(queryHandle)) {
                        //where = " "+queryName+"+0 < "+queryValue+"+0 ";
                        lists = lists.stream().filter(o -> (dateContrast(o.get(queryName), "<", queryValue))).collect(Collectors.toList());
                    } else if ("晚于".equals(queryHandle)) {
                        //where = " "+queryName+"+0 < "+queryValue+"+0 ";
                        lists = lists.stream().filter(o -> (dateContrast(o.get(queryName), ">", queryValue))).collect(Collectors.toList());
                    } else if ("当日".equals(queryHandle)) {
                        //where = " "+queryName+"+0 < "+queryValue+"+0 ";
                        lists = lists.stream().filter(o -> (dateContrast( o.get(queryName), "=", queryValue))).collect(Collectors.toList());
                    } else if ("在此之间".equals(queryHandle)) {
                        //where = " "+queryName+"+0 < "+queryValue+"+0 ";
                        lists = lists.stream().filter(o -> (dateContrast( o.get(queryName), ">=",  map1.get("startDate")) && dateContrast(o.get(queryName), "<=",  map1.get("endDate")))).collect(Collectors.toList());
                    }
                }
            }
            if (map.get("sort") != null && ((Map) map.get("sort")).get("sortName") != null) {
                Map sort = (Map) map.get("sort");
                String ORDERBY = (String) sort.get("sortName");
                String sortHandle = (String) sort.get("sortHandle");
                if("avgDay".equals(ORDERBY)){
                    ORDERBY = "average";
                }
                boolean isnum = true;
                for (Map lp : lists) {
                    if (lp.get(ORDERBY) != null) {
                        if(!lp.get(ORDERBY).toString().matches("-?[0-9]+.?[0-9]*") &&  !"".equals(lp.get(ORDERBY).toString())){
                            isnum = false;
                            break;
                        }
                    }
                }

                String finalORDERBY = ORDERBY;
                if (isnum) {
                    // 先按降序排好
                    if ("desc".equalsIgnoreCase(sortHandle)) {
                        lists.sort(Comparator.comparing((Map m) -> (Double.parseDouble(m.get(finalORDERBY)==null || "".equals(m.get(finalORDERBY))?"-1":m.get(finalORDERBY).toString()))).reversed());
                    }else {
                        lists.sort(Comparator.comparing((Map m) -> (Double.parseDouble(m.get(finalORDERBY)==null || "".equals(m.get(finalORDERBY)) ?"-1":m.get(finalORDERBY).toString()))));
                    }
                }else {

                    // 先按降序排好
                    if ("desc".equalsIgnoreCase(sortHandle)) {
                        lists.sort(Comparator.comparing((Map m) -> (objToTime(m.get(finalORDERBY)))).reversed());
                    }else {
                        lists.sort(Comparator.comparing((Map m) -> (objToTime(m.get(finalORDERBY)))));
                    }
                }
            }else {
                lists.sort(Comparator.comparing((Map m) -> (Double.parseDouble(m.get("timelyPercentage")==null || "".equals(m.get("timelyPercentage")) ?"-1":m.get("timelyPercentage").toString()))).reversed());
            }
        }
        return lists;
    }
    //将列表字段的筛选条件和排序转换成查询条件
    private Map fieldQuery(Map map) {

        List<Map> queryList = (List<Map>) map.get("queryList");
        if (queryList != null) {
            String query = "";
            for (Map map1 : queryList) {
                String queryHandle = (String) map1.get("queryHandle");
                String q = (String) map1.get("queryName");
                if ("avgDay".equals(q)) {
                    q = "average";
                }
                String queryName = q;
                String queryValue = (String) map1.get("queryValue");
                String where = "";
                if ("0".equals(queryHandle)) {
                    where = " "+queryName+" = '"+queryValue+"' ";
                    //lists = lists.stream().filter(o -> (queryValue.equals(o.get(queryName)))).collect(Collectors.toList());
                } else if ("1".equals(queryHandle)) {
                    where = " "+queryName+" LIKE '%"+queryValue+"%' ";
                    //lists = lists.stream().filter(o -> ((o.get(queryName)==null?"":o.get(queryName).toString()).indexOf(queryValue) != -1)).collect(Collectors.toList());
                } else if ("2".equals(queryHandle)) {
                    where = " "+queryName+" != '"+queryValue+"' ";
                    //lists = lists.stream().filter(o -> (!queryValue.equals(o.get(queryName)))).collect(Collectors.toList());
                } else if ("=".equals(queryHandle)) {
                    where = " "+queryName+"+0 = "+queryValue+"+0 ";
                    //lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null ? "-1" : o.get(queryName).toString()) == Double.parseDouble(queryValue))).collect(Collectors.toList());
                } else if (">=".equals(queryHandle)) {
                    where = " "+queryName+"+0 >= "+queryValue+"+0 ";
                    //lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null ? "-1" : o.get(queryName).toString()) >= Double.parseDouble(queryValue))).collect(Collectors.toList());
                } else if (">".equals(queryHandle)) {
                    where = " "+queryName+"+0 > "+queryValue+"+0 ";
                    //lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null ? "-1" : o.get(queryName).toString()) > Double.parseDouble(queryValue))).collect(Collectors.toList());
                } else if ("!=".equals(queryHandle)) {
                    where = " "+queryName+"+0 != "+queryValue+"+0 ";
                    //lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null ? "-1" : o.get(queryName).toString()) != Double.parseDouble(queryValue))).collect(Collectors.toList());
                } else if ("<=".equals(queryHandle)) {
                    where = " "+queryName+"+0 <= "+queryValue+"+0 ";
                    //lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null ? "-1" : o.get(queryName).toString()) <= Double.parseDouble(queryValue))).collect(Collectors.toList());
                } else if ("<".equals(queryHandle)) {
                    where = " "+queryName+"+0 < "+queryValue+"+0 ";
                    //lists = lists.stream().filter(o -> (Double.parseDouble(o.get(queryName) == null ? "-1" : o.get(queryName).toString()) < Double.parseDouble(queryValue))).collect(Collectors.toList());
                } else if ("早于".equals(queryHandle)) {
                    where = " "+queryName+" <= '"+queryValue+"'";
                    //lists = lists.stream().filter(o -> (dateContrast(o.get(queryName), "<", queryValue))).collect(Collectors.toList());
                } else if ("晚于".equals(queryHandle)) {
                    where = " "+queryName+" >= '"+queryValue+"'";
                    //lists = lists.stream().filter(o -> (dateContrast(o.get(queryName), ">", queryValue))).collect(Collectors.toList());
                } else if ("当日".equals(queryHandle)) {
                    where = " LEFT("+queryName+",10) = LEFT('"+queryValue+"',10)";
                    //lists = lists.stream().filter(o -> (dateContrast( o.get(queryName), "=", queryValue))).collect(Collectors.toList());
                } else if ("在此之间".equals(queryHandle)) {
                    where = " "+queryName+" >= '"+map1.get("startDate")+"' AND "+queryName+" <= '"+map1.get("endDate")+"'";
                    //lists = lists.stream().filter(o -> (dateContrast( o.get(queryName), ">=",  map1.get("startDate")) && dateContrast(o.get(queryName), "<=",  map1.get("endDate")))).collect(Collectors.toList());
                }
                query=query + " AND"+where;
            }
            map.put("QUERY",query);
        }
        if (map.get("sort") != null && ((Map) map.get("sort")).get("sortName") != null) {
            Map sort = (Map) map.get("sort");
            String ORDERBY = (String) sort.get("sortName");
            String sortHandle = (String) sort.get("sortHandle");
            if("avgDay".equals(ORDERBY)){
                ORDERBY = "average";
            }
            if ("desc".equalsIgnoreCase(sortHandle)) {
                ORDERBY += " desc";
            }
            map.put("ORDERBY",ORDERBY);
            /*boolean isnum = false;
            for (Map lp : lists) {
                if (lp.get(ORDERBY) != null) {
                    isnum = lp.get(ORDERBY).toString().matches("-?[0-9]+.?[0-9]*");
                    break;
                }
            }

            String finalORDERBY = ORDERBY;
            if (isnum) {
                // 先按降序排好
                if ("desc".equalsIgnoreCase(sortHandle)) {
                    lists.sort(Comparator.comparing((Map m) -> (Double.parseDouble(m.get(finalORDERBY)==null?"-1":m.get(finalORDERBY).toString()))).reversed());
                }else {
                    lists.sort(Comparator.comparing((Map m) -> (Double.parseDouble(m.get(finalORDERBY)==null?"-1":m.get(finalORDERBY).toString()))));
                }
            }else {

                // 先按降序排好
                if ("desc".equalsIgnoreCase(sortHandle)) {
                    lists.sort(Comparator.comparing((Map m) -> (objToTime(m.get(finalORDERBY)))).reversed());
                }else {
                    lists.sort(Comparator.comparing((Map m) -> (objToTime(m.get(finalORDERBY)))));
                }
            }*/
        }

        return map;
    }

    //日期字符串转毫秒数
    long objToTime(Object obj){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(obj.toString()).getTime();
        }catch (Exception e){
            return 0;
        }
    }
    //日期字符串转毫秒数
    long objToDay(Object obj){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(obj.toString()).getTime();
        }catch (Exception e){
            return 0;
        }
    }
    boolean dateContrast(Object a,String type,Object b){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String lDate = (a==null?"1900-01-01 00:00:00":a.toString()).substring(0,10);
            String sDate = (b==null?"1900-01-01 00:00:00":b.toString()).substring(0,10);*/
            String lDate = (a==null?"1900-01-01 00:00:00":a.toString());
            String sDate = (b==null?"1900-01-01 00:00:00":b.toString());
            long d1 = sdf.parse(lDate).getTime();
            long d2 = sdf.parse(sDate).getTime();
            if("<".equals(type)){
                if(d1<d2){
                    return true;
                }
            }else if("<=".equals(type)){
                if(d1<=d2){
                    return true;
                }
            }else if(">".equals(type)){
                if(d1>d2){
                    return true;
                }
            }else if(">=".equals(type)){
                if(d1>=d2){
                    return true;
                }
            }if("=".equals(type)){
                if(d1==d2){
                    return true;
                }
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }

    //获取输入日期所在月份的平均时长目标
    String getAvgTimeRate (Map map){
        String dateStr = (String) map.get("endTime");
        String month = null;
        String dayHouse = "";
        try {
            //日期格式化
            SimpleDateFormat ymFormat = new SimpleDateFormat("yyyy-MM");
            dateStr = dateStr.substring(0,7);
            //获取操作时间
            month = ymFormat.format(ymFormat.parse(dateStr));
        }catch (Exception e){
            System.out.println("日期格式错误");
        }
        if((map.get("accountingAreaCode")!=null&&!"".equals(map.get("accountingAreaCode")))||(map.get("storeNumber")!=null&&!"".equals(map.get("storeNumber")))){
            List<IndicatorUserDTO> list1 = targetUserConfigDao.select("district",month);
            Optional<IndicatorUserDTO> cartOptional = list1.stream().filter(item -> item.getIndicatorCode().equals("avgTimeDay")).findFirst();
            if (cartOptional.isPresent()) {
                dayHouse = cartOptional.get().getIndicatorValue();
            }
        }else {
            List<IndicatorDTO> list1 = targetCoreConfigDao.select("district",month);
            Optional<IndicatorDTO> cartOptional = list1.stream().filter(item -> item.getIndicatorCode().equals("avgTimeDay")).findFirst();
            if (cartOptional.isPresent()) {
                dayHouse = cartOptional.get().getIndicatorValue();
            }
        }
        return dayHouse;
    }
    Map lastMonth(Map map){
        try {
            //开始时间结束时间月份-1
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            String startTime = (String) map.get("startTime");
            //将开始时间提前一个月
            calendar.setTime(dateFormat.parse(startTime));
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            map.put("startTime",dateFormat.format(calendar.getTime()));
            if(map.get("aggregateDate")!=null&&"本月".equals(map.get("aggregateDate"))){
                //将结束时间提前一个月
                calendar.setTime(dateFormat.parse(startTime));
                calendar.set(Calendar.SECOND, -1);
                map.put("endTime",dateFormat.format(calendar.getTime()));
            }else{
                //将结束时间提前一个月
                calendar.setTime(dateFormat.parse((String) map.get("endTime")));
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                map.put("endTime",dateFormat.format(calendar.getTime()));
            }
        }catch (Exception e){

        }
        return map;
    }
    List<Map> dateAll(String start,String end,List<Map> list1){
        LocalDate startDate = LocalDate.parse(start.substring(0,10));
        LocalDate endDate = LocalDate.parse(end.substring(0,10));
        LocalDate toDay = LocalDate.now();
        if(endDate.isAfter(toDay)){
            endDate = toDay;
        }
        BigDecimal bfb = new BigDecimal(100);
        long between = ChronoUnit.DAYS.between(startDate, endDate);
        if (between > 0) {
            Set<String> dateAll = Stream.iterate(startDate, e -> e.plusDays(1))
                    .limit(between + 1)
                    .map(LocalDate::toString)
                    .collect(Collectors.toSet());
            dateAll.stream().forEach(d ->{

                        Optional<Map> i = list1.stream().filter(l -> d.equals(l.get("dispatchingTime"))).findFirst();
                        if(i.isPresent()){
                            Map map = i.get();
                            if(map.get("eligible")==null||map.get("total")==null||"0".equals(map.get("eligible"))||"0".equals(map.get("total"))){
                                map.put("rate","0");
                            }else{
                                BigDecimal now =new BigDecimal(map.get("eligible").toString()) ;
                                BigDecimal last = new BigDecimal( map.get("total").toString());
                                map.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
                            }
                            map.put("avgDay",hoursToDay(map.get("average")));
                        }else {
                            /*LocalDate dd = LocalDate.parse(d);
                            if(dd.isAfter(toDay)){
                                Map map = new HashMap<>();
                                map.put("dispatchingTime",d);
                                list1.add(map);
                            }else {*/
                                Map map = new HashMap<>();
                                map.put("dispatchingTime",d);
                                map.put("eligible","0");
                                map.put("total","0");
                                map.put("rate","0");
                                map.put("average","0");
                                map.put("avgDay","");
                                list1.add(map);
                           /* }*/
                        }
                    }
            );
            /*for(String d:dateAll){
                boolean f = true;
                for(Map map:list1){
                    if(d.equals(map.get("dispatchingTime"))){
                        f=false;
                        break;
                    }
                }
                if(f){
                    Map map = new HashMap<>();
                    map.put("dispatchingTime",d);
                    map.put("eligible","0");
                    map.put("total","0");
                    map.put("rate","0");
                    map.put("average","0");
                    list1.add(map);
                }

            }*/
        }else if(list1.size()==0&&!startDate.isAfter(endDate)){
            Map map = new HashMap<>();
            map.put("dispatchingTime",endDate.toString());
            map.put("eligible","0");
            map.put("total","0");
            map.put("rate","0");
            map.put("average","0");
            map.put("avgDay","");
            list1.add(map);
        }else {
            for(Map map : list1){
                if(map.get("eligible")==null||map.get("total")==null||"0".equals(map.get("eligible"))||"0".equals(map.get("total"))){
                    map.put("rate","0");
                }else{
                    BigDecimal now =new BigDecimal(map.get("eligible").toString()) ;
                    BigDecimal last = new BigDecimal( map.get("total").toString());
                    map.put("rate",(now.multiply(bfb).divide(last,1,BigDecimal.ROUND_HALF_UP)).toString());
                }
            }
        }

        list1.sort(Comparator.comparing((Map m) -> (objToDay(m.get("dispatchingTime")))));
        return list1;
    }
    String getIndicator(Map map,String key){
        String dateStr = (String) map.get("endTime");
        String month = null;
        String dayHouse = "";
        try {
            //日期格式化
            SimpleDateFormat ymFormat = new SimpleDateFormat("yyyy-MM");
            dateStr = dateStr.substring(0,7);
            //获取操作时间
            month = ymFormat.format(ymFormat.parse(dateStr));
        }catch (Exception e){
            System.out.println("日期格式错误");
        }
        if((map.get("accountingAreaCode")!=null&&!"".equals(map.get("accountingAreaCode")))||(map.get("storeNumber")!=null&&!"".equals(map.get("storeNumber")))){
            List<String> useridList = regionDao.getUserId(map);
            if(useridList!=null && useridList.size() > 0) {
                String userid = useridList.get(0);
                List<IndicatorUserDTO> list1 = targetUserConfigDao.select("district", month);
                Optional<IndicatorUserDTO> cartOptional = list1.stream().filter(item -> item.getIndicatorCode().equals(key) && item.getAccountId().equals(userid)).findFirst();
                if (cartOptional.isPresent()) {
                    dayHouse = cartOptional.get().getIndicatorValue();
                }
            }
        }else {
            List<IndicatorDTO> list1 = targetCoreConfigDao.select("district",month);
            Optional<IndicatorDTO> cartOptional = list1.stream().filter(item -> item.getIndicatorCode().equals(key)).findFirst();
            if (cartOptional.isPresent()) {
                dayHouse = cartOptional.get().getIndicatorValue();
            }
        }
        return dayHouse;
        /*String target = "";
        if((map.get("accountingAreaCode")!=null&&!"".equals(map.get("accountingAreaCode")))||(map.get("storeNumber")!=null&&!"".equals(map.get("storeNumber")))){
            List<IndicatorUserDTO> mbList = targetUserConfigService.select("district");
            Optional<IndicatorUserDTO> cartOptional = mbList.stream().filter(item -> item.getIndicatorCode().equals(key)).findFirst();
            if (cartOptional.isPresent()) {
                target = cartOptional.get().getIndicatorValue();
            }
        }else {
            List<IndicatorDTO> mbList = targetCoreConfigService.select("district");
            Optional<IndicatorDTO> cartOptional = mbList.stream().filter(item -> item.getIndicatorCode().equals(key)).findFirst();
            if (cartOptional.isPresent()) {
                target = cartOptional.get().getIndicatorValue();
            }
        }
        return target;*/
    }
    //判断是否为空
    boolean isBlank(Object obj){
        if(obj!=null&&!"".equals(obj)){
            return true;
        }
        return false;
    }
    Map getLastMonth(Map map){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //格式化时间
        //获取上个月的第一天
        Calendar cal_1=Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号
        cal_1.set(Calendar.HOUR_OF_DAY,0);
        cal_1.set(Calendar.MINUTE,0);
        cal_1.set(Calendar.SECOND,0);
        map.put("startTime",format.format(cal_1.getTime()));

        //获取上个月的最后一天
        Calendar cal_2 = Calendar.getInstance();
        cal_2.set(Calendar.DAY_OF_MONTH,0);//设置为1号,当前日期既为本月第一天
        cal_2.set(Calendar.HOUR_OF_DAY,23);
        cal_2.set(Calendar.MINUTE,59);
        cal_2.set(Calendar.SECOND,59);
        map.put("endTime",format.format(cal_2.getTime())) ;
        return map;
    }
    Map timelyJudge(Map map){

        return map;
    }

    /**
     * 通用方法：方案条件转map
     * @param map
     * @return
     */
    public Map schemeToCondition(Map map){
        if(map.get("planId")!=null&&!"".equals(map.get("planId"))) {
            TemplateQueryDataDto templateQueryDto = new TemplateQueryDataDto();
            templateQueryDto.setStartTime(map.get("startTime").toString());
            templateQueryDto.setPlanId(map.get("planId").toString());
            templateQueryDto.setEndTime(map.get("endTime").toString());
            templateQueryDto.setPlanName(map.get("planName").toString());
            JSONObject jsonObject = commonTemplateService.selectDataByKey(templateQueryDto);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        dateConversion(map);
        return map;
    }
}
