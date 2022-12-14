// Static Model

// Приложение
namespace com.meraman.meramap
{

	// Класс работающий с картой
	// Обязанности:
	// - проверять, что карта доступна для чтения
	// - получить путь к карте
	// - читать файл с карты - возвращать Stream что-ли?
	// - создавать каталог на карте согласно пути
	// - создавать-перезаписывать файл на карте согласно указанному пути - опять же через Stream? Лучше просто путь выдать, а приложение само прочитает-запишет.
	// - проверить, что файл на карте существует
	// 
	//  
	public class SDcard
	{

		protected string sdCardPath;

		// Проверить, что карта доступна для чтения
		public bool isCardCanRead()
		{
			
		}

		// Проверяет, что карта доступна для записи.
		public bool isCardCanWrite()
		{
			
		}

		// Создать промежуточные каталоги в пути, если их не существует.
		public bool createPathTo(string path)
		{
			
		}

		// Проверяет, что вообще доступны внешние хранилища на устройстве
		public static bool isExternalStorageAvailable()
		{
			
		}

		// Создать папки проекта - в корневом каталоге создать папку MERAMAN, в ней файл  блокировки  .nomedia 
		public bool createProjectFolders()
		{
			
		}

		// Получить путь к карте памяти и сохранить во внутренней переменной объекта.
		protected string getCardPath()
		{
			
		}

	}// END CLASS DEFINITION SDcard

} // com.meraman.meramap
