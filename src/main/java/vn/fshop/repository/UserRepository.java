package vn.fshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.fshop.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUsernameAndPassword(String username, String password);

	User findByUsername(String username);
}
