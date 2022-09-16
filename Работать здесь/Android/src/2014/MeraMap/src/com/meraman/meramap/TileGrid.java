package com.meraman.meramap;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.location.Location;
//XXX - класс перенесен в View - можно было бы создать субкласс и в нем обрабатывать события, чтобы меньше кода было.
//XXX - Класс выглядит несколько излишним. Хотя он хранит много кода, но некоторые переменные нужно вынести в Activity,  а отсюда к ним не будет доступа.
//XXX  Класс пока использовать как песочницу для отработки кода рисования карты.
//TODO Надо навести порядок в терминах координат: координаты гпс - мировые координаты; координаты тил - координаты на карте; координаты экрана - координаты экрана;
//TODO Переписать соответственно документацию и функционал
/**
 * Класс экранной сетки тил 
 * @author smith
 *
 */
class TileGrid extends ScreenToy {
	//TODO Add members here
	/**
	 * Кеш тил 
	 */
	private TileCache m_tileCache;
	
	//TODO: добавить список записей тил и функции для работы с ним
	//Эти переменные и код тут предварительно, а потом возможно некоторые вынесены будут в View или даже в Activity
	/**
	 * Список записей тил для отображения карты
	 */
	private ArrayList<TileRecord> m_tileRecords;
	
	/**
	 * Ширина экрана
	 */
	private int m_ScreenWidth;
	/**
	 * Высота экрана
	 */
	private int m_ScreenHeight;
	/**
	 * Номер масштаба карты
	 */
	private int m_zoomLevel;
//	/**
//	 * Точка центра экрана как кеш для вычислений - следует ее заменить вычислениями по длине и ширине
//	 */
//	private ScreenLocation m_screenCenterPoint;
	/**
	 * Точка центра экрана на сетке тил
	 */
	private TileGridLocation m_screenCenterTileGridPosition;
	/**
	 * Точка на карте, соответствующая центру экрана
	 */
	@SuppressWarnings("unused")
	private GeoLocation m_screenCenterLocation;
	/**
	 * Координаты устройства от гпс - для отображения метки гпс
	 */
	private GeoLocation m_GpsDeviceLocation;
	/**
	 * Прямоугольник метки гпс
	 */
	private Rect m_GpsLabelRectangle;
	
	/**
	 * NR-Param constructor
	 * @param store Ссылка на экземпляр хранилища тил соответствующего типу карты
	 */
	public TileGrid(LocalTileStore store)
	{
		super(store);//call superclass constructor
		//create tile cache object
		this.m_tileCache = new TileCache(store);
		//Создаем список для записей о тилах
		m_tileRecords = new ArrayList<TileRecord>();
		//инициализация всяких параметров. Некоторые из них должны где-то в настройках храниться, чтобы оттуда загружаться
		//но пока просто от балды назначим
		this.m_ScreenHeight = 240;
		this.m_ScreenWidth = 320;
		this.m_zoomLevel = 3;
		this.m_GpsLabelRectangle = new Rect();
		this.m_GpsDeviceLocation = new GeoLocation(33, 33);
		this.m_screenCenterLocation = m_GpsDeviceLocation;
		this.m_screenCenterTileGridPosition = new TileGridLocation();
		this.GoToLocation(this.m_GpsDeviceLocation);
		//TODO Add init code here
		//noMap тилу храним в m_TileCache, а используем ее здесь. Она соответствует типу карты и хранится вместе с тилами этой карты.
	}
	
	  /**
	   * NR-Отрисовать объект на указанной поверхности
	   * @param canvas
	   */
	 @Override
	  public void Draw(Canvas canvas) {
		  if(!this.m_visible) return;
		  Bitmap bmp = null;
		  TileRecord tr;
		  String s;
		  //Рисовать сетку тил
		  for(int i = 0; i < this.m_tileRecords.size(); i++)
		  {
			  //get tile record
			  tr = this.m_tileRecords.get(i);
			  //get tile path-key
			  //s = this.m_fileStore.createTileCoordPath(tr.X, tr.Y, tr.Z);//создание пути-ключа перенесено в функцию updateGrid().
			  s = tr.Key;
			  //get tile image
			  bmp = this.m_tileCache.getTileFromCache(s);
			  // check for nomap tile - removed
			  //draw image 
			  canvas.drawBitmap(bmp, null, tr.m_ScreenRectangle, null);
		  }
		  
		  //рисовать позицию гпс
		  bmp = this.m_tileCache.getGpsLabel();//получить картинку гпс из кэша
		  canvas.drawBitmap(bmp, null, this.m_GpsLabelRectangle, null);//нарисовать картинку
				  
		  return;
	  }
	 
	/**
	 * NR-
	 * @param width Новая ширина экрана
	 * @param height Новая высота экрана
	 * @param orientation Новая ориентация экрана
	 */
	 @Override
	  public void ScreenChanged(int width, int height, int orientation) {
		  //orientation can be any of 
		  //Surface.ROTATION_0;
		  //Surface.ROTATION_90;
		  //Surface.ROTATION_180;
		  //Surface.ROTATION_270;
		  this.m_ScreenWidth = width;
		  this.m_ScreenHeight = height;
		  //пересчитать сетку тил и перерисовать экран
		  this.updateView();
		 
		  return;
	  }
	 /**
	  *  NR-Обновить местоположение устройства по ГПС.
	  * @param arg0 Объект с данными от ГПС.
	  */
	public void updateLocation(Location arg0) 
	{
		//сохраним позицию в переменных как текущую
		this.m_GpsDeviceLocation.X = arg0.getLongitude();
		this.m_GpsDeviceLocation.Y = arg0.getLatitude();
		// TODO добавить код обработки события гпс по режимам
		//TODO в зависимости от режима, тут надо либо обновить представление метки гпс, либо полностью обновить все.
		this.updateView();  //or this.updateGpsRect()
		
		return;
	}
	 /**
	  * NT-Пользователь прокручивает вид
	  * @param dX
	  * @param dY
	  */
	public void Scroll(int dX, int dY) 
	{
		//сместить Т1 на dx dy
		this.m_screenCenterTileGridPosition = this.m_screenCenterTileGridPosition.Offset(dX, dY);
		//вычислить Т2 по Т1
		this.m_screenCenterLocation = this.m_fileStore.TileToLocation(this.m_screenCenterTileGridPosition, this.m_zoomLevel);
		//обновить сетку тил
		this.updateView();
		
		return;
	}
	/**
	 * NT-Пользователь желает изменить масштаб карты в некоторой точке экрана.
	 * Эта точка должна остаться на прежнем месте, а все остальные позиции изменятся.
	 * @param dZ  - -1 уменьшает зум, +1 увеличивает зум
	 * @param Xpos - координата точки зума
	 * @param Ypos - координата точки зума
	 */
	public void Zoom (int dZ, int Xpos, int Ypos)
	{
		//вычислить смещение точки зума на экране от точки середины экрана
		int dx = Xpos - (this.m_ScreenWidth / 2);
		int dy = Ypos - (this.m_ScreenHeight / 2);	
		//получить координаты гпс экранной точки зума
		GeoLocation geoloc = this.ScreenToLocation(Xpos, Ypos);
		//изменить зум (в пределах 0..20)
		this.m_zoomLevel += dZ;
		if(this.m_zoomLevel < 0) this.m_zoomLevel = 0;
		else if(this.m_zoomLevel > 20) this.m_zoomLevel = 20;
		//по координатам гпс получить координаты точки зума на сетке тил
		TileGridLocation tgloc = this.m_fileStore.LocationToTile(m_zoomLevel, geoloc);
		//координаты точки зума вычесть смещение, полученную позицию записать как новый центр экрана на сетке тил Т1
		this.m_screenCenterTileGridPosition = tgloc.Offset(-dx, -dy);
		//по Т1 вычислить новую Т2 и записать ее
		this.m_screenCenterLocation = this.m_fileStore.TileToLocation(this.m_screenCenterTileGridPosition, this.m_zoomLevel);
		//обновить вид
		updateView();
		
		return;
	}

	
	//**********************************************************************************
	//Функции сетки тил
	//**********************************************************************************
	/**
	 * NT-Пересоздать сетку тил для отображения карты. Ничего кроме этого не делается.
	 * @param center Тут требуется точка центра экрана в координатах тил, она доступна как член класса, но это напоминание, что эта точка тут используется.  
	 * После теста убрать параметр
	 */
	private void updateGrid(TileGridLocation center )
	{
		//clear tile records list
		this.m_tileRecords.clear();
		//get tile actual size
		int tileSize = this.m_fileStore.getTileSize();
		//создаем большой прямоугольник, размером с экран, на сетке тил, с центром в центре экрана типа
		//вернее, не создаем конечно, просто получаем координаты точек углов и по ним определяем номера тил
		long longX1 = center.X - (this.m_ScreenWidth / 2); 
		long longX2 = longX1 + this.m_ScreenWidth;
		
		int xFrom = (int) (longX1 / tileSize);
		//неудачно - проблема не решена
		//int xFrom = this.RoundSpec(longX1, tileSize);//округлять в отрицательную сторону надо к меньшему значению, поэтому пишем свою функцию
		int xTo = (int) (longX2 / tileSize);

		long longY1 = center.Y - (this.m_ScreenHeight / 2); 
		long longY2 = longY1 + this.m_ScreenHeight;
		
		int yFrom = (int)(longY1 / tileSize);
		//неудачное решение - проблема не решена
		//int yFrom = this.RoundSpec(longY1, tileSize);//округлять в отрицательную сторону надо к меньшему значению, поэтому пишем свою функцию
		int yTo = (int)(longY2 / tileSize);

		//сразу можно вычислить смещение от начала сетки тил
		int tileOffsetX = (int) (longX1 % tileSize);
		int tileOffsetY = (int) (longY1 % tileSize);
		//теперь в циклах по Х и У создаем записи тил, заполняем их данными и добавляем в список записей тил
		//вычисляем пределы для циклов
		int xCnt = (xTo - xFrom) + 1;
		int yCnt = (yTo - yFrom) + 1;
		//циклы
		TileRecord trec;
		int xPos, yPos;
		int tileX, tileY, tileZ;
		for(int xx = 0; xx < xCnt; xx++)
			for(int yy = 0; yy < yCnt; yy++)
			{
				trec = new TileRecord();
				//set tile coords
				tileX = xFrom + xx;
				tileY = yFrom + yy;
				tileZ = this.m_zoomLevel;
				//set tile path-key
				trec.Key = this.m_fileStore.createTileCoordPath(tileX, tileY, tileZ);
				//set tile screen rectangle
				xPos = (xx * tileSize) - tileOffsetX;
				yPos = (yy * tileSize) - tileOffsetY;
				trec.setScreenRectangle(yPos, xPos, tileSize);
				//add to tile record list
				this.m_tileRecords.add(trec);
			}
		//теперь собственно сетка тил создана, можно выйти. 
		return;
	}
	/**
	 * Округлить деление всегда в меньшую сторону независимо от знака
	 * @param longX1 - позиция на сетке тил
	 * @param tileSize - размер тилы
	 * @return номер тилы, которой принадлежит позиция на сетке тил
	 */
	private int RoundSpec(long longval, int tilesize) 
	{
		int result = (int)(longval / tilesize);
		if(longval < 0)
		{
			int t = (int)(longval % tilesize);
			if(t != 0) result--; 
		}
		return result;
	}

	/**
	 * NT-Вычислить координаты произвольной точки на экране по координатам средней точки
	 * @param x Координата точки
	 * @param y Координата точки
	 * @return Координаты точки по ГПС
	 */
	private GeoLocation ScreenToLocation(int x, int y)
	{
		//Вычислить смещение до середины экрана
		int dx = x - this.m_ScreenWidth / 2;
		int dy = y - this.m_ScreenHeight / 2;
		//вычислить позицию на сетке тил
		TileGridLocation loc = this.m_screenCenterTileGridPosition.Offset(dx, dy);
		//вычислить позицию на гпс
		GeoLocation result = this.m_fileStore.TileToLocation(loc, this.m_zoomLevel);
		//вернуть позицию
		return result;		
	}
	/**
	 * NT-Вычислить координаты на экране по координатам гпс  
	 * @param latlong координаты гпс
	 * @return Координаты на экране
	 */
	private ScreenLocation LocationToScreen(GeoLocation latlong)
	{
		//вычислить точку на сетке тил
		TileGridLocation tgloc = this.m_fileStore.LocationToTile(this.m_zoomLevel, latlong);
		//вычислить смещение до точки центра на сетке грид
		//dx = tgloc.x - V1.x;dy = tgloc.y - V1.y;
		TileGridLocation offset = tgloc.getOffset(m_screenCenterTileGridPosition);
		//сложить смещение и центральную точку
		ScreenLocation result = new ScreenLocation();
		result.X = (this.m_ScreenWidth / 2) + (int) offset.X;
		result.Y = (this.m_ScreenHeight/ 2) + (int) offset.Y;
		//вернуть экранную позицию
		return result;
	}
	
	/**
	 * NT-Перейти к отображению новой позиции на карте
	 * @param latlong
	 */
	public void GoToLocation(GeoLocation latlong)
	{
		//Т2 = аргумент
		this.m_screenCenterLocation = new GeoLocation(latlong);
		//вычислить Т1 по Т2
		this.m_screenCenterTileGridPosition = this.m_fileStore.LocationToTile(this.m_zoomLevel, latlong);
		//обновить вид
		this.updateView();
		return;
	}
	/**
	 * NT-Установить прямоугольник для рисования метки позиции гпс 
	 */
	public void updateGpsRect()
	{
		//Получить экранные координаты точки гпс по координатам гпс
		ScreenLocation loc = this.LocationToScreen(this.m_GpsDeviceLocation);
		//создать прямоугольник 32x32 для метки гпс
		this.m_GpsLabelRectangle = new Rect();
		this.m_GpsLabelRectangle.top = loc.Y - 32;
		this.m_GpsLabelRectangle.left = loc.X;		
		this.m_GpsLabelRectangle.bottom = loc.Y;
		this.m_GpsLabelRectangle.right = this.m_GpsLabelRectangle.left + 32;
		
		return;
	}
	
	/**
	 * NT-Приготовить все данные для рисования карты
	 */
	private void updateView()
	{
		//update tile grid
		this.updateGrid(this.m_screenCenterTileGridPosition); //TODO remove argument after full test
		this.m_tileCache.updateTileCache(this.m_tileRecords);//update cache
		//update gps label
		this.updateGpsRect();
		//TODO Add other updates here
		return;
	}
	
	
	
	
	
}