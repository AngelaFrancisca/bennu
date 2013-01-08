package pt.ist.bennu.core.domain.groups.immutable;

import java.util.Collections;
import java.util.Set;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

public class AnonymousGroup extends AnonymousGroup_Base {
	protected AnonymousGroup() {
		super();
	}

	@Override
	public Set<User> getMembers() {
		return Collections.emptySet();
	}

	@Override
	public boolean isMember(final User user) {
		return user == null;
	}

	@Service
	public static AnonymousGroup getInstance() {
		final AnonymousGroup group = getSystemGroup(AnonymousGroup.class);
		return group == null ? new AnonymousGroup() : group;
	}
}
