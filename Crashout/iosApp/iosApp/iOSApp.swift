import SwiftUI

@main
struct iOSApp: App {
    init() {
        GMSServices.provideAPIKey("AIzaSyALccYMsUHTAbDlcU5--EzNPgTKHWfuTKk")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}