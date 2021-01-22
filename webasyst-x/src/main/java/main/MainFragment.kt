package com.webasyst.x.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.webasyst.x.MainActivity
import com.webasyst.x.R
import com.webasyst.x.blog.BlogRootFragment
import com.webasyst.x.databinding.FragMainBinding
import com.webasyst.x.shop.orders.OrderListFragment
import com.webasyst.x.site.domainlist.DomainListFragment
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.frag_main.bottomNav

class MainFragment : Fragment() {
    private val args: MainFragmentArgs by navArgs()
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this).get(MainViewModel::class.java).also {
            it.showAddWA.value = args.showAddWA
        }
    }

    private var insecureAlert: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragMainBinding>(
        inflater,
        R.layout.frag_main,
        container,
        false
    ).let { binding ->
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.showAddWA) {
            (requireActivity() as MainActivity).toolbar.setTitle(R.string.add_webasyst)
        }

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.site, R.id.shop, R.id.blog -> {
                    onTabChange(item.itemId)
                    true
                }
                else -> false
            }
        }

        onTabChange(bottomNav.selectedItemId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if ((args.installationUrl ?: "").startsWith("http://")) {
            insecureAlert = Snackbar
                .make(requireView(), R.string.installation_connection_not_secure, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.btn_dismiss) {}
                .apply { show() }
        }
    }

    override fun onPause() {
        super.onPause()
        insecureAlert?.dismiss()
    }

    private fun initDomainsFragment(): Fragment =
        DomainListFragment::class.java.newInstance().apply {
            arguments = Bundle().apply {
                putString(INSTALLATION_ID, args.installationId)
                putString(INSTALLATION_URL, args.installationUrl)

            }
        }

    private fun initShopFragment(): Fragment =
        OrderListFragment::class.java.newInstance().apply {
            arguments = Bundle().apply {
                putString(INSTALLATION_ID, args.installationId)
                putString(INSTALLATION_URL, args.installationUrl)
            }
        }

    private fun initBlogFragment(): Fragment =
        BlogRootFragment::class.java.newInstance().apply {
            arguments = Bundle().apply {
                putString(INSTALLATION_ID, args.installationId)
                putString(INSTALLATION_URL, args.installationUrl)
            }
        }

    private fun onTabChange(@IdRes id: Int) {
        val fragment = when(id) {
            R.id.site -> initDomainsFragment()
            R.id.shop -> initShopFragment()
            R.id.blog -> initBlogFragment()
            else -> throw IllegalArgumentException("Tab not found")
        }
        loadFragment(fragment)
        if (args.installationId != null) {
            requireActivity().toolbar.title = bottomNav.menu.findItem(id).title
        }
    }

    private fun loadFragment(fragment: Fragment) {
        childFragmentManager
            .beginTransaction()
            .replace(R.id.tabContent, fragment)
            .commit()
    }

    companion object {
        const val INSTALLATION_ID = "installationId"
        const val INSTALLATION_URL = "installationUrl"
    }
}
