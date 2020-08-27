package com.webasyst.x

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.webasyst.x.auth.WebasystAuthService
import com.webasyst.x.auth.WebasystAuthStateManager
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationServiceDiscovery

class MainActivity : AppCompatActivity(), WebasystAuthStateManager.AuthStateObserver {
    private val viewModel by lazy {
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }
    private val stateStore by lazy(LazyThreadSafetyMode.NONE) {
        WebasystAuthStateManager.getInstance(this)
    }
    private val webasystAuthService by lazy(LazyThreadSafetyMode.NONE) {
        WebasystAuthService.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = intent
        if (null != intent) {
            when (intent.action) {
                ACTION_UPDATE_AFTER_AUTHORIZATION -> {
                    val response = AuthorizationResponse.fromIntent(intent)
                    val e = AuthorizationException.fromIntent(intent)
                    stateStore.updateAfterAuthorization(response, e)

                    if (null != response) {
                        WebasystAuthService.getInstance(this).performTokenRequest(response.createTokenExchangeRequest())
                    }
                }
            }
        }

        viewModel.authState.observe(this, { state ->
            val navController = findNavController(R.id.navRoot)
            if (state.isAuthorized) {
                if (navController.currentDestination?.id == R.id.authFragment) {
                    println("navigating to list")
                    navController.navigate(R.id.action_authFragment_to_installationListFragment)
                }
            } else {
                if (navController.currentDestination?.id != R.id.authFragment) {
                    println("navigating to auth")
                    navController.setGraph(R.navigation.nav_graph)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val logoffMenuItem = menu.findItem(R.id.logoff)
        viewModel.authState.observe(this, {
            logoffMenuItem.isEnabled = it.isAuthorized
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.logoff -> {
            webasystAuthService.logoff()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        stateStore.addObserver(this)
    }

    override fun onPause() {
        super.onPause()
        stateStore.removeObserver(this)
    }

    override fun onChange(state: AuthState) {
        viewModel.setAuthState(state)
    }

    fun authorize() {
        val authRequest = webasystAuthService.createAuthorizationRequest()
        val intent = createPostAuthorizationIntent(authRequest, null)
        webasystAuthService.authorize(authRequest, intent)
    }

    private fun createPostAuthorizationIntent(
        request: AuthorizationRequest,
        discoveryDoc: AuthorizationServiceDiscovery?
    ) : PendingIntent {
        val intent = Intent(this, this.javaClass)
        intent.action = ACTION_UPDATE_AFTER_AUTHORIZATION
        if (null != discoveryDoc) {
            intent.putExtra(WebasystAuthService.EXTRA_AUTH_SERVICE_DISCOVERY, discoveryDoc.docJson.toString())
        }
        return PendingIntent.getActivity(this, request.hashCode(), intent, 0)
    }

    companion object {
        internal const val ACTION_UPDATE_AFTER_AUTHORIZATION = "update_post_auth"
    }

}
