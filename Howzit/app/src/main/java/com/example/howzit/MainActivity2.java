package com.example.howzit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.flarebit.flarebarlib.FlareBar;
import com.flarebit.flarebarlib.Flaretab;
import com.flarebit.flarebarlib.TabEventObject;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;


public class MainActivity2 extends AppCompatActivity implements LocationListener {
    private Location lastLocation;
    private Location currentLocation;
    LocationManager locationManager;
    ParseGeoPoint currentUserLocation;
    private float radius;
    private static final float METERS_PER_FEET = 1000.0f;
    // Conversion from kilometers to meters
    private static final int METERS_PER_KILOMETER = 1000;
    // Maximum results returned from a Parse query
    private static final int MAX_POST_SEARCH_RESULTS = 20;
    // Milliseconds per second
    private ProgressDialog progressDialog;
    private static final long UPDATE_INTERVAL = 10 * 1000; // 10 seconds
    private static final long FASTEST_INTERVAL = 5 * 1000; // 5 seconds
    public TextView empty_text;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View customToolbarView = getLayoutInflater().inflate(R.layout.custom_toolbar_layout, null);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar_layout);

        checkForNewNotifications();

        SwipeRefreshLayout swipeRefreshLayout= findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPosts();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000); // Adjust the delay time as needed

            }
        });

      //Handle hot and new button
        AppCompatButton new_button = customToolbarView.findViewById(R.id.new_button);
        AppCompatButton hot_button = customToolbarView.findViewById(R.id.hot_button);

        new_button.setOnClickListener(
                v -> getPosts());

        hot_button.setOnClickListener(
                v -> getPostsT());

       radius = App.getSearchDistance();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
         empty_text = findViewById(R.id.empty_text);
         recyclerView =  findViewById(R.id.recyclerView);

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));

        progressDialog = new ProgressDialog(MainActivity2.this);
        initMainActivityControls();

        currentUserLocation = null;
        lastLocation = null;
        currentLocation = null;

       getlocation();
        //getPosts();
    }

    private void checkForNewNotifications() {

        final FlareBar bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setBarBackgroundColor(Color.parseColor("#3F51B5"));
        ArrayList<Flaretab> tabs = new ArrayList<>();
        tabs.add(new Flaretab(getResources().getDrawable(R.drawable.android_settings),"Settings","#FFFFFF"));
        tabs.add(new Flaretab(getResources().getDrawable(R.drawable.icons8_bell),"Notifications","#FFFFFF"));
        tabs.add(new Flaretab(getResources().getDrawable(R.drawable.icons8_person),"Profile ","#FFFFFF"));
        bottomBar.setTabList(tabs);
        bottomBar.setTabList(tabs);
        bottomBar.attachTabs(MainActivity2.this);

        //HERE
       ParseQuery<HowzitPost> query = ParseQuery.getQuery(HowzitPost.class);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("notify", true); // Check for posts with Notify field set to true
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (count > 0) {
                    // Set the icon to the one indicating new notificationsa
                if (e == null) {
                    // If count is greater than 0, there are new notifications
                        tabs.add(new Flaretab(getResources().getDrawable(R.drawable.notification_bell), "Notifications", "#FF0000"));
                    } else {
                        // If count is 0, there are no new notifications
                        // Set the icon to the default one
                       tabs.add(new Flaretab(getResources().getDrawable(R.drawable.icons8_bell), "Notifications", "#FFFFFF"));
                    }
                } else {
               /*     if (e.getMessage() != null) {
                        Toast.makeText(MainActivity2.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity2.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                    };*/
                }
            }
        });

        //HERE

        bottomBar.setTabChangedListener(new TabEventObject.TabChangedListener() {
            @Override
            public void onTabChanged(LinearLayout selectedTab, int selectedIndex, int oldIndex) {

                Intent intent;
                switch (selectedIndex) {
                    case 0: // First tab
                        intent = new Intent(MainActivity2.this, SettingsActivity.class);
                        break;
                    case 1: // Second tab
                        intent = new Intent(MainActivity2.this,NotificationActivity.class);
                        break;
                    case 2: // Second tab
                        intent = new Intent(MainActivity2.this,ProfileActivity.class);
                        break;
                    // Add more cases for other tabs as needed
                    default:
                        intent = null; // Handle other cases if necessary
                        break;
                }

                if (intent != null) {
                    startActivity(intent);
                }
             //   Toast.makeText(MainActivity2.this,"Tab "+ selectedIndex+" Selected.",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void initMainActivityControls() {
        recyclerView = findViewById(R.id.recyclerView);
        empty_text = findViewById(R.id.empty_text);
      //  openInputPopupDialogButton = findViewById(R.id.fab);
    }


    private void getPostsT() {
        //   progressDialog.show();

         Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        ParseQuery<HowzitPost> query = HowzitPost.getQuery();
        query.include("user");
        query.orderByDescending("vote");
       query.whereWithinKilometers("location", geoPointFromLocation(myLoc), radius
                * METERS_PER_FEET / METERS_PER_KILOMETER);
        query.setLimit(MAX_POST_SEARCH_RESULTS);
        query.findInBackground((objects, e) -> {
            progressDialog.dismiss();
            if (e == null) {
                //We are initializing
                loadObjects(objects);
            } else {
                  Toast.makeText(MainActivity2.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
                showAlert("Error", e.getMessage());
            }
        });
    }
    public void getlocation() {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(FASTEST_INTERVAL);

            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    currentUserLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                    currentLocation = location;

                    getPosts();
                }
            }, Looper.getMainLooper());
        } else {
            // Permission is not granted
            Toast.makeText(MainActivity2.this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
        }
    }
    private void getPosts() {
    //   progressDialog.show();
        Location myLoc = (currentUserLocation == null) ? lastLocation : currentLocation;
       if (myLoc == null) {

            // Handle the case where myLoc is null
            Toast.makeText(MainActivity2.this,
                    "Please try again after your location has been established.", Toast.LENGTH_LONG).show();
        }else {
            ParseQuery<HowzitPost> query = HowzitPost.getQuery();
            query.include("user");
            query.orderByDescending("createdAt");
           query.whereWithinKilometers("location", geoPointFromLocation(myLoc), radius
                    * METERS_PER_FEET / METERS_PER_KILOMETER);
            query.setLimit(MAX_POST_SEARCH_RESULTS);
            query.findInBackground((objects, e) -> {
                progressDialog.dismiss();
                if (e == null) {
                    //We are initializing
                    loadObjects(objects);
                } else {
                    //  Toast.makeText(MainActivity2.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
                    showAlert("Error", e.getMessage());
                }
            });
       }

    }
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    Intent intent = new Intent(MainActivity2.this, MainActivity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

  public void onLocationChanged(Location location) {
        currentLocation = location;
        if (lastLocation != null
                && geoPointFromLocation(location)
                .distanceInKilometersTo(geoPointFromLocation(lastLocation)) < 0.01) {
            // If the location hasn't changed by more than 10 meters, ignore it.
            return;
        }
        lastLocation = location;

        doListQuery();
    }
      private void doListQuery() {
        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        // If location info is available, load the data
        if (myLoc != null) {
            getPosts();
        }
    }
    private void loadObjects(List<HowzitPost> list) {

        if (list == null || list.isEmpty()) {
            empty_text.setVisibility(View.VISIBLE);
            return;
        }
         empty_text.setVisibility(View.GONE);

        PostAdapter adapter = new PostAdapter(list,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }
    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post) {
           Location myLoc = (currentUserLocation == null) ? lastLocation : currentLocation;
           // getPosts();
          if (myLoc == null) {
                Toast.makeText(MainActivity2.this,
                        "Please try again after your location has been establish.", Toast.LENGTH_LONG).show();
                return true;
            }

              Intent intent = new Intent(MainActivity2.this, PostActivity.class);

              intent.putExtra(App.INTENT_EXTRA_LOCATION, myLoc);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity2 .this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_logout) {
            ParseUser.logOut();
            // Start and intent for the dispatch activity
            Intent intent = new Intent(MainActivity2.this, Dispatch.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}