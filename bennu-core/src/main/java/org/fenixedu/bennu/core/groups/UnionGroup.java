/*
 * UnionGroup.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.groups;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentUnionGroup;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

import com.google.common.collect.ImmutableSet;

/**
 * Union composition group.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
final class UnionGroup extends Group {
    private static final long serialVersionUID = -4752173215899798915L;

    private final ImmutableSet<Group> children;

    UnionGroup(ImmutableSet<Group> children) {
        super();
        this.children = children;
    }

    @Override
    public String getPresentationName() {
        String or = " " + BundleUtil.getString("resources.BennuResources", "label.bennu.or") + " ";
        return children.stream().map(g -> g.getPresentationName()).collect(Collectors.joining(or));
    }

    @Override
    public String getExpression() {
        return children.stream().map(g -> g.getExpression()).collect(Collectors.joining(" | "));
    }

    @Override
    public PersistentGroup toPersistentGroup() {
        return PersistentUnionGroup.getInstance(children.stream().map(g -> g.toPersistentGroup()).collect(Collectors.toSet()));
    }

    @Override
    public Set<User> getMembers() {
        final Set<User> users = new HashSet<>();
        for (final Group group : children) {
            users.addAll(group.getMembers());
        }
        return users;
    }

    @Override
    public boolean isMember(final User user) {
        for (final Group group : children) {
            if (group.isMember(user)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        final Set<User> users = new HashSet<>();
        for (final Group group : children) {
            users.addAll(group.getMembers(when));
        }
        return users;
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        for (final Group group : children) {
            if (group.isMember(user, when)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Group or(Group group) {
        if (this.equals(group)) {
            return this;
        }
        if (group instanceof NobodyGroup) {
            return this;
        }
        if (group instanceof AnyoneGroup) {
            return Group.anyone();
        }
        if (group instanceof UnionGroup) {
            return new UnionGroup(ImmutableSet.<Group> builder().addAll(children).addAll(((UnionGroup) group).children).build());
        }
        return new UnionGroup(ImmutableSet.<Group> builder().addAll(children).add(group).build());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof UnionGroup) {
            return children.equals(((UnionGroup) object).children);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return children.hashCode();
    }
}
