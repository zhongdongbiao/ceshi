package utry.data.modular.ccBoard.hotLineAgent.contextholder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utry.data.modular.ccBoard.common.dao.DosCallCenterRecordDao;
import utry.data.modular.ccBoard.hotLineAgent.context.AllDayTenSecondRateContext;
import utry.data.modular.ccBoard.hotLineAgent.dto.DateQueueIdDto;
import utry.data.util.RedisUtils;

import java.util.Objects;

/**
 * @program: data
 * @description: 全天10s接通率上下文管理器
 * @author: WangXinhao
 * @create: 2022-10-31 15:13
 **/

@Data
@Component
public class AllDayTenSecondRateContextHolder {

    @Autowired
    private DosCallCenterRecordDao dosCallCenterRecordDao;

    @Autowired
    private RedisUtils redisUtils;

    private AllDayTenSecondRateContext context;

    public void init(AllDayTenSecondRateContext context) {
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

        DateQueueIdDto request = context.getRequest();
        Integer accessNumber = null;

        String key = "accessNumber:" + request.getDate() + ":" + request.getDate() + ":" + request.getQueueId();
        if (redisUtils.hasKey(key)) {
            accessNumber = (Integer) redisUtils.get(key);
        } else {
            accessNumber = dosCallCenterRecordDao.selectAccessNumber(request.getDate(), request.getDate(), request.getQueueId());
        }

        context.setAccessNumber(accessNumber);
        redisUtils.set(key, accessNumber, 10L);
        return accessNumber;
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

        DateQueueIdDto request = context.getRequest();
        Integer tenSecondConnectionNumber = null;

        String key = "tenSecondConnectionNumber:" + request.getDate() + ":" + request.getDate() + ":" + request.getQueueId();
        if (redisUtils.hasKey(key)) {
            tenSecondConnectionNumber = (Integer) redisUtils.get(key);
        } else {
            tenSecondConnectionNumber = dosCallCenterRecordDao.selectTenSecondConnectionNumber(request.getDate(), request.getDate(), request.getQueueId());
        }

        context.setTenSecondConnectionNumber(tenSecondConnectionNumber);
        redisUtils.set(key, tenSecondConnectionNumber, 10L);
        return tenSecondConnectionNumber;
    }
}
