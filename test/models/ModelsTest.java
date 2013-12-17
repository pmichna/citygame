package models;

//import models.USER_PRIVILEGE;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.*;

import com.fasterxml.jackson.core.sym.Name;

import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ModelsTest extends BaseModelTest {
	
	private static final double DELTA = 1e-15;
    
    @Test
    public void createAndRetrieveUser() {
        new User("bob@gmail.com", "Bob", "secret1", USER_PRIVILEGE.regular).save();
        new User("alice@gmail.com", "Alice", "secret2", USER_PRIVILEGE.admin).save();
        User bob = User.find.where().eq("email", "bob@gmail.com").findUnique();
        User alice = User.find.where().eq("email", "alice@gmail.com").findUnique();
        assertNotNull(bob);
        assertNotNull(alice);
        assertEquals("Bob", bob.alias);
        assertEquals("Alice", alice.alias);
        assertEquals(USER_PRIVILEGE.regular, bob.privilege);
        assertEquals(USER_PRIVILEGE.admin, alice.privilege);
    }

    @Test(expected=javax.persistence.PersistenceException.class)
    public void tryRegisterTheSameEmail() {
        new User("bob@gmail.com", "Bob1", "secret1", USER_PRIVILEGE.regular).save();
        new User("bob@gmail.com", "Bob2", "secret2", USER_PRIVILEGE.regular).save();
    }

    @Test(expected=javax.persistence.PersistenceException.class)
    public void tryRegisterTheSameAlias() {
        new User("bob1@gmail.com", "Bob", "secret1", USER_PRIVILEGE.regular).save();
        new User("bob2@gmail.com", "Bob", "secret2", USER_PRIVILEGE.regular).save();
    }
    
  //   @Test
  //   public void tryAuthenticateUser() {
  //       new User("bob@gmail.com", "Bob", "secret", User.PRIVILEGES.regular).save();
        
  //       assertNotNull(User.authenticate("bob@gmail.com", "secret"));
  //       assertNull(User.authenticate("bob@gmail.com", "badpassword"));
  //       assertNull(User.authenticate("tom@gmail.com", "secret"));
  //   }
    
  //   @Test
  //   public void findScenariosInvolving() {
  //   	new User("bob@gmail.com", "Bob", "secret", User.PRIVILEGES.regular).save();
  //       new User("jane@gmail.com", "Jane", "secret", User.PRIVILEGES.regular).save();
  //       SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

  //       Scenario.create("Scenario 1", false, null, "bob@gmail.com");
  //       try {
		// 	Scenario.create("Scenario 2", false, dt.parse("2013-12-12 00:00:00"), "jane@gmail.com");
		// } catch (ParseException e) {
		// 	System.err.println("Problem with date parsing");
		// 	e.printStackTrace();
		// }

  //       List<Scenario> results = Scenario.findInvolving("bob@gmail.com");
  //       assertEquals(1, results.size());
  //       assertEquals("Scenario 1", results.get(0).name);
  //   }
    
  //   @Test
  //   public void findScenariosNotExpired() {
  //   	new User("bob@gmail.com", "Bob", "secret", User.PRIVILEGES.regular).save();
  //       new User("jane@gmail.com", "Jane", "secret", User.PRIVILEGES.regular).save();
  //       SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
  //       Scenario.create("Scenario 1", false, null, "bob@gmail.com");
  //       try {
		// 	Scenario.create("Scenario 2", false, dt.parse("2013-12-12 00:00:00"), "jane@gmail.com");
		// 	Scenario.create("Scenario 3", false, dt.parse("2013-10-10 00:00:00"), "jane@gmail.com");
		// } catch (ParseException e) {
		// 	System.err.println("Problem with date parsing");
		// 	e.printStackTrace();
		// }
        
  //       List<Scenario> results = Scenario.findNotExpired(new Date());
  //       assertEquals(2, results.size());
  //   }
    
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
    
  //   @Test
  //   public void createAndRetrieveGame() {
  //   	new User("bob@gmail.com", "Bob", "secret", User.PRIVILEGES.regular).save();
  //   	Scenario scenario = Scenario.create("Scenario 1", false, null, "bob@gmail.com");
    	
  //   	Game.create("bob@gmail.com", Scenario.find.where().eq("name", "Scenario 1").findUnique().id);
  //   	List<Game> results = Game.getUserGames("bob@gmail.com");
  //   	assertNotNull(results);
  //   	assertEquals(1, results.size());
  //   	assertEquals("Scenario 1", results.get(0).scenario.name);
  //   	assertEquals(Game.STATUS.inProgress, results.get(0).status);
  //   	assertEquals(true, results.get(0).startDate.before(new Date()));
  //   	assertEquals(0, results.get(0).points);
  //   }
}