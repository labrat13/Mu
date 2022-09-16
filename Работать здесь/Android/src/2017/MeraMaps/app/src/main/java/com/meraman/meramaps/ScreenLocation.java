package com.meraman.meramaps;

/**
 * Класс представляет точку на экране
 * @author smith
 *
 */
class ScreenLocation
{
    /**
     * coordinate
     */
    public int X;
    /**
     * coordinate
     */
    public int Y;
    /**
     * Default constructor
     */
    public ScreenLocation()
    {
        X = 0;
        Y = 0;
    }
    /**
     * Parameter constructor
     * @param x
     * @param y
     */
    public ScreenLocation(int x, int y)
    {
        X = x;
        Y = y;
    }



}