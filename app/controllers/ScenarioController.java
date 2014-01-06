package controllers;

import play.*;
import play.data.*;
import play.mvc.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import controllers.UserAccountController.Registration;
import controllers.UserAccountController.SaveChanges;

public class ScenarioController extends Controller{
	public static Result createScenarioGET() {
		if (session("email") == null) {
			return redirect(routes.Application.loginGET());
		}
		User user=User.find
				.where().eq("email", session("email")).findUnique();
		return ok(createScenario.render(Form.form(Creation.class),user));
	}
	
	public static Result createScenarioPOST() {
		/*
		User user = User.find.where().eq("email", session("email"))
				.findUnique();
		Form<SaveChanges> editForm = Form.form(SaveChanges.class)
				.bindFromRequest();
		if (editForm.hasErrors()) {
			return badRequest(editAccount.render(editForm, user));
		} else {
			String newEmail = editForm.get().email;
			String newPasswordNotHash = editForm.get().password;
			String newAlias = editForm.get().alias;
			String newPhoneNumber = editForm.get().phoneNumber;
			User.editUser(user.email, newEmail, newPasswordNotHash, newAlias,
					newPhoneNumber);
			return redirect(routes.Application.index());
		}
		*/
		return redirect(routes.Application.index());
	}
	
	public static class Creation {
		/*
		public String email;
		public String password;
		public String alias;
		public String phoneNumber;

		public String validate() {
			final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
			final String PHONE_PATTERN = "\\d{9}";
			Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
			Matcher emailMatcher = emailPattern.matcher(email);
			Pattern phonePattern = Pattern.compile(PHONE_PATTERN);
			Matcher phoneMatcher = phonePattern.matcher(phoneNumber);

			if (!emailMatcher.matches()) {
				return "Invalid  email address";
			}

			// check if email already registered
			if (User.find.where().eq("email", email).findUnique() != null) {
				return "Email already registered";
			}

			// check if alias already registered
			if (User.find.where().eq("alias", alias).findUnique() != null) {
				return "Alias already registered";
			}

			// password validation
			if (password == null || password.equals("")) {
				return "Password cannot be empty";
			}

			// phone number validation
			if (!phoneMatcher.matches()) {
				return "Invalid phone number. Must be: xxxxxxxxx, where x - digit";
			}

			// check if phone number already exists
			if (User.find.where().eq("phoneNumber", phoneNumber).findUnique() != null) {
				return "Phone number already registered";
			}

			return null;
		}
		*/
	}
}
