package cloudbear.filters.empty;

import java.util.HashSet;
import java.util.Set;

import ch.usi.star.bear.annotations.BearFilter;
import ch.usi.star.bear.model.Event;
import ch.usi.star.bear.model.Label;

public class Filters {

	@BearFilter()
	public static Set<Label> generateNoLabels(Event event) {
		Set<Label> result = new HashSet<Label>();
		// At least one label :
		result.add(new Label(event.getId()));
		//
		return result;
	}
}
