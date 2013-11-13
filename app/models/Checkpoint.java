package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;

@Entity
public class Checkpoint extends Model {
	
	@Id
	public Long id;
	public String title;
	public float longitude;
	public float latitude;
	public int points;
	public List<String> possibleAnswers;
	@ManyToOne
	public Scenario scenario;
	
	public static Model.Finder<Long, Checkpoint> find =
			new Finder<Long, Checkpoint>(Long.class, Checkpoint.class);
	
	public static Checkpoint create(Checkpoint checkpoint, Long scenario) {
		checkpoint.scenario = Scenario.find.ref(scenario);
		checkpoint.save();
		return checkpoint;
	}
}
