package studio.honidot.litsap.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import studio.honidot.litsap.source.DefaultLiTsapRepository
import studio.honidot.litsap.source.LiTsapDataSource
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.source.local.LiTsapLocalDataSource
import studio.honidot.litsap.source.remote.LiTsapRemoteDataSource

object ServiceLocator {

    @Volatile
    var liTsapRepository: LiTsapRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): LiTsapRepository {
        synchronized(this) {
            return liTsapRepository
                ?: liTsapRepository
                ?: createLiTsapRepository(context)
        }
    }

    private fun createLiTsapRepository(context: Context): LiTsapRepository {
        return DefaultLiTsapRepository( LiTsapRemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): LiTsapDataSource {
        return LiTsapLocalDataSource(context)
    }
}