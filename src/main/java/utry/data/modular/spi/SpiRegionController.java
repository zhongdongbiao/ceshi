package utry.data.modular.spi;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.data.modular.region.service.RegionService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 大区服务spi接口Controller
 *
 * @author wanlei
 */
@RestController
@RequestMapping("subApi/spiRegion")
@Api(tags = "大区服务SPI")
public class SpiRegionController  extends CommonController {

    @Resource
    private RegionService regionService;

    @ApiOperation(value = "服务单详情推送", notes = "服务单详情推送")
    @PostMapping("/serviceDetail")
    public RetResult serviceDetail(HttpServletRequest request){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.serviceDetail(requestToMap(request));
    }

    @ApiOperation(value = "挂单解挂推送", notes = "挂单解挂推送")
    @PostMapping("/pendingOrder")
    public RetResult pendingOrder(HttpServletRequest request){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.pendingOrder(requestToMap(request));
    }

    @ApiOperation(value = "二次上门认定推送", notes = "二次上门认定推送")
    @PostMapping("/secondDoor")
    public RetResult secondDoor(HttpServletRequest request){
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return regionService.secondDoor(requestToMap(request));
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
