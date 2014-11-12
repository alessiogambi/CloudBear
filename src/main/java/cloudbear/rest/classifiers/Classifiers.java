package cloudbear.rest.classifiers;

import ch.usi.star.bear.annotations.BearClassifier;
import ch.usi.star.bear.model.Event;

public class Classifiers {

	// String user_and_tenant_IDs = userID + "::" + tenantID;
	/**
	 * Classifiers generano per un evento tutte le user class a cui appartiene
	 * 
	 * @param event
	 * @return
	 */
	@BearClassifier(name = "user")
	public static String userId(Event event) {
		return event.getUserId().split("::")[0];
	}

	@BearClassifier(name = "userAndTenant")
	public static String userAndTenantId(Event event) {
		// String user_and_tenant_IDs = userID + "::" + tenantID;
		return event.getUserId();
	}

	@BearClassifier(name = "tenant")
	public static String tenantId(Event event) {
		// String user_and_tenant_IDs = userID + "::" + tenantID;
		return event.getUserId().split("::")[1];
	}

}