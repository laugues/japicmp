package japicmp.test.api.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by lds on 14/06/2017.
 */
@Path("/person")
public class Person {


	@Inject
	@GET
	@Path("/byId")
	public Response person() {
		return null;
	}

	@PUT
	@Path("/byId")
	public Response updatePerson() {
		return null;
	}
}
