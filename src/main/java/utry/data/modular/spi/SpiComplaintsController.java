package utry.data.modular.spi;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.data.modular.complaints.dto.ComplaintDto;
import utry.data.modular.complaints.service.ComplaintsService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投诉直辖spi接口Controller
 *
 * @author wanlei
 */
@RestController
@RequestMapping("subApi/spiComplaints")
@Api(tags = "投诉直辖SPI")
public class SpiComplaintsController extends CommonController {

    @Resource
    private ComplaintsService complaintsService;

    @ApiOperation(value = "投诉处理单推送", notes = "投诉处理单推送")
    @PostMapping("/complaintDetail")
    public RetResult complaintDetail(HttpServletRequest request){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return complaintsService.complaintDetail(requestToMap(request));
    }

    @ApiOperation(value = "热线服务单推送", notes = "热线服务单推送")
    @PostMapping("/hotLineDetail")
    public RetResult hotLineDetail(HttpServletRequest request){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return complaintsService.hotLineDetail(requestToMap(request));
    }

    @ApiOperation(value = "履历信息推送", notes = "履历信息推送")
    @PostMapping("/resumeDetail")
    public RetResult resumeDetail(@RequestBody List<Map<String,Object>> resumeList){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return complaintsService.resumeDetail(resumeList);
    }

    @ApiOperation(value = "投诉7天解决率", notes = "投诉7天解决率")
    @PostMapping("/apiSevenSolveRate")
    public RetResult apiSevenSolveRate(@RequestBody Map<String,Object> map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        Map<String,Object> message = complaintsService.apiSevenSolveRate(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "投诉7天解决率排行", notes = "投诉7天解决率排行")
    @PostMapping("/apiSevenSolveRank")
    public RetResult apiSevenSolveRank(@RequestBody Map<String,Object> map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        List<Map<String,Object>> message = complaintsService.apiSevenSolveRank(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "投诉率", notes = "投诉率")
    @PostMapping("/apiComplaintRate")
    public RetResult apiComplaintRate(@RequestBody Map<String,Object> map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        Map<String,Object> message = complaintsService.apiComplaintRate(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "未结案", notes = "未结案")
    @PostMapping("/apiNotOverCase")
    public RetResult apiNotOverCase(@RequestBody Map<String,Object> map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        Map<String,Object> message = complaintsService.apiNotOverCase(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "投诉异常监控", notes = "投诉异常监控")
    @PostMapping("/apiComplaintAbnormalMonitor")
    public RetResult apiComplaintAbnormalMonitor(@RequestBody Map<String,Object> map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        List<Map<String,Object>> message = complaintsService.apiComplaintAbnormalMonitor(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "N+1解决方案提交率", notes = "N+1解决方案提交率")
    @PostMapping("/apiSolveSubmitRate")
    public RetResult apiSolveSubmitRate(@RequestBody Map<String,Object> map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        Map<String,Object> message = complaintsService.apiSolveSubmitRate(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "N+1解决方案提交率排名", notes = "N+1解决方案提交率排名")
    @PostMapping("/apiSolveSubmitRank")
    public RetResult apiSolveSubmitRank(@RequestBody Map<String,Object> map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        List<Map<String,Object>> message = complaintsService.apiSolveSubmitRank(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "结算费用及台量", notes = "结算费用及台量")
    @PostMapping("/apiSettleData")
    public RetResult apiSettleData(@RequestBody Map<String,Object> map){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        Map<String,Object> message = complaintsService.apiSettleData(map);
        return RetResponse.makeOKRsp(message);
    }

    Map requestToMap(HttpServletRequest request){
        String result="";
        try {
            InputStream in = request.getInputStream();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int n;
            while ((n = in.read(bytes)) != -1) {
                out.write(bytes, 0, n);
            }
            bytes = out.toByteArray();
            result = new String(bytes, "utf-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        result = result.replace("\\", "\\\\");
        try {
            JSONObject js1 = JSONObject.parseObject(result);
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<String, Object> entry : js1.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("【debugger】json error..");
        }
        char[] temp = result.toCharArray();
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
        result = new String(temp);
        JSONObject js = JSONObject.parseObject(result);
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : js.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }


}
