package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import beans.Comment;

public class CommentDAO {
	
	private HashMap<String, Comment> comments = new HashMap<String, Comment>();
    private String fileLocation;
    
    public CommentDAO() {
    	
    }
    
    public CommentDAO(String fileLocation) {
        fileLocation = "C:\\Users\\HP\\OneDrive\\Radna površina\\web\\CocoFactory\\Backend\\WebShopAppREST\\src\\main\\webapp\\comments.csv";
        loadCommentsFromFile();
    }
    
    public void addComment(Comment comment) {
        comments.put(comment.getId(), comment);
        saveCommentsToFile();
    }
    
    public Comment getCommentById(String id) {
        return comments.get(id);
    }
    
    public Collection<Comment> getAllComments() {
        return comments.values();
    }
    
    private void loadCommentsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileLocation))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    String id = parts[0];
                    String text = parts[1];
                    String factoryId = parts[2];
                    comments.put(id, new Comment(id, text, factoryId));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveCommentsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocation))) {
            for (Comment comment : comments.values()) {
                writer.write(comment.getId() + ";" + comment.getText() + ";" + comment.getFactoryId());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<Comment> GetCommentsByFactory(String factoryId) {
    	System.out.println("OVO JE TO" + comments.size() + "Borj id:" + factoryId);
    	ArrayList<Comment> comms = new ArrayList<Comment>();
        for(Comment coco : comments.values()) {
            System.out.println("Checking comment with factoryId: " + coco.getFactoryId() + " against " + factoryId);
        	if(coco.getFactoryId().equals(factoryId)) {
        		comms.add(coco);
        	}
        }
        return comms;
    }
}