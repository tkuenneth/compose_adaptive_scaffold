# Welcome to compose_adaptive_scaffold

The aim of this repository is to make writing Jetpack Compose apps that
support large screen and foldable devices a breeze. Here's a short clip that showcases how the
library works. Clicking on the preview image directs you to YouTube.

[![Watch the video](https://img.youtube.com/vi/3ryCurTOXVI/mqdefault.jpg)](https://youtu.be/3ryCurTOXVI)

*compose_adaptive_scaffold* is based on the idea of two panes, called *body* and *secondary 
body*. For small screens you pass alternatives called *small body* and *small secondary body*. 
The latter one is optional. Two panes are the basis for *Canonical Layouts*, an important 
Material Design concept.

Under the hood, *compose_adaptive_scaffold* uses *Jetpack WindowManager* to provide full hinge 
support. This means that you do not need to worry about screen dimensions, location and sizes of 
hinges, and hinge features like orientation. Just provide *body* and *secondary body* 
composables - everything else is handled by *compose_adaptive_scaffold*.

### Setup

The library is available through Maven Central. To include it in your apps, just add an
implementation dependency:

```groovy
dependencies {
    implementation "com.github.tkuenneth:compose_adaptive_scaffold:0.0.3"
}
```

The library uses this configuration:

| Property | Value |
| -------- | ------- |
| `namespace` | `eu.thomaskuenneth.adaptivescaffold` |
| `minSdk` | `28` |
| `targetSdk` | `33` |

Used libraries:

| Name | Version        |
| -------- |----------------|
| *Jetpack WindowManager* | `1.1.0-beta02` |
| *Jetpack Compose BOM* | `2023.04.01`   |

### How to use

Here's how the main activity of the sample app looks like:

```kotlin
class AdaptiveScaffoldDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setContent {
                    var index by rememberSaveable { mutableStateOf(0) }
                    var showSmallSecondaryBody by rememberSaveable { mutableStateOf(true) }
                    val destinations = listOf(
                        NavigationDestination(
                            icon = R.drawable.ic_android_black_24dp,
                            label = R.string.one
                        ),
                        NavigationDestination(
                            icon = R.drawable.ic_android_black_24dp,
                            label = R.string.two
                        ),
                        NavigationDestination(
                            icon = R.drawable.ic_android_black_24dp,
                            label = R.string.three
                        ),
                    )
                    MaterialTheme(
                        content = {
                            AdaptiveScaffold(
                                useDrawer = true,
                                index = index,
                                onSelectedIndexChange = { i -> index = i },
                                destinations = destinations,
                                body = { Body() },
                                smallBody = {
                                    SmallBody {
                                        showSmallSecondaryBody = !showSmallSecondaryBody
                                    }
                                },
                                secondaryBody = { SecondaryBody() },
                                smallSecondaryBody = if (showSmallSecondaryBody) {
                                    { SmallSecondaryBody() }
                                } else
                                    null
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

The `AdaptiveScaffold()` receives four composables:

1. a body
2. a secondary body
3. a small body
4. a small secondary body

Depending on the app window size, either *body* and *secondary body* or *small body*
and *small secondary body* are shown. If the device has a hinge, all features of the hinge
including its location, size, and orientation are honored.

Please note that *small secondary body* is optional.

#### Additional parameters

You can pass a top app bar. This composable will be shown if the vertical window size class is 
not compact. Rotating a smartphone to landscape mode often leads to a compact vertical window 
size class.

### Acknowledgements

*compose_adaptive_scaffold* is inspired by the Flutter package [flutter_adaptive_scaffold](https://pub.dev/packages/flutter_adaptive_scaffold).
