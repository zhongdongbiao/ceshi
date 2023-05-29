package utry.data.modular.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import utry.core.common.LoginInfoParams;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.hotLineAgent.dto.QueueIdDto;
import utry.data.modular.ccBoard.hotLineAgent.service.HotOrderFollowProcessService;
import utry.data.modular.ccBoard.hotLineAgent.vo.*;
import utry.data.modular.common.dto.TemplateQueryDataDto;
import utry.data.modular.common.service.CommonTemplateService;
import utry.data.util.HttpClientUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: data
 * @description: 热线坐席api控制类
 * @author: WangXinhao
 * @create: 2022-10-25 14:01
 **/

@RestController
@RequestMapping("subApi/hotLineSeat")
@Api(tags = "热线坐席api控制类")
public class ApiHotLineAgentController {

    @Resource
    private SysConfServiceImpl sysConfService;

    @Autowired
    private HotOrderFollowProcessService hotOrderFollowProcessService;

    @Autowired
    private CommonTemplateService commonTemplateService;

    @ApiOperation(value = "未完成待处理单图表")
    @RequestMapping(value = "/pendingIncompleteChart", method = RequestMethod.POST)
    public RetResult<PendingIncompleteChartVo> pendingIncompleteChart() {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/";
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(null));
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "热线项目实时监控-硕德移动端调用")
    @RequestMapping(value = "/projectActualTimeMonitor", method = RequestMethod.POST)
    public RetResult<ProjectActualTimeMonitorVo> projectActualTimeMonitor(@RequestBody TemplateQueryDataDto templateQueryDto) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        List<String> queueId = new ArrayList<>();
        if (!StringUtils.isEmpty(templateQueryDto.getPlanId()) && !StringUtils.isEmpty(templateQueryDto.getPlanName())) {
            JSONObject jsonObject = commonTemplateService.selectDataByKey(templateQueryDto);
            queueId = JSONArray.parseArray(jsonObject.get("chosedList").toString(), String.class);
        }
        DateDurationQueueIdDto dto = DateDurationQueueIdDto.builder().startDate(templateQueryDto.getStartTime()).endDate(templateQueryDto.getEndTime()).queueId(queueId).build();
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.projectActualTimeMonitor(dto));
    }

    @ApiOperation(value = "在线坐席-硕德移动端调用")
    @RequestMapping(value = "/onlineAgent", method = RequestMethod.POST)
    public RetResult<OnlineAgentVo> onlineAgent(@RequestBody TemplateQueryDataDto templateQueryDto) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        List<String> queueId = new ArrayList<>();
        if (!StringUtils.isEmpty(templateQueryDto.getPlanId()) && !StringUtils.isEmpty(templateQueryDto.getPlanName())) {
            JSONObject jsonObject = commonTemplateService.selectDataByKey(templateQueryDto);
            queueId = JSONArray.parseArray(jsonObject.get("chosedList").toString(), String.class);
        }
        QueueIdDto dto = new QueueIdDto();
        dto.setQueueId(queueId);
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.onlineAgent(dto));
    }

    @ApiOperation(value = "人力数据表格-硕德移动端调用")
    @RequestMapping(value = "/humanDataTable", method = RequestMethod.POST)
    public RetResult<List<HumanDataTableVo>> humanDataTable(@RequestBody TemplateQueryDataDto templateQueryDto) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        List<String> queueId = new ArrayList<>();
        if (!StringUtils.isEmpty(templateQueryDto.getPlanId()) && !StringUtils.isEmpty(templateQueryDto.getPlanName())) {
            JSONObject jsonObject = commonTemplateService.selectDataByKey(templateQueryDto);
            queueId = JSONArray.parseArray(jsonObject.get("chosedList").toString(), String.class);
        }
        DateDurationQueueIdDto dto = DateDurationQueueIdDto.builder().startDate(templateQueryDto.getStartTime()).endDate(templateQueryDto.getEndTime()).queueId(queueId).build();
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.humanDataTable(dto));
    }
}
