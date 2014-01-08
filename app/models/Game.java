package models;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import play.db.ebean.Model.Finder;

import com.avaje.ebean.*;

@Entity
public class Game extends Model{
	
	@Id
	public Long id;

	@ManyToOne
	public User user;

	@ManyToMany
	public List<Checkpoint> visitedCheckpoints = new ArrayList<Checkpoint>();

	@ManyToOne
	public Scenario scenario;

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public GAME_STATUS status;

	public Date startDate;
	public int pointsCollected;
	
	public Game(User user, Scenario scenario) {
		this.user = user;
		this.scenario = scenario;
		this.status = GAME_STATUS.playing;
		this.startDate = new Date();
		this.pointsCollected = 0;
	}
	
	public static Finder<Long,Game> find = new Finder<Long,Game>(
	        Long.class, Game.class
	    );
	
	public static Game create(Long userId, Long scenarioId) {
		Game game = new Game(User.find.ref(userId), Scenario.find.ref(scenarioId));
		game.save();
		return game;
	}
	
	public static List<Game> getUserGames(String user) {
		return find
				.where()
				.eq("user", user)
				.findList();
	}
}
