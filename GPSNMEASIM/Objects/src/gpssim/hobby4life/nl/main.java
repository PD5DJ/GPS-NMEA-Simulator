package gpssim.hobby4life.nl;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "gpssim.hobby4life.nl", "gpssim.hobby4life.nl.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "gpssim.hobby4life.nl", "gpssim.hobby4life.nl.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "gpssim.hobby4life.nl.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static int _version = 0;
public static String _app_label = "";
public static String _app_ver = "";
public static String _app_author = "";
public static String _app_email = "";
public static String _app_website = "";
public static anywheresoftware.b4a.objects.Serial.BluetoothAdmin _admin = null;
public static anywheresoftware.b4a.objects.collections.List _founddevices = null;
public static gpssim.hobby4life.nl.main._nameandmac _connecteddevice = null;
public static anywheresoftware.b4a.objects.Serial _serial1 = null;
public static anywheresoftware.b4a.objects.Timer _timer1 = null;
public static anywheresoftware.b4a.objects.Timer _timer2 = null;
public static anywheresoftware.b4a.objects.Timer _timer3 = null;
public static boolean _connected = false;
public static boolean _streaming = false;
public static boolean _imperial = false;
public static derez.libs.Navigation _nav = null;
public static anywheresoftware.b4a.objects.RuntimePermissions _rp = null;
public static String _fileini = "";
public anywheresoftware.b4a.objects.SpinnerWrapper _interval_spin = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _unit_spin = null;
public anywheresoftware.b4a.objects.ButtonWrapper _exit_button = null;
public anywheresoftware.b4a.objects.ButtonWrapper _info_button = null;
public anywheresoftware.b4a.objects.ButtonWrapper _connect_button = null;
public anywheresoftware.b4a.objects.ButtonWrapper _disconnect_button = null;
public anywheresoftware.b4a.objects.ButtonWrapper _search_button = null;
public anywheresoftware.b4a.objects.ButtonWrapper _discover_button = null;
public anywheresoftware.b4a.objects.ButtonWrapper _stop = null;
public anywheresoftware.b4a.objects.ButtonWrapper _reset_latlong_button = null;
public anywheresoftware.b4a.objects.ButtonWrapper _start = null;
public anywheresoftware.b4a.objects.LabelWrapper _status = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _ns_spin = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _ew_spin = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _satfix_spin = null;
public anywheresoftware.b4a.objects.EditTextWrapper _lattitude_input = null;
public static double _lattitude = 0;
public static String _nmea_lat = "";
public static String _lattitude_hems = "";
public anywheresoftware.b4a.objects.EditTextWrapper _longitude_input = null;
public static double _longitude = 0;
public static String _nmea_lon = "";
public static String _longitude_hems = "";
public anywheresoftware.b4a.objects.SeekBarWrapper _bearing_bar = null;
public anywheresoftware.b4a.objects.SeekBarWrapper _speed_bar = null;
public anywheresoftware.b4a.objects.SeekBarWrapper _altitude_bar = null;
public anywheresoftware.b4a.objects.SeekBarWrapper _sattelite_bar = null;
public anywheresoftware.b4a.objects.SeekBarWrapper _winddirection_bar = null;
public anywheresoftware.b4a.objects.LabelWrapper _circle_text = null;
public static double _circle_value = 0;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _circle_check = null;
public anywheresoftware.b4a.objects.ButtonWrapper _circleinc_button = null;
public anywheresoftware.b4a.objects.ButtonWrapper _circledecr_button = null;
public anywheresoftware.b4a.objects.LabelWrapper _altitude_text = null;
public static double _altitude_value = 0;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _altitude_check = null;
public anywheresoftware.b4a.objects.ButtonWrapper _altitudeinc_button = null;
public anywheresoftware.b4a.objects.ButtonWrapper _altitudedecr_button = null;
public anywheresoftware.b4a.objects.LabelWrapper _windspeed_text = null;
public static int _windspeed_value = 0;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _wind_check = null;
public anywheresoftware.b4a.objects.ButtonWrapper _windspeedinc_button = null;
public anywheresoftware.b4a.objects.ButtonWrapper _windspeeddecr_button = null;
public anywheresoftware.b4a.objects.LabelWrapper _altitude_input = null;
public static int _altitude = 0;
public static String _nmea_alt = "";
public anywheresoftware.b4a.objects.LabelWrapper _bearing_input = null;
public static int _bearing = 0;
public static String _nmea_bearing = "";
public anywheresoftware.b4a.objects.LabelWrapper _speed_input = null;
public static int _speed = 0;
public static String _nmea_speed = "";
public anywheresoftware.b4a.objects.LabelWrapper _winddirection_input = null;
public anywheresoftware.b4a.objects.LabelWrapper _satview_input = null;
public anywheresoftware.b4a.objects.TabStripViewPager _tabstrip1 = null;
public static String _gprmc = "";
public static String _gpgga = "";
public anywheresoftware.b4a.phone.Phone.PhoneWakeState _pws = null;
public anywheresoftware.b4a.objects.MediaPlayerWrapper _welcome_snd = null;
public anywheresoftware.b4a.objects.MediaPlayerWrapper _stopstream_snd = null;
public anywheresoftware.b4a.objects.MediaPlayerWrapper _startstream_snd = null;
public anywheresoftware.b4a.objects.MediaPlayerWrapper _connected_snd = null;
public anywheresoftware.b4a.objects.MediaPlayerWrapper _disconnected_snd = null;
public anywheresoftware.b4a.objects.MediaPlayerWrapper _reset_snd = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp_small = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp_mid = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp_big = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp_connect = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp_disconnect = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp_exit = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp_search = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp_discover = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp_warning = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _indicator = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _connect_image = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _disconnect_image = null;
public static long _now = 0L;
public static String _message = "";
public static int _result = 0;
public static int _interval = 0;
public static int _speed_interval = 0;
public anywheresoftware.b4a.objects.streams.File.TextWriterWrapper _textwriter1 = null;
public static String _nmea_satview = "";
public static byte _nmea_satfix = (byte)0;
public static double _distance = 0;
public static double _default_lattitude = 0;
public static double _default_longitude = 0;
public anywheresoftware.b4a.objects.LabelWrapper _interval_spin_lbl = null;
public anywheresoftware.b4a.objects.LabelWrapper _unit_spin_lbl = null;
public anywheresoftware.b4a.objects.LabelWrapper _lattitude_input_lbl = null;
public anywheresoftware.b4a.objects.LabelWrapper _ns_spin_lbl = null;
public anywheresoftware.b4a.objects.LabelWrapper _longitude_input_lbl = null;
public anywheresoftware.b4a.objects.LabelWrapper _ew_spin_lbl = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static class _nameandmac{
public boolean IsInitialized;
public String Name;
public String Mac;
public void Initialize() {
IsInitialized = true;
Name = "";
Mac = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 119;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 121;BA.debugLine="Activity.LoadLayout(\"InitScreen\")";
mostCurrent._activity.LoadLayout("InitScreen",mostCurrent.activityBA);
 //BA.debugLineNum = 122;BA.debugLine="TabStrip1.LoadLayout(\"Page1\",\"Connect\")						' Ad";
mostCurrent._tabstrip1.LoadLayout("Page1",BA.ObjectToCharSequence("Connect"));
 //BA.debugLineNum = 123;BA.debugLine="TabStrip1.LoadLayout(\"Page2\",\"Input 1\")						' Ad";
mostCurrent._tabstrip1.LoadLayout("Page2",BA.ObjectToCharSequence("Input 1"));
 //BA.debugLineNum = 124;BA.debugLine="TabStrip1.LoadLayout(\"Page3\",\"Input 2\")						' Ad";
mostCurrent._tabstrip1.LoadLayout("Page3",BA.ObjectToCharSequence("Input 2"));
 //BA.debugLineNum = 125;BA.debugLine="TabStrip1.LoadLayout(\"Page4\",\"Input 3\")";
mostCurrent._tabstrip1.LoadLayout("Page4",BA.ObjectToCharSequence("Input 3"));
 //BA.debugLineNum = 127;BA.debugLine="Activity.Title = \"GPS NMEA Simulator, \" & app_ver";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("GPS NMEA Simulator, "+_app_ver));
 //BA.debugLineNum = 128;BA.debugLine="bmp_mid.Initialize(File.DirAssets,\"saticon3.png\")";
mostCurrent._bmp_mid.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"saticon3.png");
 //BA.debugLineNum = 129;BA.debugLine="bmp_small.Initialize(File.DirAssets,\"gpsdroid_sma";
mostCurrent._bmp_small.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"gpsdroid_small.png");
 //BA.debugLineNum = 130;BA.debugLine="bmp_exit.Initialize(File.DirAssets,\"exit2.png\")";
mostCurrent._bmp_exit.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"exit2.png");
 //BA.debugLineNum = 131;BA.debugLine="bmp_warning.Initialize(File.DirAssets,\"warning.pn";
mostCurrent._bmp_warning.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"warning.png");
 //BA.debugLineNum = 133;BA.debugLine="Activity.AddMenuItem2(\"About\",\"MenuAbout\",bmp_mid";
mostCurrent._activity.AddMenuItem2(BA.ObjectToCharSequence("About"),"MenuAbout",(android.graphics.Bitmap)(mostCurrent._bmp_mid.getObject()));
 //BA.debugLineNum = 134;BA.debugLine="Activity.AddMenuItem2(\"Exit\",\"MenuExit\",bmp_exit)";
mostCurrent._activity.AddMenuItem2(BA.ObjectToCharSequence("Exit"),"MenuExit",(android.graphics.Bitmap)(mostCurrent._bmp_exit.getObject()));
 //BA.debugLineNum = 136;BA.debugLine="NS_Spin.AddAll(Array As String(\"North\", \"South\"))";
mostCurrent._ns_spin.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"North","South"}));
 //BA.debugLineNum = 137;BA.debugLine="EW_Spin.AddAll(Array As String(\"East\",\"West\"))";
mostCurrent._ew_spin.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"East","West"}));
 //BA.debugLineNum = 138;BA.debugLine="Unit_Spin.AddAll(Array As String(\"Metric\",\"Imperi";
mostCurrent._unit_spin.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"Metric","Imperial"}));
 //BA.debugLineNum = 139;BA.debugLine="Interval_Spin.AddAll(Array As String(\"100mS (10Hz";
mostCurrent._interval_spin.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"100mS (10Hz)","250mS (4Hz)","500mS (2Hz)","1000mS (1Hz)"}));
 //BA.debugLineNum = 140;BA.debugLine="SatFix_Spin.AddAll(Array As String(\"No Fix\",\"2D G";
mostCurrent._satfix_spin.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"No Fix","2D GPS FIX","3D DGPS FIX"}));
 //BA.debugLineNum = 142;BA.debugLine="Lattitude_Input.Text = Default_Lattitude";
mostCurrent._lattitude_input.setText(BA.ObjectToCharSequence(_default_lattitude));
 //BA.debugLineNum = 143;BA.debugLine="Longitude_Input.Text = Default_Longitude";
mostCurrent._longitude_input.setText(BA.ObjectToCharSequence(_default_longitude));
 //BA.debugLineNum = 145;BA.debugLine="StartStream_snd.Initialize";
mostCurrent._startstream_snd.Initialize();
 //BA.debugLineNum = 146;BA.debugLine="StopStream_snd.initialize";
mostCurrent._stopstream_snd.Initialize();
 //BA.debugLineNum = 147;BA.debugLine="Welcome_snd.Initialize";
mostCurrent._welcome_snd.Initialize();
 //BA.debugLineNum = 148;BA.debugLine="Connected_snd.Initialize";
mostCurrent._connected_snd.Initialize();
 //BA.debugLineNum = 149;BA.debugLine="Disconnected_snd.Initialize";
mostCurrent._disconnected_snd.Initialize();
 //BA.debugLineNum = 150;BA.debugLine="Reset_snd.Initialize";
mostCurrent._reset_snd.Initialize();
 //BA.debugLineNum = 151;BA.debugLine="Welcome_snd.Load(File.DirAssets, \"welcome.mp3\")";
mostCurrent._welcome_snd.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"welcome.mp3");
 //BA.debugLineNum = 152;BA.debugLine="Connected_snd.Load(File.DirAssets, \"connected.mp3";
mostCurrent._connected_snd.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"connected.mp3");
 //BA.debugLineNum = 153;BA.debugLine="Disconnected_snd.Load(File.DirAssets, \"disconnect";
mostCurrent._disconnected_snd.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"disconnected.mp3");
 //BA.debugLineNum = 154;BA.debugLine="StopStream_snd.Load(File.DirAssets, \"stopstream.m";
mostCurrent._stopstream_snd.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"stopstream.mp3");
 //BA.debugLineNum = 155;BA.debugLine="StartStream_snd.Load(File.DirAssets, \"startstream";
mostCurrent._startstream_snd.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"startstream.mp3");
 //BA.debugLineNum = 156;BA.debugLine="Reset_snd.Load(File.DirAssets, \"reset.mp3\")";
mostCurrent._reset_snd.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"reset.mp3");
 //BA.debugLineNum = 157;BA.debugLine="Speed = Speed_Bar.Value";
_speed = mostCurrent._speed_bar.getValue();
 //BA.debugLineNum = 158;BA.debugLine="Bearing = Bearing_Bar.Value";
_bearing = mostCurrent._bearing_bar.getValue();
 //BA.debugLineNum = 159;BA.debugLine="Altitude = Altitude_Bar.Value";
_altitude = mostCurrent._altitude_bar.getValue();
 //BA.debugLineNum = 160;BA.debugLine="Status_update";
_status_update();
 //BA.debugLineNum = 161;BA.debugLine="Indicator.Visible = False";
mostCurrent._indicator.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 162;BA.debugLine="Start.Enabled = False";
mostCurrent._start.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 163;BA.debugLine="Stop.enabled = False";
mostCurrent._stop.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 164;BA.debugLine="Disable_Items";
_disable_items();
 //BA.debugLineNum = 165;BA.debugLine="Streaming = False";
_streaming = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 166;BA.debugLine="Connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 168;BA.debugLine="SatFix_Spin.SelectedIndex = 2";
mostCurrent._satfix_spin.setSelectedIndex((int) (2));
 //BA.debugLineNum = 169;BA.debugLine="WindDirection_Bar.Enabled = False";
mostCurrent._winddirection_bar.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 171;BA.debugLine="LoadINI";
_loadini();
 //BA.debugLineNum = 173;BA.debugLine="Unit_Update(False)";
_unit_update(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 176;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 177;BA.debugLine="Admin.Initialize(\"admin\")";
_admin.Initialize(processBA,"admin");
 //BA.debugLineNum = 178;BA.debugLine="Serial1.Initialize(\"serial1\")";
_serial1.Initialize("serial1");
 //BA.debugLineNum = 179;BA.debugLine="Status.Text = \"Waiting for incoming connection\"";
mostCurrent._status.setText(BA.ObjectToCharSequence("Waiting for incoming connection"));
 //BA.debugLineNum = 180;BA.debugLine="Timer1.Initialize(\"Timer1\", Interval)";
_timer1.Initialize(processBA,"Timer1",(long) (_interval));
 //BA.debugLineNum = 181;BA.debugLine="Timer2.Initialize(\"Timer2\", 100)";
_timer2.Initialize(processBA,"Timer2",(long) (100));
 //BA.debugLineNum = 182;BA.debugLine="Timer3.Initialize(\"Timer3\", 1000)";
_timer3.Initialize(processBA,"Timer3",(long) (1000));
 //BA.debugLineNum = 183;BA.debugLine="Timer3.Enabled = True";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 184;BA.debugLine="Welcome_snd.Play";
mostCurrent._welcome_snd.Play();
 };
 //BA.debugLineNum = 187;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 291;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 292;BA.debugLine="If UserClosed = True Then";
if (_userclosed==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 293;BA.debugLine="Serial1.Disconnect";
_serial1.Disconnect();
 //BA.debugLineNum = 294;BA.debugLine="PWS.ReleaseKeepAlive";
mostCurrent._pws.ReleaseKeepAlive();
 };
 //BA.debugLineNum = 296;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 189;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 190;BA.debugLine="If Admin.IsEnabled = False Then";
if (_admin.IsEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 191;BA.debugLine="If Admin.Enable = False Then";
if (_admin.Enable()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 192;BA.debugLine="ToastMessageShow(\"Error enabling Bluetooth adap";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error enabling Bluetooth adapter."),anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 194;BA.debugLine="ToastMessageShow(\"Enabling Bluetooth adapter...";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Enabling Bluetooth adapter..."),anywheresoftware.b4a.keywords.Common.False);
 };
 }else {
 };
 //BA.debugLineNum = 200;BA.debugLine="LoadINI";
_loadini();
 //BA.debugLineNum = 201;BA.debugLine="End Sub";
return "";
}
public static String  _admin_devicefound(String _name,String _macaddress) throws Exception{
gpssim.hobby4life.nl.main._nameandmac _nm = null;
 //BA.debugLineNum = 334;BA.debugLine="Sub Admin_DeviceFound (Name As String, MacAddress";
 //BA.debugLineNum = 335;BA.debugLine="Log(Name & \":\" & MacAddress)";
anywheresoftware.b4a.keywords.Common.LogImpl("21048577",_name+":"+_macaddress,0);
 //BA.debugLineNum = 336;BA.debugLine="Dim nm As NameAndMac";
_nm = new gpssim.hobby4life.nl.main._nameandmac();
 //BA.debugLineNum = 337;BA.debugLine="nm.Name = Name";
_nm.Name /*String*/  = _name;
 //BA.debugLineNum = 338;BA.debugLine="nm.Mac = MacAddress";
_nm.Mac /*String*/  = _macaddress;
 //BA.debugLineNum = 339;BA.debugLine="FoundDevices.Add(nm)";
_founddevices.Add((Object)(_nm));
 //BA.debugLineNum = 340;BA.debugLine="ProgressDialogShow(\"Searching for devices (~ devi";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Searching for devices (~ device found)...".replace("~",BA.NumberToString(_founddevices.getSize()))));
 //BA.debugLineNum = 341;BA.debugLine="End Sub";
return "";
}
public static String  _admin_discoveryfinished() throws Exception{
anywheresoftware.b4a.objects.collections.List _l = null;
int _i = 0;
gpssim.hobby4life.nl.main._nameandmac _nm = null;
int _res = 0;
 //BA.debugLineNum = 311;BA.debugLine="Sub Admin_DiscoveryFinished";
 //BA.debugLineNum = 312;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 313;BA.debugLine="If FoundDevices.Size = 0 Then";
if (_founddevices.getSize()==0) { 
 //BA.debugLineNum = 314;BA.debugLine="ToastMessageShow(\"No device found.\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No device found."),anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 316;BA.debugLine="Dim l As List";
_l = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 317;BA.debugLine="l.Initialize";
_l.Initialize();
 //BA.debugLineNum = 318;BA.debugLine="For i = 0 To FoundDevices.Size - 1";
{
final int step7 = 1;
final int limit7 = (int) (_founddevices.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit7 ;_i = _i + step7 ) {
 //BA.debugLineNum = 319;BA.debugLine="Dim nm As NameAndMac";
_nm = new gpssim.hobby4life.nl.main._nameandmac();
 //BA.debugLineNum = 320;BA.debugLine="nm = FoundDevices.Get(i)";
_nm = (gpssim.hobby4life.nl.main._nameandmac)(_founddevices.Get(_i));
 //BA.debugLineNum = 321;BA.debugLine="l.Add(nm.Name)";
_l.Add((Object)(_nm.Name /*String*/ ));
 }
};
 //BA.debugLineNum = 323;BA.debugLine="Dim res As Int";
_res = 0;
 //BA.debugLineNum = 324;BA.debugLine="res = InputList(l, \"Choose device to connect\", -";
_res = anywheresoftware.b4a.keywords.Common.InputList(_l,BA.ObjectToCharSequence("Choose device to connect"),(int) (-1),mostCurrent.activityBA);
 //BA.debugLineNum = 325;BA.debugLine="If res <> DialogResponse.CANCEL Then";
if (_res!=anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
 //BA.debugLineNum = 326;BA.debugLine="connectedDevice = FoundDevices.Get(res)";
_connecteddevice = (gpssim.hobby4life.nl.main._nameandmac)(_founddevices.Get(_res));
 //BA.debugLineNum = 327;BA.debugLine="ProgressDialogShow(\"Trying to connect to: \" & c";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Trying to connect to: "+_connecteddevice.Name /*String*/ +" ("+_connecteddevice.Mac /*String*/ +")"));
 //BA.debugLineNum = 328;BA.debugLine="Serial1.Connect(connectedDevice.Mac)";
_serial1.Connect(processBA,_connecteddevice.Mac /*String*/ );
 };
 };
 //BA.debugLineNum = 332;BA.debugLine="End Sub";
return "";
}
public static String  _admin_statechanged(int _newstate,int _oldstate) throws Exception{
 //BA.debugLineNum = 286;BA.debugLine="Sub Admin_StateChanged (NewState As Int, OldState";
 //BA.debugLineNum = 289;BA.debugLine="End Sub";
return "";
}
public static String  _altitude_bar_valuechanged(int _value,boolean _userchanged) throws Exception{
 //BA.debugLineNum = 963;BA.debugLine="Sub Altitude_Bar_ValueChanged (Value As Int, UserC";
 //BA.debugLineNum = 964;BA.debugLine="Altitude = Value";
_altitude = _value;
 //BA.debugLineNum = 965;BA.debugLine="Status_update";
_status_update();
 //BA.debugLineNum = 966;BA.debugLine="End Sub";
return "";
}
public static String  _altitudedecr_button_click() throws Exception{
 //BA.debugLineNum = 818;BA.debugLine="Sub AltitudeDecr_Button_Click";
 //BA.debugLineNum = 819;BA.debugLine="If Altitude_value > -25 Then";
if (_altitude_value>-25) { 
 //BA.debugLineNum = 820;BA.debugLine="Altitude_value = Altitude_value - 1";
_altitude_value = _altitude_value-1;
 //BA.debugLineNum = 821;BA.debugLine="If Imperial = False Then";
if (_imperial==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 822;BA.debugLine="Altitude_Text.Text = Altitude_value & \" m/s\"";
mostCurrent._altitude_text.setText(BA.ObjectToCharSequence(BA.NumberToString(_altitude_value)+" m/s"));
 }else if(_imperial==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 824;BA.debugLine="Altitude_Text.Text = Altitude_value & \" ft/s\"";
mostCurrent._altitude_text.setText(BA.ObjectToCharSequence(BA.NumberToString(_altitude_value)+" ft/s"));
 };
 };
 //BA.debugLineNum = 827;BA.debugLine="End Sub";
return "";
}
public static String  _altitudeinc_button_click() throws Exception{
 //BA.debugLineNum = 807;BA.debugLine="Sub AltitudeInc_Button_Click";
 //BA.debugLineNum = 808;BA.debugLine="If Altitude_value < 25 Then";
if (_altitude_value<25) { 
 //BA.debugLineNum = 809;BA.debugLine="Altitude_value = Altitude_value + 1";
_altitude_value = _altitude_value+1;
 //BA.debugLineNum = 810;BA.debugLine="If Imperial = False Then";
if (_imperial==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 811;BA.debugLine="Altitude_Text.Text = Altitude_value & \" m/s\"";
mostCurrent._altitude_text.setText(BA.ObjectToCharSequence(BA.NumberToString(_altitude_value)+" m/s"));
 }else if(_imperial==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 813;BA.debugLine="Altitude_Text.Text = Altitude_value & \" ft/s\"";
mostCurrent._altitude_text.setText(BA.ObjectToCharSequence(BA.NumberToString(_altitude_value)+" ft/s"));
 };
 };
 //BA.debugLineNum = 816;BA.debugLine="End Sub";
return "";
}
public static String  _bearing_bar_valuechanged(int _value,boolean _userchanged) throws Exception{
 //BA.debugLineNum = 954;BA.debugLine="Sub Bearing_Bar_ValueChanged (Value As Int, UserCh";
 //BA.debugLineNum = 955;BA.debugLine="Bearing = Value";
_bearing = _value;
 //BA.debugLineNum = 956;BA.debugLine="Status_update";
_status_update();
 //BA.debugLineNum = 958;BA.debugLine="End Sub";
return "";
}
public static String  _calc_nmea() throws Exception{
String _gps_string = "";
double[] _nav_dbl = null;
String _time = "";
String _date = "";
 //BA.debugLineNum = 562;BA.debugLine="Sub Calc_NMEA";
 //BA.debugLineNum = 564;BA.debugLine="Status_update";
_status_update();
 //BA.debugLineNum = 566;BA.debugLine="Dim GPS_String As String";
_gps_string = "";
 //BA.debugLineNum = 568;BA.debugLine="Dim Nav_dbl() As Double";
_nav_dbl = new double[(int) (0)];
;
 //BA.debugLineNum = 571;BA.debugLine="Dim time As String";
_time = "";
 //BA.debugLineNum = 572;BA.debugLine="Dim Date As String";
_date = "";
 //BA.debugLineNum = 574;BA.debugLine="Now = DateTime.Now";
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 575;BA.debugLine="Date = DateTime.Date(Now).SubString2(0,6)";
_date = anywheresoftware.b4a.keywords.Common.DateTime.Date(_now).substring((int) (0),(int) (6));
 //BA.debugLineNum = 576;BA.debugLine="time = DateTime.Date(Now).SubString2(6,12) & \".00";
_time = anywheresoftware.b4a.keywords.Common.DateTime.Date(_now).substring((int) (6),(int) (12))+".000";
 //BA.debugLineNum = 578;BA.debugLine="If Imperial = True Then";
if (_imperial==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 579;BA.debugLine="NMEA_Speed = Speed * 0.868976242 'convert miles";
mostCurrent._nmea_speed = BA.NumberToString(_speed*0.868976242);
 //BA.debugLineNum = 580;BA.debugLine="Distance = (1.609344 * Speed) / Speed_Interval '";
_distance = (1.609344*_speed)/(double)_speed_interval;
 }else if(_imperial==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 582;BA.debugLine="NMEA_Speed = Speed * 0.539956803 'convert kilome";
mostCurrent._nmea_speed = BA.NumberToString(_speed*0.539956803);
 //BA.debugLineNum = 583;BA.debugLine="Distance = Speed / Speed_Interval 'Timer = 500mS";
_distance = _speed/(double)_speed_interval;
 };
 //BA.debugLineNum = 586;BA.debugLine="If NMEA_Speed.Length > 6 Then NMEA_Speed = NMEA_S";
if (mostCurrent._nmea_speed.length()>6) { 
mostCurrent._nmea_speed = mostCurrent._nmea_speed.substring((int) (0),(int) (6));};
 //BA.debugLineNum = 588;BA.debugLine="If Lattitude >= 0.00 Then";
if (_lattitude>=0.00) { 
 //BA.debugLineNum = 589;BA.debugLine="NS_Spin.SelectedIndex = 0";
mostCurrent._ns_spin.setSelectedIndex((int) (0));
 //BA.debugLineNum = 590;BA.debugLine="Lattitude_hems = \"N\"";
mostCurrent._lattitude_hems = "N";
 }else if(_lattitude<=-0.00) { 
 //BA.debugLineNum = 592;BA.debugLine="NS_Spin.SelectedIndex = 1";
mostCurrent._ns_spin.setSelectedIndex((int) (1));
 //BA.debugLineNum = 593;BA.debugLine="Lattitude_hems = \"S\"";
mostCurrent._lattitude_hems = "S";
 };
 //BA.debugLineNum = 596;BA.debugLine="If Longitude >= 0.00 Then";
if (_longitude>=0.00) { 
 //BA.debugLineNum = 597;BA.debugLine="EW_Spin.SelectedIndex = 0";
mostCurrent._ew_spin.setSelectedIndex((int) (0));
 //BA.debugLineNum = 598;BA.debugLine="Longitude_hems = \"E\"";
mostCurrent._longitude_hems = "E";
 }else if(_longitude<=-0.00) { 
 //BA.debugLineNum = 600;BA.debugLine="EW_Spin.SelectedIndex = 1";
mostCurrent._ew_spin.setSelectedIndex((int) (1));
 //BA.debugLineNum = 601;BA.debugLine="Longitude_hems = \"W\"";
mostCurrent._longitude_hems = "W";
 };
 //BA.debugLineNum = 604;BA.debugLine="Nav_dbl = Nav.GeoNextPoint(Lattitude,Longitude,Di";
_nav_dbl = _nav.GeoNextPoint(_lattitude,_longitude,_distance,_bearing);
 //BA.debugLineNum = 608;BA.debugLine="Lattitude = Nav_dbl(0)";
_lattitude = _nav_dbl[(int) (0)];
 //BA.debugLineNum = 609;BA.debugLine="Longitude = Nav_dbl(1)";
_longitude = _nav_dbl[(int) (1)];
 //BA.debugLineNum = 612;BA.debugLine="Lattitude_Input.Text = Abs(Lattitude)";
mostCurrent._lattitude_input.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Abs(_lattitude)));
 //BA.debugLineNum = 613;BA.debugLine="If Lattitude_Input.Text.Length > 8 Then Lattitude";
if (mostCurrent._lattitude_input.getText().length()>8) { 
mostCurrent._lattitude_input.setText(BA.ObjectToCharSequence(mostCurrent._lattitude_input.getText().substring((int) (0),(int) (8))));};
 //BA.debugLineNum = 615;BA.debugLine="Longitude_Input.Text = Abs(Longitude)";
mostCurrent._longitude_input.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Abs(_longitude)));
 //BA.debugLineNum = 616;BA.debugLine="If Longitude_Input.Text.length > 8 Then Longitude";
if (mostCurrent._longitude_input.getText().length()>8) { 
mostCurrent._longitude_input.setText(BA.ObjectToCharSequence(mostCurrent._longitude_input.getText().substring((int) (0),(int) (8))));};
 //BA.debugLineNum = 619;BA.debugLine="NMEA_Lat = DECtoDMS(Abs(Lattitude))";
mostCurrent._nmea_lat = BA.NumberToString(_dectodms((float) (anywheresoftware.b4a.keywords.Common.Abs(_lattitude))));
 //BA.debugLineNum = 620;BA.debugLine="NMEA_Lon = DECtoDMS(Abs(Longitude))";
mostCurrent._nmea_lon = BA.NumberToString(_dectodms((float) (anywheresoftware.b4a.keywords.Common.Abs(_longitude))));
 //BA.debugLineNum = 622;BA.debugLine="NMEA_Lat = NMEA_Lat & \"00000\"";
mostCurrent._nmea_lat = mostCurrent._nmea_lat+"00000";
 //BA.debugLineNum = 623;BA.debugLine="NMEA_Lon = NMEA_Lon & \"00000\"";
mostCurrent._nmea_lon = mostCurrent._nmea_lon+"00000";
 //BA.debugLineNum = 625;BA.debugLine="If DECtoDMS(Lattitude) < 10 Then";
if (_dectodms((float) (_lattitude))<10) { 
 //BA.debugLineNum = 626;BA.debugLine="NMEA_Lat = \"0\" & NMEA_Lat.SubString2(0,1) & NMEA";
mostCurrent._nmea_lat = "0"+mostCurrent._nmea_lat.substring((int) (0),(int) (1))+mostCurrent._nmea_lat.substring((int) (2),(int) (4))+"."+mostCurrent._nmea_lat.substring((int) (4),(int) (8));
 }else if(_dectodms((float) (_lattitude))>10) { 
 //BA.debugLineNum = 628;BA.debugLine="NMEA_Lat = NMEA_Lat.SubString2(0,2) & NMEA_Lat.";
mostCurrent._nmea_lat = mostCurrent._nmea_lat.substring((int) (0),(int) (2))+mostCurrent._nmea_lat.substring((int) (3),(int) (5))+"."+mostCurrent._nmea_lat.substring((int) (5),(int) (9));
 };
 //BA.debugLineNum = 631;BA.debugLine="If DECtoDMS(Longitude) < 10 Then";
if (_dectodms((float) (_longitude))<10) { 
 //BA.debugLineNum = 632;BA.debugLine="NMEA_Lon = \"00\" & NMEA_Lon.SubString2(0,1) & NME";
mostCurrent._nmea_lon = "00"+mostCurrent._nmea_lon.substring((int) (0),(int) (1))+mostCurrent._nmea_lon.substring((int) (2),(int) (4))+"."+mostCurrent._nmea_lon.substring((int) (4),(int) (8));
 }else if(_dectodms((float) (_longitude))<100) { 
 //BA.debugLineNum = 634;BA.debugLine="NMEA_Lon = NMEA_Lon.SubString2(0,2) & NMEA_Lon.";
mostCurrent._nmea_lon = mostCurrent._nmea_lon.substring((int) (0),(int) (2))+mostCurrent._nmea_lon.substring((int) (3),(int) (5))+"."+mostCurrent._nmea_lon.substring((int) (5),(int) (9));
 }else if(_dectodms((float) (_longitude))>100) { 
 //BA.debugLineNum = 636;BA.debugLine="NMEA_Lon = NMEA_Lon.SubString2(0,3) & NMEA_Lon";
mostCurrent._nmea_lon = mostCurrent._nmea_lon.substring((int) (0),(int) (3))+mostCurrent._nmea_lon.substring((int) (4),(int) (6))+"."+mostCurrent._nmea_lon.substring((int) (6),(int) (10));
 };
 //BA.debugLineNum = 642;BA.debugLine="GPS_String = \"$GPRMC,\"";
_gps_string = "$GPRMC,";
 //BA.debugLineNum = 643;BA.debugLine="GPS_String = GPS_String & time & \",A,\"";
_gps_string = _gps_string+_time+",A,";
 //BA.debugLineNum = 644;BA.debugLine="GPS_String = GPS_String & NMEA_Lat & \",\" & Lattit";
_gps_string = _gps_string+mostCurrent._nmea_lat+","+mostCurrent._lattitude_hems+",";
 //BA.debugLineNum = 645;BA.debugLine="GPS_String = GPS_String & NMEA_Lon & \",\" & Longit";
_gps_string = _gps_string+mostCurrent._nmea_lon+","+mostCurrent._longitude_hems+",";
 //BA.debugLineNum = 646;BA.debugLine="GPS_String = GPS_String & NMEA_Speed &\",\"& NMEA_B";
_gps_string = _gps_string+mostCurrent._nmea_speed+","+mostCurrent._nmea_bearing+".0,"+_date+",,*";
 //BA.debugLineNum = 647;BA.debugLine="GPRMC = GPS_String & NMEA_Checksum(GPS_String)";
mostCurrent._gprmc = _gps_string+_nmea_checksum(_gps_string);
 //BA.debugLineNum = 649;BA.debugLine="GPS_String = \"$GPGGA,\"";
_gps_string = "$GPGGA,";
 //BA.debugLineNum = 650;BA.debugLine="GPS_String = GPS_String & time & \",\"";
_gps_string = _gps_string+_time+",";
 //BA.debugLineNum = 651;BA.debugLine="GPS_String = GPS_String & NMEA_Lat & \",\" & Lattit";
_gps_string = _gps_string+mostCurrent._nmea_lat+","+mostCurrent._lattitude_hems+",";
 //BA.debugLineNum = 652;BA.debugLine="GPS_String = GPS_String & NMEA_Lon & \",\" & Longit";
_gps_string = _gps_string+mostCurrent._nmea_lon+","+mostCurrent._longitude_hems+",";
 //BA.debugLineNum = 653;BA.debugLine="GPS_String = GPS_String & NMEA_SatFix & \",\" & NME";
_gps_string = _gps_string+BA.NumberToString(_nmea_satfix)+","+mostCurrent._nmea_satview+",4.4,"+mostCurrent._nmea_alt+".0,M,48.0,M,,*";
 //BA.debugLineNum = 654;BA.debugLine="GPGGA = GPS_String & NMEA_Checksum(GPS_String)";
mostCurrent._gpgga = _gps_string+_nmea_checksum(_gps_string);
 //BA.debugLineNum = 665;BA.debugLine="End Sub";
return "";
}
public static String  _circledecr_button_click() throws Exception{
 //BA.debugLineNum = 857;BA.debugLine="Sub CircleDecr_Button_Click";
 //BA.debugLineNum = 858;BA.debugLine="If Circle_Value > -25 Then";
if (_circle_value>-25) { 
 //BA.debugLineNum = 859;BA.debugLine="Circle_Value = Circle_Value - 1";
_circle_value = _circle_value-1;
 //BA.debugLineNum = 860;BA.debugLine="Circle_Text.Text = Circle_Value & \" °/s\"";
mostCurrent._circle_text.setText(BA.ObjectToCharSequence(BA.NumberToString(_circle_value)+" °/s"));
 };
 //BA.debugLineNum = 862;BA.debugLine="End Sub";
return "";
}
public static String  _circleinc_button_click() throws Exception{
 //BA.debugLineNum = 851;BA.debugLine="Sub CircleInc_Button_Click";
 //BA.debugLineNum = 852;BA.debugLine="If Circle_Value < 25 Then";
if (_circle_value<25) { 
 //BA.debugLineNum = 853;BA.debugLine="Circle_Value = Circle_Value + 1";
_circle_value = _circle_value+1;
 //BA.debugLineNum = 854;BA.debugLine="Circle_Text.Text = Circle_Value  & \" °/s\"";
mostCurrent._circle_text.setText(BA.ObjectToCharSequence(BA.NumberToString(_circle_value)+" °/s"));
 };
 //BA.debugLineNum = 856;BA.debugLine="End Sub";
return "";
}
public static String  _connect_button_click() throws Exception{
anywheresoftware.b4a.objects.collections.Map _paireddevices = null;
anywheresoftware.b4a.objects.collections.List _l = null;
int _i = 0;
int _res = 0;
 //BA.debugLineNum = 422;BA.debugLine="Sub Connect_Button_Click";
 //BA.debugLineNum = 423;BA.debugLine="If Serial1.IsEnabled = True Then";
if (_serial1.IsEnabled()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 424;BA.debugLine="Status.Text = \"Connecting...\"";
mostCurrent._status.setText(BA.ObjectToCharSequence("Connecting..."));
 //BA.debugLineNum = 425;BA.debugLine="Dim PairedDevices As Map";
_paireddevices = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 426;BA.debugLine="PairedDevices = Serial1.GetPairedDevices";
_paireddevices = _serial1.GetPairedDevices();
 //BA.debugLineNum = 427;BA.debugLine="Dim l As List";
_l = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 428;BA.debugLine="l.Initialize";
_l.Initialize();
 //BA.debugLineNum = 429;BA.debugLine="For i = 0 To PairedDevices.Size - 1";
{
final int step7 = 1;
final int limit7 = (int) (_paireddevices.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit7 ;_i = _i + step7 ) {
 //BA.debugLineNum = 430;BA.debugLine="l.Add(PairedDevices.GetKeyAt(i)) 'add the";
_l.Add(_paireddevices.GetKeyAt(_i));
 }
};
 //BA.debugLineNum = 432;BA.debugLine="Dim res As Int";
_res = 0;
 //BA.debugLineNum = 433;BA.debugLine="res = InputList(l, \"Choose device\", -1) 'show";
_res = anywheresoftware.b4a.keywords.Common.InputList(_l,BA.ObjectToCharSequence("Choose device"),(int) (-1),mostCurrent.activityBA);
 //BA.debugLineNum = 434;BA.debugLine="If res <> DialogResponse.CANCEL Then";
if (_res!=anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
 //BA.debugLineNum = 435;BA.debugLine="Serial1.Connect(PairedDevices.Get(l.Get(r";
_serial1.Connect(processBA,BA.ObjectToString(_paireddevices.Get(_l.Get(_res))));
 };
 };
 //BA.debugLineNum = 439;BA.debugLine="End Sub";
return "";
}
public static float  _dectodms(float _dec) throws Exception{
int _degrees = 0;
double _minutes = 0;
 //BA.debugLineNum = 681;BA.debugLine="Sub DECtoDMS (DEC As Float) As Float";
 //BA.debugLineNum = 683;BA.debugLine="Dim Degrees As Int";
_degrees = 0;
 //BA.debugLineNum = 684;BA.debugLine="Dim Minutes As Double";
_minutes = 0;
 //BA.debugLineNum = 686;BA.debugLine="Degrees = (DEC)";
_degrees = (int) ((_dec));
 //BA.debugLineNum = 687;BA.debugLine="Minutes = ((DEC - Degrees) * 60) / 100";
_minutes = ((_dec-_degrees)*60)/(double)100;
 //BA.debugLineNum = 689;BA.debugLine="Return Degrees + Minutes";
if (true) return (float) (_degrees+_minutes);
 //BA.debugLineNum = 691;BA.debugLine="End Sub";
return 0f;
}
public static String  _disable_items() throws Exception{
 //BA.debugLineNum = 947;BA.debugLine="Sub Disable_Items";
 //BA.debugLineNum = 953;BA.debugLine="End Sub";
return "";
}
public static String  _disconnect_button_click() throws Exception{
 //BA.debugLineNum = 441;BA.debugLine="Sub Disconnect_Button_Click";
 //BA.debugLineNum = 442;BA.debugLine="If Connected = True Then";
if (_connected==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 443;BA.debugLine="Connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 444;BA.debugLine="Streaming = False";
_streaming = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 445;BA.debugLine="Serial1.Disconnect";
_serial1.Disconnect();
 //BA.debugLineNum = 446;BA.debugLine="ToastMessageShow(\"Disconnected\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Disconnected"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 447;BA.debugLine="Status.Text = \"Waiting for incoming connection\"";
mostCurrent._status.setText(BA.ObjectToCharSequence("Waiting for incoming connection"));
 //BA.debugLineNum = 448;BA.debugLine="Start.Enabled = False";
mostCurrent._start.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 449;BA.debugLine="Stop.Enabled = False";
mostCurrent._stop.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 450;BA.debugLine="Disable_Items";
_disable_items();
 //BA.debugLineNum = 451;BA.debugLine="Disconnected_snd.Play";
mostCurrent._disconnected_snd.Play();
 };
 //BA.debugLineNum = 454;BA.debugLine="End Sub";
return "";
}
public static String  _discover_button_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 343;BA.debugLine="Sub Discover_Button_Click";
 //BA.debugLineNum = 344;BA.debugLine="If Connected = False Then";
if (_connected==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 346;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 347;BA.debugLine="i.Initialize(\"android.bluetooth.adapter.action.R";
_i.Initialize("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE","");
 //BA.debugLineNum = 348;BA.debugLine="i.PutExtra(\"android.bluetooth.adapter.extra.DISC";
_i.PutExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION",(Object)(300));
 //BA.debugLineNum = 349;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_i.getObject()));
 //BA.debugLineNum = 350;BA.debugLine="Serial1.Listen";
_serial1.Listen(processBA);
 };
 //BA.debugLineNum = 352;BA.debugLine="End Sub";
return "";
}
public static String  _enable_items() throws Exception{
 //BA.debugLineNum = 940;BA.debugLine="Sub Enable_Items";
 //BA.debugLineNum = 941;BA.debugLine="Bearing_Bar.Enabled = True";
mostCurrent._bearing_bar.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 942;BA.debugLine="Speed_Bar.Enabled = True";
mostCurrent._speed_bar.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 943;BA.debugLine="Altitude_Bar.Enabled = True";
mostCurrent._altitude_bar.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 944;BA.debugLine="Sattelite_Bar.Enabled = True";
mostCurrent._sattelite_bar.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 945;BA.debugLine="End Sub";
return "";
}
public static String  _exit_app() throws Exception{
 //BA.debugLineNum = 491;BA.debugLine="Sub Exit_App";
 //BA.debugLineNum = 492;BA.debugLine="PWS.ReleaseKeepAlive";
mostCurrent._pws.ReleaseKeepAlive();
 //BA.debugLineNum = 493;BA.debugLine="Streaming = False";
_streaming = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 494;BA.debugLine="Connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 495;BA.debugLine="Serial1.Disconnect";
_serial1.Disconnect();
 //BA.debugLineNum = 496;BA.debugLine="Disconnected_snd.Play";
mostCurrent._disconnected_snd.Play();
 //BA.debugLineNum = 498;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 //BA.debugLineNum = 499;BA.debugLine="End Sub";
return "";
}
public static String  _exit_button_click() throws Exception{
 //BA.debugLineNum = 487;BA.debugLine="Sub Exit_Button_Click";
 //BA.debugLineNum = 488;BA.debugLine="Exit_App";
_exit_app();
 //BA.debugLineNum = 489;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 46;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 52;BA.debugLine="Dim Interval_Spin As Spinner";
mostCurrent._interval_spin = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 53;BA.debugLine="Dim Unit_Spin As Spinner";
mostCurrent._unit_spin = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 55;BA.debugLine="Dim Exit_Button As Button";
mostCurrent._exit_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Dim Info_Button As Button";
mostCurrent._info_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 57;BA.debugLine="Dim Connect_Button As Button";
mostCurrent._connect_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Dim Disconnect_Button As Button";
mostCurrent._disconnect_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 59;BA.debugLine="Dim Search_Button As Button";
mostCurrent._search_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Dim Discover_Button As Button : Dim Stop As Butto";
mostCurrent._discover_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Dim Discover_Button As Button : Dim Stop As Butto";
mostCurrent._stop = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 61;BA.debugLine="Dim Reset_LatLong_Button As Button";
mostCurrent._reset_latlong_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 62;BA.debugLine="Dim Start As Button";
mostCurrent._start = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 63;BA.debugLine="Dim Status As Label";
mostCurrent._status = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 65;BA.debugLine="Dim NS_Spin As Spinner";
mostCurrent._ns_spin = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 66;BA.debugLine="Dim EW_Spin As Spinner";
mostCurrent._ew_spin = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 67;BA.debugLine="Dim SatFix_Spin As Spinner";
mostCurrent._satfix_spin = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 68;BA.debugLine="Dim Lattitude_Input As EditText : Dim Lattitude A";
mostCurrent._lattitude_input = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 68;BA.debugLine="Dim Lattitude_Input As EditText : Dim Lattitude A";
_lattitude = 0;
 //BA.debugLineNum = 68;BA.debugLine="Dim Lattitude_Input As EditText : Dim Lattitude A";
mostCurrent._nmea_lat = "";
 //BA.debugLineNum = 69;BA.debugLine="Dim Lattitude_hems As String : Lattitude_hems = \"";
mostCurrent._lattitude_hems = "";
 //BA.debugLineNum = 69;BA.debugLine="Dim Lattitude_hems As String : Lattitude_hems = \"";
mostCurrent._lattitude_hems = "N";
 //BA.debugLineNum = 70;BA.debugLine="Dim Longitude_Input As EditText : Dim Longitude A";
mostCurrent._longitude_input = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 70;BA.debugLine="Dim Longitude_Input As EditText : Dim Longitude A";
_longitude = 0;
 //BA.debugLineNum = 70;BA.debugLine="Dim Longitude_Input As EditText : Dim Longitude A";
mostCurrent._nmea_lon = "";
 //BA.debugLineNum = 71;BA.debugLine="Dim Longitude_hems As String : Longitude_hems = \"";
mostCurrent._longitude_hems = "";
 //BA.debugLineNum = 71;BA.debugLine="Dim Longitude_hems As String : Longitude_hems = \"";
mostCurrent._longitude_hems = "E";
 //BA.debugLineNum = 73;BA.debugLine="Dim Bearing_Bar As SeekBar : Dim Speed_Bar As See";
mostCurrent._bearing_bar = new anywheresoftware.b4a.objects.SeekBarWrapper();
 //BA.debugLineNum = 73;BA.debugLine="Dim Bearing_Bar As SeekBar : Dim Speed_Bar As See";
mostCurrent._speed_bar = new anywheresoftware.b4a.objects.SeekBarWrapper();
 //BA.debugLineNum = 73;BA.debugLine="Dim Bearing_Bar As SeekBar : Dim Speed_Bar As See";
mostCurrent._altitude_bar = new anywheresoftware.b4a.objects.SeekBarWrapper();
 //BA.debugLineNum = 73;BA.debugLine="Dim Bearing_Bar As SeekBar : Dim Speed_Bar As See";
mostCurrent._sattelite_bar = new anywheresoftware.b4a.objects.SeekBarWrapper();
 //BA.debugLineNum = 73;BA.debugLine="Dim Bearing_Bar As SeekBar : Dim Speed_Bar As See";
mostCurrent._winddirection_bar = new anywheresoftware.b4a.objects.SeekBarWrapper();
 //BA.debugLineNum = 74;BA.debugLine="Dim Circle_Text As Label : Dim Circle_Value As Do";
mostCurrent._circle_text = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 74;BA.debugLine="Dim Circle_Text As Label : Dim Circle_Value As Do";
_circle_value = 0;
 //BA.debugLineNum = 74;BA.debugLine="Dim Circle_Text As Label : Dim Circle_Value As Do";
mostCurrent._circle_check = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 74;BA.debugLine="Dim Circle_Text As Label : Dim Circle_Value As Do";
mostCurrent._circleinc_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 74;BA.debugLine="Dim Circle_Text As Label : Dim Circle_Value As Do";
mostCurrent._circledecr_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Dim Altitude_Text As Label : Dim Altitude_value A";
mostCurrent._altitude_text = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Dim Altitude_Text As Label : Dim Altitude_value A";
_altitude_value = 0;
 //BA.debugLineNum = 75;BA.debugLine="Dim Altitude_Text As Label : Dim Altitude_value A";
mostCurrent._altitude_check = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Dim Altitude_Text As Label : Dim Altitude_value A";
mostCurrent._altitudeinc_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Dim Altitude_Text As Label : Dim Altitude_value A";
mostCurrent._altitudedecr_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 76;BA.debugLine="Dim WindSpeed_Text As Label : Dim WindSpeed_Value";
mostCurrent._windspeed_text = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 76;BA.debugLine="Dim WindSpeed_Text As Label : Dim WindSpeed_Value";
_windspeed_value = 0;
 //BA.debugLineNum = 76;BA.debugLine="Dim WindSpeed_Text As Label : Dim WindSpeed_Value";
mostCurrent._wind_check = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 76;BA.debugLine="Dim WindSpeed_Text As Label : Dim WindSpeed_Value";
mostCurrent._windspeedinc_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 76;BA.debugLine="Dim WindSpeed_Text As Label : Dim WindSpeed_Value";
mostCurrent._windspeeddecr_button = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 77;BA.debugLine="Dim Altitude_Input As Label : Dim Altitude_value";
mostCurrent._altitude_input = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 77;BA.debugLine="Dim Altitude_Input As Label : Dim Altitude_value";
_altitude_value = 0;
 //BA.debugLineNum = 77;BA.debugLine="Dim Altitude_Input As Label : Dim Altitude_value";
_altitude = 0;
 //BA.debugLineNum = 77;BA.debugLine="Dim Altitude_Input As Label : Dim Altitude_value";
mostCurrent._nmea_alt = "";
 //BA.debugLineNum = 78;BA.debugLine="Dim Bearing_Input As Label : Dim Bearing As Int :";
mostCurrent._bearing_input = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 78;BA.debugLine="Dim Bearing_Input As Label : Dim Bearing As Int :";
_bearing = 0;
 //BA.debugLineNum = 78;BA.debugLine="Dim Bearing_Input As Label : Dim Bearing As Int :";
mostCurrent._nmea_bearing = "";
 //BA.debugLineNum = 79;BA.debugLine="Dim Speed_Input As Label : Dim Speed As Int : Dim";
mostCurrent._speed_input = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 79;BA.debugLine="Dim Speed_Input As Label : Dim Speed As Int : Dim";
_speed = 0;
 //BA.debugLineNum = 79;BA.debugLine="Dim Speed_Input As Label : Dim Speed As Int : Dim";
mostCurrent._nmea_speed = "";
 //BA.debugLineNum = 80;BA.debugLine="Dim WindDirection_Input As Label";
mostCurrent._winddirection_input = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 82;BA.debugLine="Dim SatView_Input As Label";
mostCurrent._satview_input = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 84;BA.debugLine="Private TabStrip1 As TabStrip";
mostCurrent._tabstrip1 = new anywheresoftware.b4a.objects.TabStripViewPager();
 //BA.debugLineNum = 85;BA.debugLine="Dim GPRMC As String : Dim GPGGA As String";
mostCurrent._gprmc = "";
 //BA.debugLineNum = 85;BA.debugLine="Dim GPRMC As String : Dim GPGGA As String";
mostCurrent._gpgga = "";
 //BA.debugLineNum = 86;BA.debugLine="Dim PWS As PhoneWakeState";
mostCurrent._pws = new anywheresoftware.b4a.phone.Phone.PhoneWakeState();
 //BA.debugLineNum = 87;BA.debugLine="Dim Welcome_snd As MediaPlayer";
mostCurrent._welcome_snd = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 88;BA.debugLine="Dim StopStream_snd As MediaPlayer : Dim StartStre";
mostCurrent._stopstream_snd = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 88;BA.debugLine="Dim StopStream_snd As MediaPlayer : Dim StartStre";
mostCurrent._startstream_snd = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 88;BA.debugLine="Dim StopStream_snd As MediaPlayer : Dim StartStre";
mostCurrent._connected_snd = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 88;BA.debugLine="Dim StopStream_snd As MediaPlayer : Dim StartStre";
mostCurrent._disconnected_snd = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 88;BA.debugLine="Dim StopStream_snd As MediaPlayer : Dim StartStre";
mostCurrent._reset_snd = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 92;BA.debugLine="Dim bmp_small,bmp_mid, bmp_big, bmp_connect, bmp_";
mostCurrent._bmp_small = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
mostCurrent._bmp_mid = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
mostCurrent._bmp_big = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
mostCurrent._bmp_connect = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
mostCurrent._bmp_disconnect = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
mostCurrent._bmp_exit = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
mostCurrent._bmp_search = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
mostCurrent._bmp_discover = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
mostCurrent._bmp_warning = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 93;BA.debugLine="Dim Indicator As ImageView";
mostCurrent._indicator = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 94;BA.debugLine="Dim Connect_image As ImageView : Dim Disconnect_I";
mostCurrent._connect_image = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 94;BA.debugLine="Dim Connect_image As ImageView : Dim Disconnect_I";
mostCurrent._disconnect_image = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 94;BA.debugLine="Dim Connect_image As ImageView : Dim Disconnect_I";
mostCurrent._indicator = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 95;BA.debugLine="Dim Now As Long";
_now = 0L;
 //BA.debugLineNum = 96;BA.debugLine="DateTime.DateFormat = \"ddMMyyHHmmss\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("ddMMyyHHmmss");
 //BA.debugLineNum = 97;BA.debugLine="Dim Message As String";
mostCurrent._message = "";
 //BA.debugLineNum = 98;BA.debugLine="Dim Result As Int";
_result = 0;
 //BA.debugLineNum = 99;BA.debugLine="Dim Interval As Int";
_interval = 0;
 //BA.debugLineNum = 100;BA.debugLine="Dim Speed_Interval As Int";
_speed_interval = 0;
 //BA.debugLineNum = 102;BA.debugLine="Dim TextWriter1 As TextWriter";
mostCurrent._textwriter1 = new anywheresoftware.b4a.objects.streams.File.TextWriterWrapper();
 //BA.debugLineNum = 103;BA.debugLine="Dim NMEA_SatView As String";
mostCurrent._nmea_satview = "";
 //BA.debugLineNum = 104;BA.debugLine="Dim NMEA_SatFix As Byte";
_nmea_satfix = (byte)0;
 //BA.debugLineNum = 105;BA.debugLine="Dim Distance As Double";
_distance = 0;
 //BA.debugLineNum = 108;BA.debugLine="Dim Default_Lattitude As Double = 52.732386 '52.6";
_default_lattitude = 52.732386;
 //BA.debugLineNum = 109;BA.debugLine="Dim Default_Longitude As Double = 5.275058 '4.953";
_default_longitude = 5.275058;
 //BA.debugLineNum = 111;BA.debugLine="Private Interval_Spin_Lbl As Label";
mostCurrent._interval_spin_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 112;BA.debugLine="Private Unit_Spin_Lbl As Label";
mostCurrent._unit_spin_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 113;BA.debugLine="Private Lattitude_Input_Lbl As Label";
mostCurrent._lattitude_input_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 114;BA.debugLine="Private NS_Spin_Lbl As Label";
mostCurrent._ns_spin_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 115;BA.debugLine="Private Longitude_Input_Lbl As Label";
mostCurrent._longitude_input_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 116;BA.debugLine="Private EW_Spin_Lbl As Label";
mostCurrent._ew_spin_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 117;BA.debugLine="End Sub";
return "";
}
public static String  _info_button_click() throws Exception{
 //BA.debugLineNum = 461;BA.debugLine="Sub Info_Button_click";
 //BA.debugLineNum = 462;BA.debugLine="Show_Info";
_show_info();
 //BA.debugLineNum = 463;BA.debugLine="End Sub";
return "";
}
public static String  _interval_spin_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 749;BA.debugLine="Sub Interval_Spin_ItemClick (Position As Int, Valu";
 //BA.debugLineNum = 750;BA.debugLine="Set_Interval";
_set_interval();
 //BA.debugLineNum = 751;BA.debugLine="End Sub";
return "";
}
public static void  _loadini() throws Exception{
ResumableSub_LoadINI rsub = new ResumableSub_LoadINI(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_LoadINI extends BA.ResumableSub {
public ResumableSub_LoadINI(gpssim.hobby4life.nl.main parent) {
this.parent = parent;
}
gpssim.hobby4life.nl.main parent;
anywheresoftware.b4a.objects.collections.List _list1 = null;
int _versioncompare = 0;
String _permission = "";
boolean _results = false;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 208;BA.debugLine="Dim List1 As List";
_list1 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 209;BA.debugLine="Dim VersionCompare As Int";
_versioncompare = 0;
 //BA.debugLineNum = 213;BA.debugLine="If File.Exists(File.DirRootExternal,FileINI) Then";
if (true) break;

case 1:
//if
this.state = 22;
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),parent._fileini)) { 
this.state = 3;
}else {
this.state = 21;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 215;BA.debugLine="RP.CheckAndRequest(RP.PERMISSION_READ_EXTERNAL_S";
parent._rp.CheckAndRequest(processBA,parent._rp.PERMISSION_READ_EXTERNAL_STORAGE);
 //BA.debugLineNum = 216;BA.debugLine="wait for Activity_PermissionResult(Permission As";
anywheresoftware.b4a.keywords.Common.WaitFor("activity_permissionresult", processBA, this, null);
this.state = 23;
return;
case 23:
//C
this.state = 4;
_permission = (String) result[0];
_results = (Boolean) result[1];
;
 //BA.debugLineNum = 217;BA.debugLine="If Results Then";
if (true) break;

case 4:
//if
this.state = 9;
if (_results) { 
this.state = 6;
}else {
this.state = 8;
}if (true) break;

case 6:
//C
this.state = 9;
 //BA.debugLineNum = 218;BA.debugLine="List1 = File.ReadList(File.DirRootExternal,File";
_list1 = anywheresoftware.b4a.keywords.Common.File.ReadList(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),parent._fileini);
 if (true) break;

case 8:
//C
this.state = 9;
 if (true) break;

case 9:
//C
this.state = 10;
;
 //BA.debugLineNum = 224;BA.debugLine="VersionCompare = List1.Get(0)";
_versioncompare = (int)(BA.ObjectToNumber(_list1.Get((int) (0))));
 //BA.debugLineNum = 226;BA.debugLine="If VersionCompare < Version Then";
if (true) break;

case 10:
//if
this.state = 13;
if (_versioncompare<parent._version) { 
this.state = 12;
}if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 227;BA.debugLine="SaveINI";
_saveini();
 //BA.debugLineNum = 228;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 13:
//C
this.state = 14;
;
 //BA.debugLineNum = 230;BA.debugLine="Lattitude_Input.Text = List1.Get(1)";
parent.mostCurrent._lattitude_input.setText(BA.ObjectToCharSequence(_list1.Get((int) (1))));
 //BA.debugLineNum = 231;BA.debugLine="Longitude_Input.Text = List1.Get(2)";
parent.mostCurrent._longitude_input.setText(BA.ObjectToCharSequence(_list1.Get((int) (2))));
 //BA.debugLineNum = 232;BA.debugLine="NS_Spin.SelectedIndex = List1.Get(3)";
parent.mostCurrent._ns_spin.setSelectedIndex((int)(BA.ObjectToNumber(_list1.Get((int) (3)))));
 //BA.debugLineNum = 233;BA.debugLine="EW_Spin.SelectedIndex = List1.Get(4)";
parent.mostCurrent._ew_spin.setSelectedIndex((int)(BA.ObjectToNumber(_list1.Get((int) (4)))));
 //BA.debugLineNum = 234;BA.debugLine="Interval_Spin.SelectedIndex = List1.Get(5)";
parent.mostCurrent._interval_spin.setSelectedIndex((int)(BA.ObjectToNumber(_list1.Get((int) (5)))));
 //BA.debugLineNum = 235;BA.debugLine="Unit_Spin.SelectedIndex = List1.Get(6)";
parent.mostCurrent._unit_spin.setSelectedIndex((int)(BA.ObjectToNumber(_list1.Get((int) (6)))));
 //BA.debugLineNum = 237;BA.debugLine="If Unit_Spin.SelectedIndex = 0 Then";
if (true) break;

case 14:
//if
this.state = 19;
if (parent.mostCurrent._unit_spin.getSelectedIndex()==0) { 
this.state = 16;
}else if(parent.mostCurrent._unit_spin.getSelectedIndex()==1) { 
this.state = 18;
}if (true) break;

case 16:
//C
this.state = 19;
 //BA.debugLineNum = 238;BA.debugLine="Unit_Update(False)";
_unit_update(anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 18:
//C
this.state = 19;
 //BA.debugLineNum = 240;BA.debugLine="Unit_Update(True)";
_unit_update(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 19:
//C
this.state = 22;
;
 //BA.debugLineNum = 243;BA.debugLine="Set_Interval";
_set_interval();
 if (true) break;

case 21:
//C
this.state = 22;
 //BA.debugLineNum = 246;BA.debugLine="SaveINI";
_saveini();
 if (true) break;

case 22:
//C
this.state = -1;
;
 //BA.debugLineNum = 248;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _activity_permissionresult(String _permission,boolean _results) throws Exception{
}
public static String  _menuabout_click() throws Exception{
 //BA.debugLineNum = 457;BA.debugLine="Sub menuabout_click";
 //BA.debugLineNum = 458;BA.debugLine="Show_Info";
_show_info();
 //BA.debugLineNum = 459;BA.debugLine="End Sub";
return "";
}
public static String  _menuexit_click() throws Exception{
 //BA.debugLineNum = 483;BA.debugLine="Sub MenuExit_click";
 //BA.debugLineNum = 484;BA.debugLine="Exit_App";
_exit_app();
 //BA.debugLineNum = 485;BA.debugLine="End Sub";
return "";
}
public static String  _nmea_checksum(String _nmea) throws Exception{
int _stpos = 0;
int _sppos = 0;
int _a = 0;
int _checksum = 0;
int _charint = 0;
 //BA.debugLineNum = 542;BA.debugLine="Sub NMEA_Checksum(NMEA As String)";
 //BA.debugLineNum = 543;BA.debugLine="Dim stPos As Int, spPos As Int, a As Int, checksum";
_stpos = 0;
_sppos = 0;
_a = 0;
_checksum = 0;
_charint = 0;
 //BA.debugLineNum = 545;BA.debugLine="a=0";
_a = (int) (0);
 //BA.debugLineNum = 547;BA.debugLine="stPos = NMEA.LastIndexOf (\"$\")+1";
_stpos = (int) (_nmea.lastIndexOf("$")+1);
 //BA.debugLineNum = 548;BA.debugLine="spPos = NMEA.LastIndexOf (\"*\")-1";
_sppos = (int) (_nmea.lastIndexOf("*")-1);
 //BA.debugLineNum = 549;BA.debugLine="checksum=0";
_checksum = (int) (0);
 //BA.debugLineNum = 550;BA.debugLine="If stPos<>0 And spPos<>0 And spPos>stPos Then";
if (_stpos!=0 && _sppos!=0 && _sppos>_stpos) { 
 //BA.debugLineNum = 551;BA.debugLine="For a=stPos To spPos";
{
final int step7 = 1;
final int limit7 = _sppos;
_a = _stpos ;
for (;_a <= limit7 ;_a = _a + step7 ) {
 //BA.debugLineNum = 552;BA.debugLine="charInt = Asc(NMEA.CharAt(a))";
_charint = anywheresoftware.b4a.keywords.Common.Asc(_nmea.charAt(_a));
 //BA.debugLineNum = 553;BA.debugLine="checksum = Bit.Xor(checksum,charInt)";
_checksum = anywheresoftware.b4a.keywords.Common.Bit.Xor(_checksum,_charint);
 }
};
 //BA.debugLineNum = 555;BA.debugLine="Return Bit.ToHexString(checksum).ToUpperCase";
if (true) return anywheresoftware.b4a.keywords.Common.Bit.ToHexString(_checksum).toUpperCase();
 }else {
 //BA.debugLineNum = 557;BA.debugLine="Return Null";
if (true) return BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Null);
 };
 //BA.debugLineNum = 560;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 20;BA.debugLine="Private Version As Int : Version = 302";
_version = 0;
 //BA.debugLineNum = 20;BA.debugLine="Private Version As Int : Version = 302";
_version = (int) (302);
 //BA.debugLineNum = 24;BA.debugLine="Dim app_label As String: app_label = \"GPS NMEA Si";
_app_label = "";
 //BA.debugLineNum = 24;BA.debugLine="Dim app_label As String: app_label = \"GPS NMEA Si";
_app_label = "GPS NMEA Simulator";
 //BA.debugLineNum = 25;BA.debugLine="Dim app_ver As String: app_ver = \"Version \" & (Ve";
_app_ver = "";
 //BA.debugLineNum = 25;BA.debugLine="Dim app_ver As String: app_ver = \"Version \" & (Ve";
_app_ver = "Version "+BA.NumberToString((_version/(double)100));
 //BA.debugLineNum = 26;BA.debugLine="Dim app_author As String: app_author = \"Hobby4lif";
_app_author = "";
 //BA.debugLineNum = 26;BA.debugLine="Dim app_author As String: app_author = \"Hobby4lif";
_app_author = "Hobby4life, The Netherlands.";
 //BA.debugLineNum = 27;BA.debugLine="Dim app_email As String: app_email = \"hobby4lifen";
_app_email = "";
 //BA.debugLineNum = 27;BA.debugLine="Dim app_email As String: app_email = \"hobby4lifen";
_app_email = "hobby4lifenl@gmail.com";
 //BA.debugLineNum = 28;BA.debugLine="Dim app_website As String: app_website = \"https:/";
_app_website = "";
 //BA.debugLineNum = 28;BA.debugLine="Dim app_website As String: app_website = \"https:/";
_app_website = "https://www.hobby4life.nl";
 //BA.debugLineNum = 29;BA.debugLine="Dim Admin As BluetoothAdmin";
_admin = new anywheresoftware.b4a.objects.Serial.BluetoothAdmin();
 //BA.debugLineNum = 30;BA.debugLine="Dim FoundDevices As List";
_founddevices = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 31;BA.debugLine="Type NameAndMac (Name As String, Mac As String)";
;
 //BA.debugLineNum = 32;BA.debugLine="Dim connectedDevice As NameAndMac";
_connecteddevice = new gpssim.hobby4life.nl.main._nameandmac();
 //BA.debugLineNum = 33;BA.debugLine="Dim Serial1 As Serial";
_serial1 = new anywheresoftware.b4a.objects.Serial();
 //BA.debugLineNum = 34;BA.debugLine="Dim Timer1 As Timer 'represents a 500 millisecond";
_timer1 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 35;BA.debugLine="Dim Timer2 As Timer 'represents a 100 millisecond";
_timer2 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 36;BA.debugLine="Dim Timer3 As Timer 'represents a 1 Second timer";
_timer3 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 37;BA.debugLine="Dim Connected As Boolean : Connected = False";
_connected = false;
 //BA.debugLineNum = 37;BA.debugLine="Dim Connected As Boolean : Connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 38;BA.debugLine="Dim Streaming As Boolean : Streaming = False";
_streaming = false;
 //BA.debugLineNum = 38;BA.debugLine="Dim Streaming As Boolean : Streaming = False";
_streaming = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 39;BA.debugLine="Dim Imperial As Boolean : Imperial = False";
_imperial = false;
 //BA.debugLineNum = 39;BA.debugLine="Dim Imperial As Boolean : Imperial = False";
_imperial = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 40;BA.debugLine="Dim Nav As Navigation";
_nav = new derez.libs.Navigation();
 //BA.debugLineNum = 41;BA.debugLine="Private RP As RuntimePermissions";
_rp = new anywheresoftware.b4a.objects.RuntimePermissions();
 //BA.debugLineNum = 43;BA.debugLine="Private FileINI As String : FileINI = \"GPSSIM.cfg";
_fileini = "";
 //BA.debugLineNum = 43;BA.debugLine="Private FileINI As String : FileINI = \"GPSSIM.cfg";
_fileini = "GPSSIM.cfg";
 //BA.debugLineNum = 44;BA.debugLine="End Sub";
return "";
}
public static String  _reset_latlong_button_click() throws Exception{
 //BA.debugLineNum = 795;BA.debugLine="Sub Reset_LatLong_Button_Click";
 //BA.debugLineNum = 797;BA.debugLine="If Streaming = False Then";
if (_streaming==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 798;BA.debugLine="Lattitude_Input.Text = Default_Lattitude";
mostCurrent._lattitude_input.setText(BA.ObjectToCharSequence(_default_lattitude));
 //BA.debugLineNum = 799;BA.debugLine="Longitude_Input.Text = Default_Longitude";
mostCurrent._longitude_input.setText(BA.ObjectToCharSequence(_default_longitude));
 //BA.debugLineNum = 800;BA.debugLine="Reset_snd.Play";
mostCurrent._reset_snd.Play();
 };
 //BA.debugLineNum = 803;BA.debugLine="End Sub";
return "";
}
public static String  _sattelite_bar_valuechanged(int _value,boolean _userchanged) throws Exception{
 //BA.debugLineNum = 967;BA.debugLine="Sub Sattelite_Bar_ValueChanged (Value As Int, User";
 //BA.debugLineNum = 968;BA.debugLine="Status_update";
_status_update();
 //BA.debugLineNum = 969;BA.debugLine="End Sub";
return "";
}
public static String  _save_button_click() throws Exception{
 //BA.debugLineNum = 1003;BA.debugLine="Sub Save_Button_Click";
 //BA.debugLineNum = 1004;BA.debugLine="SaveINI";
_saveini();
 //BA.debugLineNum = 1005;BA.debugLine="End Sub";
return "";
}
public static void  _saveini() throws Exception{
ResumableSub_SaveINI rsub = new ResumableSub_SaveINI(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_SaveINI extends BA.ResumableSub {
public ResumableSub_SaveINI(gpssim.hobby4life.nl.main parent) {
this.parent = parent;
}
gpssim.hobby4life.nl.main parent;
anywheresoftware.b4a.objects.collections.List _list1 = null;
String _permission = "";
boolean _results = false;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 254;BA.debugLine="Dim List1 As List";
_list1 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 256;BA.debugLine="List1.Initialize";
_list1.Initialize();
 //BA.debugLineNum = 258;BA.debugLine="List1.Add(Version)";
_list1.Add((Object)(parent._version));
 //BA.debugLineNum = 259;BA.debugLine="List1.Add(Lattitude_Input.Text)";
_list1.Add((Object)(parent.mostCurrent._lattitude_input.getText()));
 //BA.debugLineNum = 260;BA.debugLine="List1.Add(Longitude_Input.Text)";
_list1.Add((Object)(parent.mostCurrent._longitude_input.getText()));
 //BA.debugLineNum = 261;BA.debugLine="List1.Add(NS_Spin.SelectedIndex)";
_list1.Add((Object)(parent.mostCurrent._ns_spin.getSelectedIndex()));
 //BA.debugLineNum = 262;BA.debugLine="List1.Add(EW_Spin.SelectedIndex)";
_list1.Add((Object)(parent.mostCurrent._ew_spin.getSelectedIndex()));
 //BA.debugLineNum = 263;BA.debugLine="List1.Add(Interval_Spin.SelectedIndex)";
_list1.Add((Object)(parent.mostCurrent._interval_spin.getSelectedIndex()));
 //BA.debugLineNum = 264;BA.debugLine="List1.Add(Unit_Spin.SelectedIndex)";
_list1.Add((Object)(parent.mostCurrent._unit_spin.getSelectedIndex()));
 //BA.debugLineNum = 267;BA.debugLine="RP.CheckAndRequest(RP.PERMISSION_WRITE_EXTERNAL_S";
parent._rp.CheckAndRequest(processBA,parent._rp.PERMISSION_WRITE_EXTERNAL_STORAGE);
 //BA.debugLineNum = 268;BA.debugLine="wait for Activity_PermissionResult( Permission As";
anywheresoftware.b4a.keywords.Common.WaitFor("activity_permissionresult", processBA, this, null);
this.state = 7;
return;
case 7:
//C
this.state = 1;
_permission = (String) result[0];
_results = (Boolean) result[1];
;
 //BA.debugLineNum = 269;BA.debugLine="If Results Then";
if (true) break;

case 1:
//if
this.state = 6;
if (_results) { 
this.state = 3;
}else {
this.state = 5;
}if (true) break;

case 3:
//C
this.state = 6;
 //BA.debugLineNum = 270;BA.debugLine="File.WriteList(File.DirRootExternal,FileINI,List";
anywheresoftware.b4a.keywords.Common.File.WriteList(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),parent._fileini,_list1);
 //BA.debugLineNum = 271;BA.debugLine="ToastMessageShow (\"Settings Saved\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Settings Saved"),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 5:
//C
this.state = 6;
 if (true) break;

case 6:
//C
this.state = -1;
;
 //BA.debugLineNum = 276;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _search_button_click() throws Exception{
 //BA.debugLineNum = 298;BA.debugLine="Sub Search_Button_Click";
 //BA.debugLineNum = 299;BA.debugLine="If Connected = False Then";
if (_connected==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 300;BA.debugLine="FoundDevices.Initialize";
_founddevices.Initialize();
 //BA.debugLineNum = 301;BA.debugLine="If Admin.StartDiscovery	= False Then";
if (_admin.StartDiscovery()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 302;BA.debugLine="ToastMessageShow(\"Error starting discovery proc";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error starting discovery process."),anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 304;BA.debugLine="ProgressDialogShow(\"Searching for devices...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Searching for devices..."));
 };
 };
 //BA.debugLineNum = 309;BA.debugLine="End Sub";
return "";
}
public static String  _serial1_connected(boolean _success) throws Exception{
 //BA.debugLineNum = 501;BA.debugLine="Sub Serial1_Connected (Success As Boolean)";
 //BA.debugLineNum = 502;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 503;BA.debugLine="Log(\"connected: \" & Success)";
anywheresoftware.b4a.keywords.Common.LogImpl("21835010","connected: "+BA.ObjectToString(_success),0);
 //BA.debugLineNum = 505;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 506;BA.debugLine="Connected_snd.Play";
mostCurrent._connected_snd.Play();
 //BA.debugLineNum = 507;BA.debugLine="Start.Enabled = True";
mostCurrent._start.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 508;BA.debugLine="ToastMessageShow(\"Connected successfully!\", Fals";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Connected successfully!"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 509;BA.debugLine="Status.Text = \"Connected\"";
mostCurrent._status.setText(BA.ObjectToCharSequence("Connected"));
 //BA.debugLineNum = 510;BA.debugLine="TextWriter1.Initialize(Serial1.OutputStrea";
mostCurrent._textwriter1.Initialize(_serial1.getOutputStream());
 //BA.debugLineNum = 511;BA.debugLine="Timer1.Enabled = True";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 512;BA.debugLine="Connected = True";
_connected = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 514;BA.debugLine="Log(LastException.Message)";
anywheresoftware.b4a.keywords.Common.LogImpl("21835021",anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 515;BA.debugLine="ToastMessageShow(\"Error connecting: \" & LastExce";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error connecting: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage()),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 516;BA.debugLine="Connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 517;BA.debugLine="Timer1.Enabled = False";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 518;BA.debugLine="Status.Text = \"Waiting for incoming connection\"";
mostCurrent._status.setText(BA.ObjectToCharSequence("Waiting for incoming connection"));
 };
 //BA.debugLineNum = 520;BA.debugLine="End Sub";
return "";
}
public static String  _set_interval() throws Exception{
 //BA.debugLineNum = 754;BA.debugLine="Sub Set_Interval()";
 //BA.debugLineNum = 756;BA.debugLine="If Interval_Spin.SelectedIndex = 0 Then";
if (mostCurrent._interval_spin.getSelectedIndex()==0) { 
 //BA.debugLineNum = 757;BA.debugLine="Warning_Message";
_warning_message();
 //BA.debugLineNum = 758;BA.debugLine="Interval = 100";
_interval = (int) (100);
 //BA.debugLineNum = 759;BA.debugLine="Speed_Interval = 36000";
_speed_interval = (int) (36000);
 }else if(mostCurrent._interval_spin.getSelectedIndex()==1) { 
 //BA.debugLineNum = 762;BA.debugLine="Warning_Message";
_warning_message();
 //BA.debugLineNum = 763;BA.debugLine="Interval = 250";
_interval = (int) (250);
 //BA.debugLineNum = 764;BA.debugLine="Speed_Interval = 14400";
_speed_interval = (int) (14400);
 }else if(mostCurrent._interval_spin.getSelectedIndex()==2) { 
 //BA.debugLineNum = 767;BA.debugLine="Warning_Message";
_warning_message();
 //BA.debugLineNum = 768;BA.debugLine="Interval = 500";
_interval = (int) (500);
 //BA.debugLineNum = 769;BA.debugLine="Speed_Interval = 7200";
_speed_interval = (int) (7200);
 }else if(mostCurrent._interval_spin.getSelectedIndex()==3) { 
 //BA.debugLineNum = 772;BA.debugLine="Warning_Message";
_warning_message();
 //BA.debugLineNum = 773;BA.debugLine="Interval = 1000";
_interval = (int) (1000);
 //BA.debugLineNum = 774;BA.debugLine="Speed_Interval = 3600";
_speed_interval = (int) (3600);
 };
 //BA.debugLineNum = 777;BA.debugLine="Timer1.Initialize(\"Timer1\", Interval) '500ms seco";
_timer1.Initialize(processBA,"Timer1",(long) (_interval));
 //BA.debugLineNum = 779;BA.debugLine="End Sub";
return "";
}
public static String  _show_info() throws Exception{
 //BA.debugLineNum = 465;BA.debugLine="Sub Show_Info";
 //BA.debugLineNum = 466;BA.debugLine="Message = Message & app_label & CRLF";
mostCurrent._message = mostCurrent._message+_app_label+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 467;BA.debugLine="Message = Message & app_ver & CRLF & CRLF";
mostCurrent._message = mostCurrent._message+_app_ver+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 468;BA.debugLine="Message = Message & app_author & CRLF";
mostCurrent._message = mostCurrent._message+_app_author+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 469;BA.debugLine="Message = Message & app_email & CRLF";
mostCurrent._message = mostCurrent._message+_app_email+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 470;BA.debugLine="Message = Message & app_website & CRLF & CRLF";
mostCurrent._message = mostCurrent._message+_app_website+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 471;BA.debugLine="Message = Message & \"This application will genera";
mostCurrent._message = mostCurrent._message+"This application will generate the NMEA $GPRMC and $GPGGA sentences with the specified parameters. These can then be adjusted live while streaming."+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 472;BA.debugLine="Result = Msgbox2(Message,\"GPS NMEA Simulator\",\"Do";
_result = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence(mostCurrent._message),BA.ObjectToCharSequence("GPS NMEA Simulator"),"Done","","",(android.graphics.Bitmap)(mostCurrent._bmp_mid.getObject()),mostCurrent.activityBA);
 //BA.debugLineNum = 473;BA.debugLine="Message = \"\"";
mostCurrent._message = "";
 //BA.debugLineNum = 474;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 }else {
 //BA.debugLineNum = 477;BA.debugLine="Return True";
if (true) return BA.ObjectToString(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 479;BA.debugLine="End Sub";
return "";
}
public static String  _speed_bar_valuechanged(int _value,boolean _userchanged) throws Exception{
 //BA.debugLineNum = 959;BA.debugLine="Sub Speed_Bar_ValueChanged (Value As Int, UserChan";
 //BA.debugLineNum = 960;BA.debugLine="Speed = Value";
_speed = _value;
 //BA.debugLineNum = 961;BA.debugLine="Status_update";
_status_update();
 //BA.debugLineNum = 962;BA.debugLine="End Sub";
return "";
}
public static String  _start_click() throws Exception{
 //BA.debugLineNum = 355;BA.debugLine="Sub Start_Click";
 //BA.debugLineNum = 357;BA.debugLine="If Connected = True Then";
if (_connected==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 358;BA.debugLine="PWS.KeepAlive(True)";
mostCurrent._pws.KeepAlive(processBA,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 359;BA.debugLine="Streaming = True";
_streaming = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 360;BA.debugLine="StartStream_snd.Play";
mostCurrent._startstream_snd.Play();
 //BA.debugLineNum = 361;BA.debugLine="Start.Enabled = False";
mostCurrent._start.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 362;BA.debugLine="Stop.Enabled = True";
mostCurrent._stop.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 363;BA.debugLine="Enable_Items";
_enable_items();
 //BA.debugLineNum = 365;BA.debugLine="ToastMessageShow(\"Streaming Started\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Streaming Started"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 367;BA.debugLine="Lattitude = Lattitude_Input.Text";
_lattitude = (double)(Double.parseDouble(mostCurrent._lattitude_input.getText()));
 //BA.debugLineNum = 369;BA.debugLine="If NS_Spin.SelectedIndex =  1 Then";
if (mostCurrent._ns_spin.getSelectedIndex()==1) { 
 //BA.debugLineNum = 370;BA.debugLine="Lattitude = -Abs(Lattitude)";
_lattitude = -anywheresoftware.b4a.keywords.Common.Abs(_lattitude);
 }else {
 //BA.debugLineNum = 372;BA.debugLine="NS_Spin.SelectedIndex = 0";
mostCurrent._ns_spin.setSelectedIndex((int) (0));
 //BA.debugLineNum = 373;BA.debugLine="Lattitude = Abs(Lattitude)";
_lattitude = anywheresoftware.b4a.keywords.Common.Abs(_lattitude);
 };
 //BA.debugLineNum = 376;BA.debugLine="Longitude = Longitude_Input.Text";
_longitude = (double)(Double.parseDouble(mostCurrent._longitude_input.getText()));
 //BA.debugLineNum = 378;BA.debugLine="If EW_Spin.SelectedIndex = 1 Then";
if (mostCurrent._ew_spin.getSelectedIndex()==1) { 
 //BA.debugLineNum = 379;BA.debugLine="Longitude = -Abs(Longitude)";
_longitude = -anywheresoftware.b4a.keywords.Common.Abs(_longitude);
 }else {
 //BA.debugLineNum = 381;BA.debugLine="EW_Spin.SelectedIndex = 0";
mostCurrent._ew_spin.setSelectedIndex((int) (0));
 //BA.debugLineNum = 382;BA.debugLine="Longitude = Abs(Longitude)";
_longitude = anywheresoftware.b4a.keywords.Common.Abs(_longitude);
 };
 //BA.debugLineNum = 386;BA.debugLine="Reset_LatLong_Button.Enabled = False";
mostCurrent._reset_latlong_button.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 387;BA.debugLine="Unit_Spin_Lbl.Color = 0xFF7F7F7F		:	Unit_Spin.En";
mostCurrent._unit_spin_lbl.setColor((int) (0xff7f7f7f));
 //BA.debugLineNum = 387;BA.debugLine="Unit_Spin_Lbl.Color = 0xFF7F7F7F		:	Unit_Spin.En";
mostCurrent._unit_spin.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 388;BA.debugLine="Interval_Spin_Lbl.Color = 0xFF7F7F7F	:	Interval_";
mostCurrent._interval_spin_lbl.setColor((int) (0xff7f7f7f));
 //BA.debugLineNum = 388;BA.debugLine="Interval_Spin_Lbl.Color = 0xFF7F7F7F	:	Interval_";
mostCurrent._interval_spin.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 389;BA.debugLine="Lattitude_Input_Lbl.Color = 0xFF7F7F7F	:	Lattitu";
mostCurrent._lattitude_input_lbl.setColor((int) (0xff7f7f7f));
 //BA.debugLineNum = 389;BA.debugLine="Lattitude_Input_Lbl.Color = 0xFF7F7F7F	:	Lattitu";
mostCurrent._lattitude_input.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 390;BA.debugLine="Longitude_Input_Lbl.Color = 0xFF7F7F7F	:	Longitu";
mostCurrent._longitude_input_lbl.setColor((int) (0xff7f7f7f));
 //BA.debugLineNum = 390;BA.debugLine="Longitude_Input_Lbl.Color = 0xFF7F7F7F	:	Longitu";
mostCurrent._longitude_input.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 391;BA.debugLine="NS_Spin_Lbl.Color = 0xFF7F7F7F			:	NS_Spin.Enabl";
mostCurrent._ns_spin_lbl.setColor((int) (0xff7f7f7f));
 //BA.debugLineNum = 391;BA.debugLine="NS_Spin_Lbl.Color = 0xFF7F7F7F			:	NS_Spin.Enabl";
mostCurrent._ns_spin.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 392;BA.debugLine="EW_Spin_Lbl.Color = 0xFF7F7F7F			:	EW_Spin.Enabl";
mostCurrent._ew_spin_lbl.setColor((int) (0xff7f7f7f));
 //BA.debugLineNum = 392;BA.debugLine="EW_Spin_Lbl.Color = 0xFF7F7F7F			:	EW_Spin.Enabl";
mostCurrent._ew_spin.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 396;BA.debugLine="End Sub";
return "";
}
public static String  _status_update() throws Exception{
int _alt_temp = 0;
 //BA.debugLineNum = 708;BA.debugLine="Sub Status_update";
 //BA.debugLineNum = 710;BA.debugLine="Dim Alt_Temp As Int";
_alt_temp = 0;
 //BA.debugLineNum = 712;BA.debugLine="Speed = Speed_Bar.Value";
_speed = mostCurrent._speed_bar.getValue();
 //BA.debugLineNum = 714;BA.debugLine="NMEA_Bearing = Bearing";
mostCurrent._nmea_bearing = BA.NumberToString(_bearing);
 //BA.debugLineNum = 716;BA.debugLine="If Imperial = True Then";
if (_imperial==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 717;BA.debugLine="Alt_Temp = (Altitude / 3.2808399)";
_alt_temp = (int) ((_altitude/(double)3.2808399));
 }else if(_imperial==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 719;BA.debugLine="Alt_Temp = Altitude";
_alt_temp = _altitude;
 };
 //BA.debugLineNum = 722;BA.debugLine="NMEA_Alt = Alt_Temp";
mostCurrent._nmea_alt = BA.NumberToString(_alt_temp);
 //BA.debugLineNum = 724;BA.debugLine="If Sattelite_Bar.Value < 10 Then";
if (mostCurrent._sattelite_bar.getValue()<10) { 
 //BA.debugLineNum = 725;BA.debugLine="NMEA_SatView = \"0\" & Sattelite_Bar.Value";
mostCurrent._nmea_satview = "0"+BA.NumberToString(mostCurrent._sattelite_bar.getValue());
 }else {
 //BA.debugLineNum = 727;BA.debugLine="NMEA_SatView = Sattelite_Bar.Value";
mostCurrent._nmea_satview = BA.NumberToString(mostCurrent._sattelite_bar.getValue());
 };
 //BA.debugLineNum = 731;BA.debugLine="NMEA_SatFix = SatFix_Spin.SelectedIndex";
_nmea_satfix = (byte) (mostCurrent._satfix_spin.getSelectedIndex());
 //BA.debugLineNum = 732;BA.debugLine="If Imperial = False Then";
if (_imperial==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 733;BA.debugLine="Altitude_Input.Text = \"Altitude: \" & Altitude &";
mostCurrent._altitude_input.setText(BA.ObjectToCharSequence("Altitude: "+BA.NumberToString(_altitude)+" Meter"));
 //BA.debugLineNum = 734;BA.debugLine="Speed_Input.Text = \"Speed: \" & Speed & \" Km/h\"";
mostCurrent._speed_input.setText(BA.ObjectToCharSequence("Speed: "+BA.NumberToString(_speed)+" Km/h"));
 }else if(_imperial==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 736;BA.debugLine="Altitude_Input.Text = \"Altitude: \" & Altitude &";
mostCurrent._altitude_input.setText(BA.ObjectToCharSequence("Altitude: "+BA.NumberToString(_altitude)+" Feet"));
 //BA.debugLineNum = 737;BA.debugLine="Speed_Input.Text = \"Speed: \" & Speed & \" mph\"";
mostCurrent._speed_input.setText(BA.ObjectToCharSequence("Speed: "+BA.NumberToString(_speed)+" mph"));
 };
 //BA.debugLineNum = 741;BA.debugLine="Bearing_Input.Text = \"Bearing: \" & Bearing & \"°\"";
mostCurrent._bearing_input.setText(BA.ObjectToCharSequence("Bearing: "+BA.NumberToString(_bearing)+"°"));
 //BA.debugLineNum = 742;BA.debugLine="SatView_Input.Text = \"Satellites in view: \" & Sat";
mostCurrent._satview_input.setText(BA.ObjectToCharSequence("Satellites in view: "+BA.NumberToString(mostCurrent._sattelite_bar.getValue())));
 //BA.debugLineNum = 743;BA.debugLine="WindDirection_Input.Text = \"Wind direction: \" & W";
mostCurrent._winddirection_input.setText(BA.ObjectToCharSequence("Wind direction: "+BA.NumberToString(mostCurrent._winddirection_bar.getValue())+"°"));
 //BA.debugLineNum = 745;BA.debugLine="End Sub";
return "";
}
public static String  _stop_click() throws Exception{
 //BA.debugLineNum = 399;BA.debugLine="Sub Stop_Click";
 //BA.debugLineNum = 401;BA.debugLine="If Connected = True Then";
if (_connected==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 402;BA.debugLine="PWS.ReleaseKeepAlive";
mostCurrent._pws.ReleaseKeepAlive();
 //BA.debugLineNum = 403;BA.debugLine="Streaming = False";
_streaming = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 404;BA.debugLine="StopStream_snd.Play";
mostCurrent._stopstream_snd.Play();
 //BA.debugLineNum = 405;BA.debugLine="ToastMessageShow(\"Streaming Stopped\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Streaming Stopped"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 406;BA.debugLine="Start.Enabled = True";
mostCurrent._start.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 407;BA.debugLine="Stop.Enabled = False";
mostCurrent._stop.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 408;BA.debugLine="Disable_Items";
_disable_items();
 //BA.debugLineNum = 410;BA.debugLine="Reset_LatLong_Button.Enabled = True";
mostCurrent._reset_latlong_button.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 411;BA.debugLine="Unit_Spin_Lbl.Color = 0xFFFFFFFF		:	Unit_Spin.Ena";
mostCurrent._unit_spin_lbl.setColor((int) (0xffffffff));
 //BA.debugLineNum = 411;BA.debugLine="Unit_Spin_Lbl.Color = 0xFFFFFFFF		:	Unit_Spin.Ena";
mostCurrent._unit_spin.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 412;BA.debugLine="Interval_Spin_Lbl.Color = 0xFFFFFFFF	: 	Interval_";
mostCurrent._interval_spin_lbl.setColor((int) (0xffffffff));
 //BA.debugLineNum = 412;BA.debugLine="Interval_Spin_Lbl.Color = 0xFFFFFFFF	: 	Interval_";
mostCurrent._interval_spin.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 413;BA.debugLine="Lattitude_Input_Lbl.Color = 0xFFFFFFFF	: 	Lattitu";
mostCurrent._lattitude_input_lbl.setColor((int) (0xffffffff));
 //BA.debugLineNum = 413;BA.debugLine="Lattitude_Input_Lbl.Color = 0xFFFFFFFF	: 	Lattitu";
mostCurrent._lattitude_input.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 414;BA.debugLine="Longitude_Input_Lbl.Color = 0xFFFFFFFF	:	Longitud";
mostCurrent._longitude_input_lbl.setColor((int) (0xffffffff));
 //BA.debugLineNum = 414;BA.debugLine="Longitude_Input_Lbl.Color = 0xFFFFFFFF	:	Longitud";
mostCurrent._longitude_input.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 415;BA.debugLine="NS_Spin_Lbl.Color = 0xFFFFFFFF			:	NS_Spin.Enable";
mostCurrent._ns_spin_lbl.setColor((int) (0xffffffff));
 //BA.debugLineNum = 415;BA.debugLine="NS_Spin_Lbl.Color = 0xFFFFFFFF			:	NS_Spin.Enable";
mostCurrent._ns_spin.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 416;BA.debugLine="EW_Spin_Lbl.Color = 0xFFFFFFFF			:	EW_Spin.Enable";
mostCurrent._ew_spin_lbl.setColor((int) (0xffffffff));
 //BA.debugLineNum = 416;BA.debugLine="EW_Spin_Lbl.Color = 0xFFFFFFFF			:	EW_Spin.Enable";
mostCurrent._ew_spin.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 420;BA.debugLine="End Sub";
return "";
}
public static String  _timer1_tick() throws Exception{
 //BA.debugLineNum = 525;BA.debugLine="Sub Timer1_Tick";
 //BA.debugLineNum = 527;BA.debugLine="If Connected = True And Streaming = True Then";
if (_connected==anywheresoftware.b4a.keywords.Common.True && _streaming==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 528;BA.debugLine="Calc_NMEA";
_calc_nmea();
 //BA.debugLineNum = 529;BA.debugLine="Transmit_Data";
_transmit_data();
 };
 //BA.debugLineNum = 532;BA.debugLine="End Sub";
return "";
}
public static String  _timer2_tick() throws Exception{
 //BA.debugLineNum = 534;BA.debugLine="Sub Timer2_tick";
 //BA.debugLineNum = 535;BA.debugLine="Timer2.Enabled = False";
_timer2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 536;BA.debugLine="Indicator.Visible = False";
mostCurrent._indicator.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 537;BA.debugLine="End Sub";
return "";
}
public static String  _timer3_tick() throws Exception{
double _bearing_temp = 0;
int _altitude_temp = 0;
 //BA.debugLineNum = 864;BA.debugLine="Sub Timer3_tick";
 //BA.debugLineNum = 865;BA.debugLine="Dim Bearing_temp As Double";
_bearing_temp = 0;
 //BA.debugLineNum = 866;BA.debugLine="Dim Altitude_temp As Int";
_altitude_temp = 0;
 //BA.debugLineNum = 868;BA.debugLine="Bearing = Bearing_Bar.Value";
_bearing = mostCurrent._bearing_bar.getValue();
 //BA.debugLineNum = 869;BA.debugLine="Altitude = Altitude_Bar.Value";
_altitude = mostCurrent._altitude_bar.getValue();
 //BA.debugLineNum = 871;BA.debugLine="Bearing_temp = Bearing";
_bearing_temp = _bearing;
 //BA.debugLineNum = 872;BA.debugLine="Altitude_temp = Altitude";
_altitude_temp = _altitude;
 //BA.debugLineNum = 874;BA.debugLine="If Circle_Check.checked = True	Then";
if (mostCurrent._circle_check.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 876;BA.debugLine="If Circle_Value < 0 Then";
if (_circle_value<0) { 
 //BA.debugLineNum = 878;BA.debugLine="Bearing_temp = Bearing_temp - Abs(Circle_Value)";
_bearing_temp = _bearing_temp-anywheresoftware.b4a.keywords.Common.Abs(_circle_value);
 //BA.debugLineNum = 880;BA.debugLine="If Bearing_temp < 0 Then";
if (_bearing_temp<0) { 
 //BA.debugLineNum = 881;BA.debugLine="Bearing_temp = 360 - Abs(Bearing_temp)";
_bearing_temp = 360-anywheresoftware.b4a.keywords.Common.Abs(_bearing_temp);
 //BA.debugLineNum = 883;BA.debugLine="Bearing = Bearing_temp";
_bearing = (int) (_bearing_temp);
 //BA.debugLineNum = 884;BA.debugLine="Bearing_Bar.Value = Bearing";
mostCurrent._bearing_bar.setValue(_bearing);
 }else {
 //BA.debugLineNum = 886;BA.debugLine="Bearing = Bearing_temp";
_bearing = (int) (_bearing_temp);
 //BA.debugLineNum = 887;BA.debugLine="Bearing_Bar.Value = Bearing";
mostCurrent._bearing_bar.setValue(_bearing);
 };
 }else if(_circle_value>0) { 
 //BA.debugLineNum = 892;BA.debugLine="Bearing_temp = Bearing_temp + Circle_Value";
_bearing_temp = _bearing_temp+_circle_value;
 //BA.debugLineNum = 894;BA.debugLine="If Bearing_temp > 360 Then";
if (_bearing_temp>360) { 
 //BA.debugLineNum = 895;BA.debugLine="Bearing_temp = Bearing_temp - 360";
_bearing_temp = _bearing_temp-360;
 //BA.debugLineNum = 896;BA.debugLine="Bearing = Bearing_temp";
_bearing = (int) (_bearing_temp);
 //BA.debugLineNum = 897;BA.debugLine="Bearing_Bar.Value = Bearing";
mostCurrent._bearing_bar.setValue(_bearing);
 }else {
 //BA.debugLineNum = 899;BA.debugLine="Bearing = Bearing_temp";
_bearing = (int) (_bearing_temp);
 //BA.debugLineNum = 900;BA.debugLine="Bearing_Bar.Value = Bearing";
mostCurrent._bearing_bar.setValue(_bearing);
 };
 };
 };
 //BA.debugLineNum = 906;BA.debugLine="If Altitude_Check.Checked = True Then";
if (mostCurrent._altitude_check.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 908;BA.debugLine="If Altitude_value < 0 Then";
if (_altitude_value<0) { 
 //BA.debugLineNum = 910;BA.debugLine="If Altitude > 0 Then";
if (_altitude>0) { 
 //BA.debugLineNum = 911;BA.debugLine="Altitude_temp = Altitude_temp - Abs(Altitude_v";
_altitude_temp = (int) (_altitude_temp-anywheresoftware.b4a.keywords.Common.Abs(_altitude_value));
 //BA.debugLineNum = 912;BA.debugLine="Altitude = Altitude_temp";
_altitude = _altitude_temp;
 //BA.debugLineNum = 913;BA.debugLine="Altitude_Bar.Value = Altitude";
mostCurrent._altitude_bar.setValue(_altitude);
 }else {
 //BA.debugLineNum = 915;BA.debugLine="Altitude = 0";
_altitude = (int) (0);
 };
 }else if(_altitude_value>0) { 
 //BA.debugLineNum = 920;BA.debugLine="If Altitude < 3000 Then";
if (_altitude<3000) { 
 //BA.debugLineNum = 921;BA.debugLine="Altitude_temp = Altitude_temp + Altitude_valu";
_altitude_temp = (int) (_altitude_temp+_altitude_value);
 //BA.debugLineNum = 922;BA.debugLine="Altitude = Altitude_temp";
_altitude = _altitude_temp;
 //BA.debugLineNum = 923;BA.debugLine="Altitude_Bar.Value = Altitude";
mostCurrent._altitude_bar.setValue(_altitude);
 }else {
 //BA.debugLineNum = 925;BA.debugLine="Altitude = 3000";
_altitude = (int) (3000);
 };
 };
 };
 //BA.debugLineNum = 931;BA.debugLine="If Wind_Check.Checked = True Then";
if (mostCurrent._wind_check.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 932;BA.debugLine="WindDirection_Bar.Enabled = True";
mostCurrent._winddirection_bar.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 934;BA.debugLine="WindDirection_Bar.Enabled = False";
mostCurrent._winddirection_bar.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 938;BA.debugLine="End Sub";
return "";
}
public static String  _transmit_data() throws Exception{
 //BA.debugLineNum = 667;BA.debugLine="Sub Transmit_Data";
 //BA.debugLineNum = 669;BA.debugLine="Timer1.Enabled = False";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 670;BA.debugLine="TextWriter1.WriteLine(GPRMC) 'Fills the TX buffer";
mostCurrent._textwriter1.WriteLine(mostCurrent._gprmc);
 //BA.debugLineNum = 671;BA.debugLine="TextWriter1.Flush 'Spits out TX buffer over bluet";
mostCurrent._textwriter1.Flush();
 //BA.debugLineNum = 672;BA.debugLine="TextWriter1.WriteLine(GPGGA)";
mostCurrent._textwriter1.WriteLine(mostCurrent._gpgga);
 //BA.debugLineNum = 673;BA.debugLine="TextWriter1.Flush 'Spits out TX buffer over bluet";
mostCurrent._textwriter1.Flush();
 //BA.debugLineNum = 674;BA.debugLine="Timer1.Enabled = True";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 675;BA.debugLine="Indicator.Visible = True";
mostCurrent._indicator.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 676;BA.debugLine="Timer2.Enabled = True";
_timer2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 678;BA.debugLine="End Sub";
return "";
}
public static String  _unit_spin_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 993;BA.debugLine="Sub Unit_Spin_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 994;BA.debugLine="If Position = 0 Then";
if (_position==0) { 
 //BA.debugLineNum = 995;BA.debugLine="Unit_Update(False)";
_unit_update(anywheresoftware.b4a.keywords.Common.False);
 }else if(_position==1) { 
 //BA.debugLineNum = 997;BA.debugLine="Unit_Update(True)";
_unit_update(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 999;BA.debugLine="End Sub";
return "";
}
public static String  _unit_update(boolean _unit_type) throws Exception{
 //BA.debugLineNum = 975;BA.debugLine="Sub Unit_Update(Unit_Type As Boolean)";
 //BA.debugLineNum = 976;BA.debugLine="If Unit_Type = False Then";
if (_unit_type==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 977;BA.debugLine="Imperial = False";
_imperial = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 978;BA.debugLine="Altitude_Text.Text = \"0 m/s\"";
mostCurrent._altitude_text.setText(BA.ObjectToCharSequence("0 m/s"));
 //BA.debugLineNum = 979;BA.debugLine="WindSpeed_Text.Text = \"0 km/h\"";
mostCurrent._windspeed_text.setText(BA.ObjectToCharSequence("0 km/h"));
 //BA.debugLineNum = 980;BA.debugLine="Speed_Input.Text = \"Speed: \" & Speed & \" km/h\"";
mostCurrent._speed_input.setText(BA.ObjectToCharSequence("Speed: "+BA.NumberToString(_speed)+" km/h"));
 //BA.debugLineNum = 981;BA.debugLine="Altitude_Input.Text = \"Altitude: \" & Altitude &";
mostCurrent._altitude_input.setText(BA.ObjectToCharSequence("Altitude: "+BA.NumberToString(_altitude)+" Meter"));
 }else if(_unit_type==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 983;BA.debugLine="Imperial = True";
_imperial = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 984;BA.debugLine="Altitude_Text.Text = \"0 ft/s\"";
mostCurrent._altitude_text.setText(BA.ObjectToCharSequence("0 ft/s"));
 //BA.debugLineNum = 985;BA.debugLine="WindSpeed_Text.Text = \"0 mph\"";
mostCurrent._windspeed_text.setText(BA.ObjectToCharSequence("0 mph"));
 //BA.debugLineNum = 986;BA.debugLine="Speed_Input.Text = \"Speed: \" & Speed & \" mph\"";
mostCurrent._speed_input.setText(BA.ObjectToCharSequence("Speed: "+BA.NumberToString(_speed)+" mph"));
 //BA.debugLineNum = 987;BA.debugLine="Altitude_Input.Text = \"Altitude: \" & Altitude &";
mostCurrent._altitude_input.setText(BA.ObjectToCharSequence("Altitude: "+BA.NumberToString(_altitude)+" feet"));
 };
 //BA.debugLineNum = 989;BA.debugLine="Circle_Text.Text = \"0 °/s\"";
mostCurrent._circle_text.setText(BA.ObjectToCharSequence("0 °/s"));
 //BA.debugLineNum = 990;BA.debugLine="End Sub";
return "";
}
public static String  _warning_message() throws Exception{
 //BA.debugLineNum = 781;BA.debugLine="Sub Warning_Message";
 //BA.debugLineNum = 782;BA.debugLine="Message = Message & \"Using this app in combinatio";
mostCurrent._message = mostCurrent._message+"Using this app in combination with a"+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 783;BA.debugLine="Message = Message & \"Bluetooth to Serial adapter,";
mostCurrent._message = mostCurrent._message+"Bluetooth to Serial adapter,"+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 784;BA.debugLine="Message = Message & \"Low baudrates in conjunction";
mostCurrent._message = mostCurrent._message+"Low baudrates in conjunction with too"+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 785;BA.debugLine="Message = Message & \"fast update intervals can ca";
mostCurrent._message = mostCurrent._message+"fast update intervals can cause truncated"+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 786;BA.debugLine="Message = Message & \"message transmissions! or ha";
mostCurrent._message = mostCurrent._message+"message transmissions! or hangup of the progam."+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 787;BA.debugLine="Message = Message & \"Guideline: 1200bps -> 1 Seco";
mostCurrent._message = mostCurrent._message+"Guideline: 1200bps -> 1 Second interval"+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 788;BA.debugLine="Message = Message & \"4800bps -> 500mS interval\"";
mostCurrent._message = mostCurrent._message+"4800bps -> 500mS interval";
 //BA.debugLineNum = 790;BA.debugLine="Msgbox2(Message,\"Warning\",\"Ok\",\"\",\"\",bmp_warning)";
anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence(mostCurrent._message),BA.ObjectToCharSequence("Warning"),"Ok","","",(android.graphics.Bitmap)(mostCurrent._bmp_warning.getObject()),mostCurrent.activityBA);
 //BA.debugLineNum = 791;BA.debugLine="Message = \"\"";
mostCurrent._message = "";
 //BA.debugLineNum = 793;BA.debugLine="End Sub";
return "";
}
public static String  _winddirection_bar_valuechanged(int _value,boolean _userchanged) throws Exception{
 //BA.debugLineNum = 970;BA.debugLine="Sub WindDirection_Bar_ValueChanged (Value As Int,";
 //BA.debugLineNum = 971;BA.debugLine="Status_update";
_status_update();
 //BA.debugLineNum = 972;BA.debugLine="End Sub";
return "";
}
public static String  _windspeeddecr_button_click() throws Exception{
 //BA.debugLineNum = 840;BA.debugLine="Sub WindSpeedDecr_Button_Click";
 //BA.debugLineNum = 841;BA.debugLine="If WindSpeed_Value > 0 Then";
if (_windspeed_value>0) { 
 //BA.debugLineNum = 842;BA.debugLine="WindSpeed_Value = WindSpeed_Value - 5";
_windspeed_value = (int) (_windspeed_value-5);
 //BA.debugLineNum = 843;BA.debugLine="If Imperial = False Then";
if (_imperial==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 844;BA.debugLine="WindSpeed_Text.Text = WindSpeed_Value & \" Km/h\"";
mostCurrent._windspeed_text.setText(BA.ObjectToCharSequence(BA.NumberToString(_windspeed_value)+" Km/h"));
 }else if(_imperial==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 846;BA.debugLine="WindSpeed_Text.Text = WindSpeed_Value & \" mph\"";
mostCurrent._windspeed_text.setText(BA.ObjectToCharSequence(BA.NumberToString(_windspeed_value)+" mph"));
 };
 };
 //BA.debugLineNum = 849;BA.debugLine="End Sub";
return "";
}
public static String  _windspeedinc_button_click() throws Exception{
 //BA.debugLineNum = 829;BA.debugLine="Sub WindSpeedInc_Button_Click";
 //BA.debugLineNum = 830;BA.debugLine="If WindSpeed_Value < 250 Then";
if (_windspeed_value<250) { 
 //BA.debugLineNum = 831;BA.debugLine="WindSpeed_Value = WindSpeed_Value + 5";
_windspeed_value = (int) (_windspeed_value+5);
 //BA.debugLineNum = 832;BA.debugLine="If Imperial = False Then";
if (_imperial==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 833;BA.debugLine="WindSpeed_Text.Text = WindSpeed_Value & \" Km/h\"";
mostCurrent._windspeed_text.setText(BA.ObjectToCharSequence(BA.NumberToString(_windspeed_value)+" Km/h"));
 }else if(_imperial==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 835;BA.debugLine="WindSpeed_Text.Text = WindSpeed_Value & \" mph\"";
mostCurrent._windspeed_text.setText(BA.ObjectToCharSequence(BA.NumberToString(_windspeed_value)+" mph"));
 };
 };
 //BA.debugLineNum = 838;BA.debugLine="End Sub";
return "";
}
}
