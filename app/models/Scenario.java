package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;

@Entity
public class Scenario extends Model {

	@Id
	public Long id;
	public String name;
	public boolean isPublic;
	public boolean isAccepted;
	public Date expirationDate;
	@ManyToOne
	public User owner;
	@ManyToMany
	public List<User> members = new ArrayList<User>(); 
	@OneToMany(cascade = CascadeType.ALL)
	public List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
	
	public static Model.Finder<Long, Scenario> find =
			new Finder<Long, Scenario>(Long.class, Scenario.class);
	
	public Scenario(String name, boolean isPublic, Date expirationDate, User owner) {
		this.name = name;
		this.isPublic = isPublic;
		this.expirationDate = expirationDate;
		this.owner = owner;
		this.isAccepted = false;
		this.members.add(owner);
	}
	
	public static Scenario create(String name, boolean isPublic, Date expirationDate,
			String owner) {
		Scenario scenario = new Scenario(name, isPublic, expirationDate, User.find.ref(owner));
		scenario.save();
		scenario.saveManyToManyAssociations("members");
		return scenario;
	}
	
	public static List<Scenario> findInvolving(String user) {
		return find.where()
				.eq("members.email", user)
				.findList();
	}
	
	public static List<Scenario> findNotExpired(Date date) {
		return find.where().or(
				com.avaje.ebean.Expr.lt("expirationDate", date),
				com.avaje.ebean.Expr.isNull("expirationDate")
				).findList();
	}
}
