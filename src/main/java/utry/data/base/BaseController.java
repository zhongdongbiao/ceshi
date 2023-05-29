package utry.data.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * Describe: 基础控制器
 */
public class BaseController {
    protected ResponseEntity<ResultManage> result(Object data) {
        ResultManage resultManage = new ResultManage();
        resultManage.setCode(200);
        resultManage.setData(data);
        return new ResponseEntity<>(resultManage, HttpStatus.OK);
    }

    protected ResponseEntity<ResultManage> result() {
        ResultManage resultManage = new ResultManage();
        resultManage.setCode(200);
        resultManage.setData(null);
        return new ResponseEntity<>(resultManage, HttpStatus.OK);
    }

    protected ResponseEntity<ResultManage> errorResult() {
        ResultManage resultManage = new ResultManage();
        resultManage.setCode(500);
        resultManage.setData(null);
        return new ResponseEntity<>(resultManage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
