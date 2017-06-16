package japicmp.test.api.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by lds on 14/06/2017.
 */
@Path("/vehicule")
public class Vehicule {


	@GET
	@Path("/byId")
	public Response vehicule() {
		return null;
	}
}
