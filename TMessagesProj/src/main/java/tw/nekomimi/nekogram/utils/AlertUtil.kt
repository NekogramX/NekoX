package tw.nekomimi.nekogram.utils

import android.widget.Toast
import com.google.android.exoplayer2.drm.DecryptionResource
import org.telegram.messenger.ApplicationLoader

object AlertUtil {

    @JvmStatic
    fun showToast(text:String) = Toast.makeText(ApplicationLoader.applicationContext,text,Toast.LENGTH_LONG).show()

}