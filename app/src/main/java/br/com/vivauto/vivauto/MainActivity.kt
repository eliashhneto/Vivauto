package br.com.vivauto.vivauto

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val URL = "http://www.vivauto.com.br"
    private var isAlreadyCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startLoaderAnimate()

        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(false)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                endLoaderAnimate()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                endLoaderAnimate()
                showErrorDialog("Erro",
                        "Não há conexão com a internet. Por favor verifique sua conexão.",
                        this@MainActivity)
            }
        }

        webView.loadUrl(URL)
    }

    override fun onResume() {
        super.onResume()

        if (isAlreadyCreated && !isNetworkAvailable()) {
            isAlreadyCreated = false
            showErrorDialog("Erro", "Não há conexão com a internet. Por favor verifique sua conexão.",
                    this@MainActivity)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectionManager =
                this@MainActivity.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected;
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showErrorDialog(title: String, message: String, context: Context) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setNegativeButton("Cancelar", { _, _ ->
            this@MainActivity.finish()
        })
        dialog.setNeutralButton("Configurações", {_, _ ->
            startActivity(Intent(Settings.ACTION_SETTINGS))
        })
        dialog.setPositiveButton("Repetir", { _, _ ->
            this@MainActivity.recreate()
        })
        dialog.create().show()
    }

    private fun endLoaderAnimate() {
        loaderImage.clearAnimation()
        loaderImage.visibility = View.GONE
    }

    private fun startLoaderAnimate() {
        val objectAnimator = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                val startHeight = 170
                val newHeight = (startHeight + (startHeight + 40) * interpolatedTime).toInt()
                loaderImage.layoutParams.height = newHeight
                loaderImage.requestLayout()
            }

            override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
                super.initialize(width, height, parentWidth, parentHeight)
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        objectAnimator.repeatCount = -1
        objectAnimator.repeatMode = ValueAnimator.REVERSE
        objectAnimator.duration = 1000
        loaderImage.startAnimation(objectAnimator)
    }

}
