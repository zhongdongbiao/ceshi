package utry.data.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.data.modular.partsManagement.service.DailyDemandAmountService;
import utry.data.modular.partsManagement.service.DailySafeDepositAmountService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import java.time.LocalDate;

/**
 * @program: data
 * @description: 定时任务执行类-部件库存提前计算
 * @author: WangXinhao
 * @create: 2022-06-20 14:29
 **/
@Api(tags = "定时任务执行类-部件库存提前计算")
@ServiceApi
@RestController
public class PartInventoryAdvanceCalculateTask {

    @Autowired
    private DailyDemandAmountService dailyDemandAmountService;

    @Autowired
    private DailySafeDepositAmountService dailySafeDepositAmountService;

    /**
     * 计算昨日需求金额
     * @return 统一返回
     */
    @ApiOperation(value = "计算昨日需求金额", notes = "定时任务执行方法入口，执行时间在部品库存数据拉取之后")
    @RequestMapping(value = "/api/task/100060/calculateYesterdayDemandAmount", method = RequestMethod.POST)
    public RetResult<Double> calculateYesterdayDemandAmount() {
        String yesterday = LocalDate.now().minusDays(1).toString();
        return dailyDemandAmountService.calculateAndInsertDemandAmountByDate(yesterday);
    }

    /**
     * 计算今日安全在库金额
     * @return 统一返回
     */
    @ApiOperation(value = "计算今日安全在库金额", notes = "定时任务执行方法入口，执行时间在部品库存数据拉取之后")
    @RequestMapping(value = "/api/task/100060/calculateTodaySafeAmount", method = RequestMethod.POST)
    public RetResult<Double> calculateTodaySafeAmount() {
        String today = LocalDate.now().toString();
        return dailySafeDepositAmountService.calculateAndInsertSafeDepositAmountByDate(today);
    }
}
