/*
 * @(#)MyOrg.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
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

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import myorg.util.DomainModelUtil;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.pstm.IllegalWriteException;
import dml.DomainClass;
import dml.Role;

public class MyOrg extends MyOrg_Base {

    public static MyOrg getInstance() {
	return FenixFramework.getRoot();
    }

    public MyOrg() {
	super();
	checkIfIsSingleton();
	new VirtualHost(this);
    }

    private void checkIfIsSingleton() {
	if (FenixFramework.getRoot() != null && FenixFramework.getRoot() != this) {
	    throw new Error("There can only be one! (instance of MyOrg)");
	}
    }

    @Service
    public static void initModules() throws SecurityException, IllegalAccessException, InvocationTargetException,
	    NoSuchMethodException, ClassNotFoundException {

	System.out.println("=== Modules Init System ===");
	Set<DomainClass> inits = getModuleRootsFor(MyOrg.class);

	MyOrg myorg = MyOrg.getInstance();

	for (DomainClass classToInit : inits) {
	    Class<ModuleInitializer> clazz = (Class<ModuleInitializer>) Class.forName(classToInit.getFullName());
	    ModuleInitializer root = null;
	    try {
		root = (ModuleInitializer) clazz.getMethod("getInstance", new Class[] {}).invoke(clazz, new Object[] {});
	    } catch (InvocationTargetException e) {
		if (e.getCause() instanceof IllegalWriteException) {
		    System.out.println("IllegalWrite restarting!");
		    throw new IllegalWriteException();
		} else {
		    throw e;
		}
	    }
	    System.out.print("Initiating " + root.getClass().getName() + "..");
	    root.init(myorg);
	    System.out.println("Done");
	}

	System.out.println("=== Finished Modules Init System ===");
    }

    private static Set<DomainClass> getModuleRootsFor(Class<?> clazz) throws ClassNotFoundException {
	Set<DomainClass> initializers = new TreeSet<DomainClass>(new Comparator<DomainClass>() {

	    @Override
	    public int compare(DomainClass d1, DomainClass d2) {
		return d1.getSourceFile().getFile().compareTo(d2.getSourceFile().getFile());
	    }

	});
	DomainClass myOrgClass = DomainModelUtil.getDomainClassFor(MyOrg.class);
	for (Role role : myOrgClass.getRoleSlotsList()) {
	    if (role.getMultiplicityLower() == 0 && role.getMultiplicityUpper() == 1) {
		DomainClass otherClass = (DomainClass) role.getType();
		if (implementsModuleRoot(otherClass)) {
		    initializers.add(otherClass);
		}
	    }

	}

	return initializers;
    }

    private static boolean implementsModuleRoot(DomainClass otherClass) throws ClassNotFoundException {
	Class<?> clazz = Class.forName(otherClass.getFullName());
	return ModuleInitializer.class.isAssignableFrom(clazz);
    }

}
