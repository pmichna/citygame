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

public class ScenarioController extends Controller {
	private static int scenariosPageSize = 10;
	
	@Security.Authenticated(Secured.class)
	public static Result createScenarioGET() {
		User user=User.find
				.where().eq("email", session("email")).findUnique();
		return ok(createScenario.render(Form.form(Creation.class),user));
	}
	 
	@Security.Authenticated(Secured.class)
	public static Result createScenarioPOST() throws ParseException {
		
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
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
			} else {
				date = formatter.parse(day+"/"+month+"/"+year);
			}
			Scenario.create(name, isPublic, date, user.email);
			return redirect(routes.ScenarioController.viewMyScenariosGET(0));
		}
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewMyScenariosGET(int pageNum) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		int totalPageCount = Scenario.getTotalPageCount(user.email, scenariosPageSize);
		if(pageNum > totalPageCount-1) {
			pageNum = 0;
		}
		return ok(myScenarios.render(
				user,
				Scenario.findInvolvingUser(user.email, scenariosPageSize, pageNum),
				pageNum,
				totalPageCount,
				scenariosPageSize,
				pageNum == 0,
				pageNum == totalPageCount - 1
			)
		);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewScenarioGET(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		Scenario scenario = Scenario.find.byId(scenarioId);
		if(!Secured.isMemberOf(scenarioId)) {
			return redirect(routes.ScenarioController.viewMyScenariosGET(0));
		}
		return ok(viewScenario.render(user, scenario));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result addMemberGET(Long scenarioId) {
		User user=User.find
				.where().eq("email", session("email")).findUnique();
		Scenario scenario = Scenario.find.byId(scenarioId);
		return ok(addMember.render(Form.form(Member.class),user,scenario));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result addMemberPOST(Long scenarioId) {
		User user = User.find.where().eq("email", session("email"))
				.findUnique();
		Form<Member> addForm = Form.form(Member.class).bindFromRequest();
		Scenario scenario = Scenario.find.byId(scenarioId);
		if (addForm.hasErrors()) {
			return badRequest(addMember.render(addForm, user,
					scenario));
		} else {
			User member = User.find.where().eq("alias", addForm.get().alias).findUnique();
			if(!scenario.members.contains(member)){
				scenario.members.add(member);
			}
			return redirect(routes.ScenarioController
					.viewScenarioGET(scenarioId));
		}
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
	
	public static class Member {
		public String alias;

		public String validate() {
			return null;
		}
	}
}
