package models;

import java.sql.Date;
import java.util.*;

import javax.persistence.*;

import play.Logger;
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
	@ManyToMany
	public List<Checkpoint> answeredCheckpoints = new ArrayList<Checkpoint>();

	@ManyToOne
	public Scenario scenario;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public GAME_STATUS status;

	public Date startDate;
	public int pointsCollected;
	
	
	public Game(User user, Scenario scenario, Date date){
		this.user = user;
		this.scenario = scenario;
		this.status = GAME_STATUS.playing;
		this.startDate = date;
		this.pointsCollected = 0;
	}
	
	public static List<Game> findGames(User user,int pageSize, int pageNum) {
		PagingList<Game> pagingList =  find.where()
												.eq("user", user)
												.findPagingList(pageSize);
		Page<Game> page = pagingList.getPage(pageNum);
		return page.getList();
	}
	
	synchronized public static Game createNewGame(User user, Scenario scenario, java.sql.Date date) {	
		Game game = new Game(user, scenario,date);
		game.save();
		return game;
	}
	
	
	public static Finder<Long,Game> find = new Finder<Long,Game>(
	        Long.class, Game.class
	    );
	
	
	
	public static List<Game> getUserGames(String userEmail) {
		//User user = User.find.where().eq("user",userEmail).findUnique();
		return Game.find
				.where()
				.eq("user.email", userEmail)
				.findList();
	}
	
	public static int getTotalPageCount(int pageSize, User user) {
		PagingList<Game> pagingList = find.where()
												.eq("user.email", user.email)
												.findPagingList(pageSize);
		return pagingList.getTotalPageCount();
	}
}
