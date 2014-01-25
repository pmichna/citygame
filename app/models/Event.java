package models;

import java.sql.Date;
import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;

import com.avaje.ebean.*;

import play.data.validation.Constraints.*;

@Entity
public class Event extends Model{

	@Id
	public Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public EVENT_TYPE type;

	@ManyToOne
	public User user;

	@Column
	public Date date;
	
	@Column(nullable = false)
	public String message;
	@Column
	public Boolean wasRead;
	

	public Event(User user, String message, EVENT_TYPE type, Date date) {
		this.user = user;
		this.message = message;
		this.type = type;
		this.date = date;
		wasRead = false;
	}
	
	public Event(User user, String message, EVENT_TYPE type){
		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.util.Date utilDate = cal.getTime();
		this.user = user;
		this.message = message;
		this.type = type;
		this.date = new Date(utilDate.getTime());
		wasRead = false;
	}
}
