package app.controller;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.server.PathParam;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;

import app.model.User;
import app.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@RequestMapping(method = RequestMethod.POST)
	public Map<String, Object> createUser(@RequestBody Map<String, Object> userMap) {
		Map<String, Object> response = new HashMap<String, Object>();
		User user = new User((String) userMap.get("name"));
		response.put("user", userRepository.save(user));
		return response;
	}

	@RequestMapping(method = RequestMethod.GET)
	public Map<String, Object> getAllUsers() {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("users", userRepository.findAll());
		return response;
	}

	@RequestMapping(value = "/{userId}/topics/{topicId}/videos", method = RequestMethod.POST, consumes = {
			"multipart/form-data" })
	public Map<String, Object> createVideos(@PathParam("userId") String userId, @PathParam("topicId") String topicId,
			@FormDataParam("video") InputStream inputStream, @RequestParam("filename") String filename) {
		Map<String, Object> response = new HashMap<>();
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient("localhost", 27017);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB mongoDB = mongoClient.getDB("test");
		System.out.println("User id" + userId);
		System.out.println("Topic id" + topicId);
		// Now let's store the binary file data using filestore GridFS
		GridFS fileStore = new GridFS(mongoDB, "videos");
		GridFSInputFile inputFile = fileStore.createFile(inputStream, filename);
		inputFile.put("userId", userId);
		inputFile.put("topicId", topicId);
		inputFile.save();
		response.put("SUCCESS", "Successfully created video");
		return response;
	}
	
	
	@RequestMapping(value = "/{userId}/topics/{topicId}/videos", method = RequestMethod.GET)
	public Map<String, Object> getVideos(@PathParam("userId") String userId, @PathParam("topicId") String topicId) throws JsonProcessingException {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient("localhost", 27017);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB mongoDB = mongoClient.getDB("test");

		GridFS gfsPhoto = new GridFS(mongoDB, "videos");

		List<GridFSDBFile> videoForOutput = gfsPhoto.find((DBObject) JSON.parse("{ userId :  null}"));
		System.out.println(videoForOutput);
		Map<String, Object> response = new HashMap<>();
		response.put("videos", videoForOutput);
		return response;
	}
	
}
