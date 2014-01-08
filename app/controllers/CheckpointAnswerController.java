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
import controllers.UserAccountController.Registration;
import controllers.UserAccountController.SaveChanges;

public class CheckpointAnswerController extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result createCheckpointAnswerGET(Long checkpointId) {
		User user = User.find.where().eq("email", session("email"))
				.findUnique();
		Checkpoint checkpoint = Checkpoint.find.ref(checkpointId);
		if (!Secured.isMemberOf(checkpoint.id)) {
			return redirect(routes.CheckpointController.viewCheckpointGET(checkpointId));
		}
		return redirect(routes.CheckpointController.viewCheckpointGET(checkpointId));
		//return ok(addMember.render(Form.form(Creation.class), user,
		//		checkpoint));
	}
	
	public static Result createCheckpointAnswerPOST(Long checkpointId) {
		Form<Creation> createForm = Form.form(Creation.class).bindFromRequest();
		if (createForm.hasErrors()) {
			
			//return badRequest(login.render(createForm));
			return redirect(routes.Application.index());
		} else {
			return redirect(routes.Application.index());
		}
	}
	
	public static class Creation {
		public String text;
		

		public String validate() {
			return null;
		}

	}
}
