package controllers;

import play.*;
import play.data.*;
import play.mvc.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

import play.data.validation.Constraints.*;

public class CheckpointAnswerController extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result createCheckpointAnswerGET(Long checkpointId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		Checkpoint checkpoint = Checkpoint.find.ref(checkpointId);
		
		if (!Secured.isMemberOf(checkpoint.scenario.id) || (checkpoint.scenario.editedBy != null && checkpoint.scenario.editedBy.id != user.id)) {
			return redirect(routes.Application.index());
		}
		return ok(addAnswer.render(Form.form(AnswerForm.class), user, checkpoint));
	}
	
	public static Result createCheckpointAnswerPOST(Long checkpointId) {
		Form<AnswerForm> createForm = Form.form(AnswerForm.class).bindFromRequest();
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		Checkpoint checkpoint = Checkpoint.find.ref(checkpointId);
		if (createForm.hasErrors()) {
			return badRequest(addAnswer.render(createForm, user, checkpoint));
		} else {
			String text = createForm.get().text;
			if(Checkpoint.hasAnswer(checkpoint.id, text))	 {
				createForm.reject("Answer already exists");
				return badRequest(addAnswer.render(createForm, user, checkpoint));
			}
			Checkpoint.addPossibleAnswer(text, checkpointId, user.privilege == USER_PRIVILEGE.admin);
			return redirect(routes.CheckpointController.editCheckpointGET(checkpoint.id));
		}
	}
	
	@Security.Authenticated(Secured.class)
	public static Result deleteCheckpointAnswerGET(Long answerId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();		
		Checkpoint checkpoint = Checkpoint.find.where().eq("possibleAnswers.id", answerId).findUnique();
		if(!checkpoint.scenario.members.contains(user)) {
			return redirect(routes.Application.index());
		}
		if(checkpoint.scenario.editedBy != null && checkpoint.scenario.editedBy.id != user.id) {
			return redirect(routes.ScenarioController.editScenarioGET(checkpoint.scenario.id));
		}
		CheckpointAnswer.find.byId(answerId).delete();
		return redirect(routes.CheckpointController.editCheckpointGET(checkpoint.id));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result editCheckpointAnswerGET(Long checkpointAnswerId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();	
		
		CheckpointAnswer checkpointAnswer = CheckpointAnswer.find.ref(checkpointAnswerId);
		Checkpoint checkpoint = Checkpoint.find.where().eq("possibleAnswers.id", checkpointAnswerId).findUnique();
		if(checkpointAnswer == null || !Secured.isMemberOf(checkpoint.scenario.id) ||
			(checkpoint.scenario.editedBy != null && checkpoint.scenario.editedBy.id != user.id)) {
			return redirect(routes.Application.index());
		}
		return ok(editAnswer.render(
			Form.form(AnswerForm.class),
			user,
			checkpointAnswer)
		);		
	}
	
	public static Result editCheckpointAnswerPOST(Long checkpointAnswerId) {
		Form<AnswerForm> editForm = Form.form(AnswerForm.class).bindFromRequest();
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		Checkpoint checkpoint = Checkpoint.find.where().eq("possibleAnswers.id", checkpointAnswerId).findUnique();
		CheckpointAnswer checkpointAnswer = CheckpointAnswer.find.ref(checkpointAnswerId);
		if (editForm.hasErrors()) {
			return badRequest(editAnswer.render(editForm, user, checkpointAnswer));
		} else {
			String text = editForm.get().text;
			if(Checkpoint.hasAnswer(checkpoint.id, text)) {
				editForm.reject("Answer already exists");
				return badRequest(editAnswer.render(editForm, user, checkpointAnswer));
			}
			CheckpointAnswer.edit(checkpointAnswerId, text, user.privilege == USER_PRIVILEGE.admin);
			return redirect(routes.CheckpointController.editCheckpointGET(checkpoint.id));
		}
	}
	
	public static class AnswerForm {
		
		@Required(message = "Answer is required")
		public String text;
	}
}