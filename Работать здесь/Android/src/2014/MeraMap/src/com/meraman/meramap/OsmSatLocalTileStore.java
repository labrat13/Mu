package com.meraman.meramap;
/**
 * Класс представляет коллекцию OSM спутниковых тил на SD карте
 * @author smith
 *
 */
class OsmSatLocalTileStore extends LocalTileStore {

	public OsmSatLocalTileStore() throws Exception {
		super();
		//override superclass values
		m_tileRootFolder = "/MERAMAN/osmsat/";
		//m_noMapTileName = "nomap.jpg";
		//m_tileFileExtension = ".jpg";
		//m_tileSize = 256;
	}
	/**
	 * NT-Создать координатный путь к тиле
	 * @param x Координата тилы
	 * @param y Координата тилы
	 * @param z Координата тилы
	 * @return Строка вида z/x/y.ext
	 */
	@Override
	public String createTileCoordPath(int x, int y, int z)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(z);
		sb.append(m_Slash);
		sb.append(x);
		sb.append(m_Slash);
		sb.append(y);
		sb.append(m_tileFileExtension);
		return sb.toString();
	}
	
}