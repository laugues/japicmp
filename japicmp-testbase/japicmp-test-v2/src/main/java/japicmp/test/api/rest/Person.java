package japicmp.test.api.rest;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * Person rest class
 */
@Path("/person")
public class Person {


	@GET
	@Path("/byId")
	public Response person() {
		return null;
	}


	@PUT
	@Path("/byId/{personId}")
	public Response updatePerson(@PathParam("personId") String personId) {
		return null;
	}
}
