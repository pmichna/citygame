package controllers;

import play.*;
import play.data.*;
import play.mvc.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	@Security.Authenticated(Secured.class)
	public static Result createScenarioPOST() throws ParseException {
		
		User user=User.find
				.where().eq("email", session("email")).findUnique();
		
		Form<Creation> createForm = Form.form(Creation.class)
				.bindFromRequest();
		if (createForm.hasErrors()) {
			return badRequest(createScenario.render(createForm, user));
		} else {
			String name = createForm.get().name;
			String day = createForm.get().day;
			String month = createForm.get().month;
			String year = createForm.get().year;
			boolean isPublic = createForm.get().isPublic;
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date date;
			if(day.equals("dd") || month.equals("mm") || year.equals("yyyy")){
				date=null;
			}else{
				date = formatter.parse(day+"/"+month+"/"+year);
			}
			Scenario.create(name, isPublic, date, user.email);
			//later change to redirecting to "my scenarios"
			return redirect(routes.Application.index());
		}
		
		//return redirect(routes.Application.index());
	}
	
	public static class Creation {
		public String name;
		public boolean isPublic;
		public String day;
		public String month;
		public String year;
		
		public String validate(){
			return null;
		}
		
		
		
		/*
		 * 
		 * 
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
