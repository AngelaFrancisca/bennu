package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.ManualGroupRegister;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.test.core.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class TestAnonymous {
    private static User user1;

    @BeforeClass
    public static void setupUsers() {
        ManualGroupRegister.ensure();
        FenixFramework.atomic(() -> {
            user1 = User.findByUsername("user1");
            if (user1 == null) {
                user1 = new User("user1", ManualGroupRegister.newProfile());
            }
        });
    }

    @Test
    public void parse() {
        String expr = "anonymous";
        assertEquals(Group.parse(expr).getExpression(), expr);
    }

    @Test
    public void membership() {
        assertTrue(Group.anonymous().getMembers().count() == 0);
        assertTrue(Group.anonymous().isMember(null));
        assertFalse(Group.anonymous().isMember(user1));
    }

    @Test
    public void createPersistent() {
        assertTrue(Group.anonymous().toPersistentGroup() != null);
    }
}
