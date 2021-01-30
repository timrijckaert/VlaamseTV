package be.tapped.vlaamsetv.browse.vrt

import android.content.Context
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.Row
import androidx.leanback.widget.SectionRow
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.browse.presenter.PresenterSelector

class VRTBrowseUseCase(
    azUseCase: VRTNUAZUseCase,
    liveTVUseCase: LiveTVUseCase,
    categoryUseCase: CategoriesUseCase,
    mostRecentUseCase: MostRecentUseCase,
) : VRTNUAZUseCase by azUseCase,
    LiveTVUseCase by liveTVUseCase,
    CategoriesUseCase by categoryUseCase,
    MostRecentUseCase by mostRecentUseCase {

    suspend fun constructMenu(ctx: Context): List<Row> =
        listOf(
            SectionRow(HeaderItem(ctx.getString(R.string.vrt_nu_name))),

            ListRow(
                HeaderItem(ctx.getString(R.string.vrt_nu_live_tv)),
                ArrayObjectAdapter(PresenterSelector()).apply {
                    addAll(0, liveStreams())
                }),

            ListRow(
                HeaderItem(ctx.getString(R.string.vrt_nu_most_recent_episodes)),
                ArrayObjectAdapter(PresenterSelector()).apply {
                    addAll(0, fetchMostRecentEpisodes())
                }),

            ListRow(
                HeaderItem(ctx.getString(R.string.vrt_nu_all_programs)),
                ArrayObjectAdapter(PresenterSelector()).apply {
                    addAll(0, fetchAZPrograms())
                }),

            ListRow(
                HeaderItem(ctx.getString(R.string.vrt_nu_categories)),
                ArrayObjectAdapter(PresenterSelector()).apply {
                    addAll(0, fetchCategories())
                }),
        )
}
