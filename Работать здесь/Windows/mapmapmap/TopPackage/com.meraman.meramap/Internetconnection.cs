// Static Model

// Приложение
namespace com.meraman.meramap
{

	// Класс управляет интернет-соединением с сервером tilestore для фоновой загрузки тил.
	// Обязанности:
	// - проверка доступности интернет-соединения
	// - получение IP сервера по имени
	// - скачивание файла тилы с сервера с таймаутом
	// - закрытие соединения.
	
	public class Internetconnection
	{

		// Проверяет возможность соединения с интернетом.
		public bool isConnectionAvailable()
		{
			
		}

		// Получает ип сервера по его имени. Этот ип в дальнейшем кешируется.
		public object getServerIP()
		{
			
		}

		// скачивает файл с сервера
		public object downloadFile(__error__ path)
		{
			
		}

		public void CloseConnection()
		{
			
		}

	}// END CLASS DEFINITION Internetconnection

} // com.meraman.meramap
