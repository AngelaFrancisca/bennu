package org.fenixedu.bennu.portal.client;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.servlet.PortalBackend;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;

public class ClientSidePortalBackend implements PortalBackend {

    public static final String BACKEND_KEY = "client-side";

    @Override
    public SemanticURLHandler getSemanticURLHandler() {
        return new SemanticURLHandler() {
            @Override
            public void handleRequest(MenuFunctionality functionality, HttpServletRequest request, HttpServletResponse response,
                    FilterChain chain) throws IOException, ServletException {
                String forwardUrl =
                        "/" + functionality.getParent().getPath() + "/"
                                + (functionality.getPath().startsWith("#") ? "" : functionality.getPath());
                RequestDispatcher requestDispatcher = request.getRequestDispatcher(forwardUrl);
                if (requestDispatcher != null) {
                    requestDispatcher.forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "No forward url could be processed");
                }
            }
        };
    }

    @Override
    public boolean requiresServerSideLayout() {
        return true;
    }

    @Override
    public String getBackendKey() {
        return BACKEND_KEY;
    }

}
