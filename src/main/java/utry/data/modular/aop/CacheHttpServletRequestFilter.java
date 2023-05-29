package utry.data.modular.aop;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * //获取请求中的流，将取出来的，再次转换成流，然后把它放入到新request对象中
 * //必须保证在所有过滤器之前执行,否则就会出现问题(按照首字母进行过滤器优先级A>B>C)
 */
@Component
@ServletComponentScan
@SuppressWarnings("all")
public class CacheHttpServletRequestFilter implements Filter {
    private static final String FORM_CONTENT_TYPE = "multipart/form-data";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * @param servletRequest  servletRequest
     * @param servletResponse servletResponse
     * @param filterChain     filterChain
     * @description 将自定义的ServletRequest替换进filterChain中，使request可以重复读取（此方案暂时弃用）
     */
   /* @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest request = null;
        if (servletRequest instanceof HttpServletRequest) {
            // 过滤掉文件上传
            String contentType = servletRequest.getContentType();
            if (!(contentType != null && contentType.contains(FORM_CONTENT_TYPE))) {
                request = new RequestWrapper((HttpServletRequest) servletRequest);
            }
        }
        if (request != null) {
            filterChain.doFilter(request, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }*/

    /**
     * 更换第二种ContentCachingRequestWrapper的包装方式，避免request两次读取
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            String contentType = request.getContentType();
            if (request instanceof HttpServletRequest) {
                HttpServletRequest requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
                //过滤掉文件上传
                if (contentType != null && contentType.contains(FORM_CONTENT_TYPE)) {
                    chain.doFilter(request, response);
                } else {
                    chain.doFilter(requestWrapper, response);
                }
                return;
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("filter error:::");
        }
    }

    @Override
    public void destroy() {

    }
}


