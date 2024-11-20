import SwiftUI

@main
struct iOSApp: App {
    init() {
        GMSServices.provideAPIKey("API_KEY")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
