package com.firefly.server.http2.router;

import com.firefly.codec.http2.model.*;
import com.firefly.server.http2.SimpleRequest;
import com.firefly.server.http2.SimpleResponse;
import com.firefly.server.http2.router.spi.HTTPBodyHandlerSPI;
import com.firefly.server.http2.router.spi.HTTPSessionHandlerSPI;
import com.firefly.server.http2.router.spi.TemplateHandlerSPI;
import com.firefly.utils.function.Action1;
import com.firefly.utils.json.JsonArray;
import com.firefly.utils.json.JsonObject;

import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pengtao Qiu
 */
public interface RoutingContext extends Closeable {

    Object get(String key);

    Object put(String key, Object value);

    Object remove(String key);

    ConcurrentHashMap<String, Object> getAttributes();

    SimpleResponse getResponse();

    SimpleResponse getAsyncResponse();

    SimpleRequest getRequest();

    String getRouterParameter(String name);

    RoutingContext content(Action1<ByteBuffer> content);

    RoutingContext contentComplete(Action1<SimpleRequest> contentComplete);

    RoutingContext messageComplete(Action1<SimpleRequest> messageComplete);

    boolean isAsynchronousRead();

    boolean next();

    boolean hasNext();


    // request wrap
    default String getMethod() {
        return getRequest().getMethod();
    }

    default HttpURI getURI() {
        return getRequest().getURI();
    }

    default HttpVersion getHttpVersion() {
        return getRequest().getHttpVersion();
    }

    default HttpFields getFields() {
        return getRequest().getFields();
    }

    default long getContentLength() {
        return getRequest().getContentLength();
    }

    default List<Cookie> getCookies() {
        return getRequest().getCookies();
    }


    // response wrap
    default RoutingContext setStatus(int status) {
        getResponse().setStatus(status);
        return this;
    }

    default RoutingContext setReason(String reason) {
        getResponse().setReason(reason);
        return this;
    }

    default RoutingContext setHttpVersion(HttpVersion httpVersion) {
        getResponse().setHttpVersion(httpVersion);
        return this;
    }

    default RoutingContext put(HttpHeader header, String value) {
        getResponse().put(header, value);
        return this;
    }

    default RoutingContext put(String header, String value) {
        getResponse().put(header, value);
        return this;
    }

    default RoutingContext add(HttpHeader header, String value) {
        getResponse().add(header, value);
        return this;
    }

    default RoutingContext add(String name, String value) {
        getResponse().add(name, value);
        return this;
    }

    default RoutingContext addCookie(Cookie cookie) {
        getResponse().addCookie(cookie);
        return this;
    }

    default RoutingContext write(String value) {
        getResponse().write(value);
        return this;
    }

    default RoutingContext end(String value) {
        return write(value).end();
    }

    default RoutingContext end() {
        getResponse().end();
        return this;
    }

    default RoutingContext write(byte[] b, int off, int len) {
        getResponse().write(b, off, len);
        return this;
    }

    default RoutingContext write(byte[] b) {
        return write(b, 0, b.length);
    }

    default RoutingContext end(byte[] b) {
        return write(b).end();
    }


    // HTTP body API
    String getParameter(String name);

    List<String> getParameterValues(String name);

    Map<String, List<String>> getParameterMap();

    Collection<Part> getParts();

    Part getPart(String name);

    InputStream getInputStream();

    BufferedReader getBufferedReader();

    String getStringBody(String charset);

    String getStringBody();

    <T> T getJsonBody(Class<T> clazz);

    JsonObject getJsonObjectBody();

    JsonArray getJsonArrayBody();

    void setHTTPBodyHandlerSPI(HTTPBodyHandlerSPI httpBodyHandlerSPI);


    // HTTP session API
    HttpSession getSession();

    HttpSession getSession(boolean create);

    boolean isRequestedSessionIdFromURL();

    boolean isRequestedSessionIdFromCookie();

    boolean isRequestedSessionIdValid();

    String getRequestedSessionId();

    void setHTTPSessionHandlerSPI(HTTPSessionHandlerSPI httpSessionHandlerSPI);

    // Template API
    void renderTemplate(String resourceName, Object scope);

    void renderTemplate(String resourceName, Object[] scopes);

    void renderTemplate(String resourceName, List<Object> scopes);

    void setTemplateHandlerSPI(TemplateHandlerSPI templateHandlerSPI);
}
