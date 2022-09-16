package com.meraman.meramaps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

//SHORT OLD PLAN
//DONE: App skeleton
//DONE: Manifest skeleton
//DONE: Compass image drawing
//FAIL: Compass data get TODO: need develop compass data method
//DONE: Surface gestures skeleton
//DONE: Surface gestures callbacks transform to useful function calls
//DONE: Develop tile storage subsystem - основное все сделано
//DONE: Develop tile cache subsystem -
//DONE: Develop tile grid subsystem with variative screen sizes. TODO tile layout bug
//DONE: Develop scrolling for tile grid subsystem
//TODO: Develop distance and zoomlevel indicator

//TODO: Add screen messages about errors
//TODO: Move strings to resources
//TODO: Develop tile downloading subsystem

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private TileMapView mTileView;
    private SensorManager mSensorManager;
    private PowerManager mPowerManager;
    private WindowManager mWindowManager;
    private Display mDisplay;
    private PowerManager.WakeLock mWakeLock;

    /**
     * Номер масштаба карты - сохранять в настройках приложения
     */
    private int mZoomLevel;
    /**
     * Точка на карте, соответствующая центру экрана - сохранять в настройках приложения
     */
    private GeoLocation mScreenCenterLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Get an instance of the PowerManager
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

        // Get an instance of the WindowManager
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        // Create a bright wake lock
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass()
                .getName());
        //Create location point
        mScreenCenterLocation = new GeoLocation();

        //загрузить настройки приложения
        this.loadAppData();
        // instantiate our tile map view and set it as the activity's content
        try {
            mTileView = new TileMapView(this);
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mTileView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } catch (Exception e) {
            //Как тут показать пользователю сообщение о проблеме вызвавшей исключение?
            showMessageBox("Application exception", e.getClass().getName());
            e.printStackTrace();
        }
        setContentView(mTileView);

        //start application
        return;
    }

    /**
     * Show message box
     * @param title  Message box title string
     * @param message Message box message text
     */
    private void showMessageBox(String title, String message)
    {
        try
        {
            AlertDialog.Builder builder  = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);
            //dlgAlert.setIcon(icon); - если есть иконка, ее надо заранее загрузить в память, до исключения
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                }
            });
            AlertDialog dlg = builder.create();
            dlg.show();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return;
    }


    /**
     * Событие продолжения работы приложения
     */
    @Override
    protected void onResume() {
        super.onResume();
        /*
         * when the activity is resumed, we acquire a wake-lock so that the
         * screen stays on, since the user will likely not be fiddling with the
         * screen or buttons.
         */
        mWakeLock.acquire();

        // Start the sensors
        mTileView.startSensors();
    }
    /**
     * Событие приостановки работы приложения
     */
    @Override
    protected void onPause() {
        super.onPause();
        /*
         * When the activity is paused, we make sure to stop the simulation,
         * release our sensor resources and wake locks
         */

        // Stop the sensors
        mTileView.stopSensors();

        //store data in application setting file
        storeAppData();
        // and release our wake-lock
        mWakeLock.release();
    }

    /**
     * NR-Примерный набросок кода записи данных в файл настроек приложения
     */
    private void storeAppData()
    {
        //get default settings file for current activity
        SharedPreferences spref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        //write zoomlevel value
        editor.putInt("Zoom", this.mZoomLevel);
        String s;
        //write latlong position as string
        s = Double.toString(this.mScreenCenterLocation.getLongitude());
        editor.putString("Longitude", s);
        s = Double.toString(this.mScreenCenterLocation.getLatitude());
        editor.putString("Latitude", s);
        //write map type as map folder name or as map class name when it's unical.
        editor.putString("MapName", "map"); //not used now
        editor.commit();
        return;
    }
    /**
     * NR-Примерный набросок кода получения данных из настроек приложения
     */
    private void loadAppData() {
        //get default settings file for current activity
        SharedPreferences sp = this.getPreferences(Context.MODE_PRIVATE);
        //read zoomlevel value
        this.mZoomLevel = sp.getInt("Zoom", 3);
        //read latlong position
        String s;
        s = sp.getString("Longitude", "33");
        if (s == "") {
            //write default value to variable
            this.mScreenCenterLocation.X = 33.0;
        } else this.mScreenCenterLocation.X = Double.valueOf(s);
        //Latitude same as Longitude
        s = sp.getString("Latitude", "33");
        if (s == "") {
            //write default value to variable
            this.mScreenCenterLocation.Y = 33.0;
        } else this.mScreenCenterLocation.Y = Double.valueOf(s);
        //read map name and apply to code - not used now
//    	s = sp.getString("MapName", "map");
//    	if(s == "")
//    	{
//    		//write existing map name here
//    		s = "map";
//    	}
//    	//create map object here

        return;
    }


    //***************************************************************************************
    class TileMapView extends View implements SensorEventListener, LocationListener {

        //gestures
        /**
         * Gesture detector for clicks and scrolls
         */
        private GestureDetector m_gestures;
        /**
         * Gesture detector for zoom gestures
         */
        private ScaleGestureDetector m_scaleGesture;
        /**
         * Service flag, True if zoom currently performed. This prevent scrolls when user do pinch.
         */
        private boolean m_isZoomScrolling;// = false current scrolling is zoom or not
        /**
         * Scale factor for zoom
         */
        private float m_scaleFactor; //scale factor for zoom, initial value = 1.0

        //*** sensors ***********

        /*
         * Сенсор положения - работает криво
         */
        private Sensor mCompass;

        private float m_oldzAxis; //z value for compass event handler - not used now
        /**
         * Location manager provides GPS data
         */
        private LocationManager m_LocationManager;
        /**
         * Указатель направления - компас
         */
        private CompassToy m_CompassToy;
        /**
         * Указатель масштаба карты
         */
        private DistanceToy m_DistanceToy;

        /**********************************************************************/
//		/**
//		 * Сетка тил для отображения карты
//		 */
//		private TileGrid m_TileGrid;
        /**********************************************************************/
        /**
         * Кеш тил
         */
        private TileCache m_tileCache;

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
//		/**
//		 * Номер масштаба карты - moved to MainActivity
//		 */
//		private int m_zoomLevel;
        /**
         * Точка центра экрана на сетке тил
         */
        private TileGridLocation m_screenCenterTileGridPosition;

        /**
         * Координаты устройства от гпс - для отображения метки гпс
         */
        private GeoLocation m_GpsDeviceLocation;
        /**
         * Прямоугольник метки гпс
         */
        private Rect m_GpsLabelRectangle;
        /**
         * Экземпляр локального хранилища тил
         */
        private LocalTileStore m_LocalTileStore;
        /**
         * Флаг - окно просмотра должно следовать за позицией гпс.
         * Если флаг установлен, каждое событие от гпс смещает окно просмотра на новую позицию гпс. Так окно движется следом за гпс.
         * Если флаг сброшен, события гпс не смещают окно просмотра, оно остается на старом месте.
         * Флаг устанавливается командой меню.
         * Прокрутка или зум карты сбрасывают этот флаг.
         */
        private boolean m_WalkWithGpsFlag;
        //TODO Флаг следования за гпс добавлен в код везде где надо, теперь нужно только меню чтобы его включать.
        /**************************************************************************************/

        //constructor
        public TileMapView(Context context) throws Exception {
            super(context);
            //init variables

            this.m_scaleFactor = 1.0f;
            m_isZoomScrolling = false;
            //create sensors
            mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            m_LocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //init gestures
            m_scaleGesture = new ScaleGestureDetector(getContext(),
                    new ScaleListener());
            m_gestures = new GestureDetector(getContext(), new GestureListener());


            //создать объект локального хранилища для выбранного типа тил
            //сейчас для тестов и отладки выбираем мою первую карту, она полностью реализована в классе LocalTileStore
            //для остальных типов карт нужно переопределить некоторые переменные и функции в их классах
            m_LocalTileStore = new MySatLocalTileStore();//new OsmSatLocalTileStore(); //new OsmMapLocalTileStore();
            // инициализировать все экранные штуки
            this.m_CompassToy = new CompassToy(m_LocalTileStore);
            this.m_DistanceToy = new DistanceToy(m_LocalTileStore);
            //this.m_TileGrid = new TileGrid(m_LocalTileStore);

            //from TileGrid ctor
            //create tile cache object
            this.m_tileCache = new TileCache(m_LocalTileStore);
            //Создаем список для записей о тилах
            m_tileRecords = new ArrayList<TileRecord>();
            //инициализация всяких параметров. Некоторые из них должны где-то в настройках храниться, чтобы оттуда загружаться
            //но пока просто от балды назначим
            this.m_ScreenHeight = 240;
            this.m_ScreenWidth = 320;
            //this.m_zoomLevel = 3; загружаются в MainActivity из настроек или дефолтовое значение
            this.m_GpsLabelRectangle = new Rect();
            //TODO Load data from app settings
            this.m_GpsDeviceLocation = new GeoLocation(33, 33);
            //m_screenCenterLocation = m_GpsDeviceLocation; - загружаются в MainActivity из настроек или дефолтовое значение
            this.m_screenCenterTileGridPosition = new TileGridLocation();
            this.TileGrid_GoToLocation(mScreenCenterLocation); // init internal grid state
            //noMap тилу храним в m_TileCache, а используем ее здесь. Она соответствует типу карты и хранится вместе с тилами этой карты.
            //end TileGrid ctor
            //Флаг следования за позицией ГПС
            //после старта false, не следовать, иначе нет смысла в запоминании последней позиции гпс.
            this.m_WalkWithGpsFlag = false;
            //start event-driven work
            this.startSensors();
            //show screen toys
            this.m_CompassToy.Show(true);
            this.m_DistanceToy.Update(mZoomLevel);//update by zoom level
            this.m_DistanceToy.Show(true);

        }


        //start GPS and Accelerometer sensors
        public void startSensors() throws SecurityException {
            /*
             * It is not necessary to get accelerometer events at a very high
             * rate, by using a slower rate (SENSOR_DELAY_UI), we get an
             * automatic low-pass filter, which "extracts" the gravity component
             * of the acceleration. As an added benefit, we use less power and
             * CPU resources.
             */
            mSensorManager.registerListener(this, mCompass, SensorManager.SENSOR_DELAY_UI);
            //start GPS device
            m_LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
            //TODO: тут надо бы попросить пользователя разрешить приложению доступ к гпс
        }

        //stop GPS and Accelerometer sensors
        public void stopSensors() {
            mSensorManager.unregisterListener(this);
            //stop GPS device
            m_LocationManager.removeUpdates(this);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            // compute the origin of the screen relative to the origin of
            // the bitmap
            Log.d("onSizeChanged", "W="+ String.valueOf(w) + " H=" + String.valueOf(h));
            //get screen orientation
            //TODO Определить, нужна ли тут ориентация экрана - ведь координаты дисплея сами переворачиваются соответственно, значит, ширины и высоты экрана вполне достаточно.
            //Ориентация пригодится для отрисовки компаса
            int orientation = mDisplay.getRotation();
            //call toy handlers for updating
            this.m_CompassToy.ScreenChanged(w, h, orientation);
            this.m_DistanceToy.ScreenChanged(w, h, orientation);
            this.TileGrid_ScreenChanged(w, h, orientation);

            return;
        }


        //**************************************************************************
        //  GPS event listener callbacks
        //*************************************************************************
        @Override
        public void onLocationChanged(Location arg0) {
            //направление использовано для отображения компаса, пока датчики положения не применены.
            this.m_CompassToy.updateCompass(arg0.getBearing());
            //все данные от гпс оптом передаем, там разберемся
            this.TileGrid_updateLocation(arg0);
            //dump direction for compass
            Log.d("onLocationChanged", "Dir="+ String.valueOf(arg0.getBearing()));
            invalidate();//перерисовать экран
        }


        @Override
        public void onProviderDisabled(String provider) {
            //Auto-generated method stub
            Toast.makeText(getBaseContext(), "Gps turned OFF", Toast.LENGTH_LONG).show();
        }


        @Override
        public void onProviderEnabled(String provider) {
            //Auto-generated method stub
            Toast.makeText(getBaseContext(), "Gps turned ON", Toast.LENGTH_LONG).show();
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Auto-generated method stub
            //not used here
        }

        //*********************************************************************************
// SensorEventListener interface functions
//*********************************************************************************
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ORIENTATION)
                return;
////        	//TODO: sensor system must be developed later and separately. Current sensor data look invalid.
//            //Log.d("onSensorChanged", " Z=" + String.valueOf(event.values[0]) + " X=" + String.valueOf(event.values[1]) + " Y=" + String.valueOf(event.values[2]));
//            float zAxis = event.values[0];
//            if((m_oldzAxis > (zAxis + 5.0f)) || (m_oldzAxis < (zAxis - 5.0f)))
//            {
//            	m_oldzAxis = zAxis;//store new value
//            	//call to recreate compass image
//            	this.m_CompassToy.updateCompass(360.0f - zAxis);
//            	//update view for compass
//            	this.invalidate();
//            }
            return;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // not used in application
        }

        //***************************************************************************************
//		TOUCH PROCESSING
//**************************************************************************************
        //touch event callback
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            //process touch
            m_scaleGesture.onTouchEvent(event);
            m_gestures.onTouchEvent(event);
            return true;
        }

        //my own scale listener class
        public class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if(detector.isInProgress())
                {
                    m_scaleFactor *= detector.getScaleFactor();
                    //convert scale factor to tile zoom level
                    //zoom in check
                    if(m_scaleFactor > 1.5f) //2x zoom as event
                    {
                        m_scaleFactor = 1.00f;
                        //increment tile zoom level
                        TileZoomIn(detector.getFocusX(), detector.getFocusY());
                    }
                    //zoom out check
                    else if(m_scaleFactor < 0.75f) // 1/2x zoom as event
                    {
                        m_scaleFactor = 1.00f;
                        //decrement tile zoom level
                        TileZoomOut(detector.getFocusX(), detector.getFocusY());
                    }
                }
                //out to log channel
                // Log.d("ScaleListener", "InProgress" + String.valueOf(detector.isInProgress()) + " ScaleFactor=" + String.valueOf(detector.getScaleFactor()) + " FocusX=" + String.valueOf(detector.getFocusX()) + " FocusY=" + String.valueOf(detector.getFocusY()));

                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                //zoom mode enable
                m_isZoomScrolling = true;
                //Log.d("ScaleBegin", "InProgress" + String.valueOf(detector.isInProgress()) + " ScaleFactor=" + String.valueOf(detector.getScaleFactor()) + " FocusX=" + String.valueOf(detector.getFocusX()) + " FocusY=" + String.valueOf(detector.getFocusY()));
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                //zoom mode disable
                m_isZoomScrolling = false;
                //Log.d("ScaleEnd", "InProgress" + String.valueOf(detector.isInProgress()) + " ScaleFactor=" + String.valueOf(detector.getScaleFactor()) + " FocusX=" + String.valueOf(detector.getFocusX()) + " FocusY=" + String.valueOf(detector.getFocusY()));
            }
        }

        //**************************************************************************************
        public class GestureListener implements GestureDetector.OnGestureListener,
                GestureDetector.OnDoubleTapListener {

            @Override
            public boolean onDown(MotionEvent e) {
                //not used in application
                //Log.d("onDown", e.toString());
                return false; //event not consumed
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                if(!m_isZoomScrolling)
                {
                    //tile scroll
                    TileScroll(distanceX, distanceY);
                    //Log.d("onScroll", "e1="+ e1.toString() + " e2=" + e2.toString() + " X="+String.valueOf(distanceX) + " Y="+ String.valueOf(distanceY));
                }
                return true;//event consumed
            }

            @Override
            public boolean onDoubleTap(MotionEvent arg0) {
                // not used in this application
                //Log.d("onDoubleTap", "arg="+arg0.toString());
                return false;//not consumed
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent arg0) {
                // not used in this application
                //Log.d("onDoubleTapEvent", "arg="+arg0.toString());
                return false;//not consumed
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent arg0) {
                //use this as click for buttons
                //Log.d("onSingleTapConf", "arg="+arg0.toString());
                //call event for next processing
                TileMapView.this.onSingleClick(arg0.getX(), arg0.getY());
                return true;//consumed event
            }

            @Override
            public boolean onFling(MotionEvent arg0, MotionEvent arg1,
                                   float arg2, float arg3) {
                //Fling not used in application
                return false;//not consumed
            }

            @Override
            public void onLongPress(MotionEvent arg0) {
                //Длинный клик должен использоваться для контекстного меню, оно вызывается инфраструктурой
                //так что тут ничего не надо вызывать.
                //TileMapView.this.onLongPress(arg0.getX(), arg0.getY());
                //Log.d("onLongPress", "arg="+arg0.toString());
            }

            @Override
            public void onShowPress(MotionEvent arg0) {
                //not used in application
                //Log.d("onShowPress", "arg="+arg0.toString());
            }

            @Override
            public boolean onSingleTapUp(MotionEvent arg0) {
                //not used in application
                //Log.d("onSingleTapUp", "arg="+arg0.toString());
                return false;//not consumed
            }
        }
        //***************************************************************************
//      click scroll zoom  refine event functions
//***************************************************************************
        public void onLongPress(float X, float Y)
        {
            //TODO Long press обрабатывается фреймворком для вызова контекстного меню

        }

        public void onSingleClick(float X, float Y)
        {
            //TODO Add single click event code here

        }

        public void TileZoomOut(float focusX, float focusY)
        {
            //Log.d("ZoomOut", "X="+String.valueOf(focusX)+" Y=" + String.valueOf(focusY));
            this.TileGrid_Zoom(-1, (int) focusX, (int)focusY);
            this.m_DistanceToy.Update(mZoomLevel);
            //update screen
            invalidate();
        }

        public void TileZoomIn(float focusX, float focusY)
        {
            this.TileGrid_Zoom(1, (int) focusX, (int)focusY);
            this.m_DistanceToy.Update(mZoomLevel);
            //Log.d("ZoomIn", "X="+String.valueOf(focusX)+" Y=" + String.valueOf(focusY));
            //update screen
            invalidate();
        }

        public void TileScroll(float distanceX, float distanceY)
        {
            //Log.d("TileScroll", "X=" + String.valueOf(distanceX) + " Y="+ String.valueOf(distanceY));
            this.TileGrid_Scroll((int) distanceX, (int) distanceY);
            //update screen
            invalidate();
        }

//*************************************************************************
// TileGrid functions
//*************************************************************************
        /**
         * NR-Отрисовать объект на указанной поверхности
         * @param canvas
         */
        public void TileGrid_Draw(Canvas canvas)
        {
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
        public void TileGrid_ScreenChanged(int width, int height, int orientation) {
            //orientation can be any of
            //Surface.ROTATION_0;
            //Surface.ROTATION_90;
            //Surface.ROTATION_180;
            //Surface.ROTATION_270;
            this.m_ScreenWidth = width;
            this.m_ScreenHeight = height;
            //пересчитать сетку тил и перерисовать экран
            this.TileGrid_updateView();

            return;
        }
        /**
         *  NR-Обновить местоположение устройства по ГПС.
         * @param arg0 Объект с данными от ГПС.
         */
        public void TileGrid_updateLocation(Location arg0)
        {
            //сохраним позицию в переменных как текущую
            this.m_GpsDeviceLocation.X = arg0.getLongitude();
            this.m_GpsDeviceLocation.Y = arg0.getLatitude();
            // TODO добавить код обработки события гпс по режимам
            //TODO в зависимости от режима, тут надо либо обновить представление метки гпс, либо полностью обновить все.
            if(this.m_WalkWithGpsFlag == true)
            {
                //окно просмотра следует за гпс
                this.TileGrid_GoToLocation(this.m_GpsDeviceLocation);
            }
            else //окно просмотра остается на старом месте, гпс отрисовывается на новом месте
                this.TileGrid_updateView();

            return;
        }
        /**
         * NT-Пользователь прокручивает вид
         * @param dX
         * @param dY
         */
        public void TileGrid_Scroll(int dX, int dY)
        {
            //сбросить флаг следования за гпс
            this.m_WalkWithGpsFlag = false;
            //сместить Т1 на dx dy
            this.m_screenCenterTileGridPosition = this.m_screenCenterTileGridPosition.Offset(dX, dY);
            //вычислить Т2 по Т1
            mScreenCenterLocation = this.m_LocalTileStore.TileToLocation(this.m_screenCenterTileGridPosition, mZoomLevel);
            //обновить сетку тил
            this.TileGrid_updateView();

            return;
        }
        /**
         * NT-Пользователь желает изменить масштаб карты в некоторой точке экрана.
         * Эта точка должна остаться на прежнем месте, а все остальные позиции изменятся.
         * @param dZ  - -1 уменьшает зум, +1 увеличивает зум
         * @param Xpos - координата точки зума
         * @param Ypos - координата точки зума
         */
        public void TileGrid_Zoom (int dZ, int Xpos, int Ypos)
        {
            //сбросить флаг следования за гпс
            this.m_WalkWithGpsFlag = false;
            //вычислить смещение точки зума на экране от точки середины экрана
            int dx = Xpos - (this.m_ScreenWidth / 2);
            int dy = Ypos - (this.m_ScreenHeight / 2);
            //получить координаты гпс экранной точки зума
            GeoLocation geoloc = this.TileGrid_ScreenToLocation(Xpos, Ypos);
            //изменить зум (в пределах 0..20)
            mZoomLevel += dZ;
            if(mZoomLevel < 0) mZoomLevel = 0;
            else if(mZoomLevel > 20) mZoomLevel = 20;
            //по координатам гпс получить координаты точки зума на сетке тил
            TileGridLocation tgloc = this.m_LocalTileStore.LocationToTile(mZoomLevel, geoloc);
            //координаты точки зума вычесть смещение, полученную позицию записать как новый центр экрана на сетке тил Т1
            this.m_screenCenterTileGridPosition = tgloc.Offset(-dx, -dy);
            //по Т1 вычислить новую Т2 и записать ее
            mScreenCenterLocation = this.m_LocalTileStore.TileToLocation(this.m_screenCenterTileGridPosition, mZoomLevel);
            //обновить вид
            TileGrid_updateView();

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
        private void TileGrid_updateGrid(TileGridLocation center )
        {
            //clear tile records list
            this.m_tileRecords.clear();
            //get tile actual size
            int tileSize = this.m_LocalTileStore.getTileSize();
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
                    tileZ = mZoomLevel;
                    //set tile path-key
                    trec.Key = this.m_LocalTileStore.createTileCoordPath(tileX, tileY, tileZ);
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
         * @param longval - позиция на сетке тил
         * @param tilesize - размер тилы
         * @return номер тилы, которой принадлежит позиция на сетке тил
         */
        private int TileGrid_RoundSpec(long longval, int tilesize)
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
        private GeoLocation TileGrid_ScreenToLocation(int x, int y)
        {
            //Вычислить смещение до середины экрана
            int dx = x - this.m_ScreenWidth / 2;
            int dy = y - this.m_ScreenHeight / 2;
            //вычислить позицию на сетке тил
            TileGridLocation loc = this.m_screenCenterTileGridPosition.Offset(dx, dy);
            //вычислить позицию на гпс
            GeoLocation result = this.m_LocalTileStore.TileToLocation(loc, mZoomLevel);
            //вернуть позицию
            return result;
        }
        /**
         * NT-Вычислить координаты на экране по координатам гпс
         * @param latlong координаты гпс
         * @return Координаты на экране
         */
        private ScreenLocation TileGrid_LocationToScreen(GeoLocation latlong)
        {
            //вычислить точку на сетке тил
            TileGridLocation tgloc = this.m_LocalTileStore.LocationToTile(mZoomLevel, latlong);
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
        public void TileGrid_GoToLocation(GeoLocation latlong)
        {
            //Т2 = аргумент
            mScreenCenterLocation = new GeoLocation(latlong);
            //вычислить Т1 по Т2
            this.m_screenCenterTileGridPosition = this.m_LocalTileStore.LocationToTile(mZoomLevel, latlong);
            //обновить вид
            this.TileGrid_updateView();
            return;
        }
        /**
         * NT-Установить прямоугольник для рисования метки позиции гпс
         */
        public void TileGrid_updateGpsRect()
        {
            //Получить экранные координаты точки гпс по координатам гпс
            ScreenLocation loc = this.TileGrid_LocationToScreen(this.m_GpsDeviceLocation);
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
        private void TileGrid_updateView()
        {
            //update tile grid
            this.TileGrid_updateGrid(this.m_screenCenterTileGridPosition); //TODO remove argument after full test
            this.m_tileCache.updateTileCache(this.m_tileRecords);//update cache
            //update gps label
            this.TileGrid_updateGpsRect();
            //TODO Add other updates here
            return;
        }



//*************************************************************************
// drawing functions
//*************************************************************************

        @Override
        protected void onDraw(Canvas canvas) {

            /*
             * draw the background
             */
            //Log.d("onDraw", "W=" + String.valueOf(canvas.getWidth()) + " H=" +String.valueOf(canvas.getHeight()));
            //хотя можно не заливать экран цветом - все равно там тилы будут... Но пока зальем для наглядности
            //TODO Использовать для отладки, закомментировать после тестов
            canvas.drawRGB(128, 128, 128);

            //draw tile grid
            this.TileGrid_Draw(canvas);
            //draw compass toy
            this.m_CompassToy.Draw(canvas);
            //draw distance toy
            this.m_DistanceToy.Draw(canvas);

            return;
        }




    } //end TileMapView class



}
