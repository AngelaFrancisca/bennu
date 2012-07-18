<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@page import="pt.ist.bennu.core.presentationTier.LayoutContext"%>
<%@page import="pt.ist.bennu.core.presentationTier.actions.ContextBaseAction"%>

<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@page import="pt.ist.fenixWebFramework.FenixWebFramework"%>
<%@page import="pt.ist.fenixWebFramework.Config.CasConfig"%>
<%@page import="pt.utl.ist.fenix.tools.util.i18n.Language"%>
<%@page import="pt.ist.fenixWebFramework.security.User"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<%
	final LayoutContext layoutContext = (LayoutContext) ContextBaseAction.getContext(request);
%>
<head>
	<logic:iterate id="head" collection="<%= layoutContext.getHead() %>" type="java.lang.String">
		<jsp:include page="<%= head %>"/>
	</logic:iterate>
	<% final String contextPath = request.getContextPath(); %>
	
	<!--[if IE 7]>
	        <link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/theme_test_beta/ie7.css" />
	<![endif]-->
	<!--[if IE 8]>
	        <link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/theme_test_beta/ie8.css" />
	<![endif]-->
	<!--[if IE 9]>
	        <link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/theme_test_beta/ie9.css" />
	<![endif]-->
	<script type="text/javascript" src="<%= contextPath %>/CSS/aqua/adminbar.js"></script>
</head>
<body>
	<jsp:include page="/layout/aqua/adminbar.jsp"/>

	<header id="header">
		<div class="container">
			<h1>
				<a href="<%= (request.getContextPath().equals("") ? "/" : request.getContextPath()) %>">
					<logic:present name="virtualHost" property="logo">
						<html:img styleClass="logo" action="/home.do?method=logo" paramId="virtualHostId" paramName="virtualHost" paramProperty="externalId" />
					</logic:present>
					<bean:write name="virtualHost" property="applicationTitle"/>
				</a>
			</h1>

			<nav id="perfil">
				<ul>
					<logic:present name="USER_SESSION_ATTRIBUTE">
					<li>
						<span lang="<%= Language.getLanguage().name() %>">
						<%
					    if (org.sotis.domain.core.AuthorGroup.getInstance().isLoggedMember()) {
						%>
							<bean:write name="USER_SESSION_ATTRIBUTE" property="user.person.author.signatureName"/>
						<%
					    } else {
						%>
							<bean:message key="label.login.loggedInAs" bundle="MYORG_RESOURCES"/>: <bean:write name="USER_SESSION_ATTRIBUTE" property="username"/>
						<%
					    }
						%>
						</span>
					</li>
					</logic:present>
					<li><jsp:include page="<%= layoutContext.getProfileLink() %>" /></li>
					<li><jsp:include page="<%= layoutContext.getHelpLink() %>"/></li>
					<li><jsp:include page="<%= layoutContext.getLogin() %>"/></li>
				</ul>
			</nav>

			<jsp:include page="<%= layoutContext.getGoogleSearch() %>"/>
		
			<nav id="tabs">			
				<bean:define id="context" type="pt.ist.bennu.core.presentationTier.Context" name="_CONTEXT_"/>
				<bean:define id="menuElements" name="context" property="menuElements"/>
				<logic:notEmpty name="menuElements">
					<bean:size name="menuElements" id="size"/>
					<logic:greaterThan name="size" value="1">
						<jsp:include page="<%= layoutContext.getMenuTop() %>"/>
					</logic:greaterThan>
				</logic:notEmpty>

				
				<jsp:include page="<%= layoutContext.getSubMenuTop() %>" />
			</nav><!-- tabs -->
			
			<logic:equal name="virtualHost" property="languageSelectionEnabled" value="true">
				<bean:define id="languageUrl"><%= request.getContextPath() %>/home.do</bean:define>
					<div id="language">
						<jsp:include page="<%= layoutContext.getLanguageSelection() %>"/>
					</div>
			</logic:equal>
			
		</div><!-- container -->
	</header><!-- header -->

	
	<div class="container">	
		<jsp:include page="<%= layoutContext.getPageOperations() %>" />
		<jsp:include page="<%= layoutContext.getBreadCrumbs() %>" />
		<jsp:include page="<%= layoutContext.getBody() %>" />
	</div><!-- container_12 -->
	
	<footer id="footer">
		<div class="container">
			<p>
				<logic:present name="virtualHost" property="applicationSubTitle">
					<span class="subtitle"><bean:write name="virtualHost" property="applicationSubTitle"/></span>
				</logic:present>
				<jsp:include page="<%= layoutContext.getFooter() %>"/>
			</p>
		</div>
	</footer><!-- footer -->

</body>
</html:html>
