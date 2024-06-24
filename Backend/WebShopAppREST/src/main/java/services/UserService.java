package services;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.User;
import beans.UserCredentials;
import beans.UserRegistration;
import dao.UserDAO;

@Path("/user")
public class UserService {
	@Context
	ServletContext ctx;
	private UserDAO userDAO;
	
	@PostConstruct
	public void init() {
		userDAO = (UserDAO) ctx.getAttribute("userDAO");
		if (userDAO == null) {
			userDAO = new UserDAO(ctx.getRealPath("/"));
			ctx.setAttribute("userDAO", userDAO);
		}
	}
	
	@PUT
    @Path("/update/{id}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") String id, UserRegistration updatedUser) {
        UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
        System.out.println("Korisnik se updejtuje");
        Boolean b=dao.updateUserForm(id,updatedUser);
        if (b) {
        	System.out.println("Korisnik updejtovan");
            return Response.ok().build();
        } else {
        	System.out.println("Korisnik NIJE updejtovan");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
	}
	
	@GET
	@Path("/profile/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUserById(@PathParam("id") String id) {
		UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
        User user = dao.getUserById(id);
        if (user != null) {
            return user;
        } else {
        	System.out.println("An error occurred while finding the user");
            return null;
        }
    }

	@POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateUser(UserCredentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        // Use userDAO directly
        User user = userDAO.getRegisteringUser(username, password);

        if (user != null) {
            return Response.ok().entity(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("User does not exist with username: " + username).build();
        }
    }
	
	@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserRegistration getUserRegById(@PathParam("id") String id) {
		System.out.println("Trazi korisnika po id");
		UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
        UserRegistration user = dao.getUserRegistrationById(id);
        if (user != null) {
        	System.out.println("User found");
            return user;
        } else {
        	System.out.println("An error occurred while finding the user");
            return null;
        }
    }
	
	@POST
	@Path("/register/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public User registerUser(@PathParam("type") String type, UserRegistration userRegistration) {
	    System.out.println("Registrovanje korisnika u register servisu");
	    User user = userDAO.registerUser(userRegistration, type);
	    if (user != null) {
	        System.out.println("Korisnik JESTE registrovan");
	        return user;
	    } else {
	        System.out.println("Korisnik NIJE registrovan");
	        return null;
	    }
	}
	
	@GET
	@Path("/allUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<User> getAllUsers() {
		System.out.println("Pozvana je metoda getAllUsers()");
		ArrayList<User> users = new ArrayList<>(userDAO.findAll());
        System.out.println("Broj korisnika: " + users.size());
        return users;
    }
	
	@PUT
	@Path("/searchUsers/{name}/{surname}/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ArrayList<User> searchUsers(@PathParam("name") String name,
									 	@PathParam("surname") String surname,
									 	@PathParam("username") String username,
									 		ArrayList<User> usersToSearch) {
		UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
	    System.out.println("Search users in service");
	    if(usersToSearch.isEmpty()) {
	    	System.out.println("LIST IS NULL");
	    }
	   return dao.searchUsers(name, surname, username, usersToSearch);
	}
	
}
