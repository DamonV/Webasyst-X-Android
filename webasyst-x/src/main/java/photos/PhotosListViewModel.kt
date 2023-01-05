package com.webasyst.x.photos

import android.app.Application
import android.content.Context
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.webasyst.api.Installation
import com.webasyst.x.R
import com.webasyst.x.WebasystXApplication
import com.webasyst.x.photos.api.PhotosApiClient
import com.webasyst.x.photos.api.PhotosApiClientFactory
import com.webasyst.x.util.ConnectivityUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.webasyst.auth.withFreshAccessToken

class PhotosListViewModel(
    app: Application,
    private val installationId: String?,
    private val installationUrl: String?
) : AndroidViewModel(app) {

    private val mPhotosApiClient by lazy {
        if (installationId == null || installationUrl == null) {
        null
    } else (getApplication<WebasystXApplication>()
        .getApiClient()
        .getFactory(PhotosApiClient::class.java) as PhotosApiClientFactory)
        .instanceForInstallation(Installation(installationId, installationUrl))
    }

    val mAuthService by lazy {
        getApplication<WebasystXApplication>().webasystAuthService
    }

    init {
        val connectivityUtil = ConnectivityUtil(getApplication())
        viewModelScope.launch(Dispatchers.Default) {
            connectivityUtil.connectivityFlow()
                .collect {
                    if (it == ConnectivityUtil.ONLINE) {
                        updateData(getApplication())
                    }
                }
        }
    }

    val appName = getApplication<Application>().getString(R.string.app_photos)
    val apiName = "photos.photo.getList"

    private val mutableState = MutableLiveData(STATE_UNKNOWN)
    val state: LiveData<Int> = mutableState

    val spinnerVisibility: LiveData<Int> = MediatorLiveData<Int>().apply {
        addSource(mutableState) { value = if (it == STATE_LOADING_DATA) View.VISIBLE else View.GONE }
    }
    val listVisibility: LiveData<Int> = MediatorLiveData<Int>().apply {
        addSource(mutableState) { value = if (it == STATE_DATA_READY) View.VISIBLE else View.GONE}
    }
    val errorVisibility: LiveData<Int> = MediatorLiveData<Int>().apply {
        addSource(mutableState) { value = if (it == STATE_ERROR) View.VISIBLE else View.GONE }
    }
    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> get() = _error

    private val mutablePhotoList = MutableLiveData<List<Photo>>()
    val domainList: LiveData<List<Photo>> = mutablePhotoList

    suspend fun updateData(context: Context) {
        if (mutableState.value == STATE_LOADING_DATA) {
            return
        }
        mutableState.postValue(STATE_LOADING_DATA)

        try {
            mAuthService.withFreshAccessToken{ _ ->
                mPhotosApiClient?.run {
                getPhotos()
                    .onSuccess {
                        _error.postValue(null)
                        mutableState.postValue(if (it.photos.isEmpty()) STATE_DATA_EMPTY else STATE_DATA_READY)
                        val baseUrl = installationUrl ?: ""
                        mutablePhotoList.postValue(it.photos.map { dto ->
                            Photo(
                                id = dto.id,
                                fullName = dto.name + dto.ext,
                                imageUrl = baseUrl + dto.imageUrl
                            )
                        })
                    }
                    .onFailure {
                        _error.postValue(it)
                        mutableState.postValue(STATE_ERROR)
                    }
                }
            }
        } catch (refreshException: Throwable){
            _error.postValue(refreshException)
            mutableState.postValue(STATE_ERROR)
        }
    }

    class Factory(
        private val application: Application,
        private val installationId: String?,
        private val installationUrl: String?
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PhotosListViewModel(application, installationId, installationUrl) as T
    }

    companion object {
        private const val TAG = "photos_list"
        const val STATE_UNKNOWN = 0
        const val STATE_LOADING_DATA = 1
        const val STATE_DATA_READY = 2
        const val STATE_DATA_EMPTY = 3
        const val STATE_ERROR = 4
    }
}
