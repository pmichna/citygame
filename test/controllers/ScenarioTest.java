package controllers;

import org.junit.*;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.List;

import play.mvc.*;
import play.libs.*;
import play.test.*;
import static play.test.Helpers.*;

import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;

import models.*;

public class ScenarioTest extends BaseControllerTest {
	private String userEmail = "test@test.pl";
	private String userAlias = "test_alias";
	private String userPassword = "secret";
	private String userPhoneNumber = "987654321";
	private String scenarioName = "test scenario";

	@Before
	public void setUp() {
		new User(userEmail, userAlias, userPassword, userPhoneNumber,
				USER_PRIVILEGE.regular).save();
	}

	@Test
	public void createScenarioSuccess() {
		Result result = callAction(
				controllers.routes.ref.ScenarioController.createScenarioPOST(),
				fakeRequest().withSession("email", userEmail)
						.withFormUrlEncodedBody(
								ImmutableMap.of(
										"name", scenarioName, 
										"isPublic", "true",
										"day", "dd", 
										"month", "mm", 
										"year", "yyyy")));
		assertEquals(303, status(result));
		
		List<Scenario> results = Scenario.findOwned(userEmail);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertTrue(results.get(0).isPublic);
	}
	
	@Test
	public void editScenarioSuccess() {
		String newName = "new scenario name";
		Date newDate = new Date(System.currentTimeMillis());
		Boolean newIsPublic = true;
		
		Scenario scenario = Scenario.create(scenarioName, !newIsPublic, null, userEmail);
		
		Result result = callAction(
				controllers.routes.ref.ScenarioController.editScenarioPOST(scenario.id),
				fakeRequest().withSession("email", userEmail)
						.withFormUrlEncodedBody(
								ImmutableMap.of(
										"name", newName,
										"isPublic", newIsPublic.toString(), 
										"day", newDate.toString().substring(8, 10), 
										"month", newDate.toString().substring(5, 7), 
										"year", newDate.toString().substring(0, 5))));
		assertEquals(303, status(result));
		Scenario modified = Scenario.find.ref(scenario.id);
		assertNotNull(modified);
		assertEquals(newName, modified.name);
		assertEquals(newDate.toString(), modified.expirationDate.toString());
		assertTrue(modified.isPublic);
		assertFalse(modified.isAccepted);
	}
	
	@Test
	public void editScenarioFailureNoName() {
		Date newDate = new Date(System.currentTimeMillis());
		Boolean newIsPublic = true;
		
		Scenario scenario = Scenario.create(scenarioName, !newIsPublic, null, userEmail);
		
		Result result = callAction(
				controllers.routes.ref.ScenarioController.editScenarioPOST(scenario.id),
				fakeRequest().withSession("email", userEmail)
						.withFormUrlEncodedBody(
								ImmutableMap.of(
										"isPublic", newIsPublic.toString(), 
										"day", newDate.toString().substring(8, 10), 
										"month", newDate.toString().substring(5, 7), 
										"year", newDate.toString().substring(0, 4))));
		assertEquals(400, status(result));
		Scenario modified = Scenario.find.ref(scenario.id);
		assertNotNull(modified);
		assertEquals(scenarioName, modified.name);
	}
	
	@Test
	public void acceptScenario() {
		String adminEmail = play.Play.application().configuration().getString("application.admin");
		Scenario scenario = Scenario.create(scenarioName, true, null, userEmail);
		
		Result result = callAction(
				controllers.routes.ref.ScenarioController.acceptScenarioGET(scenario.id),
				fakeRequest().withSession("email", adminEmail)
		);
		assertEquals(303, status(result));
		Scenario modified = Scenario.find.ref(scenario.id);
		assertNotNull(modified);
		assertTrue(modified.isAccepted);
	}

}