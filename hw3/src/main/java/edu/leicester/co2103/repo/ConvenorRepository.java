package edu.leicester.co2103.repo;

import org.springframework.data.repository.CrudRepository;

import edu.leicester.co2103.domain.Convenor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConvenorRepository extends CrudRepository<Convenor, Long> {
    List<Convenor> findAll();
}
