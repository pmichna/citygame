package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.Logger;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class GameEvent extends Model {
	@Id
	public Long id;

	@Column(length = 9, nullable = false)
	@Required
	public String userPhoneNumber;

	@Column
	@Required
	public GAME_EVENT_TYPE type;

	@ManyToOne
	public Scenario scenario;

	@ManyToOne
	public Checkpoint checkpoint;

	public String message;
	public Double longitude;
	public Double latitude;

	public GameEvent(String message, String userPhoneNumber, Long scenarioId,
			Long checkpointId) {
		this.userPhoneNumber = userPhoneNumber;
		this.message = message;
		scenario = Scenario.find.byId(scenarioId);
		checkpoint = Checkpoint.find.byId(checkpointId);
		this.longitude = null;
		this.latitude = null;
		type = GAME_EVENT_TYPE.location;
	}

	public GameEvent(double longitude, double latitude, String userPhoneNumber) {
		this.userPhoneNumber = userPhoneNumber;
		this.longitude = longitude;
		this.latitude = latitude;
		this.message = null;
		type = GAME_EVENT_TYPE.location;
	}

	public GameEvent(String userPhoneNumber, String message, GAME_EVENT_TYPE type) {
		this.type = type;
		this.userPhoneNumber = userPhoneNumber;
		this.message = message;
	}

	public static GameEvent createGameEvent(String userPhoneNumber,
			String message, GAME_EVENT_TYPE type) {
		GameEvent event = new GameEvent(userPhoneNumber, message, type);
		event.save();
		return event;
	}

	public static GameEvent createGameEvent(String phoneNumber, String message,
			Long scenarioId, Long checkpointId) {
		GameEvent event = new GameEvent(message, phoneNumber, scenarioId,
				checkpointId);
		event.save();
		return event;
	}

	public static GameEvent createGameEvent(double longitude, double latitude,
			String userPhoneNumber) {
		GameEvent event = new GameEvent(longitude, latitude, userPhoneNumber);
		event.save();
		return event;
	}

	public static Finder<Long, GameEvent> find = new Finder<Long, GameEvent>(
			Long.class, GameEvent.class);

}
