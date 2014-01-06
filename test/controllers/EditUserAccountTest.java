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

public class EditUserAccountTest extends BaseControllerTest {
	private String originalEmail = "test@test.pl";
	private String originalAlias = "test_alias";
	private String originalPassword = "secret";
	private String originalPhoneNumber = "987654321";
	
    @Before
    public void setUp() {
		new User(originalEmail, originalAlias, originalPassword, originalPhoneNumber, USER_PRIVILEGE.regular).save();
	}
			
	@Test
	public void editEmailSuccess() {
		String newEmail = "testNew@test.pl";
		Result result = callAction(
			    		controllers.routes.ref.UserAccountController.editAccountPOST(),
			    		fakeRequest()
							.withSession("email", originalEmail)
							.withFormUrlEncodedBody(ImmutableMap.of(
								"email", newEmail,
								"alias", originalAlias,
								"phoneNumber", originalPhoneNumber))
			    	);
		assertEquals(303, status(result));
		// check if session correct
		assertEquals(newEmail, session(result).get("email"));
		User userModified = User.find.where()
									.eq("email", newEmail)
									.findUnique();
		assertNotNull(userModified);
		assertEquals(originalAlias, userModified.alias);
		assertEquals(originalPhoneNumber, userModified.phoneNumber);
		assertNotNull(User.authenticate(newEmail, originalPassword));
		
		User userOld = User.find.where()
								.eq("email", originalEmail)
								.findUnique();
		assertNull(userOld);
	}
	
	@Test
	public void editEmailFailureRegex() {
		String newEmail = "testNew@test";
		Result result = callAction(
			    		controllers.routes.ref.UserAccountController.editAccountPOST(),
			    		fakeRequest()
							.withSession("email", originalEmail)
							.withFormUrlEncodedBody(ImmutableMap.of(
								"email", newEmail,
								"alias", originalAlias,
								"phoneNumber", originalPhoneNumber))
			    	);
    	assertEquals(400, status(result));
		User userModified = User.find.where()
									.eq("email", newEmail)
									.findUnique();
		assertNull(userModified);		
		User userOld = User.find.where()
								.eq("email", originalEmail)
								.findUnique();
		assertNotNull(userOld);
	}
	
	@Test
	public void editEmailFailureAlreadyRegistered() {
		new User("newUser@gmail.com", "newUser", "password", "123456789", USER_PRIVILEGE.regular).save();
		String newEmail = "newUser@gmail.com";
		Result result = callAction(
			    		controllers.routes.ref.UserAccountController.editAccountPOST(),
			    		fakeRequest()
							.withSession("email", originalEmail)
							.withFormUrlEncodedBody(ImmutableMap.of(
								"email", newEmail,
								"alias", originalAlias,
								"phoneNumber", originalPhoneNumber))
			    	);
    	assertEquals(400, status(result));
	}
}