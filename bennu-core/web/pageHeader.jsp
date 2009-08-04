<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>
<%@page import="pt.ist.fenixWebFramework.FenixWebFramework"%>
<%@page import="pt.ist.fenixWebFramework.Config.CasConfig"%>
<%@page import="pt.utl.ist.fenix.tools.util.i18n.Language"%>

<table width="100%">
  <tr>
    <td rowspan="2" width="60px" valign="middle">
		<logic:present name="virtualHost" property="logo">
	    	<bean:define id="logoUrl"><%= request.getContextPath() %>/home.do?method=logo&virtualHostId=<bean:write name="virtualHost" property="externalId"/></bean:define>
			<html:img src='<%= logoUrl %>'/>
		</logic:present>
    </td>
    <td rowspan="2" valign="top" align="left" style="padding-left: 5px; vertical-align: middle;">
		<h1 style="margin-top: 0;"><bean:write name="virtualHost" property="applicationTitle"/></h1>
		<p><bean:write name="virtualHost" property="applicationSubTitle"/></p>
    </td>
    <td align="right" nowrap="nowrap" style="vertical-align: top;">
		<%
			final String contextPath = request.getContextPath();
			final String serverName = request.getServerName();
			final CasConfig casConfig = FenixWebFramework.getConfig().getCasConfig(serverName);
			final boolean isCasEnabled = casConfig != null && casConfig.isCasEnabled();
		%>
		<logic:notPresent name="USER_SESSION_ATTRIBUTE">
			<% if (isCasEnabled) {%>
				<div class="login">
					<% final String portString = request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort(); %>
					<bean:define id="loginUrl"><%= FenixWebFramework.getConfig().getCasConfig(serverName).getCasLoginUrl() + "https" + "://" + request.getServerName() + contextPath %>/</bean:define>
					<html:link href="<%= loginUrl %>"><bean:message key="label.login.link" bundle="MYORG_RESOURCES"/></html:link>
				</div>
			<% } else { %>
				<form action="<%= contextPath %>/authenticationAction.do" class="login" method="post">
					<input type="hidden" name="method" value="login"/>
					<bean:message key="label.login.username" bundle="MYORG_RESOURCES"/>: <input type="text" name="username" size="10"/>
					<bean:message key="label.login.password" bundle="MYORG_RESOURCES"/>: <input type="password" name="password" size="10"/>
					<bean:define id="loginLabel"><bean:message key="label.login.submit" bundle="MYORG_RESOURCES"/></bean:define>
					<input class="inputbuttonlogin" type="submit" name="Submit" value="<%= loginLabel %>"/>
				</form>
			<% } %>
		</logic:notPresent>
		<logic:present name="USER_SESSION_ATTRIBUTE">
			<div class="login">
				<logic:present role="myorg.domain.RoleType.MANAGER">
					<!-- HAS_CONTEXT --><html:link page="/configuration.do?method=applicationConfiguration">
						<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration"/>
					</html:link> |
				</logic:present>
				<logic:present name="virtualHost" property="helpLink">
					<logic:notEmpty name="virtualHost" property="helpLink">
						<bean:define id="helpUrl"><bean:write name="virtualHost" property="helpLink"/></bean:define>
						<a href="<%= helpUrl %>" target="_blank"><bean:message key="label.help.link" bundle="MYORG_RESOURCES"/></a> |  
					</logic:notEmpty>
				</logic:present>
				<bean:message key="label.login.loggedInAs" bundle="MYORG_RESOURCES"/>: <bean:write name="USER_SESSION_ATTRIBUTE" property="username"/> |
				<html:link action="/authenticationAction.do?method=logout"><bean:message key="label.login.logout" bundle="MYORG_RESOURCES"/></html:link>
			</div>
		</logic:present>
	</td>
	</tr>
	<tr>
		<td align="right" nowrap="nowrap" width="40%">
				<logic:equal name="virtualHost" property="languageSelectionEnabled" value="true">
					<bean:define id="languageUrl"><%= request.getContextPath() %>/content.do</bean:define>
					<form action="<%= languageUrl %>" method="post">
						<input type="hidden" name="method" value="viewPage" />
						<logic:present name="selectedNode">
							<bean:define id="arg" name="selectedNode" property="externalId"/>
							<input type="hidden" name="nodeOid" value="<%= arg %>"/>
						</logic:present>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<select name="locale" onchange="this.form.submit();">
							<% final String country = Language.getLocale().getCountry(); %>
							<% for (Language language : Language.values()) {
								if (language == Language.getLanguage()) {
							    	%>
								    	<option value="<%= language.name() %>_<%= country %>" selected="selected">
							    			<%= language.name() %>
							    		</option>
							    	<%
								} else {
								    %>
							    		<option value="<%= language.name() %>_<%= country %>">
								    		<%= language.name() %>
							    		</option>
							    	<%							    
								}
							} %>
						</select>
						<input class=" button" type="submit" name="Submit" value="Ok" />
					</form>
				</logic:equal>
				<logic:equal name="virtualHost" property="googleSearchEnabled" value="true">	
					<bean:define id="site" name="virtualHost" property="hostname"/>
					<!-- BLOCK_HAS_CONTEXT -->
					<!-- NO_CHECKSUM --><form method="get" action="http://www.google.com/search">
						<input type="hidden" name="site" value="<%= site %>" />
						<input type="hidden" name="hl" value="en" />
						<input type="hidden" name="btnG" value="Search" />
						<input type="hidden" name="domains" value="" />
						<input type="hidden" name="sitesearch" value="" />
	
						<input type="text" id="q" name="q" value="Search..." />
						<input class=" button" type="submit" name="Submit" value="Google" />
					</form>
					<!-- END_BLOCK_HAS_CONTEXT -->
				</logic:equal>
		</td>
	</tr>
</table>
