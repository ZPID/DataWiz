package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.List;

public class SideMenuDTO implements Serializable {

	private static final long serialVersionUID = -4412745685049356069L;
	private long id;
	private String title;
	private List<SideMenuDTO> sublist;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<SideMenuDTO> getSublist() {
		return sublist;
	}

	public void setSublist(List<SideMenuDTO> sublist) {
		this.sublist = sublist;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((sublist == null) ? 0 : sublist.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		SideMenuDTO other = (SideMenuDTO) obj;
		if (id != other.id)
			return false;
		if (sublist == null) {
			if (other.sublist != null)
				return false;
		} else if (!sublist.equals(other.sublist))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SideMenuDTO [id=" + id + ", title=" + title + ", sublist=" + sublist + "]";
	}

}
