<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message key="label.configuration.title" bundle="MYORG_RESOURCES" />
</h2>

<ul>
	<li>
			<!-- HAS_CONTEXT --><html:link page="/configuration.do?method=manageSystemGroups">
				<bean:message bundle="MYORG_RESOURCES" key="label.configuration.manage.system.groups"/>
			</html:link>
	</li>
	<li>
			<!-- HAS_CONTEXT --><html:link page="/home.do?method=addContent">
				<bean:message bundle="MYORG_RESOURCES" key="label.configuration.manage.menus.features"/>
			</html:link>
	</li>
	<li>
		<!-- HAS_CONTEXT --><html:link page="/scheduler.do?method=viewScheduler">
			<bean:message bundle="MYORG_RESOURCES" key="label.configuration.tasks.scheduleing"/>
		</html:link>
	</li>
	<li>
		<!-- HAS_CONTEXT --><html:link page="/configuration.do?method=prepareAddVirtualHost">
			<bean:message bundle="MYORG_RESOURCES" key="label.configuration.virtualHost.add"/>
		</html:link>
	</li>
</ul>

<fr:view name="myOrg" property="virtualHosts" schema="virtualHost.application.configuration.summary">
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2" />
		<fr:property name="link(edit)" value="/configuration.do?method=basicApplicationConfiguration" />
		<fr:property name="key(edit)" value="label.edit" />
		<fr:property name="param(edit)" value="OID/virtualHostId" />
		<fr:property name="bundle(edit)" value="MYORG_RESOURCES" />
	</fr:layout>
</fr:view>
