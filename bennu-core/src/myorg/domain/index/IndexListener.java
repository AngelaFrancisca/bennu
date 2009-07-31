package myorg.domain.index;

import java.util.HashSet;
import java.util.Set;

import myorg.domain.index.interfaces.Indexable;
import myorg.domain.index.interfaces.Searchable;
import myorg.util.lucene.DomainIndexer;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.pstm.CommitListener;
import pt.ist.fenixframework.pstm.TopLevelTransaction;

public class IndexListener implements CommitListener {

    @Override
    public void afterCommit(TopLevelTransaction topLevelTransaction) {
	
    }

    @Override
    public void beforeCommit(TopLevelTransaction topLevelTransaction) {
	if (topLevelTransaction.isWriteTransaction()) {
	    Set<Indexable> indexableObjects = new HashSet<Indexable>();

	    for (DomainObject domainObject : new HashSet<DomainObject>(topLevelTransaction.getNewObjects())) {
		if (domainObject instanceof Searchable) {
		    indexableObjects.addAll(((Searchable) domainObject).getObjectsToIndex());
		}
	    }

	    for (DomainObject domainObject : topLevelTransaction.getModifiedObjects()) {
		if (domainObject instanceof Searchable) {
		    indexableObjects.addAll(((Searchable) domainObject).getObjectsToIndex());
		}
	    }

	    for (Indexable indexableObject : indexableObjects) {
		DomainIndexer.getInstance().indexDomainObject(indexableObject.getDocumentToIndex());
	    }
	}
    }

}
