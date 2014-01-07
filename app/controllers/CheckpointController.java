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

public class CheckpointController extends Controller{
	@Security.Authenticated(Secured.class)
	public static Result createCheckpointGET(Long scenarioId) {
		User user=User.find
				.where().eq("email", session("email")).findUnique();
		Scenario scenario=Scenario.find.ref(scenarioId);
		return ok(createCheckpoint.render(Form.form(Creation.class),user,scenario));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result createCheckpointPOST() throws ParseException {
		/*
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
		*/
		return null;
	}
	
	/*
	@Security.Authenticated(Secured.class)
	public static Result viewMyScenariosGET() {
		User user=User.find
				.where().eq("email", session("email")).findUnique();
		return ok(myScenarios.render(user,Scenario.findOwned(user.email)));
	}
	*/
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
