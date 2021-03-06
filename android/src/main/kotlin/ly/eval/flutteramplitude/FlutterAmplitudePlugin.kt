package ly.eval.flutteramplitude

import android.app.Activity
import android.util.Log.VERBOSE
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.amplitude.api.Identify
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import org.json.JSONObject

class FlutterAmplitudePlugin() : MethodCallHandler {

    private lateinit var amplitude: AmplitudeClient

    companion object {
        lateinit var activityContext: Activity
        @JvmStatic
        fun registerWith(registrar: Registrar): Unit {
            val channel = MethodChannel(registrar.messenger(), "ly.eval.flutter_amplitude")
            activityContext = registrar.activity()
            channel.setMethodCallHandler(FlutterAmplitudePlugin())
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result): Unit {
        when {
            call.method == "initAmplitudeSDK" -> {
                val apiKey = call.argument<String>("apiKey")!!
                val enableLogging = call.argument<Boolean>("enableLogging") ?: false
                val enableForegroundTracking = call.argument<Boolean>("enableForegroundTracking") ?: true
                val enableLocationListening = call.argument<Boolean>("enableLocationListening") ?: true
                initSDK(apiKey, enableLogging, enableForegroundTracking, enableLocationListening)
            }
            call.method == "logEvent" -> {
                val arguments = call.arguments as MutableMap<String, Any?>
                val eventName = arguments["eventName"]!! as String
                arguments.remove("eventName")
                logEvent(eventName, arguments)
            }
            call.method == "setUserProperties" -> {
                val properties = call.arguments as MutableMap<String, Any?>
                setUserProperties(properties)
            }
            call.method == "setUserPropertiesOnce" -> {
                val properties = call.arguments as MutableMap<String, String?>
                setUserPropertiesOnce(properties)
            }
            call.method == "clearUserProperties" -> {
                clearUserProperties()
            }
            call.method == "setUserId" -> {
                val userId = call.argument<String?>("userId")
                setUserId(userId)
            }
            else -> result.notImplemented()
        }
    }

    private fun initSDK(apiKey: String, enableLogging: Boolean, enableForegroundTracking: Boolean, enableLocationListening: Boolean) {
        amplitude = Amplitude.getInstance().initialize(activityContext, apiKey)
        amplitude.enableLogging(enableLogging)
        amplitude.setLogLevel(VERBOSE)
        if (enableForegroundTracking) {
            amplitude.enableForegroundTracking(activityContext.application)
        }
        if (enableLocationListening) {
            amplitude.enableLocationListening()
        }
    }

    private fun setUserId(userId: String?) {
        amplitude.userId = userId
    }

    private fun setUserProperties(properties: Map<String, Any?>) {
        amplitude.setUserProperties(properties.getAttributes())
    }

    private fun setUserPropertiesOnce(properties: Map<String, String?>) {
        val identify = Identify()
        for (property in properties) {
            identify.setOnce(property.key, property.value)
        }
        amplitude.identify(identify)
    }

    private fun clearUserProperties() {
        amplitude.clearUserProperties()
    }

    private fun logEvent(eventName: String, arguments: Map<String, Any?>) {
        amplitude.logEvent(eventName, arguments.getAttributes())
    }

    private fun Map<String, Any?>.getAttributes(): JSONObject {
        val json = JSONObject()
        this.forEach {
            json.put(it.key, it.value)
        }
        return json
    }
}
