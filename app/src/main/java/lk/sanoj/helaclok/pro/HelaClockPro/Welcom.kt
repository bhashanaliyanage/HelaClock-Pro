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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.ColorUtils
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.onesignal.OneSignal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    var backgroundExpanded by rememberSaveable { mutableStateOf(false) }
    var selectorSession by rememberSaveable { mutableIntStateOf(0) }
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
    BackHandler(page != "home" || backgroundExpanded) {
        if (page == "home" && backgroundExpanded) backgroundExpanded = false
        else { backgroundExpanded = false; page = "home" }
    }
    MaterialExpressiveTheme(colorScheme = colors) {
        val screenEnterSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
        val screenExitSpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()
        if (page == "clock") {
            DeskClock(prefs, revision)
        } else {
            Scaffold(
                topBar = { TopAppBar(title = { Text(if (page == "home") "HelaClock Pro" else "Settings") }) },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = page == "home",
                            onClick = { backgroundExpanded = false; page = "home" },
                            icon = { Text("⌂", style = MaterialTheme.typography.titleLarge) },
                            label = { Text("Home") }
                        )
                        NavigationBarItem(
                            selected = page != "home",
                            onClick = { backgroundExpanded = false; page = "settings" },
                            icon = { Text("⚙", style = MaterialTheme.typography.titleLarge) },
                            label = { Text("Settings") }
                        )
                    }
                }
            ) { padding ->
                AnimatedContent(
                    targetState = page,
                    modifier = Modifier.fillMaxSize().padding(padding),
                    transitionSpec = { fadeIn(screenEnterSpec) togetherWith fadeOut(screenExitSpec) },
                    label = "screen transition"
                ) { currentPage ->
                    if (currentPage == "home") {
                        HomeScreen(
                            prefs = prefs,
                            revision = revision,
                            expanded = backgroundExpanded,
                            selectorSession = selectorSession,
                            onToggleSelector = {
                                if (!backgroundExpanded) selectorSession++
                                backgroundExpanded = !backgroundExpanded
                            },
                            onCloseSelector = { backgroundExpanded = false },
                            onOpenClock = { backgroundExpanded = false; page = "clock" },
                            canNotify = canNotify,
                            onEnableNotifications = {
                                if (Build.VERSION.SDK_INT >= 33) notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        )
                    } else {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            WidgetSettings(prefs, revision, resumeCount)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    prefs: SharedPreferences,
    revision: Int,
    expanded: Boolean,
    selectorSession: Int,
    onToggleSelector: () -> Unit,
    onCloseSelector: () -> Unit,
    onOpenClock: () -> Unit,
    canNotify: Boolean,
    onEnableNotifications: () -> Unit
) {
    val scroll = rememberScrollState()
    LaunchedEffect(expanded, scroll.maxValue) {
        if (expanded) scroll.animateScrollTo(scroll.maxValue)
    }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "background selector chevron"
    )
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WidgetPreview(revision)
            if (!canNotify) OutlinedButton(onClick = onEnableNotifications, modifier = Modifier.fillMaxWidth()) {
                Text("Enable notifications")
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()) +
                    fadeIn(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
                exit = shrinkVertically(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()) +
                    fadeOut(animationSpec = MaterialTheme.motionScheme.fastEffectsSpec())
            ) {
                key(selectorSession) {
                    DeskAppearanceSelector(
                        initialKey = prefs.getString("ThemeName", deskThemes[0].first) ?: deskThemes[0].first,
                        onDismiss = onCloseSelector,
                        onApply = { prefs.edit().putString("ThemeName", it).apply(); onCloseSelector() }
                    )
                }
            }
        }
        val buttonHeight = SplitButtonDefaults.LargeContainerHeight
        BoxWithConstraints(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        val trailingWidth = minOf(72.dp, maxWidth * 0.3f)
        SplitButtonLayout(
            modifier = Modifier.fillMaxWidth(),
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    onClick = onOpenClock,
                    modifier = Modifier.width((maxWidth - trailingWidth - SplitButtonDefaults.Spacing).coerceAtLeast(0.dp)).height(buttonHeight),
                    shapes = SplitButtonDefaults.leadingButtonShapesFor(buttonHeight),
                    contentPadding = SplitButtonDefaults.leadingButtonContentPaddingFor(buttonHeight)
                ) { Text("Open desk clock", style = MaterialTheme.typography.titleMedium, maxLines = 1) }
            },
            trailingButton = {
                SplitButtonDefaults.TrailingButton(
                    checked = expanded,
                    onCheckedChange = { onToggleSelector() },
                    modifier = Modifier.width(trailingWidth).height(buttonHeight).testTag("background-selector-toggle")
                        .semantics { contentDescription = if (expanded) "Collapse background selection" else "Choose desk clock background" },
                    shapes = SplitButtonDefaults.trailingButtonShapesFor(buttonHeight),
                    contentPadding = SplitButtonDefaults.trailingButtonContentPaddingFor(buttonHeight)
                ) { Text("⌄", Modifier.rotate(rotation), style = MaterialTheme.typography.headlineMedium) }
            }
        )
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
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        choices.forEachIndexed { index, (value, label) ->
            SegmentedButton(
                selected = mode == value,
                onClick = {
                    prefs.edit().putInt("clock_color_mode", value).apply()
                    ClockWidget.refreshAll(context)
                    if (value == ClockWidgetRenderer.COLOR_MODE_CUSTOM) showPicker = true
                },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = choices.size),
                label = { Text(label, maxLines = 1) }
            )
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DeskAppearanceSelector(initialKey: String, onDismiss: () -> Unit, onApply: (String) -> Unit) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(deskThemes.indexOfFirst { it.first == initialKey }.coerceAtLeast(0)) }
    val carouselState = rememberCarouselState(initialItem = selectedIndex) { deskThemes.size }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(carouselState.currentItem) { selectedIndex = carouselState.currentItem }

    Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Column(Modifier.fillMaxWidth().padding(vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Choose background", style = MaterialTheme.typography.titleLarge)
                Text("Swipe through the desk clock styles", style = MaterialTheme.typography.bodyMedium)
            }
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                HorizontalUncontainedCarousel(
                    state = carouselState,
                    itemWidth = minOf(248.dp, maxWidth - 40.dp).coerceAtLeast(1.dp),
                    itemSpacing = 12.dp,
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    modifier = Modifier.height(200.dp).testTag("desk-background-carousel")
                ) { index ->
                    val (key, label) = deskThemes[index]
                    val selected = selectedIndex == index
                    Surface(
                        onClick = { coroutineScope.launch { carouselState.animateScrollToItem(index) } },
                        modifier = Modifier.fillMaxSize().then(if (selected) Modifier.padding(2.dp) else Modifier),
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        border = if (selected) androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DeskClockSample(key, Modifier.weight(1f).fillMaxWidth())
                            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(label, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                                if (selected) Text("Selected", color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onApply(deskThemes[selectedIndex].first) }) { Text("Apply") }
            }
        }
    }
}

@Composable
private fun DeskClockSample(key: String, modifier: Modifier = Modifier) {
    val drawable = deskBackgroundDrawable(key)
    Box(modifier.background(Color.Black, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
        if (drawable != null) {
            Image(painterResource(drawable), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("පෙ.ව. හයයි", color = Color.White, style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center, maxLines = 1)
            Text("6:00", color = Color.White, style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun deskBackgroundDrawable(key: String): Int? = when (key) {
    deskThemes[1].first -> R.drawable.sndbackgrund
    deskThemes[2].first -> R.drawable.sndsback
    deskThemes[3].first -> R.drawable.lastbck
    else -> null
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
