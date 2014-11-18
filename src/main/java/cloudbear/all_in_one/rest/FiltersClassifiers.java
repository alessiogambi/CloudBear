package cloudbear.all_in_one.rest;

import java.util.HashSet;
import java.util.Set;

import ch.usi.star.bear.annotations.BearClassifier;
import ch.usi.star.bear.annotations.BearFilter;
import ch.usi.star.bear.model.Event;
import ch.usi.star.bear.model.Feature;
import ch.usi.star.bear.model.Label;

public class FiltersClassifiers {

	public final static String USER_AND_TENANT = "userAndTenant";

	// String user_and_tenant_IDs = userID + "::" + tenantID;
	/**
	 * Classifiers generano per un evento tutte le user class a cui appartiene
	 * 
	 * @param event
	 * @return
	 */
	// @BearClassifier(name = "user")
	public static String userId(Event event) {
		return event.getUserId().split("::")[0];
	}

	@BearClassifier(name = USER_AND_TENANT)
	public static String userAndTenantId(Event event) {
		// String user_and_tenant_IDs = userID + "::" + tenantID;
		return event.getUserId();
	}

	// @BearClassifier(name = "tenant")
	public static String tenantId(Event event) {
		// String user_and_tenant_IDs = userID + "::" + tenantID;
		return event.getUserId().split("::")[1];
	}

	/**
	 * Filters create a Set of Labels given an event. All together the labels
	 * will identify the STATES of the final model
	 * 
	 * @param event
	 * @return
	 */
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

	@BearFilter()
	public static Set<Label> httpErrors(Event event) {
		Set<Label> result = new HashSet<Label>();

		for (Feature f : event.getFeatures()) {
			if ("response_status_code".equalsIgnoreCase(f.getName())) {

				// if (f.getValue().startsWith("2")) {
				// 10.2 Successful 2xx
				// result.add(new Label("Success"));
				// } else
				if (f.getValue().startsWith("4")) {
					// 10.4 Client Error 4xx
					result.add(new Label("Client_Error"));
				} else if (f.getValue().startsWith("5")) {
					// 10.5 Server Error 5xx
					result.add(new Label("Server_Error"));
				}
				// else {
				// result.add(new Label("Unknown"));
				// }

			}
		}

		return result;
	}

	// @BearFilter()
	public static Set<Label> httpOk(Event event) {
		Set<Label> result = new HashSet<Label>();

		for (Feature f : event.getFeatures()) {
			if ("response_status_code".equalsIgnoreCase(f.getName())) {

				if (f.getValue().startsWith("2")) {
					// 10.2 Successful 2xx
					result.add(new Label("Success"));
				}
			}
		}

		return result;
	}

	@BearFilter()
	public static Set<Label> httpOperation(Event event) {
		Set<Label> result = new HashSet<Label>();

		for (Feature f : event.getFeatures()) {
			if ("request_type".equalsIgnoreCase(f.getName())) {
				result.add(new Label(f.getValue()));
			}
		}

		return result;
	}

	@BearFilter()
	public static Set<Label> generateEventID(Event event) {
		Set<Label> result = new HashSet<Label>();
		// At least one label :
		result.add(new Label(event.getId()));
		//
		return result;
	}
}
