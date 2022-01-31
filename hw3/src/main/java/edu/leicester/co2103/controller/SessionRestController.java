package edu.leicester.co2103.controller;

import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.Module;
import edu.leicester.co2103.domain.Session;
import edu.leicester.co2103.repo.ConvenorRepository;
import edu.leicester.co2103.repo.ModuleRepository;
import edu.leicester.co2103.repo.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sessions")
@CrossOrigin(origins = "https://editor.swagger.io/")
public class SessionRestController {
    private final SessionRepository sessionRepository;
    private final ModuleRepository moduleRepository;
    private final ConvenorRepository convenorRepository;

    @Autowired
    public SessionRestController(SessionRepository sessionRepository, ModuleRepository moduleRepository, ConvenorRepository convenorRepository) {
        this.sessionRepository = sessionRepository;
        this.moduleRepository = moduleRepository;
        this.convenorRepository = convenorRepository;
    }

    //delete all sessions
    @DeleteMapping
    public ResponseEntity<Object> deleteAllSessions() {
        sessionRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.OK).body("All sessions deleted successfully");
    }


    //filter sessions by convener ID and Module code
    @GetMapping
    public ResponseEntity<Object> filterSessionsByConvenorAndModule(@RequestParam("convenor") Optional<Long> convenorId, @RequestParam("module") Optional<String> moduleCode) {
        //if both the convenor id and module code is set
        if (convenorId.isPresent() && moduleCode.isPresent()){
            Optional<Convenor> optionalConvenor = convenorRepository.findById(convenorId.get());
            //check if there is such a convenor
            if (optionalConvenor.isPresent()) {
                Convenor convenor = optionalConvenor.get();
                //find a module in in this convenor whose code is same as the moduleCode in the parameter
                Module module = convenor.getModules().stream().filter(module1 -> module1.getCode().equals(moduleCode.get())).findFirst().orElse(null);
                //check if the module was found
                if (module != null)
                    return ResponseEntity.status(HttpStatus.OK).body( module.getSessions());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body( "No such module in the specified convenor");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( "No such convenor");
        }

        //if only the module code is set
        if (convenorId.isEmpty() && moduleCode.isPresent()){
            Optional<Module> module = moduleRepository.findById(moduleCode.get());
            return module.<ResponseEntity<Object>>map(value -> ResponseEntity.status(HttpStatus.OK).body(value.getSessions())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such module"));
        }

        //if only the convenor id is set
        if (convenorId.isPresent()){
            Optional<Convenor> optionalConvenor = convenorRepository.findById(convenorId.get());
            //check if there is such a convenor
            if (optionalConvenor.isPresent()) {
                //get this convenor
                Convenor convenor = optionalConvenor.get();
                List<Session> convenorSessions = new ArrayList<>();
                //loop through all modules in the convenor and add their sessions in the convenorSessions list
                convenor.getModules().forEach(module -> convenorSessions.addAll(module.getSessions()));
                return ResponseEntity.status(HttpStatus.OK).body( convenorSessions);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( "No such convenor");
        }

        //if both convenor id and module code are not set
        return ResponseEntity.status(HttpStatus.OK).body(sessionRepository.findAll());


    }
}
