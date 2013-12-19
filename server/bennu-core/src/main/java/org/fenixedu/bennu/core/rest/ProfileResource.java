package org.fenixedu.bennu.core.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.domain.groups.LoggedGroup;
import org.fenixedu.bennu.core.rest.json.UserSessionViewer;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.security.UserSession;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.I18N;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Path("profile")
public class ProfileResource extends BennuRestResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile() {
        return Response.ok(view(Authenticate.getUserSession(), UserSession.class, UserSessionViewer.class)).build();
    }

    @GET
    @Path("caslogin")
    public Response caslogin() {
        if (CoreConfiguration.casConfig().isCasEnabled()) {
            try {
                return Response.temporaryRedirect(new URI(CoreConfiguration.casConfig().getCasLoginUrl(request))).build();
            } catch (URISyntaxException e) {
            }
        }
        throw AuthorizationException.authenticationFailed();
    }

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {
        if (!CoreConfiguration.casConfig().isCasEnabled()) {
            return Response.ok(view(Authenticate.login(request.getSession(true), username, password))).build();
        }
        throw AuthorizationException.authenticationFailed();
    }

    @GET
    @Path("logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletResponse response) {
        accessControl(LoggedGroup.getInstance());
        Authenticate.logout(request.getSession(false));
        if (CoreConfiguration.casConfig().isCasEnabled()) {
            try {
                response.sendRedirect(CoreConfiguration.casConfig().getCasLogoutUrl());
            } catch (IOException e) {
            }
        } else {
            return Response.ok(view(null, UserSession.class, UserSessionViewer.class)).build();
        }
        throw AuthorizationException.authenticationFailed();
    }

    @POST
    @Path("locale/{tag}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeLocale(@PathParam("tag") String localeTag) {
        if (CoreConfiguration.supportedLocales().contains(Locale.forLanguageTag(localeTag))) {
            final Locale locale = Locale.forLanguageTag(localeTag);
            I18N.setLocale(request.getSession(true), locale);
            if (Authenticate.isLogged()) {
                setPreferredLocale(locale);
            }
            return Response.ok(view(Authenticate.getUserSession(), UserSession.class, UserSessionViewer.class)).build();
        }
        throw BennuCoreDomainException.resourceNotFound(localeTag);
    }

    @Atomic(mode = TxMode.WRITE)
    private void setPreferredLocale(Locale locale) {
        Authenticate.getUser().setPreferredLocale(locale);
    }

    @POST
    @Path("preferred-locale/{tag}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePreferredLocale(@PathParam("tag") String localeTag) {
        accessControl(LoggedGroup.getInstance());
        if (CoreConfiguration.supportedLocales().contains(Locale.forLanguageTag(localeTag))) {
            Locale locale = Locale.forLanguageTag(localeTag);
            Authenticate.getUser().setPreferredLocale(locale);
            I18N.setLocale(request.getSession(true), locale);
            return Response.ok(view(Authenticate.getUserSession(), UserSession.class, UserSessionViewer.class)).build();
        }
        throw BennuCoreDomainException.resourceNotFound(localeTag);
    }
}