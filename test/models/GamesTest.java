package models;

import java.sql.Date;
import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class GamesTest extends BaseModelTest {
	String user1Email = "bob@gmail.com";
	String user1Alias = "Bob";
	String user1Password = "secret";
	String user1PhoneNumber = "123456789";
	User user1;
	USER_PRIVILEGE privilege = USER_PRIVILEGE.regular;
	String user2Email = "alice@gmail.com";
	String user2Alias = "Alice";
	String user2Password = "secretAlice";
	String user2PhoneNumber = "987654321";
	User user2;
	String scenarioName = "test scenario";
	Scenario scenario;
	
	
	@Before
	public void setUp() {
		user1 = new User(user1Email, user1Alias, user1Password, user1PhoneNumber,
				USER_PRIVILEGE.regular);
		user1.save();
		user2 = new User(user2Email, user2Alias, user2Password, user2PhoneNumber,
				USER_PRIVILEGE.regular);
		user2.save();
		scenario = Scenario.create(scenarioName,true,null,user1Email,true);
	}
	
	
	@Test
	public void createAndRetrieveGame(){
		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.util.Date utilDate = cal.getTime();
		java.sql.Date sqlDate = new Date(utilDate.getTime());
		
		Game.createNewGame(user1, scenario,sqlDate);
		List <Game> results = Game.getUserGames(user1Email);
		assertNotNull(results);
		assertEquals(1,results.size());
		assertEquals(scenarioName,results.get(0).scenario.name);
		assertEquals(sqlDate.toString(),results.get(0).startDate.toString());
		assertEquals(0,results.get(0).pointsCollected);
		assertEquals(GAME_STATUS.playing,results.get(0).status);
	}
	
	@Test
	public void tryToCreateDuplicateGame(){
		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.util.Date utilDate = cal.getTime();
		java.sql.Date sqlDate = new Date(utilDate.getTime());
		
		Game game1;
		Game game2;

		game1=Game.createNewGame(user1, scenario,sqlDate);
		game2=Game.createNewGame(user1, scenario, sqlDate);
		assertNull(game2);

		game2=Game.createNewGame(user2, scenario, sqlDate);
		assertNotNull(game2);
		
	}
}