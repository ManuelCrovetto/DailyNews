package com.macrosystems.dailynews.ui.factory

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.bumptech.glide.RequestManager
import com.macrosystems.dailynews.core.dialog.launcher.DialogFragmentLauncher
import com.macrosystems.dailynews.ui.adapters.NewsFeedAdapter
import com.macrosystems.dailynews.ui.view.fragments.DetailedNewsFragment
import com.macrosystems.dailynews.ui.view.fragments.NewsFeedFragment
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

/**Creating a FragmentFactory we are able to inject constructors in the fragments.
Visit https://developer.android.com/reference/androidx/fragment/app/FragmentFactory*/

class NewsFeedFragmentFactory @Inject constructor(
    private val newsFeedAdapter: NewsFeedAdapter,
    private val glide: RequestManager,
    private val dialogFragmentLauncher: DialogFragmentLauncher
): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            NewsFeedFragment::class.java.name ->
                NewsFeedFragment(newsFeedAdapter, dialogFragmentLauncher, glide)

            DetailedNewsFragment::class.java.name ->
                DetailedNewsFragment(glide, dialogFragmentLauncher)

            else ->
                super.instantiate(classLoader, className)
        }
    }
}