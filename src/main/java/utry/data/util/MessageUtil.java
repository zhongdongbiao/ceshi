package utry.data.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import utry.core.cloud.caller.CallerParam;
import utry.core.cloud.caller.IServiceCaller;
import utry.core.common.BusinessException;
import utry.core.common.LoginInfoParams;
import utry.core.log.UtryLogger;
import utry.core.log.UtryLoggerFactory;
import utry.core.util.ApplicationContextUtil;
import utry.core.websocket.bo.ServersideMessage;
import utry.core.websocket.bo.ServersideMessageAttribute;
import utry.core.websocket.bo.UserInfo;
import utry.data.enums.SiteCodeEnum;

import java.util.*;

/**
 * @Author: DJ
 * @Date: 2021/3/8 10:40
 */
public class MessageUtil {

    private static IServiceCaller serviceCaller;

    static {
        serviceCaller = (IServiceCaller) ApplicationContextUtil.getBean("serviceCaller");
    }


    /**
     * 发送消息
     *
     * @param title     弹框内容
     * @param content   业务类型 如：workform-工单, horse-跑马灯，busyApply-置忙申请，notice-公告
     * @param type      弹框类型 固定有三种：manual-弹框（只能手动关闭），auto-弹框（可手动关闭，到时间自动消失），unClose-弹框(只能点击链接才能消失)
     * @param user      收件人
     */
    public static void send(String title, String content, String type, UserInfo user) {
        send(title, content, type, null, null, user);
    }


    /**
     * 发送消息
     *
     * @param title     弹框内容
     * @param content   业务类型 如：workform-工单, horse-跑马灯，busyApply-置忙申请，notice-公告
     * @param type      弹框类型 固定有三种：manual-弹框（只能手动关闭），auto-弹框（可手动关闭，到时间自动消失），unClose-弹框(只能点击链接才能消失)
     * @param userInfos 收件人
     */
    public static void send(String title, String content, String type, List<UserInfo> userInfos) {
        send(title, content, type, null, null, userInfos);
    }


    /**
     * 发送消息
     *
     * @param title     弹框内容
     * @param content   业务类型 如：workform-工单, horse-跑马灯，busyApply-置忙申请，notice-公告
     * @param type      弹框类型 固定有三种：manual-弹框（只能手动关闭），auto-弹框（可手动关闭，到时间自动消失），unClose-弹框(只能点击链接才能消失)
     * @param remark    备注 针对业务类型的补充说明
     * @param paramsMap 自定义属性
     * @param user      收件人
     */
    public static void send(String title, String content, String type, String remark, Map<String, String> paramsMap, UserInfo user) {
        List<UserInfo> userInfos = new ArrayList<>();
        userInfos.add(user);
        send(title, content, type, remark, paramsMap, userInfos);
    }


    /**
     * 发送消息
     *
     * @param title     弹框内容
     * @param content   业务类型 如：workform-工单, horse-跑马灯，busyApply-置忙申请，notice-公告
     * @param type      弹框类型 固定有三种：manual-弹框（只能手动关闭），auto-弹框（可手动关闭，到时间自动消失），unClose-弹框(只能点击链接才能消失)
     * @param remark    备注 针对业务类型的补充说明
     * @param paramsMap 自定义属性
     * @param userInfos 收件人
     */
    public static void send(String title, String content, String type, String remark, Map<String, String> paramsMap, List<UserInfo> userInfos) {
        String messageId = UUID.randomUUID().toString().replace("-", "");
        ServersideMessage message = new ServersideMessage();
        List<ServersideMessageAttribute> messageAttributes = new ArrayList<>();
        ServersideMessageAttribute serversideMessageAttribute;
        message.setBusinessId("letter");
        message.setMessageId(messageId);
//        message.setSourceUserId(LoginInfoParams.getAccountID());
        message.setSourceUserId("7f2b815be57541549b7c4cd1e81f2923");
        message.setSource(LoginInfoParams.getLoginName() + "(" + LoginInfoParams.getRealName() + ")");
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setRemark(remark);
        if (paramsMap != null && paramsMap.size() > 0) {
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                serversideMessageAttribute = new ServersideMessageAttribute();
                serversideMessageAttribute.setMessageId(messageId);
                serversideMessageAttribute.setAttriKey(entry.getKey());
                serversideMessageAttribute.setAttriValue(entry.getValue());
                messageAttributes.add(serversideMessageAttribute);
            }
            message.setMessageAttributes(messageAttributes);
        }

        send(message, userInfos);
    }


    public static void send(ServersideMessage message, List<UserInfo> userInfos) {
        if (StringUtils.isEmpty(message.getTitle())) {
            throw new BusinessException("title不能为空");
        }

        if (StringUtils.isEmpty(message.getSource())) {
            throw new BusinessException("source不能为空");
        }
        if (StringUtils.isEmpty(message.getSourceUserId())) {
            throw new BusinessException("sourceId不能为空");
        }
        if (StringUtils.isEmpty(message.getBusinessId())) {
            throw new BusinessException("businessId不能为空");
        }
        if (StringUtils.isEmpty(message.getTitle())) {
            throw new BusinessException("title不能为空");
        }

        if (StringUtils.isEmpty(message.getContent())) {
            throw new BusinessException("content不能为空");
        }
        if (CollectionUtils.isEmpty(userInfos)) {
            throw new BusinessException("收件人不能为空");
        }
        if (StringUtils.isEmpty(message.getType())) {
            message.setType("manual");
        }

        JSONObject params = new JSONObject();
        params.put("message", message);
        params.put("userInfos", userInfos);
        //远程调用task子站(后来新增对外的接口)
        CallerParam callerParam = new CallerParam(SiteCodeEnum.HRM.code(), "api/hrm/message/send");
        callerParam.addSpecialParamInternal(params);
        callerParam.setHttpMethod(HttpMethod.POST);
        JSONObject response = serviceCaller.call(callerParam, new ParameterizedTypeReference<JSONObject>() {
        });
    }



    /**
     * 发送消息
     *
     * @param title     弹框内容
     * @param content   业务类型 如：workform-工单, horse-跑马灯，busyApply-置忙申请，notice-公告
     * @param type      弹框类型 固定有三种：manual-弹框（只能手动关闭），auto-弹框（可手动关闭，到时间自动消失），unClose-弹框(只能点击链接才能消失)
     * @param user      收件人
     */
    public static void onlySend(String title, String content, String type, UserInfo user) {
        onlySend(title, content, type, null, null, user);
    }



    /**
     * 发送消息
     *
     * @param title     弹框内容
     * @param content   业务类型 如：workform-工单, horse-跑马灯，busyApply-置忙申请，notice-公告
     * @param type      弹框类型 固定有三种：manual-弹框（只能手动关闭），auto-弹框（可手动关闭，到时间自动消失），unClose-弹框(只能点击链接才能消失)
     * @param userInfos 收件人
     */
    public static void onlySend(String title, String content, String type, List<UserInfo> userInfos) {
        onlySend(title, content, type, null, null, userInfos);
    }


    /**
     * 发送消息
     *
     * @param title     弹框内容
     * @param content   业务类型 如：workform-工单, horse-跑马灯，busyApply-置忙申请，notice-公告
     * @param type      弹框类型 固定有三种：manual-弹框（只能手动关闭），auto-弹框（可手动关闭，到时间自动消失），unClose-弹框(只能点击链接才能消失)
     * @param remark    备注 针对业务类型的补充说明
     * @param paramsMap 自定义属性
     * @param user      收件人
     */
    public static void onlySend(String title, String content, String type, String remark, Map<String, String> paramsMap, UserInfo user) {
        List<UserInfo> userInfos = new ArrayList<>();
        userInfos.add(user);
        onlySend(title, content, type, remark, paramsMap, userInfos);
    }



    /**
     * 发送消息
     *
     * @param title     弹框内容
     * @param content   业务类型 如：workform-工单, horse-跑马灯，busyApply-置忙申请，notice-公告
     * @param type      弹框类型 固定有三种：manual-弹框（只能手动关闭），auto-弹框（可手动关闭，到时间自动消失），unClose-弹框(只能点击链接才能消失)
     * @param remark    备注 针对业务类型的补充说明
     * @param paramsMap 自定义属性
     * @param userInfos 收件人
     */
    public static void onlySend(String title, String content, String type, String remark, Map<String, String> paramsMap, List<UserInfo> userInfos) {
        String messageId = UUID.randomUUID().toString().replace("-", "");
        ServersideMessage message = new ServersideMessage();
        List<ServersideMessageAttribute> messageAttributes = new ArrayList<>();
        ServersideMessageAttribute serversideMessageAttribute;
        message.setBusinessId("letter");
        message.setMessageId(messageId);
        message.setSourceUserId(LoginInfoParams.getAccountID());
        message.setSource(LoginInfoParams.getLoginName() + "(" + LoginInfoParams.getRealName() + ")");
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setRemark(remark);
        if (paramsMap != null && paramsMap.size() > 0) {
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                serversideMessageAttribute = new ServersideMessageAttribute();
                serversideMessageAttribute.setMessageId(messageId);
                serversideMessageAttribute.setAttriKey(entry.getKey());
                serversideMessageAttribute.setAttriValue(entry.getValue());
                messageAttributes.add(serversideMessageAttribute);
            }
            message.setMessageAttributes(messageAttributes);
        }

        onlySend(message, userInfos);
    }

    public static void onlySend(ServersideMessage message, List<UserInfo> userInfos) {
        if (StringUtils.isEmpty(message.getTitle())) {
            throw new BusinessException("title不能为空");
        }

        if (StringUtils.isEmpty(message.getSource())) {
            throw new BusinessException("source不能为空");
        }
        if (StringUtils.isEmpty(message.getSourceUserId())) {
            throw new BusinessException("sourceId不能为空");
        }
        if (StringUtils.isEmpty(message.getBusinessId())) {
            throw new BusinessException("businessId不能为空");
        }
        if (StringUtils.isEmpty(message.getTitle())) {
            throw new BusinessException("title不能为空");
        }
        if (StringUtils.isEmpty(message.getContent())) {
            throw new BusinessException("content不能为空");
        }

        if (CollectionUtils.isEmpty(userInfos)) {
            throw new BusinessException("收件人不能为空");
        }
        JSONObject params = new JSONObject();
        params.put("message", message);
        params.put("userInfos", userInfos);
        //远程调用task子站
        CallerParam callerParam = new CallerParam(SiteCodeEnum.HRM.code(), "/msg/sendNoSaveToDB.do");
        callerParam.addSpecialParamInternal(params);
        callerParam.setHttpMethod(HttpMethod.POST);
        Boolean response = serviceCaller.call(callerParam, new ParameterizedTypeReference<Boolean>() {
        });
    }
}
