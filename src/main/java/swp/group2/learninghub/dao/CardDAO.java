package swp.group2.learninghub.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.group2.learninghub.model.Card;

public interface CardDAO extends JpaRepository<Card,Integer> {
}