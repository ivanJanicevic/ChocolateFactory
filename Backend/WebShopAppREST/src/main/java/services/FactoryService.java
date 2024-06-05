package services;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Factory;
import dao.FactoryDAO;

@Path("/factories")
public class FactoryService {

    @Context
    ServletContext ctx;
    private FactoryDAO factoryDAO;

    @PostConstruct
    public void init() {
        factoryDAO = (FactoryDAO) ctx.getAttribute("factoryDAO");
        if (factoryDAO == null) {
            factoryDAO = new FactoryDAO(ctx.getRealPath("/"));
            ctx.setAttribute("factoryDAO", factoryDAO);
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Factory> getFactories() {
        return new ArrayList<>(factoryDAO.findAll());
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Factory addFactory(Factory factory) {
        return factoryDAO.save(factory);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Factory getFactory(@PathParam("id") String id) {
        return factoryDAO.findFactory(id);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean deleteFactory(@PathParam("id") String id) {
        return factoryDAO.deleteFactory(id);
    }
    
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Factory updateFactory(@PathParam("id") String id, Factory updatedFactory) {
        return factoryDAO.updateFactory(id, updatedFactory);
    }
}
