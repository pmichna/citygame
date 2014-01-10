package controllers;

import play.*;
import play.data.*;
import play.data.validation.*;
import play.mvc.*;
import static play.data.Form.*;
import views.html.*;
import models.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.avaje.ebean.Ebean;

public class UserAccountController extends Controller {
	private static String adminEmail = play.Play.application().configuration().getString("application.admin");

	public static Result createAccountGET() {
		if (session("email") != null) {
			return redirect(routes.Application.index());
		}
		return ok(createAccount.render(Form.form(RegistrationForm.class)));
	}

	public static Result createAccountPOST() {
		Form<RegistrationForm> signupForm = Form.form(RegistrationForm.class).bindFromRequest();
		if (signupForm.hasErrors()) {
			return badRequest(createAccount.render(signupForm));
		} else {
			String email = signupForm.get().email;
			String password = signupForm.get().password;
			String alias = signupForm.get().alias;
			String phoneNumber = signupForm.get().phoneNumber;
			USER_PRIVILEGE privilege = USER_PRIVILEGE.regular;
			if(email.equals(adminEmail)) {
				privilege = USER_PRIVILEGE.admin;
			}
			new User(email, alias, password, phoneNumber, privilege).save();
			return redirect(routes.Application.loginGET());
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result deleteAccountGET() {
		String email = request().username();
		session().clear();
		User.find.where().eq("email", email).findUnique().delete();
		return redirect(routes.Application.index());
	}

	@Security.Authenticated(Secured.class)
	public static Result editAccountGET() {
		return ok(editAccount.render(Form.form(SaveChangesForm.class), User.find
				.where().eq("email", request().username()).findUnique()));
	}

	public static Result editAccountPOST() {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		Form<SaveChangesForm> editForm = Form.form(SaveChangesForm.class).bindFromRequest();
		if (editForm.hasErrors()) {
			return badRequest(editAccount.render(editForm, user));
		} else {
			String newEmail = editForm.get().email;
			String newPasswordNotHash = editForm.get().password;
			String newAlias = editForm.get().alias;
			String newPhoneNumber = editForm.get().phoneNumber;
			User.editUser(user.email, newEmail, newPasswordNotHash, newAlias,
					newPhoneNumber);
			session().clear();
			session("email", newEmail);
			flash("success", "Your data has been modified");
			return redirect(routes.UserAccountController.viewAccountGET());
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result viewAccountGET() {
		return ok(viewAccount.render(User.find.where()
				.eq("email", request().username()).findUnique()));
	}

	public static class RegistrationForm {
		@Constraints.Required(message = "Email required")
		public String email;
		
		@Constraints.Required(message = "Password required")
		public String password;
		
		@Constraints.Required(message = "Alias required")
		public String alias;
		
		@Constraints.Required(message = "Phone number required (9 digits)")
		@Constraints.Pattern(value = "\\d{9}", message = "Invalid phone number. Must be 9 digits.")
		public String phoneNumber;

		public String validate() {
			final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                             + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
            Matcher emailMatcher = emailPattern.matcher(email);
			
			if (!emailMatcher.matches()) {
				return "Invalid email address";
			}            
			
			// check if email already registered
			if (User.find.where().eq("email", email).findUnique() != null) {
				return "Email already registered";
			}

			// check if alias already registered
			if (User.find.where().eq("alias", alias).findUnique() != null) {
				return "Alias already registered";
			}

			// check if phone number already exists
			if (User.find.where().eq("phoneNumber", phoneNumber).findUnique() != null) {
				return "Phone number already registered";
			}

			return null;
		}
	}

	public static class SaveChangesForm {
		@Constraints.Required(message = "Email required")
		public String email;
		
		public String password;
		
		@Constraints.Required
		public String alias;
		
		@Constraints.Required
		@Constraints.Pattern(value = "\\d{9}", message = "Invalid phone number. Must be 9 digits.")
		public String phoneNumber;

		public String validate() {
			final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                             + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
            Matcher emailMatcher = emailPattern.matcher(email);
			
			if (!emailMatcher.matches()) {
				return "Invalid email address";
			} 
			
			String loggedEmail = session("email");
			User loggedUser = User.find.where().eq("email", loggedEmail)
					.findUnique();
			
			// check if email already registered
			if (!loggedUser.email.equals(email)
				 	&& User.find.where().eq("email", email).findUnique() != null) {
				return "Email already registered";
			}

			// check if alias already registered
			if (!loggedUser.alias.equals(alias)
					&& User.find.where().eq("alias", alias).findUnique() != null) {
				return "Alias already registered";
			}

			// check if phone number already exists
			if (!loggedUser.phoneNumber.equals(phoneNumber)
					&& User.find.where().eq("phoneNumber", phoneNumber)
							.findUnique() != null) {
				return "Phone number already registered";
			}

			return null;
		}
	}
}
