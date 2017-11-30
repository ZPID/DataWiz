package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.List;

public class DataTableDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1486863136510024230L;
	private int draw;
	private int recordsTotal;
	private int recordsFiltered;
	private List<List<Object>> data;
	private String error;

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(int recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public List<List<Object>> getData() {
		return data;
	}

	public void setData(List<List<Object>> data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "DataTableDTO [draw=" + draw + ", recordsTotal=" + recordsTotal + ", recordsFiltered=" + recordsFiltered + ", data=" + data + ", error="
		    + error + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + draw;
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + recordsFiltered;
		result = prime * result + recordsTotal;
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
		DataTableDTO other = (DataTableDTO) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (draw != other.draw)
			return false;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		if (recordsFiltered != other.recordsFiltered)
			return false;
		if (recordsTotal != other.recordsTotal)
			return false;
		return true;
	}

}
