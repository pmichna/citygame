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

	public static Result index() {
		User user = null;
		if(session("email") != null) {
			user = User.find.where().eq("email", session("email")).findUnique();
			if(user==null)
				session().clear();
		}
		return ok(index.render(user));
	}

	public static Result loginGET() {
		if (session("email") != null) {
			return redirect(routes.Application.index());
		}
		return ok(login.render(Form.form(Login.class)));
	}
	
	public static Result loginPOST() {
		Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
		if (loginForm.hasErrors()) {
			return badRequest(login.render(loginForm));
		} else {
			session().clear();
			session("email", loginForm.get().email);
			return redirect(routes.Application.index());
		}
	}

	public static Result logoutGET() {
		session().clear();
		flash("success", "You've been logged out");
		return redirect(routes.Application.loginGET());
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
