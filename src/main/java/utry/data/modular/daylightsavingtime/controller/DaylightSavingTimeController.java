package utry.data.modular.daylightsavingtime.controller;/**
 * @ClassName daylightSavingTimeController.java
 * @author zd
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022年02月09日 16:45:00
 */

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import utry.data.base.BaseController;
import utry.data.base.Page;
import utry.data.base.ResultManage;
import utry.data.modular.daylightsavingtime.dto.DaylightSavingTimeDto;
import utry.data.modular.daylightsavingtime.model.DaylightSavingTime;
import utry.data.modular.daylightsavingtime.model.IdsDTO;
import utry.data.modular.daylightsavingtime.service.IDaylightSavingTimeService;

/**
 * TODO * @version 1.0 * @author zhangdi * @date 2022/2/9 16:45
 */
@Api(tags = "考勤管理-日光节约时制")
@RestController
@RequestMapping("/daylightSavingTime")
public class DaylightSavingTimeController extends BaseController {

    @Autowired
    private IDaylightSavingTimeService daylightSavingTimeService;

    @ApiOperation(value = "分页获取日光节约时制列表", notes = "分页获取日光节约时制列表")
    @RequestMapping(value = "/selectDaylightSavingTimeByPage", method = RequestMethod.POST)
    public ResponseEntity<ResultManage> selectdaylightSavingTimeByPage(@RequestBody Page<DaylightSavingTimeDto> page) {
        PageInfo pageInfo = daylightSavingTimeService.selectDaylightSavingTimeByPage(page);
        return this.result(pageInfo);
    }

    @ApiOperation(value = "保存日光节约时制", notes = "保存日光节约时制")
    @RequestMapping(value = "/saveDaylightSavingTime", method = RequestMethod.POST)
    public ResponseEntity<ResultManage> saveDaylightSavingTime(@RequestBody DaylightSavingTime daylightSavingTime) {
        int result = daylightSavingTimeService.saveDaylightSavingTime(daylightSavingTime);
        if(result>0){
            return this.result("保存成功");
        }else{
            return this.result("保存失败");
        }
    }

    @ApiOperation(value = "修改日光节约时制", notes = "修改日光节约时制")
    @RequestMapping(value = "/updateDaylightSavingTime", method = RequestMethod.POST)
    public ResponseEntity<ResultManage> updateAttendanceLocation(@RequestBody DaylightSavingTime daylightSavingTime) {
        int result= daylightSavingTimeService.updateDaylightSavingTime(daylightSavingTime);
        if(result>0){
            return this.result("修改成功");
        }else{
            return this.result("修改失败");
        }
    }

    @ApiOperation(value = "删除日光节约时制", notes = "删除日光节约时制")
    @RequestMapping(value = "/deleteDaylightSavingTime", method = RequestMethod.POST)
    public ResponseEntity<ResultManage> deleteBlackList(@RequestBody IdsDTO idsDTO) {
        daylightSavingTimeService.deleteDaylightSavingTime(idsDTO.getIds());
        return this.result();
    }
}
