package utry.data.modular.ccBoard.visit.service;

import utry.data.util.RetResult;

import javax.servlet.http.HttpServletRequest;

/**
 * 回访任务ServiceSpi
 *
 * @author zhongdongbiao
 * @date 2022/10/25 14:25
 */
public interface SpiVisitService {

    /**
     * 回访任务数据创建
     * @param request
     * @return
     */
    RetResult create(HttpServletRequest request);

    /**
     * 回访任务数据修改
     * @param request
     * @return
     */
    RetResult updateVisitTask(HttpServletRequest request);

    /**
     * 服务回访记录审核数据创建
     * @param request
     * @return
     */
    RetResult createVisitAudit(HttpServletRequest request);

    /**
     * 服务回访记录审核数据修改
     * @param request
     * @return
     */
    RetResult updateVisitAudit(HttpServletRequest request);

    /**
     * 回访违约单数据创建
     * @param request
     * @return
     */
    RetResult createVisitDefault(HttpServletRequest request);

    /**
     * 回访违约单数据修改
     * @param request
     * @return
     */
    RetResult updateVisitDefault(HttpServletRequest request);
}
