package cloudbear.filters.all;

import java.util.HashSet;
import java.util.Set;

import ch.usi.star.bear.annotations.BearFilter;
import ch.usi.star.bear.model.Event;
import ch.usi.star.bear.model.Feature;
import ch.usi.star.bear.model.Label;

public class Filters {

	@BearFilter()
	public static Set<Label> FilterUno(Event event) {
		Set<Label> result = new HashSet<Label>();

		for (Feature f : event.getFeatures()) {
			// System.out.println(f.getValue());
			if (f.getValue().contains(":"))
				result.add(new Label(f.getName()));
			else
				result.add(new Label(f.getName() + "" + f.getValue()));
		}

		Label label = new Label(event.getId());
		result.add(label);
		return result;
	}
}
