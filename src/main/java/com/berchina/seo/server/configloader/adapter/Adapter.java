package com.berchina.seo.server.configloader.adapter;

import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.configloader.exception.server.ServerException;
import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Package com.berchina.seo.server.configloader.adapter
 * @Description: TODO(服务器请求过滤)
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 15/12/23 下午4:17
 * @Version V1.0
 */
@Configuration
public class Adapter extends WebMvcConfigurerAdapter {

    //    private static final Logger LOGGER = LoggerFactory.getLogger(Adapter.class);
    @Autowired
    private Environment env;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SeoSecurityInterceptor())
                .addPathPatterns("/seo/**");
    }

    public class SeoSecurityInterceptor implements HandlerInterceptor {
        /**
         * preHandle方法是进行处理器拦截用的，顾名思义，该方法将在Controller处理之前进行调用，SpringMVC中的Interceptor拦截器是链式的，可以同时存在
         * 多个Interceptor，然后SpringMVC会根据声明的前后顺序一个接一个的执行，而且所有的Interceptor中的preHandle方法都会在
         * Controller方法调用之前调用。SpringMVC的这种Interceptor链式结构也是可以进行中断的，这种中断方式是令preHandle的返
         * 回值为false，当preHandle的返回值为false的时候整个请求就结束了。
         */
        @Override
        public boolean preHandle(
                HttpServletRequest httpServletRequest,
                HttpServletResponse httpServletResponse, Object o) throws Exception {

            Transaction t = Cat.newTransaction("System", "Adapter");
            try {
                String uri = StringUtil.getRequestURI(httpServletRequest);
//                LOGGER.info("Customer request SEO service address : {} ", uri);
                if (!StringUtils.isEmpty(uri)) {
                    String[] args = env.getProperty(Constants.SEO_SYSTEM_REQUEST_ADAPTER).split(",");
                    for (String path : args) {
                        if (uri.contains(path))
                            return true;
                    }
                    t.setStatus(new RuntimeException(ServerException.SEO_REQUEST_ADAPTER.getErroMssage()));
//                    throw new SeoException(
//                            "SEO refused to consumers call, please contact the administrator authorization, thank you!",
//                            ServerException.SEO_REQUEST_ADAPTER);
                }
                t.setStatus(Transaction.SUCCESS); // 设置状态
            } catch (SeoException ex) {
                t.setStatus(ex);  //  设置错误状态
                Cat.logError(ex);
//                LOGGER.error("Consumers request SEO service exception : {}", ex.getMessage());
//                httpServletResponse.sendError(ex.getSeo().getErrCode(), ex.getMessage());
            } finally {
                t.complete();  // 结束Transaction
            }
            return false;
        }

        /**
         * 这个方法只会在当前这个Interceptor的preHandle方法返回值为true的时候才会执行。postHandle是进行处理器拦截用的，它的执行时间是在处理器进行处理之
         * 后，也就是在Controller的方法调用之后执行，但是它会在DispatcherServlet进行视图的渲染之前执行，也就是说在这个方法中你可以对ModelAndView进行操
         * 作。这个方法的链式结构跟正常访问的方向是相反的，也就是说先声明的Interceptor拦截器该方法反而会后调用，这跟Struts2里面的拦截器的执行过程有点像，
         * 只是Struts2里面的intercept方法中要手动的调用ActionInvocation的invoke方法，Struts2中调用ActionInvocation的invoke方法就是调用下一个Interceptor
         * 或者是调用action，然后要在Interceptor之前调用的内容都写在调用invoke之前，要在Interceptor之后调用的内容都写在调用invoke方法之后。
         */
        @Override
        public void postHandle(
                HttpServletRequest httpServletRequest,
                HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        }

        /**
         * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行。该方法将在整个请求完成之后，也就是DispatcherServlet渲染了视图执行，
         * 这个方法的主要作用是用于清理资源的，当然这个方法也只能在当前这个Interceptor的preHandle方法的返回值为true时才会执行。
         */
        @Override
        public void afterCompletion(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        }
    }
}
