package org.fenixedu.bennu.core.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Locale.Builder;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.Avatar;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.groups.LoggedGroup;
import org.fenixedu.bennu.core.json.adapters.AuthenticatedUserViewer;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.I18N;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Path("/bennu-core/profile")
public class ProfileResource extends BennuRestResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getProfile() {
        return view(null, Void.class, AuthenticatedUserViewer.class);
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
    public String login(@FormParam("username") String username, @FormParam("password") String password) {
        if (!CoreConfiguration.casConfig().isCasEnabled()) {
            Authenticate.login(request.getSession(true), username, password);
            return view(null, Void.class, AuthenticatedUserViewer.class);
        }
        throw AuthorizationException.authenticationFailed();
    }

    @GET
    @Path("logout")
    @Produces(MediaType.APPLICATION_JSON)
    public String logout(@Context HttpServletResponse response) {
        accessControl(LoggedGroup.get());
        Authenticate.logout(request.getSession(false));
        if (CoreConfiguration.casConfig().isCasEnabled()) {
            try {
                response.sendRedirect(CoreConfiguration.casConfig().getCasLogoutUrl());
            } catch (IOException e) {
            }
        } else {
            return view(null, Void.class, AuthenticatedUserViewer.class);
        }
        throw AuthorizationException.authenticationFailed();
    }

    @POST
    @Path("locale/{tag}")
    @Produces(MediaType.APPLICATION_JSON)
    public String changeLocale(@PathParam("tag") String localeTag) {
        try {
            Locale locale = new Builder().setLanguageTag(localeTag).build();
            if (CoreConfiguration.supportedLocales().contains(locale)) {
                I18N.setLocale(request.getSession(true), locale);
                if (Authenticate.isLogged()) {
                    setPreferredLocale(locale);
                }
                return view(null, Void.class, AuthenticatedUserViewer.class);
            }
        } catch (IllformedLocaleException e) {
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
    public String changePreferredLocale(@PathParam("tag") String localeTag) {
        accessControl(LoggedGroup.get());
        try {
            Locale locale = new Builder().setLanguageTag(localeTag).build();
            if (CoreConfiguration.supportedLocales().contains(locale)) {
                Authenticate.getUser().setPreferredLocale(locale);
                I18N.setLocale(request.getSession(true), locale);
                return view(null, Void.class, AuthenticatedUserViewer.class);
            }
        } catch (IllformedLocaleException e) {
        }
        throw BennuCoreDomainException.resourceNotFound(localeTag);
    }

    @GET
    @Path("localavatar/{username}")
    public Response localAvatar(@PathParam("username") String username, @QueryParam("s") int size) {
        User user = User.findByUsername(username);
        if (user != null) {
            Avatar avatar = Avatar.getForUser(user);
            if (avatar != null) {
                return Response.ok(avatar.getData(size), avatar.getMimeType()).build();
            } else {
                try (InputStream mm =
                        ProfileResource.class.getClassLoader().getResourceAsStream("META-INF/resources/img/mysteryman.png")) {
                    return Response.ok(Avatar.process(mm, "image/png", size), "image/png").build();
                } catch (IOException e) {
                    throw BennuCoreDomainException.resourceNotFound(username);
                }
            }
        }
        throw BennuCoreDomainException.resourceNotFound(username);
    }
}