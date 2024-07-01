package services;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Coco;
import dao.CocoDAO;

@Path("/chocolates")
public class CocoService {
	@Context
	ServletContext ctx;
	private CocoDAO cocoDAO;
	
	@PostConstruct
	public void init() {
		cocoDAO = (CocoDAO) ctx.getAttribute("cocoDAO");
		if (cocoDAO == null) {
			cocoDAO = new CocoDAO(ctx.getRealPath("/"));
			ctx.setAttribute("cocoDAO", cocoDAO);
		}
	}
	
	@GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Coco> getCocos() {
        System.out.println("Pozvana je metoda getCocos()");
        return new ArrayList<>(cocoDAO.findAll());
    }
	
	@GET
    @Path("/factory/{factoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Coco> getCocolatesByFactory(@PathParam("factoryId") String factoryId) {
		CocoDAO cocoDAO = (CocoDAO) ctx.getAttribute("cocoDAO");
        return cocoDAO.getCocolatesByFactory(factoryId);
    }
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Coco getCocoById(@PathParam("id") String id) {
		CocoDAO cocoDAO = (CocoDAO) ctx.getAttribute("cocoDAO");
		return cocoDAO.findCoco(id);
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Coco addCoco(Coco coco) {
		CocoDAO cocoDAO = (CocoDAO) ctx.getAttribute(("cocoDAO"));
		System.out.println(coco.getId());
		return cocoDAO.save(coco);
	}
		
	@DELETE
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public boolean deleteCoco(@PathParam("id") String id) {
		CocoDAO cocoDAO = (CocoDAO) ctx.getAttribute("cocoDAO");
        return cocoDAO.deleteCoco(id);
    }
	
	@PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Coco updateCoco(@PathParam("id") String id, Coco updatedCoco) {
		CocoDAO cocoDAO = (CocoDAO) ctx.getAttribute("cocoDAO");
        return cocoDAO.updateCoco(id, updatedCoco);
    }
}
