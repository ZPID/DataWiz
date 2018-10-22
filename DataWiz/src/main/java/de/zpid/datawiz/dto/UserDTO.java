package de.zpid.datawiz.dto;

import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.util.RegexUtil;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * User Data Transfer Object
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 */
public class UserDTO implements Serializable {

    private static final long serialVersionUID = -4743208731231501718L;

    private long id;
    @NotNull
    @Pattern(regexp = RegexUtil.validEmail)
    private String email;
    @Pattern(regexp = RegexUtil.emptyOr + RegexUtil.validEmail)
    private String secEmail;
    @NotNull
    private String password;
    @Pattern(regexp = RegexUtil.emptyOr + RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to50)
    @Size(max = 25)
    private String title;
    @Pattern(regexp = RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to250)
    private String firstName;
    @Pattern(regexp = RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to250)
    private String lastName;
    @Pattern(regexp = RegexUtil.emptyOr + RegexUtil.phonenumberGermanDIN)
    private String phone;
    @Pattern(regexp = RegexUtil.emptyOr + RegexUtil.phonenumberGermanDIN)
    private String fax;
    @Size(max = 500)
    private String comments;
    @Size(max = 250)
    private String institution;
    @Size(max = 250)
    private String department;
    @Size(max = 250)
    private String homepage;
    @Size(max = 250)
    private String street;
    @Pattern(regexp = RegexUtil.emptyOr + RegexUtil.onlyDigits + RegexUtil.size0to10)
    private String zip;
    @Size(max = 250)
    private String city;
    @Size(max = 250)
    private String state;
    @Size(max = 250)
    private String country;
    @Pattern(regexp = RegexUtil.regexORCID)
    private String orcid;
    private LocalDateTime regDate;
    private LocalDateTime lastLogin;
    private String account_state;

    private String activationCode;
    private List<UserRoleDTO> globalRoles = new ArrayList<>();
    // Values for RegisterCheck
    private String password_retyped;
    private boolean checkedGTC;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
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

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getPassword_old() {
        return password_old;
    }

    public void setPassword_old(String password_old) {
        this.password_old = password_old;
    }

    public boolean hasRole(Object rol) {
        return hasRole(rol, Optional.empty(), false);
    }

    public boolean hasRole(Object rol, Object id, boolean isStudy) {
        return hasRole(rol, (id == null) ? Optional.empty() : Optional.of(id), isStudy);
    }

    public boolean hasRole(final Object rol, final Optional<Object> id, final boolean isStudy) {
        Roles role = null;
        long pid = 0;
        if (id.isPresent()) {
            if (id.get() instanceof String) {
                String pidS = (String) id.get();
                if (pidS.isEmpty()) {
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
        long finalPid = pid;
        Roles finalrole = role;
        if (this.globalRoles != null && this.globalRoles.size() > 0) {
            return this.globalRoles.parallelStream().anyMatch(tmp -> {
                boolean ret = false;
                if (id.isPresent()) {
                    if (!isStudy) {
                        if (tmp.getProjectId() > 0 && tmp.getProjectId() == finalPid && tmp.getType().equals(finalrole.name()))
                            ret = true;
                    } else {
                        if (tmp.getStudyId() > 0 && tmp.getStudyId() == finalPid && tmp.getType().equals(finalrole.name()))
                            ret = true;
                    }
                } else {
                    if (tmp.getType().equals(finalrole.name()))
                        ret = true;
                }
                return ret;
            });
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return id == userDTO.id &&
                checkedGTC == userDTO.checkedGTC &&
                Objects.equals(email, userDTO.email) &&
                Objects.equals(secEmail, userDTO.secEmail) &&
                Objects.equals(password, userDTO.password) &&
                Objects.equals(title, userDTO.title) &&
                Objects.equals(firstName, userDTO.firstName) &&
                Objects.equals(lastName, userDTO.lastName) &&
                Objects.equals(phone, userDTO.phone) &&
                Objects.equals(fax, userDTO.fax) &&
                Objects.equals(comments, userDTO.comments) &&
                Objects.equals(institution, userDTO.institution) &&
                Objects.equals(department, userDTO.department) &&
                Objects.equals(homepage, userDTO.homepage) &&
                Objects.equals(street, userDTO.street) &&
                Objects.equals(zip, userDTO.zip) &&
                Objects.equals(city, userDTO.city) &&
                Objects.equals(state, userDTO.state) &&
                Objects.equals(country, userDTO.country) &&
                Objects.equals(orcid, userDTO.orcid) &&
                Objects.equals(regDate, userDTO.regDate) &&
                Objects.equals(lastLogin, userDTO.lastLogin) &&
                Objects.equals(account_state, userDTO.account_state) &&
                Objects.equals(activationCode, userDTO.activationCode) &&
                Objects.equals(globalRoles, userDTO.globalRoles) &&
                Objects.equals(password_retyped, userDTO.password_retyped) &&
                Objects.equals(password_old, userDTO.password_old);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, secEmail, password, title, firstName, lastName, phone, fax, comments, institution, department, homepage, street, zip, city, state, country, orcid, regDate, lastLogin, account_state, activationCode, globalRoles, password_retyped, checkedGTC, password_old);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", secEmail='" + secEmail + '\'' +
                ", title='" + title + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", fax='" + fax + '\'' +
                ", comments='" + comments + '\'' +
                ", institution='" + institution + '\'' +
                ", department='" + department + '\'' +
                ", homepage='" + homepage + '\'' +
                ", street='" + street + '\'' +
                ", zip='" + zip + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", orcid='" + orcid + '\'' +
                ", regDate=" + regDate +
                ", lastLogin=" + lastLogin +
                ", account_state='" + account_state + '\'' +
                ", activationCode='" + activationCode + '\'' +
                ", globalRoles=" + globalRoles +
                ", checkedGTC=" + checkedGTC +
                '}';
    }
}
