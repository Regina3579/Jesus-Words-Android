import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var viewModel: WordsViewModel
    @AppStorage("hasSeenLanguageTip_v3") private var hasSeenLanguageTip = false
    @AppStorage("hasSeenThemesTip_v2") private var hasSeenThemesTip = false
    @State private var selectedTime = Date()
    @State private var showingSaved = false

    private var isTamil: Bool { viewModel.selectedLanguage == .tamil }

    var body: some View {
        ZStack {
            ThemeBackgroundView(theme: viewModel.selectedTheme)

            ScrollView {
                VStack(spacing: 24) {
                    // Header
                    VStack(spacing: 8) {
                        Text(isTamil ? "அமைப்புகள்" : "Settings")
                            .font(.system(size: 32, weight: .bold, design: viewModel.selectedTheme.fontDesign))
                            .foregroundColor(.white)
                        Text(isTamil ? "உங்கள் அனுபவத்தைத் தனிப்பயனாக்குங்கள்" : "Customize your experience")
                            .font(.subheadline)
                            .foregroundColor(.white.opacity(0.8))
                    }
                    .padding(.top, 20)

                    // Reminder Time
                    VStack(spacing: 16) {
                        HStack {
                            Image(systemName: "bell.fill")
                                .foregroundColor(.white)
                            Text(isTamil ? "நினைவூட்டல் நேரம்" : "Reminder Time")
                                .font(.system(size: 18, weight: .semibold, design: viewModel.selectedTheme.fontDesign))
                                .foregroundColor(.white)
                            Spacer()
                        }

                        DatePicker("", selection: $selectedTime, displayedComponents: .hourAndMinute)
                            .datePickerStyle(.wheel)
                            .labelsHidden()
                            .colorScheme(.dark)

                        Button(action: saveReminder) {
                            HStack {
                                Image(systemName: showingSaved ? "checkmark.circle.fill" : "bell.badge")
                                Text(showingSaved
                                    ? (isTamil ? "சேமிக்கப்பட்டது!" : "Saved!")
                                    : (isTamil ? "நினைவூட்டலைப் புதுப்பி" : "Update Reminder"))
                            }
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                            .background(
                                Capsule()
                                    .fill(showingSaved ? Color.green.opacity(0.4) : Color.white.opacity(0.2))
                                    .overlay(Capsule().stroke(Color.white.opacity(0.4), lineWidth: 1))
                            )
                        }
                    }
                    .padding(20)
                    .frame(maxWidth: .infinity)
                    .background(
                        RoundedRectangle(cornerRadius: 20)
                            .fill(.ultraThinMaterial)
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .fill(Color.black.opacity(0.22))
                            )
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .stroke(Color.white.opacity(0.28), lineWidth: 1)
                            )
                    )
                    .padding(.horizontal, 16)

                    // Replay the first-launch tips (switches to Today and shows them in order)
                    Button {
                        hasSeenThemesTip = false
                        hasSeenLanguageTip = false
                    } label: {
                        HStack(spacing: 10) {
                            Image(systemName: "lightbulb.fill")
                            Text(isTamil ? "குறிப்புகளை மீண்டும் காட்டு" : "Show Tips Again")
                            Spacer()
                            Image(systemName: "chevron.right").font(.system(size: 13, weight: .semibold))
                        }
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(.white)
                        .padding(18)
                        .background(
                            RoundedRectangle(cornerRadius: 20)
                                .fill(.ultraThinMaterial)
                                .overlay(RoundedRectangle(cornerRadius: 20).fill(Color.black.opacity(0.22)))
                                .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.white.opacity(0.28), lineWidth: 1))
                        )
                    }
                    .buttonStyle(.plain)
                    .padding(.horizontal, 16)

                    // About Section
                    VStack(spacing: 12) {
                        HStack {
                            Image(systemName: "info.circle.fill")
                                .foregroundColor(.white)
                            Text(isTamil ? "பற்றி" : "About")
                                .font(.system(size: 18, weight: .semibold, design: viewModel.selectedTheme.fontDesign))
                                .foregroundColor(.white)
                            Spacer()
                        }

                        Text(isTamil
                            ? "இயேசுவின் வார்த்தைகள் புதிய ஏற்பாட்டிலிருந்து இயேசு கிறிஸ்துவின் 365 உண்மையான வார்த்தைகளை உங்களுக்குக் கொண்டுவருகிறது. ஒவ்வொரு நாளையும் அவரது ஞானம், ஆறுதல் மற்றும் ஊக்கத்துடன் தொடங்குங்கள்."
                            : "Jesus Words brings you 365 authentic spoken words of Jesus Christ from the New Testament. Start each day with His wisdom, comfort, and encouragement.")
                            .font(.system(size: 14, design: viewModel.selectedTheme.fontDesign))
                            .foregroundColor(.white.opacity(0.8))
                            .lineSpacing(4)

                        Divider().background(Color.white.opacity(0.3))

                        VStack(spacing: 8) {
                            aboutRow(
                                label: isTamil ? "மொத்த வார்த்தைகள்" : "Total Words",
                                value: "365"
                            )
                            aboutRow(
                                label: isTamil ? "வகைகள்" : "Categories",
                                value: "8"
                            )
                            aboutRow(
                                label: isTamil ? "ஆதாரம்" : "Source",
                                value: isTamil ? "புதிய ஏற்பாடு (NIV)" : "New Testament (NIV)"
                            )
                            aboutRow(
                                label: isTamil ? "பதிப்பு" : "Version",
                                value: "1.1"
                            )
                        }
                    }
                    .padding(20)
                    .frame(maxWidth: .infinity)
                    .background(
                        RoundedRectangle(cornerRadius: 20)
                            .fill(.ultraThinMaterial)
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .fill(Color.black.opacity(0.22))
                            )
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .stroke(Color.white.opacity(0.28), lineWidth: 1)
                            )
                    )
                    .padding(.horizontal, 16)

                    Spacer().frame(height: 100)
                }
            }
        }
        .onAppear {
            let hour = UserDefaults.standard.integer(forKey: "reminderHour")
            let minute = UserDefaults.standard.integer(forKey: "reminderMinute")
            var components = DateComponents()
            components.hour = hour > 0 ? hour : 7
            components.minute = minute
            if let date = Calendar.current.date(from: components) {
                selectedTime = date
            }
        }
    }

    private func aboutRow(label: String, value: String) -> some View {
        HStack {
            Text(label)
                .font(.system(size: 14))
                .foregroundColor(.white.opacity(0.6))
            Spacer()
            Text(value)
                .font(.system(size: 14, weight: .semibold))
                .foregroundColor(.white.opacity(0.9))
        }
    }

    private func saveReminder() {
        let components = Calendar.current.dateComponents([.hour, .minute], from: selectedTime)
        let hour = components.hour ?? 7
        let minute = components.minute ?? 0
        viewModel.setReminder(hour: hour, minute: minute)

        withAnimation {
            showingSaved = true
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            withAnimation {
                showingSaved = false
            }
        }
    }
}
