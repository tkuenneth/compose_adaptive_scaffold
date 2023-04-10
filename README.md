# Welcome to compose_adaptive_scaffold

The aim of this repository is to make writing Jetpack Compose apps that 
support large screens and foldables a breeze. Here's a short clip that showcases how the
library works (clicking on the preview image directs you to YouTube):

[![Watch the video](https://img.youtube.com/vi/3ryCurTOXVI/mqdefault.jpg)](https://youtu.be/3ryCurTOXVI)

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

The `AdaptiveScaffold()` receives four composables: a body, a secondary body, a small body, and a small secondary body. Depending on the app window size, either *body* and *secondary body* or *small body* and *small secondary body* are shown. If the device has a hinge, all features of the hinge including its location, size, and orientation are honored. Please note that *small secondary body* is optional.

*compose_adaptive_scaffold* is inspired by the Flutter package [flutter_adaptive_scaffold](https://pub.dev/packages/flutter_adaptive_scaffold).
