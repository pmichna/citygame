package controllers;

import play.*;
import play.data.*;
import play.mvc.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render(User.find.where().eq("email", request().username()).findUnique()));
    }

    public static Result login() {
		if(session("email") != null) {
			return redirect(routes.Application.index());
		}
        return ok(
            login.render(Form.form(Login.class))
        );
    }

    public static Result signup() {
		if(session("email") != null) {
			return redirect(routes.Application.index());
		}
    	return ok(
    		signup.render(Form.form(Registration.class))
    	);
    }

    public static Result logout() {
        session().clear();
        flash("success","You've been logged out");
        return redirect(
            routes.Application.login()
        );
    }

    public static Result createAccount() {
    	Form<Registration> signupForm = Form.form(Registration.class).bindFromRequest();
		if (signupForm.hasErrors()) {
		        return badRequest(signup.render(signupForm));
		} else {
	    	String email = signupForm.get().email;
	    	String password = signupForm.get().password;
	    	String alias = signupForm.get().alias;
	    	String phoneNumber = signupForm.get().phoneNumber;
	    	new User(email, alias, password, phoneNumber, USER_PRIVILEGE.regular).save();
	    	return redirect(routes.Application.index());
		}
    }

    @Security.Authenticated(Secured.class)
    public static Result editAccount() {
		
        return ok(
            editAccount.render(
            		Form.form(SaveChanges.class),
            		User.find.where().eq("email", request().username()).findUnique()
            		)
        );
    }
    
    public static Result saveAccountChanges(){
    	
    	String t=request().username();
    	Form<SaveChanges> editForm = Form.form(SaveChanges.class).bindFromRequest();
    	return ok(test.render("karolk"));/*
    	User user = User.find.where().eq("email", request().username()).findUnique();
		
    	if (editForm.hasErrors()) {
		        return badRequest(editAccount.render(editForm,user));
		} else {
	    	String newEmail = editForm.get().email;
	    	String newPasswordNotHash = editForm.get().password;
	    	String newAlias = editForm.get().alias;
	    	String newPhoneNumber = editForm.get().phoneNumber;
	    	User.editUser(user.email, newEmail, newPasswordNotHash, newAlias, newPhoneNumber);
	    	return redirect(routes.Application.index());
		}*/
    	
    	//return redirect(routes.Application.index());
    }
    
    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
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
		
		public String validate() { 
			final String EMAIL_PATTERN = 
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
			final String PHONE_PATTERN = "\\d{9}";
			Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
			Matcher emailMatcher = emailPattern.matcher(email);
			Pattern phonePattern = Pattern.compile(PHONE_PATTERN);
			Matcher phoneMatcher = phonePattern.matcher(phoneNumber);
			String loggedEmail = session("email");
			User loggedUser = User.find.where().eq("email", loggedEmail).findUnique();
			
			
			if(!emailMatcher.matches()) {
				return "Invalid  email address";
			}
			
			// check if email already registered
			if(!loggedEmail.equals(email) && User.find.where().eq("email", email).findUnique() != null) {
				return "Email already registered";
			}
			
			// check if alias already registered
			if(!loggedUser.alias.equals(alias) && User.find.where().eq("alias", alias).findUnique() != null) {
				return "Alias already registered";
			}
			
			// password validation
			if(password==null || password.equals("")){
				return "Password cannot be empty";
			}
			
			// phone number validation
			if(!phoneMatcher.matches()) {
				return "Invalid phone number. Must be: xxxxxxxxx, where x - digit";
			}
			
			// check if phone number already exists
			if(!loggedUser.phoneNumber.equals(phoneNumber) && User.find.where().eq("phoneNumber", phoneNumber).findUnique() != null) {
				return "Phone number already registered";
			}
			
			return null;
		}
    }
    
    public static class SaveChanges {
    	public String email;
    	public String password;
    	public String alias;
    	public String phoneNumber;
		
		public String validate() { 
			return "test";
			/*
			 * final String EMAIL_PATTERN =
			 * "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
			 * "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"; final String
			 * PHONE_PATTERN = "\\d{9}"; Pattern emailPattern =
			 * Pattern.compile(EMAIL_PATTERN); Matcher emailMatcher =
			 * emailPattern.matcher(email); Pattern phonePattern =
			 * Pattern.compile(PHONE_PATTERN); Matcher phoneMatcher =
			 * phonePattern.matcher(phoneNumber);
			 * 
			 * if(!emailMatcher.matches()) { return "Invalid  email address"; }
			 * 
			 * 
			 * // check if alias already registered
			 * if(User.find.where().eq("alias", alias).findUnique() != null) {
			 * return "Alias already registered"; }
			 * 
			 * // phone number validation if(!phoneMatcher.matches()) { return
			 * "Invalid phone number. Must be: xxxxxxxxx, where x - digit"; }
			 * 
			 * // check if phone number already exists
			 * if(User.find.where().eq("phoneNumber", phoneNumber).findUnique()
			 * != null) { return "Phone number already registered"; }
			 */
			//return null;
		}
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
