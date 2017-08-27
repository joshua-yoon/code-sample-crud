package com.kotelking.event;

import com.kotelking.event.exception.LimitReachedException;
import com.kotelking.event.model.Apply;
import com.kotelking.event.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RegisterController {

    private final RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService){
        this.registerService=registerService;
    }


    @RequestMapping(method= RequestMethod.GET,value="applies")
    public List<Apply> getList(){

        return registerService.getApplies();
    }

    /**
     * Register on list (Create Item)
     * @param users
     * @return
     */
    @RequestMapping(method= RequestMethod.POST,value="applies")
    public String register(List<User> users){

        registerService.register(users);
        return "신청되었습니다";
    }

    @RequestMapping(method= RequestMethod.GET,value="applies/{id}")
    public Apply getApply(@PathVariable("id") int id){
        return registerService.getApply(id);
    }

    /**
     * Update
     * @param apply
     * @return
     */
    @RequestMapping(method= RequestMethod.PUT,value="applies/{id}")
    public Apply update(@PathVariable("id") int id, Apply apply){
        if (id != apply.getId())
            throw new RuntimeException();
        return registerService.update(apply);
    }

    /**
     * Delete
     * @param id
     * @return
     */
    @RequestMapping(method= RequestMethod.DELETE,value="applies/{id}")
    public Apply delete(@PathVariable("id") int id){
        return registerService.remove(id);
    }

    @ExceptionHandler(LimitReachedException.class)
    public String limitReached(LimitReachedException e){
        return e.getMessage();
    }

}
