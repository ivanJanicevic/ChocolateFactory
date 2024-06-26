package beans;

import java.util.ArrayList;

public class Factory {
    private String id;
    private String name;
    private String status; // Promenjen tip sa FactoryStatus na String
    private String location;
    private String pathToLogo;
    private double rate;
    private boolean isDeleted;
    private String workingTime;
    private ArrayList<String> comments;
    private ArrayList<Integer> chocolateIds;
    
    public Factory() {
    }
    
    public Factory(String id, String name, String location, String pathToLogo, double rate,
            boolean isDeleted, String workingTime, ArrayList<Integer> chocolateIds) {
        super();
        this.id = id;
        this.name = name;
        this.location = location;
        this.pathToLogo = pathToLogo;
        this.rate = rate;
        this.isDeleted = isDeleted;
        this.workingTime = workingTime;
        this.comments = new ArrayList<String>();
        this.status = "Ne radi"; // Postavljeno podrazumevano stanje na "Ne radi"
        this.chocolateIds = chocolateIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPathToLogo() {
        return pathToLogo;
    }

    public void setPathToLogo(String pathToLogo) {
        this.pathToLogo = pathToLogo;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(String workingTime) {
        this.workingTime = workingTime;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }
    
    public void addComment(String comment) {
        this.comments.add(comment);
    }

	public ArrayList<Integer> getChocolateIds() {
		return chocolateIds;
	}

	public void setChocolateIds(ArrayList<Integer> chocolateIds) {
		this.chocolateIds = chocolateIds;
	}
	public void addChocolateById(Integer id) {
		this.chocolateIds.add(id);
	}
}
