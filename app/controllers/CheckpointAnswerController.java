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

import com.avaje.ebean.Ebean;

import controllers.Application.Login;
import controllers.CheckpointController.Creation;
import controllers.UserAccountController.Registration;
import controllers.UserAccountController.SaveChanges;

public class CheckpointAnswerController extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result createCheckpointAnswerGET(Long checkpointId) {
		User user = User.find.where().eq("email", session("email"))
				.findUnique();
		Checkpoint checkpoint = Checkpoint.find.ref(checkpointId);
		
		if (!Secured.isMemberOf(checkpoint.id)) {
			//return redirect(routes.CheckpointController.viewCheckpointGET(checkpointId));
		}
		return ok(addAnswer.render(Form.form(Creation.class), user, checkpoint));
	}
	
	public static Result createCheckpointAnswerPOST(Long checkpointId) {
		Form<Creation> createForm = Form.form(Creation.class).bindFromRequest();
		User user = User.find
				.where()
				.eq("email", session("email"))
					.findUnique();
		Checkpoint checkpoint = Checkpoint.findCheckpoint(checkpointId);
		if (createForm.hasErrors()) {
			return badRequest(addAnswer.render(createForm,user,checkpoint));
		} else {
			String text = createForm.get().text;
			CheckpointAnswer answer = new CheckpointAnswer(text);
			checkpoint.possibleAnswers.add(answer);
			checkpoint.save();
			return redirect(routes.CheckpointController.viewCheckpointGET(checkpointId));
		}
	}
	
	public static class Creation {
		public String text;
		

		public String validate() {
			return null;
		}

	}
}
