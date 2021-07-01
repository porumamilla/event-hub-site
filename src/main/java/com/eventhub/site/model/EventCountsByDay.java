package com.eventhub.site.model;
import java.io.Serializable;
public class EventCountsByDay implements Serializable {
    private static final long serialVersionUID = 1L;
	private String dayName;
	private Long count = 0L;
	
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
