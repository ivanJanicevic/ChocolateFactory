package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import beans.Factory;
import beans.Purchase;
import beans.User;
import beans.UserRegistration;
import enums.UserRole;
import enums.UserStatus;

public class UserDAO {
	
	private ArrayList<User> users = new ArrayList<>();
	private String FileLocation;
	private FactoryDAO factoryDAO;
	private PurchaseDAO purchaseDAO;
	public UserDAO() {
		
	}
	
	public UserDAO(String contextPath) {
		//FileLocation = "C:\\Users\\janic\\FAX\\SEMESTAR 6\\Veb programiranje\\CocoFactory\\veb-projekat\\Backend\\WebShopAppREST\\src\\main\\webapp\\users.csv";
		FileLocation = "C:\\Users\\HP\\OneDrive\\Radna površina\\najnoviji web projekat\\CocoFactory\\Backend\\WebShopAppREST\\src\\main\\webapp\\users.csv";
		factoryDAO = new FactoryDAO(contextPath);
		
		loadUsers(FileLocation);
		System.out.println("Svi korisnici: " + users.size());
	}

	public User getUserById(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
	
	public ArrayList<User> findAll() {
	    return users;
	}
	
	public ArrayList<User> getAllManagers(FactoryDAO factoryDAO) {
        ArrayList<User> managers = new ArrayList<>();
        this.factoryDAO = factoryDAO;
		loadUsers(FileLocation);
        for (User user : users) {
        	//System.out.println(user.getFactory().getId() + "ID JE:");
        	//System.out.println("Role" + user.getRole());
            if (user.getRole() == UserRole.Manager && user.getFactory().getId() == "0") {
                managers.add(user);
                System.out.println("ime" + user.getFirstName());
            }
        }
        //System.out.println(managers.size());
        return managers;
    }
	
	public User getRegisteringUser(String username, String password) {
	    //System.out.println("Trazi usera");
	    for (User user : users) {
	        System.out.println("Provera korisnika: " + user.getUsername() + ", " + user.getPassword());
	        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
	            System.out.println("nasao usera");
	            if(user.isActive()) {
	                return user;
	            } else {
	                System.out.println("nalog deaktiviran");
	                return user;
	            }
	        }
	    }
	    System.out.println("nije nasao usera");
	    return null;
	}
	
	public User registerUser(UserRegistration userReg, String type) {
		System.out.println("Korisnik treba da se registruje");
		System.out.println("Tip korisnika: " + type);
		System.out.println("Pol korisnika: " + userReg.getGender());
		boolean userExists = users.stream()
		        .anyMatch(u -> u.getUsername().equals(userReg.getUsername()));

		if (userExists) {
		    System.out.println("Postoji vec korisnik sa ovim podacima");
		    return null;
		} else {
			User user = new User();
			Integer maxId = -1;
			for (User f : users) {
			    int idNum = Integer.parseInt(f.getId());
			    if (idNum > maxId) {
			        maxId = idNum;
			    }
			}
			maxId++;
			user.setId(maxId.toString());
			user.setBirthDate(userReg.getBirthDate());
			user.setGender(userReg.getGender());
			user.setFirstName(userReg.getName());
			user.setPassword(userReg.getPassword());
			user.setUsername(userReg.getUsername());
			user.setLastName(userReg.getSurname());
			user.setPoints(0);
			user.setType("Silver");
			user.setStatus(UserStatus.ACTIVATED);
			user.setCanceled(0);
			//user.setUserStatus(UserStatus.Active);
			user.setFactory(new Factory());
			if(type.equals("m")) {
				System.out.println("Registruje menadzera");
				user.setRole(UserRole.Manager);
			}
			else if(type.equals("c")){
				System.out.println("Registruje kupca");
				user.setRole(UserRole.Customer);
			}
			else if(type.equals("w")){
				System.out.println("Registruje kupca");
				user.setRole(UserRole.Worker);
				System.out.println("Radnik radi na: " + user.getFactory().getId());
			}
		    users.add(user);
		    System.out.println("Korisnik registrovan");
		    SaveUserToFile();
		    return user;
		}

	}
	public User registerUserWorker(UserRegistration userReg, String type, String factoryId) {
		System.out.println("Korisnik treba da se registruje");
		System.out.println("Tip korisnika: " + type);
		System.out.println("Pol korisnika: " + userReg.getGender());
		boolean userExists = users.stream()
		        .anyMatch(u -> u.getUsername().equals(userReg.getUsername()));

		if (userExists) {
		    System.out.println("Postoji vec korisnik sa ovim podacima");
		    return null;
		} else {
			User user = new User();
			Integer maxId = -1;
			for (User f : users) {
			    int idNum = Integer.parseInt(f.getId());
			    if (idNum > maxId) {
			        maxId = idNum;
			    }
			}
			maxId++;
			user.setId(maxId.toString());
			user.setBirthDate(userReg.getBirthDate());
			user.setGender(userReg.getGender());
			user.setFirstName(userReg.getName());
			user.setPassword(userReg.getPassword());
			user.setUsername(userReg.getUsername());
			user.setLastName(userReg.getSurname());
			user.setPoints(0);
			user.setType("Silver");
			user.setStatus(UserStatus.ACTIVATED);
			user.setCanceled(0);
			//user.setUserStatus(UserStatus.Active);
			user.setFactory(factoryDAO.findFactory(factoryId));
			if(type.equals("m")) {
				System.out.println("Registruje menadzera");
				user.setRole(UserRole.Manager);
			}
			else if(type.equals("c")){
				System.out.println("Registruje kupca");
				user.setRole(UserRole.Customer);
			}
			else if(type.equals("w")){
				System.out.println("Registruje kupca");
				user.setRole(UserRole.Worker);
				System.out.println("Radnik radi na: " + user.getFactory().getId());
			}
		    users.add(user);
		    System.out.println("Korisnik registrovan");
		    SaveUserToFile();
		    return user;
		}

	}
	
	public UserRegistration getUserRegistrationById(String id) {
		System.out.println("JES USLO");
		System.out.println("getUserRegistraionByID" + id);
	    User user = getUserById(id);
	    if (user == null) {
	        System.out.println("Korisnik sa ID-om " + id + " nije pronađen");
	        return null;
	    }
	    return new UserRegistration(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getGender(), user.getBirthDate());
	}
	
	public Boolean updateUserForm(String id, User updatedUser) {
		User user = getUserById(id);
        if (user != null) {
        	System.out.println("Korisnik naden koji  se updejtuje");
            user.setUsername(updatedUser.getUsername());
            user.setPassword(updatedUser.getPassword());
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setGender(updatedUser.getGender());
            user.setBirthDate(updatedUser.getBirthDate());
            user.setActive(updatedUser.isActive());
            user.setStatus(updatedUser.getStatus());
            user.setPoints(updatedUser.getPoints());
            user.setType(updatedUser.getType());
            user.setCanceled(updatedUser.getCanceled());
            SaveUserToFile();
            return true;
        }
        return false;
		
	}
	
	public Boolean updateManagerForm(String factoryId, User updatedUser, FactoryDAO factoryDAO) {
		System.out.println("Uslo u update form mnager");
		User user = updatedUser;
        if (user != null) {
        	System.out.println("Korisnik naden koji  se updejtuje");
        	Factory factory = factoryDAO.findFactory(factoryId);
        	System.out.println(factory);
            user.setFactory(factory);
            System.out.println(user.getFactory().getId() + "<-Ovo je id");
            SaveUserToFile();
            return true;
        }
        return false;
		
	}

	private void loadUsers(String contextPath) {
		BufferedReader in = null;
		users.clear();
		try {
			File file = new File(contextPath);
			in = new BufferedReader(new FileReader(file));
			String line, id="", firstName="", lastName="", username="", password="", gender="", birthDate="", roleStr="", isActive="", statusStr="", factoryId="", points="", type="", canceled="";
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				System.out.println(line + "Linija");
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					id = st.nextToken().trim();
					firstName = st.nextToken().trim();
					lastName = st.nextToken().trim();
					username = st.nextToken().trim();
					password = st.nextToken().trim();
					gender = st.nextToken().trim();
					birthDate = st.nextToken().trim();
					roleStr = st.nextToken().trim();
					isActive = st.nextToken().trim();
					statusStr = st.nextToken().trim();
					//System.out.println(statusStr);
					factoryId = st.nextToken().trim();
					points = st.nextToken().trim();
					type = st.nextToken().trim();
					canceled = st.nextToken().trim();
					
					//System.out.println(canceled + "-------------------------------------------broj otkazanih");
				}
				UserStatus status = UserStatus.valueOf(statusStr);
	            UserRole role = UserRole.valueOf(roleStr);
	            
            	Factory factory = new Factory();
            	System.out.println("Ako je 0:" + factory.getId());
	            if(!factoryId.equals("0")) {
	            	System.out.println("Razlicito je: " + factoryId);
		            factory = factoryDAO.findFactory(factoryId);
	            }
	            type = CheckType(Double.parseDouble(points));
	            User user = new User(id, firstName, lastName, username, password, gender, birthDate, role, Boolean.parseBoolean(isActive), status, factory, Double.parseDouble(points), type, Integer.parseInt(canceled));
	            users.add(user);
	            System.out.println("Loaded user: " + user);
	            SaveUserToFile();
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
		}
	}
	
	private String CheckType(Double points) {
		String type = "Bronze";
		System.out.println("POENI: " + points);
    	if(points > 3000 && points < 6000) {
    		type = "Silver";
    	} else if (points >= 6000) {
    		type = "Golden";
    	} else if (points <= 3000) {
    		type = "Bronze";
    	}
    	System.out.println("TIP: " + type);
    	return type;
    }
	
	private void SaveUserToFile() {
		System.out.println(FileLocation);
	    try (BufferedWriter out = new BufferedWriter(new FileWriter(FileLocation))) {
	        for (User user : users) {
	        	System.out.println(user.getFirstName());
	            String line = String.join(";",
	            	user.getId(),
	            	user.getFirstName(),
	            	user.getLastName(),
	            	user.getUsername(),
	            	user.getPassword(),
	            	user.getGender(),
	            	user.getBirthDate(),
	            	String.valueOf(user.getRole()),
	            	String.valueOf(user.isActive()),
	            	String.valueOf(user.getStatus()),
	            	user.getFactory().getId(),
	            	String.valueOf(user.getPoints()),
	            	user.getType(),
	            	String.valueOf(user.getCanceled())
	            );
	            out.write(line);
	            out.newLine();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public List<User> searchUsers(String firstName, String lastName, String username, UserRole role, String type) {
	    List<User> result = new ArrayList<>();

	    for (User user : users) {
	        boolean matches = true;

	        if (firstName != null && !user.getFirstName().toLowerCase().contains(firstName.toLowerCase())) {
	            matches = false;
	        }
	        
	        if (lastName != null && !user.getLastName().equalsIgnoreCase(lastName)) {
	            matches = false;
	        }
	        
	        if (username != null && !user.getUsername().equalsIgnoreCase(username)) {
	            matches = false;
	        }

	        if (role != null && user.getRole() != role) {
	            matches = false;
	        }
	        
	        if (type != null && !user.getType().equalsIgnoreCase(type)) {
	            matches = false;
	        }

	        if (matches) {
	            result.add(user);
	        }
	    }

	    return result;
	}

	
	public List<User> sortUsers(String criterion, boolean ascending) {
        List<User> sortedUsers = new ArrayList<>(users);

        Comparator<User> comparator;

        switch (criterion.toLowerCase()) {
            case "firstname":
                comparator = Comparator.comparing(User::getFirstName);
                break;
            case "lastname":
                comparator = Comparator.comparing(User::getLastName);
                break;
            case "username":
                comparator = Comparator.comparing(User::getUsername);
                break;
            case "points":
            	comparator = Comparator.comparingDouble(User::getPoints);
            default:
                throw new IllegalArgumentException("Nepoznat kriterijum za sortiranje: " + criterion);
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        Collections.sort(sortedUsers, comparator);
        return sortedUsers;
    }
}
