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
		
		if (!Secured.isMemberOf(checkpoint.scenario.id)) {
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
			Checkpoint.addPossibleAnswer(text, checkpointId, user.privilege == USER_PRIVILEGE.admin);
			return redirect(routes.CheckpointController.editCheckpointGET(checkpoint.id));
		}
	}
	
	public static class AnswerForm {
		
		@Required(message = "Answer is required")
		public String text;
	}
}