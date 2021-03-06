
/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package view;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import controller.UserController;
import exceptions.InvalidTokenException;
import model.user.Child;
import model.user.Credentials;
import model.user.Mentor;

@Path("/user")
public class UserRequest extends ViewSuper {
	private UserController userController = new UserController();

	public UserRequest() throws Exception {
		super();

	}

	/**
	 * TODO: register function. @ xml notation everywere
	 *
	 * @api {post} /user/register Registers a user
	 *
	 * @apiName register
	 * @apiGroup User
	 * @apiParam {String} email Email adres.
	 * @apiParam {String} username Username of user
	 * @apiParam {String} password User password
	 * @apiParam {String} profilepicture ProfilePicture
	 * @apiParam {String} name Fullname of user
	 *
	 * @apiError SQLException If there is a db error.
	 * @apiError UserDuplicate If the user already exist.
	 *
	 *
	 * @apiSuccess Success-Response: { MESSAGE: "Succesvol geregistreerd" ,
	 *             STATE: "SUCCEEDED" } }
	 *
	 * @apiError Error-Response: { MESSAGE:
	 *           "Er is iets fout gegaan met de mentor toevoegen" , STATE:
	 *           "ERROR" } }
	 *
	 * @param input
	 * @return
	 * @throws UnknownHostException
	 */
	@POST
	@Consumes("application/json")
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public String register(String input) throws UnknownHostException {
		return userController.addMentor(gson.fromJson(input, Mentor.class));
	}

	@POST
	@Consumes("application/json")
	@Path("/registerchild")
	@Produces(MediaType.APPLICATION_JSON)
	public String registerChild(@HeaderParam("token") String token, String input)
			throws UnknownHostException, SQLException, InvalidTokenException {
		System.out.println("date");
		System.out.println(input);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd").create();
		Child c = gson.fromJson(input, Child.class);
		Mentor m = session.getMentorFromToken(token);
		return userController.addChild(c, m);
	}

	@GET
	@Consumes("application/json")
	@Path("/loadchilds")
	@Produces(MediaType.APPLICATION_JSON)
	public String loadChilds(@HeaderParam("token") String token)
			throws UnknownHostException, SQLException, InvalidTokenException {
		Mentor m = session.getMentorFromToken(token);
		return userController.getChildsFromMentor(m);
	}

	/**
	 * @api {get} /user/info returns a mentor or child object based on token
	 *
	 * @apiName info
	 * @apiGroup User
	 * @apiParam {String} token Token for mentor or child object.
	 * @apiError SQLException If there is a db error.
	 *
	 *
	 * @apiSuccess Success-Response: user info object
	 *
	 * @apiError Error-Response:
	 *           "Er is iets misgegaan met het ophalen van jouw gegevens. Probeer het nog eens"
	 *
	 * @param token
	 * @return
	 * @throws UnknownHostException
	 */
	@GET
	@Path("/info")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMentor(@HeaderParam("token") String token) throws Exception {
		return userController.getUserInfo(token);

	}

	/**
	 * @api {post} /user/login Login function user
	 *
	 * @apiName login
	 * @apiGroup User
	 * @apiParam {String} username Username of user
	 * @apiParam {String} password User password
	 *
	 * @apiError SQLException If there is a db error.
	 * @apiError CredentialsMisMatch If the credentials are incorrect.
	 *
	 *
	 * @apiSuccess Success-Response: { MESSAGE: <token> , STATE: "SUCCEEDED" } }
	 *
	 * @apiError Error-Response: { MESSAGE: "Verkeerde inloggegevens" , STATE:
	 *           "ERROR" } }
	 **/
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public String login(String credentials) throws Exception {
        return sec.login(gson.fromJson(credentials, Credentials.class));
	}

	@GET
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public String logout(@HeaderParam("token") String token) throws InvalidTokenException, SQLException {
		return sec.logout(session.getUserFromToken(token));
	}

	@POST
	@Path("/forget")
	@Produces(MediaType.APPLICATION_JSON)
	public String forget(String credentials) throws JsonSyntaxException, SQLException {
		return userController.forgetPassword(credentials);
	}

	@POST
	@Path("/updatepassword")
	@Produces(MediaType.APPLICATION_JSON)
	public String updatepassword(String credentials) throws JsonSyntaxException, SQLException {
		return userController.updatePassword(credentials);
	}

	@GET
	@Produces("image/png")
	@Path("/profilepic/{id}")
	public byte[] getProfilePicture(@PathParam("id") int id) throws Exception {
		return userController.getProfilePicture(session.getUserFromId(id));
	}
}