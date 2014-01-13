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

	@ManyToMany(cascade = CascadeType.REMOVE)
	public List<Checkpoint> visitedCheckpoints = new ArrayList<Checkpoint>();
	
	@ManyToMany(cascade = CascadeType.REMOVE)
	public List<Checkpoint> answeredCheckpoints = new ArrayList<Checkpoint>();

	@ManyToOne
	public Scenario scenario;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public GAME_STATUS status;

	public Date startDate;
	public int pointsCollected;
	
	public static Finder<Long,Game> find = new Finder<Long,Game>(
	        Long.class, Game.class
	    );
	
	
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
	
	public static List<GameAggregate> getRankingGames(Long scenarioId) {
		
	    String sql = "SELECT u.alias, max(points_collected) as points"
					+ " from game g"
					+ " join user u on u.id = g.user_id"
					+ " where g.scenario_id = :scenarioId"
					+ " group by u.id"
					+ " ORDER BY points desc";
		
		List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
									.setParameter("scenarioId", scenarioId)
									.findList();
		List<GameAggregate> games = new ArrayList<GameAggregate>(sqlRows.size());
		
		for(int i = 0; i < sqlRows.size(); i++) {
			String alias = sqlRows.get(i).getString("alias");
			Integer points = sqlRows.get(i).getInteger("points");		
			games.add(new GameAggregate(alias, points));
		}
       
		return games;
	}
}
