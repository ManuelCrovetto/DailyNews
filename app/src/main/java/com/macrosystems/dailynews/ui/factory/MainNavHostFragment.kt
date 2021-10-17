package com.macrosystems.dailynews.ui.factory

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**Custom NavHostFragment has been created and used, so we can instantiate safely all fragments with its constructors injected properly with Hilt.
*This is the solution I am implementing, if you know any other better solution, let me know :)*/

@AndroidEntryPoint
class MainNavHostFragment: NavHostFragment() {

    @Inject
    lateinit var fragmentFactory: NewsFeedFragmentFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        childFragmentManager.fragmentFactory = fragmentFactory
    }
}