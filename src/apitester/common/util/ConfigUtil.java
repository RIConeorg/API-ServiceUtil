package apitester.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import apitester.Init;

/**
 * @author      Dan Whitehouse <daniel.whitehouse@neric.org>
 * @version     0.8.3
 * @since       2015-09-03
 */

public class ConfigUtil
{
	private static ConfigUtil instance = null;
	private static Properties properties = new Properties();

	public ConfigUtil()
	{
		try
		{
			File jarPath = new File(getJarPath());
			File propertiesPath = jarPath.getParentFile();	
			//System.out.println(propertiesPath);
			//System.out.println("/" + propertiesPath + "/config.properties");
			FileInputStream in = new FileInputStream("/" + propertiesPath + "/config/config.properties");
			
			properties.load(in);
			in.close();
		}
		catch (Exception e)
		{
			System.out.println("No dice... couldn't find file.");
		}
	}
	
	public static String getJarPath()
	{
		String path = Init.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		return path;
	}

	public static ConfigUtil getInstance()
	{
		if(instance == null)
		{
			synchronized(ConfigUtil.class)
			{
				if(instance==null)
				{
					instance = new ConfigUtil();
				}
			}
		}
		return instance;
	}

	public String getProviderId() throws IOException
	{
		return properties.getProperty("provider");
	}
	
	public String getAccountUsername() throws IOException
	{
		return properties.getProperty("account.username");
	}
	
	public String getAccountPassword() throws IOException
	{
		return properties.getProperty("account.password");
	}

	//Lea
	public boolean leaGetRefs() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("lea.getRefs"));
	}
	public int leaRefAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("lea.refAmount"));
	}
	public boolean leaRunTest() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("lea.runTest"));
	}
	public int leaRunAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("lea.runAmount"));
	}

	//School
	public boolean schoolGetRefs() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("school.getRefs"));
	}
	public int schoolRefAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("school.refAmount"));
	}
	public boolean schoolRunTest() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("school.runTest"));
	}
	public int schoolRunAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("school.runAmount"));
	}

	//Calendar
	public boolean calendarGetRefs() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("calendar.getRefs"));
	}
	public int calendarRefAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("calendar.refAmount"));
	}
	public boolean calendarRunTest() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("calendar.runTest"));
	}
	public int calendarRunAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("calendar.runAmount"));
	}

	//Course
	public boolean courseGetRefs() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("course.getRefs"));
	}
	public int courseRefAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("course.refAmount"));
	}
	public boolean courseRunTest() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("course.runTest"));
	}
	public int courseRunAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("course.runAmount"));
	}

	//Roster
	public boolean rosterGetRefs() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("roster.getRefs"));
	}
	public int rosterRefAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("roster.refAmount"));
	}
	public boolean rosterRunTest() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("roster.runTest"));
	}
	public int rosterRunAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("roster.runAmount"));
	}

	//Staff
	public boolean staffGetRefs() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("staff.getRefs"));
	}
	public int staffRefAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("staff.refAmount"));
	}
	public boolean staffRunTest() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("staff.runTest"));
	}
	public int staffRunAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("staff.runAmount"));
	}

	//Student
	public boolean studentGetRefs() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("student.getRefs"));
	}
	public int studentRefAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("student.refAmount"));
	}
	public boolean studentRunTest() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("student.runTest"));
	}
	public int studentRunAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("student.runAmount"));
	}

	//Contact
	public boolean contactGetRefs() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("contact.getRefs"));
	}
	public int contactRefAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("contact.refAmount"));
	}
	public boolean contactRunTest() throws IOException
	{
		return Util.getTrueOrFalse(properties.getProperty("contact.runTest"));
	}
	public int contactRunAmount() throws IOException
	{
		return Util.getIntFromString(properties.getProperty("contact.runAmount"));
	}
}
