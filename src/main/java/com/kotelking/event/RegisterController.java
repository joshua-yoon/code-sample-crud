package com.kotelking.event;

import com.kotelking.event.exception.ApiException;
import com.kotelking.event.model.Application;
import com.kotelking.event.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("applications")
@RestController
public class RegisterController {

    private final RegisterService registerService;


    @Autowired
    public RegisterController(RegisterService registerService){
        this.registerService=registerService;
    }


    /**
     * Get applied list
     * @return
     */
    @RequestMapping(method= RequestMethod.GET)
    public List<Application> getList(){

        return registerService.getApplications();
    }

    /**
     * Register on list (Create)
     * @param users
     * @return
     */
    @RequestMapping(method= RequestMethod.POST)
    public Application register(List<User> users){

        return registerService.register(users);
    }

    /**
     * Gets an application for id
     * @param id
     * @return
     */
    @RequestMapping(method= RequestMethod.GET,value="{id}")
    public Application getApply(@PathVariable("id") int id){
        return registerService.getApplication(id);
    }

    /**
     * Update Application
     * @param application
     * @return
     */
    @RequestMapping(method= RequestMethod.PUT,value="{id}")
    public Application update(@PathVariable("id") int id, Application application){
        if (id != application.getId())
            throw new RuntimeException();
        return registerService.update(application);
    }

    /**
     * Delete Application
     * @param id
     * @return
     */
    @RequestMapping(method= RequestMethod.DELETE,value="{id}")
    public Application delete(@PathVariable("id") int id){
        return registerService.delete(id);
    }

    /**
     * ApiException Handler
     * @param e
     * @return
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> exception(ApiException e){
        return new ResponseEntity<>(e.getMessage(),e.getHttpStatus());
    }

}
