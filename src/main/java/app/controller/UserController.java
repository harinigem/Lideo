package app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;

import app.model.Topic;
import app.model.User;
import app.repository.TopicRepository;
import app.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	TopicRepository topicRepository;

	@RequestMapping(method = RequestMethod.POST)
	public Map<String, Object> createUser(@RequestBody Map<String, Object> userMap) {
		Map<String, Object> response = new HashMap<String, Object>();
		String email = (String) userMap.get("email");
		User user = userRepository.findByEmail(email);
		if(user == null) {
			user = new User((String) userMap.get("name"), email);
		}
		response.put("userId", userRepository.save(user).getId());
		return response;
	}

	@RequestMapping(method = RequestMethod.GET)
	public Map<String, Object> getAllUsers() {
		Map<String, Object> response = new HashMap<String, Object>();
		System.out.println("In user contr");
		response.put("users", userRepository.findAll());
		return response;
	}

	@RequestMapping(value = "/{userId}/topics/{topicId}/videos", method = RequestMethod.POST, consumes = {
			"multipart/form-data" })
	public Map<String, Object> createVideos(@PathVariable("userId") String userId,
			@PathVariable("topicId") String topicId, @FormDataParam("video") InputStream inputStream,
			@RequestParam("filename") String filename) throws IOException {
		Map<String, Object> response = new HashMap<>();
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient("localhost", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB mongoDB = mongoClient.getDB("test");
		System.out.println("User id" + userId);
		System.out.println("Topic id" + topicId);
		
		GridFS fileStore = new GridFS(mongoDB, "videos");
		GridFSInputFile inputFile = fileStore.createFile(inputStream);
		inputFile.setFilename(filename);
		inputFile.save();

		User user = userRepository.findOne(userId);
		
		if (user != null) {
			List<String> topicsAttempted = user.getTopicsAttempted();
			if (topicsAttempted == null)
				topicsAttempted = new ArrayList<>();
			topicsAttempted.add(topicId);
			user.setTopicsAttempted(topicsAttempted);
			userRepository.save(user);
		}
		response.put("SUCCESS", "Successfully created video");
		return response;
	}

	@RequestMapping(value = "/{userId}/topics/{topicId}/videos", method = RequestMethod.GET, produces = {
			"application/octet-stream" })
	@ResponseBody
	public ResponseEntity<InputStreamResource> getVideos(@PathVariable("userId") String userId,
			@PathVariable("topicId") String topicId) throws JsonProcessingException {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient("localhost", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB mongoDB = mongoClient.getDB("test");

		GridFS gfsPhoto = new GridFS(mongoDB, "videos");

		List<GridFSDBFile> videoForOutput = gfsPhoto
				.find((DBObject) JSON.parse("{ userId : '" + userId + "', topicId : '" + topicId + "'}"));
		if (videoForOutput.size() == 0) {
			return null;
		}
		return ResponseEntity.ok().contentLength(videoForOutput.get(0).getLength())
				.contentType(MediaType.parseMediaType("video/mp4"))
				.body(new InputStreamResource(videoForOutput.get(0).getInputStream()));

	}

	@RequestMapping(value = "/{userId}/attempted_topics", method = RequestMethod.GET)
	public Map<String, Object> getTopicsAttemptedByUser(@PathVariable("userId") String userId) {
		System.out.println("Fetching topics for user " + userId);
		Map<String, Object> response = new HashMap<String, Object>();
		User user = userRepository.findOne(userId);
		List<String> topicsAttempted = user.getTopicsAttempted();
		System.out.println("List of topics attempted " + topicsAttempted);
		List<Topic> topicsList = new ArrayList<>();
		if(topicsAttempted != null) {
			topicsList = topicRepository.findByIdIn(topicsAttempted);
		}
		response.put("topics", topicsList);
		return response;
	}
}
