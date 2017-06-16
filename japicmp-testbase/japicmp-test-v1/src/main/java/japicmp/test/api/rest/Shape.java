package japicmp.test.api.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Shape rest class
 */
@Path("/shapes")
public class Shape {


	@POST
	@Path("/byId")
	public Response shape() {
		return null;
	}
}
