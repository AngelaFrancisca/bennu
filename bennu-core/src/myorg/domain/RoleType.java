/*
 * @(#)RoleType.java
 *
 * Copyright 2009 Instituto Superior Tecnico, João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.domain;

import myorg.domain.groups.IRoleEnum;
import myorg.util.BundleUtil;

public enum RoleType implements IRoleEnum {

    MANAGER;

    public String getPresentationName() {
	return BundleUtil.getStringFromResourceBundle("resources/MyorgResources", "label.persistent.group.roleType.name."
		+ name());
    }

    public String getRepresentation() {
	return getClass().getName() + "." + name();
    }
}
