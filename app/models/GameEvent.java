package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

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

	public String message;

	GameEvent(String message, String userPhoneNumber) {
		this.userPhoneNumber=userPhoneNumber;
		this.message = message;
	}

	
	synchronized public static GameEvent createGameEvent(String phoneNumber, String message){
		GameEvent event=new GameEvent(message,phoneNumber);
		event.save();
		return event;
	}
	
	public static Finder<Long,GameEvent> find = new Finder<Long,GameEvent>(
	        Long.class, GameEvent.class
	    );
}
