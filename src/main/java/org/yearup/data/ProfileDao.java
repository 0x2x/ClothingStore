package org.yearup.data;


import org.yearup.models.Profile;

public interface ProfileDao
{
    Profile create(Profile profile);
    Profile edit(Profile profile);
    Profile getUserByUsername(String username);
}
