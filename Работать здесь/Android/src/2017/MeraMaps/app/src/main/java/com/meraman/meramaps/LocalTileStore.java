package com.meraman.meramaps;

//****************************************
/*    Folder structure on SD Card

.android_secure  - folder - папка управляется андроидом
DCIM      - camera folder - папка вероятно фоток камеры для совместимости
LOST.DIR  - folder        - неизвестно зачем
MERAMAN   - project folder - основная папка проекта
   .nomedia - file prevent mediascan - надо создавать, иначе все файлы в этой папке будут включены в коллекцию устройства.
   browser - browser files folder - папка файлов браузера вроде картинок и такого прочего
      compass.png  - картинка компаса
      comparr.png - картинка красной стрелки компаса
      gpslabel.png - файл метки позиции гпс
      xxx.xxx  - другие файлы браузера   - на будущее
   map     - моя основная карта
      nomap.jpg - nomap tile   обязательно рядом с папками тил
      xxx.png  - файл(ы) линейки масштаба
      0/0/0.jpg - tile in folders  папки с тилами по уровням в виде z/y/x.jpg
   osmmap  - рисованная карта из общедоступного источника
      nomap.jpg - nomap tile
      0/0/0.jpg - tile in folders
   osmsat  - спутниковая карта из общедоступного источника - пока не размещена на SD-карте
      nomap.jpg - nomap tile
      0/0/0.jpg - tile in folders
*/
//****************************************



//XXX функции и переменные класса вроде готовы

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Класс представляет локальное хранилище файлов и в том числе тил с SD-картой
 */
class LocalTileStore {
    /**
     * Имя файла метки текущей позиции гпс
     */
    private static final String GpsLabelFileName = "gpslabel.png";
    /**
     * Имя файла компаса
     */
    private static final String CompassFileName = "compass.png";
    /**
     * Имя файла красной стрелки компаса
     */
    private static final String CompassRedArrowFileName = "comparr.png";
    /**
     * Строка пути к SD карте
     */
    protected String m_sdCardPath;
    /**
     * Строка пути до папки браузера, без пути SD карты
     */
    private final String m_BrowserFolderPath;
    /**
     * Слеш для путей файлов
     */
    public String m_Slash;
    /**
     * Корневой каталог папок тил данного типа
     */
    protected String m_tileRootFolder;
    /**
     * Расширение файла тилы в коллекции тил
     */
    protected String m_tileFileExtension;
    /**
     * Размер тилы в пикселах
     */
    protected int m_tileSize;
    /**
     * Имя и расширение файла noMap тилы
     */
    protected String m_noMapTileName;



    /**
     * NT-Default constructor
     * @throws Exception
     */
    public LocalTileStore() throws Exception
    {
        m_Slash = File.separator;
        //TODO Заменить все слеши в текстах на File.separator - а не надо, все равно работать будет
        m_tileFileExtension = ".jpg";//установим пока расширение файла тилы по умолчанию.
        m_BrowserFolderPath = "/MERAMAN/browser/";
        m_tileRootFolder = "/MERAMAN/map/";//установим каталог тил по умолчанию. Это значение надо переопределить под выбранный набор тил.
        m_tileSize = 256;//размер стороны тилы в пикселах
        m_noMapTileName = "nomap.jpg"; //TODO Убедиться, что имя номап тилы правильное
        //SD card initializing
        //check that external storage is readable
        this.m_sdCardPath = this.tryGetSdCardPath();
        if(this.m_sdCardPath == null)
            throw new java.lang.Exception("SD card not found!"); //TODO перенести строку об ошибке в ресурсы, если можно
        //check card is readable
        if(this.isCardCanRead() == false)
            throw new java.lang.Exception("SD card not readable!"); //TODO перенести строку об ошибке в ресурсы, если можно
        //TODO произвести инициализацию класса полностью.
    }
    /**
     * NT-Получить размер стороны тилы в пикселах
     * @return
     */
    public int getTileSize()
    {
        return m_tileSize;
    }

    /**
     * NT-Return true if current card is readable
     * @return
     * Функция пытается проверить наличие файла noMap в его обычном месте.
     * Если такой файл есть, то возвращается true. В остальных случаях возвращается false.
     */
    public boolean isCardCanRead() {
        boolean result = false;
        try {

            if(this.isTileExists(this.m_noMapTileName))
                result = true;
        }
        catch(Exception ex)
        {
        }
        return result;
    }
    /**
     * NR-Return true if current card readable and writable
     * @return
     */
    public boolean isCardCanWrite() {
        boolean result = false;
        try {
            //TODO Придумать как тут проверить, что можно писать на SD-карту
            result = true;
        }
        catch(Exception ex)
        {
        }
        return result;
    }
    /**
     * NT-Get card full size in bytes
     * @return
     */
    public long getCardSize() {
        File f = new File(this.m_sdCardPath);
        return f.getTotalSpace();
    }
    /**
     * NT-Get card free space in bytes
     * @return
     */
    public long getCardFreeSpace() {
        File f = new File(this.m_sdCardPath);
        return f.getFreeSpace();
    }
    /**
     * NT-Return true if external storage available on device
     * @return
     */
    public static boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    /**
     * NR-Create folders for Meraman Map project if card is clean
     * @return Return true if all success. Return false if any exceptions occurred.
     */
    boolean createProjectFolders()
    {
//TODO: add code here
        //вызывается очень редко
        //create folder MERAMAN
        //create file .nomedia
        //create folder browser
        //...
        return false;
    }

    /**
     * NT-Get card path
     * @return Returns card path from object variable
     */
    protected String getCardPath()
    {
        return this.m_sdCardPath;
    }

    /**
     * NT-Получить полный путь к папке браузера
     * @return
     */
    public String getBrowserFolderPath()
    {
        return this.m_sdCardPath + this.m_BrowserFolderPath;
    }
    /**
     * NT-Получить полный путь к папке карты
     * @return
     */
    public String getMapFolderPath()
    {
        return this.m_sdCardPath + this.m_tileRootFolder;
    }

    /**
     * NT-Создать координатный путь к тиле
     * @param x Координата тилы
     * @param y Координата тилы
     * @param z Координата тилы
     * @return Строка вида z/y/x.ext
     */
    public String createTileCoordPath(int x, int y, int z)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(z);
        sb.append(m_Slash);
        sb.append(y);
        sb.append(m_Slash);
        sb.append(x);
        sb.append(m_tileFileExtension);
        return sb.toString();
    }
    /**
     *  NT-Создать по координатам полный путь к файлу тилы для проверки-создания-чтения-записи файла.
     * @return
     */
    public String createFullTilePath(int x, int y, int z)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(m_sdCardPath);
        sb.append(m_tileRootFolder);
        sb.append(createTileCoordPath(x, y, z));
        return sb.toString();
    }

    /**
     * NT-Получить из хранилища тилу по координатному пути к ней
     * @param coordPath путь к тиле как z/y/x.jpg
     * @return Возвращает битмап или нуль
     */
    public Bitmap getTile(String coordPath)
    {
        Bitmap bmp = null;
        String pathName = null;
        try
        {
            pathName = getMapFolderPath() + coordPath;
            bmp = BitmapFactory.decodeFile(pathName);
        }
        catch(Exception e)
        {
            Log.d("LocalTileStore", "getTile - "+ e.toString());
        }
        return bmp;
    }


    /**
     * NT-Возвращает труе если тила по координатному пути существует в хранилище.
     * False если файл не найден. Исключение при файловой ошибке.
     * @param coordPath путь к тиле как z/x/y.jpg
     * @return
     */
    public boolean isTileExists(String coordPath)
    {
        String pathName = this.getMapFolderPath() + coordPath;

        File f = new File(pathName);

        return f.exists(); //TODO Проверить, как тут уничтожается объект
    }

    /**
     * NT-Get noMap tile from map directory
     * @return Возвращает картинку или нуль при ошибке
     */
    public Bitmap getNoMapTile()
    {
        return this.getTile(this.m_noMapTileName);
    }
    /**
     * NT-Получить картинку метки позиции гпс
     * @return Возвращает картинку или нуль при ошибке
     */
    public Bitmap getGpsLabelImage() {
        return this.loadBrowserImage(GpsLabelFileName);
    }
    /**
     * Получить картинку корпуса компаса
     * @return Возвращает картинку или нуль при ошибке
     */
    public Bitmap getCompassImage() {
        return this.loadBrowserImage(CompassFileName);
    }
    /**
     * Получить картинку красной стрелки компаса
     * @return Возвращает картинку или нуль при ошибке
     */
    public Bitmap getCompassRedArrowImage() {
        return this.loadBrowserImage(CompassRedArrowFileName);
    }

    /**
     * NT-Загрузить картинку из папки браузера
     * @param filename Имя файла картинки в папке браузера
     * @return Возвращает картинку или нуль при ошибке
     */
    public Bitmap loadBrowserImage(String filename) {

        Bitmap bmp = null;
        String pathName = null;
        try
        {
            pathName = this.getBrowserFolderPath() + filename;
            bmp = BitmapFactory.decodeFile(pathName);
        }
        catch(Exception e)
        {
            Log.d("LocalTileStore", "loadBrowserImage - "+ e.toString());
        }
        return bmp;
    }


    /**
     *  NT-Перевести позицию в градусах в позицию в пикселах тил
     * @param zAxis Масштаб, номер слоя тил
     * @param location Позиция на местности в градусах
     * @return Возвращает позицию в пикселах тил
     * Эта функция помещена здесь, так как она транслирует координаты в тилы.
     * Способ трансляции зависит от происхождения тил.
     * Для поддержки другого формата тил нужно переопределить эту функцию в производном классе.
     */
    public TileGridLocation LocationToTile(int zAxis, GeoLocation location)
    {
        TileGridLocation p = new TileGridLocation();
        double X, Y;
        X = (((location.X + 180.0) / 360.0) * (1 << zAxis));
        Y = ((1.0 - Math.log(Math.tan(location.Y * Math.PI / 180.0) +
                1.0 / Math.cos(location.Y * Math.PI / 180.0)) / Math.PI) / 2.0 * (1 << zAxis));
        //get tile size
        double t = (double) this.getTileSize();
        //convert results to tile grid coords
        p.X = Math.round(X * t);
        p.Y = Math.round(Y * t);

        return p;
    }

    /**
     *  NT-Перевести позицию в сетке тил в позицию в градусах
     * @param tilepos Позиция в сетке тил в пикселах
     * @param zAxis Масштаб, номер слоя тил
     * @return Возвращает позицию в градусах
     * Эта функция помещена здесь, так как она транслирует тилы в координаты.
     * Способ трансляции зависит от происхождения тил.
     * Для поддержки другого формата тил нужно переопределить эту функцию в производном классе.
     */
    public GeoLocation TileToLocation(TileGridLocation tilepos,	int zAxis)
    {
        GeoLocation p = new GeoLocation();
        //convert tile grid position to tiles as double
        double X, Y, t;
        t = (double) this.getTileSize();
        X = (double)tilepos.X / t;
        Y = (double)tilepos.Y / t;

        double n = Math.PI - ((2.0 * Math.PI * Y) / Math.pow(2.0, zAxis));

        p.X = (float)((X / Math.pow(2.0, zAxis) * 360.0) - 180.0);
        p.Y = (float)(180.0 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n))));

        return p;
    }

    /**
     *  NT-Try get sd card path. Return sd card path or return null if fail
     *  Это нетривиальная задача, поскольку в устройстве часто уже есть внешняя память
     *   - это часть общей флеш-памяти устройства, а не SD-карта, доступ к которой
     *    нельзя получить обычным способом.
     */
    private String tryGetSdCardPath()
    {
        File file = new File("/system/etc/vold.fstab");
        FileReader fr = null;
        BufferedReader br = null;
        String path = null;
        //открываем файл. Если открыть не удалось, ловим исключение.
        try
        {
            fr = new FileReader(file);
        }
        catch (FileNotFoundException e)
        {
            Log.d("tryGetSdCardPath", "SDCARD path 0 " + e.toString());
        }
        //если открыть удалось, то читаем файл построчно и ищем некоторую строку, парсим ее, извлекаем путь к карте.
        try
        {
            if (fr != null)
            {
                br = new BufferedReader(fr);
                String s = br.readLine();
                while (s != null)
                {
                    if (s.startsWith("dev_mount"))
                    {
                        String[] tokens = s.split("\\s");
                        path = tokens[2]; //mount_point
                        if (!Environment.getExternalStorageDirectory().getAbsolutePath().equals(path))
                        {
                            break;
                        }
                        else path = null; //сбросим значение, чтобы на выходе в строке не оказалось что-нибудь левое
                    }
                    s = br.readLine();
                }
            }
        }
        catch (IOException e)
        {
            Log.d("tryGetSdCardPath", "SDCARD path 1 " + e.toString());
        }
        finally
        {
            try
            {
                if (fr != null)
                {
                    fr.close();
                }
                if (br != null)
                {
                    br.close();
                }
            }
            catch (IOException e)
            {
                Log.d("tryGetSdCardPath", "SDCARD path 2 " + e.toString());
            }
        }
        return path;
    }







}
