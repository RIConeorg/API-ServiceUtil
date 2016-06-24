package apitester;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.cert.X509Certificate;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apitester.common.Account;
import apitester.common.ProviderRefData;
import apitester.common.util.ConfigUtil;
import riconeapi.common.Authenticator;
import riconeapi.common.ResponseMulti;
import riconeapi.common.ResponseSingle;
import riconeapi.common.RicOneApiClient;
import riconeapi.models.authentication.Endpoint;
import riconeapi.models.xpress.XCalendarType;
import riconeapi.models.xpress.XContactType;
import riconeapi.models.xpress.XCourseType;
import riconeapi.models.xpress.XLeaType;
import riconeapi.models.xpress.XRosterType;
import riconeapi.models.xpress.XSchoolType;
import riconeapi.models.xpress.XStaffType;
import riconeapi.models.xpress.XStudentType;

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

	//Average Response Time
	private static List<Long> avgLeaRT = new ArrayList<Long>();
	private static List<Long> avgSchoolRT = new ArrayList<Long>();
	private static List<Long> avgCalendarRT = new ArrayList<Long>();
	private static List<Long> avgCourseRT = new ArrayList<Long>();
	private static List<Long> avgRosterRT = new ArrayList<Long>();
	private static List<Long> avgStaffRT = new ArrayList<Long>();
	private static List<Long> avgStudentRT = new ArrayList<Long>();
	private static List<Long> avgContactRT = new ArrayList<Long>();
	
	private static Logger logger = LogManager.getLogger();
	
	
	public static void main(String[] args) throws InterruptedException, IOException
	{
		logger.info("Configuring...");
		bypassHttps();
		if(configure())
		{
			logger.info("~!!!!! Starting Test !!!!!~");
			List<Account> accounts = loadAccounts();

			for(Account account : accounts)
			{
				logger.info("+--- Account Being Tested: " + account.getUsername());

				Authenticator auth = new Authenticator("https://auth.test.ricone.org/login", account.getUsername(), account.getPassword());

				if(StringUtils.isNotBlank(ConfigUtil.getInstance().getProviderId()))
				{
					for(Endpoint endpoint : auth.getEndpoints(ConfigUtil.getInstance().getProviderId()))
					{
						endpoint.setHref("http://localhost:8080/api/requests/");
						runTests(endpoint, account);
						
						RicOneApiClient ricOne = new RicOneApiClient(endpoint);
						//runCustomeTest(account, ricOne, endpoint.getProviderId(), 0);
					}
				}
				else
				{
					for(Endpoint endpoint : auth.getEndpoints())
					{
						runTests(endpoint, account);
					}
				}				
			}
			logger.info("~!!!!! Test Over !!!!!~");
			
			printAllStats();
			
		}
	}

	private static void bypassHttps()
	{
		//FIX NOT FOR PRODUCTION!!!!! ONLY TESTING ON INVALID SERVER CERT
		try
		{
			TrustManager[] trustAllCerts = new TrustManager[] 
			{
				new X509TrustManager() 
				{
					public java.security.cert.X509Certificate[] getAcceptedIssuers() 
					{
						return null;
					}

					public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

					public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
				}
			};

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() 
			{
				public boolean verify(String hostname, SSLSession session) 
				{
					return true;
				}
			};
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		}
		catch(Exception e){}		
	}

	private static boolean configure()
	{
		try
		{
			getLeaRefs = ConfigUtil.getInstance().leaGetRefs();
			leasRefAmount = ConfigUtil.getInstance().leaRefAmount();
			runLeaTest = ConfigUtil.getInstance().leaRunTest();
			leaRunAmount =  ConfigUtil.getInstance().leaRunAmount();
			
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
				logger.error("No 'x.runTests' are set to true in config file, try turning one of them on...");
				return false;
			}

		}
		catch(Exception e)
		{
			logger.error("Error while configuring, try setting some values....");
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
	    logger.info("\t\t +--- Loading necessary refIds...");

	    ProviderRefData data = new ProviderRefData(endpoint.getProviderId());

	    if(getLeaRefs)
	    {
	    	logger.info("\t\t\t +--- Getting " + leasRefAmount + " xLea refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	try
	    	{
	    		for(XLeaType type : ricOne.xPress.getXLeas(1, leasRefAmount).getData())
				{
		    		refs.add(type.getRefId());
				}
	    	}
	    	catch(Exception e){}
	    	//refs.add("NotARefId");
	    	data.setLeaRefs(refs);
	    }

	    if(getSchoolRefs)
	    {
	    	logger.info("\t\t\t +--- Getting " + schoolsRefAmount + " xSchool refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	try
	    	{
	    		for(XSchoolType type : ricOne.xPress.getXSchools(1, schoolsRefAmount).getData())
	    		{
	    			refs.add(type.getRefId());
	    		}
	    	}
	    	catch(Exception e){}
	    	data.setSchoolRefs(refs);
	    }

	    if(getCalendarRefs)
	    {
	    	logger.info("\t\t\t +--- Getting " + calendarsRefAmount + " xCalendar refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	try
	    	{
	    		for(XCalendarType type : ricOne.xPress.getXCalendars(1, calendarsRefAmount).getData())
	    		{
	    			refs.add(type.getRefId());
	    		}
	    	}
	    	catch(Exception e){}
	    	data.setCalendarRefs(refs);
	    }

	    if(getCourseRefs)
	    {
	    	logger.info("\t\t\t +--- Getting " + coursesRefAmount + " xCourse refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	try
	    	{
	    		for(XCourseType type : ricOne.xPress.getXCourses(1, coursesRefAmount).getData())
	    		{
	    			refs.add(type.getRefId());
	    		}
	    	}
	    	catch(Exception e){}
	    	data.setCourseRefs(refs);
	    }

	    if(getRosterRefs)
	    {
	    	logger.info("\t\t\t +--- Getting " + rostersRefAmount + " xRoster refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	try
	    	{
	    		for(XRosterType type : ricOne.xPress.getXRosters(1, rostersRefAmount).getData())
	    		{
	    			refs.add(type.getRefId());
	    		}
	    	}	    	
	    	catch(Exception e){}
	    	data.setRosterRefs(refs);
	    }

	    if(getStaffRefs)
	    {
	    	logger.info("\t\t\t +--- Getting " + staffsRefAmount + " xStaff refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	try
	    	{
		    	for(XStaffType type : ricOne.xPress.getXStaffs(1, staffsRefAmount).getData())
				{
		    		refs.add(type.getRefId());
				}	
	    	}
	    	catch(Exception e){}
	    	data.setStaffRefs(refs);
	    }

	    if(getStudentRefs)
	    {
	    	logger.info("\t\t\t +--- Getting " + studentsRefAmount + " xStudent refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	try
	    	{
		    	for(XStudentType type : ricOne.xPress.getXStudents(1, studentsRefAmount).getData())
				{
		    		refs.add(type.getRefId());
				}	
	    	}
	    	catch(Exception e){}
	    	data.setStudentRefs(refs);
	    }

	    if(getContactRefs)
	    {
	    	logger.info("\t\t\t +--- Getting " + contactsRefAmount + " xContact refIds...");
	    	List<String> refs = new ArrayList<String>();
	    	try
	    	{
		    	for(XContactType type : ricOne.xPress.getXContacts(1, contactsRefAmount).getData())
				{
		    		refs.add(type.getRefId());
				}	
	    	}
	    	catch(Exception e){}
	    	data.setContactRefs(refs);
	    }
	    //Add Data to Account ProviderRefData
	    account.getProviderData().add(data);
	}

	private static void runTests(Endpoint endpoint, Account account)
	{
		logger.info("\t +--- Testing Provider: " + endpoint.getProviderId());
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
		logger.info("\t\t +--- Running xLea Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			logger.info("\t\t\t +--- Time " + i + " of " + runAmmount);
	
			try
			{
				Date d = new Date();
				ResponseMulti<XLeaType> response = ricOne.xPress.getXLeas();
				addAvgTime(d, avgLeaRT);
				logger.info("\t\t\t\t +--- getXLea()"  + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXLea()"  + " | " + 500);}

			
			try
			{
				Date d = new Date();
				ResponseMulti<XLeaType> response = ricOne.xPress.getXLeas(1, 5);
				addAvgTime(d, avgLeaRT);
				logger.info("\t\t\t\t +--- getXLea(1,5)"  + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXLea(1,5)"  + " | " + 500);}

			
			try
			{
				Date d = new Date();
				ResponseMulti<XLeaType> response = ricOne.xPress.getXLeas(2, 5);
				addAvgTime(d, avgLeaRT);
				logger.info("\t\t\t\t +--- getXLea(2,5)"  + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXLea(2,5)"  + " | " + 500);}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getLeaRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXLea(refId)");
						for(String refId : data.getLeaRefs())
						{						
							try
							{
								Date d = new Date();
								ResponseSingle<XLeaType> response = ricOne.xPress.getXLea(refId);
								addAvgTime(d, avgLeaRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXLeasByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							
							try
							{
								Date d = new Date();
								ResponseMulti<XLeaType> response = ricOne.xPress.getXLeasByXSchool(refId);								
								addAvgTime(d, avgLeaRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getRosterRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXLeasByXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XLeaType> response = ricOne.xPress.getXLeasByXRoster(refId);
								addAvgTime(d, avgLeaRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getStaffRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXLeasByXStaff(refId)");
						for(String refId : data.getStaffRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XLeaType> response = ricOne.xPress.getXLeasByXStaff(refId);
								addAvgTime(d, avgLeaRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getStudentRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXLeasByXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XLeaType> response = ricOne.xPress.getXLeasByXStudent(refId);
								addAvgTime(d, avgLeaRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}
				}
			}
		}
	}

	public static void runSchoolTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		logger.info("\t\t +--- Running xSchool Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			logger.info("\t\t\t +--- Time " + i + " of " + runAmmount );

			try
			{
				Date d = new Date();
				ResponseMulti<XSchoolType> response = ricOne.xPress.getXSchools();
				addAvgTime(d, avgSchoolRT);
				logger.info("\t\t\t\t +--- getXSchools()"  + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXSchools()"  + " | " + 500);}

			try
			{
				Date d = new Date();
				ResponseMulti<XSchoolType> response = ricOne.xPress.getXSchools(1, 5);
				addAvgTime(d, avgSchoolRT);
				logger.info("\t\t\t\t +--- getXSchools(1,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXSchools(1,5)" + " | " + 500);}

			try
			{
				Date d = new Date();
				ResponseMulti<XSchoolType> response = ricOne.xPress.getXSchools(2, 5);
				addAvgTime(d, avgSchoolRT);
				logger.info("\t\t\t\t +--- getXSchools(2,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXSchools(2,5)" + " | " + 500);}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getSchoolRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							try
							{
								Date d = new Date();
								ResponseSingle<XSchoolType> response = ricOne.xPress.getXSchool(refId);
								addAvgTime(d, avgSchoolRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXSchoolsByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XSchoolType> response = ricOne.xPress.getXSchoolsByXLea(refId);
								addAvgTime(d, avgSchoolRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getCalendarRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXSchoolsByXCalendar(refId)");
						for(String refId : data.getCalendarRefs())
						{		
							try
							{
								Date d = new Date();
								ResponseMulti<XSchoolType> response = ricOne.xPress.getXSchoolsByXCalendar(refId);
								addAvgTime(d, avgSchoolRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getCourseRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXSchoolsByXCourse(refId)");
						for(String refId : data.getCourseRefs())
						{						
							try
							{
								Date d = new Date();
								ResponseMulti<XSchoolType> response = ricOne.xPress.getXSchoolsByXCourse(refId);
								addAvgTime(d, avgSchoolRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getRosterRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXSchoolsByXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XSchoolType> response = ricOne.xPress.getXSchoolsByXRoster(refId);
								addAvgTime(d, avgSchoolRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getStaffRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXSchoolsByXStaff(refId)");
						for(String refId : data.getStaffRefs())
						{		
							try
							{
								Date d = new Date();
								ResponseMulti<XSchoolType> response = ricOne.xPress.getXSchoolsByXStaff(refId);
								addAvgTime(d, avgSchoolRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getStudentRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXSchoolsByXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XSchoolType> response = ricOne.xPress.getXSchoolsByXStudent(refId);
								addAvgTime(d, avgSchoolRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getContactRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXSchoolsByXContact(refId)");
						for(String refId : data.getContactRefs())
						{				
							try
							{
								Date d = new Date();
								ResponseMulti<XSchoolType> response = ricOne.xPress.getXSchoolsByXContact(refId);
								addAvgTime(d, avgSchoolRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}
				}
			}
		}
	}

	public static void runCalendarTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		logger.info("\t\t\t +--- Running xCalendar Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			logger.info("\t\t\t +--- Time " + i + " of " + runAmmount);

			try
			{
				Date d = new Date();
				ResponseMulti<XCalendarType> response = ricOne.xPress.getXCalendars();
				addAvgTime(d, avgCalendarRT);
				logger.info("\t\t\t\t +--- getXCalendars()" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXCalendars()" + " | " + 500);}

			try
			{
				Date d = new Date();
				ResponseMulti<XCalendarType> response = ricOne.xPress.getXCalendars(1, 5);
				addAvgTime(d, avgCalendarRT);
				logger.info("\t\t\t\t +--- getXCalendars(1,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXCalendars(1,5)" + " | " + 500);}

			try
			{
				Date d = new Date();
				ResponseMulti<XCalendarType> response = ricOne.xPress.getXCalendars(2, 5);
				addAvgTime(d, avgCalendarRT);
				logger.info("\t\t\t\t +--- getXCalendars(2,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXCalendars(2,5)" + " | " + 500);}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getCalendarRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXCalendar(refId)");
						for(String refId : data.getCalendarRefs())
						{
							try
							{
								Date d = new Date();
								ResponseSingle<XCalendarType> response = ricOne.xPress.getXCalendar(refId);
								addAvgTime(d, avgCalendarRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXCalendarsByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XCalendarType> response = ricOne.xPress.getXCalendarsByXLea(refId);
								addAvgTime(d, avgCalendarRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXCalendarsByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XCalendarType> response = ricOne.xPress.getXCalendarsByXSchool(refId);
								addAvgTime(d, avgCalendarRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}
				}
			}
		}
	}

	public static void runCourseTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		logger.info("\t\t\t +--- Running xCourse Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			logger.info("\t\t\t +--- Time " + i + " of " + runAmmount);

			try
			{
				Date d = new Date();
				ResponseMulti<XCourseType> response = ricOne.xPress.getXCourses();
				addAvgTime(d, avgCourseRT);
				logger.info("\t\t\t\t +--- getXCourses()" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXCourses()" + " | " + 500);}

			
			try
			{
				Date d = new Date();
				ResponseMulti<XCourseType> response = ricOne.xPress.getXCourses(1, 5);
				addAvgTime(d, avgCourseRT);
				logger.info("\t\t\t\t +--- getXCourses(1,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXCourses(1,5)" + " | " + 500);}

			try
			{
				Date d = new Date();
				ResponseMulti<XCourseType> response = ricOne.xPress.getXCourses(2, 5);
				addAvgTime(d, avgCourseRT);
				logger.info("\t\t\t\t +--- getXCourses(2,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXCourses(2,5)" + " | " + 500);}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getCourseRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXCourse(refId)");
						for(String refId : data.getCourseRefs())
						{
							try
							{
								Date d = new Date();
								ResponseSingle<XCourseType> response = ricOne.xPress.getXCourse(refId);
								addAvgTime(d, avgCourseRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXCoursesByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XCourseType> response = ricOne.xPress.getXCoursesByXLea(refId);
								addAvgTime(d, avgCourseRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXCoursesByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XCourseType> response = ricOne.xPress.getXCoursesByXSchool(refId);
								addAvgTime(d, avgCourseRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getRosterRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXCoursesByXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XCourseType> response = ricOne.xPress.getXCoursesByXRoster(refId);
								addAvgTime(d, avgCourseRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}
				}
			}
		}
	}

	public static void runRosterTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		logger.info("\t\t\t +--- Running xRoster Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			logger.info("\t\t\t +--- Time " + i + " of " + runAmmount);
			
			try
			{
				Date d = new Date();
				ResponseMulti<XRosterType> response = ricOne.xPress.getXRosters();
				addAvgTime(d, avgRosterRT);
				logger.info("\t\t\t\t +--- getXRosters()" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXRosters()" + " | " + 500);}

			
			try
			{
				Date d = new Date();
				ResponseMulti<XRosterType> response = ricOne.xPress.getXRosters(1, 5);
				addAvgTime(d, avgRosterRT);
				logger.info("\t\t\t\t +--- getXRosters(1,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXRosters(1,5)" + " | " + 500);}

			
			try
			{
				Date d = new Date();
				ResponseMulti<XRosterType> response = ricOne.xPress.getXRosters(2, 5);
				addAvgTime(d, avgRosterRT);
				logger.info("\t\t\t\t +--- getXRosters(2,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXRosters(2,5)" + " | " + 500);}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getRosterRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							try
							{
								Date d = new Date();
								ResponseSingle<XRosterType> response = ricOne.xPress.getXRoster(refId);
								addAvgTime(d, avgRosterRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXRostersByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XRosterType> response = ricOne.xPress.getXRostersByXLea(refId);
								addAvgTime(d, avgRosterRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXRostersByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XRosterType> response = ricOne.xPress.getXRostersByXSchool(refId);
								addAvgTime(d, avgRosterRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getCourseRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXRostersByXCourse(refId)");
						for(String refId : data.getCourseRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XRosterType> response = ricOne.xPress.getXRostersByXCourse(refId);
								addAvgTime(d, avgRosterRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getStaffRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXRostersByXStaff(refId)");
						for(String refId : data.getStaffRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XRosterType> response = ricOne.xPress.getXRostersByXStaff(refId);
								addAvgTime(d, avgRosterRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getStudentRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXRostersByXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XRosterType> response = ricOne.xPress.getXRostersByXStudent(refId);
								addAvgTime(d, avgRosterRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}
				}
			}
		}
	}

	public static void runStaffTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		logger.info("\t\t\t +--- Running xStaff Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			logger.info("\t\t\t +--- Time " + i + " of " + runAmmount);

			try
			{
				Date d = new Date();
				ResponseMulti<XStaffType> response = ricOne.xPress.getXStaffs();
				addAvgTime(d, avgStaffRT);
				logger.info("\t\t\t\t +--- getXStaffs()" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXStaffs()" + " | " + 500);}
		
			try
			{
				Date d = new Date();
				ResponseMulti<XStaffType> response = ricOne.xPress.getXStaffs(1, 5);
				addAvgTime(d, avgStaffRT);
				logger.info("\t\t\t\t +--- getXStaffs(1,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXStaffs(1,5)" + " | " + 500);}
			
			try
			{
				Date d = new Date();
				ResponseMulti<XStaffType> response = ricOne.xPress.getXStaffs(2, 5);
				addAvgTime(d, avgStaffRT);
				logger.info("\t\t\t\t +--- getXStaffs(2,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXStaffs(2,5)" + " | " + 500);}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getStaffRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStaff(refId)");
						for(String refId : data.getStaffRefs())
						{
							try
							{
								Date d = new Date();
								ResponseSingle<XStaffType> response = ricOne.xPress.getXStaff(refId);
								addAvgTime(d, avgStaffRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStaffsByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XStaffType> response = ricOne.xPress.getXStaffsByXLea(refId);
								addAvgTime(d, avgStaffRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStaffsByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XStaffType> response = ricOne.xPress.getXStaffsByXSchool(refId);
								addAvgTime(d, avgStaffRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getCourseRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStaffsByXCourse(refId)");
						for(String refId : data.getCourseRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XStaffType> response = ricOne.xPress.getXStaffsByXCourse(refId);
								addAvgTime(d, avgStaffRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getRosterRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStaffsByXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XStaffType> response = ricOne.xPress.getXStaffsByXRoster(refId);
								addAvgTime(d, avgStaffRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getStudentRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStaffsByXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XStaffType> response = ricOne.xPress.getXStaffsByXStudent(refId);
								addAvgTime(d, avgStaffRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}
				}
			}
		}
	}

	public static void runStudentTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		logger.info("\t\t\t +--- Running xStudent Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			logger.info("\t\t\t +--- Time " + i + " of " + runAmmount);

			try
			{
				Date d = new Date();
				ResponseMulti<XStudentType> response = ricOne.xPress.getXStudents();
				addAvgTime(d, avgStudentRT);
				logger.info("\t\t\t\t +--- getXStudents()" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXStudents()" + " | " + 500);}

			try
			{
				Date d = new Date();
				ResponseMulti<XStudentType> response = ricOne.xPress.getXStudents(1, 5);
				addAvgTime(d, avgStudentRT);
				logger.info("\t\t\t\t +--- getXStudents(1,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXStudents(1,5)" + " | " + 500);}

			try
			{
				Date d = new Date();
				ResponseMulti<XStudentType> response = ricOne.xPress.getXStudents(2, 5);
				addAvgTime(d, avgStudentRT);
				logger.info("\t\t\t\t +--- getXStudents(2,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getStudentRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							try
							{
								Date d = new Date();
								ResponseSingle<XStudentType> response = ricOne.xPress.getXStudent(refId);
								addAvgTime(d, avgStudentRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStudentsByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XStudentType> response = ricOne.xPress.getXStudentsByXLea(refId);
								addAvgTime(d, avgStudentRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStudentsByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XStudentType> response = ricOne.xPress.getXStudentsByXSchool(refId);
								addAvgTime(d, avgStudentRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getRosterRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStudentsByXRoster(refId)");
						for(String refId : data.getRosterRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XStudentType> response = ricOne.xPress.getXStudentsByXRoster(refId);
								addAvgTime(d, avgStudentRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getStaffRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStudentsByXStaff(refId)");
						for(String refId : data.getStaffRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XStudentType> response = ricOne.xPress.getXStudentsByXStaff(refId);
								addAvgTime(d, avgStudentRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getContactRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXStudentsByXContact(refId)");
						for(String refId : data.getContactRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XStudentType> response = ricOne.xPress.getXStudentsByXContact(refId);
								addAvgTime(d, avgStudentRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}
				}
			}
		}
	}

	public static void runContactTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		logger.info("\t\t\t +--- Running xContact Ref Requests...");

		for(int i = 1; i <= runAmmount; i++)
		{
			logger.info("\t\t\t +--- Time " + i + " of " + runAmmount);

			try
			{
				Date d = new Date();
				ResponseMulti<XContactType> response = ricOne.xPress.getXContacts();
				addAvgTime(d, avgContactRT);
				logger.info("\t\t\t\t +--- getXContacts()" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXContacts()" + " | " + 500);}
		
			try
			{
				Date d = new Date();
				ResponseMulti<XContactType> response = ricOne.xPress.getXContacts(1, 5);
				addAvgTime(d, avgContactRT);
				logger.info("\t\t\t\t +--- getXContacts(1,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXContacts(1,5)" + " | " + 500);}

			try
			{
				Date d = new Date();
				ResponseMulti<XContactType> response = ricOne.xPress.getXContacts(2, 5);
				addAvgTime(d, avgContactRT);
				logger.info("\t\t\t\t +--- getXContacts(2,5)" + " | " + response.getStatusCode());
			}
			catch(Exception e){logger.error("\t\t\t\t +--- getXContacts(2,5)" + " | " + 500);}

			for(ProviderRefData data : account.getProviderData())
			{
				if(data.getProviderId().equalsIgnoreCase(providerId))
				{
					if(!data.getContactRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXContact(refId)");
						for(String refId : data.getContactRefs())
						{
							try
							{
								Date d = new Date();
								ResponseSingle<XContactType> response = ricOne.xPress.getXContact(refId);
								addAvgTime(d, avgContactRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getLeaRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXContactsByXLea(refId)");
						for(String refId : data.getLeaRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XContactType> response = ricOne.xPress.getXContactsByXLea(refId);
								addAvgTime(d, avgContactRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getSchoolRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXContactsByXSchool(refId)");
						for(String refId : data.getSchoolRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XContactType> response = ricOne.xPress.getXContactsByXSchool(refId);
								addAvgTime(d, avgContactRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}

					if(!data.getStudentRefs().isEmpty())
					{
						logger.info("\t\t\t\t +--- getXContactsByXStudent(refId)");
						for(String refId : data.getStudentRefs())
						{
							try
							{
								Date d = new Date();
								ResponseMulti<XContactType> response = ricOne.xPress.getXContactsByXStudent(refId);
								addAvgTime(d, avgContactRT);
								logger.info("\t\t\t\t\t +--- " + refId + " | " + response.getStatusCode());
							}
							catch(Exception e){logger.error("\t\t\t\t\t +--- " + refId + " | " + 500);}
						}
					}
				}
			}
		}
	}
	
	public static void runCustomeTest(Account account, RicOneApiClient ricOne, String providerId, int runAmmount)
	{
		XStudentType student = ricOne.xPress.getXStudent("0000DE7C-467E-4072-B327-8C82E4A2EC39").getData();
		
		logger.info(student.getName().getGivenName());
	}
	
	private static void addAvgTime(Date before, List<Long> out)
	{	
		long timeTaken = System.currentTimeMillis() - before.getTime();
		out.add(timeTaken);
	}
	

	private static void printStats(List<Long> list, String objectName)
	{
		if(list != null && list.size() > 0)
		{
			long avg = 0;
			for(Long l : list)
			{
				avg += l;
			}
			avg = avg / list.size();
			Collections.sort(list);
			logger.info(objectName + "\t  Avg RT: " + avg + "ms" + " | Lowest RT: " + list.get(0) + "ms | Highest RT: " + list.get(list.size()-1) + "ms");
		}
	}
	
	private static void printAllStats()
	{
		HashMap<String, List<Long>> hm = new HashMap<String, List<Long>>();		
		hm.put("Lea", avgLeaRT);
		hm.put("School", avgSchoolRT);
		hm.put("Calendar", avgCalendarRT);
		hm.put("Course", avgCourseRT);
		hm.put("Roster", avgRosterRT);
		hm.put("Staff", avgStaffRT);
		hm.put("Student", avgStudentRT);
		hm.put("Contact", avgContactRT);	

		for(String key : hm.keySet())
		{
			printStats(hm.get(key), key);	
		}
	}
}