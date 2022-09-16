package com.meraman.meramap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

//XXX Класс пока ничего не делает, займемся им позже
/** 
 *  Класс для отображения шкалы расстояния в левом нижнем углу карты.
 */
class DistanceToy extends ScreenToy {
	
	/**
	 * bitmap for composed toy image  
	 */
	private Bitmap m_DistanceBitmap;
	/**
	 * Массив величин расстояния в метрах
	 */
	private String[] m_LevelMeterValues;
	/**
	 * Массив величин расстояния в пикселах 
	 */
	private int[] m_LevelPixelValues;
	/**
	 * Ширина экрана
	 */
	private int m_screenWidth;
	/**
	 * Высота экрана
	 */
	private int m_screenHeight;

	/**
	 * Parameter constructor
	 * @param store Initialized local tile store object
	 * @throws Exception  Invalid distance file format
	 */
	public DistanceToy(LocalTileStore store) throws Exception {
		super(store);
		
		m_screenWidth = 320; //some default values
		m_screenHeight = 240;
		//TODO Add init code here
	}
 
	/**
	 * Обновить вид согласно новому уровню карты
	 * @param zoomLevel
	 */
	public void Update(int zoomLevel)
	{
		//TODO Сделать вычисление масштаба согласно позиции и номеру слоя
		//Этот код тут старый и недоделанный - сам масштаб зависит от точки на карте и номера слоя.
		//get level values
		String label = String.valueOf(zoomLevel);
		int px = 20;
		//calculate size and create new bitmap
		//for label 
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(20.0f);
		Rect rc = new Rect();
		paint.getTextBounds(label, 0, label.length(), rc);
		//get max width
		int w = Math.max(px, rc.width());
		//calc bitmap size
		int h = rc.height() + 20;
		w += 40;
		//create bitmap
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		this.m_DistanceBitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
		Canvas canvas = new Canvas(this.m_DistanceBitmap);
		//draw on canvas
		//TODO доделать это все тут
		canvas.drawARGB(127, 192, 192, 192);
		canvas.drawText(label, 10, 20, paint);

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
		  this.m_screenHeight = height;
		  this.m_screenWidth = width;
		  
	  }
	
	  /**
	   * NR-Отрисовать объект на указанной поверхности в нужном месте
	   * @param canvas
	   */
	@Override
	  public void Draw(Canvas canvas) {
		  if(!this.m_visible) return;
		  
		  if(this.m_DistanceBitmap == null) return;
		  //draw bitmap on 10, 10 from  bottomleft point
		  int y = this.m_screenHeight - 10 - this.m_DistanceBitmap.getHeight();
		  int x = 10;
		  canvas.drawBitmap(this.m_DistanceBitmap, (float)x, (float)y, null);
		  
		  return;
	  }

//	Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
//	Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
//	Canvas canvas = new Canvas(bmp);
}