package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.Logger;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class ReceivedMessage extends Model {
	@Id
	public Long id;

	@Column(length = 9, nullable = false)
	@Required
	public String userPhoneNumber;

	@ManyToOne
	public Scenario scenario;

	@ManyToOne
	public Checkpoint checkpoint;

	public String message;
	
	public static Finder<Long, ReceivedMessage> find = new Finder<Long, ReceivedMessage>(Long.class, ReceivedMessage.class);

	public ReceivedMessage(String message, String userPhoneNumber, Long scenarioId,
			Long checkpointId) {
		this.userPhoneNumber = userPhoneNumber;
		this.message = message;
		scenario = Scenario.find.byId(scenarioId);
		checkpoint = Checkpoint.find.byId(checkpointId);
	}


	public static ReceivedMessage createReceivedMessage(String phoneNumber, String message,
			Long scenarioId, Long checkpointId) {
		ReceivedMessage receivedMessage = new ReceivedMessage(message, phoneNumber, scenarioId,
				checkpointId);
		receivedMessage.save();
		return receivedMessage;
	}
	
	// public static Boolean wasSmsReceived(String message, String userPhoneNumber, Long scenarioId, Long checkpointId) {
	// 	return find.where()
	// 				.eq("message", message)
	// 				.eq("userPhoneNumber", userPhoneNumber)
	// 				.eq("scenario.id", scenarioId)
	// 				.eq("checkpoint.id", checkpointId)
	// 				.findRowCount() > 0;
	// }

}
