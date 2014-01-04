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
    public void editUser(){
    	new User("bob@gmail.com", "Bob", "secret", "123456789", USER_PRIVILEGE.regular).save();
    	User.editUserData("bob@gmail.com", "bob2@gmail.com", "newSecret", "Robert", "987654321");
    	User bobNew = User.find.where().eq("email", "bob2@gmail.com").findUnique();
    	//assertNotNull(bobNew);
    	User bobOld = User.find.where().eq("email", "bob@gmail.com").findUnique();
    	//assertNull(bobOld);
    	
    	//assertEquals("Robert", bobNew.alias);
    	//assertEquals("987654321", bobNew.phoneNumber);
    	//assertNotNull(User.authenticate("bob2@gmail.com", "newSecret"));
    	//assertNull(User.authenticate("bob@gmail.com", "secret"));
    }
}