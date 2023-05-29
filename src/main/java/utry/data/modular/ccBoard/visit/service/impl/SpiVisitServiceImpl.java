package utry.data.modular.ccBoard.visit.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import org.springframework.stereotype.Service;
import utry.data.modular.ccBoard.visit.dao.VisitAuditDao;
import utry.data.modular.ccBoard.visit.dao.VisitDao;
import utry.data.modular.ccBoard.visit.dao.VisitDefaultDao;
import utry.data.modular.ccBoard.visit.model.VisitAudit;
import utry.data.modular.ccBoard.visit.model.VisitDefault;
import utry.data.modular.ccBoard.visit.model.VisitTask;
import utry.data.modular.ccBoard.visit.service.SpiVisitService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;
import utry.data.util.TimeUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 回访任务SpiService实现类
 *
 * @author zhongdongbiao
 * @date 2022/10/25 14:25
 */
@Service
public class SpiVisitServiceImpl implements SpiVisitService {

    @Resource
    private VisitDao visitDao;

    @Resource
    private VisitAuditDao visitAuditDao;

    @Resource
    private VisitDefaultDao visitDefaultDao;

    /**
     * 回访任务数据创建
     * @param request
     * @return
     */
    @Override
    public RetResult create(HttpServletRequest request) {
        try{
            if(request!=null){
                VisitTask visitTask =new VisitTask();
                VisitTask insertTask = (VisitTask) TimeUtil.requestToObject(request,visitTask);
                VisitTask flag = visitDao.getFlag(insertTask.getServiceNumber());
                if(flag!=null){
                    return RetResponse.makeRsp(401,"回访任务已存在！");
                }
                visitDao.create(insertTask);
            }else {
                return RetResponse.makeRsp(500,"创建失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeRsp(500,"创建失败！");
        }
        return RetResponse.makeOKRsp();
    }

    /**
     * 回访任务数据修改
     * @param request
     * @return
     */
    @Override
    public RetResult updateVisitTask(HttpServletRequest request) {
        try{
            if(request!=null){
                VisitTask visitTask =new VisitTask();
                VisitTask updateTask = (VisitTask) TimeUtil.requestToObject(request,visitTask);
                VisitTask flag = visitDao.getFlag(updateTask.getServiceNumber());
                if(flag!=null){
                    if(flag.getUpdateTime()==null){
                        visitDao.updateVisitTask(updateTask);
                    }else {
                        if(LocalDateTimeUtil.parse(flag.getUpdateTime(),"yyyy-MM-dd HH:mm:ss").isBefore(LocalDateTimeUtil.parse(updateTask.getUpdateTime(),"yyyy-MM-dd HH:mm:ss"))){
                            visitDao.updateVisitTask(updateTask);
                        }
                    }
                }else {
                    visitDao.create(updateTask);
                }
            }else {
                return RetResponse.makeRsp(500,"修改失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeRsp(500,"修改失败！");
        }
        return RetResponse.makeOKRsp();
    }

    /**
     * 服务回访记录审核数据创建
     * @param request
     * @return
     */
    @Override
    public RetResult createVisitAudit(HttpServletRequest request) {
        try{
            if(request!=null){
                VisitAudit visitAudit =new VisitAudit();
                VisitAudit insertAudit = (VisitAudit) TimeUtil.requestToObject(request,visitAudit);
                VisitAudit flag = visitAuditDao.getFlag(insertAudit.getServiceNumber());
                if(flag!=null){
                    return RetResponse.makeRsp(401,"服务回访记录审核数据已存在！");
                }
                visitAuditDao.createVisitAudit(insertAudit);
                visitAuditDao.createVisitAuditHistory(insertAudit);
            }else {
                return RetResponse.makeRsp(500,"创建失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeRsp(500,"创建失败！");
        }
        return RetResponse.makeOKRsp();
    }

    /**
     * 服务回访记录审核数据修改
     * @param request
     * @return
     */
    @Override
    public RetResult updateVisitAudit(HttpServletRequest request) {
        try{
            if(request!=null){
                VisitAudit visitAudit =new VisitAudit();
                VisitAudit updateAudit = (VisitAudit) TimeUtil.requestToObject(request,visitAudit);
                VisitAudit flag = visitAuditDao.getFlag(updateAudit.getServiceNumber());
                if(flag==null){
                    visitAuditDao.createVisitAudit(updateAudit);
                }else {
                    visitAuditDao.updateVisitAudit(updateAudit);
                }

            }else {
                return RetResponse.makeRsp(500,"修改失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeRsp(500,"修改失败！");
        }
        return RetResponse.makeOKRsp();
    }

    /**
     * 回访违约单数据创建
     * @param request
     * @return
     */
    @Override
    public RetResult createVisitDefault(HttpServletRequest request) {
        try{
            if(request!=null){
                VisitDefault visitDefault =new VisitDefault();
                VisitDefault insertDefault = (VisitDefault) TimeUtil.requestToObject(request,visitDefault);
                VisitDefault flag = visitDefaultDao.getFlag(insertDefault.getServiceNumber());
                if(flag!=null){
                    return RetResponse.makeRsp(401,"服务回访违单数据已存在！");
                }
                visitDefaultDao.createVisitAudit(insertDefault);
            }else {
                return RetResponse.makeRsp(500,"创建失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeRsp(500,"创建失败！");
        }
        return RetResponse.makeOKRsp();
    }

    /**
     * 回访违约单数据修改
     * @param request
     * @return
     */
    @Override
    public RetResult updateVisitDefault(HttpServletRequest request) {
        try{
            if(request!=null){
                VisitDefault visitDefault =new VisitDefault();
                VisitDefault updateDefault = (VisitDefault) TimeUtil.requestToObject(request,visitDefault);
                VisitDefault flag = visitDefaultDao.getFlag(updateDefault.getServiceNumber());
                if(flag==null){
                    visitDefaultDao.createVisitAudit(updateDefault);
                }else {
                    visitDefaultDao.updateVisitDefault(updateDefault);
                }

            }else {
                return RetResponse.makeRsp(500,"修改失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeRsp(500,"修改失败！");
        }
        return RetResponse.makeOKRsp();
    }
}
