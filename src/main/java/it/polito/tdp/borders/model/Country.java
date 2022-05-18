package it.polito.tdp.borders.model;

public class Country implements Comparable<Country> {

	private int cCode; // Country Code for the state
	private String stateAbb; // State Abbreviation (3 capital letters)
	private String stateName; // Full State name

	public Country(int cCode, String stateAbb, String stateName) {
		this.cCode = cCode;
		this.stateAbb = stateAbb;
		this.stateName = stateName;
	}

	public int getcCode() {
		return cCode;
	}

	public void setcCode(int cCode) {
		this.cCode = cCode;
	}

	public String getStateAbb() {
		return stateAbb;
	}

	public void setStateAbb(String stateAbb) {
		this.stateAbb = stateAbb;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cCode;
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
		Country other = (Country) obj;
		if (cCode != other.cCode)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s", stateName);
	}

	@Override
	public int compareTo(Country o) {
		return this.getStateName().compareTo(o.getStateName());
	}

}
