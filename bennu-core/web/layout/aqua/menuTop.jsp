<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@page import="myorg.domain.contents.INode"%>

<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
<bean:define id="menuElements" name="context" property="menuElements"/>

<%
	int count = 0;
	for (INode node : context.getMenuElements()) {
	    if (!node.isManager()) {
			count++;
	    }
	}
	if (count > 1) {
%>

<ul class="mainnav">
<logic:iterate id="node" name="menuElements" type="myorg.domain.contents.Node">
	<logic:equal name="node" property="accessible" value="true">
	<logic:equal name="node" property="manager" value="false">
		<% if (context.contains(node)) { %>
			<li class="selected">
		<% } else { %>
			<li>
		<% } %>
				<% final String url = node.getUrl(); %>
				<% if (url.indexOf(':') > 0) { %>
					<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(node.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link href="<%= url %>">
						<logic:present name="node" property="link">
							<span><bean:write name="node" property="link"/></span>
						</logic:present>
						<logic:notPresent name="node" property="link">
							<span><bean:write name="node"/></span>
						</logic:notPresent>
					</html:link>
				<% } else { %>
					<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(node.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link page="<%= url %>">
						<logic:present name="node" property="link">
							<span><bean:write name="node" property="link"/></span>
						</logic:present>
						<logic:notPresent name="node" property="link">
							<span><bean:write name="node"/></span>
						</logic:notPresent>					
					</html:link>				
				<% } %>
		</li>
	</logic:equal>
	</logic:equal>
</logic:iterate>
</ul>
<% } %>
