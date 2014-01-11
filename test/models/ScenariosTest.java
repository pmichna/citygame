package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
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

	@Test
	public void editScenario() {
		String oldName = "Scenario 1";
		Boolean oldIsPublic = false;
		Date oldExpirationDate = null;
		
		String newName = "Scenario new";
		Boolean newIsPublic = true;
		Date newExpirationDate = new Date(System.currentTimeMillis());
		
		Scenario scenario = Scenario.create(oldName, oldIsPublic, oldExpirationDate, user1Email);
		Scenario.edit(scenario.id, newName, newIsPublic, newExpirationDate);
		Scenario modified = Scenario.find.where().eq("name", newName).findUnique();
		assertNotNull(modified);
		assertEquals(newIsPublic, modified.isPublic);
		assertEquals(false, modified.isAccepted);
		assertEquals(newExpirationDate.toString(), modified.expirationDate.toString());
	}
}