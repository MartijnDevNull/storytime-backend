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
package controller;

/**
 * @author martijn
 *
 * Handles all security functions
 *
 */

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.sql.SQLException;

import dao.SessionManagementDAO;
import model.system.State;
import model.user.Credentials;
import model.user.User;


public class Security {
    private SessionManagementDAO session;
    protected Json json = new Json();
    private UserController user = new UserController();

    public Security() throws Exception {
        session = new SessionManagementDAO();
    }

    /**
     * Escape HTML characters
     *
     * @param in
     * @return
     */
    public String escape(String in) {
        return escapeHtml4(in);
    }

    public String login(Credentials cred) throws Exception {
        String token = session.Login(cred);
        if (token != null) {
            return user.getUserInfo(token);
        } else {
            return json.createJson(State.ERROR, "Verkeerde inloggegevens");
        }
    }

    public String logout(User user) throws SQLException {
        if (session.logout(user)) {
            return json.createJson(State.PASSED, "Gebruiker is uitgelogd");
        } else {
            return json.createJson(State.ERROR, "Er is iets mis gegaan");
        }
    }
}
