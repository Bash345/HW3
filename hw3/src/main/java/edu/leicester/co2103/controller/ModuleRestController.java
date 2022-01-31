package edu.leicester.co2103.controller;

import edu.leicester.co2103.domain.Module;
import edu.leicester.co2103.domain.Session;
import edu.leicester.co2103.repo.ModuleRepository;
import edu.leicester.co2103.repo.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/modules")
public class ModuleRestController {
    private final ModuleRepository moduleRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    public ModuleRestController(ModuleRepository moduleRepository, SessionRepository sessionRepository) {
        this.moduleRepository = moduleRepository;
        this.sessionRepository = sessionRepository;
    }

    //Get a list of all modules
    @GetMapping
    public ResponseEntity<Object> listAllModules(){
        return ResponseEntity.status(HttpStatus.OK).body(moduleRepository.findAll());
    }

    //create a module and return the saved module
    @PostMapping
    public ResponseEntity<Object> createModule(@RequestBody Module moduleRequest){
        try {
            Module module = new Module(moduleRequest.getCode(), moduleRequest.getTitle(), moduleRequest.getLevel(), moduleRequest.isOptional());
            return ResponseEntity.status(HttpStatus.OK).body(moduleRepository.save(module));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create module");
        }

    }

    //Get a module by id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getModule(@PathVariable("id") String id){
        Optional<Module> optionalModule = moduleRepository.findById(id);
        return optionalModule.<ResponseEntity<Object>>map(module -> ResponseEntity.status(HttpStatus.OK).body(module)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such module"));
    }

    //update module and return the updated module
    @PutMapping
    public ResponseEntity<Object> updateModule(@RequestBody Module module){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(moduleRepository.save(module));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update module");
        }
    }

    //delete a module by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteModule(@PathVariable("id") String id){
        try {
            Optional<Module> module = moduleRepository.findById(id);
            if (module.isPresent()){
                module.get().getSessions().clear();
                moduleRepository.delete(module.get());
                return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to deleted module. This module may not be present");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to deleted module. This module may not be present");
        }
    }

    //list all sessions in a module
    @GetMapping("/{code}/sessions")
    public ResponseEntity<Object> listModuleSessions(@PathVariable("code") String code){
        Optional<Module> optionalModule = moduleRepository.findById(code);
        return optionalModule.<ResponseEntity<Object>>map(module -> ResponseEntity.status(HttpStatus.OK).body(module.getSessions())).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such module"));
    }

    //create a session in a module
    @PostMapping("/{code}/sessions")
    public ResponseEntity<Object> createSession(@PathVariable("code") String code, @RequestBody Session session){
        Optional<Module> optionalModule = moduleRepository.findById(code);
        if (optionalModule.isPresent()){
            try {
                optionalModule.get().getSessions().add(session);
                moduleRepository.save(optionalModule.get());
                return ResponseEntity.status(HttpStatus.OK).body(moduleRepository.save(optionalModule.get()));
            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such module");
    }

    //update a session in a module and return this session
    @PutMapping("/{code}/sessions")
    public ResponseEntity<Object> updateModuleSessionWithPut(@PathVariable("code") String code, @RequestBody Session session){
        Optional<Module> optionalModule = moduleRepository.findById(code);
        if (optionalModule.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(sessionRepository.save(session));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such module");
    }

    @PatchMapping("/{code}/sessions")
    public ResponseEntity<Object> updateModuleSessionWithPatch(@PathVariable("code") String code, @RequestBody Session session){
        Optional<Module> optionalModule = moduleRepository.findById(code);
        if (optionalModule.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(sessionRepository.save(session));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such module");
    }

    //get a session in a module
    @GetMapping("/{code}/sessions/{id}")
    public ResponseEntity<Object> getModuleSession(@PathVariable("code") String code, @PathVariable("id") long id){
        Optional<Module> optionalModule = moduleRepository.findById(code);
        if (optionalModule.isPresent()){
            Session session= optionalModule.get().getSessions().stream().filter(session1 -> session1.getId() == id).findFirst().orElse(null);
            if (session ==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such session in this module");
            }
            return ResponseEntity.status(HttpStatus.OK).body(session);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such module");
    }

    //delete a session in a module
    @DeleteMapping("/{code}/sessions/{id}")
    public ResponseEntity<Object> deleteModuleSession(@PathVariable("code") String code, @PathVariable("id") long id){
        Optional<Module> optionalModule = moduleRepository.findById(code);
        Session session = optionalModule.flatMap(module -> module.getSessions().stream().filter(session1 -> session1.getId() == id).findFirst()).orElse(null);
        if (session != null){
            optionalModule.get().getSessions().remove(session);
            moduleRepository.save(optionalModule.get());
            return ResponseEntity.status(HttpStatus.OK).body("Session deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such session in the specified module");
    }
}
