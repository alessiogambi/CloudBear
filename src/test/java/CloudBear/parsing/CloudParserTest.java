package CloudBear.parsing;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.testng.annotations.Test;

import cloudbear.CloudInference_RESTResources;

import com.sun.jersey.api.uri.UriTemplate;

public class CloudParserTest {

	@Test
	public void parseURLwithTemplate() {

		String path = "/v2/83d2db83cb7e4f61aafde0e1063cbc4b/servers/detail?status=error&all_tenants=1";

		Map<String, String> map = new HashMap<String, String>();

		UriTemplate tenant = new UriTemplate("/v2/{tenant-id}");
		UriTemplate servers = new UriTemplate("/v2/{tenant-id}/servers");
		UriTemplate server_details = new UriTemplate("/v2/{tenant-id}/servers/detail");
		UriTemplate server_details_with_attributes = new UriTemplate("/v2/{tenant-id}/servers/detail?{attributes}");

		Assert.assertFalse(tenant.match(path, map));
		Assert.assertFalse(servers.match(path, map));
		Assert.assertFalse(server_details.match(path, map));
		Assert.assertTrue(server_details_with_attributes.match(path, map));

		System.out.println("CloudParserTest.parseURLwithTemplate() Map: " + map);

	}

	@Test
	public void parseURLwithTemplateIgnoreParameters() {

		String path = "/v2/83d2db83cb7e4f61aafde0e1063cbc4b/servers/detail?status=error&all_tenants=1";
		String path1 = "/v2/83d2db83cb7e4f61aafde0e1063cbc4b/servers/detail";

		Map<String, String> map = new HashMap<String, String>();

		UriTemplate server_details = new UriTemplate("/v2/{tenant-id}/servers/detail{ignore-me}");

		Assert.assertTrue(server_details.match(path, map));

		System.out.println("CloudParserTest.parseURLwithTemplate() Map: " + map);

		Assert.assertFalse(server_details.match(path1, map));

		System.out.println("CloudParserTest.parseURLwithTemplate() Map: " + map);

	}

	@Test
	public void testRemoveAttributesIfPresent() {

		String path = "/v2/83d2db83cb7e4f61aafde0e1063cbc4b/servers/detail?status=error&all_tenants=1";
		String path_1 = "/v2/83d2db83cb7e4f61aafde0e1063cbc4b/servers/detail";

		String path1 = CloudInference_RESTResources.removeAttributes(path);
		String path2 = CloudInference_RESTResources.removeAttributes(path_1);

		Map<String, String> map = new HashMap<String, String>();
		UriTemplate server_details = new UriTemplate("/v2/{tenant-id}/servers/detail");

		Assert.assertTrue(server_details.match(path1, map));

		System.out.println("CloudParserTest.parseURLwithTemplate() Map: " + map);
		// Only the tenant-id
		Assert.assertTrue(map.size() == 1);

		map.clear();
		Assert.assertTrue(server_details.match(path2, map));

		System.out.println("CloudParserTest.parseURLwithTemplate() Map: " + map);
		// Only the tenant-id
		Assert.assertTrue(map.size() == 1);

	}
}
