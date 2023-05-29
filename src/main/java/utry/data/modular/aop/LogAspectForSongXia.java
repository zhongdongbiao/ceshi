package utry.data.modular.aop;

import org.apache.commons.io.IOUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.core.sysConfig.ISysConfService;
import utry.data.modular.aop.dao.LogMapper;
import utry.data.util.RetResult;
import utry.data.util.TimeUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * AOP切面 松下日志记录
 */
@Aspect
@Component
@SuppressWarnings("all")
public class LogAspectForSongXia extends CommonController {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat MonthOfDaySdf = new SimpleDateFormat("yyyy-MM-dd");

    @Resource
    private LogMapper logMapper;
    @Resource
    ISysConfService iSysConfService;

    /**
     * 松下日志数据保存专用线程池
     */
    public static final ThreadPoolExecutor DOCK_EXECUTOR = new ThreadPoolExecutor(
            90,
            400,
            3600L,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(8000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Pointcut(value = "execution( * utry.data.modular.api..*(..)) || execution( * utry.data.modular.spi..*(..))" +
            "|| execution( * utry.data.modular.region.controller.RegionController.directManagementAreaScore(..))")
    public void webPointCut() {
    }


    /**
     * 主要采用后置通知
     * 后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
     *
     * @param jp
     * @param returnValue
     */
    @AfterReturning(pointcut = "webPointCut()", returning = "returnValue")
    public void after(JoinPoint jp, Object returnValue) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        Boolean logFlag = Boolean.valueOf(iSysConfService.getSystemConfig("logFlag"));
        try {
            String className = jp.getSignature().getDeclaringTypeName();
            String methodName = jp.getSignature().getName();
            RetResult<Object> returnValueRetResult = (RetResult<Object>) returnValue;
            int code = returnValueRetResult.getCode();
            if (!logFlag && code == 200) {
                return;
            }
            String parameter = "无参数";
            try {
                Object firstParam = jp.getArgs()[0];
                if (firstParam instanceof HttpServletRequest) {
                    parameter = IOUtils.toString(((ContentCachingRequestWrapper) firstParam).getContentAsByteArray(), "utf-8")
                            .replace("\\", "\\\\")
                            .replaceAll("\\n", "")
                            .replaceAll(" ", "");
                } else {
                    parameter = (String) firstParam;
                }
            } catch (Exception e) {
                System.out.println("no Param" + e.getMessage());
            }
            String describe = "执行了" + methodName;
            String returnStr = returnValue.toString().replaceAll("'", "").replaceAll("\\n", "");

        /*try {
            RetResult retResult = (RetResult) returnValue;
            JSONObject data = (JSONObject) retResult.getData();
            code = Integer.parseInt(data.getString("code"));
        } catch (Exception e) {
        }
        if (!logFlag && code == 200) {
            return;
        }*/

            SongXiaLog songXiaLog = new SongXiaLog(UUID.randomUUID().toString().replaceAll("-", ""),
                    className + "." + methodName,
                    parameter,
                    returnStr,
                    describe,
                    sdf.format(new Date()),
                    MonthOfDaySdf.format(new Date()),
                    code + "");
            DOCK_EXECUTOR.submit(() -> {
                this.hardwareCallLogSave(songXiaLog);
            });
        } catch (Exception e) {
            System.out.println("aspect error");
            e.printStackTrace();
        }
    }

    /**
     * 松下接口存储操作日志方法
     * 存储到Mysql
     * 注：同步方法
     *
     * @param songXiaLog
     * @return
     */
//    public synchronized void hardwareCallLogSave(SongXiaLog songXiaLog) {
    public void hardwareCallLogSave(SongXiaLog songXiaLog) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try {
            this.logMapper.insertSongXiaLog(songXiaLog);
        } catch (Exception e) {
            System.out.println("insert throw error");
            e.printStackTrace();
        }
    }


}
