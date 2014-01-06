package controllers;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.*;

import play.mvc.*;
import play.libs.*;
import play.test.*;
import static play.test.Helpers.*;

import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;

import models.*;

public class CreateScenarioTest extends BaseControllerTest {
	private String userEmail = "test@test.pl";
	private String userAlias = "test_alias";
	private String userPassword = "secret";
	private String userPhoneNumber = "987654321";

	@Before
	public void setUp() {
		new User(userEmail, userAlias, userPassword, userPhoneNumber,
				USER_PRIVILEGE.regular).save();
	}

	@Test
	public void createScenarioSuccess() {
		String correctName = "testScenario";
		Result result = callAction(
				controllers.routes.ref.ScenarioController.createScenarioPOST(),
				fakeRequest().withSession("email", userEmail)
						.withFormUrlEncodedBody(
								ImmutableMap.of(
										"name", correctName, 
										"day", "dd", 
										"month", "mm", 
										"year", "yyyy")));
		assertEquals(303, status(result));
		
		List<Scenario> results = Scenario.findOwned(userEmail);
		assertNotNull(results);
		assertEquals(1,results.size());
		
	}

}