package utry.data.task;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import utry.core.bo.LoginBean;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.core.common.LoginInfoParams;
import utry.data.base.BaseController;
import utry.data.modular.baseData.controller.BaseDataController;
import utry.data.modular.baseData.dto.HistoriDataDto;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;


/**
 * @author lidakai
 */
@Api(tags = "定时任务执行类-获取历史数据测试")
@ServiceApi
@Controller
public class HistoryDataTestTask extends BaseController {


    @Resource
    private BaseDataController baseDataController;
    /**
     * 同步历史数据
     */
    public static final ThreadPoolExecutor HISTORY_EXECUTOR = new ThreadPoolExecutor(
            50,
            500,
            20L,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(600),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    static {
        // 设置allowCoreThreadTimeout=true（默认false）时，核心线程会超时关闭
        HISTORY_EXECUTOR.allowCoreThreadTimeOut(true);
    }

    @ApiOperation(value = "一次性拉取", notes = "入口")
    @RequestMapping(value = "/api/task/100060/historyData", method = RequestMethod.POST)
    public ResponseEntity disposable(String param) {
        LoginBean sp = LoginInfoParams.getSp();
        LoginInfoParams.addCompanyIdAsLoginBean("222");
        LoginBean sp1 = LoginInfoParams.getSp();
        RetResult result = baseDataController.getHistoryDisposable();
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        result = baseDataController.insertHistoryDisposable((List<HistoriDataDto>) result.getData());
        if (200 != result.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }

    @ApiOperation(value = "多线程拉取", notes = "入口")
    @RequestMapping(value = "/api/task/100060/batchByBatch", method = RequestMethod.POST)
    public ResponseEntity batchByBatch(@RequestParam String param) {
        JSONObject parse = JSONObject.parseObject(param);
        Integer start = parse.getInteger("start");
        Integer end = parse.getInteger("end");
        Integer newEnd = start;
        RetResult result;
        long l = System.currentTimeMillis();
        System.out.println();
        Integer addSize = 5000 *2;
        Integer added = 0;
        if (start <= end) {
            while (true) {
                newEnd = start + addSize;
                if (newEnd <= end) {
                    System.out.println(start + "-" + newEnd);
                    LoginInfoParams.addCompanyIdAsLoginBean("222");
                    result = baseDataController.getHistoryBybatch(start, newEnd);
                    LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
                    result = baseDataController.insertHistoryDisposable((List<HistoriDataDto>) result.getData());
                } else {
                    System.out.println(start + "-" + end);
                    LoginInfoParams.addCompanyIdAsLoginBean("222");
                    result = baseDataController.getHistoryBybatch(start, end);
                    LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
                    result = baseDataController.insertHistoryDisposable((List<HistoriDataDto>) result.getData());
                    break;
                }
                start += addSize + 1;
            }
        }
        long l2 = System.currentTimeMillis();
        System.out.println("共耗时：" + (l2 - l));
        return this.errorResult();
    }

    @ApiOperation(value = "历史数据拉取", notes = "入口")
    @RequestMapping(value = "/api/task/100060/batchByThread", method = RequestMethod.POST)
    public ResponseEntity batchByThread(@RequestParam String param) throws ExecutionException, InterruptedException {
        JSONObject parse = JSONObject.parseObject(param);
        Integer start = parse.getInteger("start");
        Integer end = parse.getInteger("end");
        Integer newEnd = start;
        long l = System.currentTimeMillis();
        System.out.println("开始计时：" + l);
        Integer addSize = 5000;
        List<Future<RetResult>> results = new LinkedList<Future<RetResult>>();
        if (start < end) {
            while (true) {
                newEnd = start + addSize;
                if (newEnd < end) {
                    Integer finalStart = start;
                    Integer finalEnd = newEnd;
                    Callable myCallable = new Callable<RetResult>() {
                        @Override
                        public RetResult call() {
                            return save(finalStart, finalEnd);
                        }
                    };
                    results.add(HISTORY_EXECUTOR.submit(myCallable));
                } else {
                    Integer finalStart1 = start;
                    Callable myCallable1 = new Callable<RetResult>() {
                        @Override
                        public RetResult call() {
                            return save(finalStart1, end);
                        }
                    };
                    results.add(HISTORY_EXECUTOR.submit(myCallable1));
                    break;
                }
                start += addSize + 1;
            }
        }
        int size = results.size();
        System.out.println("共需执行" + size + "次");
        System.out.println("主线程开始阻塞===");
        Integer i = 0;
        Integer added = 0;
        for (Future<RetResult> result : results) {
            RetResult s = (RetResult) result.get();
            Integer data = (Integer) s.getData();
            added += data;
            System.out.println("遍历第"+(++i)+"次执行结果==" + s.toString());
        }
        System.out.println("执行结束,共耗时：" + (System.currentTimeMillis() - l) + "ms,共插入" + added + "条");
        return this.errorResult();
    }


     RetResult save(Integer start, Integer end) {
        RetResult result = null;
        String name = Thread.currentThread().getName();
        try {
            System.out.println(name + "|" + start + "-" + end);
            LoginInfoParams.addCompanyIdAsLoginBean("222");
//            System.out.println(name + "-se");
            result = baseDataController.getHistoryBybatch(start, end);
            LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
//            System.out.println(name + "-in");
            return baseDataController.insertHistoryDisposable((List<HistoriDataDto>) result.getData());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("丢数据记录" + name + "|" + start + "-" + end);
        }
        return RetResponse.makeErrRsp(name + "丢数据了");
    }


}
