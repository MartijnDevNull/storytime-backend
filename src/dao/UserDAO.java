package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import logging.Level;
import model.user.Child;
import model.user.Mentor;
import model.user.User;

public class UserDAO extends DataAccesObject {
	public UserDAO() throws Exception {
		super();
	}

	private PreparedStatement statement;

	public boolean userExists(String username) throws SQLException {
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
		} finally {
			statement.close();
		}
		return found;
	}
	
	/**
	 * Should be private, addUser can only be called when adding mentor or child
	 * @param theUser
	 * @return insertedID
	 * @throws SQLException 
	 */
	private int addUser(User theUser) throws SQLException {
		int id = 0;
		try {
			statement = con.prepareStatement("INSERT INTO  `storytime`.`User` (`username` , `password` , `profile_picture`, `name`)	VALUES (?,  ?,  ?,  ?);", PreparedStatement.RETURN_GENERATED_KEYS);
			statement.setString(1, theUser.getUsername());
			statement.setString(2, org.apache.commons.codec.digest.DigestUtils.sha256Hex(theUser.getPassword()));
			statement.setString(3, theUser.getProfilePicture());
			statement.setString(4, theUser.getName());
			if(statement.execute() != true) {
				return 0;
			}
			ResultSet generatedKey = statement.getGeneratedKeys();
			while(generatedKey.next())
			{
				id = generatedKey.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "adding mentor failed");
		} finally {
			statement.close();
		}
		return id;
	}
	
	/**
	 * Child should have a plaintext password in object. It hashes the password
	 * automatically
	 */
	public boolean addChild(Mentor theMentor, Child theChild) throws SQLException {
		try {
			int userId = addUser(theChild);
			PreparedStatement childQuery = con.prepareStatement("INSERT INTO  `storytime`.`Child` (`date_of_birth` ,`gender` , `user_id`, `mentor_id`)	VALUES (?,  ?, ?, ?);", PreparedStatement.RETURN_GENERATED_KEYS);
			childQuery.setString(1, theChild.getDateOfBirth().toString());
			childQuery.setString(2, theChild.getGender());
			childQuery.setInt(3, userId);
			childQuery.setInt(3, theMentor.getId());
		
			if(childQuery.execute() != true) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "adding child failed");
		} finally {
			statement.close();
		}
		return true;
	}
	
	/**
	 * Mentor should have a plaintext password in object. It hashes the password
	 * automatically
	 */
	public boolean addMentor(Mentor theMentor) throws SQLException {
		
		try {
			int userId = addUser(theMentor);
			PreparedStatement mentorQuery = con.prepareStatement("INSERT INTO  `storytime`.`Mentor` (`email` , `user_id`)	VALUES (?,  ?);");
			mentorQuery.setString(1, theMentor.getEmail());
			mentorQuery.setInt(2, userId);
			if(mentorQuery.execute() != true) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "adding mentor failed");
		} finally {
			statement.close();
		}
		return true;
	}
	
	/**
	 * Delete user, should not be public, and can only be called by deleteMentor or deleteUser
	 * @param userID the user to delete
	 * @return
	 * @throws SQLException
	 */
	private boolean deleteUser(int userID) throws SQLException {
		try {
			PreparedStatement statement = con.prepareStatement("DELETE FROM user WHERE user_id = ?");
			statement.setInt(1, userID);
			if(statement.execute() != true) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "Deleting the user has failed");
		} finally {
			statement.close();
		}
		return true;
	}
	
	/**
	 * Deletes the specific mentor. All dependencies get the mentor_id 999,
	 * this is an anonymous account
	 * @param mentorID
	 * @return
	 * @throws SQLException
	 */
	public boolean deleteMentor(int mentorID) throws SQLException
	{
		try {
			// remove child dependency
			statement = con.prepareStatement("UPDATE Child SET mentor_id = 999 WHERE mentor_id = ?;");
			statement.setInt(1, mentorID);
			if(statement.execute() != true) {
				return false;
			}
			// remove quiz dependency
			statement = con.prepareStatement("UPDATE Quiz SET mentor_id = 999 WHERE mentor_id = ?;");
			statement.setInt(1, mentorID);
			if(statement.execute() != true) {
				return false;
			}

			// remove roadmap dependency
			statement = con.prepareStatement("UPDATE Roadmap SET mentor_id = 999 WHERE mentor_id = ?;");
			statement.setInt(1, mentorID);
			if(statement.execute() != true) {
				return false;
			}

			// remove mentor
			statement = con.prepareStatement("DELETE FROM Mentor WHERE mentor_id = ?;");
			statement.setInt(1, mentorID);
			if(statement.execute() != true) {
				return false;
			}
			// remove user
			statement = con.prepareStatement("SELECT * FROM Mentor WHERE mentor_id = ?;");
			statement.setInt(1, mentorID);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				deleteUser(result.getInt("user_id"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "Deleting mentor went wrong");
		} finally {
			statement.close();
		}
		return true;
	}
	
	/**
	 * Delete child and remove dependencies
	 * @param childID the childID to remove
	 * @return
	 * @throws SQLException
	 */
	public boolean deleteChild(int childID) throws SQLException
	{
		try {
			// remove quiz dependency
			statement = con.prepareStatement("DELETE FROM Child_has_Quiz WHERE child_id = ?;");
			statement.setInt(1, childID);
			if(statement.execute() != true) {
				return false;
			}
			// remove question dependency
			statement = con.prepareStatement("DELETE FROM Child_has_Question WHERE child_id = ?;");
			statement.setInt(1, childID);
			if(statement.execute() != true) {
				return false;
			}
			// remove roadmap dependency
			statement = con.prepareStatement("DELETE FROM Child_has_Roadmap WHERE child_id = ?;");
			statement.setInt(1, childID);
			if(statement.execute() != true) {
				return false;
			}
			// remove step dependency
			statement = con.prepareStatement("DELETE FROM Step_has_Child WHERE child_id = ?;");
			statement.setInt(1, childID);
			if(statement.execute() != true) {
				return false;
			}

			// remove child
			statement = con.prepareStatement("DELETE FROM Child WHERE child_id = ?;");
			statement.setInt(1, childID);
			if(statement.execute() != true) {
				return false;
			}
			// remove user
			statement = con.prepareStatement("SELECT * FROM Child WHERE child_id = ?;");
			statement.setInt(1, childID);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				deleteUser(result.getInt("user_id"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			log.out(Level.ERROR, "", "Deleting child went wrong");
		} finally {
			statement.close();
		}
		return true;
	}

	public byte[] getProfilePicture(User user) throws SQLException {
		try {
			statement = con.prepareStatement("SELECT profile_picture FROM User WHERE user_id = ?");
			statement.setInt(1, user.getId());

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
				log.out(Level.ERROR, "getProfilePicture", "Can't close database streams");
			}
		}
		return null;
	}
}
