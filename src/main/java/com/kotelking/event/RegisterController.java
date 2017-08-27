package com.kotelking.event;

import com.kotelking.event.model.Apply;
import com.kotelking.event.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class RegisterController {

    private final RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService){
        this.registerService=registerService;
    }

    @RequestMapping(method= RequestMethod.POST,value="applies")
    public String register(List<User> users){

        registerService.register(users);
        return "신청되었습니다";
    }

    @RequestMapping(method= RequestMethod.GET,value="applies")
    public List<Apply> getList(){

        return registerService.getApplies();
    }

    @ExceptionHandler(LimitReachedException.class)
    public String limitReached(LimitReachedException e){
        return e.getMessage();
    }

}
