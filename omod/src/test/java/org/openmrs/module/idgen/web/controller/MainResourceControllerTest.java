package org.openmrs.module.idgen.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Facilitates testing controllers.
 */
public abstract class MainResourceControllerTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private AnnotationMethodHandlerAdapter handlerAdapter;

    @Autowired
    private List<DefaultAnnotationHandlerMapping> handlerMappings;

    /**
     * Creates a request from the given parameters.
     * <p/>
     * The requestURI is automatically preceded with "/rest/" + RestConstants.VERSION_1.
     *
     * @param method
     * @param requestURI
     * @return
     */
    public MockHttpServletRequest request(RequestMethod method, String requestURI) {
        MockHttpServletRequest request = new MockHttpServletRequest(method.toString(), "/rest/" + getNamespace() + "/"
                + requestURI);
        request.addHeader("content-type", "application/json");
        return request;
    }

    /**
     * Override this method to test a different namespace than v1.
     *
     * @return the namespace
     */
    public String getNamespace() {
        return RestConstants.VERSION_1;
    }

    public static class Parameter {

        public String name;

        public String value;

        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public MockHttpServletRequest newRequest(RequestMethod method, String requestURI, Parameter... parameters) {
        MockHttpServletRequest request = request(method, requestURI);
        for (Parameter parameter : parameters) {
            request.addParameter(parameter.name, parameter.value);
        }
        return request;
    }

    public MockHttpServletRequest newDeleteRequest(String requestURI, Parameter... parameters) {
        return newRequest(RequestMethod.DELETE, requestURI, parameters);
    }

    public MockHttpServletRequest newGetRequest(String requestURI, Parameter... parameters) {
        return newRequest(RequestMethod.GET, requestURI, parameters);
    }

    public MockHttpServletRequest newPostRequest(String requestURI, Object content) {
        MockHttpServletRequest request = request(RequestMethod.POST, requestURI);
        try {
            String json = new ObjectMapper().writeValueAsString(content);
            request.setContent(json.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    public MockHttpServletRequest newPostRequest(String requestURI, String content) {
        MockHttpServletRequest request = request(RequestMethod.POST, requestURI);
        try {
            request.setContent(content.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    /**
     * Passes the given request to a proper controller.
     *
     * @param request
     * @return
     * @throws Exception
     */
    public MockHttpServletResponse handle(HttpServletRequest request) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        HandlerExecutionChain handlerExecutionChain = null;
        for (DefaultAnnotationHandlerMapping handlerMapping : handlerMappings) {
            handlerExecutionChain = handlerMapping.getHandler(request);
            if (handlerExecutionChain != null) {
                break;
            }
        }
        Assert.assertNotNull("The request URI does not exist", handlerExecutionChain);

        handlerAdapter.handle(request, response, handlerExecutionChain.getHandler());

        return response;
    }

    /**
     * Deserializes the JSON response.
     *
     * @param response
     * @return
     * @throws Exception
     */
    public SimpleObject deserialize(MockHttpServletResponse response) throws Exception {
        return new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
    }

}