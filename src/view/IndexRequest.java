/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package view;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.jetty.util.IO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import model.system.State;
import dao.ConnectorFactory;

/**
 * @author martijn
 *
 */

@Path("/")
public class IndexRequest extends ViewSuper {

	public IndexRequest() throws Exception {
		super();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String index() {
		return json.createJson(State.ERROR, "Nothing on index, please see https://docs.story.social");
	}

	@GET
	@Path("/favicon.ico")
	@Produces("image/x-icon")
	public byte[] favicon() throws IOException {
		return IO.readBytes(this.getClass().getClassLoader().getResource("init/favicon.ico").openStream());
	}

	/**
	 * @api {get} /up Gives status of system and database connection
	 * 
	 * @apiName up
	 * @apiGroup Index
	 * 
	 * @apiSuccessExample Success-Response:
	 *                    { MESSAGE:
	 *                    "yes plebian is running. Is database connection valid: true. time to prevent caching: 15:49:29"
	 *                    , STATE: "SUCCEEDED" } }
	 * 
	 * @param useragent
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/up")
	@Produces(MediaType.APPLICATION_JSON)
	public String available(@HeaderParam("user-agent") String useragent) throws Exception {
		Connection con = ConnectorFactory.getConnection();

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String apple = (useragent.contains("Macintosh"))
				? "<br><br><a href=\"https://www.debian.org/\">Apple user detected, please switch operating systeem</a>"
				: "";
		return json.createJson(State.PASSED,
				"yes " + InetAddress.getLocalHost().getHostName() + " is running. Is database connection valid: "
						+ con.isValid(20) + ". time to prevent caching: " + sdf.format(cal.getTime()) + apple);
	}
}
