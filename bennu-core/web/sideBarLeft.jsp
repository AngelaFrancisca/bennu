<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
<bean:define id="menuElements" name="context" property="menuElements"/>
<bean:define id="prefixPath" name="context" property="prefixPath"/>

<logic:present name="USER_SESSION_ATTRIBUTE">
	<div id="navops">
		<!-- HAS_CONTEXT --><html:link page="/home.do?method=addContent">
			<bean:message bundle="MYORG_RESOURCES" key="label.content.page.new"/>
		</html:link>
	</div>
</logic:present>
<div id="navlist">
<%
	final Object o = request.getAttribute("reorderPages");
	final boolean reorder = o != null && ((Boolean) o).booleanValue();
%>
<ul <% if (reorder) {%> id="dragableElementsParentBox" <% } %>>
	<logic:empty name="menuElements">
		<li><!-- NO_CHECKSUM --><a href="home.do?method=firstPage"><bean:message bundle="MYORG_RESOURCES" key="label.application.home"/></a></li>
	</logic:empty>
	<logic:notEmpty name="menuElements">

		<logic:iterate id="node" name="menuElements" indexId="nindex">
			<bean:define id="articleId">articleNode<%= nindex %></bean:define>
			<li>
				<bean:define id="url">/content.do?method=viewPage&amp;_CONTEXT_PATH_=<bean:write name="prefixPath"/><bean:write name="node" property="OID"/></bean:define>
				<div <% if (reorder) {%>dragableBox="true"<% } %> id="<%= articleId %>">
					<% if (!reorder) { %>
						<!-- HAS_CONTEXT --><html:link page="<%= url %>">
							<bean:write name="node" property="link"/>
						</html:link>
					<% } else { %>
						<div class="navigationContentMove">
							<bean:write name="node" property="link"/>
						</div>
					<% } %>
				</div>
			</li>
			<logic:notEmpty name="node" property="children">
				<% if (!reorder && context.getSelectedNode() == node) { %>
					<logic:iterate id="childNode" name="node" property="orderedChildren">
						<li class="navsublist">
							<bean:define id="urlChild"><bean:write name="url" filter="false"/>,<bean:write name="childNode" property="OID"/></bean:define>
							<!-- HAS_CONTEXT --><html:link page="<%= urlChild %>">
								<bean:write name="childNode" property="link"/>
							</html:link>
						</li>
					</logic:iterate>
				<% } %>
			</logic:notEmpty>
		</logic:iterate>
	</logic:notEmpty>
	<% if (reorder) {%>
		<li>
				<br/>
				<div class="clear" id="clear"></div>
				<ul>
				<li>
				<form action="<%= request.getContextPath() %>/content.do" method="post">
					<input type="hidden" name="method" value="savePageOrders"/>
					<input type="hidden" id="articleOrders" name="articleOrders"/>
					<bean:define id="originalArticleIds"><logic:iterate id="node" name="menuElements" indexId="nindex"><% if (nindex > 0) {%>;<% } %><bean:write name="node" property="OID"/></logic:iterate></bean:define>
					<input type="hidden" name="originalArticleIds" value="<%= originalArticleIds %>"/>
					<bean:define id="buttonLabel"><bean:message bundle="MYORG_RESOURCES" key="label.content.page.order.save"/></bean:define>
					<input type="submit" value="<%= buttonLabel %>" onclick="saveArticleOrders();"/>
				</form>
				</li>
				</ul>
				<div id="insertionMarker">
					<img src="<%= request.getContextPath() %>/CSS/marker_top.gif" alt=""/>
					<img src="<%= request.getContextPath() %>/CSS/marker_middle.gif" id="insertionMarkerLine" alt=""/>
					<img src="<%= request.getContextPath() %>/CSS/marker_bottom.gif" alt=""/>
				</div>
		</li>
	<% } %>
<!-- 
	<li>
		<p>
			<strong>A tiny little service announcement.</strong>
			<br/>Put all your little tidbits of information or pictures in this small yet useful little area.
		</p>
	</li>
 -->
</ul>
<logic:present name="USER_SESSION_ATTRIBUTE">
	<ul>
		<li>
			<html:link action="/configuration.do?method=applicationConfiguration"><bean:message key="label.configuration.link" bundle="MYORG_RESOURCES"/></html:link>
		</li>
		<li>
			<html:link action="/organization.do?method=showOptions"><bean:message key="label.organization" bundle="ORGANIZATION_RESOURCES"/></html:link>
		</li>
	</ul>
</logic:present>
</div>