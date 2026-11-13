package lk.sanoj.helaclok.pro.HelaClockPro

import android.app.Activity
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.ColorUtils
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.onesignal.OneSignal
import kotlinx.coroutines.delay
import lk.sanoj.helaclok.pro.HelaClockPro.Models.Clock
import top.defaults.colorpicker.ColorPickerView

class Welcom : ComponentActivity() {
    private var resumeCount by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OneSignal.initWithContext(this, "be09b248-91ab-4177-a28e-5b4acd5c5a5a")
        setContent { HelaApp(resumeCount) }
    }

    override fun onResume() {
        super.onResume()
        resumeCount++
        ClockWidget.refreshAll(this)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HelaApp(resumeCount: Int) {
    val context = LocalContext.current
    val dark = isSystemInDarkTheme()
    val colors = if (Build.VERSION.SDK_INT >= 31) {
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else if (dark) darkColorScheme() else lightColorScheme()
    val prefs = remember { context.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE) }
    var revision by remember { mutableIntStateOf(0) }
    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> revision++ }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    var page by rememberSaveable { mutableStateOf("home") }
    var notificationRevision by remember { mutableIntStateOf(0) }
    val notificationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        notificationRevision++
    }
    @Suppress("UNUSED_VARIABLE") val permissionRefresh = notificationRevision + resumeCount
    val canNotify = Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
        context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    SideEffect {
        val activity = context as Activity
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = !dark && page != "clock"
            isAppearanceLightNavigationBars = !dark && page != "clock"
        }
    }
    BackHandler(page != "home") { page = "home" }
    MaterialExpressiveTheme(colorScheme = colors) {
        if (page == "clock") {
            DeskClock(prefs, revision)
        } else {
            Scaffold(topBar = {
                TopAppBar(title = { Text(if (page == "home") "HelaClock Pro" else if (page == "widget") "Widget settings" else "Desk clock appearance") },
                    navigationIcon = { if (page != "home") TextButton(onClick = { page = "home" }) { Text("Back") } })
            }) { padding ->
                Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    when (page) {
                        "home" -> {
                            Text("සිංහල ඔරලෝසුව", style = MaterialTheme.typography.headlineSmall)
                            WidgetPreview(revision)
                            Button(onClick = { page = "clock" }, modifier = Modifier.fillMaxWidth()) { Text("Open desk clock") }
                            FilledTonalButton(onClick = { page = "deskSettings" }, modifier = Modifier.fillMaxWidth()) { Text("Desk clock appearance") }
                            FilledTonalButton(onClick = { page = "widget" }, modifier = Modifier.fillMaxWidth()) { Text("Widget settings") }
                            if (!canNotify) OutlinedButton(onClick = {
                                if (Build.VERSION.SDK_INT >= 33) notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }, modifier = Modifier.fillMaxWidth()) { Text("Enable notifications") }
                        }
                        "widget" -> WidgetSettings(prefs, revision, resumeCount)
                        else -> DeskSettings(prefs, revision)
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetPreview(revision: Int) {
    val context = LocalContext.current
    var tick by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { while (true) { delay(1000); tick++ } }
    @Suppress("UNUSED_VARIABLE") val changed = revision + tick
    val widgetColor = ClockWidgetRenderer.resolveConfiguredColor(context)
    val screenColor = MaterialTheme.colorScheme.background.toArgb()
    val composited = ColorUtils.compositeColors(widgetColor, screenColor)
    val needsBackdrop = ColorUtils.calculateContrast(composited, screenColor) < 2.5
    val backdrop = if (ColorUtils.calculateLuminance(widgetColor) > 0.5) Color(0xff242424) else Color(0xfff0f0f0)
    Column {
        Surface(color = if (needsBackdrop) backdrop else Color.Transparent,
            shape = MaterialTheme.shapes.large) {
            AndroidView(factory = { FrameLayout(it) }, modifier = Modifier.fillMaxWidth().height(220.dp), update = {
                @Suppress("UNUSED_VARIABLE") val refreshKey = revision + tick
                ClockWidgetRenderer.renderPreview(it.context, it)
            })
        }
        if (needsBackdrop) Text("Preview shown on a contrasting surface", style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun WidgetSettings(prefs: SharedPreferences, revision: Int, resumeCount: Int) {
    val context = LocalContext.current
    @Suppress("UNUSED_VARIABLE") val changed = revision + resumeCount
    val mode = ClockWidgetRenderer.getConfiguredColorMode(context)
    val hint = prefs.getInt("hint", 0) != 1
    var showPicker by remember { mutableStateOf(false) }
    WidgetPreview(revision)
    Text("Clock color", style = MaterialTheme.typography.titleMedium)
    val choices = buildList {
        if (Build.VERSION.SDK_INT >= 31) add(ClockWidgetRenderer.COLOR_MODE_SYSTEM to "System")
        add(ClockWidgetRenderer.COLOR_MODE_CUSTOM to "Custom")
        add(ClockWidgetRenderer.COLOR_MODE_WHITE to "White")
    }
    Column(Modifier.selectableGroup()) {
      choices.forEach { (value, label) ->
        Row(Modifier.fillMaxWidth().selectable(selected = mode == value, role = Role.RadioButton, onClick = {
            prefs.edit().putInt("clock_color_mode", value).apply()
            ClockWidget.refreshAll(context)
            if (value == ClockWidgetRenderer.COLOR_MODE_CUSTOM) showPicker = true
        }).padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = mode == value, onClick = null)
            Spacer(Modifier.width(12.dp))
            Text(label)
        }
      }
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Show hint image", Modifier.weight(1f))
        Switch(checked = hint, onCheckedChange = {
            prefs.edit().putInt("hint", if (it) 0 else 1).apply()
            ClockWidget.refreshAll(context)
        })
    }
    if (Build.VERSION.SDK_INT >= 31 && ClockWidget.hasWidgets(context) && !ClockWidget.canScheduleExact(context)) {
        Text("Allow precise alarms for timely clock updates. Without access, updates may be delayed.", style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(onClick = {
            runCatching { context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:${context.packageName}"))) }
        }) { Text("Allow precise updates") }
    }
    OutlinedButton(onClick = { prefs.edit().clear().apply(); ClockWidget.refreshAll(context) }) { Text("Reset settings") }
    if (showPicker) {
        ColorDialog(prefs.getInt("textc", AndroidColor.WHITE).let { if (it == 0) AndroidColor.WHITE else it },
            onDismiss = { showPicker = false }, onSave = { color ->
                prefs.edit().putInt("textc", color).putString("colour", String.format("#%06X", 0xFFFFFF and color))
                    .putInt("clock_color_mode", ClockWidgetRenderer.COLOR_MODE_CUSTOM).apply()
                ClockWidget.refreshAll(context)
                showPicker = false
            })
    }
}

@Composable
private fun ColorDialog(initial: Int, onDismiss: () -> Unit, onSave: (Int) -> Unit) {
    var picker by remember { mutableStateOf<ColorPickerView?>(null) }
    var hex by remember { mutableStateOf(String.format("#%06X", 0xFFFFFF and initial)) }
    var error by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Custom color") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AndroidView(factory = { context -> ColorPickerView(context).apply {
                setEnabledBrightness(true); setEnabledAlpha(true); setInitialColor(initial); picker = this
            } }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = hex, onValueChange = {
                hex = it; error = false
                if (it.matches(Regex("#[0-9a-fA-F]{6}"))) picker?.setInitialColor(AndroidColor.parseColor(it))
            }, label = { Text("Hex color") }, isError = error, singleLine = true)
        }
    }, confirmButton = { TextButton(onClick = {
        if (!hex.matches(Regex("#[0-9a-fA-F]{6}"))) error = true else onSave(picker?.color ?: initial)
    }) { Text("Apply") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

private val deskThemes = listOf("AssXSDrrfgssdbh" to "Blank", "WdhbgfhghhdwaSSDfgdfg" to "Pattern",
    "HlknsdakjKJHfdskljhs" to "Grunge", "dAsdsQWdsfdSDfdsfS" to "Rock wall")

@Composable
private fun DeskSettings(prefs: SharedPreferences, revision: Int) {
    @Suppress("UNUSED_VARIABLE") val changed = revision
    val selected = prefs.getString("ThemeName", deskThemes[0].first)
    Text("Choose a background", style = MaterialTheme.typography.titleMedium)
    Column(Modifier.selectableGroup()) {
      deskThemes.forEach { (key, label) ->
        Row(Modifier.fillMaxWidth().selectable(selected = selected == key, role = Role.RadioButton,
            onClick = { prefs.edit().putString("ThemeName", key).apply() })
            .padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected == key, onClick = null)
            Spacer(Modifier.width(12.dp)); Text(label)
        }
      }
    }
}

@Composable
private fun DeskClock(prefs: SharedPreferences, revision: Int) {
    val activity = LocalContext.current as Activity
    DisposableEffect(activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        WindowCompat.getInsetsController(activity.window, activity.window.decorView)
            .hide(WindowInsetsCompat.Type.statusBars())
        onDispose {
            if (!activity.isChangingConfigurations) {
                WindowCompat.getInsetsController(activity.window, activity.window.decorView)
                    .show(WindowInsetsCompat.Type.statusBars())
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }
    var time by remember { mutableStateOf(Clock().time) }
    LaunchedEffect(Unit) { while (true) { delay(1000); time = Clock().time } }
    @Suppress("UNUSED_VARIABLE") val changed = revision
    val background = when (prefs.getString("ThemeName", null)) {
        deskThemes[1].first -> R.drawable.sndbackgrund
        deskThemes[2].first -> R.drawable.sndsback
        deskThemes[3].first -> R.drawable.lastbck
        else -> null
    }
    val words = ClockWidgetRenderer.clockWords(time)
    if (time.minutes == "00") words[2] = "තත්පර කීපයකි"
    if (time.minutes == "49") words[2] = "විනාඩි හතලිස් නවයයි"
    BoxWithConstraints(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        val clockSize = minOf(maxHeight.value * 0.15f, maxWidth.value * 0.07f).sp
        if (background != null) Image(painterResource(background), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            words.forEach { Text(it, color = Color.White, fontFamily = FontFamily(Font(R.font.emanee)),
                fontSize = clockSize, lineHeight = clockSize * 1.1f, textAlign = TextAlign.Center, maxLines = 1) }
            Spacer(Modifier.height(16.dp))
            Text("${time.hours}  ${time.minutes}  ${time.meridiem}", color = Color.White)
        }
    }
}
