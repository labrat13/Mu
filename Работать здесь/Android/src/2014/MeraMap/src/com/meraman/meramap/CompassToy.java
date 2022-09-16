package com.meraman.meramap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;



/** 
 *  Компас в правом верхнем углу экрана
 */
class CompassToy extends ScreenToy {

	/**
	 * src bitmap of compass, must be loaded from SD card before 
	 */
    private Bitmap m_CompassBitmapSrc;
    /**
     * Compass red arrow bitmap
     */
    private Bitmap m_CompassArrowSrc;
    /**
     * ready-to-draw compass bitmap rotated with current position - not drawed as needed
     */
    private Bitmap m_CompassBitmapReadyToDraw;
    //переменные вынесены сюда для оптимизации - хотя их можно было каждый раз вычислять
    
    private float m_centerArrowX;
    
    private float m_centerArrowY;
    
    private int m_compassBodyX;
    private int m_compassBodyY;
    /**
     * Позиция компаса на экране
     */
    private ScreenLocation m_CompassScreenLocation;
	/**
	 * NT-Param constructor
	 * @param store Initialized  local tile store object
	 * @throws Exception 
	 */
	public CompassToy(LocalTileStore store) throws Exception {
		super(store);
		//Load compass src bitmap from browser folder on sd card
		this.m_CompassBitmapSrc = this.m_fileStore.getCompassImage();
		if(this.m_CompassBitmapSrc == null) 
		{
			throw new Exception("Compass file loading failed");
		}
		//Copy this bitmap also to dst bitmap for first time.
		this.m_CompassBitmapReadyToDraw = Bitmap.createBitmap(this.m_CompassBitmapSrc);
		//load arrow bitmap
		this.m_CompassArrowSrc = this.m_fileStore.getCompassRedArrowImage();
		if(this.m_CompassArrowSrc == null) 
		{
			throw new Exception("Compass red arrow file loading failed");
		}
		m_centerArrowX = (float) (m_CompassArrowSrc.getWidth() / 2.0f);
		m_centerArrowY = (float) (m_CompassArrowSrc.getHeight() / 2.0f);	
		
		//set compass position to upper-left corner of screen
		this.m_CompassScreenLocation = new ScreenLocation(48, 48);
		
		//setup compass base coords for speed optimization
		this.m_compassBodyX = this.m_CompassScreenLocation.X - (this.m_CompassBitmapSrc.getWidth() / 2);
		this.m_compassBodyY = this.m_CompassScreenLocation.Y - (this.m_CompassBitmapSrc.getHeight() / 2);
		
		return;
	}
	
	
	  /**
	   * NT-Отрисовать объект на указанной поверхности в нужном месте
	   * @param canvas
	   */
	@Override
	  public void Draw(Canvas canvas) {
		  if(!this.m_visible) return;
		  
      	//draw compass bitmap as test on center screen
		  //draw compass body
		canvas.drawBitmap(m_CompassBitmapSrc, this.m_compassBodyX, this.m_compassBodyY, null);  
      	//draw compass rotated arrow with dynamic size
		canvas.drawBitmap(m_CompassBitmapReadyToDraw, this.m_CompassScreenLocation.X - (m_CompassBitmapReadyToDraw.getWidth() / 2), this.m_CompassScreenLocation.Y - (m_CompassBitmapReadyToDraw.getHeight() / 2), null); 
      	return;
	  }
	/**
	 * NR-
	 * @param width Новая ширина экрана
	 * @param height Новая высота экрана
	 * @param orientation Новая ориентация экрана
	 */
	  public void ScreenChanged(int width, int height, int orientation) {
		  //orientation can be any of 
		  //Surface.ROTATION_0;
		  //Surface.ROTATION_90;
		  //Surface.ROTATION_180;
		  //Surface.ROTATION_270;
		  //TODO Установить новое положение всех отображаемых сущностей согласно размерам экрана
	  }
	  /**
	   * NT-Update compass position
	   * @param zAngle new compass angle in range 0-359 degrees
	   */
	public void updateCompass(float zAngle) 
	{
		//Создаем новую картинку из исходной поворотом на заданный угол
		//создать матрицу для поворота картинки вокруг центра 	
		Matrix mat = new Matrix();
		mat.postRotate(zAngle, this.m_centerArrowX, this.m_centerArrowY);
		//создать новую картинку стрелки повернутую на заданный угол - теперь размеры картинки могут быть больше исходной, так как у квадрата есть углы.
		Bitmap bmpT = Bitmap.createBitmap(this.m_CompassArrowSrc, 0, 0, this.m_CompassArrowSrc.getWidth(),  m_CompassArrowSrc.getHeight(), mat, true);
		//TODO: Наложить картинки друг на друга - будем делать это при рисовании
		//store bitmap
		m_CompassBitmapReadyToDraw = bmpT;//set new bitmap
		return;
	}
		
	  
	  
	
}