<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<jsp:include page="../default/subMenuTop.jsp" />


<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
<bean:define id="menuElements" name="context" property="menuElements"/>

<logic:notEmpty name="menuElements">
	<logic:iterate id="node" name="menuElements" indexId="nindex" type="myorg.domain.contents.Node">
		<logic:equal name="node" property="accessible" value="true">
		<logic:equal name="node" property="manager" value="false">
			<logic:notEmpty name="node" property="children">
				<% if (context.contains(node)) { %>
					<% boolean isFirst = true; %>
					<ul>
						<logic:iterate id="childNode" name="node" property="orderedChildren" type="myorg.domain.contents.Node">
							<logic:equal name="childNode" property="accessible" value="true">
							<logic:equal name="node" property="manager" value="false">
								<% if (isFirst) {
							    	isFirst = false;
								} else { %>
									<span class="bar">|</span>
								<% } %>
								<li class="navsublist">
									<% if (childNode.getUrl().indexOf(':') > 0) { %>
										<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(childNode.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link href="<%= childNode.getUrl() %>">
											<span><bean:write name="childNode" property="link" /></span>
										</html:link>
									<% } else { %>
										<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(childNode.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link page="<%= childNode.getUrl() %>">
											<span><bean:write name="childNode" property="link" /></span>
										</html:link>
									<% } %>
								</li>
							</logic:equal>
							</logic:equal>
						</logic:iterate>
					</ul>
				<% } %>
			</logic:notEmpty>
		</logic:equal>
		</logic:equal>
	</logic:iterate>
</logic:notEmpty>
