package edu.leicester.co2103.repo;

import org.springframework.data.repository.CrudRepository;

import edu.leicester.co2103.domain.Module;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends CrudRepository<Module, String> {

}
