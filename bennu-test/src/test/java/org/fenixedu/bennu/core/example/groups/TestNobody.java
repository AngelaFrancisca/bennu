package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.ManualGroupRegister;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class TestNobody {
    private static User user1;

    @BeforeClass
    @Atomic(mode = TxMode.WRITE)
    public static void setupUsers() {
        ManualGroupRegister.ensure();
        user1 = User.findByUsername("user1");
        if (user1 == null) {
            user1 = new User("user1", ManualGroupRegister.newProfile());
        }
    }

    @Test
    public void parse() {
        String expr = "nobody";
        assertEquals(Group.parse(expr).getExpression(), expr);
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void membership() {
        assertTrue(Group.nobody().getMembers().count() == 0);
        assertFalse(Group.nobody().isMember(user1));
        assertFalse(Group.nobody().isMember(null));
    }

    @Test
    @Atomic(mode = TxMode.WRITE)
    public void createPersistent() {
        assertTrue(Group.nobody().toPersistentGroup() != null);
    }
}