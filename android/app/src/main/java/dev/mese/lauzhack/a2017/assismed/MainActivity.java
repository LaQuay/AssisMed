package dev.mese.lauzhack.a2017.assismed;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = HomeFragment.newInstance();
                    ft.replace(android.R.id.content, fragment, "");
                    ft.commit();
                    return true;
                case R.id.navigation_chat:
                    fragment = ChatFragment.newInstance();
                    ft.replace(android.R.id.content, fragment, "");
                    ft.commit();
                    return true;
                case R.id.navigation_about:
                    fragment = AboutFragment.newInstance();
                    ft.replace(android.R.id.content, fragment, "");
                    ft.commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        setElements();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_home);
    }

    private void checkPermissions() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        for (int i = 0; i < report.getDeniedPermissionResponses().size(); ++i) {
                            Log.e(TAG, "PermissionDenied: " + report.getDeniedPermissionResponses().get(i).getPermissionName());
                        }
                        for (int i = 0; i < report.getGrantedPermissionResponses().size(); ++i) {
                            Log.e(TAG, "PermissionGranted: " + report.getGrantedPermissionResponses().get(i).getPermissionName());
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
    }

    public void setElements() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
    }
}
