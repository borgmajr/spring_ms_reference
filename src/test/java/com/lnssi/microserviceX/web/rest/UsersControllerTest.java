package com.lnssi.microserviceX.web.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.lnssi.microserviceX.ReferenceResTfulMicroserviceApplication;
import com.lnssi.microserviceX.model.security.Role;
import com.lnssi.microserviceX.model.security.User;
import com.lnssi.microserviceX.services.RoleService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ReferenceResTfulMicroserviceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersControllerTest {

	@Autowired
	private RoleService roleService;

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = createHeaders("admin", "admin");

	@WithMockUser(value = "admin", password = "admin")
	@Test
	public void testUserController() throws JSONException {

		//////////////////////////// GET LIST /////////////////////////////////////
		{
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);

			ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/rest/users"), HttpMethod.GET,
					entity, String.class);

			JSONArray expectedJson = new JSONArray(
					"[{\"roles\":null,\"id\":1,\"email\":\"admin@test.com\",\"username\":\"admin\"},{\"roles\":null,\"id\":2,\"email\":\"user@test.com\",\"username\":\"user\"}]");

			JSONArray responseJson = new JSONArray(response.getBody());
			System.out.println(responseJson);
			assertEquals(2, responseJson.length());

			JSONAssert.assertEquals(expectedJson, responseJson, false);

			System.out.println(responseJson.getJSONObject(0));
			JSONAssert.assertEquals("{\"roles\":null,\"id\":1,\"email\":\"admin@test.com\",\"username\":\"admin\"}",
					responseJson.getJSONObject(0).toString(), false);

			System.out.println(responseJson.getJSONObject(1));
			JSONAssert.assertEquals("{\"roles\":null,\"id\":2,\"email\":\"user@test.com\",\"username\":\"user\"}",
					responseJson.getJSONObject(1).toString(), false);
		}
		////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////// ADD USER ///////////////////////////////////
		{
			User user3 = new User("tester", "$2y$10$jAIKR6uMz/xtBg/oOIYoW.omkmUYOE5aq.KAsdsNvkHw4MGJe2Yvq",
					"tester@test.org");
			List<Role> roles = Arrays.asList(roleService.findByName(Role.ROLE_USER).get());
			user3.setRoles(new HashSet<Role>(roles));

			HttpEntity<User> entity = new HttpEntity<User>(user3, headers);

			ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/rest/users"), HttpMethod.POST,
					entity, String.class);

//			String actual = response.getHeaders().get(HttpHeaders.LOCATION).get(0);
//			assertTrue(actual.contains("/rest/users"));

			JSONObject responseJson = new JSONObject(response.getBody());
			JSONAssert.assertEquals(
					"{\"roles\":[{\"name\":\"ROLE_USER\",\"id\":2}],\"id\":3,\"email\":\"tester@test.org\",\"username\":\"tester\"}",
					responseJson, false);
		}
		////////////////////////////////////////////////////////////////////////////////

		//////////////////////////// GET LIST AGAIN /////////////////////////////////////
		{
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);

			ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/rest/users"), HttpMethod.GET,
					entity, String.class);

			JSONArray expectedJson = new JSONArray(
					"[{\"roles\":null,\"id\":1,\"email\":\"admin@test.com\",\"username\":\"admin\"},{\"roles\":null,\"id\":2,\"email\":\"user@test.com\",\"username\":\"user\"},{\"roles\":null,\"id\":3,\"email\":\"tester@test.org\",\"username\":\"tester\"}]");

			JSONArray responseJson = new JSONArray(response.getBody());
			System.out.println(responseJson);
			assertEquals(3, responseJson.length());

			JSONAssert.assertEquals(expectedJson, responseJson, false);

			System.out.println(responseJson.getJSONObject(0));
			JSONAssert.assertEquals("{\"roles\":null,\"id\":1,\"email\":\"admin@test.com\",\"username\":\"admin\"}",
					responseJson.getJSONObject(0).toString(), false);

			System.out.println(responseJson.getJSONObject(1));
			JSONAssert.assertEquals("{\"roles\":null,\"id\":2,\"email\":\"user@test.com\",\"username\":\"user\"}",
					responseJson.getJSONObject(1).toString(), false);
			
			System.out.println(responseJson.getJSONObject(2));
			JSONAssert.assertEquals("{\"roles\":null,\"id\":3,\"email\":\"tester@test.org\",\"username\":\"tester\"}",
					responseJson.getJSONObject(2).toString(), false);
		}
		////////////////////////////////////////////////////////////////////////////////

	}

	HttpHeaders createHeaders(String username, String password) {
		return new HttpHeaders() {
			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

}
