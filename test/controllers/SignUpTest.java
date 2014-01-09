package controllers;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import play.mvc.*;
import play.libs.*;
import play.test.*;
import static play.test.Helpers.*;
import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;
import models.*;

public class SignUpTest extends BaseControllerTest {
	
	@Test
	public void createAccountSuccess() {
	    Result result = callAction(
        	controllers.routes.ref.UserAccountController.createAccountPOST(),
        	fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
	            "email", "bob@example.com",
            	"password", "secret",
            	"alias", "bob",
            	"phoneNumber", "123456789"))
    	);
    	assertEquals(303, status(result));
    	User user = User.find.where()
    					.eq("email", "bob@example.com")
    					.findUnique();
    	assertNotNull(user);
    	assertEquals("bob@example.com", user.email);
    	assertEquals("bob", user.alias);
    	assertEquals("123456789", user.phoneNumber);
	}
	
	@Test
	public void createAdmin() {
		String adminEmail = play.Play.application().configuration().getString("application.admin");
		String adminPass = "pass";
		String adminAlias = "admin";
		String adminPhoneNumber = "123123123";
		
	    Result result = callAction(
        	controllers.routes.ref.UserAccountController.createAccountPOST(),
        	fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
	            "email", adminEmail,
            	"password", adminPass,
            	"alias", adminAlias,
            	"phoneNumber", adminPhoneNumber))
    	);
    	assertEquals(303, status(result));
    	User user = User.find.where()
    					.eq("email", adminEmail)
    					.findUnique();
    	assertNotNull(user);
    	assertEquals(adminEmail, user.email);
    	assertEquals(adminAlias, user.alias);
    	assertEquals(adminPhoneNumber, user.phoneNumber);
		assertEquals(USER_PRIVILEGE.admin, user.privilege);
	}
	
	@Test
	public void createAccountFailureEmailRegex() {
	    Result result = callAction(
        	controllers.routes.ref.UserAccountController.createAccountPOST(),
        	fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
	            "email", "bobexample.com",
            	"password", "secret",
            	"alias", "bob",
            	"phoneNumber", "123456789"))
    	);
    	assertEquals(400, status(result));
    	User user = User.find.where()
    					.eq("email", "bobexample.com")
    					.findUnique();
    	assertNull(user);
	}
	
	@Test
	public void createAccountFailurePhoneRegex() {
	    Result result = callAction(
        	controllers.routes.ref.UserAccountController.createAccountPOST(),
        	fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
	            "email", "bob@example.com",
            	"password", "secret",
            	"alias", "bob",
            	"phoneNumber", "123"))
    	);
    	assertEquals(400, status(result));
    	User user = User.find.where()
    					.eq("email", "bob@example.com")
    					.findUnique();
    	assertNull(user);
	}
	
	@Test
	public void createAccountFailureEmailExists() {
        new User("bob@example.com", "Bob", "secret1", "111111111", USER_PRIVILEGE.regular).save();
	    Result result = callAction(
        	controllers.routes.ref.UserAccountController.createAccountPOST(),
        	fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
	            "email", "bob@example.com",
            	"password", "secret",
            	"alias", "bob2",
            	"phoneNumber", "123456789"))
    	);
    	assertEquals(400, status(result));
    	List<User> users = User.find.where()
    					.eq("email", "bob@example.com")
    					.findList();
    	assertNotNull(users);
		assertEquals(1, users.size());
	}
	
	@Test
	public void createAccountFailurePhoneExists() {
        new User("bob@example.com", "Bob", "secret1", "111111111", USER_PRIVILEGE.regular).save();
	    Result result = callAction(
        	controllers.routes.ref.UserAccountController.createAccountPOST(),
        	fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
	            "email", "alice@example.com",
            	"password", "secret",
            	"alias", "alice",
            	"phoneNumber", "111111111"))
    	);
    	assertEquals(400, status(result));
    	User user = User.find.where()
    					.eq("email", "alice@example.com")
    					.findUnique();
    	assertNull(user);
	}
	
	@Test
	public void createAccountFailureAliasExists() {
        new User("bob@example.com", "Bob", "secret1", "111111111", USER_PRIVILEGE.regular).save();
	    Result result = callAction(
        	controllers.routes.ref.UserAccountController.createAccountPOST(),
        	fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
	            "email", "bob2@example.com",
            	"password", "secret",
            	"alias", "Bob",
            	"phoneNumber", "222222222"))
    	);
    	assertEquals(400, status(result));
    	User user = User.find.where()
    					.eq("email", "bob2@example.com")
    					.findUnique();
    	assertNull(user);
	}
}