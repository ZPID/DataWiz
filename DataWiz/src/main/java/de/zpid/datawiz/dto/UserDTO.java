package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import de.zpid.datawiz.util.RegexUtil;
import de.zpid.datawiz.util.Roles;

/**
 * 
 * @author Ronny Boelter
 *
 */
public class UserDTO implements Serializable {

  private static final long serialVersionUID = -4743208731231501718L;

  private int id;

  @NotNull
  @Pattern(regexp = RegexUtil.validEmail)
  private String email;
  private String account_state;
  private List<UserRoleDTO> globalRoles = new ArrayList<UserRoleDTO>();

  @NotNull
  private String password;

  @Pattern(regexp = RegexUtil.regexStringWithoutNumber + RegexUtil.size0to250)
  private String firstName;

  @Pattern(regexp = RegexUtil.regexStringWithoutNumber + RegexUtil.size0to250)
  private String lastName;

  private String activationCode;

  // Values for RegisterCheck
  @NotNull
  private String password_retyped;
  private boolean checkedGTC;

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

  public String getAccountState() {
    return account_state;
  }

  public void setAccountState(String state) {
    this.account_state = state;
  }

  public List<UserRoleDTO> getGlobalRoles() {
    return globalRoles;
  }

  public void setGlobalRoles(List<UserRoleDTO> globalRoles) {
    this.globalRoles = globalRoles;
  }

  public String getAccount_state() {
    return account_state;
  }

  public void setAccount_state(String account_state) {
    this.account_state = account_state;
  }

  public String getPassword_retyped() {
    return password_retyped;
  }

  public void setPassword_retyped(String password_retyped) {
    this.password_retyped = password_retyped;
  }

  public boolean isCheckedGTC() {
    return checkedGTC;
  }

  public void setCheckedGTC(boolean checkedGTC) {
    this.checkedGTC = checkedGTC;
  }

  public String getActivationCode() {
    return activationCode;
  }

  public void setActivationCode(String activationCode) {
    this.activationCode = activationCode;
  }

  /**
   * 
   * @param role
   * @return
   */
  public boolean hasRole(Roles role) {
    if (this.globalRoles != null && this.globalRoles.size() > 0) {
      for (UserRoleDTO tmp : this.globalRoles) {
        if (tmp.getType().equals(role.name()))
          return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((account_state == null) ? 0 : account_state.hashCode());
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
    result = prime * result + ((globalRoles == null) ? 0 : globalRoles.hashCode());
    result = prime * result + id;
    result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UserDTO other = (UserDTO) obj;
    if (account_state == null) {
      if (other.account_state != null)
        return false;
    } else if (!account_state.equals(other.account_state))
      return false;
    if (email == null) {
      if (other.email != null)
        return false;
    } else if (!email.equals(other.email))
      return false;
    if (firstName == null) {
      if (other.firstName != null)
        return false;
    } else if (!firstName.equals(other.firstName))
      return false;
    if (globalRoles == null) {
      if (other.globalRoles != null)
        return false;
    } else if (!globalRoles.equals(other.globalRoles))
      return false;
    if (id != other.id)
      return false;
    if (lastName == null) {
      if (other.lastName != null)
        return false;
    } else if (!lastName.equals(other.lastName))
      return false;
    if (password == null) {
      if (other.password != null)
        return false;
    } else if (!password.equals(other.password))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "UserDTO [id=" + id + ", email=" + email + ", account_state=" + account_state + ", globalRoles="
        + globalRoles + ", firstName=" + firstName + ", lastName=" + lastName + "]";
  }

}
