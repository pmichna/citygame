package models;

import org.junit.*;
import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class CheckpointAnswerTest extends BaseModelTest {
	
	private String answerText = "answer";
	private String scenarioName = "test scenario";
	private String userEmail = "test@test.pl";
	private String userAlias = "user alias";
	private String userPassword = "secret";
	private String userPhoneNumber = "111222333";
	private USER_PRIVILEGE privilege = USER_PRIVILEGE.regular;
	
	private String checkpointName = "test checkpoint";
	
	@Before
	public void setUp() {
		User user = new User(userEmail, userAlias, userPassword, userPhoneNumber, privilege);
		user.save();
		Scenario scenario = Scenario.create(scenarioName, false, null, userEmail, false);
		Checkpoint.create(checkpointName, 1, 1, 2, "message", scenario.id, false);
	}
	
	@Test
	public void editCheckpointAnswer() {
		String newAnswer = "new answer";
		Checkpoint checkpoint = Checkpoint.find.where().eq("name", checkpointName).findUnique();
		CheckpointAnswer ca = Checkpoint.addPossibleAnswer("test answer", checkpoint.id, false);
		CheckpointAnswer modified = CheckpointAnswer.edit(ca.id, newAnswer, false);
		assertEquals(newAnswer, modified.text);
	}
}