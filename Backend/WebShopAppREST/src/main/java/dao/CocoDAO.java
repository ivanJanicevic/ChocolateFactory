package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import beans.Coco;

public class CocoDAO {
	
	private HashMap<String, Coco> cocos = new HashMap<String, Coco>();
	private String fileLocation;
	
	public CocoDAO() {
		
	}
	
	public CocoDAO(String contextPath) {
		this.fileLocation = new File(contextPath, "chocolates.csv").getAbsolutePath();
		loadCocos(fileLocation);
		//System.out.println(cocos.size() + "eeeee");
	}

	public Collection<Coco> findAll() {
		return cocos.values();
	}
	
	public String findChocolateIdByName(String chocolateName) {
	    if (chocolateName == null) {
	        return null;
	    }
	    for (Coco coco : cocos.values()) {
	        if (coco.getName() != null && coco.getName().equalsIgnoreCase(chocolateName)) {
	            System.out.println("id cokolade je: " + coco.getId());
	            return coco.getId();
	        }
	    }
	    return null;
	}
	
	public List<String> findChocolateIdsByCategory(String category) {
	    List<String> chocolateIds = new ArrayList<>();
	    if (category == null) {
	        return chocolateIds;
	    }
	    for (Coco coco : cocos.values()) {
	        if (coco.getCategory() != null && coco.getCategory().equalsIgnoreCase(category)) {
	            chocolateIds.add(coco.getId());
	        }
	    }
	    return chocolateIds;
	}
	
	public List<String> findChocolateIdsByType(String type) {
	    List<String> chocolateIds = new ArrayList<>();
	    if (type == null) {
	        return chocolateIds;
	    }
	    for (Coco coco : cocos.values()) {
	        if (coco.getType() != null && coco.getType().equalsIgnoreCase(type)) {
	            chocolateIds.add(coco.getId());
	        }
	    }
	    return chocolateIds;
	}

	public Coco findCoco(String id) {
		return cocos.containsKey(id) ? cocos.get(id) : null;
	}
	
	public Coco findCocoByName(String name) {
		return cocos.containsKey(name) ? cocos.get(name) : null;
	}
	
	public Coco updateCoco(String id, Coco coco) {
		Coco c = cocos.containsKey(id) ? cocos.get(id) : null;
		if (c == null) {
			return save(coco);
		} else {
			c.setName(coco.getName());
			c.setCost(coco.getCost());
			c.setCategory(coco.getCategory());
			c.setType(coco.getType());
			c.setStatus(coco.getStatus());
			c.setMass(coco.getMass());
			c.setDetails(coco.getDetails());
			c.setPicture(coco.getPicture());
			c.setStock(coco.getStock());
			c.setDeleted(coco.isDeleted());
			c.setFactoryId(coco.getFactoryId());
		}
		
		return c;
	}
	
	public Coco save(Coco coco) {
	    coco.setId(generateNewId());
	    cocos.put(coco.getId(), coco);
	    saveChocolatesToFile();
	    return coco;
	}

	private String generateNewId() {
	    int newId = cocos.keySet().stream()
	        .mapToInt(id -> Integer.parseInt(id))
	        .max()
	        .orElse(0) + 1;
	    return String.valueOf(newId);
	}
	
	public boolean deleteCoco(String id) {
	    if (cocos.containsKey(id)) {
	        cocos.remove(id);
	        saveChocolatesToFile();
	        return true; // Uspesno obrisano
	    } else {
	        return false; // Objekat sa datim ID-om nije pronadjen
	    }
	}


	private void loadCocos(String filePath) {
		//System.out.println("OVde u laod je ucitano");
		BufferedReader in = null;
		try {
			File file = new File(filePath);
			in = new BufferedReader(new FileReader(file));
			String line, id = "", name = "", cost = "", category = "", type = "", status = "", mass = "", details = "", picture = "", stock = "", isDeleted = "", factoryId = "";
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
			    //System.out.println("Ukupno pročitano linija iz CSV datoteke: ");
				while (st.hasMoreTokens()) {
					id = st.nextToken().trim();
					name = st.nextToken().trim();
					cost = st.nextToken().trim();
					category = st.nextToken().trim();
					type = st.nextToken().trim();
					mass = st.nextToken().trim();
					details = st.nextToken().trim();
					picture = st.nextToken().trim();
					stock = st.nextToken().trim();
					isDeleted = st.nextToken().trim();
					factoryId = st.nextToken().trim();
					status = st.nextToken().trim();
				}
				cocos.put(id, new Coco(id, name, Double.parseDouble(cost), category, type, Double.parseDouble(mass), details, picture, Integer.parseInt(stock), Boolean.parseBoolean(isDeleted), factoryId, status));
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
	
	private void saveChocolatesToFile() {
		System.out.println(fileLocation);
	    try (BufferedWriter out = new BufferedWriter(new FileWriter(fileLocation))) {
	        for (Coco cocolates : cocos.values()) {
	        	System.out.println(cocolates.getName());
	            String line = String.join(";",
	                    cocolates.getId(),
	                    cocolates.getName(),
	                    String.valueOf(cocolates.getCost()),
	                    cocolates.getCategory(),
	                    cocolates.getType(),
	                    String.valueOf(cocolates.getMass()),
	                    cocolates.getDetails(),
	                    cocolates.getPicture(),
	                    String.valueOf(cocolates.getStock()),
	                    String.valueOf(cocolates.isDeleted()),
	                    cocolates.getFactoryId(),
	                    cocolates.getStatus()
	            );
	            out.write(line);
	            out.newLine();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public ArrayList<Coco> getCocolatesByFactory(String factoryId) {
		ArrayList<Coco> cocolates = new ArrayList<Coco>();
        for(Coco coco : cocos.values()) {
        	if(coco.getFactoryId().equals(factoryId)) {
        		if(coco.isDeleted()==false) {
        			cocolates.add(coco);
        		}
        	}
        }
		System.out.println(cocos.size() + "ovo je cocos");
		System.out.println(cocolates.size());
        return cocolates;
	}

}
