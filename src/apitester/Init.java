package apitester;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import apitester.common.Account;
import apitester.common.ProviderRefData;
import apitester.common.util.ConfigUtil;
import riconeapi.common.Authenticator;
import riconeapi.common.RicOneApiClient;
import riconeapi.models.authentication.Endpoint;
import riconeapi.models.sifxpress.XCalendarType;
import riconeapi.models.sifxpress.XContactType;
import riconeapi.models.sifxpress.XCourseType;
import riconeapi.models.sifxpress.XLeaType;
import riconeapi.models.sifxpress.XRosterType;
import riconeapi.models.sifxpress.XSchoolType;
import riconeapi.models.sifxpress.XStaffType;
import riconeapi.models.sifxpress.XStudentType;

public class Init
{
	public static boolean getLeaRefs = false;		public static boolean runLeaTest = false;		public static int leaRunAmount = 0;			public static int leasRefAmount = 0;
	public static boolean getSchoolRefs = false;	public static boolean runSchoolTest = false;	public static int schoolRunAmount = 0;		public static int schoolsRefAmount = 0;
	public static boolean getCalendarRefs = false;	public static boolean runCalendarTest = false;	public static int calendarRunAmount = 0;	public static int calendarsRefAmount = 0;
	public static boolean getCourseRefs = false;	public static boolean runCourseTest = false;	public static int courseRunAmount = 0;		public static int coursesRefAmount = 0;
	public static boolean getRosterRefs = false;	public static boolean runRosterTest = false;	public static int rosterRunAmount = 0;		public static int rostersRefAmount = 0;
	public static boolean getStaffRefs = false;		public static boolean runStaffTest = false;		public static int staffRunAmount = 0;		public static int staffsRefAmount = 0;
	public static boolean getStudentRefs = false;	public static boolean runStudentTest = false;	public static int studentRunAmount = 0;		public static int studentsRefAmount = 0;
	public static boolean getContactRefs = false;	public static boolean runContactTest = false;	public static int contactRunAmount = 0;		public static int contactsRefAmount = 0;

	public static void main(String[] args) throws InterruptedException, IOException
	{
		System.out.println("Configuring...");

		if(configure())
		{
			System.out.println("~!!!!! Starting Test !!!!!~");
			List<Account> accounts = loadAccounts();

			for(Account account : accounts)
			{
				System.out.println("+--- Account Being Tested: " + account.getUsername());

				Authenticator auth = new Authenticator("http://auth.test.ricone.org/login", account.getUsername(), account.getPassword());

				if(StringUtils.isNotBlank(ConfigUtil.getInstance().getProviderId()))
				{
					for(Endpoint endpoint : auth.GetEndpoints(ConfigUtil.getInstance().getProviderId()))
					{
						runTests(endpoint, account);
					}
				}
				else
				{
					for(Endpoint endpoint : auth.GetEndpoints())
					{
						runTests(endpoint, account);
					}
				}				
			}
			System.out.println("~!!!!! Test Over !!!!!~");
		}
	}

	private static boolean configure()
	{
		try
		{
			getLeaRefs = ConfigUtil.getInstance().leaGetRefs();
			leasRefAmount = ConfigUtil.getInstance().leaRefAmount();
			runLeaTest = ConfigUtil.getInstance().leaRunTest();
			leaRunAmount =  ConfigUtil.getInstance().leaRunAmount();
			
			System.out.println(ConfigUtil.getInstance().leaGetRefs());

			getSchoolRefs = ConfigUtil.getInstance().schoolGetRefs();
			schoolsRefAmount = ConfigUtil.getInstance().schoolRefAmount();
			runSchoolTest = ConfigUtil.getInstance().schoolRunTest();
			schoolRunAmount =  ConfigUtil.getInstance().schoolRunAmount();

			getCalendarRefs = ConfigUtil.getInstance().calendarGetRefs();
			calendarsRefAmount = ConfigUtil.getInstance().calendarRefAmount();
			runCalendarTest = ConfigUtil.getInstance().calendarRunTest();
			calendarRunAmount =  ConfigUtil.getInstance().calendarRunAmount();

			getCourseRefs = ConfigUtil.getInstance().courseGetRefs();
			coursesRefAmount = ConfigUtil.getInstance().courseRefAmount();
			runCourseTest = ConfigUtil.getInstance().courseRunTest();
			courseRunAmount =  ConfigUtil.getInstance().courseRunAmount();

			getRosterRefs = ConfigUtil.getInstance().rosterGetRefs();
			rostersRefAmount = ConfigUtil.getInstance().rosterRefAmount();
			runRosterTest = ConfigUtil.getInstance().rosterRunTest();
			rosterRunAmount =  ConfigUtil.getInstance().rosterRunAmount();

			getStaffRefs = ConfigUtil.getInstance().staffGetRefs();
			staffsRefAmount = ConfigUtil.getInstance().staffRefAmount();
			runStaffTest = ConfigUtil.getInstance().staffRunTest();
			staffRunAmount =  ConfigUtil.getInstance().staffRunAmount();

			getStudentRefs = ConfigUtil.getInstance().studentGetRefs();
			studentsRefAmount = ConfigUtil.getInstance().studentRefAmount();
			runStudentTest = ConfigUtil.getInstance().studentRunTest();
			studentRunAmount =  ConfigUtil.getInstance().studentRunAmount();

			getContactRefs = ConfigUtil.getInstance().contactGetRefs();
			contactsRefAmount = ConfigUtil.getInstance().contactRefAmount();
			runContactTest = ConfigUtil.getInstance().contactRunTest();
			contactRunAmount =  ConfigUtil.getInstance().contactRunAmount();

			if(runLeaTest != false || runSchoolTest != false || runCalendarTest != false || runCourseTest != false
			|| runRosterTest != false || runStaffTest != false || runStudentTest != false || runContactTest != false)
			{
				return true;
			}
			else
			{
				System.out.println("No 'x.runTests' are set to true in config file, try turning one of them on...");
				return false;
			}

		}
		catch(Exception e)
		{
			System.out.println("Error while configuring, try setting some values....");
			return false;
		}

	}

	public static ArrayList<Account> loadAccounts()
	{
		ArrayList<Account> accounts = new ArrayList<Account>();
		try
		{
			accounts.add(new Account(ConfigUtil.getInstance().getAccountUsername(), ConfigUtil.getInstance().getAccountPassword()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return accounts;
	}

	public static void loadRefIds(Account account, RicOneApiClient ricOne, Endpoint endpoint)
	{
	    System.out.println("\t\t +--- Loading necessary refIds...");

	    ProviderRefData data = new ProviderRefData(endpoint.getProviderId());

	    if(getLeaRefs)
	    {
	    	System.out.println("\t\t\t +--- Getting " + leasRefAmount + " xLea refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	for(XLeaType type : ricOne.sifXpress.GetXLeas(1, leasRefAmount))
			{
	    		refs.add(type.getRefId());
			}
	    	//refs.add("NotARefId");
	    	data.setLeaRefs(refs);
	    }

	    if(getSchoolRefs)
	    {
	    	System.out.println("\t\t\t +--- Getting " + schoolsRefAmount + " xSchool refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	for(XSchoolType type : ricOne.sifXpress.GetXSchools(1, schoolsRefAmount))
			{
	    		refs.add(type.getRefId());
			}
	    	data.setSchoolRefs(refs);
	    }

	    if(getCalendarRefs)
	    {
	    	System.out.println("\t\t\t +--- Getting " + calendarsRefAmount + " xCalendar refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	for(XCalendarType type : ricOne.sifXpress.GetXCalendars(1, calendarsRefAmount))
			{
	    		refs.add(type.getRefId());
			}
	    	data.setCalendarRefs(refs);
	    }

	    if(getCourseRefs)
	    {
	    	System.out.println("\t\t\t +--- Getting " + coursesRefAmount + " xCourse refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	for(XCourseType type : ricOne.sifXpress.GetXCourses(1, coursesRefAmount))
			{
	    		refs.add(type.getRefId());
			}
	    	data.setCourseRefs(refs);
	    }

	    if(getRosterRefs)
	    {
	    	System.out.println("\t\t\t +--- Getting " + rostersRefAmount + " xRoster refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	for(XRosterType type : ricOne.sifXpress.GetXRosters(1, rostersRefAmount))
			{
	    		refs.add(type.getRefId());
			}
	    	data.setRosterRefs(refs);
	    }

	    if(getStaffRefs)
	    {
	    	System.out.println("\t\t\t +--- Getting " + staffsRefAmount + " xStaff refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	for(XStaffType type : ricOne.sifXpress.GetXStaffs(1, staffsRefAmount))
			{
	    		refs.add(type.getRefId());
			}
	    	data.setStaffRefs(refs);
	    }

	    if(getStudentRefs)
	    {
	    	System.out.println("\t\t\t +--- Getting " + studentsRefAmount + " xStudent refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	for(XStudentType type : ricOne.sifXpress.GetXStudents(1, studentsRefAmount))
			{
	    		refs.add(type.getRefId());
			}
	    	data.setStudentRefs(refs);
	    }

	    if(getContactRefs)
	    {
	    	System.out.println("\t\t\t +--- Getting " + contactsRefAmount + " xContact refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	for(XContactType type : ricOne.sifXpress.GetXContacts(1, contactsRefAmount))
			{
	    		refs.add(type.getRefId());
			}
	    	data.setContactRefs(refs);
	    }
	    //Add Data to Account ProviderRefData
	    account.getProviderData().add(data);
	}

	private static void runTests(Endpoint endpoint, Account account)
	{
		System.out.println("\t +--- Testing Provider: " + endpoint.getProviderId());
	    RicOneApiClient ricOne = new RicOneApiClient(endpoint);

	    loadRefIds(account, ricOne, endpoint);

	    if(runLeaTest)
	    {
	    	runLeaTest(account, ricOne, endpoint.getProviderId(), leaRunAmount);
	    }

	    if(runSchoolTest)
	    {
	    	runSchoolTest(account, ricOne, endpoint.getProviderId(), schoolRunAmount);
	    }

	    if(runCalendarTest)
	    {
	    	runCalendarTest(account, ricOne, endpoint.getProviderId(), calendarRunAmount);
	    }

	    if(runCourseTest)
	    {
	    	runCourseTest(account, ricOne, endpoint.getProviderId(), courseRunAmount);
	    }

	    if(runRosterTest)
	    {
	    	runRosterTest(account, ricOne, endpoint.getProviderId(), rosterRunAmount);
	    }

	    if(runStaffTest)
	    {
	    	runStaffTest(account, ricOne, endpoint.getProviderId(), staffRunAmount);
	    }

	    if(runStudentTest)
	    {
	    	runStudentTest(account, ricOne, endpoint.getProviderId(), studentRunAmount);
	    }

	    if(runContactTest)
	    {
	    	runContactTest(account, ricOne, endpoint.getProviderId(), contactRunAmount);
	    }
		
	}
		
	public static void runLeaTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		System.out.println("\t\t +--- Running xLea Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			System.out.println("\t\t\t +--- Time " + i + " of " + runAmmount);

			System.out.println("\t\t\t\t +--- GetXLea()");
			try
			{
				ricOne.sifXpress.GetXLeas();
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXLea(1,5)");
			ricOne.sifXpress.GetXLeas(1, 5);

			System.out.println("\t\t\t\t +--- GetXLea(2,5)");
			try
			{
				ricOne.sifXpress.GetXLeas(2, 5);
			}
			catch(Exception e){}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getLeaRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXLea(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXLeasByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXLeasByXSchool(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getRosterRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXLeasByXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXLeasByXRoster(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getStaffRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXLeasByXStaff(refId)");
						for(String refId : data.getStaffRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXLeasByXStaff(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getStudentRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXLeasByXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXLeasByXStudent(refId);
							}
							catch(Exception e){}
						}
					}
				}
			}
		}
	}

	public static void runSchoolTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		System.out.println("\t\t +--- Running xSchool Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			System.out.println("\t\t\t +--- Time " + i + " of " + runAmmount);

			System.out.println("\t\t\t\t +--- GetXSchools()");
			try
			{
				ricOne.sifXpress.GetXSchools();
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXSchools(1,5)");
			try
			{
				ricOne.sifXpress.GetXSchools(1, 5);
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXSchools(2,5)");
			try
			{
				ricOne.sifXpress.GetXSchools(2, 5);
			}
			catch(Exception e){}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getSchoolRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXSchool(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXSchoolsByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXSchoolsByXLea(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getCalendarRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXSchoolsByXCalendar(refId)");
						for(String refId : data.getCalendarRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXSchoolsByXCalendar(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getCourseRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXSchoolsByXCourse(refId)");
						for(String refId : data.getCourseRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXSchoolsByXCourse(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getRosterRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXSchoolsByXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXSchoolsByXRoster(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getStaffRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXSchoolsByXStaff(refId)");
						for(String refId : data.getStaffRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXSchoolsByXStaff(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getStudentRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXSchoolsByXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							try
							{
								ricOne.sifXpress.GetXSchoolsByXStudent(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getContactRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXSchoolsByXContact(refId)");
						for(String refId : data.getContactRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXSchoolsByXContact(refId);
							}
							catch(Exception e){}
						}
					}
				}
			}
		}
	}

	public static void runCalendarTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		System.out.println("\t\t\t +--- Running xCalendar Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			System.out.println("\t\t\t +--- Time " + i + " of " + runAmmount);

			System.out.println("\t\t\t\t +--- GetXCalendars()");
			try
			{
				ricOne.sifXpress.GetXCalendars();
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXCalendars(1,5)");
			try
			{
				ricOne.sifXpress.GetXCalendars(1, 5);
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXCalendars(2,5)");
			try
			{
				ricOne.sifXpress.GetXCalendars(2, 5);
			}
			catch(Exception e){}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getCalendarRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXCalendar(refId)");
						for(String refId : data.getCalendarRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXCalendar(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXCalendarsByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXCalendarsByXLea(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXCalendarsByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXCalendarsByXSchool(refId);
							}
							catch(Exception e){}
						}
					}
				}
			}
		}
		System.out.println("Finished xCalendar Ref Requests");
	}

	public static void runCourseTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		System.out.println("\t\t\t +--- Running xCourse Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			System.out.println("\t\t\t +--- Time " + i + " of " + runAmmount);

			System.out.println("\t\t\t\t +--- GetXCourses()");
			try
			{
				ricOne.sifXpress.GetXCourses();
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXCourses(1,5)");
			try
			{
				ricOne.sifXpress.GetXCourses(1, 5);
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXCourses(2,5)");
			try
			{
				ricOne.sifXpress.GetXCourses(2, 5);
			}
			catch(Exception e){}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getCourseRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXCourse(refId)");
						for(String refId : data.getCourseRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXCourse(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXCoursesByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXCoursesByXLea(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXCoursesByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXCoursesByXSchool(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getRosterRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXCoursesByXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXCoursesByXRoster(refId);
							}
							catch(Exception e){}
						}
					}
				}
			}
		}
	}

	public static void runRosterTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		System.out.println("\t\t\t +--- Running xRoster Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			System.out.println("\t\t\t +--- Time " + i + " of " + runAmmount);

			System.out.println("\t\t\t\t +--- GetXRosters()");
			try
			{
				ricOne.sifXpress.GetXRosters();
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXRosters(1,5)");
			try
			{
				ricOne.sifXpress.GetXRosters(1, 5);
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXRosters(2,5)");
			try
			{
				ricOne.sifXpress.GetXRosters(2, 5);
			}
			catch(Exception e){}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getRosterRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXRoster(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXRostersByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXRostersByXLea(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXRostersByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXRostersByXSchool(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getCourseRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXRostersByXCourse(refId)");
						for(String refId : data.getCourseRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXRostersByXCourse(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getStaffRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXRostersByXStaff(refId)");
						for(String refId : data.getStaffRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXRostersByXStaff(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getStudentRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXRostersByXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXRostersByXStudent(refId);
							}
							catch(Exception e){}
						}
					}
				}
			}
		}
	}

	public static void runStaffTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		System.out.println("\t\t\t +--- Running xStaff Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			System.out.println("\t\t\t +--- Time " + i + " of " + runAmmount);

			System.out.println("\t\t\t\t +--- GetXStaffs()");
			try
			{
				ricOne.sifXpress.GetXStaffs();
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXStaffs(1,5)");
			try
			{
				ricOne.sifXpress.GetXStaffs(1, 5);
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXStaffs(2,5)");
			try
			{
				ricOne.sifXpress.GetXStaffs(2, 5);
			}
			catch(Exception e){}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getStaffRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStaff(refId)");
						for(String refId : data.getStaffRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStaff(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStaffsByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStaffsByXLea(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStaffsByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStaffsByXSchool(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getCourseRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStaffsByXCourse(refId)");
						for(String refId : data.getCourseRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStaffsByXCourse(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getRosterRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStaffsByXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStaffsByXRoster(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getStudentRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStaffsByXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStaffsByXStudent(refId);
							}
							catch(Exception e){}
						}
					}
				}
			}
		}
	}

	public static void runStudentTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		System.out.println("\t\t\t +--- Running xStudent Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			System.out.println("\t\t\t +--- Time " + i + " of " + runAmmount);

			System.out.println("\t\t\t\t +--- GetXStudents()");
			try
			{
				ricOne.sifXpress.GetXStudents();
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXStudents(1,5)");
			try
			{
				ricOne.sifXpress.GetXStudents(1, 5);
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXStudents(2,5)");
			try
			{
				ricOne.sifXpress.GetXStudents(2, 5);
			}
			catch(Exception e){}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getStudentRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStudent(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStudentsByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStudentsByXLea(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStudentsByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStudentsByXSchool(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getRosterRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStudentsByXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStudentsByXRoster(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getStaffRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStudentsByXStaff(refId)");
						for(String refId : data.getStaffRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStudentsByXStaff(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getContactRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXStudentsByXContact(refId)");
						for(String refId : data.getContactRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXStudentsByXContact(refId);
							}
							catch(Exception e){}
						}
					}
				}
			}
		}
	}

	public static void runContactTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		System.out.println("\t\t\t +--- Running xContact Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			System.out.println("\t\t\t +--- Time " + i + " of " + runAmmount);

			System.out.println("\t\t\t\t +--- GetXContacts()");
			try
			{
				ricOne.sifXpress.GetXContacts();
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXContacts(1,5)");
			try
			{
				ricOne.sifXpress.GetXContacts(1, 5);
			}
			catch(Exception e){}

			System.out.println("\t\t\t\t +--- GetXContacts(2,5)");
			try
			{
				ricOne.sifXpress.GetXContacts(2, 5);
			}
			catch(Exception e){}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getContactRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXContact(refId)");
						for(String refId : data.getContactRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXContact(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXContactsByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXContactsByXLea(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXContactsByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXContactsByXSchool(refId);
							}
							catch(Exception e){}
						}
					}

					if(!data.getStudentRefs().isEmpty())
					{
						System.out.println("\t\t\t\t +--- GetXContactsByXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							System.out.println("\t\t\t\t\t +--- " + refId);
							try
							{
								ricOne.sifXpress.GetXContactsByXStudent(refId);
							}
							catch(Exception e){}
						}
					}
				}
			}
		}
	}
}