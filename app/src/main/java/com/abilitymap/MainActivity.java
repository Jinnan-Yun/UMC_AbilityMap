package com.abilitymap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.location.Address;
import android.content.DialogInterface;
import android.content.Intent;

import java.text.SimpleDateFormat;

import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.abilitymap.databinding.ActivityMainBinding;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, Overlay.OnClickListener, SetMarker_facility, SetMarker_wheel {
    private GpsTracker gpsTracker;
    private NaverMap naverMap;
    public static Activity firstActivity;
    public static ArrayList<JsonApi_total.total_item> total_list = new ArrayList();
    public static ArrayList<JsonApi_bike.bike_item> bike_list = new ArrayList();
    public static ArrayList<JsonApi_charge.charge_item> charge_list = new ArrayList();
    public static ArrayList<JsonApi_slope.slope_item> slope_list = new ArrayList();
    public static ArrayList<JsonApi_danger.danger_item> danger_list = new ArrayList();
    public static ArrayList<JsonApi_ele.ele_item> ele_list = new ArrayList();


    LabeledSwitch labeledSwitch_total1;
    LabeledSwitch labeledSwitch_hos2;
    LabeledSwitch labeledSwitch_fac3;
    LabeledSwitch labeledSwitch_charge4;
    LabeledSwitch labeledSwitch_wheel5;
    LabeledSwitch labeledSwitch_ele6;
    LabeledSwitch labeledSwitch_bike7;
    LabeledSwitch labeledSwitch_slope8;
    LabeledSwitch labeledSwitch_danger9;
    Button filter_button;


    Map<String, ?> total1_;
    Map<String, ?> hos2_;
    Map<String, ?> fac3_;
    Map<String, ?> charge4_;
    Map<String, ?> wheel5_;
    Map<String, ?> ele6_;
    Map<String, ?> bike7_;
    Map<String, ?> slope8_;
    Map<String, ?> danger9_;



    private FusedLocationSource locationSource;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int CAMERA_PICTURE_SAVED_CODE = 3001;
    private FusedLocationProviderClient fusedLocationClient;
    private Location mLastlocation = null;
    private double speed, calSpeed, getSpeed;
    private LocationButtonView locationButtonView2;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private ArrayList<Marker> TotalmarkerList = new ArrayList();
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private LatLng currentPosition;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final String[] REQUIRED_PERMISSIONS2 = {
            "123",
            Manifest.permission.SEND_SMS,
    };
    private ActivityMainBinding binding;

    ActivityResultLauncher<Intent> activityResultLauncher;

    List<LatLng> latLngList = new ArrayList<>();
    private boolean clickable = true;
    private boolean clickable2 = true;
    private final long finishtimeed = 1000;
    private long presstime = 0;
    private boolean isDrawerOpen = false;
    private boolean isFilter = false;
    ProgressDialog dialog; //원형 프로그레스바

//    List<Double> latitudeList = new ArrayList<Double>();
//    List<Double> longitudeList = new ArrayList<Double>();
//
//    double LNG = Double.parseDouble(latitudeList.toString());
//    double LAT = Double.parseDouble(longitudeList.toString());
//
//
//try {
//
//        JSONObject Land = new JSONObject(result);
//        JSONArray jsonArray = Land.getJSONArray("Response");
//        for(int i = 0 ; i<jsonArray.length(); i++){
//            JSONObject subJsonObject = jsonArray.getJSONObject(i);
//
//            Double sLAT = subJsonObject.getDouble("latitude"); //String sLAT = subJsonObject.getString("latitude");
//            Double sLNG = subJsonObject.getDouble("longitude"); //String sLNG = subJsonObject.getString("longitude");
//
//            latitudeList.add(sLAT);
//            longitudeList.add(sLNG);
//        }
//    } catch (
//    JSONException e) {
//        e.printStackTrace();
//    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState){ //화면 생성과 함께 현재 위치 받아옴.
        dialog = new ProgressDialog(MainActivity.this); //프로그레스 대화상자 객체 생성
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //프로그레스 대화상자 스타일 원형으로 설정
        dialog.setCancelable(false);
        dialog.setMessage("잠시만 기다려주세요."); //프로그레스 대화상자 메시지 설정
        dialog.show(); //프로그레스 대화상자 띄우기

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                dialog.dismiss(); // 3초 시간지연 후 프로그레스 대화상자 닫기
            }
        }, 2000);
        //로딩

        firstActivity = MainActivity.this;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton Report_message = (ImageButton) findViewById(R.id.message_button);
        System.out.println("oncreate");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initClickListener();
        initLauncher();
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {

            checkRunTimePermission();
        }
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            currentPosition = new LatLng(37.3595316, 127.1052133);
                            // Got last known location. In some rare situations this can be null.
                        } else {
                            currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                            // Logic to handle location object
                        }
                    }
                });
        String lat = String.valueOf(NaverMap.DEFAULT_CAMERA_POSITION.target.latitude);
        String lon = String.valueOf(NaverMap.DEFAULT_CAMERA_POSITION.target.longitude);


        JsonApi_total total_api = new JsonApi_total();
        JsonApi_bike bike_api  = new JsonApi_bike();
        JsonApi_slope slope_api  = new JsonApi_slope();
        JsonApi_charge charge_api  = new JsonApi_charge();
        JsonApi_danger danger_api  = new JsonApi_danger();
        JsonApi_ele ele_api  = new JsonApi_ele();
        total_api.execute(lat,lon,"");
        bike_api.execute(lat,lon,"");
        charge_api.execute(lat,lon,"");
        slope_api.execute(lat,lon,"");
        danger_api.execute(lat, lon, "");
        ele_api.execute(lat, lon, "");

//        new Thread(() -> {
//            setUpMap(); // network 동작, 인터넷에서 xml을 받아오는 코드
//        }).start();


        // 핸들러

                         

    }


    //
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();

                } else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

            if (grandResults.length > 0 && grandResults[0] == PackageManager.PERMISSION_GRANTED) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.Face);
            }
        }

    }


    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        ImageButton repot_message = (ImageButton) findViewById(R.id.message_button);
        ImageButton Report_button = (ImageButton) findViewById(R.id.repot_button);

        /*Report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //현 위치 location 받아와서 서버로 넘겨줘야함
                //넘겨줄 것 : 사진, text, 닉네임, 좌표, 신고일자

                //카메라 권한요청, 내 파일 권한 요청 필요

                //카메라 화면이 먼저 나옴
                //사진 찍고
                //report detail 화면 띄워서
                //입력받고 전송하기 버튼 누르면

                //현 위치 : locationSource

                //아니 여기 왜 버튼이 안눌려렬렬려려려려려려려려려렬
                //버튼 init버튼인가 밑에 함수에서 설정하면 됩니다^^
//zzzzz

                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                Log.d("camera","Reportbutton clicked");

                Intent intent = null;
                Log.d("camera","clicked");
                setCamera(intent);
            }
        });*/
        if (overlay instanceof Marker && clickable) {
//            Toast.makeText(this.getApplicationContext(),"위험지역입니다",Toast.LENGTH_LONG).show();
            Object object = overlay.getTag();
            String tag = String.valueOf(object);
            //charge_list.get()
            JsonApi_charge.charge_item selectedItem = findThisMarkerItem(((Marker) overlay).getPosition(), charge_list);

            String location = selectedItem.getLocation();
            String week = selectedItem.getWeek();
            String weekend = selectedItem.getWeekend();
            String holiday = selectedItem.getHoliday();

            System.out.println("리스트 검색 결과 : "+ location + "," + week +"," + weekend +","+holiday);

            LocationDetailFragment infoFragment = new LocationDetailFragment(tag,location,week,weekend,holiday);


            //LocationDetailFragment infoFragment = new LocationDetailFragment(tag);

            getSupportFragmentManager().beginTransaction().add(R.id.map, infoFragment).addToBackStack(null).commit();
            clickable = false;
            repot_message.setVisibility(View.INVISIBLE);
            Report_button.setVisibility(View.INVISIBLE);

            Log.d("clickable?", String.valueOf(clickable));

            LatLng selectedPosition = ((Marker) overlay).getPosition();
            CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(selectedPosition, 16).pivot(new PointF(0.5f, 0.37f)).animate(CameraAnimation.Easing);
            naverMap.moveCamera(cameraUpdate);


            naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                    getSupportFragmentManager().beginTransaction().remove(infoFragment).commit();
                    getSupportFragmentManager().popBackStack();
                    clickable = true;

                    repot_message.setVisibility(View.VISIBLE);
                    Report_button.setVisibility(View.VISIBLE);
                    Log.d("clickable?", String.valueOf(clickable));
                    Log.d("click event", "onMapClick");


                }


            });

            return true;
        }
        return false;
    }

    JsonApi_charge.charge_item findThisMarkerItem(LatLng location, ArrayList<JsonApi_charge.charge_item> list) {
        String thisLat = String.valueOf(location.latitude);
        String thisLng = String.valueOf(location.longitude);
        JsonApi_charge.charge_item selectedItem = null;

        System.out.println(thisLat);
        System.out.println(thisLng);

        for (int i = 0; i < list.size(); i++) {
            JsonApi_charge.charge_item item = list.get(i);
            System.out.println(i+"," + item + ", Lat : "+item.getLat() + "Lng : " + item.getLng());
            if ( thisLat.equals(item.getLat()) && thisLng.equals(item.getLng()) ) {
                selectedItem = item;
                System.out.println("item Found!");
            }
        }
        return selectedItem;
    }


    @Override
    public void onBackPressed() {
//        ImageButton Call_button = (ImageButton)findViewById(R.id.call_button);
//        ImageButton Report_button = (ImageButton)findViewById(R.id.repot_button);
//        ImageButton Report_message = (ImageButton)findViewById(R.id.repot_message);
//        clickable = true;
//        super.onBackPressed();
//        Call_button.setVisibility(View.VISIBLE);
//        Report_button.setVisibility(View.VISIBLE);
//        Report_message.setVisibility(View.VISIBLE);
//        Log.d("clickable?", "backKeyPressed");
//        Log.d("clickable?", String.valueOf(clickable));


        if (isDrawerOpen) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            isDrawerOpen = false;
        } else if (isFilter) {
            isFilter = false;
        } else {

            if (clickable) {

                long tempTime = System.currentTimeMillis();
                long intervalTime = tempTime - presstime;

                if (0 <= intervalTime && finishtimeed >= intervalTime) {
                    finish();
                } else {
                    presstime = tempTime;
                    Toast.makeText(getApplicationContext(), "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
                }

            } else {
                super.onBackPressed();
                ImageButton message_button = (ImageButton) findViewById(R.id.message_button);
                ImageButton Report_button = (ImageButton) findViewById(R.id.repot_button);
                message_button.setVisibility(View.VISIBLE);
                Report_button.setVisibility(View.VISIBLE);
                clickable = true;


            }


        }
    }


    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                //Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }
    }

    public String getSimpleCurrentAddress(String currAddress){
        String addrCut[] = currAddress.split(" ");
        String simpleAdress;

        if (addrCut.length >= 6) {
            simpleAdress = addrCut[2] + " " + addrCut[3] + " " + addrCut[4] + " " + addrCut[5];
        } else if (addrCut.length >= 5) {
            simpleAdress = addrCut[2] + " " + addrCut[3] + " " + addrCut[4];
        } else if (addrCut.length >= 4) {
            simpleAdress = addrCut[2] + " " + addrCut[3];
        } else if (addrCut.length >= 3) {
            simpleAdress = addrCut[1] + " " + addrCut[2];
        } else if (addrCut.length >= 2) {
            simpleAdress = addrCut[0] + " " + addrCut[1];
        } else if (addrCut.length >= 1) {
            simpleAdress = addrCut[0];
        } else {
            simpleAdress = "위치 정보 없음";
        }

        return simpleAdress;

    }


    public String getCurrentAddress(double latitude, double longitude) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    8);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);

        return address.getAddressLine(0).toString() + "\n";
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, id);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public void Dead() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }



    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(currentPosition).animate(CameraAnimation.Fly,0);
        naverMap.moveCamera(cameraUpdate);
        this.naverMap = naverMap;


        SharedPreferences total1 = getSharedPreferences("total", Activity.MODE_PRIVATE);
        SharedPreferences hos2 = getSharedPreferences("hos2", Activity.MODE_PRIVATE);
        SharedPreferences fac3 = getSharedPreferences("fac3", Activity.MODE_PRIVATE);
        SharedPreferences charge4 = getSharedPreferences("charge4", Activity.MODE_PRIVATE);
        SharedPreferences wheel5 = getSharedPreferences("wheel5", Activity.MODE_PRIVATE);
        SharedPreferences ele6 = getSharedPreferences("ele6", Activity.MODE_PRIVATE);
        SharedPreferences bike7 = getSharedPreferences("bike7", Activity.MODE_PRIVATE);
        SharedPreferences slope8 = getSharedPreferences("slope8", Activity.MODE_PRIVATE);
        SharedPreferences danger9 = getSharedPreferences("danger9", Activity.MODE_PRIVATE);


        if(total1.getBoolean("total",true)){
            setMarker_hos(); //병원이랑 시설
            drawMarker_bike();
            setMarker_Charge();
            drawMarker_slope();
            setMarker_danger();
            drawMarker_ele();
        }else{
            if (hos2.getBoolean("total", true)) {
                setMarker_hos(); //병원이랑 시설
            }
            if (fac3.getBoolean("total", true)) {

            }
            if (charge4.getBoolean("total", true)) {
                setMarker_Charge();
            }
            if (wheel5.getBoolean("total", true)) {

            }
            if (ele6.getBoolean("total", true)) {
                drawMarker_ele();
            }
            if (bike7.getBoolean("total", true)) {
                drawMarker_bike();
            }
            if (slope8.getBoolean("total", true)) {
                drawMarker_slope();
            }
            if (danger9.getBoolean("total", true)) {
                setMarker_danger();
            }

        }



        //충전기
        naverMap.setMaxZoom(19.0);
        naverMap.setMinZoom(2.0);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setZoomControlEnabled(true); //줌인 줌아웃
        uiSettings.setLocationButtonEnabled(true);


        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);


        final TextView location_text = (TextView) findViewById(R.id.location_text);



        /*
        Marker marker = new Marker();
        marker.setPosition(latLngList.get(0));
        marker.setMap(naverMap);
        marker.setOnClickListener(this);
*/

        /*
        final TextView textView_lat = findViewById(R.id.lat);
        final TextView textView_lon = findViewById(R.id.lon);
        */
//        final TextView tvGetSpeed = findViewById(R.id.tvGetspeed);
//        final TextView tvCalSpeed = findViewById(R.id.tvCalspeed);


        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                gpsTracker = new GpsTracker(MainActivity.this);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                double deltaTime = 0;
                // getSpeed() 함수를 이용하여 속도 계산(m/s -> km/h)
                getSpeed = Double.parseDouble(String.format("%.3f", location.getSpeed() * 3.6));
                // 위치 변경이 두번째로 변경된 경우 계산에 의해 속도 계산
                if (mLastlocation != null) {
                    deltaTime = (location.getTime() - mLastlocation.getTime());
                    // 속도 계산(시간=ms, 거리=m -> km/h)
                    speed = (mLastlocation.distanceTo(location) / deltaTime) * 3600;
                    calSpeed = Double.parseDouble(String.format("%.3f", speed));
                }
                //현재위치를 지난 위치로 변경
                mLastlocation = location;

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                String address = getSimpleCurrentAddress(getCurrentAddress(latitude, longitude));
                location_text.setText(address);

                //0719

                String lat_str = Double.toString(latitude);
                String lon_str = Double.toString(longitude);

                /*
                textView_lat.setText(lat_str);
                textView_lon.setText(lon_str);
                 */

                String gs_str = Double.toString(getSpeed);
                String cs_str = Double.toString(calSpeed);


            }

        });


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == 3000) {
                Intent cameraIntent = result.getData();
                String cameraFlag = cameraIntent.getStringExtra(Camera2Activity.picSaved);
                //Toast.makeText(MainActivity.this,cameraFlag, Toast.LENGTH_SHORT).show();
                Log.d("lancher", "launch ok");
            }

        });
    }


    private void setCamera(Intent cameraIntent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            cameraIntent = new Intent(getApplicationContext(), Camera2Activity.class);
            //activityResultLauncher.launch(cameraIntent);
        }

        //activityResultLauncher.launch(cameraIntent);
        startActivity(cameraIntent);
    }

    //메시지 보내기 함수
    private void sendSms() {
        SmsManager manager = SmsManager.getDefault();
        
        SharedPreferences spfPersonInfo = getSharedPreferences("personInfo", MODE_PRIVATE);
        int personId = spfPersonInfo.getInt("position", -1);
        Log.d("DB POSITION", String.valueOf(personId));

        if (personId != -1){    //선택된 연락처가 있을 때만
            PersonInfoDatabase personInfoDatabase = PersonInfoDatabase.Companion.getInstance(this);
            List<PersonInfo> pL = personInfoDatabase.personInfoDao().getPersonList();
            Log.d("데이타 베이스 확인 ! ! !", personInfoDatabase.personInfoDao().getPersonList().toString());
            Log.d("데이타 베이스 번호", pL.get(personId).getPhoneNumber());
            Log.d("데이타 베이스 텍스트", pL.get(personId).getText());
            
            if (!(pL.get(personId).getText().equals(""))){  //텍스트 입력한 기록이 있는 연락처에 한정
                manager.sendTextMessage(pL.get(personId).getPhoneNumber(), null, pL.get(personId).getText(), null, null);
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);
        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }


    private void initClickListener() {

        //긴급신고 메세지지
        ImageButton Report_message = findViewById(R.id.message_button);
        Report_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로컬에 기록하기. 그걸 가지고 1,2,3번 시도 구분
                SharedPreferences pref2 = getSharedPreferences("Permission_touch", Activity.MODE_PRIVATE);
                boolean Permission_touch = pref2.getBoolean("Permission_touch", false);
                //입력한 값을 가져와 변수에 담는다
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS2[1])) {
                        //2번 퍼미션 시도
                        SharedPreferences.Editor editor = pref2.edit();
                        editor.putBoolean("Permission_touch", true);
                        editor.commit();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                    } else {
                        if (Permission_touch == true) {
                            //3번째부터
                            Toast.makeText(getApplicationContext(), "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                        } else {
                            //1번째 퍼미션 시도
                        }
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }//팝업 이어갈 예정
                } else {
                    //퍼미션 허용받으면 이쪽입니다~~~
                    SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
                    boolean first_touch = pref.getBoolean("isFirst", false);

                    System.out.println("허용");
                    //이전 선택된 연락처 기록 가져오기
                    SharedPreferences spfPersonInfo = getSharedPreferences("personInfo", MODE_PRIVATE);
                    String name = spfPersonInfo.getString("name", "");
                    String phoneNumber = spfPersonInfo.getString("phoneNumber", "");
                    int personId = spfPersonInfo.getInt("position", -1);

                    Log.d("이름", name);
                    Log.d("번호", phoneNumber);

//                    if(name.equals("") && phoneNumber.equals("")){    //연락처 선택한 기록이 없을 시 연락처 추가하기로 이동 (저장된 연락처 확인하고 이것도 없으면 추가하기로 이동하는게 낫지 않을까요?)
//                        Intent intent = new Intent(getApplicationContext(), AddPhoneBookActivity.class);
//                        startActivity(intent);
//                    }


                    if (name.equals("") || phoneNumber.equals("")) {
                        //앱 최초 실행시 하고 싶은 작업
                        View dialogView = getLayoutInflater().inflate(R.layout.first_popup, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setView(dialogView);
                        final AlertDialog alertDialog = builder.create();
                        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                        InsetDrawable inset = new InsetDrawable(back, 24);
                        alertDialog.getWindow().setBackgroundDrawable(inset);
                        alertDialog.setCanceledOnTouchOutside(true);//없어지지 않도록 설정
                        alertDialog.show();

                        TextView noButton = alertDialog.findViewById(R.id.first_no_button);
                        noButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                            }
                        });
                        TextView yesButton = alertDialog.findViewById(R.id.first_yes_button);
                        yesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), EmergencyCallActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "연락처를 클릭해서 문자를 전송할 연락처를 선택해주세요!", Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean("isFirst", true);
                                editor.commit();
                                alertDialog.dismiss();


                            }
                        });
//                        alertDialog.show();
                    } else {

                        View dialogView = getLayoutInflater().inflate(R.layout.mesaage_dialog, null);
                        TextView set11;
                        set11 = (TextView) dialogView.findViewById(R.id.text_dialog_);
                        set11.setText(name + set11.getText());


                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setView(dialogView);
                        final AlertDialog alertDialog = builder.create();
                        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                        InsetDrawable inset = new InsetDrawable(back, 24);
                        alertDialog.getWindow().setBackgroundDrawable(inset);
                        alertDialog.setCanceledOnTouchOutside(true);//없어지지 않도록 설정
                        alertDialog.show();

//                        String nameText = TextView(mContext)
//                        nameText.S(name);
//                        text.setText(nameText.text.toString() + text.text.toString());

                        TextView noButton = alertDialog.findViewById(R.id.tv_no_dialog);
                        noButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                            }
                        });
                        TextView yesButton = alertDialog.findViewById(R.id.tv_yes_dialog);
                        yesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendSms();
                                Toast.makeText(getApplicationContext(), "긴급 문자가 전송되었습니다!", Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();

                            }
                        });
                        /*                        alertDialog.show();*/
                    }
                }

            }
        });

        ImageButton Report_button = findViewById(R.id.repot_button);
        Report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //현 위치 location 받아와서 서버로 넘겨줘야함
                //넘겨줄 것 : 사진, text, 닉네임, 좌표, 신고일자

                //카메라 권한요청, 내 파일 권한 요청 필요

                //카메라 화면이 먼저 나옴
                //사진 찍고
                //report detail 화면 띄워서
                //입력받고 전송하기 버튼 누르면

                //현 위치 : locationSource

                //아니 여기 왜 버튼이 안눌려렬렬려려려려려려려려려렬
                //버튼 init버튼인가 밑에 함수에서 설정하면 됩니다^^
//zzzzz

/*
                Log.d("camera", "Reportbutton clicked");

                Intent intent = null;
                Log.d("camera", "clicked");
                setCamera(intent);
*/
                //0719

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                String address = getSimpleCurrentAddress(getCurrentAddress(latitude, longitude));

                SimpleDateFormat timeForServer = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String sReportDate = timeForServer.format(new Date());

                SimpleDateFormat timeForClient = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
                String cReportDate = timeForClient.format(new Date());

                System.out.println("현재 위치 : "+address);

                Intent reportIntent = new Intent(getApplicationContext(), Report_detail.class);

                reportIntent.putExtra("reportLat",currentPosition.latitude);    //서버 위도 경도
                reportIntent.putExtra("reportLng",currentPosition.longitude);
                // 이거 값 이상하면 바로 윗줄 latitude,longitude로 주기
                reportIntent.putExtra("address",address);   //사용자 화면 주소
                reportIntent.putExtra("sReportDate",sReportDate);
                reportIntent.putExtra("cReportDate",cReportDate);





                startActivity(reportIntent);


/*
                System.out.println("현재 위치 : "+address);
                reportIntent.putExtra("reportLocation","우리집 내방 이불밖");
                reportIntent.putExtra("reportTime","2022년 07월 19일");
*/

            }
        });

        binding.layoutToolBar.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            //menu 클릭 시 open drawer
                binding.drawerLayout.openDrawer(GravityCompat.START);
                isDrawerOpen = true;
            }
        });

        binding.layoutToolBar.ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override

            //필터
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
                startActivity(intent);
                isFilter = true;

            }
        });


        View header = binding.navigationView.getHeaderView(0);
        ImageView image = header.findViewById(R.id.iv_close);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {        // X 클릭 시 close drawer
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_notification) {
//                 binding.drawerLayout.closeDrawer(GravityCompat.START); //열려있는 메뉴판 닫고 화면 전환

                   Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                   startActivity(intent);
               }
               else if (item.getItemId() == R.id.nav_call) {
                   Intent intent = new Intent(getApplicationContext(), EmergencyCallActivity.class);
                   startActivity(intent);
               }
               else if (item.getItemId() == R.id.nav_report) {
/*
                   Intent intent = null;
                   Log.d("camera","clicked");
                   setCamera(intent);
*/
               }
               else if (item.getItemId() == R.id.nav_book) {

               }
               else if (item.getItemId() == R.id.nav_review) {

               }
               else if (item.getItemId() == R.id.nav_oss) {
                   Intent intent = new Intent(getApplicationContext(), OssActivity.class);
                   startActivity(intent);
               }
               return true;
           }
       });
    }



    //필터 닫기~~
    private void Filter_close() {
        SharedPreferences total1 = getSharedPreferences("total", Activity.MODE_PRIVATE);
        SharedPreferences hos2 = getSharedPreferences("hos2", Activity.MODE_PRIVATE);
        SharedPreferences fac3 = getSharedPreferences("fac3", Activity.MODE_PRIVATE);
        SharedPreferences charge4 = getSharedPreferences("charge4", Activity.MODE_PRIVATE);
        SharedPreferences wheel5 = getSharedPreferences("wheel5", Activity.MODE_PRIVATE);
        SharedPreferences ele6 = getSharedPreferences("ele6", Activity.MODE_PRIVATE);
        SharedPreferences bike7 = getSharedPreferences("bike7", Activity.MODE_PRIVATE);
        SharedPreferences slope8 = getSharedPreferences("slope8", Activity.MODE_PRIVATE);
        SharedPreferences danger9 = getSharedPreferences("danger9", Activity.MODE_PRIVATE);

        ImageButton filterclose = findViewById(R.id.filter_close);
        filterclose.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                finish();
                if(total1_.get("total")==null){
                    SharedPreferences.Editor editor = total1.edit();
                    editor.putBoolean("total",true);
                    editor.commit();
                }else if(total1.getAll().equals(total1_)==false){
                    SharedPreferences.Editor editor = total1.edit();
                    editor.putBoolean("total",(boolean)total1_.get("total"));
                    editor.commit();
                }if(hos2_.get("total")==null){
                    SharedPreferences.Editor editor = hos2.edit();
                    editor.putBoolean("total",true);
                    editor.commit();
                }else if(hos2.getAll().equals(hos2_)==false){
                    SharedPreferences.Editor editor = hos2.edit();
                    editor.putBoolean("total",(boolean)hos2_.get("total"));
                    editor.commit();
                }

                if(fac3_.get("total")==null) {
                    SharedPreferences.Editor editor = fac3.edit();
                    editor.putBoolean("total", true);
                    editor.commit();
                }else if(fac3.getAll().equals(fac3_)==false){
                    SharedPreferences.Editor editor = fac3.edit();
                    editor.putBoolean("total",(boolean)fac3_.get("total"));
                    editor.commit();
                }


                if(charge4_.get("total")==null){
                    SharedPreferences.Editor editor = charge4.edit();
                    editor.putBoolean("total",true);
                    editor.commit();
                }else if(charge4.getAll().equals(charge4_)==false){
                    SharedPreferences.Editor editor = charge4.edit();
                    editor.putBoolean("total",(boolean)charge4_.get("total"));
                    editor.commit();
                }
                if(wheel5_.get("total")==null){
                    SharedPreferences.Editor editor = wheel5.edit();
                    editor.putBoolean("total",true);
                    editor.commit();
                }else if(wheel5.getAll().equals(wheel5_)==false){
                    SharedPreferences.Editor editor = wheel5.edit();
                    editor.putBoolean("total",(boolean)wheel5_.get("total"));
                    editor.commit();
                }
                if(ele6_.get("total")==null){
                    SharedPreferences.Editor editor = ele6.edit();
                    editor.putBoolean("total",true);
                    editor.commit();
                }else if(ele6.getAll().equals(ele6_)==false){
                    SharedPreferences.Editor editor = ele6.edit();
                    editor.putBoolean("total",(boolean)ele6_.get("total"));
                    editor.commit();
                }
                if(bike7_.get("total")==null){
                    SharedPreferences.Editor editor = bike7.edit();
                    editor.putBoolean("total",true);
                    editor.commit();
                }else if(bike7.getAll().equals(bike7_)==false){
                    SharedPreferences.Editor editor = bike7.edit();
                    editor.putBoolean("total",(boolean)bike7_.get("total"));
                    editor.commit();
                }

                if(slope8_.get("total")==null){
                    SharedPreferences.Editor editor = slope8.edit();
                    editor.putBoolean("total",true);
                    editor.commit();
                }else if(slope8.getAll().equals(slope8_)==false){
                    SharedPreferences.Editor editor = slope8.edit();
                    editor.putBoolean("total",(boolean)slope8_.get("total"));
                    editor.commit();
                }
                if(danger9_.get("total")==null){
                    SharedPreferences.Editor editor = danger9.edit();
                    editor.putBoolean("total",true);
                    editor.commit();
                }else if(danger9.getAll().equals(danger9_)==false){
                    SharedPreferences.Editor editor = danger9.edit();
                    editor.putBoolean("total",(boolean)danger9_.get("total"));
                    editor.commit();
                }



            }
        });
    }

    // xml 가져오는 코드
//    private void setUpMap(){
//        XmlApi parser = new XmlApi();
//        ArrayList<MapPoint> mapPoint = new ArrayList<MapPoint>();
//    try {
//
//        mapPoint = parser.apiParserSearch();
//    } catch (Exception e) {
//        System.out.println(3333);
//        e.printStackTrace();
//    }
//    for (int i =0; i<mapPoint.size(); i++){
//        for (MapPoint entity:mapPoint){
//            AccidentCircle(mapPoint.get(i).getLatitude(), mapPoint.get(i).getLongitude());
//        }
//    }
//    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("온푸즈");
    }


    private void removeMarkerAll() {
        for (Marker marker : TotalmarkerList) {
            marker.setMap(null); // 삭제
        }

    }

    //의료기관setMarker_facility_delete
    private void setMarker_hos() {
        for (int i = 0; i < total_list.size(); i++) {
            JsonApi_total.total_item item = total_list.get(i);
            setMarker_facility(Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng()), "hos", naverMap);
        }
        //TotalmarkerList.add(setMarker_facility(Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng()),"hos",naverMap));//클러스터링코드
        System.out.println("setMarker_hos");
        return;
    }


    //충전기
    private void setMarker_Charge() {
        for (int i = 0; i < charge_list.size(); i++) {
            JsonApi_charge.charge_item item = charge_list.get(i);
            setMarker_facility(Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng()), "charge", naverMap);
            // cluster_item2.add(new NaverItem((Double.parseDouble(item.getLat())), Double.parseDouble(item.getLng())));//클러스터링코드
            System.out.println("setMarker_charge");
        }
        return;
    }

    //위험지역
    private void setMarker_danger() {
        for (int i = 0; i < danger_list.size(); i++) {
            JsonApi_danger.danger_item item = danger_list.get(i);
            System.out.println("setMarker_danger");
            setMarker_facility(Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng()), "danger", naverMap);
            // cluster_item2.add(new NaverItem((Double.parseDouble(item.getLat())), Double.parseDouble(item.getLng())));//클러스터링코드
        }
        return;
    }



    //자전거 사고 다발지역 만들기
    private void drawMarker_bike() {
        for (int i = 0; i < bike_list.size(); i++) {
            JsonApi_bike.bike_item item = bike_list.get(i);
            AccidentCircle((Double.parseDouble(item.getLat())), Double.parseDouble(item.getLng()),"자전거 사고다발 지역");
            //cluster_item.add(new NaverItem((Double.parseDouble(item.getLat())), Double.parseDouble(item.getLng())));//클러스터링코드
        }
        return;
    }

    //승강기
    private void drawMarker_ele() {
        for (int i = 0; i < ele_list.size(); i++) {
            JsonApi_ele.ele_item item = ele_list.get(i);
            setMarker_wheel((Double.parseDouble(item.getLat())), Double.parseDouble(item.getLng()),"ele",naverMap,"외부 승강기");
            //cluster_item.add(new NaverItem((Double.parseDouble(item.getLat())), Double.parseDouble(item.getLng())));//클러스터링코드
        }
        return;
    }




    //급경사로 지역 만들기
    private void drawMarker_slope() {
        for (int i =0 ; i< slope_list.size(); i++){
            JsonApi_slope.slope_item item = slope_list.get(i);
            AccidentCircle_slope((Double.parseDouble(item.getLat())), Double.parseDouble(item.getLng()),"급경사 지역");
                     //cluster_item.add(new NaverItem((Double.parseDouble(item.getLat())), Double.parseDouble(item.getLng())));//클러스터링코드
        }
        return;
    }



    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();

    }



    private void AccidentCircle(double x, double y, String z){
        CircleOverlay circle = new CircleOverlay();
        circle.setCenter(new LatLng(x, y));
        circle.setRadius(30);
        circle.setColor(Color.parseColor("#30FF7B00"));
        circle.setOutlineColor(Color.parseColor("#30FF7B00"));
        circle.setMinZoom(12);//줌 설정
        circle.setMap(naverMap);

        InfoWindow infoWindow = new InfoWindow();
        Marker marker = new Marker();

        marker.setPosition(new LatLng(x,y));
        marker.setMinZoom(12);//줌 설정

        marker.setIcon(OverlayImage.fromResource(R.drawable.danger_location_yellow));
        marker.setWidth(80);
        marker.setHeight(80);
        marker.setMap(naverMap);

        marker.setTag(z);
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                // 정보 창이 열린 마커의 tag를 텍스트로 노출하도록 반환
                return (CharSequence) infoWindow.getMarker().getTag();
            }
        });
        infoWindow.setAlpha(0.8f);
        Overlay.OnClickListener listener = overlay -> {
            naverMap.setOnMapClickListener((coord, point) -> {
                infoWindow.close();
            });
            if (marker.getInfoWindow() == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker);
                Handler handler = new Handler();
                if (marker.getInfoWindow() != null) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            infoWindow.close();
                        }
                    }, 3000);    //3초 동안 딜레이
                }
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close();
            }

            return true;
        };
        marker.setOnClickListener(listener);

    }


             //자전거 사고 다발지역 마커
             private void AccidentCircle_slope(double x, double y, String z){
                 InfoWindow infoWindow = new InfoWindow();
                 Marker marker = new Marker();
                 marker.setPosition(new LatLng(x,y));
                 marker.setMinZoom(12);//줌 설정
                 marker.setIcon(OverlayImage.fromResource(R.drawable.danger_location_yellow));
                 marker.setWidth(80);
                 marker.setHeight(80);
                 marker.setMap(naverMap);

                 marker.setTag(z);
                 infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
                     @NonNull
                     @Override
                     public CharSequence getText(@NonNull InfoWindow infoWindow) {
                         // 정보 창이 열린 마커의 tag를 텍스트로 노출하도록 반환
                         return (CharSequence)infoWindow.getMarker().getTag();
                     }
                 });
                 infoWindow.setAlpha(0.8f);
                 Overlay.OnClickListener listener = overlay -> {
                     naverMap.setOnMapClickListener((coord, point) -> {
                         infoWindow.close();
                     });
                     if (marker.getInfoWindow() == null) {
                         // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                         infoWindow.open(marker);
                         Handler handler = new Handler();
                         if(marker.getInfoWindow() != null){
                             handler.postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     infoWindow.close();
                                 }
                             },3000);	//3초 동안 딜레이
                         }
                     } else {
                         // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                         infoWindow.close();
                     }

                     return true;
                 };
                 marker.setOnClickListener(listener);

             }

}