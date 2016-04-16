package app.model;

import java.util.List;

import org.springframework.data.annotation.Id;

public class User {

	@Id
	private String id;
	private String name;
	private List<String> topicsAttempted;
	
	public User(String name) {
		this.name = name;
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

	public List<String> getTopicsAttempted() {
		return topicsAttempted;
	}

	public void setTopicsAttempted(List<String> topicsAttempted) {
		this.topicsAttempted = topicsAttempted;
	}
	
}
