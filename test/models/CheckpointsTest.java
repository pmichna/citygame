package models;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;
import java.util.*;

public class CheckpointsTest extends BaseModelTest {
	private String userEmail = "bob@gmail.com";
	private String userName = "Bob";
	private String userPassword = "secret";
	private String userPhoneNumber = "123456789";
	private USER_PRIVILEGE privilege = USER_PRIVILEGE.regular;
	
	private static final double DELTA = 1e-15;

	@Before
	public void setUp() {
		new User(userEmail, userName, userPassword, userPhoneNumber, privilege).save();
	}
	
	@Test
	public void createAndRetrieveCheckpoint() {
		String checkpointName = "checkpointTest";
		double longitude = 55.55;
		double latitude = 33.33;
		int points = 5;
		String message = "testMessage";
		
		Scenario scenario = Scenario.create("Scenario 1", false, null, userEmail);
		Checkpoint checkpoint = Checkpoint.create(checkpointName, longitude, latitude, points, message, scenario.id);
		  	
		List<Checkpoint> results = Checkpoint.findAssignedTo(scenario.id);
		assertEquals(1, results.size());
		assertEquals(checkpointName, results.get(0).name);
		assertEquals(longitude, results.get(0).longitude, DELTA);
		assertEquals(latitude, results.get(0).latitude, DELTA);
		assertEquals(points, results.get(0).points);
	}
}