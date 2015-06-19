package org.fenixedu.bennu.io.domain;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;

public final class GroupBasedFile extends GroupBasedFile_Base {
    public GroupBasedFile(String displayName, String filename, byte[] content, Group accessGroup) {
        super();
        init(displayName, filename, content);
        setAccessGroup(accessGroup);
    }

    public Group getAccessGroup() {
        return getGroup().toGroup();
    }

    public void setAccessGroup(Group accessGroup) {
        setGroup(accessGroup.toPersistentGroup());
    }

    @Override
    public boolean isAccessible(User user) {
        return getGroup().isMember(user);
    }

    @Override
    public void delete() {
        setGroup(null);
        super.delete();
    }
}
