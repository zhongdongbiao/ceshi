package utry.data.modular.calendar.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.core.common.LoginInfoParams;
import utry.data.base.BaseController;
import utry.data.base.ResultManage;
import utry.data.modular.calendar.dao.CalendarMapper;
import utry.data.modular.calendar.service.ICalendarService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import utry.data.modular.calendar.model.CalendarDto;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description:
 * @author: lvlb
 * @Date: 2020/9/23 13:29
 */
@Controller
@RequestMapping("/calendar")
@Api(tags = "日历接口")
@ServiceApi
public class CalendarController extends BaseController {

    @Autowired
    private ICalendarService calendarService;
    @Resource
    private CalendarMapper calendarMapper;

    @ApiOperation("查询日历数据")
    @RequestMapping(value = "getCalendarList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public RetResult getCalendarList(@RequestBody CalendarDto calendarDto) {
        JSONObject json = new JSONObject();
        List<CalendarDto> calendarDtoList = new ArrayList<>();
        try {
            calendarDtoList = calendarService.getCalendarList(calendarDto);
        } catch (Exception e) {
            e.printStackTrace();
            json.put("errorMsg", e.getMessage());
        }
        json.put("calendarList", calendarDtoList);
        return RetResponse.makeOKRsp(json);
    }

    @ApiOperation("更新假期类型")
    @RequestMapping(value = "updateCalendarType", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public RetResult updateCalendarType(@RequestBody CalendarDto calendarDto) {
//        LoginInfoParams.addCompanyIdAsLoginBean("2001");
        JSONObject json = new JSONObject();
        int count = calendarService.updateCalendarType(calendarDto);
        json.put("count", count);
        return RetResponse.makeOKRsp(json);
    }

    @ApiOperation("批量更新假期类型")
    @RequestMapping(value = "batchUpdateCalendarType", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public RetResult batchUpdateCalendarType(@RequestBody List<CalendarDto> calendarList) {
        LoginInfoParams.addCompanyIdAsLoginBean("2001");
        if (calendarList.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            Date day30 = get30Day();
            for (CalendarDto calendarDto : calendarList) {
                String fullDate = calendarDto.getFullDate();
                try {
                    Date submitDay = sdf.parse(fullDate);
                    // 如果提交的今天之前的日期，未修改不校验
                    if (submitDay.compareTo(day30) <= -1) {
                        CalendarDto dto = this.calendarMapper.selectTypeByYear(fullDate);
                        if (dto.getType() == calendarDto.getType()) {
                            continue;
                        }
                    }
                    if (submitDay.compareTo(day30) == -1) {
                        return RetResponse.makeErrRsp("请提交修改含有" + sdf1.format(day30) + "之后的日期");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        JSONObject json = new JSONObject();
        calendarService.batchUpdateCalendarType(calendarList);
        return RetResponse.makeOKRsp(json);
    }

    /**
     * 获取未来30天
     *
     * @return
     */
    private Date get30Day() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, 30);
        return now.getTime();
    }

    @ApiOperation("获取夏令时冬令时时间")
    @ResponseBody
    @RequestMapping(value = "/getDayLightTimeData", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public RetResult getDayLightTimeData() {
        Map<String, Object> dayLightTimeData = calendarService.getDayLightTimeData();
        return RetResponse.makeOKRsp(dayLightTimeData);
    }

    @ApiOperation("启用夏令时冬令时时间")
    @ResponseBody
    @RequestMapping(value = "/setDayLightTimeData", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public RetResult setDayLightTimeData(@RequestBody JSONObject jsonObject) {
        calendarService.setDayLightTimeData(jsonObject.getString("type"), jsonObject.getString("name"));
        return RetResponse.makeOKRsp();
    }

    @ApiOperation("法定节假日及调班日期导入维护")
    @RequestMapping(value = "/importCalendarTemplate", method = RequestMethod.POST)
    public ResponseEntity<ResultManage> importCalendarTemplate(@RequestParam MultipartFile file) {
        this.calendarService.importCalendarTemplate(file);
        return this.result();
    }

    @ApiOperation("模板导出")
    @RequestMapping(value = "/exportCalendarTemplate", method = RequestMethod.GET)
    public ResponseEntity<ResultManage> exportCalendarTemplate(HttpServletResponse response) {
        this.calendarService.exportCalendarTemplate(response);
        return this.result();
    }
}
