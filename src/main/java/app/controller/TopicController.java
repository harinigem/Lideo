package app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.model.Topic;
import app.repository.TopicRepository;

@RestController
@RequestMapping("/topics")
public class TopicController {

	@Autowired
	TopicRepository topicRepository;

	@RequestMapping(method = RequestMethod.POST)
	public Map<String, Object> createTopics(@RequestBody Map<String, Object> topicsMap) {
		Map<String, Object> response = new HashMap<String, Object>();
		Topic topic = new Topic();
		topic.setLevel((String) topicsMap.get("level"));
		topic.setCategory((String) topicsMap.get("category"));
		topic.setTopicName((String) topicsMap.get("topicName"));
		topic.setLinks((List<String>) topicsMap.get("links"));
		response.put("topics", topicRepository.save(topic));
		return response;
	}

//	@RequestMapping(method = RequestMethod.GET)
//	public Map<String, Object> getAllTopics() throws IOException {
//		Map<String, Object> response = new HashMap<String, Object>();
//		response.put("topics", topicRepository.findAll());
//		return response;
//	}
	
	@RequestMapping(method = RequestMethod.GET)
	public Map<String, Object> getAllTopics(@RequestParam("level") String level, @RequestParam(value = "categories", defaultValue = "all") List<String> categories) throws IOException {
		System.out.println("Fetching topics for " + level);
		Map<String, Object> response = new HashMap<String, Object>();
		List<Topic> topicsList = new ArrayList<>();
		if(categories.contains("all")) {
			topicsList = topicRepository.findByLevel(level);
		} else {
			topicsList = topicRepository.findByLevelAndCategoryIn(level, categories);
		}
		response.put("topics", topicsList);
		return response;
	}
	
}
