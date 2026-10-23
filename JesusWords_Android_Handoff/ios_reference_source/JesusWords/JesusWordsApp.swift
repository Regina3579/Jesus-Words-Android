import SwiftUI

@main
struct JesusWordsApp: App {
    @StateObject private var viewModel = WordsViewModel()
    @AppStorage("hasCompletedSetup") private var hasCompletedSetup = false
    @Environment(\.scenePhase) private var scenePhase

    var body: some Scene {
        WindowGroup {
            if hasCompletedSetup {
                ContentView()
                    .environmentObject(viewModel)
            } else {
                ReminderSetupView(hasCompletedSetup: $hasCompletedSetup)
                    .environmentObject(viewModel)
            }
        }
        .onChange(of: scenePhase) { newPhase in
            if newPhase == .active {
                // Clear badge when user opens or returns to the app
                NotificationManager.shared.clearBadge()
            }
        }
    }
}
