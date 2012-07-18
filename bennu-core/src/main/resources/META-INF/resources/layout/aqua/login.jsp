<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@page import="pt.ist.fenixWebFramework.FenixWebFramework"%>
<%@page import="pt.ist.fenixWebFramework.Config.CasConfig"%>

<%
	final String contextPath = request.getContextPath();
	final String serverName = request.getServerName();
	final CasConfig casConfig = FenixWebFramework.getConfig().getCasConfig(serverName);
	// If no config is found don't allow login.
	if (casConfig == null) {
%>
		<!-- context: <%= contextPath %> - serverName: <%= serverName %> -->
<%
	} else {
		final boolean isCasEnabled = casConfig.isCasEnabled();
%>
<logic:notPresent name="USER_SESSION_ATTRIBUTE">
	<% if (isCasEnabled) {%>
		<% final String portString = (request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort(); %>
		<bean:define id="loginUrl"><%= FenixWebFramework.getConfig().getCasConfig(serverName).getCasLoginUrl() + "https" + "://" + request.getServerName() + contextPath %>/</bean:define>
		<html:link href="<%= loginUrl %>"><bean:message key="label.login.link" bundle="MYORG_RESOURCES"/></html:link>
	<% } else { %>
		<form action="<%= contextPath %>/authenticationAction.do" class="login" method="post">
			<input type="hidden" name="method" value="login"/>
			<span><bean:message key="label.login.username" bundle="MYORG_RESOURCES"/>: <input type="text" name="username" size="10"/></span>
			<span><bean:message key="label.login.password" bundle="MYORG_RESOURCES"/>: <input type="password" name="password" size="10"/></span>
			<bean:define id="loginLabel"><bean:message key="label.login.submit" bundle="MYORG_RESOURCES"/></bean:define>
			<input class="inputbuttonlogin" type="submit" name="Submit" value="<%= loginLabel %>"/>
		</form>
	<% } %>
</logic:notPresent>
<% } %>

<logic:present name="USER_SESSION_ATTRIBUTE">
	<html:link action="/authenticationAction.do?method=logout"><bean:message key="label.login.logout" bundle="MYORG_RESOURCES"/></html:link>
</logic:present>
