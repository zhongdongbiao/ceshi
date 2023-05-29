package utry.data.modular.partsManagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import utry.data.modular.partsManagement.service.DailyDemandAmountService;
import utry.data.util.RetResult;

/**
 * @program: data
 * @description: 每日需求金额控制类
 * @author: WangXinhao
 * @create: 2022-06-20 16:18
 **/
@RestController
@RequestMapping("/dailyDemandAmount")
@Api(tags = "每日需求金额控制类")
public class DailyDemandAmountController {

    @Autowired
    private DailyDemandAmountService dailyDemandAmountService;


    @ApiOperation(value = "计算并插入指定日期需求金额")
    @RequestMapping(value = "/calculateAndInsertDemandAmountByDate", method = RequestMethod.GET)
    public RetResult<Double> calculateAndInsertDemandAmountByDate(@RequestParam("date") String date) {
        return dailyDemandAmountService.calculateAndInsertDemandAmountByDate(date);
    }
}
