package models;

import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.*;

import play.Logger;
import play.db.ebean.*;

import com.avaje.ebean.PagingList;
import com.avaje.ebean.Page;

@Entity
public class Scenario extends Model {

	@Id
	public Long id;

	@Column(length=80,nullable=false)
	public String name;
	
	@Column(nullable=false)
	public Boolean isPublic;

	@Column(nullable=false)
	public Boolean isAccepted;

	public Date expirationDate;

	@ManyToOne
	public User owner;

	@ManyToMany(cascade = CascadeType.ALL)
	public List<User> members = new ArrayList<User>();

	@OneToMany(cascade = CascadeType.ALL)
	public List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
	
	@ManyToOne
	public User editedBy;

	public static Finder<Long,Scenario> find = new Finder(
		Long.class, Scenario.class
		);
	
	public Scenario(String name, boolean isPublic, Date expirationDate, User owner, Boolean isAccepted) {
		this.name = name;
		this.isPublic = isPublic;
		this.expirationDate = expirationDate;
		this.owner = owner;
		this.isAccepted = isAccepted;
		this.members.add(owner);
		this.editedBy = null;
	}
	
	public static Scenario create(String name, boolean isPublic, Date expirationDate,
		String ownerEmail, Boolean isAccepted) {
		Scenario scenario = new Scenario(name, isPublic, expirationDate, User.find
																				.where()
																				.eq("email", ownerEmail)
																				.findUnique(),
																				isAccepted);
		scenario.save();
		scenario.saveManyToManyAssociations("members");
		return scenario;
	}
	
	public static Scenario edit(Long scenarioId, String newName, Boolean newIsPublic, Date newExpirationDate, Boolean isAccepted) {
		Scenario scenario = find.ref(scenarioId);
		if(newName != null && !newName.equals(scenario.name)) {
			scenario.name = newName;
		}
		if(newIsPublic != null && newIsPublic != scenario.isPublic) {
			scenario.isPublic = newIsPublic;
		}
		if(newExpirationDate != null && !newExpirationDate.equals(scenario.expirationDate)) {
			scenario.expirationDate = newExpirationDate;
		}
		scenario.isAccepted = isAccepted;
		scenario.editedBy = null;
		scenario.save();
		return scenario;
	}
	
	public static List<Scenario> findOwned(String userEmail){
		return find.where().eq("owner.email",userEmail).findList();
	}
	
	public List<Checkpoint> findNearbyCheckpoints(double longitude, double latitude){
		List<Checkpoint> nearby = new ArrayList<Checkpoint>();
		Logger.debug("finding checkpoints");
		for(Checkpoint c:checkpoints){
			Logger.debug("Longitude: "+longitude);
			Logger.debug("Longitude d: "+(c.longitude-longitude));
			Logger.debug("Distance: "+(c.longitude-longitude)*(c.longitude-longitude)+
					(c.latitude-latitude)*(c.latitude-latitude));
			if((c.longitude-longitude)*(c.longitude-longitude)+
					(c.latitude-latitude)*(c.latitude-latitude)<0.05*0.05){
						nearby.add(c);
					}	
		}
		return nearby;
	}
	
	public static List<Scenario> findInvolvingUser(String user, int pageSize, int pageNum) {
		PagingList<Scenario> pagingList = find.where()
												.eq("members.email", user)
												.findPagingList(pageSize);
		Page<Scenario> page = pagingList.getPage(pageNum);
		return page.getList();
	}
	
	public static Scenario findInvolvingCheckpoint(Long checkpointId) {
		return find.where()
					.eq("checkpoints.id", checkpointId)
					.findUnique();
	}
	
	public static List<Scenario> findPublicAcceptedNotExpired(Date currentDate, int pageSize, int pageNum) {
		PagingList<Scenario> pagingList =  find.where()
												.eq("isPublic", true)
												.eq("isAccepted", true)
												.or(
													com.avaje.ebean.Expr.gt("expirationDate", currentDate),
													com.avaje.ebean.Expr.isNull("expirationDate")
												)
												.findPagingList(pageSize);
		Page<Scenario> page = pagingList.getPage(pageNum);
		return page.getList();
	}
	
	public static boolean isMember(Long scenarioId, String userEmail){
		 return find.where()
			        .eq("members.email", userEmail)
			        .eq("id", scenarioId)
			        .findRowCount() > 0;
	}
	
	public static int getTotalPrivatePageCount(String user, int pageSize) {
		PagingList<Scenario> pagingList = find.where()
												.eq("members.email", user)
												.findPagingList(pageSize);
		return pagingList.getTotalPageCount();
	}
	
	public static int getTotalPublicAcceptedNotExpiredPageCount(int pageSize, Date currentDate) {
		PagingList<Scenario> pagingList = find.where()
												.eq("isPublic", true)
												.eq("isAccepted", true)
												.or(
													com.avaje.ebean.Expr.lt("expirationDate", currentDate),
													com.avaje.ebean.Expr.isNull("expirationDate")
												)
												.findPagingList(pageSize);
		return pagingList.getTotalPageCount();
	}
	
	public static List<Scenario> findToAccept(Date currentDate, int pageSize, int pageNum) {
		PagingList<Scenario> pagingList =  find.where()
												.eq("isAccepted", false)
												.eq("isPublic", true)
												.or(
													com.avaje.ebean.Expr.lt("expirationDate", currentDate),
													com.avaje.ebean.Expr.isNull("expirationDate")
												)
												.findPagingList(pageSize);
		Page<Scenario> page = pagingList.getPage(pageNum);
		return page.getList();
	}
	
	public static int getTotalToAcceptPageCount(Date currentDate, int pageSize) {
		return find.where()
					.eq("isAccepted", false)
					.eq("isPublic", true)
					.or(
						com.avaje.ebean.Expr.lt("expirationDate", currentDate),
						com.avaje.ebean.Expr.isNull("expirationDate")
					)
					.findPagingList(pageSize)
					.getTotalPageCount();
	}
}
