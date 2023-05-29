package utry.data.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import utry.data.constant.RedisKeyConstant;

import java.util.*;

/**
 * @program: data
 * @description: id生成工具类 - yyyyMMdd000000
 * 例如：今天是 2022-06-06，id 范围 20220606000000-20220606999999
 * @author: WangXinhao
 * @create: 2022-06-06 09:18
 **/
@Component
public class IdGeneratorUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 生成今天的id（步长为 1）
     * @param key redisKey
     * @param length 年月日后面保留的长度
     * @return yyyyMMdd000000
     */
    public String generateIdByToday(String key, Integer length) {
        RedisAtomicLong counter = new RedisAtomicLong(key, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        long num = counter.incrementAndGet();
        counter.expireAt(DateTimeUtil.getTodayEndTime());
        String today = getToday();
        return today + String.format("%0" + length + "d", num);
    }

    /**
     * 批量生成今天的id list（步长为 1）
     * @param key redisKey
     * @param length 年月日后面保留的长度
     * @param size 批量范围
     * @return
     */
    public LinkedList<String> generateIdBatchByToday(String key, Integer length, int size) {
        RedisAtomicLong counter = new RedisAtomicLong(key, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        counter.expireAt(DateTimeUtil.getTodayEndTime());
        long max = counter.addAndGet(size);
        long min = max - size;
        LinkedList<String> list = new LinkedList<>();
        String today = getToday();
        for (int i = 0; i < size; i++) {
            list.add(today + String.format("%0" + length + "d", (min + i)));
        }
        return list;
    }

    private String getToday() {
        String today;
        if (redisTemplate.hasKey(RedisKeyConstant.GENERATE_ID_DATE_PREFIX)) {
            today = (String) redisTemplate.opsForValue().get(RedisKeyConstant.GENERATE_ID_DATE_PREFIX);
        } else {
            today = DateTimeUtil.getTodayFormat();
            redisTemplate.opsForValue().set(RedisKeyConstant.GENERATE_ID_DATE_PREFIX, today, DateTimeUtil.getDValueNowToTodayEndTime());
        }
        return today;
    }
}
