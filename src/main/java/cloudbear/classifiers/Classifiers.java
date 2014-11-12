package cloudbear.classifiers;

import ch.usi.star.bear.annotations.BearClassifier;
import ch.usi.star.bear.model.Event;

public class Classifiers {

	/**
	 * Classifiers generano per un evento tutte le user class a cui appartiene
	 * 
	 * @param event
	 * @return
	 */
	@BearClassifier(name = "status")
	public static String userId(Event event) {
		return event.getUserId();
	}
}