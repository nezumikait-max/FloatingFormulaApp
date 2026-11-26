package com.example.myapplication

import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.myapplication.data.Formula
import com.example.myapplication.data.SettingsManager
import com.example.myapplication.ui.DismissZone
import com.example.myapplication.ui.FloatingBubble
import com.example.myapplication.ui.OverlayLifecycleOwner
import com.example.myapplication.ui.OverlayView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.math.roundToInt

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var overlayLifecycleOwner: OverlayLifecycleOwner

    private var bubbleView: ComposeView? = null
    private var dismissView: ComposeView? = null
    private var formulaCardView: ComposeView? = null
    private var bubbleParams: WindowManager.LayoutParams? = null
    private var isCardExpanded = false

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var settingsManager: SettingsManager
    private var overlayParams: WindowManager.LayoutParams? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notification)
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        composeView = ComposeView(this).apply {
            setContent {
                OverlayView(
                    onClose = { stopSelf() },
                    onSpawnBubble = { formula -> spawnBubble(formula) }
                )
            }
        }

        overlayLifecycleOwner = OverlayLifecycleOwner()
        overlayLifecycleOwner.onCreate()
        overlayLifecycleOwner.onStart()
        overlayLifecycleOwner.onResume()

        composeView.setViewTreeLifecycleOwner(overlayLifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(overlayLifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(overlayLifecycleOwner)

        settingsManager = SettingsManager(this)

        overlayParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
        }

        windowManager.addView(composeView, overlayParams)
    }

    private fun spawnBubble(formula: Formula) {
        if (bubbleView != null) return // Already showing a bubble

        bubbleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = resources.displayMetrics.widthPixels / 2
            y = resources.displayMetrics.heightPixels / 2
        }

        bubbleView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(overlayLifecycleOwner)
            setViewTreeViewModelStoreOwner(overlayLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(overlayLifecycleOwner)
            setContent {
                FloatingBubble(
                    formula = formula,
                    onDrag = { dx, dy ->
                        if (isCardExpanded) {
                            hideFormulaCard()
                        }
                        showDismissZone()
                        bubbleParams?.let { params ->
                            params.x += dx.roundToInt()
                            params.y += dy.roundToInt()
                            windowManager.updateViewLayout(this, params)
                        }
                    },
                    onDragEnd = {
                        handleBubbleDragEnd()
                    },
                    onClick = {
                        if (isCardExpanded) {
                            hideFormulaCard()
                        } else {
                            showFormulaCard(formula)
                        }
                    }
                )
            }
        }
        windowManager.addView(bubbleView, bubbleParams)
    }

    private fun showFormulaCard(formula: Formula) {
        if (formulaCardView != null) return
        isCardExpanded = true
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        
        val bubbleX = bubbleParams?.x ?: 0
        val bubbleY = bubbleParams?.y ?: 0
        val bubbleWidth = (72 * displayMetrics.density).toInt()
        
        val isLeftEdge = bubbleX < screenWidth / 2
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            val cardWidthPx = (300 * displayMetrics.density).toInt()
            if (isLeftEdge) {
                x = bubbleX + bubbleWidth + 16
            } else {
                x = bubbleX - cardWidthPx - 16
            }
            x = x.coerceIn(0, screenWidth - cardWidthPx)
            y = bubbleY
        }
        
        formulaCardView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(overlayLifecycleOwner)
            setViewTreeViewModelStoreOwner(overlayLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(overlayLifecycleOwner)
            setContent {
                com.example.myapplication.ui.FormulaCard(
                    formula = formula,
                    onClose = { hideFormulaCard() }
                )
            }
        }
        windowManager.addView(formulaCardView, params)
    }

    private fun hideFormulaCard() {
        isCardExpanded = false
        formulaCardView?.let {
            windowManager.removeView(it)
            formulaCardView = null
        }
    }

    private fun showDismissZone() {
        if (dismissView != null) return
        val displayMetrics = resources.displayMetrics
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = (100 * displayMetrics.density).toInt()
        }

        dismissView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(overlayLifecycleOwner)
            setViewTreeViewModelStoreOwner(overlayLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(overlayLifecycleOwner)
            setContent {
                DismissZone()
            }
        }
        windowManager.addView(dismissView, params)
    }

    private fun hideDismissZone() {
        dismissView?.let {
            windowManager.removeView(it)
            dismissView = null
        }
    }

    private fun handleBubbleDragEnd() {
        val params = bubbleParams ?: return
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels
        val density = displayMetrics.density

        // Check if near bottom center for dismiss
        // The dismiss zone is at Gravity.BOTTOM, y = 100dp
        // and height is 100dp.
        val dismissZoneHeightPx = 100 * density
        val dismissBottomMarginPx = 100 * density
        val dismissZoneTopY = screenHeight - dismissBottomMarginPx - dismissZoneHeightPx
        val dismissYThreshold = dismissZoneTopY - (50 * density) // a little extra leeway above it

        val isXCentered = params.x > screenWidth / 4 && params.x < screenWidth * 3 / 4

        if (params.y > dismissYThreshold && isXCentered) {
            // Dismiss
            bubbleView?.let {
                windowManager.removeView(it)
                bubbleView = null
            }
            if (isCardExpanded) {
                hideFormulaCard()
            }
            hideDismissZone()
            return
        }

        hideDismissZone()

        // Snap to nearest horizontal edge
        val bubbleWidthPx = (72 * displayMetrics.density).toInt()
        val targetX = if (params.x < screenWidth / 2) 0 else screenWidth - bubbleWidthPx

        val animator = ValueAnimator.ofInt(params.x, targetX)
        animator.addUpdateListener { anim ->
            params.x = anim.animatedValue as Int
            if (bubbleView != null && bubbleView?.windowToken != null) {
                windowManager.updateViewLayout(bubbleView, params)
            }
        }
        animator.duration = 200
        animator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (::composeView.isInitialized) {
            windowManager.removeView(composeView)
        }
        bubbleView?.let { windowManager.removeView(it) }
        dismissView?.let { windowManager.removeView(it) }
        formulaCardView?.let { windowManager.removeView(it) }
        
        if (::overlayLifecycleOwner.isInitialized) {
            overlayLifecycleOwner.onPause()
            overlayLifecycleOwner.onStop()
            overlayLifecycleOwner.onDestroy()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Overlay Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Overlay Active")
            .setContentText("Favorite formulas are just a swipe away.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "OverlayServiceChannel"
    }
}
