package dao;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import exceptions.MissingPropertiesFile;
// Doei error
//import exceptions.DatabaseInsertException;
import logging.Level;
import mail.Mailer;
import model.user.Child;
import model.user.Mentor;
import model.user.User;

public class UserDAO extends DataAccesObject {
	public UserDAO() {
		super();
	}

	private PreparedStatement statement;
	private PreparedStatement childQuery;

	/**
	 * 
	 * @param email
	 * @return mentor ID
	 */
	public int emailExists(String email) {
		boolean found = false;
		int mentorId = -1;
		try {
			statement = con.prepareStatement("SELECT mentor_id FROM Mentor WHERE email = ?;");
			statement.setString(1, email);

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				mentorId = result.getInt("mentor_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.INFORMATIVE, "", "Gebruiker bestaat niet");
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
				e.printStackTrace();
			}
		}
		return mentorId;
	}

	public boolean userExists(String username) {
		boolean found = false;

		try {
			statement = con.prepareStatement("SELECT username FROM User WHERE username = ?;");
			statement.setString(1, username);

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				found = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.INFORMATIVE, "", "Gebruiker bestaat niet");
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
				e.printStackTrace();
			}
		}
		return found;
	}

	/**
	 * Should be private, addUser can only be called when adding mentor or child
	 * 
	 * @param theUser
	 * @return insertedID
	 */
	private int addUser(User theUser) {
		int id = 0;
		try {
			statement = con.prepareStatement(
					"INSERT INTO  `User` (`username` , `password` , `profile_picture`, `name`)	VALUES (?,  ?,  ?,  ?);",
					PreparedStatement.RETURN_GENERATED_KEYS);
			statement.setString(1, theUser.getUsername());
			statement.setString(2, org.apache.commons.codec.digest.DigestUtils.sha256Hex(theUser.getPassword()));
			statement.setString(3, theUser.getProfilePicture());
			statement.setString(4, theUser.getName());
			statement.executeUpdate();

			ResultSet generatedKey = statement.getGeneratedKeys();
			generatedKey.next();
			id = generatedKey.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "Couldn't add user");
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
				e.printStackTrace();
			}
		}
		return id;
	}

	/**
	 * Child should have a plaintext password in object. It hashes the password
	 * automatically
	 */
	public boolean addChild(Mentor theMentor, Child theChild) {
		try {
			int userId = addUser(theChild);
			childQuery = con.prepareStatement(
					"INSERT INTO  `Child` (`date_of_birth` ,`gender` , `user_id`, `mentor_id`)	VALUES (?,  ?, ?, ?);",
					PreparedStatement.RETURN_GENERATED_KEYS);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			childQuery.setString(1, sdf.format(theChild.getDateOfBirth()));
			childQuery.setString(2, theChild.getGender());
			childQuery.setInt(3, userId);
			childQuery.setInt(4, theMentor.getMentorId());

			if (childQuery.executeUpdate() <= 0) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "Couldn't add child");
		} finally {
			try {
				childQuery.close();
			} catch (SQLException e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * Mentor should have a plaintext password in object. It hashes the password
	 * automatically
	 */
	public boolean addMentor(Mentor theMentor) {

		try {
			int userId = addUser(theMentor);
			statement = con
					.prepareStatement("INSERT INTO  `Mentor` (`email` , `user_id`)	VALUES (?,  ?);");
			statement.setString(1, theMentor.getEmail());
			statement.setInt(2, userId);
			if (statement.executeUpdate() <= 0) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "Couldn't add Mentor");
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * Delete user, should not be public, and can only be called by deleteMentor
	 * or deleteUser
	 * 
	 * @param userID
	 *            the user to delete
	 * @return
	 */
	private boolean deleteUser(int userID) {
		try {
			PreparedStatement statement = con.prepareStatement("DELETE FROM user WHERE user_id = ?");
			statement.setInt(1, userID);
			if (statement.execute() != true) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "Couldn't delete user");
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * Deletes the specific mentor. All dependencies get the mentor_id 999, this
	 * is an anonymous account
	 * 
	 * @param mentorID
	 * @return
	 */
	public boolean deleteMentor(int mentorID) {
		try {
			// remove child dependency
			statement = con.prepareStatement("UPDATE Child SET mentor_id = 999 WHERE mentor_id = ?;");
			statement.setInt(1, mentorID);
			if (statement.executeUpdate() < 0) {
				System.out.println("execute error");
				return false;
			}
			// remove quiz dependency
			statement = con.prepareStatement("UPDATE Quiz SET mentor_id = 999 WHERE mentor_id = ?;");
			statement.setInt(1, mentorID);
			if (statement.executeUpdate() < 0) {
				return false;
			}

			// remove roadmap dependency
			statement = con.prepareStatement("UPDATE Roadmap SET mentor_id = 999 WHERE mentor_id = ?;");
			statement.setInt(1, mentorID);
			if (statement.executeUpdate() < 0) {
				return false;
			}

			// get to removed user ID
			int userId = 0;
			statement = con.prepareStatement("SELECT * FROM Mentor WHERE mentor_id = ?;");
			statement.setInt(1, mentorID);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				userId = result.getInt("user_id");
			}

			// remove mentor
			statement = con.prepareStatement("DELETE FROM Mentor WHERE mentor_id = ?;");
			statement.setInt(1, mentorID);
			if (statement.executeUpdate() <= 0) {
				return false;
			}
			// finally remove user
			statement = con.prepareStatement("DELETE FROM User WHERE user_id = ?;");
			statement.setInt(1, userId);
			if (statement.executeUpdate() <= 0) {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "Couldn't delete mentor");
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * Delete child and remove dependencies
	 * 
	 * @param childID
	 *            the childID to remove
	 * @return
	 */
	public boolean deleteChild(int childID) {
		try {
			// remove quiz dependency
			statement = con.prepareStatement("DELETE FROM Child_has_Quiz WHERE child_id = ?;");
			statement.setInt(1, childID);
			if (statement.executeUpdate() < 0) {
				return false;
			}
			// remove question dependency
			statement = con.prepareStatement("DELETE FROM Child_has_Question WHERE child_id = ?;");
			statement.setInt(1, childID);
			if (statement.executeUpdate() < 0) {
				return false;
			}
			// remove roadmap dependency
			statement = con.prepareStatement("DELETE FROM Child_has_Roadmap WHERE child_id = ?;");
			statement.setInt(1, childID);
			if (statement.executeUpdate() < 0) {
				return false;
			}
			// remove step dependency
			statement = con.prepareStatement("DELETE FROM Step_has_Child WHERE child_id = ?;");
			statement.setInt(1, childID);
			if (statement.executeUpdate() < 0) {
				return false;
			}

			// get to removed user ID
			int userId = 0;
			statement = con.prepareStatement("SELECT * FROM Child WHERE child_id = ?;");
			statement.setInt(1, childID);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				userId = result.getInt("user_id");
			}

			// remove child
			statement = con.prepareStatement("DELETE FROM Child WHERE child_id = ?;");
			statement.setInt(1, childID);
			if (statement.executeUpdate() <= 0) {
				return false;
			}
			// finally remove user
			statement = con.prepareStatement("DELETE FROM User WHERE user_id = ?;");
			statement.setInt(1, userId);
			if (statement.executeUpdate() <= 0) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "Couldn't delete Child");
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
			}
		}
		return true;
	}

	public byte[] getProfilePicture(User user) {
		try {
			statement = con.prepareStatement("SELECT profile_picture FROM User WHERE user_id = ?");
			statement.setInt(1, user.getUserId());

			ResultSet res = statement.executeQuery();
			while (res.next()) {
				return res.getBytes("profile_picture");
			}
		} catch (Exception e) {
			log.out(Level.ERROR, "getProfilePicture", "Error while getting profilepicture from database");
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
			}
		}
		return null;
	}

	// For testing purpose
	public int getLatestIdMentor() {
		int mentorId = 0;
		try {
			statement = con.prepareStatement("SELECT MAX(mentor_id) FROM Mentor");
			ResultSet result = statement.executeQuery();
			result.next();
			mentorId = result.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mentorId;
	}

	// For testing purpose
	public int getLatestIdChild() {
		int childId = 0;
		try {
			statement = con.prepareStatement("SELECT MAX(child_id) FROM Child");
			ResultSet result = statement.executeQuery();
			result.next();
			childId = result.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return childId;
	}
	
	/**
	 * @param mentor_id
	 * @return Mentor by given id
	 */
	public Mentor getMentorById(int mentor_id) {
		Mentor mentor = new Mentor();
		try {
			statement = con.prepareStatement("SELECT Mentor.mentor_id, Mentor.email, Mentor.user_id FROM Mentor WHERE Mentor.mentor_id = ?");
			statement.setInt(1, mentor_id);
			ResultSet result = statement.executeQuery();
			result.next();
			mentor = new Mentor(result.getInt("mentor_id"), result.getString("name"), result.getInt("mentor_id"));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
				e.printStackTrace();
			}
		}
		return mentor;
	}

	public boolean generatePasswordToken(String email) {
		int mentorId = emailExists(email);
		String randomToken = randomString();
		java.util.Date current = new java.util.Date();
		if (mentorId == -1) {
			return false;
		}
		try {
			PreparedStatement generateQuery = con.prepareStatement(
					"INSERT INTO  `Password_Token` (`token` ,`date_created`, `mentor_id`)	VALUES (?,  ?, ?);");
			generateQuery.setString(1, randomToken);
			generateQuery.setDate(2, new Date(current.getTime()));
			generateQuery.setInt(3, mentorId);

			if (generateQuery.executeUpdate() <= 0) {
				return false;
			} else {
				sentPasswordMail(email, randomToken);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "Couldn't add password token");
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
				e.printStackTrace();
			}
		}
		return true;
	}

	private void sentPasswordMail(String email, String randomToken) {
		// TODO Auto-generated method stub
		Mailer m = new Mailer();
		try {
			m.sentPasswordMail(email, randomToken);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingPropertiesFile e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String randomString() {
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	public boolean updatePassword(String token, String email, String newPassword) {
		boolean tokenFound = false;
		try {
			statement = con.prepareStatement("SELECT token FROM Password_Token WHERE token = ?");
			statement.setString(1, token);

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				tokenFound = true;
			}
			if (tokenFound) {
				PreparedStatement updateStatement = con.prepareStatement(
						"UPDATE User u JOIN Mentor m ON u.user_id = m.user_id AND m.email = ? " + "SET u.password = ?");
				updateStatement.setString(1, email);
				updateStatement.setString(2, org.apache.commons.codec.digest.DigestUtils.sha256Hex(newPassword));
				if (updateStatement.executeUpdate() < 0) {
					return false;
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.INFORMATIVE, "", "Gebruiker bestaat niet");
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				log.out(Level.ERROR, "", "Statement isn't closed");
				e.printStackTrace();
			}
		}
		return false;
	}

	public List<Child> getChilds(Mentor m) {
		List<Child> childs = new ArrayList<Child>();
		try {
			statement = con.prepareStatement("SELECT * FROM User INNER JOIN Child on User.user_id = Child.user_id WHERE mentor_id = ?");
			statement.setInt(1, m.getMentorId());
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				childs.add(new Child(results.getInt("user_id"), results.getDate("date_of_birth"),
						results.getString("gender"), results.getString("username"), null,
						"/account/profilepic/" + results.getInt("user_id"), results.getString("name")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return childs;
	}
}
