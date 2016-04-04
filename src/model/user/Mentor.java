package model.user;

import java.util.ArrayList;
import java.util.List;

import model.roadmap.Roadmap;

public class Mentor extends User {

	protected String email;
	protected List<Child> theChildren = new ArrayList<Child>();
	protected List<Roadmap> theRoadmaps = new ArrayList<Roadmap>();

	public Mentor() {
	}

	public Mentor(String email, List<Child> theChildren, List<Roadmap> theRoadmaps) {
		super();
		this.email = email;
		this.theChildren = theChildren;
		this.theRoadmaps = theRoadmaps;
	}

	protected String getEmail() {
		return email;
	}

	protected void setEmail(String email) {
		this.email = email;
	}

	protected List<Child> getTheChildren() {
		return theChildren;
	}

	protected void setTheChildren(List<Child> theChildren) {
		this.theChildren = theChildren;
	}

	protected boolean addChild(Child child) {
		return theChildren.add(child);
	}

	protected boolean remvoveChild(Child child) {
		return theChildren.remove(child);
	}

	protected List<Roadmap> getTheRoadmaps() {
		return theRoadmaps;
	}

	protected void setTheRoadmaps(List<Roadmap> theRoadmaps) {
		this.theRoadmaps = theRoadmaps;
	}

	protected boolean addRoadmap(Roadmap roadmap) {
		return theRoadmaps.add(roadmap);
	}

	protected boolean remvoveRoadmap(Roadmap roadmap) {
		return theRoadmaps.remove(roadmap);
	}

	@Override
	public String toString() {
		return super.toString() + "Mentor [email=" + email + ", theChildren=" + theChildren + ", theRoadmaps="
				+ theRoadmaps + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((theChildren == null) ? 0 : theChildren.hashCode());
		result = prime * result + ((theRoadmaps == null) ? 0 : theRoadmaps.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mentor other = (Mentor) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (theChildren == null) {
			if (other.theChildren != null)
				return false;
		} else if (!theChildren.equals(other.theChildren))
			return false;
		if (theRoadmaps == null) {
			if (other.theRoadmaps != null)
				return false;
		} else if (!theRoadmaps.equals(other.theRoadmaps))
			return false;
		return true;
	}
}