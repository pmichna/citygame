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
	
	@Test
	public void editAliasSuccess() {
		String newAlias = "newAlias";
		Result result = callAction(
			    		controllers.routes.ref.UserAccountController.editAccountPOST(),
			    		fakeRequest()
							.withSession("email", originalEmail)
							.withFormUrlEncodedBody(ImmutableMap.of(
								"email", originalEmail,
								"alias", newAlias,
								"phoneNumber", originalPhoneNumber))
			    	);
		assertEquals(303, status(result));
		// check if session correct
		assertEquals(originalEmail, session(result).get("email"));
		User userModified = User.find.where()
									.eq("email", originalEmail)
									.findUnique();
		assertNotNull(userModified);
		assertEquals(newAlias, userModified.alias);
		assertEquals(originalPhoneNumber, userModified.phoneNumber);
		assertNotNull(User.authenticate(originalEmail, originalPassword));
		
		User userOld = User.find.where()
								.eq("alias", originalAlias)
								.findUnique();
		assertNull(userOld);
	}
	
	@Test
	public void editAliasFailureAlreadyRegistered() {
		new User("newUser@gmail.com", "newUser", "password", "123456789", USER_PRIVILEGE.regular).save();
		String newAlias = "newUser";
		Result result = callAction(
			    		controllers.routes.ref.UserAccountController.editAccountPOST(),
			    		fakeRequest()
							.withSession("email", originalEmail)
							.withFormUrlEncodedBody(ImmutableMap.of(
								"email", originalEmail,
								"alias", newAlias,
								"phoneNumber", originalPhoneNumber))
			    	);
    	assertEquals(400, status(result));
		User user = User.find.where()
								.eq("email", originalEmail)
								.findUnique();
		assertNotNull(user);
		assertEquals(originalAlias, user.alias);
	}
	
	@Test
	public void editPhoneNumberSuccess() {
		String newPhoneNumber = "111444777";
		Result result = callAction(
			    		controllers.routes.ref.UserAccountController.editAccountPOST(),
			    		fakeRequest()
							.withSession("email", originalEmail)
							.withFormUrlEncodedBody(ImmutableMap.of(
								"email", originalEmail,
								"alias", originalAlias,
								"phoneNumber", newPhoneNumber))
			    	);
		assertEquals(303, status(result));
		// check if session correct
		assertEquals(originalEmail, session(result).get("email"));
		User userModified = User.find.where()
									.eq("email", originalEmail)
									.findUnique();
		assertNotNull(userModified);
		assertEquals(originalAlias, userModified.alias);
		assertEquals(newPhoneNumber, userModified.phoneNumber);
		assertNotNull(User.authenticate(originalEmail, originalPassword));
	}
	
	@Test
	public void editPhoneNumberFailureRegex() {
		String newPhoneNumber = "123";
		Result result = callAction(
			    		controllers.routes.ref.UserAccountController.editAccountPOST(),
			    		fakeRequest()
							.withSession("email", originalEmail)
							.withFormUrlEncodedBody(ImmutableMap.of(
								"email", originalEmail,
								"alias", originalAlias,
								"phoneNumber", newPhoneNumber))
			    	);
    	assertEquals(400, status(result));
		User user = User.find.where()
									.eq("email", originalEmail)
									.findUnique();
		assertNotNull(user);		
		assertEquals(originalPhoneNumber, user.phoneNumber);
	}
	
	@Test
	public void editPhoneNumberFailureAlreadyRegistered() {
		String newPhoneNumber = "123456789";
		new User("newUser@gmail.com", "newUser", "password", newPhoneNumber, USER_PRIVILEGE.regular).save();
		Result result = callAction(
			    		controllers.routes.ref.UserAccountController.editAccountPOST(),
			    		fakeRequest()
							.withSession("email", originalEmail)
							.withFormUrlEncodedBody(ImmutableMap.of(
								"email", originalEmail,
								"alias", originalAlias,
								"phoneNumber", newPhoneNumber))
			    	);
    	assertEquals(400, status(result));
		User user = User.find.where()
								.eq("email", originalEmail)
								.findUnique();
		assertNotNull(user);
		assertEquals(originalPhoneNumber, user.phoneNumber);
	}
	
	@Test
	public void editPasswordSuccess() {
		String newPassword = "newPassword";
		Result result = callAction(
			    		controllers.routes.ref.UserAccountController.editAccountPOST(),
			    		fakeRequest()
							.withSession("email", originalEmail)
							.withFormUrlEncodedBody(ImmutableMap.of(
								"email", originalEmail,
								"alias", originalAlias,
								"phoneNumber", originalPhoneNumber,
								"password", newPassword))
			    	);
		assertEquals(303, status(result));
		assertNotNull(User.authenticate(originalEmail, newPassword));
		assertNull(User.authenticate(originalEmail, originalPassword));
	}
	/* add test for all */
	
}