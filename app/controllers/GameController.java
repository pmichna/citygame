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
		Logger.debug("Page num:" + pageNum+" totalPageCount:" + totalPageCount);
		return ok(viewMyGames.render(
					user,
					Game.findGames(user, gamesPageSize, pageNum),
					pageNum,
					totalPageCount,
					gamesPageSize)
		);
	}

	private static class GameThread implements Runnable {
		String userEmail;
		Long scenarioId;

		public GameThread(String userEmail, Long scenarioId) {
			this.userEmail = userEmail;
			this.scenarioId = scenarioId;
		}
		
		@Override
		public void run() {

			try {
				java.util.Calendar cal = java.util.Calendar.getInstance();
				java.util.Date utilDate = cal.getTime();
				java.sql.Date sqlDate = new Date(utilDate.getTime());

				// create game
				User user = User.find.where().eq("email", userEmail)
						.findUnique();
				Scenario scenario = Scenario.find.byId(scenarioId);

				if (user == null || scenario == null)
					return;
				Game game = Game.createNewGame(user, scenario, sqlDate);

				if (game == null)
					return;

				// game loop, will continue till game is stopped
				for (int i = 0; i < 10; i++) {
					// while (game.status != GAME_STATUS.stopped) {

					// Find events to process for this phone number
					List<GameEvent> currentEvents = GameEvent.find.where()
							.eq("userPhoneNumber", user.phoneNumber).findList();
					// Process events
					for (GameEvent e : currentEvents) {
						// If event was an sms message
						if (e.message != null) {
							// do something with sms
						} else {
							// if message is empty
						}
					}
					// remove all processed events
					Ebean.delete(currentEvents);
					// Wait before next game loop iteration to not waste server
					// resources
					Thread.sleep(30);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	@Security.Authenticated(Secured.class)
	public static play.libs.F.Promise<Result> startGameGET(final Long scenarioId) {
		String feedUrl = "https://api2.orange.pl/getopcode/";
		final User user = User.find
						.where()
						.eq("email", session("email"))
						.findUnique();
		
		final play.libs.F.Promise<Result> resultPromise = WS.url(feedUrl)
															.setQueryParameter("msisdn", user.phoneNumber)
															.setAuth("48509237274", "Y7A7HNM3EFF3LF")
															.get()
															.map(new Function<WS.Response, Result>() {
																NodeList opcodeNode = null;

																public Result apply(WS.Response response) {
																	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
																	DocumentBuilder db = null;
																	try {
																		db = dbf.newDocumentBuilder();
																		InputSource is = new InputSource();
																		is.setCharacterStream(new StringReader(response.getBody().toString()));
																		Document doc = db.parse(is);
																		opcodeNode = doc.getElementsByTagName("opcode");
																	} catch (Exception e) {
																		Logger.debug("Failed to properly pase location file");
																	}
																	String opcode = opcodeNode.item(0).getTextContent();
																	Scenario scenario = Scenario.find.ref(scenarioId);
																	if(scenario == null) {
																		return redirect(routes.Application.index());
																	}
																	if(!opcode.equals("26003")) {
																		return ok(badNumber.render(user));
																	}
																	Thread t = new Thread(new GameThread(session("email"), scenarioId));
																	t.start();
																	return redirect(routes.GameController.viewMyGamesGET(0));
																}
															});
		return resultPromise;	
	}
	
	public static play.libs.F.Promise<Result> locationControllerGET(String number) {

		String feedUrl = "https://api2.orange.pl/terminallocation/?msisdn=";

		final play.libs.F.Promise<Result> resultPromise = WS.url(feedUrl)
															.setAuth("48509237274", "Y7A7HNM3EFF3LF")
															.get()
															.map(new Function<WS.Response, Result>() {
																NodeList longitude = null;
																NodeList latitude = null;

																public Result apply(WS.Response response) {
																	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
																	DocumentBuilder db = null;
																	try {
																		db = dbf.newDocumentBuilder();
																		InputSource is = new InputSource();
																		is.setCharacterStream(new StringReader(response.getBody().toString()));
																		Document doc = db.parse(is);
																		longitude = doc.getElementsByTagName("longitude");
																		latitude = doc.getElementsByTagName("latitude");
																	} catch (Exception e) {
																		Logger.debug("Failed to properly pase location file");
																	}
																	if(latitude.getLength()==0 || longitude.getLength()==0)
																		return ok("");
																	return ok(latitude.item(0).getTextContent()+" "+longitude.item(0).getTextContent());
																}
															});
		return resultPromise;
	}
	
	@Security.Authenticated(Secured.class)
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
			return ok(viewMyGames.render(
									user,
									games,
									pageNumber,
									totalPageCount,
									gamesPageSize)
			);
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
		return Scenario.create(name, false, sqlDate, user.email,false);

	}

	synchronized public static Scenario getScenario(Long scenarioId) {
		return Scenario.find.byId(scenarioId);
	}
	
	synchronized public static User getUser(Long userId) {
		return User.find.byId(userId);
	}

}