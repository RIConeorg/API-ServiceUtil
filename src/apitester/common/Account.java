package apitester.common;

import java.util.ArrayList;
import java.util.List;

public class Account
{
	private String username = null;
	private String password = null;
	private List<ProviderRefData> providerData = new ArrayList<ProviderRefData>();

	public Account()
	{
		super();
	}
	
	public Account(String username, String password)
	{
		super();
		this.username = username;
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}

	public List<ProviderRefData> getProviderData()
	{
		return providerData;
	}

	public void setProviderData(List<ProviderRefData> providerData)
	{
		this.providerData = providerData;
	}
}