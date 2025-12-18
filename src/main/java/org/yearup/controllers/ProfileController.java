package org.yearup.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

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
    public Profile getProfile(Principal principal, @RequestParam(required = false) String username) {
        if(username != null && !username.isBlank()) {
            return profileDao.getUserByUsername(username);
        }
        return getProfileByPrincipal(principal);
    }

    // edit Profile
    @PutMapping()
    public Profile editProfile(Principal principal, @RequestBody Profile profile) {
        Profile profile1 = getProfileByPrincipal(principal);
        profile.setUserId(profile1.getUserId());
        return profileDao.edit(profile);
    }

    private Profile getProfileByPrincipal(Principal principal) {
        String username = principal.getName();
        return profileDao.getUserByUsername(username);
    }
}
