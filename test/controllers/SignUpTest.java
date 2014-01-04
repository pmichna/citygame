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

public class SignUpTest extends BaseModelTest {
	
	@Test
	public void createAccountSuccess() {
	    Result result = callAction(
        	controllers.routes.ref.Application.createAccount(),
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
	public void createAccountFailureEmailRegex() {
	    Result result = callAction(
        	controllers.routes.ref.Application.createAccount(),
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
        	controllers.routes.ref.Application.createAccount(),
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
        	controllers.routes.ref.Application.createAccount(),
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
        	controllers.routes.ref.Application.createAccount(),
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
        	controllers.routes.ref.Application.createAccount(),
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