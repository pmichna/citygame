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
}