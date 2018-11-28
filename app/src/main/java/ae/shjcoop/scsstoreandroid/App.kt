package ae.shjcoop.scsstoreandroid

import android.view.View
import android.widget.ProgressBar

/**
 * Created by faisalkhalid on 10/30/17.
 */
class App (name:String, appID:Int, bundleID:String, path:String, currentVersion:Float, minimumOSRequired: Float, isTabletSupported:Boolean, isMobileSupported:Boolean, category:String){
    var name:String = name
    val appID:Int = appID
    val bundleID:String = bundleID
    val path:String = path
    val currentVersion:Float = currentVersion
    val minimumOSRequired:Float = minimumOSRequired
    val isTabletSupported:Boolean = isTabletSupported
    val isMobileSupported:Boolean = isMobileSupported
    val category:String = category

    var isDownloading:Boolean = false

    var progressBar: ProgressBar? = null

}
