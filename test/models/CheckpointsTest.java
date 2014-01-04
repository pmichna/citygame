package models;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class CheckpointsTest extends BaseModelTest {
//   @Test
//   public void createAndRetrieveCheckpoint() {
//   	new User("bob@gmail.com", "Bob", "secret", User.PRIVILEGES.regular).save();
//   	Scenario scenario = Scenario.create("Scenario 1", false, null, "bob@gmail.com");
//   	List<String> answers = Arrays.asList(new String[] {"abc", "def", "ghi"});
//   	Checkpoint checkpoint = new Checkpoint();
//   	checkpoint.title = "Checkpoint test";
//   	checkpoint.longitude = 12.345;
//   	checkpoint.latitude = 23.456;
//   	checkpoint.points = 10;
//   	checkpoint.possibleAnswers = answers;
//   	checkpoint.scenario = scenario;
//   	checkpoint.save();
  	
//   	List<Checkpoint> results = Checkpoint.findAssignedTo(scenario.id);
//       assertEquals(1, results.size());
//       assertEquals("Checkpoint test", results.get(0).title);
//       assertEquals(12.345, results.get(0).longitude, DELTA);
//       assertEquals(23.456, results.get(0).latitude, DELTA);
//       assertEquals(10, results.get(0).points);
//       assertEquals("abc", results.get(0).possibleAnswers.get(0));
//       assertEquals("def", results.get(0).possibleAnswers.get(1));
//       assertEquals("ghi", results.get(0).possibleAnswers.get(2));
      
//       List<Checkpoint> wrongResults = Checkpoint.findAssignedTo(scenario.id+1);
//       assertNull(wrongResults);
//   }
}