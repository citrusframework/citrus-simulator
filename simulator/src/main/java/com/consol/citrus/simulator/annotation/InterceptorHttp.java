package com.consol.citrus.simulator.annotation;

import com.consol.citrus.http.controller.HttpMessageController;
import com.consol.citrus.message.RawMessage;
import com.consol.citrus.report.MessageListeners;
import com.consol.citrus.util.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Interceptor for {@literal <citrus-http:server />} endpoints. Adding this interceptor to a http-endpoint ensures that
 * {@code MessageListeners} are notified when a http message is sent or received.
 */
public class InterceptorHttp implements HandlerInterceptor {


    private final MessageListeners messageListeners;

    public InterceptorHttp(MessageListeners messageListeners) {
        this.messageListeners = messageListeners;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (messageListeners != null) {
            messageListeners.onInboundMessage(new RawMessage(getRequestContent(request)), null);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (messageListeners != null) {
            messageListeners.onOutboundMessage(new RawMessage(getResponseContent(response, handler)), null);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private String getRequestContent(HttpServletRequest request) throws IOException {
        return FileUtils.readToString(request.getInputStream());
    }

    private String getResponseContent(HttpServletResponse response, Object handler) {
        if (handler instanceof HttpMessageController) {
            HttpMessageController handlerController = (HttpMessageController) handler;
            ResponseEntity<String> responseEntity = handlerController.getResponseCache();
            if (responseEntity != null) {
                return responseEntity.getBody();
            }
        }
        return "Could not extract Http Response";
    }
}
