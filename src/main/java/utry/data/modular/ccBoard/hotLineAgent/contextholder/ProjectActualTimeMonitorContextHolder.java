package utry.data.modular.ccBoard.hotLineAgent.contextholder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utry.data.modular.ccBoard.common.dao.DosCallCenterRecordDao;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.hotLineAgent.context.ProjectActualTimeMonitorContext;
import utry.data.util.RedisUtils;

import java.util.Objects;

/**
 * @program: data
 * @description: 热线项目实时监控上下文管理
 * @author: WangXinhao
 * @create: 2022-10-27 14:58
 **/

@Data
@Component
public class ProjectActualTimeMonitorContextHolder {

    @Autowired
    private DosCallCenterRecordDao dosCallCenterRecordDao;

    @Autowired
    private RedisUtils redisUtils;


    private ProjectActualTimeMonitorContext context;

    public void init(ProjectActualTimeMonitorContext context) {
        this.setContext(context);
    }

    /**
     * 获取接入量
     *
     * @return 接入量
     */
    public Integer getAccessNumber() {
        if (Objects.nonNull(context.getAccessNumber())) {
            return context.getAccessNumber();
        }

        DateDurationQueueIdDto request = context.getRequest();
        Integer accessNumber = null;

        String key = "accessNumber:" + request.getStartDate() + ":" + request.getEndDate() + ":" + request.getQueueId();
        if (redisUtils.hasKey(key)) {
            accessNumber = (Integer) redisUtils.get(key);
        } else {
            accessNumber = dosCallCenterRecordDao.selectAccessNumber(request.getStartDate(), request.getEndDate(), request.getQueueId());
        }

        context.setAccessNumber(accessNumber);
        redisUtils.set(key, accessNumber, 10L);
        return accessNumber;
    }

    /**
     * 获取坐席接起量
     *
     * @return 坐席接起量
     */
    public Integer getConnectionNumber() {
        if (Objects.nonNull(context.getConnectionNumber())) {
            return context.getConnectionNumber();
        }

        DateDurationQueueIdDto request = context.getRequest();
        Integer connectionNumber = null;

        String key = "connectionNumber:" + request.getStartDate() + ":" + request.getEndDate() + ":" + request.getQueueId();
        if (redisUtils.hasKey(key)) {
            connectionNumber = (Integer) redisUtils.get(key);
        } else {
            connectionNumber = dosCallCenterRecordDao.selectConnectionNumber(request);
        }

        context.setConnectionNumber(connectionNumber);
        redisUtils.set(key, connectionNumber, 10L);
        return connectionNumber;
    }

    /**
     * 获取10s内坐席接起量
     *
     * @return 10s内坐席接起量
     */
    public Integer getTenSecondConnectionNumber() {
        if (Objects.nonNull(context.getTenSecondConnectionNumber())) {
            return context.getTenSecondConnectionNumber();
        }

        DateDurationQueueIdDto request = context.getRequest();
        Integer tenSecondConnectionNumber = null;

        String key = "tenSecondConnectionNumber:" + request.getStartDate() + ":" + request.getEndDate() + ":" + request.getQueueId();
        if (redisUtils.hasKey(key)) {
            tenSecondConnectionNumber = (Integer) redisUtils.get(key);
        } else {
            tenSecondConnectionNumber = dosCallCenterRecordDao.selectTenSecondConnectionNumber(request.getStartDate(), request.getEndDate(), request.getQueueId());
        }

        context.setTenSecondConnectionNumber(tenSecondConnectionNumber);
        redisUtils.set(key, tenSecondConnectionNumber, 10L);
        return tenSecondConnectionNumber;
    }

    /**
     * 获取ACD排队时间
     *
     * @return ACD排队时间
     */
    public Integer getAcdQueueTime() {
        if (Objects.nonNull(context.getAcdQueueTime())) {
            return context.getAcdQueueTime();
        }

        DateDurationQueueIdDto request = context.getRequest();
        Integer acdQueueTime = null;

        String key = "acdQueueTime:" + request.getStartDate() + ":" + request.getEndDate() + ":" + request.getQueueId();
        if (redisUtils.hasKey(key)) {
            acdQueueTime = (Integer) redisUtils.get(key);
        } else {
            acdQueueTime = dosCallCenterRecordDao.selectAcdQueueTime(request);
        }

        context.setAcdQueueTime(acdQueueTime);
        redisUtils.set(key, acdQueueTime, 10L);
        return acdQueueTime;
    }

    /**
     * 获取通话时间
     *
     * @return 通话时间
     */
    public Integer getTalkTime() {
        if (Objects.nonNull(context.getTalkTime())) {
            return context.getTalkTime();
        }

        DateDurationQueueIdDto request = context.getRequest();
        Integer talkTime = null;

        String key = "talkTime:" + request.getStartDate() + ":" + request.getEndDate() + ":" + request.getQueueId();
        if (redisUtils.hasKey(key)) {
            talkTime = (Integer) redisUtils.get(key);
        } else {
            talkTime = dosCallCenterRecordDao.selectTalkTime(request);
        }

        context.setTalkTime(talkTime);
        redisUtils.set(key, talkTime, 10L);
        return talkTime;
    }

    /**
     * 获取接入客户数
     *
     * @return 通话时间
     */
    public Integer getCallInCustomerNumber() {
        if (Objects.nonNull(context.getCallInCustomerNumber())) {
            return context.getCallInCustomerNumber();
        }

        DateDurationQueueIdDto request = context.getRequest();
        Integer callInCustomerNumber = null;

        String key = "callInCustomerNumber:" + request.getStartDate() + ":" + request.getEndDate() + ":" + request.getQueueId();
        if (redisUtils.hasKey(key)) {
            callInCustomerNumber = (Integer) redisUtils.get(key);
        } else {
            callInCustomerNumber = dosCallCenterRecordDao.selectCallInCustomerNumber(request);
        }

        context.setCallInCustomerNumber(callInCustomerNumber);
        redisUtils.set(key, callInCustomerNumber, 10L);
        return callInCustomerNumber;
    }

    /**
     * 获取接起客户数
     *
     * @return 通话时间
     */
    public Integer getConnectCustomerNumber() {
        if (Objects.nonNull(context.getConnectCustomerNumber())) {
            return context.getConnectCustomerNumber();
        }

        DateDurationQueueIdDto request = context.getRequest();
        Integer connectCustomerNumber = null;

        String key = "connectCustomerNumber:" + request.getStartDate() + ":" + request.getEndDate() + ":" + request.getQueueId();
        if (redisUtils.hasKey(key)) {
            connectCustomerNumber = (Integer) redisUtils.get(key);
        } else {
            connectCustomerNumber = dosCallCenterRecordDao.selectConnectCustomerNumber(request);
        }

        context.setConnectCustomerNumber(connectCustomerNumber);
        redisUtils.set(key, connectCustomerNumber, 10L);
        return connectCustomerNumber;
    }
}
