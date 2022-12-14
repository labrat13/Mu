// Static Model

// Приложение
namespace com.meraman.meramap
{

	// Класс для хранения позиции
	public class Location
	{

		// X coordinate

		private double m_X;

		// Y coordinate
		private decimal m_Y;

		// Записать значения широты и долготы
		public void setLatLong(double latitude,
						 double longitude)
		{
			m_X = lat; m_y = long; или наоборот - надо уточнить.
		}

		public double getLatitude()
		{
			
		}

		public double getLongitude()
		{
			
		}

		// Set tile X and Y coordinates for tilepos 
		public void setTileXY(long tileX,
						long tileY)
		{
			m_X = (Double)arg0; m_Y = (Double) arg1;
		}

		// Get tile Y positin with offset
		public long getTileY()
		{
			return (long) (m_Y * 256) //tile current width
		}

		// Get tile X position with tile offset
		public long getTileX()
		{
			
		}

		// tile X position without offset
		public int getTileXposition()
		{
			return (int) m_X
		}

		// get tile Y position without offset
		public int getTileYposition()
		{
			
		}

		// get tile X offset within tile
		public int getTileXoffset()
		{
			return ((long)(m_X * 256)) % 256;
		}

		// Get tile Y offset within tile
		public int getTileYoffset()
		{
			
		}

	}// END CLASS DEFINITION Location

} // com.meraman.meramap
