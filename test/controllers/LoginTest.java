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

public class LoginTest extends BaseControllerTest {
    @Before
    public void setUp() {
        new User("test@test.pl", "test_alias", "password", "987654321", USER_PRIVILEGE.regular).save();
    }

    @Test
    public void authenticateSuccess() {
    	Result result = callAction(
    		controllers.routes.ref.Application.authenticate(),
    		fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
    			"email", "test@test.pl",
    			"password", "password"))
    	);
    	assertEquals(303, status(result));
    	assertEquals("test@test.pl", session(result).get("email"));
    }

    @Test
	public void authenticateFailure() {
	    Result result = callAction(
        	controllers.routes.ref.Application.authenticate(),
        	fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
	            "email", "bob@example.com",
            	"password", "badpassword"))
    		);
    	assertEquals(400, status(result));
    	assertNull(session(result).get("email"));
	}
	
	@Test
	public void authenticated() {
	    Result result = callAction(
	        controllers.routes.ref.Application.index(),
	        fakeRequest().withSession("email", "test@test.pl")
	    );
	    assertEquals(200, status(result));
	}  
	
	@Test
	public void notAuthenticated() {
	    Result result = callAction(
	        controllers.routes.ref.Application.index(),
	        fakeRequest()
	    );
	    assertEquals(303, status(result));
	    assertEquals("/login", header("Location", result));
	}
}