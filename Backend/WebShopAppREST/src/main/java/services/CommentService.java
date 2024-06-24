package services;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Coco;
import beans.Comment;
import dao.CocoDAO;
import dao.CommentDAO;

@Path("/comments")
public class CommentService {
	
	@Context
	ServletContext ctx;
	private CommentDAO commentDAO;
	
	@PostConstruct
	public void init() {
	    System.out.println("USLO JE ODJE VALJDA");
	    commentDAO = (CommentDAO) ctx.getAttribute("commentDAO");
	    if (commentDAO == null) {
	    	commentDAO = new CommentDAO(ctx.getRealPath("/"));
			ctx.setAttribute("commentDAO", commentDAO);
	    }
	    /*} else {
	        System.out.println("commentDAO već postoji u kontekstu: " + ctx.getAttribute("commentDAO").getClass().getName());
	    }*/
	}
	
	@GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Comment> getComments() {
        System.out.println("Pozvana je metoda getComments()");
        return new ArrayList<>(commentDAO.getAllComments());
    }
	
	@GET
	@Path("/factory/{factoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Comment> GetCommentsByFactory(@PathParam("factoryId") String factoryId) {
	    System.out.println("USLO Je facory id " + factoryId);
	    CommentDAO commentDAO = (CommentDAO) ctx.getAttribute("commentDAO");
	    System.out.println("Bravo sava");
	    ArrayList<Comment> comments = commentDAO.GetCommentsByFactory("1");
	    System.out.println("Broj komentara: " + comments.size());
	    return comments;
	}

	
	
}