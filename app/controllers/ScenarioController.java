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
import play.data.validation.Constraints.*;
import java.util.List;

import com.avaje.ebean.Ebean;

public class ScenarioController extends Controller {
	private static int pageSize = 10;
	
	@Security.Authenticated(Secured.class)
	public static Result createScenarioGET() {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		return ok(createScenario.render(Form.form(ScenarioForm.class), user));
	}
	 
	@Security.Authenticated(Secured.class)
	public static Result createScenarioPOST() throws ParseException {
		
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		Form<ScenarioForm> createForm = Form.form(ScenarioForm.class)
				.bindFromRequest();
		if (createForm.hasErrors()) {
			return badRequest(createScenario.render(createForm, user));
		} else {
			String name = createForm.get().name;
			String day = createForm.get().day;
			String month = createForm.get().month;
			String year = createForm.get().year;
			Boolean isPublic = createForm.get().isPublic;
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date date;
			if(day.equals("dd") || month.equals("mm") || year.equals("yyyy")){
				date = null;
			} else {
				date = new Date(formatter.parse(day + "/" + month + "/" + year).getTime());
			}
			Scenario.create(name, isPublic, date, user.email);
			return redirect(routes.ScenarioController.viewPrivateScenariosGET(0));
		}
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewPrivateScenariosGET(int pageNum) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		int totalPageCount = Scenario.getTotalPrivatePageCount(user.email, pageSize);
		if(pageNum > totalPageCount-1) {
			pageNum = 0;
		}
		return ok(viewPrivateScenarios.render(
				user,
				Scenario.findInvolvingUser(user.email, pageSize, pageNum),
				pageNum,
				totalPageCount,
				pageSize,
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
		int totalPageCount = Scenario.getTotalPublicNotExpiredPageCount(pageSize, new Date(System.currentTimeMillis()));
		if(pageNum > totalPageCount - 1) {
			pageNum = 0;
		}
		return ok(viewPublicScenarios.render(
				user,
				Scenario.findPublicAcceptedNotExpired(new Date(System.currentTimeMillis()), pageSize, pageNum),
				pageNum,
				totalPageCount,
				pageSize,
				pageNum == 0,
				pageNum == totalPageCount - 1
			)
		);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewPrivateScenarioGET(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		Boolean isAdminMode = (user.privilege == USER_PRIVILEGE.admin);
		Scenario scenario = Scenario.find.byId(scenarioId);
		if(!Secured.isMemberOf(scenarioId) && !isAdminMode) {
			return redirect(routes.ScenarioController.viewPrivateScenariosGET(0));
		}
		return ok(viewPrivateScenario.render(user, scenario, isAdminMode));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewPublicScenarioGET(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		Scenario scenario = Scenario.find.byId(scenarioId);
		return ok(viewPublicScenario.render(user, scenario));
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
			return redirect(routes.ScenarioController.viewPrivateScenarioGET(scenarioId));
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
		return redirect(routes.ScenarioController.viewPrivateScenarioGET(scenario.id));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result editScenarioGET(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		if(!Secured.isMemberOf(scenarioId)) {
			return redirect(routes.ScenarioController.viewPrivateScenariosGET(0));
		}
		Scenario scenario = Scenario.find.ref(scenarioId);
		return ok(editScenario.render(Form.form(ScenarioForm.class), user, scenario));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result editScenarioPOST(Long scenarioId) throws ParseException {
		
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		Form<ScenarioForm> editForm = Form.form(ScenarioForm.class)
											.bindFromRequest();
		if (editForm.hasErrors()) {
			return badRequest(editScenario.render(editForm, user, Scenario.find.ref(scenarioId)));
		} else {
			String name = editForm.get().name;
			String day = editForm.get().day;
			String month = editForm.get().month;
			String year = editForm.get().year;
			Boolean isPublic = editForm.get().isPublic;
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date date;
			if(day.equals("dd") || month.equals("mm") || year.equals("yyyy")){
				date = null;
			} else {
				date = new Date(formatter.parse(day + "/" + month + "/" + year).getTime());
			}
			Scenario.edit(scenarioId, name, isPublic, date);
			return redirect(routes.ScenarioController.viewPrivateScenarioGET(scenarioId));
		}
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewScenariosToAcceptGET(int pageNum) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		if(user.privilege != USER_PRIVILEGE.admin) {
			return badRequest(index.render(user));
		}
		List<Scenario> scenarios = Scenario.findToAccept(new Date(System.currentTimeMillis()), pageSize, pageNum);
		int totalPageCount = Scenario.getTotalToAcceptPageCount(new Date(System.currentTimeMillis()), pageSize);
		return ok(viewScenariosToAccept.render(
			user,
			scenarios,
			pageNum,
			totalPageCount,
			pageSize,
			pageNum == 0,
			pageNum == totalPageCount - 1					
		));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result acceptScenarioGET(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		if(user.privilege != USER_PRIVILEGE.admin) {
			return badRequest(index.render(user));
		}
		Scenario scenario = Scenario.find.ref(scenarioId);
		if(scenario != null) {
			scenario.isAccepted = true;
			scenario.save();
		}
		return redirect(routes.ScenarioController.viewScenariosToAcceptGET(0));
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
