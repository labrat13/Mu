package com.meraman.meramap;
/**
 * Позиция на карте в градусах 
 * @author smith
 *
 */
public class GeoLocation {
	
	/**
	 * X coordinate
	 */
	public double X;
	/**
	 * Y coordinate
	 */
	public double Y;	
	
	/*
	 * Default constructor
	 */
	public GeoLocation()
	{
		this.X = 0.0;
		this.Y = 0.0;
	}
	/**
	 * Parameter constructor
	 * @param lat Latitude
	 * @param lon Longitude
	 */
	public GeoLocation(double lat, double lon)
	{
		this.Y = lat;
		this.X = lon;
	}
	/**
	 * Copy constructor
	 * @param latlong Object to copy
	 */
	public GeoLocation(GeoLocation arg) 
	{
		this.X = arg.X;
		this.Y = arg.Y;
	}
	/**
	 * Get Latitude
	 * @return
	 */
	public double getLatitude()
	{
		return this.Y;
	}
	/**
	 * Get Longitude
	 * @return
	 */
	public double getLongitude()
	{
		return this.X;
	}
	/**
	 * Set Latitude and Longitude
	 * @param lat
	 * @param lon
	 */
	public void setLatLong(double lat, double lon)
	{
		this.Y = lat;
		this.X = lon;
	}
//	/**
//	 * Set tile coordinates
//	 * @param tileX
//	 * @param tileY
//	 */
//	public void setTileXY(long tileX, long tileY)
//	{
//		this.X = (double) tileX;
//		this.Y = (double) tileY;		
//	}
	/**
	 * Return string representation of object
	 * @return
	 */
	@Override
	public String toString()
	{
		return "X=" + String.valueOf(X) + ";Y=" + String.valueOf(Y);
	}
}//end class
