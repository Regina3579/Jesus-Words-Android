import Foundation
import UserNotifications
import UIKit

class NotificationManager: NSObject, ObservableObject, UNUserNotificationCenterDelegate {
    static let shared = NotificationManager()

    override init() {
        super.init()
        UNUserNotificationCenter.current().delegate = self
    }

    func requestPermission(completion: @escaping (Bool) -> Void) {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            DispatchQueue.main.async {
                completion(granted)
            }
        }
    }

    /// Schedule notifications for the next 7 days, each with the correct word for that day.
    func scheduleDailyNotifications(hour: Int, minute: Int, allWords: [JesusWord], language: AppLanguage) {
        let center = UNUserNotificationCenter.current()
        center.removeAllPendingNotificationRequests()

        guard !allWords.isEmpty else { return }

        let isTamil = language == .tamil

        for dayOffset in 0..<7 {
            guard let futureDate = Calendar.current.date(byAdding: .day, value: dayOffset, to: Date()) else { continue }

            let dayOfYear = Calendar.current.ordinality(of: .day, in: .year, for: futureDate) ?? 1
            let wordIndex = (dayOfYear - 1) % allWords.count
            let word = allWords[wordIndex]

            let content = UNMutableNotificationContent()
            content.title = isTamil ? "✝️ இயேசுவின் வார்த்தைகள்" : "✝️ Jesus Words"
            content.subtitle = isTamil ? (word.tamilReference ?? word.reference) : word.reference
            content.body = isTamil ? (word.tamilQuote ?? word.quote) : word.quote
            content.sound = .default
            content.badge = 1

            var dateComponents = Calendar.current.dateComponents([.year, .month, .day], from: futureDate)
            dateComponents.hour = hour
            dateComponents.minute = minute

            let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: false)
            let request = UNNotificationRequest(identifier: "jesuswords-day-\(dayOffset)", content: content, trigger: trigger)
            center.add(request)
        }
    }

    func cancelAllNotifications() {
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
    }

    /// Clear the badge count from the app icon
    func clearBadge() {
        DispatchQueue.main.async {
            UIApplication.shared.applicationIconBadgeNumber = 0
        }
        // Also remove all delivered notifications from Notification Center
        UNUserNotificationCenter.current().removeAllDeliveredNotifications()
    }

    // MARK: - UNUserNotificationCenterDelegate

    /// Called when user taps on a notification to open the app
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        clearBadge()
        completionHandler()
    }

    /// Called when a notification arrives while the app is in the foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.banner, .sound])
    }
}
