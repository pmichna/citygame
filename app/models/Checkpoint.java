package models;

import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.Ebean;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;

@Entity
public class Checkpoint extends Model {

	@Id
	public Long id;

	@Column(length = 80, nullable = false)
	public String name;

	@Column(nullable = false)
	public double longitude;

	@Column(nullable = false)
	public double latitude;

	@Column(nullable = false)
	public int points;

	@Column(length = 160, nullable = false)
	public String message;

	@OneToMany(cascade = CascadeType.ALL)
	public List<CheckpointAnswer> possibleAnswers = new ArrayList<CheckpointAnswer>();

	@ManyToOne(cascade = CascadeType.PERSIST)
	public Scenario scenario;

	public static Model.Finder<Long, Checkpoint> find = new Finder<Long, Checkpoint>(
			Long.class, Checkpoint.class);

	public static Checkpoint create(String checkpointName, double longitude,
			double latitude, int points, String message, Long scenarioId, Boolean isAccepted) {
		Checkpoint checkpoint = new Checkpoint();
		checkpoint.name = checkpointName;
		checkpoint.longitude = longitude;
		checkpoint.latitude = latitude;
		checkpoint.points = points;
		checkpoint.message = message;
		checkpoint.scenario = Scenario.find.ref(scenarioId);
		checkpoint.scenario.isAccepted = isAccepted;
		checkpoint.save();
		return checkpoint;
	}
	
	
	public int getLongitudeDegrees() {
		return (int) this.longitude;
	}
	
	public double getLongitudeMinutes() {
		return (longitude - getLongitudeDegrees())*4;
	}
	
	public int getLatitudeDegrees() {
		return (int) this.longitude;
	}
	
	public double getLatitudeMinutes() {
		return (longitude - getLongitudeDegrees())*60;
	}

	public static List<Checkpoint> findAssignedTo(Long scenario) {
		return find.where().eq("scenario.id", scenario).findList();
	}

	public static String addPossibleAnswer(String answer, Long checkpointId, Boolean isAccepted) {
		Checkpoint checkpoint = find.ref(checkpointId);
		CheckpointAnswer checkpointAnswer = new CheckpointAnswer(answer);
		checkpoint.possibleAnswers.add(checkpointAnswer);
		checkpoint.scenario.isAccepted = isAccepted;
		checkpoint.save();
		return answer;
	}

	public static List<String> addPossibleAnswers(List<String> answers,
			Long checkpointId, Boolean isAccepted) {
		Checkpoint checkpoint = find.ref(checkpointId);
		List<CheckpointAnswer> checkpointAnswers = new ArrayList<CheckpointAnswer>();
		for (String a : answers) {
			checkpointAnswers.add(new CheckpointAnswer(a));
		}
		checkpoint.possibleAnswers.addAll(checkpointAnswers);
		checkpoint.scenario.isAccepted = isAccepted;
		checkpoint.save();
		return answers;
	}

	public static List<String> getAnswers(Long checkpointId) {
		Checkpoint checkpoint = find.ref(checkpointId);
		List<String> answers = new ArrayList<String>();
		for (CheckpointAnswer ca : checkpoint.possibleAnswers) {
			answers.add(ca.text);
		}
		return answers;
	}
	
	 public static Checkpoint editCheckpoint(Long checkpointId, String checkpointName, double longitude,
				double latitude, int points, String message, Boolean isAccepted) {
    	Checkpoint checkpoint = find.ref(checkpointId);
    	if(checkpoint == null) {
    		return null;
    	}
    	if(!checkpointName.equals("") && !checkpointName.equals(checkpoint.name)) {
    		checkpoint.name = checkpointName;
    	}
    	if(message != null && !message.equals("") && !message.equals(checkpoint.message)) {
    		checkpoint.message = message;
    	}
    	if(checkpoint.longitude != longitude) {
    		checkpoint.longitude = longitude;
    	}
    	if(checkpoint.latitude != latitude) {
    		checkpoint.latitude = latitude;
    	}
		if(checkpoint.points != points) {
			checkpoint.points = points;
		}
		checkpoint.scenario.isAccepted = isAccepted;
    	checkpoint.save();
    	
    	return checkpoint;
	}
}
