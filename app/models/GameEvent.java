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

	GameEvent(String message, String userPhoneNumber, Long scenarioId, Long checkpointId) {
		this.userPhoneNumber=userPhoneNumber;
		this.message = message;
		scenario=Scenario.find.byId(scenarioId);
		checkpoint=Checkpoint.find.byId(checkpointId);
	}

	
	synchronized public static GameEvent createGameEvent(String phoneNumber, String message, Long scenarioId, Long checkpointId){
		
		GameEvent event=new GameEvent(message,phoneNumber,scenarioId,checkpointId);
		event.save();
		return event;
	}
	
	public static Finder<Long,GameEvent> find = new Finder<Long,GameEvent>(
	        Long.class, GameEvent.class
	    );
	
}
