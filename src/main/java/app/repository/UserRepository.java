package app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import app.model.User;

public interface UserRepository extends MongoRepository<User, String>{

	public User findByEmail(String email);
}
