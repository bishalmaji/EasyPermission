package com.gunjanapps.managepermissioneasy;

import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private final Activity activity;
    private Dialog dialog;

    public PermissionManager(Activity activity) {
        this.activity = activity;

    }

    /**
     * Request Permissions
     * <p>
     * This method requests permissions based on a comma-separated list of permission types.
     * It checks if the permissions are already granted and requests only the ones that are not.
     *
     * @param permissions A comma-separated string of permission types to request.
     */
    public void requestPermission(String permissions) {
        String[] permission_list = permissions.split(",");
        List<String> new_permission_list = new ArrayList<>();

        for (String perm : permission_list) {

            List<String> new_permissions = extractPermissions(perm);
            for (String permission : new_permissions) {

                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    new_permission_list.add(permission);
                }
            }
        }

        if (!new_permission_list.isEmpty()) {
            Log.d("PermissionManager", "requestPermission: Permissions not granted, requesting...");
            ActivityCompat.requestPermissions(activity, new_permission_list.toArray(new String[0]), 0);
        } else {
            Toast.makeText(activity, "All permission are already granted", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Request Dialog Permission
     * <p>
     * This method requests permissions based on the provided permission list.
     * It checks if the permissions are already granted and requests only the ones that are not.
     *
     * @param permissions The array of permissions to request.
     */
    private void requestDialogPermission(String[] permissions) {
        List<String> permissionList = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[0]), 0);
        } else {
            Toast.makeText(activity, "All permission are already granted", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Extract Permissions
     * <p>
     * This method takes a comma-separated string of permission types and returns a list of Android
     * permission strings corresponding to those types. It simplifies the process of requesting
     * multiple permissions based on user-defined types.
     *
     * @param permissions_list A comma-separated string of permission types.
     *                         Supported types: "camera", "storage", "location", "sms".
     * @return A list of Android permission strings based on the specified types.
     */
    private List<String> extractPermissions(String permissions_list) {
        List<String> permissions = new ArrayList<>();

        switch (permissions_list) {
            case "camera":
                permissions.add(Manifest.permission.CAMERA);
                break;
            case "storage":
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            case "location":
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                break;
            case "sms":
                permissions.add(Manifest.permission.SEND_SMS);
                permissions.add(Manifest.permission.READ_SMS);
                break;
            default:
                break;
        }

        return permissions;
    }

    /**
     * Show Permission Result Dialog
     * <p>
     * This method displays a custom dialog to inform the user about the status of requested permissions.
     * It can handle both the scenario where permissions are denied with a rationale and the scenario where
     * permissions are permanently denied and the user needs to go to the app settings to grant them.
     *
     * @param permissions  The array of permissions that were requested.
     * @param grantResults The corresponding grant results for each requested permission.
     * @param title        The title of the dialog.
     * @param message      The message to display in the dialog.
     * @param background   The resource ID for the background drawable of the dialog.
     * @return True if all permissions are granted, false otherwise.
     */
    public boolean showPermissionResultDialog(String[] permissions, int[] grantResults, String title, String message, int background) {
        boolean allPermissionsGranted = true;

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;

                // Handle the denied permission here for permissions[i]
                if (shouldShowRequestPermissionRationale(activity, permissions[i])) {
                    // Explain to the user why the permission is needed and request again if necessary
                    showCustomDenyDialog(title, message, permissions, background);
                } else {
                    // Permission is permanently denied; take the user to app settings
                    showCustomDenyDialog(title, "We need to open the app settings to grant the necessary permissions.", new String[0], background);
                }

            }
        }

        return allPermissionsGranted;
    }

    /**
     * Show Custom Deny Dialog
     * <p>
     * This method displays a custom dialog for handling denied permissions. It provides options
     * for users to understand the reason for permission denial and take action accordingly.
     *
     * @param title                      The title of the dialog.
     * @param message                    The message to display in the dialog.
     * @param permissions                The array of permissions associated with the dialog.
     * @param backgroundDrawableResource The resource ID for the background drawable of the dialog.
     */
    public void showCustomDenyDialog(String title, String message, String[] permissions, int backgroundDrawableResource) {

        if (dialog != null) {
            dialog.cancel();
            dialog.dismiss();
            dialog = null;
        }

        dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_view);
        TextView tv_message = (TextView) dialog.findViewById(R.id.dialog_msg);
        Button bt_yes = (Button) dialog.findViewById(R.id.dialogbtn_retry);
        Button bt_no = (Button) dialog.findViewById(R.id.dialogbtn_close);

        tv_message.setText(title + "\n" + message);
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissions.length == 0) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivity(intent);
                } else {
                    requestDialogPermission(permissions);
                }
                dismissDialog();
            }
        });
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        dialog.show();

    }

    public boolean isDialogShowing() {
        return dialog != null && dialog.isShowing();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
