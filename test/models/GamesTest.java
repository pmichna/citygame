package models;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class GamesTest extends BaseModelTest {
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