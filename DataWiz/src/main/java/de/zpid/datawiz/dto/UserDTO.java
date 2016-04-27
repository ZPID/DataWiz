package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.util.RegexUtil;

/**
 * 
 * @author Ronny Boelter
 *
 */
public class UserDTO implements Serializable {

  private static final long serialVersionUID = -4743208731231501718L;

  private long id;
  @NotNull
  @Pattern(regexp = RegexUtil.validEmail)
  private String email;
  @Pattern(regexp = RegexUtil.validEmail)
  private String secEmail;
  @NotNull
  private String password;
  @Pattern(regexp = RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to50)
  @Size(min = 0, max = 25)
  private String title;
  @Pattern(regexp = RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to250)
  private String firstName;
  @Pattern(regexp = RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to250)
  private String lastName;
  @Pattern(regexp = RegexUtil.phonenumberGermanDIN)
  private String phone;
  @Pattern(regexp = RegexUtil.phonenumberGermanDIN)
  private String fax;
  @Size(min = 0, max = 500)
  private String comments;
  @Size(min = 0, max = 250)
  private String institution;
  @Size(min = 0, max = 250)
  private String department;
  @Size(min = 0, max = 250)
  private String homepage;
  @Size(min = 0, max = 250)
  private String street;
  // @Pattern(regexp = RegexUtil.onlyDigits + RegexUtil.size0to10)
  private int zip;
  @Size(min = 0, max = 250)
  private String city;
  @Size(min = 0, max = 250)
  private String state;
  @Size(min = 0, max = 250)
  private String country;
  // @Pattern(regexp = RegexUtil.regexORCID)
  private String orcid;

  private String account_state;

  private String activationCode;
  private List<UserRoleDTO> globalRoles = new ArrayList<UserRoleDTO>();
  // Values for RegisterCheck
  @NotNull
  private String password_retyped;
  private boolean checkedGTC;
  @NotNull
  private String password_old;

  public long getId() {
    return id;
  }

  public void setId(long id) {
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

  public String getSecEmail() {
    return secEmail;
  }

  public void setSecEmail(String secEmail) {
    this.secEmail = secEmail;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getInstitution() {
    return institution;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String getHomepage() {
    return homepage;
  }

  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public int getZip() {
    return zip;
  }

  public void setZip(int zip) {
    this.zip = zip;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getOrcid() {
    return orcid;
  }

  public void setOrcid(String orcid) {
    this.orcid = orcid;
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

  public boolean hasRole(Object rol) {
    return hasRole(rol, Optional.empty(), false);
  }

  public boolean hasRole(Object rol, Object id, boolean isStudy) {
    return hasRole(rol, (id == null) ? Optional.empty() : Optional.of(id), isStudy);
  }

  public String getPassword_old() {
    return password_old;
  }

  public void setPassword_old(String password_old) {
    this.password_old = password_old;
  }

  public boolean hasRole(final Object rol, final Optional<Object> id, final boolean isStudy) {
    Roles role = null;
    long pid = 0;
    if (id.isPresent()) {
      if (id.get() instanceof String) {
        String pidS = (String) id.get();
        if (pidS == null || pidS.isEmpty()) {
          return false;
        }
        try {
          pid = Long.parseLong(pidS);
        } catch (Exception e) {
          return false;
        }
      } else if (id.get() instanceof Number) {
        pid = ((Number) id.get()).longValue();
      } else {
        return false;
      }
    }
    if (rol instanceof Roles) {
      role = (Roles) rol;
    } else if (rol instanceof String) {
      role = Roles.valueOf((String) rol);
    } else {
      return false;
    }
    if (this.globalRoles != null && this.globalRoles.size() > 0) {
      for (UserRoleDTO tmp : this.globalRoles) {
        if (id.isPresent()) {
          if (!isStudy) {
            if (tmp.getProjectId() > 0 && tmp.getProjectId() == pid && tmp.getType().equals(role.name()))
              return true;
          } else {
            if (tmp.getStudyId() > 0 && tmp.getStudyId() == pid && tmp.getType().equals(role.name()))
              return true;
          }
        } else {
          if (tmp.getType().equals(role.name()))
            return true;
        }
      }
    }
    return false;
  }

  /**
   * 
   * @param role
   * @return
   */

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((account_state == null) ? 0 : account_state.hashCode());
    result = prime * result + ((activationCode == null) ? 0 : activationCode.hashCode());
    result = prime * result + (checkedGTC ? 1231 : 1237);
    result = prime * result + ((city == null) ? 0 : city.hashCode());
    result = prime * result + ((comments == null) ? 0 : comments.hashCode());
    result = prime * result + ((country == null) ? 0 : country.hashCode());
    result = prime * result + ((department == null) ? 0 : department.hashCode());
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((fax == null) ? 0 : fax.hashCode());
    result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
    result = prime * result + ((globalRoles == null) ? 0 : globalRoles.hashCode());
    result = prime * result + ((homepage == null) ? 0 : homepage.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((institution == null) ? 0 : institution.hashCode());
    result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
    result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + ((password_retyped == null) ? 0 : password_retyped.hashCode());
    result = prime * result + ((phone == null) ? 0 : phone.hashCode());
    result = prime * result + ((secEmail == null) ? 0 : secEmail.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((street == null) ? 0 : street.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    result = prime * result + zip;
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
    if (activationCode == null) {
      if (other.activationCode != null)
        return false;
    } else if (!activationCode.equals(other.activationCode))
      return false;
    if (checkedGTC != other.checkedGTC)
      return false;
    if (city == null) {
      if (other.city != null)
        return false;
    } else if (!city.equals(other.city))
      return false;
    if (comments == null) {
      if (other.comments != null)
        return false;
    } else if (!comments.equals(other.comments))
      return false;
    if (country == null) {
      if (other.country != null)
        return false;
    } else if (!country.equals(other.country))
      return false;
    if (department == null) {
      if (other.department != null)
        return false;
    } else if (!department.equals(other.department))
      return false;
    if (email == null) {
      if (other.email != null)
        return false;
    } else if (!email.equals(other.email))
      return false;
    if (fax == null) {
      if (other.fax != null)
        return false;
    } else if (!fax.equals(other.fax))
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
    if (homepage == null) {
      if (other.homepage != null)
        return false;
    } else if (!homepage.equals(other.homepage))
      return false;
    if (id != other.id)
      return false;
    if (institution == null) {
      if (other.institution != null)
        return false;
    } else if (!institution.equals(other.institution))
      return false;
    if (lastName == null) {
      if (other.lastName != null)
        return false;
    } else if (!lastName.equals(other.lastName))
      return false;
    if (orcid == null) {
      if (other.orcid != null)
        return false;
    } else if (!orcid.equals(other.orcid))
      return false;
    if (password == null) {
      if (other.password != null)
        return false;
    } else if (!password.equals(other.password))
      return false;
    if (password_retyped == null) {
      if (other.password_retyped != null)
        return false;
    } else if (!password_retyped.equals(other.password_retyped))
      return false;
    if (phone == null) {
      if (other.phone != null)
        return false;
    } else if (!phone.equals(other.phone))
      return false;
    if (secEmail == null) {
      if (other.secEmail != null)
        return false;
    } else if (!secEmail.equals(other.secEmail))
      return false;
    if (state == null) {
      if (other.state != null)
        return false;
    } else if (!state.equals(other.state))
      return false;
    if (street == null) {
      if (other.street != null)
        return false;
    } else if (!street.equals(other.street))
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    if (zip != other.zip)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "UserDTO [id=" + id + ", email=" + email + ", secEmail=" + secEmail + ", title=" + title + ", firstName="
        + firstName + ", lastName=" + lastName + ", phone=" + phone + ", fax=" + fax + ", comments=" + comments
        + ", institution=" + institution + ", department=" + department + ", homepage=" + homepage + ", street="
        + street + ", zip=" + zip + ", city=" + city + ", state=" + state + ", country=" + country + ", orcid=" + orcid
        + ", account_state=" + account_state + ", globalRoles=" + globalRoles + "]";
  }
}
