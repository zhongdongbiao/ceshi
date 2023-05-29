package utry.data.modular.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Request包装类
 */
@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    /**
     * @param request HttpServletRequest
     * @description 将request中输入流中的内容保存起来
     */
    public RequestWrapper(HttpServletRequest request) {
        super(request);
        byte[] bytes = null;
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            bytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error("requestWrapper error", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        body = bytes;
    }

    /**
     * @return javax.servlet.ServletInputStream
     * @description 重写getInputStream，返回保存在属性中的body
     */
    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;
    }
}


