package com.bishal.easypermission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gunjanapps.managepermissioneasy.PermissionManager;


public class MainActivity extends AppCompatActivity {

    private PermissionManager permissionManager;
    private String title = "Storage+Camera";
    private String message = "both storage and camera permission is required";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionManager = new PermissionManager(this);
        Button requestPermissionBtn = findViewById(R.id.request_permisison_btn);
        requestPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionManager.requestPermission("camera,location");
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        This method checks all permission is granted or not, and show dialog accordingly
        if (permissionManager.showPermissionResultDialog(permissions, grantResults, title, message, R.drawable.ic_launcher_background)) {
            Toast.makeText(this, "All Permissions are granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (permissionManager != null && permissionManager.isDialogShowing()) {
            permissionManager.dismissDialog();

        }
    }

}
