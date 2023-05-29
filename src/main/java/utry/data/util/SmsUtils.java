package utry.data.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import utry.core.cloud.caller.CallerParam;
import utry.core.cloud.caller.IServiceCaller;
import utry.core.site.SiteCode;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sms
 * @description:
 * @author: zhangj
 * @create: 2019-09-09 15:30
 **/
@Component("smsUtils")
public class SmsUtils {


    @Resource
    private IServiceCaller serviceCaller;

    /**
     * 获取字典项接口
     */
    private static final String DICT_CODE_INTERFACE = "/api/sys/listenerCon/getKeyList";

    /**
     * @Description: 通过key获取字典中的value值
     * @Param: [list, dataKey]
     * @return: java.lang.String
     * @Author: zhangj
     * @Date: 2019/9/9
     */
    public String getCodeName(List<Map<String, Object>> list, String dataKey) {
        if (list != null && StringUtils.isNotBlank(dataKey)) {
            for (Map<String, Object> map : list) {
                if (StringUtils.isNotBlank(MapUtils.getString(map, "key")) && dataKey.equalsIgnoreCase(MapUtils.getString(map, "key"))) {
                    return MapUtils.getString(map, "value");
                }
            }
        }
        return null;
    }

    /**
     * 获取数据字典中的值
     *
     * @param code 数据编码 如（ASSESS_TYPE）
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getDictList(String code) {
        List<Map<String, Object>> codeList = null;
        CallerParam callerParam = new CallerParam(SiteCode.SYS.code(), DICT_CODE_INTERFACE);
        callerParam.addParameter("dataCode", code);
        JSONObject call = serviceCaller.call(callerParam, JSONObject.class);
        codeList = (List<Map<String, Object>>) call.get("data");
        Map<String, String> map = new ConcurrentHashMap<>();
        for (Map<String, Object> m : codeList) {
            map.put((String) m.get("key"), (String) m.get("value"));
        }
        return map;
    }

    /**
     * 获取数据字典中的值,返回List
     *
     * @param code 数据编码 如（ASSESS_TYPE）
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDictOfList(String code) {
        List<Map<String, Object>> codeList = null;
        CallerParam callerParam = new CallerParam(SiteCode.SYS.code(), DICT_CODE_INTERFACE);
        callerParam.addParameter("dataCode", code);
        JSONObject call = serviceCaller.call(callerParam, JSONObject.class);
        codeList = (List<Map<String, Object>>) call.get("data");
        return codeList;
    }
}
