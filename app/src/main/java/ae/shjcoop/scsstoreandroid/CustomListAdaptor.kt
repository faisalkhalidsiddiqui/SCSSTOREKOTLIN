package ae.shjcoop.scsstoreandroid

import ae.shjcoop.scsstoreandroid.R.id.editText
import ae.shjcoop.scsstoreandroid.R.id.parent
import android.content.Context
import android.content.pm.PackageManager
import android.media.Image
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.appcode.view.*
import org.w3c.dom.Text
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.support.v4.content.ContextCompat.startActivity
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import java.io.File
import android.widget.LinearLayout
import android.R.attr.versionName
import android.content.pm.PackageInfo
import android.app.AlertDialog
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.list_layout.view.*
import android.os.Looper
import android.text.InputType
import android.view.*
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.view.inputmethod.InputMethodManager


/**
 * Created by faisalkhalid on 10/31/17.
 */

class CustomListAdaptor(var context: Context, var apps:MutableList<App>):BaseAdapter(){
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {


 var view:View? = p1
        view = LayoutInflater.from(context).inflate(R.layout.list_layout, null,false)



        var appName:TextView
        var appIcon:ImageView
        var category:TextView
        var button:Button
        var progressBar: ProgressBar



        appName = view.findViewById(R.id.AppName)
        appName.setText(apps[p0].name)


        category = view.findViewById(R.id.AppCategory)
        category.setText(apps[p0].category)

        progressBar = view.findViewById(R.id.progressBar)
        apps[p0].progressBar = progressBar


        if(apps[p0].isDownloading == false) {
            apps[p0].progressBar!!.visibility = ProgressBar.GONE


        }
        else {
            apps[p0].progressBar!!.visibility = ProgressBar.VISIBLE

        }




        appIcon = view.findViewById(R.id.AppIcon)

        Picasso.with(context).load(apps[p0].path+"Icon.png").into(appIcon);


        button = view.findViewById(R.id.AppActionButton)

        if (appInstalledOrNot(apps[p0].bundleID)){
            button.setText("Open")

        }
        else {

            button.setText("Install")

        }




        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {





                if(button.text == "Open") {

                    val pm = context.packageManager
                    val appStartIntent = pm.getLaunchIntentForPackage(apps[p0].bundleID)
                    if (null != appStartIntent) {
                        context.startActivity(appStartIntent)
                    }


                    try {
                        val pInfo = context.getPackageManager().getPackageInfo(apps[p0].bundleID, 0)
                        val version = pInfo.versionName
                        Log.d("faisal",apps[p0].bundleID+ " -version $version")



                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }



                }
                else if (button.text == "Install") {





                    val alertDialog = AlertDialog.Builder(context).create()
                    val input = EditText(context)
                    input.inputType = InputType.TYPE_CLASS_NUMBER

                //    alertDialog.setTitle("title")
                    alertDialog.setMessage("Please Enter App Access Token:")



                    alertDialog.setView(input)

                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);






                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Verify", {
                        dialogInterface, i ->

                        var validCode =   input.text.toString()

if (validCode.equals(getSecurityCode(apps[p0].bundleID),ignoreCase = true)) {






    apps[p0].isDownloading = true


    Fuel.download(apps[p0].path + "app.apk").destination { response, url ->


        val dir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).toString())
        File(dir, "abcd.apk")


    }.progress { readBytes, totalBytes ->
        val progress = (readBytes.toFloat() / totalBytes.toFloat() * 100.0f).toInt()


        apps[p0].progressBar!!.progress = progress

    }.response { req, res, result ->


apps[p0].isDownloading = false
        apps[p0].progressBar!!.visibility = ProgressBar.GONE
        apps[p0].progressBar!!.progress = 0


        val promptInstall = Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.parse("file:///storage/emulated/0/Download/abcd.apk"), "application/vnd.android.package-archive")

        // context.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(promptInstall)

    }


}
                        else{

    Toast.makeText(context,"Invalid Code. Please contact IT Department!",Toast.LENGTH_LONG).show()
}


                    })


alertDialog.show()

                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(input, InputMethodManager.SHOW_FORCED)

                }

            }
        })

        return view
    }

    override fun getItem(p0: Int): Any {

    return apps.get(p0)
    }

    override fun getItemId(p0: Int): Long {
    return p0.toLong()
    }

    override fun getCount(): Int {
    return apps.count()


    }



    fun appInstalledOrNot(uri:String):Boolean{

        var pm: PackageManager = context.packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;


        } catch ( e: PackageManager.NameNotFoundException) {

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

}

