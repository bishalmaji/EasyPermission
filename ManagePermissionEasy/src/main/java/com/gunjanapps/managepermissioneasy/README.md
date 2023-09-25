# EasyPermission Library

## OverView-

requestPermission(String permissions): Request specified permissions.
showPermissionResultDialog(String[] permissions, int[] grantResults, String title, String message, int background): Show a custom dialog with permission results.


## Usage-

## 1.Initialize the PermissionManager in your activity:
    PermissionManager permissionManager = new PermissionManager(this);

## 2.Request permissions:
    permissionManager.requestPermission("camera,location");
* A comma-separated string of permission types. Supported types: "camera", "storage", "location", "sms".


## 3.Handle permission results by overriding activity's onRequestPermissionsResult method.
## And add the following example code, given below to get the result of the requested permission:
    if (permissionManager.showPermissionResultDialog(permissions, grantResults, title, message, R.drawable.ic_launcher_background)) {
        // Permissions granted
    } else {
        // Permissions denied
    }
