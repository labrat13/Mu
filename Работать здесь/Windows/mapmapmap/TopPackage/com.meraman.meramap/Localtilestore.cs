// Static Model

// Приложение
namespace com.meraman.meramap
{

	// Локальное хранилище тил. 
	// Обязанности: 
	// -Выдавать тилы по координатному пути к ним.
	// -Транслировать координаты ГПС в номера тил и координаты в них.
	// -Транслировать номера и позицию в тиле в координаты ГПС
	// -Выдавать для каждого слоя тил информацию о масштабе для DistanceView. Лучше всего сразу всю оптом, чтобы не загружать каждый раз из файла.
	// - проверять, есть ли указанная тила в хранилище тил
	// - добавлять тилу в хранилище, автоматически создавать необходимые каталоги
	// -
	public class Localtilestore	: Browserfilestore
	{

		// Корневой каталог папок тил данного типа.
		private string m_tileRootFolder;

		// получить тилу по координатному пути к ней
		public Bitmap getTile(string CoordPath)
		{
			
		}

		// Возвращает труе если тила с данным координатным именем существует в хранилище.
		public bool isTileExists(string CoordPath)
		{
			
		}

		// Создать полный путь к файлу тилы для проверки-создания-чтения-записи файла.
		public string createFullTilePath()
		{
			
		}

		// Перевести позицию в градусах в позицию в тилах
		public static Location LocationToTile(int Zaxis,
										Location location)
		{
			
		}

		// Перевести позицию в тиле в позицию в градусах
		public static Location TileToLocation(Location tilepos,
										int Zaxis)
		{
			
		}

		public void конструктор()
		{
			
		}

	}// END CLASS DEFINITION Localtilestore

} // com.meraman.meramap
