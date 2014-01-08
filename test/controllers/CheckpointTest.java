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

public class CheckpointTest extends BaseControllerTest {
	private String scenarioName = "scenario test name";
	private boolean isScenarioPublic = false;
	private String userEmail = "testemail#gmail.com";
	private String userAlias = "user alias";
	private String userPass = "secret";
	private String userPhoneNumber = "123456789";
	private USER_PRIVILEGE userPrivilege = USER_PRIVILEGE.regular;
	
	private String checkpointName = "checkpoint name";
	private int longitudeDegrees = 2;
	private double longitudeMinutes = 3;
	private int latitudeDegrees = 2;
	private double latitudeMinutes = 4;
	String message = "checkpoint message";
	int points = 10;
	private static final double DELTA = 0.07;
	
	@Before
	public void setUp() {
		new User(userEmail, userAlias, userPass, userPhoneNumber, userPrivilege).save();
		Scenario.create(scenarioName, isScenarioPublic, null, userEmail);
	}
	
	@Test
	public void createCheckpointSuccess() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Integer.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", Integer.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinute", Double.toString(latitudeMinutes));
		mapBuilder.put("message", message);
		mapBuilder.put("points", Integer.toString(points));
		
		ImmutableMap<String, String> map = mapBuilder.build();
		
	    Result result = callAction(
        	controllers.routes.ref.CheckpointController.createCheckpointPOST(scenario.id),
        	fakeRequest()
				.withSession("email", userEmail)
				.withFormUrlEncodedBody(map)
    	);
		assertEquals(303, status(result));
		
		Checkpoint checkpoint = Checkpoint.find
											.where()
											.eq("name", checkpointName)
											.findUnique();
		assertNotNull(checkpoint);
		assertEquals(checkpointName, checkpoint.name);
		double longitude = longitudeDegrees + longitudeMinutes / 4L;
		assertEquals(longitude, checkpoint.longitude, DELTA);
		double latitude = latitudeDegrees + latitudeMinutes / 60L;
		assertEquals(latitude, checkpoint.latitude, DELTA);
	}
	
	@Test
	public void createCheckpointFailureNoName() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("longitudeDegrees", Integer.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", Integer.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinute", Double.toString(latitudeMinutes));
		mapBuilder.put("message", message);
		mapBuilder.put("points", Integer.toString(points));
		
		ImmutableMap<String, String> map = mapBuilder.build();
		
	    Result result = callAction(
        	controllers.routes.ref.CheckpointController.createCheckpointPOST(scenario.id),
        	fakeRequest()
				.withSession("email", userEmail)
				.withFormUrlEncodedBody(map)
    	);
		assertEquals(400, status(result));
		
		Checkpoint checkpoint = Checkpoint.find
											.where()
											.eq("name", checkpointName)
											.findUnique();
		assertNull(checkpoint);
	}
}