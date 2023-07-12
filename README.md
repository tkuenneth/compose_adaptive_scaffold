# Welcome to compose_adaptive_scaffold

The aim of this library is to make writing Jetpack Compose apps that
support large screen and foldable devices a breeze. Here's a short clip that showcases how the
library works. Clicking on the preview image directs you to YouTube.

[![Watch the video](https://img.youtube.com/vi/nJDmJ0mmpys/mqdefault.jpg)](https://youtu.be/nJDmJ0mmpys)

*compose_adaptive_scaffold* is based on the idea of two panes, called *body* and *secondary body*.
For small screens you pass alternatives (or variations) called *small body* and *small secondary
body* (the latter one is optional). Depending on your screen layout, the pairs *body* and *small
body*, and *secondary body* and *small secondary body* may even be the same. Two panes are the basis
for *Canonical Layouts*, an important Material Design concept.

Under the hood, *compose_adaptive_scaffold* uses *Jetpack WindowManager* to provide full hinge 
support. This means that you do not need to worry about screen dimensions, location and sizes of 
hinges, and hinge features like orientation. Just provide *body* and *secondary body* 
composables - everything else is handled by *compose_adaptive_scaffold*.

<p>
<img src="./docs/Duo_01.jpg" width="15%" valign="top" alt="Picture of Microsoft Surface Duo portrait folded" />
<img src="./docs/Duo_02.jpg" width="22%" valign="top" alt="Picture of Microsoft Surface Duo landscape folded" />
<img src="./docs/Duo_03.jpg" width="30%" valign="top" alt="Picture of Microsoft Surface Duo portrait opened" />
<img src="./docs/Duo_04.jpg" width="25%" valign="top" alt="Picture of Microsoft Surface Duo landscape opened" />
</p>

**Please note**

Version `0.2.0` removes `LocalWindowSizeClass` in favor of the new `LocalFoldDef` `CompositionLocal`. Instead of writing,
for example, `if (LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.COMPACT) {` you should now be using
`if (LocalFoldDef.current.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {`.

### Setup

The library is available through Maven Central. To include it in your apps, just add an
implementation dependency:

```groovy
dependencies {
    implementation "com.github.tkuenneth:compose_adaptive_scaffold:0.2.0"
}
```

The library uses this configuration:

| Property | Value                                |
| -------- |--------------------------------------|
| `namespace` | `eu.thomaskuenneth.adaptivescaffold` |
| `minSdk` | `24`                                 |
| `targetSdk` | `34`                                 |

Used libraries:

| Name | Version         |
| -------- |-----------------|
| *Jetpack WindowManager* | `1.2.0-alpha03` |
| *Jetpack Compose BOM* | `2023.05.01`    |

### How to use

Here's how the main activity of the sample app looks like. The two states, `toggleSmallSecondaryBodyVisible`
and `showSmallSecondaryBody` are used to show or hide a menu, so they are not strictly related to
`AdaptiveScaffold()`.

```kotlin
class AdaptiveScaffoldDemoActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        setContent {
          var toggleSmallSecondaryBodyVisible by rememberSaveable { mutableStateOf(false) }
          var showSmallSecondaryBody by rememberSaveable { mutableStateOf(true) }
          MaterialTheme(
            content = {
              AdaptiveScaffold(
                useDrawer = true,
                startDestination = destinationOne(showSmallSecondaryBody),
                otherDestinations = listOf(destinationFoldInfo),
                onDestinationChanged = {
                  toggleSmallSecondaryBodyVisible = it != destinationFoldInfo
                },
                topBar = {
                  AdaptiveScaffoldDemoTopAppBar(
                    toggleSmallSecondaryBodyVisible = toggleSmallSecondaryBodyVisible,
                    toggleSmallSecondaryBodyClicked = {
                      showSmallSecondaryBody = !showSmallSecondaryBody
                    }
                  )
                },
              )
            },
            colorScheme = defaultColorScheme()
          )
        }
      }
    }
  }
}
```

The two destinations are defined like this:

```kotlin
fun destinationOne(showSmallSecondaryBody: Boolean) = NavigationDestination(
  icon = R.drawable.ic_android_black_24dp,
  label = R.string.one,
  body = {
    Body()
  },
  smallBody = {
    SmallBody()
  },
  secondaryBody = { SecondaryBody() },
  smallSecondaryBody = if (showSmallSecondaryBody) {
    { SmallSecondaryBody() }
  } else null
)

val destinationFoldInfo = NavigationDestination(
  icon = R.drawable.baseline_perm_device_information_24,
  label = R.string.fold_info,
  overlay = { FoldDefInfo() },
)
```

Defining a destination as a function allows you to changes panes based on state.

`NavigationDestination` receives an icon, a label, and four composable functions:

1. a body
2. a secondary body
3. a small body
4. a small secondary body (can be `null`)

Depending on the app window size, either *body* and *secondary body* **or** *small body*
and *small secondary body* are shown. If the device has a hinge, all features of the hinge including
its location, size, and orientation are honored.

Inside your composable functions, you can use `LocalFoldDef.current` to find out the 
current window size classes and the configuration of the fold or hinge.

```kotlin
@Composable
private fun FoldDefInfo() {
  with(LocalFoldDef.current) {
    LazyColumn(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
      item {
        FoldDefInfoItem("isSeparating", isSeparating.toString())
        FoldDefInfoItem("orientation", orientation.toString())
        FoldDefInfoItem("occlusionType", occlusionType.toString())
        VerticalSpacer()
        FoldDefInfoItem(
          "windowWidthSizeClass",
          windowSizeClass.windowWidthSizeClass.toString().lastPart()
        )
        FoldDefInfoItem(
          "windowHeightSizeClass",
          windowSizeClass.windowHeightSizeClass.toString().lastPart()
        )
        VerticalSpacer()
        FoldDefInfoItem("foldWidth", foldWidth.toString())
        FoldDefInfoItem("foldHeight", foldHeight.toString())
        VerticalSpacer()
        FoldDefInfoItem("widthLeftOrTop", widthLeftOrTop.toString())
        FoldDefInfoItem("heightLeftOrTop", heightLeftOrTop.toString())
        VerticalSpacer()
        FoldDefInfoItem("widthRightOrBottom", widthRightOrBottom.toString())
        FoldDefInfoItem("heightRightOrBottom", heightRightOrBottom.toString())
      }
    }
  }
}
```

Here's something interesting: if you want to show something that should span the two panes,
you can pass a `overlay` composable to `NavigationDestination`. Please have a look at `destinationFoldInfo` to
see how that works. In this example the two panes are empty, so the overlay becomes the main content.

### Acknowledgements

*compose_adaptive_scaffold* is inspired by the Flutter package [flutter_adaptive_scaffold](https://pub.dev/packages/flutter_adaptive_scaffold).
