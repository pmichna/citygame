package models;

import java.util.*;

import javax.persistence.*;
import play.data.validation.*;
import play.db.ebean.*;
import play.db.ebean.Model.Finder;

@Entity
public class CheckpointAnswer extends Model {
	
	@Id
	public Long id;

	@Column(length=160,nullable=false)
	public String text;
	
	public static Model.Finder<Long, CheckpointAnswer> find =
			new Finder<Long, CheckpointAnswer>(Long.class, CheckpointAnswer.class);
	
	public CheckpointAnswer(String text) {
		this.text = text;
	}
	
	public static CheckpointAnswer edit(Long checkpointAnswerId, String text, Boolean isAccepted) {
		CheckpointAnswer ca = find.ref(checkpointAnswerId);
		ca.text = text;
		ca.save();
		Checkpoint checkpoint = Checkpoint.find.where().eq("possibleAnswers.id", checkpointAnswerId).findUnique();
		checkpoint.scenario.isAccepted = isAccepted;
		return ca;
	}
	
}
