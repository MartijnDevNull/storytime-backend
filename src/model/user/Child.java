package model.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dao.QuizDAO;
import dao.RoadmapDAO;
import model.quiz.Quiz;
import model.roadmap.Achievement;
import model.roadmap.Roadmap;

public class Child extends User {
	
	private int childId;
	private Date dateOfBirth;
	private String gender;
	private List<Roadmap> theRoadmaps = new ArrayList<Roadmap>();
	private List<Quiz> theQuizes = new ArrayList<Quiz>();

	public Child() {
	}

	public Child(int id, Date dateOfBirth, String gender, String username, String password, String profilePicture, String name) {
		super(username, password, profilePicture, name);
		this.childId = id;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
	}
	
	public int getChildId() {
		return childId;
	}

	public void setChildId(int id) {
		this.childId = id;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<Roadmap> getTheRoadmaps() {
		return theRoadmaps;
	}

	public void setTheRoadmaps(List<Roadmap> theRoadmaps) {
		this.theRoadmaps = theRoadmaps;
	}

	public boolean addRoadmap(Roadmap roadmap) {
		return theRoadmaps.add(roadmap);
	}

	public boolean removeRoadmap(Roadmap roadmap) {
		return theRoadmaps.remove(roadmap);
	}
	
	public List<Quiz> getTheQuizes() {
		return theQuizes;
	}

	public void setTheQuizes(List<Quiz> theQuizes) {
		this.theQuizes = theQuizes;
	}
	
	public boolean addQuiz(Quiz quiz) {
		return theQuizes.add(quiz);
	}

	public boolean removeQuiz(Quiz quiz) {
		return theQuizes.remove(quiz);
	}

	public List<Achievement> getAllUncompletedAchievements() {
		List<Achievement> theAchievements = new ArrayList<Achievement>();
		RoadmapDAO roadmapDAO = new RoadmapDAO();
		for(Roadmap roadmap : roadmapDAO.getAllRoadmapsByChild(this)) {
			if(!roadmap.isCompleted()) {
				theAchievements.add(roadmap.getAchievement());
			}
		}
		return theAchievements;
	}
	
	public List<Achievement> getAllCompletedAchievements() {
		List<Achievement> theAchievements = new ArrayList<Achievement>();
		RoadmapDAO roadmapDAO = new RoadmapDAO();
		for(Roadmap roadmap : roadmapDAO.getAllRoadmapsByChild(this)) {
			if(roadmap.isCompleted()) {
				theAchievements.add(roadmap.getAchievement());
			}
		}
		return theAchievements;
	}
	
	public double getAllAchievementPoints() {
		double totalPoints = 0;
		for(Achievement achievement : getAllCompletedAchievements()) {
			totalPoints += achievement.getPoints();
		}
		return totalPoints;
	}
	
	public List<Quiz> getAllUncompletedQuizes() {
		List<Quiz> theQuizes = new ArrayList<Quiz>();
		QuizDAO quizDAO = new QuizDAO();
		for(Quiz quiz : quizDAO.getAllQuizesByChild(getChildId())) {
			if(!quiz.isCompleted()) {
				theQuizes.add(quiz);
			}
		}
		return theQuizes;
	}
	
	public List<Quiz> getAllCompletedQuizes() {
		List<Quiz> theQuizes = new ArrayList<Quiz>();
		QuizDAO quizDAO = new QuizDAO();
		for(Quiz quiz : quizDAO.getAllQuizesByChild(getChildId())) {
			if(quiz.isCompleted()) {
				theQuizes.add(quiz);
			}
		}
		return theQuizes;
	}

	@Override
	public String toString() {
		return "Child [childId=" + childId + ", dateOfBirth=" + dateOfBirth + ", gender=" + gender + ", theRoadmaps="
				+ theRoadmaps + ", theQuizes=" + theQuizes + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
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
		Child other = (Child) obj;
		if (dateOfBirth == null) {
			if (other.dateOfBirth != null)
				return false;
		} else if (!dateOfBirth.equals(other.dateOfBirth))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		return true;
	}
}