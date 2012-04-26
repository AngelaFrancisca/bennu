package myorg.domain.groups;

public class IntersectionNamedGroup extends IntersectionNamedGroup_Base {
    
    public IntersectionNamedGroup(final String groupName, final PersistentGroup... persistentGroups) {
	super();
	for (final PersistentGroup persistentGroup : persistentGroups) {
	    addPersistentGroups(persistentGroup);
	}
	setGroupName(groupName);
    }
    
}
