package cloudbear.rest.filters;

import java.util.HashSet;
import java.util.Set;

import ch.usi.star.bear.annotations.BearFilter;
import ch.usi.star.bear.model.Event;
import ch.usi.star.bear.model.Feature;
import ch.usi.star.bear.model.Label;

public class HTTPFilters {

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
}
