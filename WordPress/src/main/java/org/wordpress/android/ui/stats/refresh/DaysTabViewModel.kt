package org.wordpress.android.ui.stats.refresh

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModelProvider
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.launch
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.modules.UI_SCOPE
import org.wordpress.android.ui.stats.refresh.InsightsUiState.StatsListState.DONE
import javax.inject.Inject
import javax.inject.Named

class DaysTabViewModel @Inject constructor(
    @Named(UI_SCOPE) private val uiScope: CoroutineScope
) : StatsListViewModel() {
    override val data: LiveData<InsightsUiState> = MutableLiveData()

    override fun start(site: SiteModel) {
        reload(site)
    }

    override fun reload(site: SiteModel) {
    }
}
