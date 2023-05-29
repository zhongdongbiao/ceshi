package utry.data.modular.partsManagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import utry.data.modular.partsManagement.service.DailySafeDepositAmountService;
import utry.data.util.RetResult;

/**
 * @program: data
 * @description: 每日安全在库金额控制类
 * @author: WangXinhao
 * @create: 2022-06-20 16:55
 **/

@RestController
@RequestMapping("/dailySafeDepositAmount")
@Api(tags = "每日安全在库金额控制类")
public class DailySafeDepositAmountController {


    @Autowired
    private DailySafeDepositAmountService dailySafeDepositAmountService;


    @ApiOperation(value = "计算并插入指定日期安全在库金额")
    @RequestMapping(value = "/calculateAndInsertSafeDepositAmountByDate", method = RequestMethod.GET)
    public RetResult<Double> calculateAndInsertSafeDepositAmountByDate(@RequestParam("date") String date) {
        return dailySafeDepositAmountService.calculateAndInsertSafeDepositAmountByDate(date);
    }
}
