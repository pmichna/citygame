package models;

import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ScenariosTest extends BaseModelTest {
	String user1Email = "bob@gmail.com";
	String user1Name = "Bob";
	String user1Password = "secret";
	String user1PhoneNumber = "123456789";
	USER_PRIVILEGE privilege = USER_PRIVILEGE.regular;
	String user2Email = "alice@gmail.com";
	String user2Name = "Alice";
	String user2Password = "secretAlice";
	String user2PhoneNumber = "987654321";

	@Before
	public void setUp() {
		new User(user1Email, user1Name, user1Password, user1PhoneNumber,
				privilege).save();
		new User(user2Email, user2Name, user2Password, user2PhoneNumber,
				privilege).save();
	}

	@Test
	public void findOwnedBy() {
		Scenario.create("Scenario 1", false, null, user1Email);
		Scenario.create("Scenario 2", false, null, user1Email);
		
		List<Scenario> results = Scenario.findOwned(user1Email);
		assertEquals(2, results.size());
		assertEquals("Scenario 1", results.get(0).name);
	}
	// @Test
	// public void findScenariosNotExpired() {
	// new User("bob@gmail.com", "Bob", "secret",
	// User.PRIVILEGES.regular).save();
	// new User("jane@gmail.com", "Jane", "secret",
	// User.PRIVILEGES.regular).save();
	// SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	// Scenario.create("Scenario 1", false, null, "bob@gmail.com");
	// try {
	// Scenario.create("Scenario 2", false, dt.parse("2013-12-12 00:00:00"),
	// "jane@gmail.com");
	// Scenario.create("Scenario 3", false, dt.parse("2013-10-10 00:00:00"),
	// "jane@gmail.com");
	// } catch (ParseException e) {
	// System.err.println("Problem with date parsing");
	// e.printStackTrace();
	// }

	// List<Scenario> results = Scenario.findNotExpired(new Date());
	// assertEquals(2, results.size());
	// }
}