package models;

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

public class UsersTest extends BaseModelTest {
	
	private static final double DELTA = 1e-15;
    
    @Test
    public void createAndRetrieveUser() {
        new User("bob@gmail.com", "Bob", "secret1", "111111111", USER_PRIVILEGE.regular).save();
        new User("alice@gmail.com", "Alice", "secret2", "222222222", USER_PRIVILEGE.admin).save();
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
        new User("bob@gmail.com", "Bob1", "secret1", "111111111", USER_PRIVILEGE.regular).save();
        new User("bob@gmail.com", "Bob2", "secret2", "222222222", USER_PRIVILEGE.regular).save();
    }

    @Test(expected=javax.persistence.PersistenceException.class)
    public void tryRegisterTheSameAlias() {
        new User("bob1@gmail.com", "Bob", "secret1", "111111111", USER_PRIVILEGE.regular).save();
        new User("bob2@gmail.com", "Bob", "secret2", "222222222", USER_PRIVILEGE.regular).save();
    }
    
    @Test
    public void tryAuthenticateUser() {
    	new User("bob@gmail.com", "Bob", "secret", "123456789", USER_PRIVILEGE.regular).save();
        
		assertNotNull(User.authenticate("bob@gmail.com", "secret"));
		assertNull(User.authenticate("bob@gmail.com", "badpassword"));
		assertNull(User.authenticate("tom@gmail.com", "secret"));
	}
    
    @Test
    public void editUser() {
		String originalEmail = "bob@gmail.com";
		String newEmail = "bobNew@gmail.com";
		String originalAlias = "Bob";
		String newAlias = "BobNew";
		String originalPass = "secret";
		String newPass = "secretNew";
		String originalPhone = "123456789";
		String newPhone = "987654321";
		
    	new User(originalEmail, originalAlias, originalPass, originalPhone, USER_PRIVILEGE.regular).save();
    	User.editUser(originalEmail, newEmail, newPass, newAlias, newPhone);
    	User bobOld = User.find.where().eq("email", originalEmail).findUnique();
    	assertNull(bobOld);
    	User bobNew = User.find.where().eq("email", newEmail).findUnique();
    	assertNotNull(bobNew);
    	
    	assertEquals(newAlias, bobNew.alias);
    	assertEquals(newPhone, bobNew.phoneNumber);
    	assertNull(User.authenticate(newEmail, originalPass));
    	assertNotNull(User.authenticate(newEmail, newPass));    	
    }
}