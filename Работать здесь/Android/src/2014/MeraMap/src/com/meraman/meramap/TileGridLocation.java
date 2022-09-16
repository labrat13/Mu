package com.meraman.meramap;

import android.graphics.Point;

/**
 * Точка на сетке тил
 * @author smith
 *
 */
public class TileGridLocation 
{
	/**
	 * X coordinate
	 */
	public long X;
	/**
	 * Y coordinate
	 */
	public long Y;
	/**
	 * Default constructor
	 */
	public TileGridLocation()
	{
		X = 0;
		Y = 0;
	}
	/*
	 * Param constructor
	 */
	public TileGridLocation(long x, long y)
	{
		X = x;
		Y = y;
	}
	/**
	 * Object values as string
	 */
	public String toString()
	{
		return ("X=" + X + ";Y=" + Y);
	}
	/**
	 * Создать новый объект, смещенный на указанную величину
	 * @param pt
	 * @return Возвращает новый объект
	 */
	public TileGridLocation Offset(Point pt)
	{
		TileGridLocation res = new TileGridLocation(this.X, this.Y);
		res.X += (long)pt.x;
		res.Y += (long)pt.y;
		return res;		
	}
	/**
	 * Создать новый объект, смещенный на указанную величину
	 * @param dx смещение
	 * @param dy смещение
	 * @return Возвращает новый объект
	 */
	public TileGridLocation Offset(int dx, int dy) 
	{
		TileGridLocation res = new TileGridLocation(this.X, this.Y);
		res.X += (long)dx;
		res.Y += (long)dy;
		return res;
	}
	
	/**
	 * Вычислить смещение как разность между текущей позицией и аргументом
	 * @param loc
	 * @return
	 */
	public TileGridLocation getOffset(TileGridLocation loc)
	{
		TileGridLocation result = new TileGridLocation();
		result.X = this.X - loc.X;
		result.Y = this.Y - loc.Y;
		return result;
	}
	
	
	
}
