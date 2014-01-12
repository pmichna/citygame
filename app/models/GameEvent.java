package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
	
	@ManyToOne
	public Scenario scenario;
	
	@ManyToOne
	public Checkpoint checkpoint;
	
	public String message;
	public Double longitude;
	public Double latitude;

	GameEvent(String message, String userPhoneNumber, Long scenarioId, Long checkpointId) {
		this.userPhoneNumber=userPhoneNumber;
		this.message = message;
		scenario=Scenario.find.byId(scenarioId);
		checkpoint=Checkpoint.find.byId(checkpointId);
		this.longitude=null;
		this.latitude=null;
	}
	
	GameEvent(double longitude, double latitude, String userPhoneNumber){
		this.userPhoneNumber=userPhoneNumber;
		this.longitude=longitude;
		this.latitude=latitude;
		this.message=null;
	}

	
	public static GameEvent createGameEvent(String phoneNumber, String message, Long scenarioId, Long checkpointId){
		GameEvent event=new GameEvent(message,phoneNumber,scenarioId,checkpointId);
		event.save();
		return event;
	}
	
	public static GameEvent createGameEvent(double longitude, double latitude, String userPhoneNumber){
		GameEvent event=new GameEvent(longitude,latitude,userPhoneNumber);
		event.save();
		return event;
	}
	
	public static Finder<Long,GameEvent> find = new Finder<Long,GameEvent>(
	        Long.class, GameEvent.class
	    );
	
}
