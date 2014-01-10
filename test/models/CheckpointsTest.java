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
	String checkpointName = "checkpointTest";
	double longitude = 55.55;
	double latitude = 33.33;
	int points = 5;
	String message = "testMessage";
	
	private static final double DELTA = 1e-15;

	@Before
	public void setUp() {
		new User(userEmail, userName, userPassword, userPhoneNumber, privilege).save();
	}
	
	@Test
	public void createAndRetrieveCheckpoint() {
		Scenario scenario = Scenario.create("Scenario 1", false, null, userEmail, false);
		Checkpoint checkpoint = Checkpoint.create(checkpointName, longitude, latitude, points, message, scenario.id, false);
		  	
		List<Checkpoint> results = Checkpoint.findAssignedTo(scenario.id);
		assertEquals(1, results.size());
		assertEquals(checkpointName, results.get(0).name);
		assertEquals(longitude, results.get(0).longitude, DELTA);
		assertEquals(latitude, results.get(0).latitude, DELTA);
		assertEquals(points, results.get(0).points);
	}
	
	@Test
	public void addAndRetrievePossibleAnswer() {
		String answer1 = "answer1";
		Scenario scenario = Scenario.create("Scenario 1", false, null, userEmail, false);
		Checkpoint checkpoint = Checkpoint.create(checkpointName, longitude, latitude, points, message, scenario.id, false);
		Checkpoint.addPossibleAnswer(answer1, checkpoint.id, false);
		
		List<String> answers = Checkpoint.getAnswers(checkpoint.id);
		assertEquals(1, answers.size());		
		assertEquals(answer1, answers.get(0));
	}
	
	@Test
	public void addAndRetrievePossibleAnswersList() {
		String answer1 = "answer1";
		String answer2 = "answer2";
		String answer3 = "answer3";
		List<String> answers = new ArrayList<String>(3);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);
		
		Scenario scenario = Scenario.create("Scenario 1", false, null, userEmail, false);
		Checkpoint checkpoint = Checkpoint.create(checkpointName, longitude, latitude, points, message, scenario.id, false);
		Checkpoint.addPossibleAnswers(answers, checkpoint.id, false);
		
		List<String> results = Checkpoint.getAnswers(checkpoint.id);
		assertEquals(3, results.size());
		assertTrue(results.contains(answer1));
		assertTrue(results.contains(answer2));
		assertTrue(results.contains(answer3));
	}
	
	@Test
	public void editCheckpoint() {
		Scenario scenario = Scenario.create("Scenario 1", false, null, userEmail, false);
		Checkpoint checkpoint = Checkpoint.create(checkpointName, longitude, latitude, points, message, scenario.id, false);
		
		String newName = "newName";
		double newLongitude = 12.2;
		double newLatitude = 12.2;
		int newPoints = 12;
		String newMessage = "newMessage";
		
		Checkpoint.editCheckpoint(checkpoint.id, newName, newLongitude, newLatitude, newPoints, newMessage, false);
		checkpoint.refresh();
		assertEquals(newName, checkpoint.name);
		assertEquals(newLongitude, checkpoint.longitude, DELTA);
		assertEquals(newLatitude, checkpoint.latitude, DELTA);
		assertEquals(newPoints, checkpoint.points);
		assertEquals(newMessage, checkpoint.message);
	}
}