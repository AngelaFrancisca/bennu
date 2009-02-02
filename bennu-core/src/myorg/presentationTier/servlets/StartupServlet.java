/*
 * @(#)StartupServlet.java
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

package myorg.presentationTier.servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import myorg._development.PropertiesManager;
import myorg.applicationTier.Authenticate;
import myorg.domain.MyOrg;
import myorg.domain.RoleType;
import myorg.domain.groups.AnyoneGroup;
import myorg.domain.groups.UserGroup;
import myorg.domain.scheduler.Scheduler;
import myorg.domain.scheduler.Task;
import pt.ist.fenixWebFramework.FenixWebFramework;

public class StartupServlet extends HttpServlet {

    private static final long serialVersionUID = -7035892286820898843L;

    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	final String domainmodelPath = getServletContext().getRealPath(getInitParameter("domainmodelPath"));
	final File dir = new File(domainmodelPath);
	final List<String> urls = new ArrayList<String>();
	for (final File file : dir.listFiles()) {
	    if (file.isFile() && file.getName().endsWith(".dml")) {
		try {
		    urls.add(file.getCanonicalPath());
		} catch (IOException e) {
		    e.printStackTrace();
		    throw new ServletException(e);
		}
	    }
	}
	Collections.sort(urls);
	final String[] paths = new String[urls.size()];
	for (int i = 0; i < urls.size(); i++) {
	    paths[i] = urls.get(i);
	}
	try {
	    FenixWebFramework.initialize(PropertiesManager.getFenixFrameworkConfig(paths));
	} catch (Throwable t) {
	    t.printStackTrace();
	    throw new Error(t);
	}
	MyOrg.initialize(FenixWebFramework.getConfig());

	final String managerUsernames = PropertiesManager.getProperty("manager.usernames");
	Authenticate.initRole(RoleType.MANAGER, managerUsernames);

	initializePersistentGroups();

	Scheduler.initialize();
    }

    private void initializePersistentGroups() {
	UserGroup.getInstance();
	AnyoneGroup.getInstance();
    }

}
