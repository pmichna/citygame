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

        @OneToMany
        public List<CheckpointAnswer> possibleAnswers = new ArrayList<CheckpointAnswer>();

        @ManyToOne
        public Scenario scenario;

        public static Model.Finder<Long, Checkpoint> find =
                new Finder<Long, Checkpoint>(Long.class, Checkpoint.class);

        public static Checkpoint create(Checkpoint checkpoint, Long scenario) {
                checkpoint.scenario = Scenario.find.ref(scenario);
                checkpoint.save();
                return checkpoint;
        }

        public static List<Checkpoint> findAssignedTo(Long scenario) {
                return find.where()
                        .eq("scenario.id", scenario)
                        .findList();
        }
}
