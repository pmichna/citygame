package controllers;

import play.*;
import play.data.*;
import play.mvc.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

import play.data.validation.Constraints.*;

public class CheckpointController extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result createCheckpointGET(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		Scenario scenario = Scenario.find.ref(scenarioId);
		if (!Secured.isMemberOf(scenarioId)) {
			return redirect(routes.Application.index());
		}
		return ok(createCheckpoint.render(Form.form(CheckpointForm.class), user,
				scenario));
	}

	@Security.Authenticated(Secured.class)
	public static Result createCheckpointPOST(Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		Form<CheckpointForm> createForm = Form.form(CheckpointForm.class).bindFromRequest();
		if (createForm.hasErrors()) {
			return badRequest(createCheckpoint.render(createForm, user,
					Scenario.find.ref(scenarioId)));
		} else {
			String name = createForm.get().name;
			int longitudeDegrees = createForm.get().longitudeDegrees;
			double longitudeMinutes = createForm.get().longitudeMinutes;
			int latitudeDegrees = createForm.get().latitudeDegrees;
			double latitudeMinutes = createForm.get().latitudeMinutes;
			String message = createForm.get().message;
			int points = createForm.get().points;
			double longitude = longitudeDegrees + longitudeMinutes / 4L;
			double latitude = latitudeDegrees + latitudeMinutes / 60L;

			Checkpoint.create(name, longitude, latitude, points, message,
					scenarioId);
			return redirect(routes.ScenarioController.viewPrivateScenarioGET(scenarioId));
		}
	}

	@Security.Authenticated(Secured.class)
	public static Result editCheckpointGET(Long checkpointId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		Checkpoint checkpoint = Checkpoint.find.ref(checkpointId);
		Scenario scenario = checkpoint.scenario;
		if (checkpoint == null || !Secured.isMemberOf(scenario.id)) {
			return redirect(routes.Application.index());
		}
		return ok(editCheckpoint.render(Form.form(CheckpointForm.class), user,
				scenario, checkpoint));
	}

	@Security.Authenticated(Secured.class)
	public static Result editCheckpointPOST(Long checkpointId, Long scenarioId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		if(!Secured.isMemberOf(scenarioId)) {
			return redirect(routes.Application.index());
		}
		Form<CheckpointForm> editionForm = Form.form(CheckpointForm.class).bindFromRequest();
		if (editionForm.hasErrors()) {
			return badRequest(editCheckpoint.render(editionForm, user,
					Scenario.find.ref(scenarioId),
					Checkpoint.find.ref(checkpointId)));
		} else {
			String name = editionForm.get().name;
			int longitudeDegrees = editionForm.get().longitudeDegrees;
			double longitudeMinutes = editionForm.get().longitudeMinutes;
			int latitudeDegrees = editionForm.get().latitudeDegrees;
			double latitudeMinutes = editionForm.get().latitudeMinutes;
			String message = editionForm.get().message;
			int points = editionForm.get().points;
			double longitude = longitudeDegrees + longitudeMinutes / 4;
			double latitude = latitudeDegrees + latitudeMinutes / 60;

			Checkpoint.editCheckpoint(checkpointId, name, longitude, latitude,
					points, message);
			return redirect(routes.CheckpointController.viewCheckpointGET(checkpointId));
		}
	}
	
	@Security.Authenticated(Secured.class)
	public static Result viewCheckpointGET(Long checkpointId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		Checkpoint checkpoint = Checkpoint.find.ref(checkpointId);
		Scenario scenario = checkpoint.scenario;
		Boolean isAdminMode = (user.privilege == USER_PRIVILEGE.admin);
		if(!Secured.isMemberOf(scenario.id) && !isAdminMode) {
			return redirect(routes.Application.index());
		}
		return ok(viewCheckpoint.render(user, scenario, checkpoint, isAdminMode));
	}

	@Security.Authenticated(Secured.class)
	public static Result removeCheckpointGET(Long checkpointId) {
		User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		Checkpoint checkpoint = Checkpoint.find.ref(checkpointId);
		long scenarioId = checkpoint.scenario.id;
		if (checkpoint == null) {
			return redirect(routes.Application.index());
		}

		checkpoint.delete();
		return redirect(routes.ScenarioController.viewPrivateScenarioGET(scenarioId));
	}

	public static class CheckpointForm {
		@Required(message = "Checkpoint name is required")
		public String name;

		@Required(message = "Longitude degrees required")
		@Min(value = 0, message = "Longitude degress can't be lower than 0")
		@Max(value = 180, message = "Longitude degress can't be greate than 180")
		public Integer longitudeDegrees;
	
		@Required(message = "Longitude minutes required")
		@Min(value = 0, message = "Longitude minutes can't be lower than 0")
		@Max(value = (long) 3.9999, message = "Longitude minutes can't be greater than 4")
		public Double longitudeMinutes;
		
		@Required(message = "Latitude degrees required")
		@Min(value = 0, message = "Latitude degress can't be lower than 0")
		@Max(value = 90, message = "Latitude degress can't be greater than 90")
		public Integer latitudeDegrees;
		
		@Required(message = "Latitude minutes required")
		@Min(value = 0, message = "Latitude minutes can't be lower than 0")
		@Max(value = (long) 59.9999, message = "Latitude minutes can't be greater than 59.999")
		public Double latitudeMinutes;
		
		@Required(message = "Message is required")
		public String message;

		@Required(message = "Points are required")
		@Min(value = 1, message = "Points min. = 1")
		public Integer points;	
		
		public String validate() {
			if((longitudeDegrees == 180 && longitudeMinutes != 0) ||
			(latitudeDegrees == 90 && latitudeMinutes != 0)) {
				return "Enter correct coordinates";
			}
			return null;
		}
	}	
}