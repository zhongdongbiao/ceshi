package utry.data.modular.indicatorWarning.service;

import utry.data.modular.indicatorWarning.vo.IndicatorAnomalyWarningVo;

import java.util.List;
import java.util.Map;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/5/17 16:36
 * description 指标预警的接口
 */
public interface IndicatorWarningService {

    /**
     * 指标预警，站内信
     * @param paramMap 参数列表
     * @return
     */
    void stationLetter(List<Map<String, String>> paramMap);

    /**
     * 3小时预警一次
     * @return
     */
    void threeHoursOneWarning();

    /**
     * 一天预警一次-零件管理
     * @return
     */
    void oneDayOneWarningPartsManagement();

    /**
     * 一天预警一次-大区服务
     * @return
     */
    void oneDayOneWarningDistrict();

    /**
     * 一天预警一次-投诉处理
     * @return
     */
    void oneDayOneWarningComplaint();

    /**
     * 一天预警一次-技术品质
     * @return
     */
    void oneDayOneWarningCategory();

}
