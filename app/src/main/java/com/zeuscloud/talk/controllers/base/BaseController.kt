/*
 * Nextcloud Talk application
 *
 * @author Andy Scherzinger
 * @author BlueLine Labs, Inc.
 * @author Mario Danic
 * Copyright (C) 2021 Andy Scherzinger (info@andy-scherzinger.de)
 * Copyright (C) 2021 BlueLine Labs, Inc.
 * Copyright (C) 2020 Mario Danic (mario@lovelyhq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zeuscloud.talk.controllers.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import autodagger.AutoInjector
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.zeuscloud.talk.R
import com.zeuscloud.talk.activities.MainActivity
import com.zeuscloud.talk.application.NextcloudTalkApplication
import com.zeuscloud.talk.application.NextcloudTalkApplication.Companion.sharedApplication
import com.zeuscloud.talk.controllers.AccountVerificationController
import com.zeuscloud.talk.controllers.ServerSelectionController
import com.zeuscloud.talk.controllers.SwitchAccountController
import com.zeuscloud.talk.controllers.WebViewLoginController
import com.zeuscloud.talk.controllers.base.providers.ActionBarProvider
import com.zeuscloud.talk.ui.theme.ViewThemeUtils
import com.zeuscloud.talk.utils.preferences.AppPreferences
import javax.inject.Inject
import kotlin.jvm.internal.Intrinsics

// TODO: check what needs to be migrated from this class to BaseActivity etc when conductor is removed
@AutoInjector(NextcloudTalkApplication::class)
abstract class BaseController(@LayoutRes var layoutRes: Int, args: Bundle? = null) : Controller(args) {
    enum class AppBarLayoutType {
        TOOLBAR, SEARCH_BAR, EMPTY
    }

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var viewThemeUtils: ViewThemeUtils

    protected open val title: String?
        get() = null

    @Suppress("Detekt.TooGenericExceptionCaught")
    protected val actionBar: ActionBar?
        get() {
            var actionBarProvider: ActionBarProvider? = null
            if (this.activity is ActionBarProvider) {
                try {
                    actionBarProvider = this.activity as ActionBarProvider?
                } catch (e: Exception) {
                    Log.d(TAG, "Failed to fetch the action bar provider", e)
                }
            }
            return actionBarProvider?.supportActionBar
        }

    init {
        @Suppress("LeakingThis")
        sharedApplication!!.componentApplication.inject(this)
        addLifecycleListener(object : LifecycleListener() {
            override fun postCreateView(controller: Controller, view: View) {
                onViewBound(view)
                actionBar?.let { setTitle() }
            }
        })
        cleanTempCertPreference()
    }

    fun isAlive(): Boolean {
        return !isDestroyed && !isBeingDestroyed
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        return inflater.inflate(layoutRes, container, false)
    }

    protected open fun onViewBound(view: View) {
        var activity: MainActivity? = null

        // if (getActivity() != null && getActivity() is MainActivity) {
        //     activity = getActivity() as MainActivity?
        //     viewThemeUtils.material.themeCardView(activity!!.binding.searchToolbar)
        //     viewThemeUtils.material.themeToolbar(activity.binding.toolbar)
        //     viewThemeUtils.material.themeSearchBarText(activity.binding.searchText)
        // }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appPreferences.isKeyboardIncognito) {
            disableKeyboardPersonalisedLearning((view as ViewGroup))
            if (activity != null) {
                disableKeyboardPersonalisedLearning(activity.binding.appBar)
            }
        }
    }

    override fun onAttach(view: View) {
        // showSearchOrToolbar()
        setTitle()
        if (actionBar != null) {
            actionBar!!.setDisplayHomeAsUpEnabled(parentController != null || router.backstackSize >= 1)
        }
        super.onAttach(view)
    }

    // open fun showSearchOrToolbar() {
    //     if (isValidActivity(activity)) {
    //         val showSearchBar = appBarLayoutType == AppBarLayoutType.SEARCH_BAR
    //         val activity = activity as MainActivity
    //
    //         if (appBarLayoutType == AppBarLayoutType.EMPTY) {
    //             hideBars(activity.binding)
    //         } else {
    //             if (showSearchBar) {
    //                 showSearchBar(activity.binding)
    //             } else {
    //                 showToolbar(activity.binding)
    //             }
    //             colorizeStatusBar(showSearchBar, activity, resources)
    //         }
    //
    //         colorizeNavigationBar(activity, resources)
    //     }
    // }
    //
    // private fun isValidActivity(activity: Activity?): Boolean {
    //     return activity != null && activity is MainActivity
    // }
    //
    // private fun showSearchBar(binding: ActivityMainBinding) {
    //     val layoutParams = binding.searchToolbar.layoutParams as AppBarLayout.LayoutParams
    //     binding.searchToolbar.visibility = View.VISIBLE
    //     binding.searchText.hint = searchHint
    //     binding.toolbar.visibility = View.GONE
    //     // layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout
    //     // .LayoutParams.SCROLL_FLAG_SNAP | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
    //     layoutParams.scrollFlags = 0
    //     binding.appBar.stateListAnimator = AnimatorInflater.loadStateListAnimator(
    //         binding.appBar.context,
    //         R.animator.appbar_elevation_off
    //     )
    //     binding.searchToolbar.layoutParams = layoutParams
    // }
    //
    // private fun showToolbar(binding: ActivityMainBinding) {
    //     val layoutParams = binding.searchToolbar.layoutParams as AppBarLayout.LayoutParams
    //     binding.searchToolbar.visibility = View.GONE
    //     binding.toolbar.visibility = View.VISIBLE
    //     viewThemeUtils.material.colorToolbarOverflowIcon(binding.toolbar)
    //     layoutParams.scrollFlags = 0
    //     binding.appBar.stateListAnimator = AnimatorInflater.loadStateListAnimator(
    //         binding.appBar.context,
    //         R.animator.appbar_elevation_on
    //     )
    //     binding.searchToolbar.layoutParams = layoutParams
    // }
    //
    // private fun hideBars(binding: ActivityMainBinding) {
    //     binding.toolbar.visibility = View.GONE
    //     binding.searchToolbar.visibility = View.GONE
    // }
    //
    // fun hideSearchBar() {
    //     val activity = activity as MainActivity?
    //     val layoutParams = activity!!.binding.searchToolbar.layoutParams as AppBarLayout.LayoutParams
    //     activity.binding.searchToolbar.visibility = View.GONE
    //     activity.binding.toolbar.visibility = View.VISIBLE
    //     layoutParams.scrollFlags = 0
    //     activity.binding.appBar.stateListAnimator = AnimatorInflater.loadStateListAnimator(
    //         activity.binding.appBar.context,
    //         R.animator.appbar_elevation_on
    //     )
    // }
    //
    // private fun colorizeStatusBar(showSearchBar: Boolean, activity: Activity?, resources: Resources?) {
    //     if (activity != null && resources != null) {
    //         if (showSearchBar) {
    //             view?.let { viewThemeUtils.platform.resetStatusBar(activity) }
    //         } else {
    //             view?.let { viewThemeUtils.platform.themeStatusBar(activity, it) }
    //         }
    //     }
    // }
    //
    // private fun colorizeNavigationBar(activity: Activity?, resources: Resources?) {
    //     if (activity != null && resources != null) {
    //         DisplayUtils.applyColorToNavigationBar(
    //             activity.window,
    //             ResourcesCompat.getColor(resources, R.color.bg_default, null)
    //         )
    //     }
    // }

    override fun onDetach(view: View) {
        super.onDetach(view)
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun setTitle() {
        if (isTitleSetable()) {
            run {
                calculateValidParentController()
            }
            actionBar!!.title = title
        }
    }

    private fun calculateValidParentController() {
        var parentController = parentController
        while (parentController != null) {
            parentController = parentController.parentController
        }
    }

    private fun isTitleSetable(): Boolean {
        return title != null && actionBar != null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            router.popCurrentController()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeStarted(changeHandler, changeType)
        if (changeType.isEnter && actionBar != null) {
            configureMenu(actionBar!!)
        }
    }

    fun configureMenu(toolbar: ActionBar) {
        Intrinsics.checkNotNullParameter(toolbar, "toolbar")
    }

    // TODO: check if this must be migrated when using activities instead of conductor
    private fun cleanTempCertPreference() {
        val temporaryClassNames: MutableList<String> = ArrayList()
        temporaryClassNames.add(ServerSelectionController::class.java.name)
        temporaryClassNames.add(AccountVerificationController::class.java.name)
        temporaryClassNames.add(WebViewLoginController::class.java.name)
        temporaryClassNames.add(SwitchAccountController::class.java.name)
        if (!temporaryClassNames.contains(javaClass.name)) {
            appPreferences.removeTemporaryClientCertAlias()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun disableKeyboardPersonalisedLearning(viewGroup: ViewGroup) {
        var view: View?
        var editText: EditText
        for (i in 0 until viewGroup.childCount) {
            view = viewGroup.getChildAt(i)
            if (view is EditText) {
                editText = view
                editText.imeOptions = editText.imeOptions or EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
            } else if (view is ViewGroup) {
                disableKeyboardPersonalisedLearning(view)
            }
        }
    }

    open val appBarLayoutType: AppBarLayoutType
        get() = AppBarLayoutType.TOOLBAR
    val searchHint: String
        get() = context.getString(R.string.appbar_search_in, context.getString(R.string.nc_app_product_name))

    companion object {
        private val TAG = BaseController::class.java.simpleName
    }
}
