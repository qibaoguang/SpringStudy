package org.simple.spring.web.mvc;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class FreeMarkerView implements View, ServletContextAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String DEFAULT_CHARSET = "UTF-8";

    private final String encoding;
    private final String contentType;
    private final FreeMarkerConfig config;
    private ServletContext servletContext;
    private ServletContextHashModel servletContextHashModel;

    public FreeMarkerView(final FreeMarkerConfig config, final String encoding,
            final String contentType) {
        if (config == null) {
            throw new NullPointerException("FreeMarkerConfig required ");
        }
        this.config = config;
        this.encoding = (encoding == null || encoding.isEmpty() ? DEFAULT_CHARSET
                : encoding);
        this.contentType = (contentType == null || contentType.isEmpty() ? DEFAULT_CONTENT_TYPE
                : contentType);
    }

    // The template name
    public abstract String getUrl();

    protected Configuration getConfiguration() {
        return config.getConfiguration();
    }

    protected TaglibFactory getTaglibFactory() {
        return config.getTaglibFactory();
    }

    protected ObjectWrapper getObjectWrapper() {
        ObjectWrapper ow = getConfiguration().getObjectWrapper();
        return (ow != null ? ow : ObjectWrapper.DEFAULT_WRAPPER);
    }

    protected void initServletContext(ServletContext servletContext) {
        GenericServlet servlet = new GenericServletAdapter();
        try {
            servlet.init(new DelegatingServletConfig());
        } catch (ServletException e) {
            throw new RuntimeException(
                    "Initialization of GenericServlet adapter fail", e);
        }
        // Ensure that the servletContextHashModel initialized only once.
        this.servletContextHashModel = new ServletContextHashModel(servlet,
                getObjectWrapper());
    }

    protected String getViewName() {
        return FreeMarkerView.class.getSimpleName();
    }

    public void setServletContext(ServletContext servletContext) {
        if (servletContext != this.servletContext) {
            this.servletContext = servletContext;
            if (servletContext != null) {
                initServletContext(servletContext);
            }
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getContextType() {
        return this.contentType;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void render(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("Rendering view with name '{}' with model [ {} ]  ",
                getViewName(), model);
        // Check response's contentType
        applyContentType(response);
        // Expose model to JSP tags
        exposeModelAsRequestAttributes(model, request);
        // Expose all standard FreeMaker hash models
        final SimpleHash fmModel = buildTemplateModel(model, request, response);
        logger.debug(
                "Rendering FreeMarker template [ {} ] in FreeMarkerView '{}'",
                getUrl(), getViewName());
        final String url = getUrl();
        if (url == null || url.isEmpty()) {
            throw new NullPointerException("FreeMaker template url required");
        }
        processTemplate(getTemplate(url), fmModel, response);
    }

    protected void applyContentType(HttpServletResponse response) {
        if (response.getContentType() == null) {
            response.setContentType(getContextType());
        }
    }

    protected void processTemplate(Template template, SimpleHash model,
            HttpServletResponse response) throws TemplateException, IOException {
        template.process(model, response.getWriter());
    }

    protected Template getTemplate(String name) throws IOException {
        return getConfiguration().getTemplate(name, getEncoding());
    }

    protected SimpleHash buildTemplateModel(Map<String, Object> model,
            HttpServletRequest request, HttpServletResponse response) {
        AllHttpScopesHashModel fmModel = new AllHttpScopesHashModel(
                getObjectWrapper(), getServletContext(), request);
        fmModel.put(FreemarkerServlet.KEY_JSP_TAGLIBS,
                this.config.getTaglibFactory());
        fmModel.put(FreemarkerServlet.KEY_APPLICATION,
                this.servletContextHashModel);
        fmModel.put(FreemarkerServlet.KEY_SESSION,
                buildSessionModel(request, response));
        fmModel.put(FreemarkerServlet.KEY_REQUEST, new HttpRequestHashModel(
                request, response, getObjectWrapper()));
        fmModel.put(FreemarkerServlet.KEY_REQUEST_PARAMETERS,
                new HttpRequestParametersHashModel(request));
        fmModel.putAll(model);
        return fmModel;
    }

    protected HttpSessionHashModel buildSessionModel(
            HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return new HttpSessionHashModel(session, getObjectWrapper());
        } else {
            return new HttpSessionHashModel(null, request, response,
                    getObjectWrapper());
        }
    }

    protected void exposeModelAsRequestAttributes(Map<String, Object> model,
            HttpServletRequest request) {
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            String modelName = entry.getKey();
            Object modelValue = entry.getValue();
            if (modelValue != null) {
                request.setAttribute(modelName, modelValue);
                logger.debug(
                        "Added model object '{}' of type [{}] to request in view with name '{}'",
                        modelName, modelValue.getClass().getName(),
                        getViewName());
            } else {
                request.removeAttribute(modelName);
                logger.debug(
                        "Removed model object '{}' from request in view with name '{}'",
                        modelName, getViewName());
            }
        }
    }

    private static class GenericServletAdapter extends GenericServlet {
        private static final long serialVersionUID = 1L;

        @Override
        public void service(ServletRequest request, ServletResponse response)
                throws ServletException, IOException {
            // do nothing
        }
    }

    private class DelegatingServletConfig implements ServletConfig {

        public String getInitParameter(String paramName) {
            return null;
        }

        public Enumeration<String> getInitParameterNames() {
            return Collections.emptyEnumeration();
        }

        public ServletContext getServletContext() {
            return FreeMarkerView.this.getServletContext();
        }

        public String getServletName() {
            return FreeMarkerView.this.getViewName();
        }
    }

}
