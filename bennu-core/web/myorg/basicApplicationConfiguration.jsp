<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.basic"/>
</h2>
<fr:edit id="myOrg" name="myOrg" schema="myOrg.application.configuration"
		action="/configuration?method=applicationConfiguration">
	<fr:layout name="tabular">
		<fr:property name="classes" value="form thwidth150px"/>
		<fr:property name="columnClasses" value=",,tderror"/>
	</fr:layout>
</fr:edit>

<html:link page="/configuration.do?method=manageSystemGroups">
	<bean:message bundle="MYORG_RESOURCES" key="label.configuration.manage.system.groups"/>
</html:link>