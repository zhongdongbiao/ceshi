package utry.data.common;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.springframework.stereotype.Component;
import utry.data.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * @program: data
 * @description: 按单月分表
 * @author: WangXinhao
 * @create: 2022-06-08 10:27
 **/
@Component
public class DateShardingAlgorithm implements PreciseShardingAlgorithm<Date>, RangeShardingAlgorithm<String> {

    private static final String UNDERLINE = "_";

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Date> preciseShardingValue) {
        String tb_name = preciseShardingValue.getLogicTableName();
        try {
            Date date = preciseShardingValue.getValue();
            String year = String.format("%tY", date);
            String mon = String.format("%tm", date);

            String suffix = getTableSuffix(year, mon);
            tb_name = tb_name + UNDERLINE + suffix;
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String each : availableTargetNames) {
            if (each.equals(tb_name)) {
                return each;
            }
        }
        return tb_name;
    }

    /**
     * 根据年月获取表后缀（后缀范围：0-54）
     *
     * @param year 年
     * @param mon  月
     * @return 后缀 actualDataNodes: db1.t_part_drawing_stock_${0..54}
     */
    private String getTableSuffix(String year, String mon) {
        int yearVal = Integer.parseInt(year);
        int yearMonVal = Integer.parseInt(year + mon);
        int base = 202206;
        int baseYear = 2022;
        int index = yearMonVal - base - (88 * (yearVal - baseYear));
        return String.valueOf(index);
    }

    /**
     * 返回符合范围查询的表名
     * @param availableTargetNames
     * @param shardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<String> shardingValue) {
        String logicTableName = shardingValue.getLogicTableName();
        Range<String> valueRange = shardingValue.getValueRange();
        // 范围下限
        String lowerEndpoint = valueRange.lowerEndpoint();
        // 范围上限
        String upperEndpoint = valueRange.upperEndpoint();

        // 求上限和下限对应的表后缀{0..54}
        String format = "yyyy-MM";
        Date lowerEndpointDate = DateTimeUtil.getFormatDateFromString(lowerEndpoint, format);
        Date upperEndpointDate = DateTimeUtil.getFormatDateFromString(upperEndpoint, format);

        String lowerEndpointYear = String.format("%tY", lowerEndpointDate);
        String lowerEndpointMon = String.format("%tm", lowerEndpointDate);
        String lowerEndpointSuffix = getTableSuffix(lowerEndpointYear, lowerEndpointMon);

        String upperEndpointYear = String.format("%tY", upperEndpointDate);
        String upperEndpointMon = String.format("%tm", upperEndpointDate);
        String upperEndpointSuffix = getTableSuffix(upperEndpointYear, upperEndpointMon);

        // 符合范围的表集合
        Collection<String> collect = new ArrayList<>();
        for (int i = Integer.parseInt(lowerEndpointSuffix); i <= Integer.parseInt(upperEndpointSuffix); i++) {
            String completeTableName = logicTableName + UNDERLINE + i;
            boolean contains = availableTargetNames.contains(completeTableName);
            if (contains) {
                collect.add(completeTableName);
            }
        }
        return collect;
    }
}
