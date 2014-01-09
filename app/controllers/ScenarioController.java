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
import java.sql.Date;
import play.data.constraints.*;

import com.avaje.ebean.Ebean;

public class ScenarioController extends Controller {
	private static int scenariosPageSize = 10;
	
	@Security.Authenticated(Secured.class)
	public static Result createScenarioGET() {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		return ok(createScenario.render(Form.form(Creation.class), user));
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
				date = null;
			} else {
				date = new Date(formatter.parse(day + "/" + month + "/" + year).getTime());
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
	public static Result viewPublicScenariosGET(int pageNum) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		int totalPageCount = Scenario.getTotalPageCount(user.email, scenariosPageSize);
		if(pageNum > totalPageCount-1) {
			pageNum = 0;
		}
		return ok(viewPublicScenarios.render(
				user,
				Scenario.findAvailable(user.email, scenariosPageSize, pageNum),
				pageNum,
				totalPageCount,
				scenariosPageSize,
				pageNum == 0,
				pageNum == totalPageCount - 1
			)
		);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewMyScenarioGET(Long scenarioId) {
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
			User member = User.find.where().eq("email", addForm.get().email).findUnique();
			if(!scenario.members.contains(member)){
				scenario.members.add(member);
				scenario.save();
			}
			return redirect(routes.ScenarioController
					.viewMyScenarioGET(scenarioId));
		}
	}
	@Security.Authenticated(Secured.class)
	public static Result removeMemberGET(Long scenarioId, Long memberId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		User member = User.find.where().eq("id", memberId).findUnique();
		Scenario scenario = Scenario.find.byId(scenarioId);
		if (scenario == null) {
			return redirect(routes.Application.index());
		}
		if(member.id == scenario.owner.id){
			return redirect(routes.Application.index());
		}
		scenario.members.remove(member);
		scenario.save();
		return redirect(routes.ScenarioController.viewMyScenarioGET(scenario.id));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result editScenarioGET(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		if(!Secured.isMemberOf(scenariodId)) {
			return redirect(routes.ScenarioController.viewMyScenariosGET(0));
		}
		return ok(editScenario.render(Form.form(ScenarioForm.class), user));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result editScenarioPOST(Long scenarioId) throws ParseException {
		
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		Form<ScenarioForm> editForm = Form.form(ScenarioForm.class)
											.bindFromRequest();
		if (createForm.hasErrors()) {
			return badRequest(editScenario.render(editForm, user));
		} else {
			String name = createForm.get().name;
			String day = createForm.get().day;
			String month = createForm.get().month;
			String year = createForm.get().year;
			boolean isPublic = createForm.get().isPublic;
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date date;
			if(day.equals("dd") || month.equals("mm") || year.equals("yyyy")){
				date = null;
			} else {
				date = new Date(formatter.parse(day + "/" + month + "/" + year).getTime());
			}
			Scenario.edit(scenarioId, name, isPublic, date, user.email);
			return redirect(routes.ScenarioController.viewMyScenarioGET(scenarioId));
		}
	}
	
	public static class ScenarioForm {
		
		@Required(message = "Name is required")
		public String name;
		public Boolean isPublic;
		public String day;
		public String month;
		public String year;
	}
	
	public static class Member {
		
		@Email
		public String email;
	}
}
