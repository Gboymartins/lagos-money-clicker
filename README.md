# Lagos Money Clicker

A native Android auto-clicker starter project. It uses Android AccessibilityService gesture dispatch and a floating overlay.
The user must explicitly enable Accessibility and overlay permissions. It does not run invisibly.

## Build without installing tools on your Mac

1. Create a new GitHub repository named `lagos-money-clicker`.
2. Upload the contents of this folder to the repository using GitHub's web interface (do not upload the outer ZIP itself).
3. Open the repository's **Actions** tab.
4. If prompted, enable GitHub Actions.
5. The workflow named **Build Android APK** runs after the first push. You can also choose **Run workflow**.
6. Open the completed workflow run, scroll to **Artifacts**, and download `Lagos-Money-Clicker-debug`.
7. Extract the downloaded artifact ZIP to get `app-debug.apk`, then transfer it to your Android phone and install it. You may need to allow installation from your browser or file manager.

## Android setup

1. Open the app.
2. Tap **Enable Accessibility Service** and enable Lagos Money Clicker in Android Accessibility settings.
3. Return to the app and allow **Display over other apps**.
4. Open floating controls.
5. Drag the gold target markers to the screen positions you want tapped.
6. Press START. Press STOP to stop immediately.

## Important limitations

- Android controls actual gesture dispatch timing. A requested 1 ms interval is not a guarantee of 1,000 physical taps per second.
- Android accessibility gesture dispatch has a minimum gesture duration and system scheduling overhead.
- Test in a safe app or blank screen first. Some apps and games prohibit automation.
- This starter project is unsigned debug output, intended for personal testing, not Play Store distribution.
