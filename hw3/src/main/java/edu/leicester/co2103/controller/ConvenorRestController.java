package edu.leicester.co2103.controller;

import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.Module;
import edu.leicester.co2103.repo.ConvenorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/convenors")
@RestController
public class ConvenorRestController {
    private final ConvenorRepository convenorRepository;

    @Autowired
    public ConvenorRestController(ConvenorRepository convenorRepository) {
        this.convenorRepository = convenorRepository;
    }

    //Get a list of all convenors
    @GetMapping
    public ResponseEntity<Object> listAllConvenors(){
        return ResponseEntity.status(HttpStatus.OK).body(convenorRepository.findAll());
    }

    //create a convenor and return the saved convenor
    @PostMapping
    public ResponseEntity<Object> createConvenor(@RequestBody Convenor convenorRequest){
        try {
            Convenor convenor = new Convenor(convenorRequest.getName(), convenorRequest.getPosition());
            return ResponseEntity.status(HttpStatus.OK).body(convenorRepository.save(convenor));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create convenor");
        }

    }

    //Get a convenor by id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getConvenor(@PathVariable("id") long id){
        Optional<Convenor> optionalConvenor = convenorRepository.findById(id);
        return optionalConvenor.<ResponseEntity<Object>>map(convenor -> ResponseEntity.status(HttpStatus.OK).body(convenor)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Such convenor"));
    }

    //update convenor and return the updated convenor
    @PutMapping
    public ResponseEntity<Object> updateConvenor(@RequestBody Convenor convenor){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(convenorRepository.save(convenor));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update convenor");
        }
    }

    //delete a convenor by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteConvenor(@PathVariable("id") long id){
        try {
            Optional<Convenor> optionalConvenor = convenorRepository.findById(id);
            if (optionalConvenor.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such convenor");

            optionalConvenor.get().getModules().clear();
            convenorRepository.delete(optionalConvenor.get());
            return ResponseEntity.status(HttpStatus.OK).body("Convenor deleted successfully");
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such convenor");

        }
    }

    //List all modules taught by a convenor
    @GetMapping("/{id}/modules")
    public ResponseEntity<Object> listConvenorModules(@PathVariable("id") long id){
        Optional<Convenor> optionalConvenor = convenorRepository.findById(id);
        return optionalConvenor.<ResponseEntity<Object>>map(convenor -> ResponseEntity.status(HttpStatus.OK).body(convenor.getModules())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such convenor"));
    }
}
