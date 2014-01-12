package controllers;

import java.sql.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagingList;

import models.*;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import scala.collection.concurrent.Debug;
import views.html.*;

public class GameController extends Controller {
	private static int gamesPageSize = 10;
	// time used in each game refresh
	private static int refreshTime = 100;

	public static Result viewMyGamesGET(int pageNum) {
		User user = User.find.where().eq("email", session("email"))
				.findUnique();
		if (user == null) {
			return redirect(routes.Application.index());
		}
		int totalPageCount = Game.getTotalPageCount(gamesPageSize, user);
		if (pageNum > totalPageCount - 1) {
			pageNum = 0;
		}
		Logger.debug("Page num:" + pageNum + " totalPageCount:"
				+ totalPageCount);
		return ok(viewMyGames.render(user,
				Game.findGames(user, gamesPageSize, pageNum), pageNum,
				totalPageCount, gamesPageSize));
	}

	public static Game createAndStartGame(String userEmail, Long scenarioId){
		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.util.Date utilDate = cal.getTime();
		java.sql.Date sqlDate = new Date(utilDate.getTime());
		User user = User.find.where().eq("email", userEmail)
				.findUnique();
		Scenario scenario = Scenario.find.byId(scenarioId);
		if (user == null || scenario == null)
			return null;
		Game game = Game.createNewGame(user, scenario, sqlDate);
		//MessageController.sendMsg(user.phoneNumber, "costam");
		startGame(game);
		return game;
		
	}
	
	public static void startGame(Game game){
		Thread t = new Thread(new GameThread(game));
		t.start();
	}
	
	private static class GameThread implements Runnable {

		Game game;

		public GameThread(Game game) {
			this.game = game;
		}

		@Override
		public void run() {

			try {
				Logger.info("Started new game thread");
				// game loop, will continue till game is stopped
				while (game.status != GAME_STATUS.stopped) {

					// if game is paused, do nothing
					if (game.status != GAME_STATUS.paused) {

						// check current position
						LocationController.locationControllerGET(game.user.phoneNumber);
						Logger.debug("acceptedLocation:" +game.user.acceptedLocation);
						if (game.user.acceptedLocation) {
							List<Checkpoint> nearby = game.scenario
									.findNearbyCheckpoints(game.user.lastLongitude,
											game.user.lastLatitude);
							
							Logger.debug("Nearby checkpooints #: "+nearby.size());
							// send messages from nearby checkpoints
							for (Checkpoint c : nearby) {
								if(!game.visitedCheckpoints.contains(c)){
								c.sendMessage(game.user.phoneNumber);
								game.visitedCheckpoints.add(c);
								}
							}
							game.update();

							// if user does not have correctly set location
						} else {
							game.status = GAME_STATUS.paused;
							game.update();
							continue;
						}

						// Find events to process for this phone number and this
						// scenario
						List<GameEvent> currentEvents = GameEvent.find.where()
								.eq("userPhoneNumber", game.user.phoneNumber)
								.eq("scenario", game.scenario).findList();
						if (currentEvents.size() > 0)
							Logger.debug("events number:"
									+ currentEvents.size());
						// Process events
						for (GameEvent e : currentEvents) {
							Logger.debug("events");
							// If event was an sms message
							if (e.type == GAME_EVENT_TYPE.smsReceive) {
								// do something with sms
								// check if answer is correct
								Logger.debug("[event] Message being processed: "
										+ e.message);
								boolean checkAnswer = Checkpoint.hasAnswer(
										e.checkpoint.id, e.message);
								Logger.debug("[event] Maching answer:"
										+ checkAnswer);
								// if checkpoint does not match, search further
								if (!checkAnswer)
									continue;
								// if answers match add points and mark it as
								// answered
								game.pointsCollected += e.checkpoint.points;
								game.answeredCheckpoints.add(e.checkpoint);
								game.update();

							}

						}
						// remove all processed events
						Ebean.delete(currentEvents);
					}

					// Wait before next game loop iteration to not waste server
					// resources
					Thread.sleep(20000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public static Result startGameGET(Long scenarioId) {
		createAndStartGame(session("email"), scenarioId);
		return redirect(routes.GameController.viewMyGamesGET(0));
	}

	public static Result changeGameStatusById(Long gameId, Integer pageNumber,
			GAME_STATUS status) {

		Game game = Game.find.byId(gameId);
		if (game != null) {
			game.status = status;
			game.save();
			User user = User.find.where().eq("email", session("email"))
					.findUnique();
			List<Game> games = Game.findGames(user, gamesPageSize, pageNumber);
			int totalPageCount = Game.getTotalPageCount(gamesPageSize, user);
			boolean isFirstPage, isListPage;
			if (pageNumber > 0) {
				isFirstPage = false;
				isListPage = true;
			} else {
				isFirstPage = true;
				isListPage = false;
			}
			return ok(viewMyGames.render(user, games, pageNumber,
					totalPageCount, gamesPageSize));
		} else
			return redirect(routes.GameController.viewMyGamesGET(pageNumber));
	}

	public static Result pauseGameById(Long gameId, Integer pageNumber) {

		return changeGameStatusById(gameId, pageNumber, GAME_STATUS.paused);
	}

	public static Result stopGameById(Long gameId, Integer pageNumber) {
		return changeGameStatusById(gameId, pageNumber, GAME_STATUS.stopped);
	}

	public static Result playGameById(Long gameId, Integer pageNumber) {
		return changeGameStatusById(gameId, pageNumber, GAME_STATUS.playing);
	}

	synchronized public static Scenario saveScenario(String name, User user) {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.util.Date utilDate = cal.getTime();
		java.sql.Date sqlDate = new Date(utilDate.getTime());
		return Scenario.create(name, false, sqlDate, user.email, false);

	}

	synchronized public static Scenario getScenario(Long scenarioId) {
		return Scenario.find.byId(scenarioId);
	}

	synchronized public static User getUser(Long userId) {
		return User.find.byId(userId);
	}

}