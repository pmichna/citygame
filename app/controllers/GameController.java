package controllers;

import java.sql.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagingList;

import models.*;
import play.Logger;

import play.mvc.*;

import views.html.*;
import play.libs.F.Function;
import play.libs.WS;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringReader;

public class GameController extends Controller {
	private static int gamesPageSize = 10;
	// time used in each game refresh
	private static int refreshTime = 100;
	
	private static String orangeUser = play.Play.application().configuration()
			.getString("application.orangeUser");
	private static String orangePass = play.Play.application().configuration()
			.getString("application.orangePass");

	@Security.Authenticated(Secured.class)
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
						Logger.debug("acceptedLocation:" + game.user.acceptedLocation);
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
							game.save();

							// if user does not have correctly set location
						} else {
							game.status = GAME_STATUS.paused;
							game.save();
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
								game.save();
							}
						}
						// remove all processed events
						Ebean.delete(currentEvents);
					}

					// Wait before next game loop iteration to not waste server
					// resources
					Thread.sleep(20000);
					game.refresh();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	@Security.Authenticated(Secured.class)
	public static play.libs.F.Promise<Result> startGameGET(final Long scenarioId) {
		final User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		final Scenario scenario = Scenario.find.ref(scenarioId);
		
		
		final String feedUrl = "https://api2.orange.pl/terminallocation/";
		
		final play.libs.F.Promise<Result> resultPromise = WS.url(feedUrl)
				.setQueryParameter("msisdn", user.phoneNumber)
				.setAuth(orangeUser, orangePass).get()
				.map(new Function<WS.Response, Result>() {				
					
					NodeList longitude = null;
					NodeList latitude = null;

					public Result apply(WS.Response response) {
						if(user == null || scenario == null) {
							return redirect(routes.Application.index());
						}
						if(!scenario.isPublic && !scenario.members.contains(user)) {
							return redirect(routes.Application.index());
						}
						//response.body.asXml();
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder db = null;
						try {
							db = dbf.newDocumentBuilder();
							InputSource is = new InputSource();
							is.setCharacterStream(new StringReader(response
									.getBody().toString()));

							Document doc = db.parse(is);
							longitude = doc.getElementsByTagName("longitude");
							latitude = doc.getElementsByTagName("latitude");

						} catch (Exception e) {
							Logger.error("Failed to properly parse location file");
							Logger.info(orangeUser);
							Logger.info(orangePass);
						}
						if(latitude.getLength()==0 || longitude.getLength()==0){
							Logger.error("Failed to get coordinates");
							if(response.getBody().indexOf("<description>msisdn not allowed</description>")!=-1){
								Logger.info("Msisdn not allowed for:" + user.phoneNumber);
								User.setUserLocation(user.phoneNumber, false);
								return ok(locationForbidden.render(user));
							}
							if(response.getBody().indexOf("<description>getLocation limit reached</description>")!=-1){
								Logger.info("Msisdn limit reached for: "+ user.phoneNumber);
							}
							return ok("Failed to get coordinates");
						}
						createAndStartGame(session("email"), scenarioId);
						return redirect(routes.GameController.viewMyGamesGET(0));						
					}
				});
		return resultPromise;
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

	@Security.Authenticated(Secured.class)
	public static Result pauseGameById(Long gameId, Integer pageNumber) {

		return changeGameStatusById(gameId, pageNumber, GAME_STATUS.paused);
	}

	@Security.Authenticated(Secured.class)
	public static Result stopGameById(Long gameId, Integer pageNumber) {
		return changeGameStatusById(gameId, pageNumber, GAME_STATUS.stopped);
	}

	@Security.Authenticated(Secured.class)
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