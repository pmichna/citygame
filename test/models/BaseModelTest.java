package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.avaje.ebean.config.dbplatform.MySqlPlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import play.test.FakeApplication;
import play.test.Helpers;

import java.io.IOException;

public class BaseModelTest {
	public static FakeApplication app;
	public static DdlGenerator ddl;

	@BeforeClass
	public static void startApp() throws IOException {
		app = Helpers.fakeApplication();
		Helpers.start(app);		
	}

 	@AfterClass
	public static void stopApp() {
		Helpers.stop(app);
	}
 
	@Before
	public void dropCreateDb() throws IOException {
		String serverName = "default";
 		EbeanServer server = Ebean.getServer(serverName);
 		ServerConfig config = new ServerConfig();
		ddl = new DdlGenerator();
		ddl.setup((SpiEbeanServer) server, new MySqlPlatform(), config);
 		// Drop
		ddl.runScript(false, ddl.generateDropDdl());
		// Create
		ddl.runScript(false, ddl.generateCreateDdl());
	}
}