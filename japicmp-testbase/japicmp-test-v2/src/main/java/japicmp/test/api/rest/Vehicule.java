package japicmp.test.api.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Vehicule rest class
 */
@Path("/vehicule")
public class Vehicule {


	@POST
	@Path("/byId")
	public Response vehicule() {
		return null;
	}
}
