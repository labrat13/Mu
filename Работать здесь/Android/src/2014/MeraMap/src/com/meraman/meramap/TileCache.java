package com.meraman.meramap;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import android.graphics.Bitmap;


/**
 * Кеш тил для ускорения отрисовки карты на экране
 * @author smith
 *
 */
class TileCache {
	//TODO Add members here
	/**
	 * Словарь-хеш тил. 
	 * Тилы ищутся по ключам. 
	 * Ключом является координатный путь к файлу (z/y/x.jpg или подобный).
	 *  
	 */
	private Hashtable<String, Bitmap> m_tileDictionary;
	/**
	 * Хранилище тил производное от LocalTileStore
	 */
	public LocalTileStore m_TileStore;
	/**
	 * Тила заменяющая отсутствующие тилы карты.
	 * Хранится здесь постоянно, часто используется.
	 * Должна загружаться при инициализации класса.
	 */
	private Bitmap m_NoMapTile;
	/**
	 * Изображение метки позиции гпс
	 */
	private Bitmap m_GpsLabel;
	
	/**
	 * NR-Default constructor
	 * @param store Экземпляр хранилища тил согласно типу карты 
	 */
	public TileCache(LocalTileStore store)
	{
		//create dictionary 
		m_tileDictionary = new Hashtable<String, Bitmap>();

		m_TileStore = store;
		//get noMap tile bitmap
		m_NoMapTile = store.getNoMapTile();
		//get gps label image
		m_GpsLabel = store.getGpsLabelImage();
		
		//TODO Add init code here 		
		return;
	}
	/**
	 * Получить тилу noMap
	 * @return
	 */
	public Bitmap getNoMapTile()
	{
		return m_NoMapTile;
	}
	/**
	 * Получить картинку метки гпс
	 * @return
	 */
	public Bitmap getGpsLabel()
	{
		return m_GpsLabel;
	}
	
	
    /**
     * NT-Get bitmap from cache
     * @param coordPath Tile key as coordinate path to tile z/y/x.jpg
     * @return Return Bitmap or null, if key not exists
     * 
     */
    public Bitmap getTileFromCache(String coordPath)
    {
    	if(m_tileDictionary.containsKey(coordPath))
    	return m_tileDictionary.get(coordPath);
    	else return null;
    }
    
    /**
     * NT-Create temporary tile list for update cache
     * @param records List of records about tiles from TileGrid 
     * @return
	 * 
     */
	private ArrayList<String> createTileKeyList(ArrayList<TileRecord> records) {
        
        ArrayList<String> result = new ArrayList<String>();
        for(int i = 0; i < records.size(); i++)
        {
        	//get tile record
        	TileRecord tr = records.get(i);
        	//get tile key
        	//String tilekey = this.m_TileStore.createTileCoordPath(tr.X, tr.Y, tr.Z); - ключ создается при создании записи тилы
        	result.add(tr.Key);	
        }
		return result;
	}
    
	/**
	 * NT-Обновить кеш тил согласно новому списку координатных путей тил
	 * @param records Список записей тил из TileGrid
	 */
    public void updateTileCache(ArrayList<TileRecord> records)
    {
        //сначала надо создать список ключей нужных для показа тил
    	ArrayList<String> tileKeys = this.createTileKeyList(records);
        //тут все тилы, которых нет в списке но есть в словаре, надо выгрузить из памяти и удалить из словаря
        //а потом в словарь загрузить тилы, которые есть в списке но нет в словаре
        //это должен быть именно список, чтобы из него выкинуть тилы, которые уже есть, и оставить только тилы, которые надо загрузить
    	
    	//список для тил, которые надо удалить из кеша
    	ArrayList<String> tilesToRemove = new ArrayList<String>();
    	
    	//Получаем три списка:
    	//tileKeys - Список ключей тил, которые должны храниться в кеше
    	//m_tileDictionary.keys() - Список ключей тил, которые уже находятся в кеше
    	//tilesToRemove - Список тил, которые надо удалить из кеша
    	//просматриваем элементы словаря и удаляем те из них, которых нет в tileKeys.
    	//Словарь нельзя модифицировать в процессе просмотра.
    	//просматриваем все элементы словаря и если их нет в tileKeys, то пишем в tilesToRemove
    	//а если они есть в tileKeys, то удаляем их оттуда - эти тилы не надо загружать, они уже есть.
    	//потом по tilesToRemove  удаляем элементы из словаря
    	//потом загружаем те элементы, которые остались в tileKeys
    	String tileCoordPath;
    	Enumeration<String> keys;
    	keys = m_tileDictionary.keys();
    	while(keys.hasMoreElements())
    	{
    		tileCoordPath = keys.nextElement();
    		//если тила из словаря есть в списке тил для загрузки, то удалить ее оттуда
    		//а если ее нет, то поместить тилу из словаря в список удаляемых из словаря тил.
    		if(!tileKeys.contains(tileCoordPath)) //проверить, что сравнение по значению а не по адресу
    			tilesToRemove.add(tileCoordPath);
    		else
    			tileKeys.remove(tileCoordPath);
    	}
    	//Теперь tilesToRemove содержит ключи тил, которые надо удалить из словаря
    	//Удаляем
    	Bitmap bmp;
    	for(int i = 0; i < tilesToRemove.size(); i++)
    	{
    		tileCoordPath = tilesToRemove.get(i);
    		//get bitmap object and remove it
    		bmp = m_tileDictionary.get(tileCoordPath);
    		//освобождаем память занятую картинкой тилы
    		//если тила не была загружена, словарь содержит null
    		if(bmp != this.m_NoMapTile)
    			bmp.recycle(); //TODO Надо ли вызывать Bitmap.recycle() чтобы освобождать память?
    		m_tileDictionary.remove(tileCoordPath);
    	}
    	//Теперь этот список не нужен, его можно удалить
    	tilesToRemove = null;
    	
    	//Загружаем новые тилы в кеш 
    	for(int i = 0; i < tileKeys.size(); i++)
    	{
    		tileCoordPath = tileKeys.get(i);
    		bmp = this.m_TileStore.getTile(tileCoordPath);
    		//если тилы нет, вместо нее в кеше нельзя хранить null - в словарь нельзя вставлять элементы null
    		//поэтому подставляем вместо тилы номап тилу, а в Draw будем их перехватывать и отрисовывать? 
    		if(bmp == null)
    			bmp = this.m_NoMapTile;
    		m_tileDictionary.put(tileCoordPath, bmp);	
    	}
    	
    	return;
    }
    
    
    
	
	
}