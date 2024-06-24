package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import beans.User;
import beans.UserRegistration;
import enums.UserRole;
import enums.UserStatus;

public class UserDAO {
	
	private ArrayList<User> users = new ArrayList<>();
	private String FileLocation;
	
	public UserDAO() {
		
	}
	
	public UserDAO(String contextPath) {
		FileLocation = "C:\\Users\\HP\\OneDrive\\Radna površina\\web\\CocoFactory\\Backend\\WebShopAppREST\\src\\main\\webapp\\users.csv";
		loadUsers(FileLocation);
		System.out.println("Svi korisnici: ");
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
	
	public User getRegisteringUser(String username, String password) {
	    System.out.println("Trazi usera");
	    for (User user : users) {
	        System.out.println("Provera korisnika: " + user.getUsername() + ", " + user.getPassword());
	        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
	            System.out.println("nasao usera");
	            if(user.getUserStatus().equals(UserStatus.Active)) {
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
			user.setUserStatus(UserStatus.Active);
			if(type.equals("m")) {
				System.out.println("Registruje menadzera");
				user.setRole(UserRole.Manager);
			}
			else if(type.equals("c")){
				System.out.println("Registruje kupca");
				user.setRole(UserRole.Customer);
			}
		    users.add(user);
		    System.out.println("Korisnik registrovan");
		    SaveUserToFile();
		    return user;
		}

	}
	
	public UserRegistration getUserRegistrationById(String id) {
	    User user = getUserById(id);
	    if (user == null) {
	        System.out.println("Korisnik sa ID-om " + id + " nije pronađen");
	        return null;
	    }
	    return new UserRegistration(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getGender(), user.getBirthDate());
	}
	
	public Boolean updateUserForm(String id, UserRegistration updatedUser) {
		User user = getUserById(id);
        if (user != null) {
        	System.out.println("Korisnik naden koji  se updejtuje");
            user.setUsername(updatedUser.getUsername());
            user.setPassword(updatedUser.getPassword());
            user.setFirstName(updatedUser.getName());
            user.setLastName(updatedUser.getSurname());
            user.setGender(updatedUser.getGender());
            user.setBirthDate(updatedUser.getBirthDate());
            SaveUserToFile();
            return true;
        }
        return false;
		
	}
    
    public ArrayList<User> searchUsers(String name, String surname, String username, ArrayList<User> usersToSearch){
    	ArrayList<User> searchedUsers = new ArrayList<User>();
    	
    	for(User u : usersToSearch) {
    		boolean nameTrue = u.getFirstName().toLowerCase().contains(name.toLowerCase()) || name.equals("null");
    		boolean surnameTrue = u.getLastName().toLowerCase().contains(surname.toLowerCase()) || surname.equals("null");
    		boolean usernameTrue = u.getUsername().toLowerCase().contains(username.toLowerCase()) || username.equals("null");
    		
    		System.out.println("nameTrue: " +nameTrue);
    		System.out.println("surnameTrue: "+surnameTrue);
    		System.out.println("usernameTrue: "+usernameTrue);
    		boolean isTrue = nameTrue && surnameTrue && usernameTrue;
    		
    		if(isTrue) {
    			searchedUsers.add(u);
    			System.out.println("Pretrazeni user: "+u);
    		}
    	}
    	
    	return searchedUsers;
    }

	private void loadUsers(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath);
			in = new BufferedReader(new FileReader(file));
			String line, id="", firstName="", lastName="", username="", password="", gender="", birthDate="", roleStr="", statusStr="";
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
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
					statusStr = st.nextToken().trim();
				}
				UserStatus status = UserStatus.valueOf(statusStr);
	            UserRole role = UserRole.valueOf(roleStr);

	            User user = new User(id, firstName, lastName, username, password, gender, birthDate, role, status);
	            users.add(user);
	            System.out.println("Loaded user: " + user);
				
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
	
	private void SaveUserToFile() {
		System.out.println(FileLocation);
	    try (BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Users\\HP\\OneDrive\\Radna površina\\web\\CocoFactory\\Backend\\WebShopAppREST\\src\\main\\webapp\\users.csv"))) {
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
	            	String.valueOf(user.getUserStatus())
	            );
	            out.write(line);
	            out.newLine();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
