package controllers;

import play.*;
import play.data.*;
import play.mvc.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

public class Application extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {

        return ok(index.render());
    }

    public static Result login() {
        return ok(
            login.render(form(Login.class))
        );
    }

    public static Result signup() {
    	return ok(
    		signup.render(form(Registration.class))
    	);
    }

    public static Result createAccount() {
    	Form<Registration> signupForm = form(Registration.class).bindFromRequest();
    	String email = signupForm.get().email;
    	String password = signupForm.get().password;
    	String alias = signupForm.get().alias;
    	String phoneNumber = signupForm.get().phoneNumber;
    	new User(email, alias, password, phoneNumber, USER_PRIVILEGE.regular).save();
    	return redirect(routes.Application.index());
    }

    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if(loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(
                routes.Application.index()
            );
        }
    }

    public static class Registration {
    	public String email;
    	public String password;
    	public String alias;
    	public String phoneNumber;
    }
    

    public static class Login {
        public String email;
        public String password;

        public String validate() {
            if (User.authenticate(email, password) == null) {
                return "Invalid email or password";
            }
            return null;
        }
    }
}
