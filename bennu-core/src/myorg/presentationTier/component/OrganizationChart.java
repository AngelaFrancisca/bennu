package myorg.presentationTier.component;

import java.util.ArrayList;
import java.util.Collection;

public class OrganizationChart<T> extends ArrayList<OrganizationChartRow<T>> {

    public OrganizationChart(final T element, final Collection<T> parents, final Collection<T> children) {
	addAll(parents);
	add(new OrganizationChartRow<T>(element));
	addAll(children);
    }

    public void addAll(final Collection<T> elements) {
	if (elements != null) {
	    OrganizationChartRow<T> row = null;
	    for (final T t : elements) {
		if (row == null || row.isFull()) {
		    row = new OrganizationChartRow<T>(t);
		    add(row);
		} else {
		    row.add(t);
		}	    
	    }
	}
    }

}
