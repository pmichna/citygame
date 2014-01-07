package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;

@Entity
public class Checkpoint extends Model {

        @Id
        public Long id;

        @Column(length=80,nullable=false)
        public String name;

        @Column(nullable=false)
        public double longitude;

        @Column(nullable=false)
        public double latitude;

        @Column(nullable=false)
        public int points;

        @Column(length=160,nullable=false)
        public String message;

        @OneToMany(cascade=CascadeType.ALL)
        public List<CheckpointAnswer> possibleAnswers = new ArrayList<CheckpointAnswer>();

        @ManyToOne
        public Scenario scenario;

        public static Model.Finder<Long, Checkpoint> find =
            new Finder<Long, Checkpoint>(Long.class, Checkpoint.class);


        public static Checkpoint create(String checkpointName, double longitude, double latitude, int points, String message, Long scenarioId) {
			Checkpoint checkpoint = new Checkpoint();
			checkpoint.name = checkpointName;
			checkpoint.longitude = longitude;
			checkpoint.latitude = latitude;
			checkpoint.points = points;
			checkpoint.message = message;
			checkpoint.scenario = Scenario.find.ref(scenarioId);
            checkpoint.save();
            return checkpoint;
        }

        public static List<Checkpoint> findAssignedTo(Long scenario) {
            return find.where()
                    .eq("scenario.id", scenario)
                    .findList();
        }
		
		public static String addPossibleAnswer(String answer, Long checkpointId) {
			Checkpoint checkpoint = find.ref(checkpointId);
            CheckpointAnswer checkpointAnswer = new CheckpointAnswer(answer);
            checkpoint.possibleAnswers.add(checkpointAnswer);
            checkpoint.save();
			return answer;
		}
		
		public static List<String> addPossibleAnswers(List<String> answers, Long checkpointId) {
			Checkpoint checkpoint = find.ref(checkpointId);
			List<CheckpointAnswer> checkpointAnswers = new ArrayList<CheckpointAnswer>();
			for(String a: answers) {
				checkpointAnswers.add(new CheckpointAnswer(a));
			}
            checkpoint.possibleAnswers.addAll(checkpointAnswers);
            checkpoint.save();
			return answers;
		}
		
		public static List<String> getAnswers(Long checkpointId) {
			Checkpoint checkpoint = find.ref(checkpointId);
			List<String> answers = new ArrayList<String>();
			for(CheckpointAnswer ca: checkpoint.possibleAnswers) {
				answers.add(ca.text);
			}
			return answers;
		}
}
