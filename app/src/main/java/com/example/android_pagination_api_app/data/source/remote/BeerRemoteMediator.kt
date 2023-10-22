package com.example.android_pagination_api_app.data.source.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.android_pagination_api_app.data.source.local.BeerDatabase
import com.example.android_pagination_api_app.data.source.local.BeerEntity
import com.example.android_pagination_api_app.data.source.mapper.toBeerEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class BeerRemoteMediator(
    private val beerDb: BeerDatabase,
    private val beerApi: BeerApi
): RemoteMediator<Int, BeerEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BeerEntity>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType){
                LoadType.REFRESH-> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND ->{
                    val lastItem = state.lastItemOrNull()
                    if(lastItem == null){
                        1
                    }else{
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }

            ////pase value beer api service
            val beers = beerApi.getBeers(
                page = loadKey,
                pageCount = state.config.pageSize
            )

            beerDb.withTransaction {
                if (loadType == LoadType.REFRESH){
                    beerDb.dao.clearAll()
                }
                ///covert BeerDto to BeerEntity
                val beerEntity = beers.map {
                    it.toBeerEntity()
                }
                // passs beerEntity list in dao
                beerDb.dao.upsertAll(beerEntity)
            }
            MediatorResult.Success(
                endOfPaginationReached = beers.isEmpty()
            )
        }catch (e: IOException){
            MediatorResult.Error(e)
        }catch (e: HttpException){
            MediatorResult.Error(e)
        }
    }

}