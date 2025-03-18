//Create a interface User
package com.openarena.openarenaportalapi.model;


import java.util.Collection;
import java.util.Set;
public interface User {
    Integer getId();
    String getUsername();
    String getPassword();
    Set<Role> getRoles();
    String getEmail(); // Add this
    String getName();
}