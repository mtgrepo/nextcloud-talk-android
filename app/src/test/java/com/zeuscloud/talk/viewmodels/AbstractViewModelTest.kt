package com.zeuscloud.talk.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.BeforeClass
import org.junit.Rule

open class AbstractViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            RxJavaPlugins.setIoSchedulerHandler {
                Schedulers.trampoline()
            }
            RxJavaPlugins.setComputationSchedulerHandler {
                Schedulers.trampoline()
            }
            RxJavaPlugins.setNewThreadSchedulerHandler {
                Schedulers.trampoline()
            }

            RxAndroidPlugins.setInitMainThreadSchedulerHandler {
                Schedulers.trampoline()
            }
        }
    }
}
