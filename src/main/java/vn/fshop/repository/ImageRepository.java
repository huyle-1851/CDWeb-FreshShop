package vn.fshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fshop.model.Image;
import java.util.*;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
	List<Image> findByProduct_Id(UUID id);
}
