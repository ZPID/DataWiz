package de.zpid.datawiz.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

@Entity
@Table(name = "DATAWIZ_USERS")
public class DataWizUser implements Serializable {

  private static final long serialVersionUID = 5499229439519722215L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotNull
  @Column(name = "PASSWORD", nullable = false)
  private String password;
  
  @NotNull
  @Size(min = 3, max = 30)
  @Column(name = "FIRST_NAME", nullable = false)
  private String firstName;

  @NotNull
  @Size(min = 3, max = 30)
  @Column(name = "LAST_NAME", nullable = false)
  private String lastName;

  @Email
  @NotNull
  @Column(name = "EMAIL", unique = true, nullable = false)
  private String email;

  @Column(name = "STATE", nullable = false)
  private String state;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "APP_USER_USER_PROFILE", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
      @JoinColumn(name = "USER_PROFILE_ID") })
  private Set<DataWizUserRoles> userProfiles = new HashSet<DataWizUserRoles>();

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

  public Set<DataWizUserRoles> getUserProfiles() {
    return userProfiles;
  }

  public void setUserProfiles(Set<DataWizUserRoles> userProfiles) {
    this.userProfiles = userProfiles;
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
    if (!(obj instanceof DataWizUser))
      return false;
    DataWizUser other = (DataWizUser) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "DataWizUser [id=" + id + ", password=" + password + ", firstName=" + firstName + ", lastName=" + lastName
        + ", email=" + email + ", state=" + state + ", userProfiles=" + userProfiles + "]";
  }

}
