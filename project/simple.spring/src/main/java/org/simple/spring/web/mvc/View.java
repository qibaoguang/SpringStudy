package org.simple.spring.web.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface View {

    void render(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception;

}
