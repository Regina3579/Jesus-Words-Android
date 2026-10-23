import SwiftUI

struct ContentView: View {
    @EnvironmentObject var viewModel: WordsViewModel
    @State private var selectedTab = 0
    // First-launch tips: language tip (HomeView) first, then this Themes tip.
    @AppStorage("hasSeenLanguageTip_v3") private var hasSeenLanguageTip = false
    @AppStorage("hasSeenThemesTip_v2") private var hasSeenThemesTip = false
    @State private var showThemesTip = false
    @State private var tabFrames: [Int: CGRect] = [:]

    var body: some View {
        ZStack(alignment: .bottom) {
            TabView(selection: $selectedTab) {
                HomeView()
                    .tag(0)

                HistoryView()
                    .tag(1)

                ThemeView()
                    .tag(2)

                WallpapersView()
                    .tag(3)

                SettingsView()
                    .tag(4)
            }
            .tabViewStyle(.page(indexDisplayMode: .never))

            // Custom Tab Bar
            HStack(spacing: 0) {
                tabButton(icon: "house.fill", label: "Today", tag: 0)
                tabButton(icon: "clock.fill", label: "History", tag: 1)
                tabButton(icon: "paintpalette.fill", label: "Themes", tag: 2)
                tabButton(icon: "photo.stack.fill", label: "Wallpapers", tag: 3)
                tabButton(icon: "gearshape.fill", label: "Settings", tag: 4)
            }
            .padding(.horizontal, 10)
            .padding(.vertical, 12)
            .background(
                RoundedRectangle(cornerRadius: 25)
                    .fill(.ultraThinMaterial)
                    .shadow(color: .black.opacity(0.2), radius: 10, y: 5)
            )
            .padding(.horizontal, 16)
            .padding(.bottom, 20)
        }
        .onPreferenceChange(TabButtonFramesKey.self) { tabFrames = $0 }
        .onAppear { scheduleThemesTip() }
        .onChange(of: hasSeenLanguageTip) { seen in
            if seen {
                scheduleThemesTip()
            } else {
                withAnimation { selectedTab = 0 }   // "Show Tips Again" from Settings
            }
        }
        .onChange(of: selectedTab) { tab in
            if tab == 2 { finishThemesTip() }
        }
        .overlay {
            if showThemesTip, let frame = tabFrames[2] {
                ThemesTipView(tabFrame: frame) { finishThemesTip() }
                    .transition(.opacity)
            }
        }
    }

    private func scheduleThemesTip() {
        guard hasSeenLanguageTip, !hasSeenThemesTip, !showThemesTip else { return }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.6) {
            if !hasSeenThemesTip { showThemesTip = true }
        }
    }

    private func finishThemesTip() {
        hasSeenThemesTip = true
        showThemesTip = false
    }

    private var isTamil: Bool { viewModel.selectedLanguage == .tamil }

    private func tabButton(icon: String, label: String, tag: Int) -> some View {
        let tamilLabels = ["இன்று", "வரலாறு", "தீம்கள்", "வால்பேப்பர்", "அமைப்புகள்"]
        let displayLabel = isTamil ? tamilLabels[tag] : label
        return Button(action: {
            withAnimation(.easeInOut(duration: 0.2)) {
                selectedTab = tag
            }
        }) {
            VStack(spacing: 4) {
                Image(systemName: icon)
                    .font(.system(size: 18))
                Text(displayLabel)
                    .font(.system(size: 10, weight: .medium))
                    .lineLimit(1)
                    .minimumScaleFactor(0.7)
            }
            .foregroundColor(selectedTab == tag ? .white : .white.opacity(0.5))
            .frame(maxWidth: .infinity)
        }
        .background(
            GeometryReader { g in
                Color.clear.preference(key: TabButtonFramesKey.self, value: [tag: g.frame(in: .global)])
            }
        )
    }
}
