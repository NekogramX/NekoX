package tw.nekomimi.nekogram.utils

import android.content.Context
import android.widget.Toast
import com.google.android.exoplayer2.drm.DecryptionResource
import org.telegram.messenger.ApplicationLoader

object AlertUtil {

    @JvmStatic
    @JvmOverloads
    fun showToast(ctx: Context = ApplicationLoader.applicationContext,text:String) = Toast.makeText(ctx,text,Toast.LENGTH_LONG).show()

}