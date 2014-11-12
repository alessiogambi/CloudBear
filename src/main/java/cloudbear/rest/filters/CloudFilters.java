package cloudbear.rest.filters;

import java.util.HashSet;
import java.util.Set;

import ch.usi.star.bear.annotations.BearFilter;
import ch.usi.star.bear.model.Event;
import ch.usi.star.bear.model.Feature;
import ch.usi.star.bear.model.Label;

public class CloudFilters {

	// This creates a huge amount of states because we consider each single
	// instance !
	// @BearFilter()
	public static Set<Label> serverID(Event event) {
		Set<Label> result = new HashSet<Label>();

		for (Feature f : event.getFeatures()) {
			if ("server-id".equalsIgnoreCase(f.getName())) {
				result.add(new Label("Server_" + f.getValue()));
			}
		}
		return result;
	}

	// @BearFilter()
	public static Set<Label> tenantID(Event event) {
		Set<Label> result = new HashSet<Label>();

		for (Feature f : event.getFeatures()) {
			if ("tenant-id".equalsIgnoreCase(f.getName())) {
				result.add(new Label("Tenant_" + f.getValue()));
			}
		}
		return result;
	}

}
