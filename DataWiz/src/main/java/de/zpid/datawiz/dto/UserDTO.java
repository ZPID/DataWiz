package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

public class UserDTO implements Serializable {

  private static final long serialVersionUID = -4743208731231501718L;

  private int id;
  @NotNull
  private String password;
  @NotNull
  @Size(min = 3, max = 30)
  private String firstName;
  @NotNull
  @Size(min = 3, max = 30)
  private String lastName;
  @Email
  @NotNull
  private String email;
  private String state;
  private List<UserRoleDTO> globalRoles = new ArrayList<UserRoleDTO>();

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public List<UserRoleDTO> getGlobalRoles() {
    return globalRoles;
  }

  public void setGlobalRoles(List<UserRoleDTO> globalRoles) {
    this.globalRoles = globalRoles;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof UserDTO))
      return false;
    UserDTO other = (UserDTO) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "UserDTO [id=" + id + ", password=" + password + ", firstName=" + firstName + ", lastName=" + lastName
        + ", email=" + email + ", state=" + state + ", globalRoles=" + globalRoles + "]";
  }

}
