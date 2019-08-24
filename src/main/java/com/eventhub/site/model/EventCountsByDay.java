package com.eventhub.site.model;

public class EventCountsByDay {

	private String dayName;
	private Long count = new Long(0);
	
	public String getDayName() {
		return dayName;
	}
	
	public void setDayName(String dayName) {
		this.dayName = dayName;
	}
	
	public Long getCount() {
		return count;
	}
	
	public void setCount(Long count) {
		this.count = count;
	}
	
	@Override
    public boolean equals(Object o) {
		EventCountsByDay passedInObj = (EventCountsByDay) o;
		if (this.dayName.equals(passedInObj.getDayName())) {
			return true;
		}
		return false;
	}
}
