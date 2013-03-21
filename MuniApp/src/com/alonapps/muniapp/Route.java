package com.alonapps.muniapp;

public class Route {
	private String _tag;
	private String _title;
	
	public Route(String tag, String title)
	{
		this.setTag(tag);
		this.setTitle(title);
		
	}
	public Route()
	{
		this("", "");
	}
	
	public String getTag()
	{
		return _tag;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public void setTag(String tag)
	{
		_tag = tag;
	}
	
	public void setTitle(String title)
	{
		_title = title;
	}
	
	@Override
	public String toString()
	{
		return this.getTitle();		
	}
	@Override
	public boolean equals(Object o) {
		if( o instanceof Route)
		{
			if(((Route)o).getTag() == this.getTag())
				return true;
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.getTag().hashCode();
	}
	
	
}
