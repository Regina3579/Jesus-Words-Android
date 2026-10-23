import SwiftUI

struct ReminderSetupView: View {
    @EnvironmentObject var viewModel: WordsViewModel
    @Binding var hasCompletedSetup: Bool
    @State private var selectedTime = Date()
    @State private var currentPage = 0

    var body: some View {
        ZStack {
            BackgroundView(dayOfYear: 1)

            VStack {
                TabView(selection: $currentPage) {
                    welcomePage.tag(0)
                    reminderPage.tag(1)
                }
                .tabViewStyle(.page(indexDisplayMode: .always))
            }
        }
    }

    private var welcomePage: some View {
        VStack(spacing: 24) {
            Spacer()

            LatinCrossIcon(size: 70)
                .foregroundColor(.white)
                .shadow(color: .white.opacity(0.3), radius: 20)

            Text("Jesus Words")
                .font(.system(size: 40, weight: .bold, design: .serif))
                .foregroundColor(.white)

            Text("Daily Blessings from the\nNew Testament")
                .font(.system(size: 18, design: .serif))
                .foregroundColor(.white.opacity(0.85))
                .multilineTextAlignment(.center)

            VStack(alignment: .leading, spacing: 12) {
                featureRow(icon: "sun.max.fill", text: "365 Jesus-spoken words")
                featureRow(icon: "bell.fill", text: "Optional daily reminders")
                featureRow(icon: "clock.fill", text: "History of all blessings")
                featureRow(icon: "square.and.arrow.up", text: "Share with loved ones")
            }
            .padding(.horizontal, 40)
            .padding(.top, 16)

            Spacer()

            Button(action: { withAnimation { currentPage = 1 } }) {
                Text("Get Started")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 16)
                    .background(
                        Capsule()
                            .fill(Color.white.opacity(0.25))
                            .overlay(Capsule().stroke(Color.white.opacity(0.5), lineWidth: 1))
                    )
            }
            .padding(.horizontal, 40)
            .padding(.bottom, 60)
        }
    }

    private var reminderPage: some View {
        VStack(spacing: 24) {
            Spacer()

            Image(systemName: "bell.badge.fill")
                .font(.system(size: 50))
                .foregroundColor(.white)

            Text("Set Your Reminder")
                .font(.system(size: 28, weight: .bold, design: .serif))
                .foregroundColor(.white)

            Text("Choose when you'd like to receive\nyour daily blessing (optional)")
                .font(.system(size: 16, design: .serif))
                .foregroundColor(.white.opacity(0.8))
                .multilineTextAlignment(.center)

            DatePicker("Reminder Time", selection: $selectedTime, displayedComponents: .hourAndMinute)
                .datePickerStyle(.wheel)
                .labelsHidden()
                .colorScheme(.dark)
                .padding(.horizontal, 40)

            Spacer()

            VStack(spacing: 14) {
                Button(action: setupReminder) {
                    Text("Enable Reminders")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(
                            Capsule()
                                .fill(Color.white.opacity(0.25))
                                .overlay(Capsule().stroke(Color.white.opacity(0.5), lineWidth: 1))
                        )
                }

                Button(action: skipReminder) {
                    Text("Skip for Now")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.white.opacity(0.7))
                }
            }
            .padding(.horizontal, 40)
            .padding(.bottom, 60)
        }
    }

    private func featureRow(icon: String, text: String) -> some View {
        HStack(spacing: 14) {
            Image(systemName: icon)
                .font(.system(size: 18))
                .foregroundColor(.white.opacity(0.9))
                .frame(width: 28)
            Text(text)
                .font(.system(size: 16, design: .serif))
                .foregroundColor(.white.opacity(0.9))
        }
    }

    private func setupReminder() {
        let components = Calendar.current.dateComponents([.hour, .minute], from: selectedTime)
        let hour = components.hour ?? 7
        let minute = components.minute ?? 0

        NotificationManager.shared.requestPermission { granted in
            if granted {
                viewModel.setReminder(hour: hour, minute: minute)
            }
            withAnimation {
                hasCompletedSetup = true
            }
        }
    }

    private func skipReminder() {
        withAnimation {
            hasCompletedSetup = true
        }
    }
}
