package com.student.githubdemo.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

enum class ConnectionState {
    Available, Unavailable
}

class ConnectivityObserver(context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observe(): Flow<ConnectionState> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(ConnectionState.Available)
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    trySend(ConnectionState.Unavailable)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(ConnectionState.Unavailable)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(ConnectionState.Unavailable)
                }
            }

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            
            connectivityManager.registerNetworkCallback(request, callback)

            // Send current state
            val currentState = getCurrentConnectivityState()
            trySend(currentState)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }

    private fun getCurrentConnectivityState(): ConnectionState {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        
        return if (capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
            ConnectionState.Available
        } else {
            ConnectionState.Unavailable
        }
    }
}

@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current
    
    return produceState(initialValue = ConnectionState.Available) {
        val connectivityObserver = ConnectivityObserver(context)
        connectivityObserver.observe().collect { value = it }
    }
}
