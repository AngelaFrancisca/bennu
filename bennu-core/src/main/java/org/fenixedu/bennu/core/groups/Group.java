package org.fenixedu.bennu.core.groups;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * <p>
 * Groups represent access groups. They are immutable objects, all operations return a new group with the result.
 * </p>
 * 
 * <p>
 * Every group has a persistent counter part, to be used when domain relations are needed to persist the group information. Groups
 * can be converted back and forth to {@link PersistentGroup}s using {@link #toPersistentGroup()} and
 * {@link PersistentGroup#toGroup()}.
 * </p>
 * 
 * <p>
 * Groups can be translated to and from a DSL of groups, using methods {@link #getExpression()} and {@link #parse(String)},
 * respectively. The language supports compositions ({@code |}), intersections ({@code &}), negations ({@code !}) and differences
 * ({@code -}) over basic constructs, that can either be functions or the special link group: {@code #name} ({@link DynamicGroup}
 * ). Functions have the general form: {@code id(argName=argValue, argName=argValue,...)} but for groups without arguments they
 * loose the parenthesis completely, also the argNames can be skipped if a default argument is set. Common basic constructs are:
 * {@code anonymous} ( {@link AnonymousGroup}), {@code logged} ({@link LoggedGroup}), {@code anyone} ({@link AnyoneGroup}),
 * {@code nobody} ( {@link NobodyGroup}), {@code U(istxxx, istxxxx,...)} ({@link UserGroup}).
 * </p>
 * 
 * <p>
 * Sub-classes should be annotated with {@link GroupOperator} for proper language support, and should extends {@link CustomGroup}.
 * </p>
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see PersistentGroup
 * @see GroupOperator
 */
public abstract class Group implements Serializable, Comparable<Group> {
    private static final long serialVersionUID = 1177210800165802668L;

    private static final Group ANONYMOUS = new AnonymousGroup();
    private static final Group ANYONE = new AnyoneGroup();
    private static final Group LOGGED = new LoggedGroup();
    private static final Group NOBODY = new NobodyGroup();

    public static final Group anonymous() {
        return ANONYMOUS;
    }

    public static final Group anyone() {
        return ANYONE;
    }

    public static final Group logged() {
        return LOGGED;
    }

    public static final Group nobody() {
        return NOBODY;
    }

    public static DynamicGroup managers() {
        return DynamicGroup.MANAGERS;
    }

    public static Group users(User... members) {
        return users(Arrays.stream(members));
    }

    public static Group users(Stream<User> members) {
        Set<User> set = members.collect(Collectors.toSet());
        if (set.size() == 0) {
            return NOBODY;
        }
        return new UserGroup(Collections.unmodifiableSet(set));
    }

    public static DynamicGroup dynamic(String name) {
        return new DynamicGroup(name);
    }

    /**
     * Human readable, internationalized textual representation of this group.
     * 
     * @return internationalized name of the group.
     */
    public abstract String getPresentationName();

    /**
     * Textual representation of this group in the group language.
     * 
     * @return this group in group language.
     */
    public abstract String getExpression();

    /**
     * Obtains (creating if necessary) the corresponding group domain entity.
     * 
     * @return An instance of {@link PersistentGroup}.
     */
    public abstract PersistentGroup toPersistentGroup();

    /**
     * Computes the full member list of this group. Potentially processor consuming operation, preferably developers should orient
     * code to {@link #isMember(User)} or {@link #isMember(User, DateTime)} methods.
     * 
     * @return all member users in the system at the exact moment of the invocation
     */
    public abstract Stream<User> getMembers();

    /**
     * Same as {@link #getMembers()} but at a given moment in time. This is like a time-machine for the groups domain.
     * 
     * @param when
     *            moment when to fetch the user list.
     * @return all member users in the system at the requested moment
     */
    public abstract Stream<User> getMembers(DateTime when);

    /**
     * Tests if the given user is a member of the group.
     * 
     * @param user
     *            the user to test, can be null
     * @return <code>true</code> if member, <code>false</code> otherwise
     * 
     * @see #verify()
     */
    public abstract boolean isMember(User user);

    /**
     * Same as {@link #isMember(User)} but at a given moment in time. This is like a time-machine for the groups domain.
     * 
     * @param user
     *            the user to test, can be null
     * @param when
     *            moment when to test the user.
     * @return <code>true</code> if member, <code>false</code> otherwise
     */
    public abstract boolean isMember(User user, DateTime when);

    /**
     * Tests if the given user is a member of the group, throwing an exception if not.
     * 
     * @throws AuthorizationException
     *             if user is not a member of the group.
     */
    public void verify() throws AuthorizationException {
        if (!isMember(Authenticate.getUser())) {
            throw AuthorizationException.unauthorized();
        }
    }

    /**
     * Intersect with given group. Returns the resulting group without changing {@code this} or the argument.
     * 
     * @param group group to intersect with
     * @return group resulting of the intersection between '{@code this}' and '{@code group}'
     */
    public Group and(Group group) {
        if (this.equals(group)) {
            return this;
        }
        if (group instanceof NobodyGroup) {
            return group;
        }
        if (group instanceof AnyoneGroup) {
            return this;
        }
        if (group instanceof LoggedGroup && !this.isMember(null)) {
            return this;
        }
        return new IntersectionGroup(ImmutableSet.<Group> builder().add(this).add(group).build());
    }

    /**
     * Unite with given group. Returns the resulting group without changing {@code this} or the argument.
     * 
     * @param group
     *            group to unite with
     * @return group resulting of the union between '{@code this}' and '{@code group}'
     */
    public Group or(Group group) {
        if (this.equals(group)) {
            return this;
        }
        if (group instanceof NobodyGroup) {
            return this;
        }
        if (group instanceof AnyoneGroup) {
            return group;
        }
        if (group instanceof LoggedGroup && !this.isMember(null)) {
            return group;
        }
        return new UnionGroup(ImmutableSet.<Group> builder().add(this).add(group).build());
    }

    /**
     * Subtract with given group. Returns the resulting group without changing {@code this} or the argument.
     * 
     * @param group
     *            group to subtract with
     * @return group resulting of all members of '{@code this}' except members of '{@code group}'
     */
    public Group minus(Group group) {
        if (this.equals(group)) {
            return NOBODY;
        }
        if (group instanceof NobodyGroup) {
            return this;
        }
        if (group instanceof AnyoneGroup) {
            return NOBODY;
        }
        return new DifferenceGroup(this, ImmutableSet.of(group));
    }

    /**
     * Negate the group. Returns the resulting group without changing {@code this}.
     * 
     * @return inverse group
     */
    public Group not() {
        return new NegationGroup(this);
    }

    /**
     * Grants access to the given user. Returns the resulting group without changing {@code this}.
     * 
     * @param user
     *            user to grant access to
     * @return group resulting of the union between '{@code this}' and the group of the given user
     */
    public Group grant(User user) {
        return or(user.groupOf());
    }

    /**
     * Revokes access to the given user. Returns the resulting group without changing {@code this}.
     * 
     * @param user
     *            user to revoke access from
     * @return group resulting of the difference between '{@code this}' and the group of the given user
     */
    public Group revoke(User user) {
        return minus(user.groupOf());
    }

    /**
     * Parse group from the group language expression.
     * 
     * @param expression
     *            the group in textual form
     * @return group representing the semantics of the expression.
     * @throws BennuCoreDomainException
     *             if a parsing error occurs
     */
    public static Group parse(String expression) {
        if (Strings.isNullOrEmpty(expression)) {
            return NOBODY;
        }
        return GroupParser.parse(expression);
    }

    @Override
    public String toString() {
        return getPresentationName();
    }

    @Override
    public abstract boolean equals(Object object);

    @Override
    public abstract int hashCode();

    @Override
    public int compareTo(Group other) {
        int byname = getPresentationName().compareTo(other.getPresentationName());
        if (byname != 0) {
            return byname;
        }
        return getExpression().compareTo(getExpression());
    }

    static String compositeExpression(Group group) {
        if (group instanceof UnionGroup || group instanceof IntersectionGroup || group instanceof DifferenceGroup || group
                instanceof NegationGroup) {
            return "(" + group.getExpression() + ")";
        } else {
            return group.getExpression();
        }
    }
}
