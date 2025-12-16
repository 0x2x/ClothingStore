package org.yearup.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.User;

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {

    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    // get Profile
    @GetMapping() // if for a specfic user use ?username= or ?id=
    public User getProfile(@RequestHeader("authorization") String token, @RequestParam(required = false) String username) {
        if(!username.isEmpty()) {
//            return profileDao.getByUserName(username);
        }
        return new User();
//        return profileDao.getUserById(Integer.parseInt(id));
    }


    // edit Profile
    @PutMapping()
    public User editProfile(@RequestHeader("authorization") String token) {
        return new User();
    }

    //
}
