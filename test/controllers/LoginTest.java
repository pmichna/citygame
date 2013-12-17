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
}