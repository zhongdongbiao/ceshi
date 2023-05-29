package utry.data.modular.spi;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.modular.partsManagement.dao.OrderDetailDao;
import utry.data.modular.partsManagement.dao.ReceiptDao;
import utry.data.modular.partsManagement.service.CoreIndexService;
import utry.data.modular.partsManagement.vo.JobOrderVo;
import utry.data.modular.partsManagement.vo.PartsReceiptsHeatMapVo;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;
import utry.data.util.TimeUtil;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 大屏SPI
 *
 * @author lidakai
 */
@RestController
@RequestMapping("subApi/spiScreen")
@Api(tags = "大屏SPI")
public class SpiScreenController extends CommonController {

    @Resource
    ReceiptDao receiptDao;
    @Resource
    OrderDetailDao orderDetailDao;
    @Resource
    private SysConfServiceImpl sysConfService;
    @Autowired
    CoreIndexService coreIndexService;

    @ApiOperation(value = "NDS2、NDS3每月达成情况", notes = "NDS2、NDS3每月达成情况")
    @PostMapping("/partsReceiptsHeatMap")
    public RetResult partsReceiptsHeatMap(@RequestBody JSONObject jsonObject) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        List<PartsReceiptsHeatMapVo> partsReceiptsHeatMapVoList = new ArrayList<>();
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        String days = jsonObject.getString("days");
        List<String> timeInterval = findDaysStr(startTime.substring(0,10), endTime.substring(0,10));
        for (String time:timeInterval
             ) {
            PartsReceiptsHeatMapVo partsReceiptsHeatMapVo = new PartsReceiptsHeatMapVo();
            partsReceiptsHeatMapVo.setDate(time.substring(0,7));
            String startDate = TimeUtil.getFirstDayOfMonth(time);
            String endDate = TimeUtil.getLastDayOfMonth(time);
            // 获取NDS2
            int countByDate;
            int countByNDS2;
            // 获取担当的所有的服务店收货订单行数
            countByDate = receiptDao.getCountByScreenDate(startDate, endDate);
            if("2".equals(days)){
                // 获取担当的符合nds2的服务店收货订单行数
                countByNDS2 = receiptDao.getCountByNds2ScreenDate(startDate, endDate);
            }else {
                // 获取担当的符合nds3的服务店收货订单行数
                countByNDS2 = receiptDao.getCountByNDS3(startDate, endDate);
            }
            String nds2 = TimeUtil.getFourRate(countByNDS2,countByDate);
            if(nds2!=null){
                partsReceiptsHeatMapVo.setDataValue(nds2);
            }else {
                partsReceiptsHeatMapVo.setDataValue("0.0000");
            }

            partsReceiptsHeatMapVoList.add(partsReceiptsHeatMapVo);
        }

        return RetResponse.makeOKRsp(partsReceiptsHeatMapVoList);
    }

    @ApiOperation(value = "作业订单列表(部品动态)", notes = "作业订单列表(部品动态)")
    @PostMapping("/jobOrderList")
    public RetResult jobOrderList(@RequestBody JSONObject jsonObject) throws ParseException {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        List<JobOrderVo> jobOrderVoList = new ArrayList<>();
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        List<JobOrderVo> list = new ArrayList<>();
        List<JobOrderVo> screenProcess = orderDetailDao.getScreenProcess(startTime,endTime);
        jobOrderVoList.addAll(screenProcess);
        List<JobOrderVo> screenMis = orderDetailDao.getScreenMis(startTime,endTime);
        jobOrderVoList.addAll(screenMis);
        List<JobOrderVo> screenPackage = orderDetailDao.getScreenPackage(startTime,endTime);
        jobOrderVoList.addAll(screenPackage);
        List<JobOrderVo> screenApprove = orderDetailDao.getScreenApprove(startTime,endTime);
        jobOrderVoList.addAll(screenApprove);
        List<JobOrderVo> screenGood = orderDetailDao.getScreenGood(startTime,endTime);
        jobOrderVoList.addAll(screenGood);
        list = jobOrderVoList.stream().sorted(Comparator.comparing(JobOrderVo::getStatusChangeTime,Comparator.nullsLast(String::compareTo)).reversed())
                .collect(Collectors.toList());
        return RetResponse.makeOKRsp(list);
    }

    /**
     * 获取两个时间内的每一天时间 yyyy-MM-dd
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    private List<String> findDaysStr(String startDate, String endDate) {
        LocalDate startLocalDate = LocalDate.parse(startDate);
        LocalDate endLocalDate = LocalDate.parse(endDate);
        List<String> result = null;
        // 按月聚合
        LocalDate startFirstDayOfMonth = startLocalDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endFirstDayOfMonth = endLocalDate.with(TemporalAdjusters.firstDayOfMonth());
        long betweenMonths = startFirstDayOfMonth.until(endFirstDayOfMonth, ChronoUnit.MONTHS) + 1;
        result = new ArrayList<>();
        for (long i = 0; i < betweenMonths; i++) {
            LocalDate plusMonths = startFirstDayOfMonth.plusMonths(i);
            result.add(plusMonths.toString());
        }
        return result;
    }



}
