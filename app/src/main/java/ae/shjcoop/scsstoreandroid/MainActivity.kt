package ae.shjcoop.scsstoreandroid

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import org.json.JSONObject
import android.R.string.cancel
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.view.ViewGroup
import android.view.LayoutInflater
import com.github.kittinunf.fuel.Fuel
import java.io.File
import java.security.AccessController.getContext
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.R.attr.path
import android.os.StrictMode
import com.github.kittinunf.fuel.core.Request
import java.nio.file.FileSystem
import android.app.DownloadManager
import android.content.Context
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.widget.ProgressBar
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.list_layout.*
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

class MainActivity : AppCompatActivity() {



    var apps:MutableList<App> = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)



        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:com.symbol.enterprisehomescreen"));
        startActivity(intent);



        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())


        Fabric.with(this, Crashlytics())
        //check for updates

if (isStoragePermissionGranted()){

    fetchAllApps()
}


    }

fun  fetchAllApps(){



    FuelManager.instance.basePath = "https://mobileapi.shjcoop.ae/MobileAppServices/AppStoreAndroid"
    "/apps".httpGet().responseString { request, response, result ->
        //make a GET to http://httpbin.org/get and do something with response
        val (data, error) = result



        if (error == null) {
            //do something when success
            var json = data.toString()
            val jsonObj = JSONObject(json)
            var result = jsonObj.getBoolean("Result")

            if (result){

                var jsonApps = jsonObj.getJSONArray("Apps")


                apps = mutableListOf()
                for (i in 0..(jsonApps.length() - 1) ){

                    var jsonApp =  jsonApps.getJSONObject(i)

                    var app = App(jsonApp.getString("NAME"),jsonApp.getInt("ID"),jsonApp.getString("BUNDLEID"),jsonApp.getString("PATH"),jsonApp.getString("CURRENTVERSION").toFloat(),jsonApp.getString("MINIMUMOSREQUIRED").toFloat() , jsonApp.getInt("ISTABLETSUPPORTED") == 1 ,jsonApp.getInt("ISMOBILESUPPORTED") == 1,jsonApp.getString("CATEGORY"))


                    if (app.appID == 1005) {
                        checkForUpdates(app.currentVersion.toString())

                    }
                    else {

                        apps.add(app)

                    }
                }



            }

            var listview = findViewById<ListView>(R.id.applist) as ListView
            var adaptor = CustomListAdaptor(this,apps)
            listview.adapter = adaptor



            isStoragePermissionGranted()


        } else {
            Log.d("faisal",error.localizedMessage)


        }
    }

    }

       fun appInstalledOrNot(uri:String):Boolean{

    var pm:PackageManager = applicationContext.packageManager
    try {
        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
        return true;


    } catch ( e:PackageManager.NameNotFoundException) {

        return false;
    }


}

    fun getSecurityCode(bundleID:String): String {

        var counter = 0;
        for (keycode in bundleID) {
            counter = counter + keycode.hashCode()
        }
        val length = bundleID.length
        val digitOne = counter%9 ;
        val digitTwo = (length * (counter % 20)) % 9
        val digitThree = (counter % 4)
        val digitFour = ((counter % 9) * (counter)) % 9


        return "$digitOne$digitTwo$digitThree$digitFour"
    }


public  fun isStoragePermissionGranted():Boolean {
    if (Build.VERSION.SDK_INT >= 23) {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {

            var arr = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
           ActivityCompat.requestPermissions(this, arr, 1);
            return false;
        }
    }
    else { //permission is automatically granted on sdk<23 upon installation
        return true;
    }
}


    fun checkForUpdates(newVersion:String){

        val versionName = BuildConfig.VERSION_NAME

        if (versionName < newVersion) {
            var dialog = MaterialDialog.Builder(this)
                    .title("Updating Your App")
                    .content("Downloading...")
                    .progressIndeterminateStyle(true)
                    .progress(false, 100)
                    .show();



            dialog.setCancelable(false)

            dialog.progressBar.progress = 0

            Fuel.download("https://mobileapi.shjcoop.ae/MobileAppFileServices/Store/ANDROID/store/app.apk").destination { response, url ->
                // var file = File.createTempFile("temp", ".apk")
                val dir = File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).toString())
                File(dir, "abcd.apk")


            }.progress { readBytes, totalBytes ->
                val progress = (readBytes.toFloat() / totalBytes.toFloat() * 100.0f).toInt()

                dialog.setProgress(progress)
            }.response { req, res, result ->


                dialog.dismiss();

                val promptInstall = Intent(Intent.ACTION_VIEW)
                        .setDataAndType(Uri.parse("file:///storage/emulated/0/Download/abcd.apk"), "application/vnd.android.package-archive")

                startActivity(promptInstall)

            }

        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        fetchAllApps()
        }
        else {

            isStoragePermissionGranted()
        }
    }




}
