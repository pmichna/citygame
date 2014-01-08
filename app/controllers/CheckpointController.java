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
	@Security.Authenticated(Secured.class)
	public static Result editCheckpointGET(Long checkpointId) {
		User user = User.find.where().eq("email", session("email"))
				.findUnique();
		Checkpoint checkpoint = Checkpoint.find
				.where().eq("id", checkpointId).findUnique();
		if(checkpoint == null){
			return redirect(routes.Application.index());
		}
		Scenario scenario = checkpoint.scenario;
		/*
		if (!Secured.isMemberOf(scenario.id)) {
			return redirect(routes.Application.index());
		}*/
		
		return ok(editCheckpoint.render(Form.form(Edition.class), user,
				scenario, checkpoint));
	}

	@Security.Authenticated(Secured.class)
	public static Result editCheckpointPOST() {
		User user = User.find.where().eq("email", session("email"))
				.findUnique();
		Form<Edition> editionForm = Form.form(Edition.class).bindFromRequest();
		long scenarioId = editionForm.get().scenarioId;
		long checkpointId = editionForm.get().checkpointId;
		if (editionForm.hasErrors()) {
			return badRequest(editCheckpoint.render(editionForm, user,
					Scenario.find.ref(scenarioId), Checkpoint.find.ref(checkpointId)));
		} else {
			String name = editionForm.get().name;
			double longitudeDegrees = editionForm.get().longitudeDegrees;
			double longitudeMinutes = editionForm.get().longitudeMinutes;
			double latitudeDegrees = editionForm.get().latitudeDegrees;
			double latitudeMinutes = editionForm.get().latitudeMinutes;
			String message = editionForm.get().message;
			int points = editionForm.get().points;
			double longitude = longitudeDegrees + longitudeMinutes / 4;
			double latitude = latitudeDegrees + latitudeMinutes / 60;

			Checkpoint.editCheckpoint(checkpointId, name, longitude, latitude, points, message);
			return redirect(routes.ScenarioController.viewMyScenariosGET());
		}

	}
	
	

	public static class Creation {
		public String name;
		public double longitudeDegrees;
		public double longitudeMinutes;
		public double latitudeDegrees;
		public double latitudeMinutes;
		public String message;
		public int points;
		public long scenarioId;

		public String validate() {
			return null;
		}

	}
	
	public static class Edition {
		public String name;
		public double longitudeDegrees;
		public double longitudeMinutes;
		public double latitudeDegrees;
		public double latitudeMinutes;
		public String message;
		public int points;
		public long scenarioId;
		public long checkpointId;

		public String validate() {
			return null;
		}

	}
}
