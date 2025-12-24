package com.venom.quiz.ui.components

import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun QuestionWebView(
    html: String,
    contentHeightPx: Int,
    onContentHeightPxChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(contentHeightPx.dp)
            .clip(RoundedCornerShape(12.dp)),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setBackgroundColor(Color.TRANSPARENT)
                isVerticalScrollBarEnabled = true
                isHorizontalScrollBarEnabled = false
                overScrollMode = WebView.OVER_SCROLL_IF_CONTENT_SCROLLS

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = false
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.defaultTextEncodingName = "utf-8"
                settings.allowFileAccess = false
                settings.allowContentAccess = false

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        view?.evaluateJavascript(
                            """
                                (function() {
                                  var h = Math.max(
                                    document.documentElement.scrollHeight || 0,
                                    document.body.scrollHeight || 0,
                                    document.documentElement.clientHeight || 0
                                  );
                                  return h;
                                })();
                            """.trimIndent()
                        ) { value ->
                            val parsed = value
                                ?.replace("\"", "")
                                ?.toFloatOrNull()
                                ?.toInt()
                            if (parsed != null && parsed > 0) {
                                onContentHeightPxChanged(parsed)
                            }
                        }
                    }
                }
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, wrapHtml(html), "text/html", "utf-8", null)
        }
    )
}

private fun wrapHtml(body: String): String {
    return """
        <!DOCTYPE html>
        <html>
          <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <style>
              html, body { margin: 0; padding: 0; }
              body { color: #E6E1E5; background: transparent; font-size: 16px; line-height: 1.6; }
              img { display: inline; max-width: 100%; height: auto; }
              * { box-sizing: border-box; }
            </style>
          </head>
          <body>
            $body
          </body>
        </html>
    """.trimIndent()
}
