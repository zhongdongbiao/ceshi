package utry.data.modular.calendar.api;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import utry.core.common.LoginInfoParams;
import utry.data.modular.calendar.service.ICalendarService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;
import utry.data.modular.calendar.model.CalendarDto;

import javax.annotation.Resource;

/**
 * @ClassName: CalendarApi
 * @Description: TODO
 * @author: yangkesheng
 * @date: 2022/2/15  16:10
 * @version: 1.0
 */
@Api(tags = "获取该日期是否是工作日")
@RestController
@RequestMapping(value = "/restful/calendar")
public class CalendarApi {
    @Resource
    private ICalendarService calendarService;

    /**
     * {"date":"20211001"}
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation(value = "获取该日期是否是工作日", notes = "\"date\":\"20211001\"    返回type:0工作日1非工作日\"")
    @RequestMapping(value = "/getTypeForToday", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public RetResult getCalendar(@RequestBody JSONObject jsonObject) {
        CalendarDto calendarDto = this.calendarService.selectTypeByYear(jsonObject.getString("date"));
        return RetResponse.makeOKRsp(calendarDto);
    }

    /**
     * {"time":"2020-05-05 11:30:00"}
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation(value = "获取该时间是否工作时间", notes = "\"time\":\"2020-05-05 11:30:00\"    返回type:0工作时间1非工作时间\"")
    @RequestMapping(value = "/getWhetherWorkTime", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public RetResult getWhetherWorkTime(@RequestBody JSONObject jsonObject) {
//        LoginInfoParams.addCompanyIdAsLoginBean("2001");
        return RetResponse.makeOKRsp(this.calendarService.getWhetherWorkTime(jsonObject.getString("time")));
    }

}
