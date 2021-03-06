package controller;

import java.util.List;

import org.json.simple.JSONObject;

import com.google.gson.Gson;

import dao.SessionManagementDAO;
import dao.UserDAO;
import model.system.State;
import model.user.Child;
import model.user.Mentor;
import model.user.User;

public class UserController {
	UserDAO userDAO;
	Json json = new Json();

	public UserController(){
		userDAO = new UserDAO();
	}

	public String addMentor(Mentor theMentor) {
		if (userExists(theMentor.getUsername())) {
			return json.createJson(State.ERROR, "User bestaat al");
		}
		if(userDAO.addMentor(theMentor)){
			return json.createJson(State.PASSED, "Succesvol geregistreerd");
		}
		return json.createJson(State.ERROR, "Registreren is niet gelukt");
	}

	public boolean userExists(String username) {
		if (userDAO.userExists(username)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Provides information about the user, wheter this is a Child or Mentor and
	 * the path the UI should point to
	 * 
	 * TODO: Maybe needs more info but can be added any time
	 * 
	 * @param token
	 * @return JSON String
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String getUserInfo(String token) throws Exception {
		SessionManagementDAO session = new SessionManagementDAO();
		JSONObject userInfo = new JSONObject();
		
		Child c = session.getChildFromToken(token);
		Mentor m = session.getMentorFromToken(token);

		if (c != null) {
			userInfo.put("Type", "Child");
			userInfo.put("Name", c.getName());
			userInfo.put("Username", c.getUsername());
			userInfo.put("Birthday", c.getDateOfBirth().toString());
			userInfo.put("Gender", c.getGender());
			userInfo.put("Token", token);

			return json.nestedJson(State.PASSED, userInfo);
		} else if (m != null) {
			userInfo.put("Type", "Mentor");
			userInfo.put("Name", m.getName());
			userInfo.put("Username", m.getUsername());
			userInfo.put("Email", m.getEmail());
			userInfo.put("Token", token);

			return json.nestedJson(State.PASSED, userInfo);
		}
		return json.createJson(State.ERROR, "Er is iets misgegaan met het ophalen van jouw gegevens. Probeer het nog eens");
	}

	public byte[] getProfilePicture(User user) throws Exception {
		byte[] profilePicture  = userDAO.getProfilePicture(user);
		if(profilePicture.length > 0){
			return profilePicture; 
		}
		throw new Exception("Kan profielfoto niet laden");
	}
	
	/**
	 * Request a password reset
	 * 
	 * @Todo NOT FINISHED
	 * 
	 * @param token
	 * @return JSON String
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String forgetPassword(String credentials) {
		Json jSon = new Json();
		String email = jSon.parseJsonKeyword(credentials, "email");
		if (userDAO.generatePasswordToken(email)) {
			return json.createJson(State.PASSED, "Wachtwoord succesvol opnieuw aangevraag, zie " + email + " voor verdere instructies");
		}
		return json.createJson(State.ERROR, "Email bestaat niet");
	}

	/**
	 * Checks if token is valid and then updates password provided in JSON
	 * @param credentials
	 * @return
	 */
	public String updatePassword(String credentials) {
		Json jSon = new Json();
		String token = jSon.parseJsonKeyword(credentials, "token");
		String email = jSon.parseJsonKeyword(credentials, "email");
		String newPassword = jSon.parseJsonKeyword(credentials, "password");
		if (userDAO.updatePassword(token, email, newPassword)) {
			return json.createJson(State.PASSED, "Uw wachtwoord is succesvol gewijzigd");
		}
		return json.createJson(State.ERROR, "Uw aanvraag is niet juist gevalideerd");
	}

	public String addChild(Child c, Mentor m) {
		if (userDAO.addChild(m, c)) {
			return json.createJson(State.PASSED, "Het kind is succesvol toegevoegd");
		}
		return json.createJson(State.ERROR, "Het kind is niet succesvol toegevoegd");
	}

	public String getChildsFromMentor(Mentor m) {
		List<Child> childs = userDAO.getChilds(m);
		Gson g = new Gson();
		return g.toJson(childs);
	}
}