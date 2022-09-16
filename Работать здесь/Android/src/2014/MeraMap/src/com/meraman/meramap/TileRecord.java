package com.meraman.meramap;

import android.graphics.Rect;

public class TileRecord {
//	/**
//	 * Tile coordinate
//	 */
//	public int X;
//	/**
//	 *  Tile coordinate
//	 */
//	public int Y;
//	/**
//	 *  Tile coordinate
//	 */
//	public int Z;
	/**
	 *  Screen rectangle for tile
	 */
	public Rect m_ScreenRectangle;
	/** 
	 * Tile pathname and key
	 */
	public String Key;
	/**
	 * Default constructor
	 */
	public TileRecord()
	{
		m_ScreenRectangle = new Rect();
		//X = 0; Y = 0; Z = 0;
	}
	
	/**
	 * Устанавливает размеры прямоугольника для всей тилы целиком
	 * @param top Top of rectangle
	 * @param left Left of rectangle
	 * @param tileSize Tile size value from tile store object
	 */
	public void setScreenRectangle(int top, int left, int tileSize)
	{
		m_ScreenRectangle.top = top;
		m_ScreenRectangle.left = left;
		m_ScreenRectangle.right = m_ScreenRectangle.left + tileSize;
		m_ScreenRectangle.bottom = m_ScreenRectangle.top + tileSize;
	}
	
	
	
}
