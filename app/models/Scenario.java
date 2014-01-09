package models;

import java.sql.Date;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.*;
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

	@ManyToMany
	public List<User> members = new ArrayList<User>();

	@OneToMany
	public List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();

	public static Finder<Long,Scenario> find = new Finder(
		Long.class, Scenario.class
		);
	
	public Scenario(String name, boolean isPublic, Date expirationDate, User owner) {
		this.name = name;
		this.isPublic = isPublic;
		this.expirationDate = expirationDate;
		this.owner = owner;
		this.isAccepted = false;
		this.members.add(owner);
	}
	
	public static Scenario create(String name, boolean isPublic, Date expirationDate,
		String ownerEmail) {
		Scenario scenario = new Scenario(name, isPublic, expirationDate, User.find
																				.where()
																				.eq("email", ownerEmail)
																				.findUnique());
		scenario.save();
		scenario.saveManyToManyAssociations("members");
		return scenario;
	}
	
	public static Scenario edit(Long scenarioId, String newName, Boolean newIsPublic, Date newExpirationDate) {
		Scenario scenario = find.ref(scenarioId);
		if(newName != null && !newName.equals(scenario.name)) {
			scenario.name = newName;
			scenario.isAccepted = false;
		}
		if(newIsPublic != null && newIsPublic != scenario.isPublic) {
			scenario.isPublic = newIsPublic;
			scenario.isAccepted = false;
		}
		if(newExpirationDate != null && !newExpirationDate.equals(scenario.expirationDate)) {
			scenario.expirationDate = newExpirationDate;
			scenario.isAccepted = false;
		}
		scenario.save();
		return scenario;
	}
	
	public static List<Scenario> findOwned(String userEmail){
		return find.where().eq("owner.email",userEmail).findList();
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
	
	public static List<Scenario> findAvailable(String userEmail, int pageSize, int pageNum){
		Date date = new Date(System.currentTimeMillis());
		PagingList<Scenario> pagingList = find.where().or(
				com.avaje.ebean.Expr.gt("expirationDate", date),
				com.avaje.ebean.Expr.isNull("expirationDate"))
				.or(
				com.avaje.ebean.Expr.contains("members.email", userEmail),
				com.avaje.ebean.Expr.eq("isPublic", true)
				).findPagingList(pageSize);
		
		Page<Scenario> page = pagingList.getPage(pageNum);
		return page.getList();
	}
	
	public static List<Scenario> findNotExpired(Date date) {
		return find.where().or(
			com.avaje.ebean.Expr.lt("expirationDate", date),
			com.avaje.ebean.Expr.isNull("expirationDate")
			).findList();
	}
	
	public static boolean isMember(Long scenarioId, String userEmail){
		 return find.where()
			        .eq("members.email", userEmail)
			        .eq("id", scenarioId)
			        .findRowCount() > 0;
	}
	
	public static int getTotalPageCount(String user, int pageSize) {
		PagingList<Scenario> pagingList = find.where()
												.eq("members.email", user)
												.findPagingList(pageSize);
		return pagingList.getTotalPageCount();
	}
	
}
