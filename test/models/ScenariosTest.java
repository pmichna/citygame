package models;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ScenariosTest extends BaseModelTest {
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
}