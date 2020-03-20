package nekox

interface GcmInterface {

    fun initGcmService()

    companion object {

        @JvmStatic
        lateinit var INSTANCE: GcmInterface

    }

}