package app.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import app.model.Topic;

public interface TopicRepository extends MongoRepository<Topic, String>{
	
	public List<Topic> findByLevelAndCategoryIn(String level, List<String> categories);
	
	public List<Topic> findByLevel(String level);
	
	public List<Topic> findByIdIn(List<String> topics);
	
}
