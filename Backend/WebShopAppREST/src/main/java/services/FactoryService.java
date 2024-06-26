package services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Factory;
import dao.CocoDAO;
import dao.FactoryDAO;

@Path("/factories")
public class FactoryService {

    @Context
    ServletContext ctx;
    private FactoryDAO factoryDAO;

    @PostConstruct
    public void init() {
    	System.out.println("Pre");
        factoryDAO = (FactoryDAO) ctx.getAttribute("factoryDAO");
        if (factoryDAO == null) {
        	System.out.println("posle");
            factoryDAO = new FactoryDAO(ctx.getRealPath("/"));
            ctx.setAttribute("factoryDAO", factoryDAO);
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Factory> getFactories() {
        System.out.println("Pozvana je metoda getFactories()"); // Dodajte ovu liniju za ispisivanje poruke u konzolu
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
        Factory factory = factoryDAO.findFactory(id);
        if (factory != null) {
            String status = factoryDAO.checkStatus(factory.getWorkingTime());
            factory.setStatus(status);
        }
        return factory;
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
    
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Factory> searchFactories(@QueryParam("factoryName") String factoryName,
                                         @QueryParam("location") String location,
                                         @QueryParam("averageRating") Double averageRating,
                                         @QueryParam("chocolateName") String chocolateName) {
        List<Factory> result = new ArrayList<>();

        for (Factory factory : factoryDAO.findAll()) {
            if ((factoryName == null || factoryName.trim().isEmpty() || factory.getName().toLowerCase().contains(factoryName.toLowerCase())) &&
                (location == null || location.trim().isEmpty() || factory.getLocation().equalsIgnoreCase(location)) &&
                (chocolateName == null || chocolateName.trim().isEmpty() || factoryDAO.factoryContainsChocolate(factory, chocolateName)) &&
                (averageRating == null || factory.getRate() >= averageRating)) {
            	
            	result.add(factory);
            }
        }

        return result;
    }

}
