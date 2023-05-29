package utry.data.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import utry.data.enums.BizCodeEnum;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: dev
 * @description: 校验异常处理
 * @author: WangXinhao
 * @create: 2022-02-10 15:13
 **/

@ControllerAdvice(basePackages = "com.utry.data")
public class ValidExceptionHandle {

    Logger logger = LoggerFactory.getLogger(ValidExceptionHandle.class);

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public RetResult<Void> handleValidException(MethodArgumentNotValidException e) {
        logger.error("参数校验错误{} 异常类型{}", e.getMessage(), e.getClass());

        // 校验完的错误信息结果
        BindingResult bindingResult = e.getBindingResult();

        Map<String, String> errMap = new HashMap<>();
        String msg = "";
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            msg = msg + fieldError.getDefaultMessage();
        }
        return RetResponse.makeErrRsp(BizCodeEnum.PARAMETER_ERROR.getMessage());

    }
}
