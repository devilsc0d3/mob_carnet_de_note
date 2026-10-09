package com.example.testcour.ui.components

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.webkit.WebViewAssetLoader
import com.example.testcour.R
import com.example.testcour.data.local.NoteEntity
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "NotesMap"

/**
 * Carte Leaflet (page HTML dans une WebView) affichant un marqueur par note géolocalisée.
 * Le clic sur un marqueur ouvre une bulle ; si [onNoteClick] est fourni, la bulle
 * contient un bouton qui renvoie l'id de la note côté Kotlin.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NotesMap(
    notes: List<NoteEntity>,
    modifier: Modifier = Modifier,
    zoom: Double = 13.0,
    onNoteClick: ((Long) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnNoteClick by rememberUpdatedState(onNoteClick)
    var pageLoaded by remember { mutableStateOf(false) }
    val locale = currentLocale()
    val openLabel = stringResource(R.string.map_open_note)

    val webView = remember {
        // Sert les fichiers de assets/ depuis une URL https : OpenStreetMap exige un "Referer" valide,
        // ce qu'une page chargée en file:// ne fournit pas.
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()

        WebView(context).apply {
            // Sans ça, la page a une hauteur de 0 et la carte (height: 100%) reste invisible
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? = assetLoader.shouldInterceptRequest(request.url)

                override fun onPageFinished(view: WebView, url: String) {
                    pageLoaded = true
                }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    Log.w(TAG, "Erreur ${request.url} : ${error.description}")
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                    Log.d(TAG, "JS: ${message.message()} (${message.sourceId()}:${message.lineNumber()})")
                    return true
                }
            }
            addJavascriptInterface(object {
                // Appelé par le JavaScript sur un thread du WebView : on repasse sur le thread UI
                @JavascriptInterface
                fun onNoteClick(id: String) {
                    post { id.toLongOrNull()?.let { currentOnNoteClick?.invoke(it) } }
                }
            }, "AndroidBridge")
            loadUrl("https://appassets.androidplatform.net/assets/map.html")
        }
    }

    // Envoie les notes à Leaflet dès que la page est prête, puis à chaque changement
    LaunchedEffect(notes, pageLoaded, openLabel) {
        if (!pageLoaded) return@LaunchedEffect
        val json = JSONArray()
        notes.filter { it.hasLocation }.forEach { note ->
            json.put(
                JSONObject()
                    .put("id", note.id)
                    .put("title", note.title)
                    .put("date", formatDate(note.date, locale))
                    .put("lat", note.latitude)
                    .put("lng", note.longitude)
            )
        }
        val clickable = onNoteClick != null
        webView.evaluateJavascript(
            "setNotes($json, $zoom, $clickable, ${JSONObject.quote(openLabel)});",
            null
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> webView.onResume()
                Lifecycle.Event.ON_PAUSE -> webView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            webView.destroy()
        }
    }

    AndroidView(factory = { webView }, modifier = modifier)
}
