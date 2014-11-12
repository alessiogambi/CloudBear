package cloudbear;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.usi.star.bear.analysis.AnalysisEngine;
import ch.usi.star.bear.inference.InferenceEngine;
import ch.usi.star.bear.model.Event;
import ch.usi.star.bear.model.Feature;
import ch.usi.star.bear.model.Model;
import ch.usi.star.bear.properties.BearProperties;
import ch.usi.star.bear.visualization.BearVisualizer;

public class CloudInference {

	public static void main(String[] args) throws Exception {

		BearProperties.getInstance().setProperty(BearProperties.FILTERSPACKAGEPREFIX, "cloudbear.filters");
		BearProperties.getInstance().setProperty(BearProperties.CLASSIFIERSPACKAGEPREFIX, "cloudbear.classifiers");
		BearProperties.getInstance().setProperty(BearProperties.EVENTDURATION, "false");

		File file = new File("res/nova-api.log");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;
		
		InferenceEngine inferenceEngine = new InferenceEngine();
		AnalysisEngine analisysEngine = new AnalysisEngine();
		
		while ((line = br.readLine()) != null) {
			if (!line.equals("") && line.contains("HTTP/")) {
				Event event = parseLine(line);
				if (event != null) {
					inferenceEngine.signal(event);

				} else {
					System.out.println("Null event");
				}
			}
		}
		List<Model> models = inferenceEngine.exportModels();
		br.close();
		fr.close();
		
		
		
		Model model = analisysEngine.synthesize(models, "{}");

		BearVisualizer visualizer = new BearVisualizer(model);
		visualizer.display();
		System.out.println(model.generatePrismModel("prova"));
		// String result = analisysEngine.analyze(model, pctl, "BEAR", false);
		// System.out.println(result);

	}

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

			HashSet<Feature> features = getUrlFeatures(res[14]);
			Event event = null;

			if (line.contains("servers/") && line.contains("detail")) {
				event = new Event("server_detail", res[6], features, false);
				System.out.println("server_detail");
			}

			if (line.contains("servers/") && !line.contains("detail")) {
				event = new Event("server_id", res[6], features, false);
				System.out.println("server_id");
			}

			if (line.contains("servers") && !line.contains("servers/")) {
				event = new Event("servers", res[6], features, false);
				System.out.println("servers");
			}

			if (line.contains("flavors") && !line.contains("flavors/")) {
				System.out.println("flavors");
				event = new Event("flavors", res[6], features, false);
			}

			if (line.contains("flavors/")) {
				if (line.contains("flavors/m")) {
					event = new Event("flavors_types", res[6], features, false);
					System.out.println("flavors_type");
				} else {
					event = new Event("flavors_id", res[6], features, false);
					System.out.println("flavors_id");
				}
			}

			if (line.contains("os-simple-tenant-usage")) {
				System.out.println("os-simple-tenant-usage");
				event = new Event("os_simple_tenant_usage", res[6], features, false);
			}

			if (line.contains("os-floating-ips")) {
				System.out.println("os-floating-ips");
				event = new Event("os_floating_ips", res[6], features, false);
			}

			if (line.contains("volumes")) {
				event = new Event("volumes", res[6], features, false);
				System.out.println("volumes");
			}

			if (line.contains("os-quota-sets")) {
				System.out.println("os-quota-sets");
				event = new Event("os_quota_sets", res[6], features, false);
			}

			if (line.contains("images")){
				System.out.println("images");
				event = new Event("images", res[6], features, false);
			}

			if (event == null)
				System.out.println(line);

			return event;
		} else {
			System.out.println("NO MATCH");
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
}
