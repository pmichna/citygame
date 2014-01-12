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
	private double latitudeMinutes = 3;
	String message = "checkpoint message";
	int points = 1;
	private static final double DELTA = 0.07;
	
	@Before
	public void setUp() {
		new User(userEmail, userAlias, userPass, userPhoneNumber, userPrivilege).save();
		Scenario.create(scenarioName, isScenarioPublic, null, userEmail, false);
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
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
		double longitude = longitudeDegrees + longitudeMinutes / 60;
		assertEquals(longitude, checkpoint.longitude, DELTA);
		double latitude = latitudeDegrees + latitudeMinutes / 60;
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
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
	
	@Test
	public void createCheckpointFailureNoLongitudeDeg() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", Integer.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
	
	@Test
	public void createCheckpointFailureNoLongitudeMinutes() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Integer.toString(longitudeDegrees));
		mapBuilder.put("latitudeDegrees", Integer.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
	
	@Test
	public void createCheckpointFailureNoLatitudeDeg() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Double.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
	
	@Test
	public void createCheckpointFailureNoLatitudeMinutes() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Double.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", Double.toString(latitudeDegrees));
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
	
	@Test
	public void createCheckpointFailureNoMessage() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Double.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", Double.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
	
	@Test
	public void createCheckpointFailureNoPoints() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Double.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", Double.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
		mapBuilder.put("message", message);
		
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
	
	@Test
	public void createCheckpointFailureWrongLonDegMin() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", "-1");
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", Integer.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
	
	@Test
	public void createCheckpointFailureWrongLonDegMax() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", "181");
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", Integer.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
	
	@Test
	public void createCheckpointFailureWrongLonMinMin() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Integer.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", "-1");
		mapBuilder.put("latitudeDegrees", Integer.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
	
	@Test
	public void createCheckpointFailureWrongLonMinMax() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Integer.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", "60");
		mapBuilder.put("latitudeDegrees", Integer.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
	// //////////////////
	
	@Test
	public void createCheckpointFailureWrongLatDegMin() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Integer.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", "-1");
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
	
	@Test
	public void createCheckpointFailureWrongLatDegMax() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Integer.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", "91");
		mapBuilder.put("latitudeMinutes", Double.toString(latitudeMinutes));
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
	
	@Test
	public void createCheckpointFailureWrongLatMinMin() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Integer.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", Integer.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinutes", "-1");
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
	
	@Test
	public void createCheckpointFailureWrongLatMinMax() {
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", checkpointName);
		mapBuilder.put("longitudeDegrees", Integer.toString(longitudeDegrees));
		mapBuilder.put("longitudeMinutes", Double.toString(longitudeMinutes));
		mapBuilder.put("latitudeDegrees", Integer.toString(latitudeDegrees));
		mapBuilder.put("latitudeMinutes", "60");
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
	
	@Test
	public void editCheckpointSuccess() {
		String newName = "new name";
		int newLongitudeDegrees = 1;
		double newLongitudeMinutes = 2;
		int newLatitudeDegrees = 5;
		double newLatitudeMinutes = 20;
		String newMessage = "some new message";
		int newPoints = 20;
		
		Scenario scenario = Scenario.find
									.where()
									.eq("name", scenarioName)
									.findUnique();
		Checkpoint checkpoint = Checkpoint.create(checkpointName, 3, 3, points, message, scenario.id, false);
		

		
		ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
		mapBuilder.put("name", newName);
		mapBuilder.put("longitudeDegrees", Integer.toString(newLongitudeDegrees));
		mapBuilder.put("longitudeMinutes", Double.toString(newLongitudeMinutes));
		mapBuilder.put("latitudeDegrees", Integer.toString(newLatitudeDegrees));
		mapBuilder.put("latitudeMinutes", Double.toString(newLatitudeMinutes));
		mapBuilder.put("message", newMessage);
		mapBuilder.put("points", Integer.toString(newPoints));
		ImmutableMap<String, String> map = mapBuilder.build();
		
	    Result result = callAction(
        	controllers.routes.ref.CheckpointController.editCheckpointPOST(checkpoint.id, scenario.id),
        	fakeRequest()
				.withSession("email", userEmail)
				.withFormUrlEncodedBody(map)
    	);
		assertEquals(200, status(result));
		
		Checkpoint modified = Checkpoint.find.ref(checkpoint.id);
		assertNotNull(modified);
		assertEquals(newName, modified.name);
		double newLongitude = newLongitudeDegrees + newLongitudeMinutes/60;
		assertEquals(newLongitude, modified.longitude, DELTA);
		double newLatitude = newLatitudeDegrees + newLatitudeMinutes/60;
		assertEquals(newLatitude, modified.latitude, DELTA);
		assertEquals(newMessage, modified.message);
		assertEquals(newPoints, modified.points);
	}
}