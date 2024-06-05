package com.fabio.connectivitytest

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fabio.connectivitytest.ui.theme.ConnectivityTestTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {
    private val isInternetConnected = MutableStateFlow(false)
    private val networkUpload = MutableStateFlow("0")
    private val networkDownload = MutableStateFlow("0")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        monitorInternetConnection()
        setContent {
            val isConnected = isInternetConnected.collectAsState().value
            val uploadSpeed = networkUpload.collectAsState().value
            val downloadSpeed = networkDownload.collectAsState().value
            ConnectivityTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center) {
                        Column {
                            Text(
                                text = if (isConnected) "Online" else "Offline")
                            Text(
                                text = "Upload speed: $uploadSpeed Kbps"
                            )
                            Text(
                                text = "Download speed: $downloadSpeed Kbps"
                            )
                        }
                    }
                }
            }
        }

    }

    fun monitorInternetConnection() {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(object : NetworkCallback() {
            override fun onUnavailable() {
                super.onUnavailable()
                isInternetConnected.update {
                    false
                }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                networkUpload.update {
                    "${networkCapabilities.linkUpstreamBandwidthKbps}"
                }
                networkDownload.update {
                    "${networkCapabilities.linkDownstreamBandwidthKbps}"
                }
            }
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isInternetConnected.update {
                    true
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isInternetConnected.update {
                    false
                }
            }
        })
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ConnectivityTestTheme {
        Greeting("Android")
    }
}