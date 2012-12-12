<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.application.users"/>
</h2>

<fr:edit id="searchUsers"
		name="searchUsers"
		type="pt.ist.bennu.core.domain.SearchUsers"
		schema="application.searchUsers"
		action="manageUsers.do?method=searchUsers">
	<fr:layout name="tabular">
		<fr:property name="classes" value="form"/>
		<fr:property name="columnClasses" value=",,tderror"/>
	</fr:layout>
</fr:edit>

<logic:present role="pt.ist.bennu.core.domain.RoleType.MANAGER,pt.ist.bennu.core.domain.RoleType.USER_MANAGER">
	<br/>
	<html:link action="manageUsers.do?method=prepareCreateNewUser">
		<bean:message bundle="MYORG_RESOURCES" key="label.application.users.create.new"/>
	</html:link>
</logic:present>

<logic:present name="searchUsers" property="user">
	<br/>
	<br/>
	<h3>
		<bean:message bundle="MYORG_RESOURCES" key="label.user"/>
		:
		<bean:write name="searchUsers" property="user.username"/>
	</h3>
	<bean:define id="username" name="searchUsers" property="user.username"/>
	<p>
		<logic:present user="<%= username.toString() %>">
			<html:link action="manageUsers.do?method=changePassword" paramId="userId" paramName="searchUsers" paramProperty="user.externalId">
				<bean:message bundle="MYORG_RESOURCES" key="label.application.users.change.password"/>
			</html:link>
		</logic:present>
		<logic:present role="pt.ist.bennu.core.domain.RoleType.MANAGER,pt.ist.bennu.core.domain.RoleType.USER_MANAGER">
			<logic:present user="<%= username.toString() %>">
				|
			</logic:present>
			<html:link action="manageUsers.do?method=editUser" paramId="userId" paramName="searchUsers" paramProperty="user.externalId">
				<bean:message bundle="MYORG_RESOURCES" key="label.application.users.edit"/>
			</html:link>
			|
			<html:link action="manageUsers.do?method=generatePassword" paramId="userId" paramName="searchUsers" paramProperty="user.externalId">
				<bean:message bundle="MYORG_RESOURCES" key="label.application.users.generate.password"/>
			</html:link>
			<logic:present name="password">
				<br/>
				<br/>
				<font size="4" color="red">
					<bean:write name="password"/>
				</font>
			</logic:present>
		</logic:present>
	</p>
	<h4>
		<bean:message bundle="MYORG_RESOURCES" key="label.user.lastLogoutDateTime"/>
	</h4>
	<p>
		<bean:write name="searchUsers" property="user.lastLogoutDateTime"/>
	</p>
	<h4>
		<bean:message bundle="MYORG_RESOURCES" key="label.user.groups"/>
	</h4>
	<p>
		<logic:present role="pt.ist.bennu.core.domain.RoleType.MANAGER,pt.ist.bennu.core.domain.RoleType.USER_MANAGER">
			<html:link action="manageUsers.do?method=prepareAddGroup" paramId="userId" paramName="searchUsers" paramProperty="user.externalId">
				<bean:message bundle="MYORG_RESOURCES" key="label.application.users.group.add"/>
			</html:link>
			<br/>
		</logic:present>
		<logic:iterate id="peopleGroup" name="searchUsers" property="user.peopleGroups">
			<bean:write name="peopleGroup" property="name"/>
			<bean:define id="url">manageUsers.do?method=removeGroup&amp;peopleId=<bean:write name="peopleGroup" property="externalId"/></bean:define>
			<html:link action="<%= url %>" paramId="userId" paramName="searchUsers" paramProperty="user.externalId">
				<bean:message bundle="MYORG_RESOURCES" key="label.application.users.group.remove"/>
			</html:link>			
			<br/>
		</logic:iterate>
	</p>
</logic:present>
