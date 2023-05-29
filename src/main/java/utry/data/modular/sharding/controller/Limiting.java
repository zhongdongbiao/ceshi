package utry.data.modular.sharding.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author : ldk
 * @date : 23:22 2022/7/4
 * @desc ： Semaphore实现接口限流
 */
@RestController
@RequestMapping("/subApi")
public class Limiting {

    /**
     * 最大信号量，例如此处1，生成环境可以做成可配置项，通过注入方式进行注入
     */
    private static final int MAX_SEMAPHORE = 1;
    /**
     * 获取信号量最大等待时间
     */
    private static int TIME_OUT = 1;

    /**
     * Semaphore主限流，全局就行
     */
    private static final Semaphore SEMAPHORE = new Semaphore(MAX_SEMAPHORE, false);

    @ApiOperation(value = "获取担当信息")
    @GetMapping("/limit")
    public RetResult getAllBear() throws InterruptedException {
        // 使用阻塞Acquire，如果获取不到就快速返回失败
        if (!(SEMAPHORE.tryAcquire(TIME_OUT, TimeUnit.SECONDS))) {
            return RetResponse.makeErrRsp("请您稍后再试");
        }
        System.out.println("拿到令牌了");
        try {
            // 执行你的业务逻辑
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 一定要释放，否则导致接口假死无法处理请求
            SEMAPHORE.release();
        }
        return RetResponse.makeOKRsp();
    }
}
