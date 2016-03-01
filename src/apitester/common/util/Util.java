package apitester.common.util;

public class Util
{
	public static boolean getTrueOrFalse(String value)
	{
		if(value != null)
		{
			if(Boolean.valueOf(value))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	public static int getIntFromString(String value)
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
}
