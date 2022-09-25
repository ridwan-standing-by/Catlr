package com.ridwanstandingby.catlr

import android.app.Application
import com.facebook.drawee.backends.pipeline.BuildConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.ridwanstandingby.catlr.di.KoinInjector
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CatlrApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
        initFresco()
    }

    private fun initKoin() {
        startKoin {
            allowOverride(true)
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@CatlrApplication)
            modules(KoinInjector.buildModule())
        }
    }

    private fun initFresco() {
        val pipelineConfig =
            OkHttpImagePipelineConfigFactory
                .newBuilder(this, OkHttpClient.Builder().build())
                .setDiskCacheEnabled(true)
                .setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true)
                .build()

        Fresco.initialize(this, pipelineConfig)
    }
}