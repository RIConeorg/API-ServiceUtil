package apitester.common;

import java.util.ArrayList;
import java.util.List;

public class ProviderRefData
{
	private String providerId = null;
	private List<String> leaRefs = new ArrayList<String>();
	private List<String> schoolRefs = new ArrayList<String>();
	private List<String> calendarRefs = new ArrayList<String>();
	private List<String> courseRefs = new ArrayList<String>();
	private List<String> rosterRefs = new ArrayList<String>();
	private List<String> staffRefs = new ArrayList<String>();
	private List<String> studentRefs = new ArrayList<String>();
	private List<String> contactRefs = new ArrayList<String>();
	
	public ProviderRefData()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public ProviderRefData(String providerId)
	{
		super();
		this.providerId = providerId;
	}

	public String getProviderId()
	{
		return providerId;
	}

	public void setProviderId(String providerId)
	{
		this.providerId = providerId;
	}

	public List<String> getLeaRefs()
	{
		return leaRefs;
	}
	
	public void setLeaRefs(List<String> leaRefs)
	{
		this.leaRefs = leaRefs;
	}
	
	public List<String> getSchoolRefs()
	{
		return schoolRefs;
	}
	
	public void setSchoolRefs(List<String> schoolRefs)
	{
		this.schoolRefs = schoolRefs;
	}
	
	public List<String> getCalendarRefs()
	{
		return calendarRefs;
	}
	
	public void setCalendarRefs(List<String> calendarRefs)
	{
		this.calendarRefs = calendarRefs;
	}
	
	public List<String> getCourseRefs()
	{
		return courseRefs;
	}
	
	public void setCourseRefs(List<String> courseRefs)
	{
		this.courseRefs = courseRefs;
	}
	
	public List<String> getRosterRefs()
	{
		return rosterRefs;
	}
	public void setRosterRefs(List<String> rosterRefs)
	{
		this.rosterRefs = rosterRefs;
	}
	
	public List<String> getStaffRefs()
	{
		return staffRefs;
	}
	
	public void setStaffRefs(List<String> staffRefs)
	{
		this.staffRefs = staffRefs;
	}
	
	public List<String> getStudentRefs()
	{
		return studentRefs;
	}
	
	public void setStudentRefs(List<String> studentRefs)
	{
		this.studentRefs = studentRefs;
	}
	
	public List<String> getContactRefs()
	{
		return contactRefs;
	}
	
	public void setContactRefs(List<String> contactRefs)
	{
		this.contactRefs = contactRefs;
	}	
}