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

public class CheckpointController extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result createCheckpointGET(Long scenarioId) {
		User user = User.find.where().eq("email", session("email"))
				.findUnique();
		Scenario scenario = Scenario.find.ref(scenarioId);
		if (!Secured.isMemberOf(scenarioId)) {
			return redirect(routes.Application.index());
		}
		return ok(createCheckpoint.render(Form.form(Creation.class), user,
				scenario));
	}

	@Security.Authenticated(Secured.class)
	public static Result createCheckpointPOST() {
		User user = User.find.where().eq("email", session("email"))
				.findUnique();
		Form<Creation> createForm = Form.form(Creation.class).bindFromRequest();
		long scenarioId = createForm.get().scenarioId;
		if (createForm.hasErrors()) {
			return badRequest(createCheckpoint.render(createForm, user,
					Scenario.find.ref(scenarioId)));
		} else {
			String name = createForm.get().name;
			double longitudeDegrees = createForm.get().longitudeDegrees;
			double longitudeMinutes = createForm.get().longitudeMinutes;
			double latitudeDegrees = createForm.get().latitudeDegrees;
			double latitudeMinutes = createForm.get().latitudeMinutes;
			String message = createForm.get().message;
			int points = createForm.get().points;
			double longitude = longitudeDegrees + longitudeMinutes / 4;
			double latitude = latitudeDegrees + latitudeMinutes / 60;

			Checkpoint.create(name, longitude, latitude, points, message,
					scenarioId);
			return redirect(routes.ScenarioController.viewMyScenariosGET());
		}

	}

	/*
	 * @Security.Authenticated(Secured.class) public static Result
	 * viewMyScenariosGET() { User user=User.find .where().eq("email",
	 * session("email")).findUnique(); return
	 * ok(myScenarios.render(user,Scenario.findOwned(user.email))); }
	 */

	public static class Creation {
		public String name;
		double longitudeDegrees;
		double longitudeMinutes;
		double latitudeDegrees;
		double latitudeMinutes;
		String message;
		int points;
		long scenarioId;

		public String validate() {
			return null;
		}

	}
}
