package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import beans.Coco;
import beans.Factory;

public class FactoryDAO {
	
	private HashMap<String, Factory> factories = new HashMap<>();
	private CocoDAO cocoDAO;
	private String fileLocation;
	
	public FactoryDAO() {
		factories.put("1", new Factory("1", "Blabla", "strpce", "http:", 2.3, false, "2.5", null));
	}
	
	public FactoryDAO(String contextPath) {
		//System.out.println("!");
		this.fileLocation = "C:\\Users\\HP\\OneDrive\\Radna površina\\web\\CocoFactory\\Backend\\WebShopAppREST\\src\\main\\webapp\\factories.csv";
		this.cocoDAO = new CocoDAO("C:\\Users\\HP\\OneDrive\\Radna površina\\web\\CocoFactory\\Backend\\WebShopAppREST\\src\\main\\webapp\\chocolates.csv");
		loadFactories(fileLocation);
	}

	public Collection<Factory> findAll() {
	    //System.out.println("OLEEE");
	    	
	    // Ažuriranje statusa svake fabrike
	    for (Factory factory : factories.values()) {
	        factory.setStatus(checkStatus(factory.getWorkingTime()));
	    }

	    // Sortiranje fabrika po statusu (Work > Do not work)
	    List<Factory> factoryList = new ArrayList<>(factories.values());
	    Collections.sort(factoryList, new Comparator<Factory>() {
	        @Override
	        public int compare(Factory f1, Factory f2) {
	            String status1 = f1.getStatus();
	            String status2 = f2.getStatus();

	            if (status1.equals("Work") && !status2.equals("Work")) {
	                return -1; 
	            } else if (!status1.equals("Work") && status2.equals("Work")) {
	                return 1; 
	            } else {
	                return 0;
	            }
	        }
	    });

	    return factoryList;
	}


	public Factory findFactory(String id) {
	    Factory factory = factories.get(id);
	    if (factory != null) {
	        factory.setStatus(checkStatus(factory.getWorkingTime()));
	    }
	    return factory;
	}

	
	public Factory updateFactory(String id, Factory factory) {
		Factory f = factories.containsKey(id) ? factories.get(id) : null;
		if (f == null) {
			return save(factory);
		} else {
			f.setName(factory.getName());
			f.setStatus(factory.getStatus());
			f.setLocation(factory.getLocation());
			f.setPathToLogo(factory.getPathToLogo());
			f.setRate(factory.getRate());
			f.setDeleted(factory.isDeleted());
			f.setWorkingTime(factory.getWorkingTime());
		}
		
		return f;
	}
	
	public Factory save(Factory factory) {
	    factory.setId(generateNewId());
	    factories.put(factory.getId(), factory);
	    saveFactoriesToFile();
	    return factory;
	}

	private String generateNewId() {
	    int newId = factories.keySet().stream()
	        .mapToInt(id -> Integer.parseInt(id))
	        .max()
	        .orElse(0) + 1;
	    return String.valueOf(newId);
	}
	
	public boolean deleteFactory(String id) {
	    if (factories.containsKey(id)) {
	        factories.remove(id);
	        saveFactoriesToFile();
	        return true; // Successfully deleted
	    } else {
	        return false; // Object with given ID not found
	    }
	}


	private void loadFactories(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath);
			System.out.println("!");
			in = new BufferedReader(new FileReader(file));
			String line, id = "", name = "", workingTime = "" , location = "", pathToLogo = "", rate = "", isDeleted = "";
			StringTokenizer st;
			System.out.println(contextPath);
			while ((line = in.readLine()) != null) {
			    line = line.trim();
			    if (line.equals("") || line.indexOf('#') == 0)
			        continue;
			    st = new StringTokenizer(line, ";");
			    while (st.hasMoreTokens()) {
			        id = st.nextToken().trim();
			        name = st.nextToken().trim();
			        workingTime = st.nextToken().trim();
			        location = st.nextToken().trim();
			        pathToLogo = st.nextToken().trim();
			        rate = st.nextToken().trim();
			        isDeleted = st.nextToken().trim();
			        String chocolateIdsStr = st.nextToken().trim(); // Dodato čitanje chocolateIds
			        ArrayList<Integer> chocolateIds = new ArrayList<>();
			        String[] chocolateIdsArray = chocolateIdsStr.split(",");
			        for (String chocolateId : chocolateIdsArray) {
			            chocolateIds.add(Integer.parseInt(chocolateId));
			        }
			        factories.put(id, new Factory(id, name, location, pathToLogo, Double.parseDouble(rate),
			                        Boolean.parseBoolean(isDeleted), workingTime, chocolateIds));
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( in != null ) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
		}
		
	}
	
	private void saveFactoriesToFile() {
	    try (BufferedWriter out = new BufferedWriter(new FileWriter(fileLocation))) {
	    	for (Factory factory : factories.values()) {
	    	    String chocolateIdsStr = String.join(",", factory.getChocolateIds().stream().map(String::valueOf).toArray(String[]::new));
	    	    String line = String.join(";",
	    	            factory.getId(),
	    	            factory.getName(),
	    	            factory.getStatus(),
	    	            factory.getLocation(),
	    	            factory.getPathToLogo(),
	    	            String.valueOf(factory.getRate()),
	    	            String.valueOf(factory.isDeleted()),
	    	            String.valueOf(factory.getWorkingTime()),
	    	            chocolateIdsStr // Dodato čuvanje chocolateIds
	    	    );
	    	    out.write(line);
	    	    out.newLine();
	    	}
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static String checkStatus(String workingHours) {
	    String[] times = parseWorkingHours(workingHours);
	    LocalTime[] timeValues = getTimeValues(times);

	    if (isWithinWorkingHours(timeValues[0], timeValues[1], timeValues[2])) {
	        return "Work";
	    } else {
	        return "Do not work";
	    }
	}

	private static String[] parseWorkingHours(String workingHours) {
	    String[] times = workingHours.split("-");
	    if (times.length != 2) {
	        throw new IllegalArgumentException("Invalid working hours format. Expected format: 'HH:mm-HH:mm'");
	    }
	    return times;
	}

	private static LocalTime[] getTimeValues(String[] times) {
	    LocalTime[] timeValues = new LocalTime[3];
	    timeValues[0] = parseTime(times[0]);
	    timeValues[1] = parseTime(times[1]);
	    timeValues[2] = getCurrentTime();
	    return timeValues;
	}

	private static LocalTime parseTime(String time) {
	    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	    return LocalTime.parse(time, timeFormatter);
	}

	private static LocalTime getCurrentTime() {
	    return LocalTime.now();
	}

	private static boolean isWithinWorkingHours(LocalTime startTime, LocalTime endTime, LocalTime currentTime) {
	    return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
	}

	public List<Factory> searchFactories(String factoryName, String chocolateName, String location, Double averageRating) {
	    List<Factory> result = new ArrayList<>();

	    for (Factory factory : factories.values()) {
	        if ((factoryName == null || factory.getName().equalsIgnoreCase(factoryName)) &&
	            (chocolateName == null || factoryContainsChocolate(factory, chocolateName)) &&
	            (location == null || factory.getLocation().equalsIgnoreCase(location)) &&
	            (averageRating == null || factory.getRate() >= averageRating)) {
	            
	            result.add(factory);
	        }
	    }

	    return result;
	}

	public boolean factoryContainsChocolate(Factory factory, String chocolateName) {
	    if (chocolateName == null || chocolateName.trim().isEmpty()) {
	        return false;
	    }
	    
	    String chocolateIdStr = cocoDAO.findChocolateIdByName(chocolateName);

	    if (chocolateIdStr == null) {
	        System.out.println("Čokolada nije pronađena za ime: " + chocolateName);
	        return false;
	    }

	    Integer chocolateId;

	    try {
	        chocolateId = Integer.parseInt(chocolateIdStr);
	    } catch (NumberFormatException e) {
	        System.out.println("Greška prilikom parsiranja ID-ja čokolade: " + chocolateIdStr);
	        return false;
	    }

	    if (!factory.getChocolateIds().contains(chocolateId)) {
	        System.out.println("Fabrika ne sadrži čokoladu sa ID-jem: " + chocolateId);
	        return false;
	    }

	    if (factory.isDeleted()) {
	        System.out.println("Fabrika je označena kao obrisana.");
	        return false;
	    }

	    return true;
	}



}
