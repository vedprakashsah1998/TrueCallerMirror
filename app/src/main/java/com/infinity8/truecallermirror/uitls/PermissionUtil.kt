import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity


/**
 * this method is used for single permission
 */
fun isPermissionGranted(activity: Activity, permission: String) =
    ContextCompat.checkSelfPermission(
        activity,
        permission
    ) == PackageManager.PERMISSION_GRANTED

/**
 * this method is used for checking multiple permission
 *  if (isPermissionGranted(
 *                 arrayOf(
 *                     android.Manifest.permission.CAMERA,
 *                     android.Manifest.permission.RECORD_AUDIO,
 *                     android.Manifest.permission.ACCESS_COARSE_LOCATION,
 *                     android.Manifest.permission.ACCESS_FINE_LOCATION
 *                 )
 *             )
 *         ) {
 *             startCamera()
 *         } else {
 *             showPermissionDeniedDialog()
 *
 */

fun Activity.isPermissionGranted(permissions: Array<String>): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}

fun FragmentActivity.requestPermission(
    onPermissionResult: (Boolean) -> Unit
) =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        onPermissionResult(isGranted)
    }

/**
 *     private val requestPermissionLauncher =
 *         this@MainActivity.requestMultiplePermission(allowedPermission = {
 *             if (!isPermissionRequested) {
 *                 startCamera()
 *             } else {
 *                 isPermissionRequested = false
 *             }
 *         }, deniedPermission = {
 *             showPermissionDeniedDialog()
 *
 *         })
 */

fun FragmentActivity.requestMultiplePermission(
    allowedPermission: () -> Unit,
    deniedPermission: () -> Unit
) =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsResult ->
        if (permissionsResult[android.Manifest.permission.READ_CALL_LOG] == true && permissionsResult[android.Manifest.permission.READ_CONTACTS] == true &&
            permissionsResult[android.Manifest.permission.READ_PHONE_STATE] == true&&
            permissionsResult[android.Manifest.permission.ANSWER_PHONE_CALLS] == true
        ) {
            allowedPermission()
        } else {
            deniedPermission()
        }
    }
