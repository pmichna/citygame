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
	@Security.Authenticated(Secured.class)
	public static Result createScenarioGET() {
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
			return redirect(routes.ScenarioController.viewMyScenariosGET());
		}
	}
	@Security.Authenticated(Secured.class)
	public static Result viewMyScenariosGET() {
		User user=User.find
				.where().eq("email", session("email")).findUnique();
		return ok(myScenarios.render(user,Scenario.findOwned(user.email)));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewScenarioGET(Long scenarioId) {
		User user=User.find
				.where().eq("email", session("email")).findUnique();
		Scenario scenario = Scenario.find.byId(scenarioId);
		
		return ok(viewScenario.render(user,scenario));
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
		
	}
}
