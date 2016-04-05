package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.roadmap.Achievement;
import model.roadmap.Roadmap;
import model.roadmap.Step;

public class AchievementDAO {
	protected Logger log = Logger.getGlobal();
	protected Connection con;
	private PreparedStatement statement;

	public List<Roadmap> getAllCompletedAchievementsByChild(int child_id) throws SQLException {
		List<Roadmap> theRoadmaps = new ArrayList<Roadmap>();

		try {
			statement = con.prepareStatement(
					"SELECT Achievement.achievement_id, Achievement.name, Achievement.points, Roadmap.roadmap_id, Roadmap.name, Roadmap.description, Step.step_id, Step.name, Step.description, Step.completed"
							+ "FROM Roadmap JOIN Achievement ON Roadmap.achievement_id = Achievement.achievement_id"
							+ "JOIN Step ON Roadmap.roadmap_id = Step.roadmap_id"
							+ "JOIN Child_has_Roadmap ON Roadmap.roadmap_id = Child_has_Roadmap.roadmap_id"
							+ "WHERE Child_has_Roadmap.child_id = ?;");
			statement.setInt(1, child_id);

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				Achievement achievement = new Achievement(result.getInt("achievement_id"), result.getString("name"), result.getDouble("points"));
				Roadmap roadmap = new Roadmap(result.getShort("roadmap_id"), result.getString("name"), result.getString("Roadmap.description"), achievement);

				if (!theRoadmaps.contains(roadmap)) {
					Step step = new Step(result.getInt("step_id"), result.getString("name"), result.getString("Step.description"), result.getBoolean("completed"));
					roadmap.addStep(step);
					
					if(roadmap.isCompleted()) {
						theRoadmaps.add(roadmap);
					}
				} else {
					for (Roadmap r : theRoadmaps) {
						if (r.equals(achievement)) {

							Step step = new Step(result.getInt("step_id"), result.getString("name"), result.getString("Step.description"), result.getBoolean("completed"));
							if (!r.getSteps().contains(step)) {

								roadmap.addStep(step);
							}/* else {
								for (Question qu : q.getTheQuestions()) {
								if (qu.equals(question)) {
									Answer answer = new Answer(result.getString("answer"),
											result.getBoolean("correct"));
									qu.getTheAnswers().add(answer);
								}
							}
						}*/
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			statement.close();
		}
		return theRoadmaps;
	}

	public List<Achievement> getAllAchievementsByChild(int child_id) throws SQLException {
		List<Achievement> theAchievements = new ArrayList<Achievement>();

		try {
			statement = con.prepareStatement("SELECT Achievement.achievement_id, Achievement.name, Achievement.points"
					+ "FROM Roadmap JOIN Achievement ON Roadmap.achievement_id = Achievement.achievement_id"
					+ "JOIN Child_has_Roadmap ON Roadmap.roadmap_id = Child_has_Roadmap.roadmap_id"
					+ "WHERE Child_has_Roadmap.child_id = ?;");
			statement.setInt(1, child_id);

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				Achievement achievement = new Achievement(result.getShort("achievement_id"), result.getString("name"), result.getDouble("points"));
				theAchievements.add(achievement);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			statement.close();
		}
		return theAchievements;
	}
}