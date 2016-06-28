package de.zpid.datawiz.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.AccountState;
import de.zpid.datawiz.enumeration.Roles;

public class CustomUserDetails implements UserDetails, CredentialsContainer {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	private static Logger log = LogManager.getLogger(CustomUserDetails.class);

	private UserDTO user;
	private String password;
	private final String username;
	private final Set<GrantedAuthority> authorities;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	public CustomUserDetails(UserDTO user) {
		if (user == null || user.getEmail() == null || user.getEmail().isEmpty() || user.getPassword() == null
				|| user.getPassword().isEmpty()) {
			throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
		}
		try {
			switch (AccountState.valueOf(user.getAccountState().toUpperCase().trim())) {
			case ACTIVE:
				if (log.isDebugEnabled())
					log.debug("User State is ACTIVE for User: " + user.getEmail());
				this.accountNonExpired = true;
				this.accountNonLocked = true;
				this.credentialsNonExpired = true;
				this.enabled = true;
				break;
			case EXPIRED:
				if (log.isDebugEnabled())
					log.debug("User State is EXPIRED for User: " + user.getEmail());
				this.accountNonExpired = false;
				this.accountNonLocked = true;
				this.credentialsNonExpired = false;
				this.enabled = true;
				break;
			case LOCKED:
				if (log.isDebugEnabled())
					log.debug("User State is LOCKED for User: " + user.getEmail());
				this.accountNonExpired = true;
				this.accountNonLocked = false;
				this.credentialsNonExpired = true;
				this.enabled = true;
				break;
			default:
				log.warn("User State is UNKNOWN for User: " + user.getEmail());
				this.accountNonExpired = false;
				this.accountNonLocked = false;
				this.credentialsNonExpired = false;
				this.enabled = false;
				break;
			}
		} catch (Exception e) {
			log.warn("Please check AccountState enum and users state column - they have different entities for user: "
					+ user.getEmail());
			throw new InternalAuthenticationServiceException("State from DB doesn't match AccountState ENUM values: "
					+ user.getAccountState().toUpperCase().trim());
		}
		this.user = user;
		this.username = user.getEmail();
		this.password = user.getPassword().trim();
		user.setPassword("");
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if (user.getGlobalRoles() != null)
			for (UserRoleDTO userProfile : user.getGlobalRoles()) {
				if (userProfile.getType().equals(Roles.ADMIN.name()) || userProfile.getType().equals(Roles.USER.name()))
					authorities.add(new SimpleGrantedAuthority("ROLE_" + userProfile.getType()));
			}
		this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
	}

	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void eraseCredentials() {
		password = null;
	}

	private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
		Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
		// Ensure array iteration order is predictable (as per
		// UserDetails.getAuthorities() contract and SEC-717)
		SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<GrantedAuthority>(new AuthorityComparator());
		for (GrantedAuthority grantedAuthority : authorities) {
			Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
			sortedAuthorities.add(grantedAuthority);
		}

		return sortedAuthorities;
	}

	private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
		private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

		public int compare(GrantedAuthority g1, GrantedAuthority g2) {
			// Neither should ever be null as each entry is checked before
			// adding it to
			// the set.
			// If the authority is null, it is a custom authority and should
			// precede
			// others.
			if (g2.getAuthority() == null) {
				return -1;
			}
			if (g1.getAuthority() == null) {
				return 1;
			}
			return g1.getAuthority().compareTo(g2.getAuthority());
		}
	}

	/**
	 * Returns {@code true} if the supplied object is a {@code User} instance
	 * with the same {@code username} value.
	 * <p>
	 * In other words, the objects are equal if they have the same username,
	 * representing the same principal.
	 */
	@Override
	public boolean equals(Object rhs) {
		if (rhs instanceof User) {
			return username.equals(((CustomUserDetails) rhs).username);
		}
		return false;
	}

	/**
	 * Returns the hashcode of the {@code username}.
	 */
	@Override
	public int hashCode() {
		return username.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");
		sb.append("Username: ").append(this.username).append("; ");
		sb.append("Password: [PROTECTED]; ");
		sb.append("Enabled: ").append(this.enabled).append("; ");
		sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
		sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");
		sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");
		if (!authorities.isEmpty()) {
			sb.append("Granted Authorities: ");
			boolean first = true;
			for (GrantedAuthority auth : authorities) {
				if (!first) {
					sb.append(",");
				}
				first = false;
				sb.append(auth);
			}
		} else {
			sb.append("Not granted any authorities");
		}
		return sb.toString();
	}

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}
}
