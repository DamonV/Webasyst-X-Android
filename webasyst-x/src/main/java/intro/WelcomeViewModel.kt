package com.webasyst.x.intro

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.webasyst.auth.WebasystAuthHelper
import com.webasyst.x.R
import com.webasyst.x.signin.SignInActivity
import com.webasyst.x.util.getActivity

class WelcomeViewModel : ViewModel() {
    fun onPhoneSignIn(view: View) {
        val intent = Intent(view.context, SignInActivity::class.java)
        view.context.startActivity(intent)
    }

    fun onSignIn(view: View) {
        view.getActivity()?.let { activity ->
            activity.javaClass.let { activityClass ->
                val authHelper = WebasystAuthHelper(activity)
                authHelper.signIn(activityClass)
            }
        }
    }
}
