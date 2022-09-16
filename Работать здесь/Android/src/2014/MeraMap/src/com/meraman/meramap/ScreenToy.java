package com.meraman.meramap;

import android.graphics.Canvas;
/**
 * Общий класс всех экранных штучек
 * @author smith
 *
 */
abstract class ScreenToy {
	
	//TODO Добавить события клика пользователя по экрану. Если клик был обработан, функция должна возвращать true иначе false. 
	//Соответственно если клик не был обработан, в родительском коде следующий объект этого класса вызывается для обработки события.
/**
 * Флаг что объект должен отображаться на экране
 */
  protected boolean m_visible;
  /**
   * Хранилище тил и прочих файлов для карты данного типа
   */
  protected LocalTileStore m_fileStore;
/**
 * Parameter constructor
 * @param store Хранилище карт данного типа
 */
  public ScreenToy(LocalTileStore store)
  {
	  this.m_fileStore = store;
	  return;
  }
  
  /**
   * Отрисовать объект на указанной поверхности в нужном месте
   * @param canvas
   */
  public void Draw(Canvas canvas) {
	  if(!this.m_visible) return;
  }
/**
 * 
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
	  
  }
/**
 * Показать или спрятать объект
 * @param showOrHide true for show, false for hide
 */
  public void Show(boolean showOrHide) {
	  m_visible = showOrHide;
  }

}
