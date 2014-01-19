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
import java.util.ArrayList;
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
			if(isPublic == null) {
				isPublic = false;
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date date;
			if(day.equals("dd") || month.equals("mm") || year.equals("yyyy")){
				date = null;
			} else {
				date = new Date(formatter.parse(day + "/" + month + "/" + year).getTime());
			}
			Scenario newScenario = Scenario.create(name, isPublic, date, user.email, user.privilege == USER_PRIVILEGE.admin);
			return redirect(routes.ScenarioController.editScenarioGET(newScenario.id));
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
				null
			)
		);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewPrivateScenariosSearchGET(String searchTerm,int pageNum) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		int totalPageCount = Scenario.getTotalPrivateSearchPageCount(searchTerm,user.email, pageSize);
		if(pageNum > totalPageCount-1) {
			pageNum = 0;
		}
		return ok(viewPrivateScenarios.render(
				user,
				Scenario.findInvolvingUserSearch(searchTerm,user.email, pageSize, pageNum),
				pageNum,
				totalPageCount,
				pageSize,
				searchTerm
			)
		);
	}
	@Security.Authenticated(Secured.class)
	public static Result viewPrivateScenariosSearchPOST() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String searchTerm=requestData.get("searchTerm");
		if(searchTerm==null || searchTerm.trim().length()==0)
			return redirect(routes.ScenarioController.viewPrivateScenariosGET(0));
		else
			return redirect(routes.ScenarioController.viewPrivateScenariosSearchGET(searchTerm,0));
		
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewPublicScenariosSearchGET(String searchTerm,int pageNum) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		int totalPageCount = Scenario.getTotalPublicAcceptedNotExpiredSearchPageCount(searchTerm, pageSize,new Date(System.currentTimeMillis()));
		if(pageNum > totalPageCount-1) {
			pageNum = 0;
		}
		return ok(viewPublicScenarios.render(
				user,
				Scenario.findPublicAcceptedNotExpiredSearch(searchTerm,new Date(System.currentTimeMillis()), pageSize, pageNum),
				pageNum,
				totalPageCount,
				pageSize,
				searchTerm
			)
		);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewPublicScenariosSearchPOST() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String searchTerm=requestData.get("searchTerm");
		if(searchTerm==null || searchTerm.trim().length()==0)
			return redirect(routes.ScenarioController.viewPublicScenariosGET(0));
		else
			return redirect(routes.ScenarioController.viewPublicScenariosSearchGET(searchTerm,0));
		
	}
	
	
	@Security.Authenticated(Secured.class)
	public static Result viewPublicScenariosGET(int pageNum) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		int totalPageCount = Scenario.getTotalPublicAcceptedNotExpiredPageCount(pageSize, new Date(System.currentTimeMillis()));
		if(pageNum > totalPageCount - 1) {
			pageNum = 0;
		}
		return ok(viewPublicScenarios.render(
				user,
				Scenario.findPublicAcceptedNotExpired(new Date(System.currentTimeMillis()), pageSize, pageNum),
				pageNum,
				totalPageCount,
				pageSize,
				null
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
		List<Checkpoint> sortedCheckpoints = Checkpoint.find.where().eq("scenario.id", scenario.id).orderBy("checkpoint_index asc").findList();
		if(!Secured.isMemberOf(scenarioId) && !isAdminMode) {
			return redirect(routes.ScenarioController.viewPrivateScenariosGET(0));
		}
		return ok(viewPrivateScenario.render(user, scenario, sortedCheckpoints, isAdminMode));
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
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		Scenario scenario = Scenario.find.ref(scenarioId);
		if(scenario.owner.id != user.id) {
			return redirect(routes.Application.index());
		}
		return ok(addMember.render(Form.form(Member.class),user,scenario));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result addMemberPOST(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		Scenario scenario = Scenario.find.ref(scenarioId);
		if(scenario.owner.id != user.id) {
			return redirect(routes.Application.index());
		}
		
		Form<Member> addForm = Form.form(Member.class).bindFromRequest();

		if (addForm.hasErrors()) {
			return badRequest(addMember.render(addForm, user,
					scenario));
		} else {
			User member = User.find.where().eq("email", addForm.get().email).findUnique();
			if(member == null) {
				addForm.reject("User doesn't exist");
				return ok(addMember.render(
					addForm,
					user,
					scenario
				));
			}
			if(scenario.members.contains(member)) {
				addForm.reject("User already added");
				return ok(addMember.render(
					addForm,
					user,
					scenario
				));
			}
			scenario.members.add(member);
			scenario.save();
			return redirect(routes.ScenarioController.editScenarioGET(scenarioId));
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
		if(member.id == scenario.owner.id) {
			return redirect(routes.Application.index());
		}
		if(scenario.owner.id != user.id) {
			return redirect(routes.Application.index());
		}
		scenario.members.remove(member);
		if(scenario.editedBy != null && scenario.editedBy.id == member.id) {
			scenario.editedBy = null;
		}
		scenario.save();
		return redirect(routes.ScenarioController.editScenarioGET(scenario.id));
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
		List<Checkpoint> sortedCheckpoints = Checkpoint.find.where().eq("scenario.id", scenario.id).orderBy("checkpoint_index asc").findList();
		if(scenario.editedBy != null && !scenario.editedBy.email.equals(user.email)) {
			return ok(editScenario.render(Form.form(ScenarioForm.class), user, scenario, sortedCheckpoints, true));
		}
		scenario.editedBy = user;
		scenario.save();
		return ok(editScenario.render(Form.form(ScenarioForm.class), user, scenario, sortedCheckpoints, false));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result cancelEditGET(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		Scenario scenario = Scenario.find.ref(scenarioId);
		if(scenario == null || !scenario.editedBy.email.equals(user.email)) {
			return redirect(routes.ScenarioController.viewPrivateScenariosGET(0));
		}
		scenario.editedBy = null;
		scenario.save();
		return redirect(routes.ScenarioController.viewPrivateScenariosGET(0));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result editScenarioPOST(Long scenarioId) throws ParseException {
		
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		Form<ScenarioForm> editForm = Form.form(ScenarioForm.class)
											.bindFromRequest();
		List<Checkpoint> sortedCheckpoints = Checkpoint.find.where().eq("scenario.id", scenarioId).orderBy("checkpoint_index asc").findList();
		if (editForm.hasErrors()) {
			return badRequest(editScenario.render(editForm, user, Scenario.find.ref(scenarioId), sortedCheckpoints, false));
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
			Scenario.edit(scenarioId, name, isPublic, date, user.privilege == USER_PRIVILEGE.admin);
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
			pageSize
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
	
	@Security.Authenticated(Secured.class)
	public static Result deleteScenarioGET(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		Scenario scenario = Scenario.find.ref(scenarioId);
		if(scenario.owner.id != user.id) {
			return redirect(routes.Application.index());
		}
		if(Game.find.where().eq("scenario.id", scenarioId).eq("status", GAME_STATUS.playing).findRowCount() > 0) {
			flash("error", "Sorry, someone is playing this scenario right now. Wait until he finishes.");
			return redirect(routes.ScenarioController.viewPrivateScenariosGET(0));
		}
		List<Game> games = Game.find.where().eq("scenario.id", scenarioId).findList();
		for(Game g: games) {
			g.delete();
		}
		List<GameEvent> events = GameEvent.find.where().eq("scenario.id", scenarioId).findList();
		for(GameEvent ge: events) {
			ge.delete();
		}
		scenario.delete();
		return redirect(routes.ScenarioController.viewPrivateScenariosGET(0));
	}

	@Security.Authenticated(Secured.class)
	public static Result viewScenarioRankingGET(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		Scenario scenario = Scenario.find.ref(scenarioId);
		if(scenario == null || (!scenario.isPublic && !scenario.members.contains(user))) {
			return redirect(routes.Application.index());
		}
		List<GameAggregate> games = Game.getRankingGames(scenarioId);
		return ok(viewScenarioRanking.render(user, games, scenario));
	}
	
	public static class ScenarioForm {
		
		@Required(message = "Name is required")
		public String name;
		public Boolean isPublic;
		public String day;
		public String month;
		public String year;
		
		public String validate() {
			if(day.equals("dd") || month.equals("mm") || year.equals("yyyy")) {
				return null;
			}
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    sdf.setLenient(false);
		    java.util.Date date = sdf.parse(year + "-" + month + "-" + day, new java.text.ParsePosition(0));
			if(date == null){
				return "Wrong date";
			}
			else {
				return null;
			}
		}
	}
	
	public static class SearchForm{
		public String searchTerm;
	}
	
	public static class Member {
		
		@Email
		public String email;
	}
}
