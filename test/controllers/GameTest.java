package controllers;

import org.junit.*;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.List;

import play.mvc.*;
import play.libs.*;
import play.test.*;
import static play.test.Helpers.*;

import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;

import models.*;

public class GameTest extends BaseControllerTest {
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
	Game game;
	
	@Before
	public void setUp() {
		user1 = new User(user1Email, user1Alias, user1Password,
				user1PhoneNumber, USER_PRIVILEGE.regular);
		user1.save();
		user2 = new User(user2Email, user2Alias, user2Password,
				user2PhoneNumber, USER_PRIVILEGE.regular);
		user2.save();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.util.Date utilDate = cal.getTime();
		java.sql.Date sqlDate = new Date(utilDate.getTime());
		scenario = Scenario.create(scenarioName, true, null, user1Email,true);
		game=Game.createNewGame(user1, scenario, sqlDate);
	}

	/*
	@Test
	public void pauseGameSuccess() {
		Result result = callAction(
				controllers.routes.ref.GameController.pauseGameById(game.id,0),
				fakeRequest().withSession("email", user1Email));
		assertEquals(game.status, GAME_STATUS.paused);
		assertEquals(303, status(result));
		
	}
	
	public void stopGameSuccess() {
		Result result = callAction(
				controllers.routes.ref.GameController.stopGameById(game.id,0),
				fakeRequest().withSession("email", user1Email));
		assertEquals(303, status(result));
		assertEquals(game.status, GAME_STATUS.paused);
	}
	
	public void playGameSuccess() {
		Result result = callAction(
				controllers.routes.ref.GameController.playGameById(game.id,0),
				fakeRequest().withSession("email", user1Email));
		assertEquals(303, status(result));
		assertEquals(game.status, GAME_STATUS.playing);
	}
	
	public void playGameFailure() {
		Result result = callAction(
				controllers.routes.ref.GameController.playGameById(game.id,0),
				fakeRequest().withSession("email", user1Email));
		assertEquals(303, status(result));
		assertEquals(game.status, GAME_STATUS.playing);
	}
*/
	

}