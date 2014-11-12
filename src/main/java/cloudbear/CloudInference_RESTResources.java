package cloudbear;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.usi.star.bear.analysis.AnalysisEngine;
import ch.usi.star.bear.inference.InferenceEngine;
import ch.usi.star.bear.model.Event;
import ch.usi.star.bear.model.Feature;
import ch.usi.star.bear.model.Model;
import ch.usi.star.bear.properties.BearProperties;
import ch.usi.star.bear.visualization.BearVisualizer;

import com.sun.jersey.api.uri.UriTemplate;

public class CloudInference_RESTResources {

	public static void main(String[] args) throws Exception {

		assert RESOURCE_TEMPLATES.length == EVENT_ID.length;

		// BearProperties.getInstance().setProperty(BearProperties.FILTERSPACKAGEPREFIX,
		// "cloudbear.filters");

		// BearProperties.getInstance().setProperty(BearProperties.CLASSIFIERSPACKAGEPREFIX,
		// "cloudbear.classifiers");

		// Remove all the labels filters
		// BearProperties.getInstance().setProperty(BearProperties.FILTERSPACKAGEPREFIX,
		// "cloudbear.filters.empty");

		BearProperties.getInstance().setProperty(BearProperties.FILTERSPACKAGEPREFIX, "cloudbear.rest.filters");
		BearProperties.getInstance().setProperty(BearProperties.CLASSIFIERSPACKAGEPREFIX, "cloudbear.rest.classifiers");
		//
		BearProperties.getInstance().setProperty(BearProperties.EVENTDURATION, "false");

		// DEV:
		// File file = new File("res/small-nova-api.log");
		// TEST:
		File file = new File("res/nova-api.log");
		/*
		 * FINAL: SOME Log entries were malformed (the ones with error):
		 * 2014-10-01 01:00:01 4273 INFO nova.osapi_compute.wsgi.server [-]
		 * 128.130.172.244 - - [01/Oct/2014 01:00:01]
		 * "GET /v2/83d2db83cb7e4f61aafde0e1063cbc4b/servers/detail?status=error&all_tenants=1 HTTP/1.1"
		 * 401 461 0.049015
		 */
		// File file = new File("res/merged-nova-api-no-malformed.log");

		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;

		InferenceEngine inferenceEngine = new InferenceEngine();
		AnalysisEngine analisysEngine = new AnalysisEngine();

		// Ignored Events
		Set<String> ignoredEvents = new HashSet<String>();
		ignoredEvents.add("Os-floating-ips");
		ignoredEvents.add("Os-keypairs");
		ignoredEvents.add("Os-quota-sets-for-Tenant");
		ignoredEvents.add("Os-security-groups");
		ignoredEvents.add("Os-simple-tenant-usage-forTenant");

		while ((line = br.readLine()) != null) {
			if (!line.equals("") && line.contains("HTTP/")) {
				// Do the parsing
				Event event = parseLine(line);

				if (event != null && !ignoredEvents.contains(event.getId())) {
					inferenceEngine.signal(event);

				} else {
					System.out.println("Null event");
				}
			}
		}
		List<Model> models = inferenceEngine.exportModels();

		System.out.println("CloudInference_RESTResources.main() Generate " + models.size() + " models");

		br.close();
		fr.close();

		// UserID model
		Model userModel = analisysEngine.synthesize(models, "{user=\\.*}");

		BearVisualizer visualizer = new BearVisualizer(userModel);
		visualizer.display();

		// Model tenantModel = analisysEngine.synthesize(models,
		// "{tenant=\\.*}");
		// Model userAndTenantmodel = analisysEngine.synthesize(models,
		// "{userAndTenant=\\.*}");
		// visualizer = new BearVisualizer(tenantModel);
		// visualizer.display();

		// visualizer = new BearVisualizer(userAndTenantmodel);
		// visualizer.display();

		// System.out.println(userIDmodel.generatePrismModel("prova"));
		// String result = analisysEngine.analyze(model, pctl, "BEAR", false);
		// System.out.println(result);

	}

	// List only the one actually inside the logs !
	/*
	 * // TODO The resource MUST BE Declared Hierarchically !!!!!
	 */
	private final static UriTemplate[] RESOURCE_TEMPLATES = new UriTemplate[] { //
	//
			new UriTemplate("/v2/{tenant-id}/servers"),// GET/POST
			new UriTemplate("/v2/{tenant-id}/servers/detail"),// GET
			// new UriTemplate("/v2/{tenant-id}/servers/detail?{features}"), //
			// GET
			new UriTemplate("/v2/{tenant-id}/servers/{server-id}"),// GET,
			new UriTemplate("/v2/{tenant-id}/servers/{server-id}/os-security-groups"),//
			new UriTemplate("/v2/{tenant-id}/servers/{server-id}/os-volume_attachments"),//
			new UriTemplate("/v2/{tenant-id}/servers/{server-id}/action"), // POST
			new UriTemplate("/v2/{tenant-id}/images"), //
			new UriTemplate("/v2/{tenant-id}/images/detail"),//
			new UriTemplate("/v2/{tenant-id}/images/{image-id}"),//
			new UriTemplate("/v2/{tenant-id}/extensions"),//
			new UriTemplate("/v2/{tenant-id}/flavors"),//
			new UriTemplate("/v2/{tenant-id}/flavors/detail"),//
			new UriTemplate("/v2/{tenant-id}/flavors/{flavor-id}"),//
			//
			new UriTemplate("/v2/{tenant-id}/os-floating-ips"),//
			new UriTemplate("/v2/{tenant-id}/os-keypairs"),//
			new UriTemplate("/v2/{tenant-id}/os-quota-sets/{tenant-id}"),//
			new UriTemplate("/v2/{tenant-id}/os-security-groups"),//
			new UriTemplate("/v2/{tenant-id}/os-simple-tenant-usage/{target-tenant-id}?{features}"),//
			new UriTemplate("/v1/{tenant-id}/snapshots/detail"),//
			new UriTemplate("/v1/{tenant-id}/volumes/detail") };//

	private final static String[] EVENT_ID = new String[] { //
	//
			"Servers",//
			"Servers_Detail",//
			"Server",//
			"Server_os_security_groups",//
			"Server_os-volume_attachments",//
			"Server_Action",//
			"Images",//
			"Images_Detail",//
			"Image",//
			"Extensions",//
			"Flavors",//
			"Flavors_Detail",//
			"Flavor",//
			//
			"Os-floating-ips",//
			"Os-keypairs",//
			"Os-quota-sets-for-Tenant",//
			"Os-security-groups",//
			"Os-simple-tenant-usage-forTenant",//
			"Snapshots-Detail",//
			"Volumes-Detail" };//

	private static Event parseLine(String line) {

		String pattern = "(\\S*) (\\S*) (\\S*) (\\S*) \\[(\\S*) (\\S*) (\\S*)\\] (\\S*) (\\S*) (\\S*) \\[(\\S*) (\\S*)\\] (\\S*) (\\S*) (\\S*) (\\S*) (\\S*) (\\S*)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(line);

		String[] res = new String[18];

		System.out.println(line);

		if (m.find()) {
			for (int i = 0; i < m.groupCount(); i++) {
				res[i] = m.group(i);
			}

			System.out.println("Parsing Path " + res[14]);
			// Extract USER ID
			String userID = res[6];
			System.out.println("CloudInference_RESTResources.parseLine() User " + userID);
			String tenantID = res[7];
			System.out.println("CloudInference_RESTResources.parseLine() Tenant " + tenantID);

			String request_type = res[13];
			String response_status_code = res[16];

			// TODO
			// Concatenate user and tenant ?
			String user_and_tenant_IDs = userID + "::" + tenantID;

			// Extract Request Attributes/parameters
			HashSet<Feature> features = getUrlFeatures(res[14]);
			// System.out.println(String.format("Got %d features",
			// features.size()));

			// Remove the Attributes from the URL for easy parsing
			String path_no_parameters = removeAttributes(res[14]);

			Event event = null;

			// Iterate over the resource labels and get the best matching
			// TODO This can be improved !

			Map<String, String> in_url_features = new HashMap<String, String>();

			for (int index = 0; index < RESOURCE_TEMPLATES.length; index++) {

				if (RESOURCE_TEMPLATES[index].match(path_no_parameters, in_url_features)) {

					// Update features with in_url_features
					// TODO Check name consistency !
					for (Entry<String, String> feature : in_url_features.entrySet()) {
						features.add(new Feature(feature.getKey(), feature.getValue()));
					}

					// Update features with HTTP OPERATION and RESPONSE CODE
					features.add(new Feature("response_status_code", response_status_code));
					features.add(new Feature("request_type", request_type));

					// Create the event using the EVENT_ID -> to be used inside
					// the filters
					event = new Event(EVENT_ID[index], user_and_tenant_IDs, features, false);

					System.out.println("CloudInference_RESTResources.parseLine() Created Event " + EVENT_ID[index] + "\n\n");
					// NOTE: Under the assumption that URL are in hierarchy this
					// should be ok to stop here !
					// Kinda Chain-of-command pattern
					break;
				}
			}

			// if (line.contains("servers/") && line.contains("detail")) {
			// event = new Event("server_detail", res[6], features, false);
			// System.out.println("server_detail" + " " + res[6] + " " +
			// features);
			// }
			//
			// if (line.contains("servers/") && !line.contains("detail")) {
			// event = new Event("server_id", res[6], features, false);
			// System.out.println("server_id" + " " + res[6] + " " + features);
			// }
			//
			// if (line.contains("servers") && !line.contains("servers/")) {
			// event = new Event("servers", res[6], features, false);
			// System.out.println("servers" + " " + res[6] + " " + features);
			// }
			//
			// if (line.contains("flavors") && !line.contains("flavors/")) {
			// System.out.println("flavors");
			// event = new Event("flavors", res[6], features, false);
			// System.out.println("flavors" + " " + res[6] + " " + features);
			// }
			//
			// if (line.contains("flavors/")) {
			// if (line.contains("flavors/m")) {
			// event = new Event("flavors_types", res[6], features, false);
			// System.out.println("flavors_type" + " " + res[6] + " " +
			// features);
			// } else {
			// event = new Event("flavors_id", res[6], features, false);
			// System.out.println("flavors_id" + " " + res[6] + " " + features);
			// }
			// }
			//
			// if (line.contains("os-simple-tenant-usage")) {
			// System.out.println("os-simple-tenant-usage");
			// event = new Event("os_simple_tenant_usage", res[6], features,
			// false);
			// }
			//
			// if (line.contains("os-floating-ips")) {
			// System.out.println("os-floating-ips");
			// event = new Event("os_floating_ips", res[6], features, false);
			// }
			//
			// if (line.contains("volumes")) {
			// event = new Event("volumes", res[6], features, false);
			// System.out.println("volumes");
			// }
			//
			// if (line.contains("os-quota-sets")) {
			// System.out.println("os-quota-sets");
			// event = new Event("os_quota_sets", res[6], features, false);
			// }
			//
			// if (line.contains("images")) {
			// System.out.println("images");
			// event = new Event("images", res[6], features, false);
			// }

			if (event == null) {
				System.err.println("No event create for line: " + line);
			}

			return event;
		} else {
			System.err.println("NO MATCH");
			return null;
		}
	}

	public static Long covertTimestamp(String timestamp) throws ParseException {
		// 2013-08-10 12:54:48
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		cal.setTime(sdf.parse(timestamp));// all done
		return cal.getTime().getTime();
	}

	public static HashSet<Feature> getUrlFeatures(String url) {
		HashSet<Feature> features = new HashSet<Feature>();
		String pattern = "\\?(.+?)=(.+)(?:&(.+?)=(.+))+";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(url);
		if (m.find()) {
			for (int i = 1; i < m.groupCount(); i += 2) {
				features.add(new Feature(m.group(i), m.group(i + 1)));
			}
		}
		return features;
	}

	public static String removeAttributes(String path) {
		int i = path.lastIndexOf('?');
		if (i == -1) {
			// No parameters -> return original String
			return path;
		} else {
			return path.substring(0, i);
		}

	}
}
