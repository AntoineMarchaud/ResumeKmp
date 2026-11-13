import UIKit
import ComposeApp

/// Hosts the Compose app and owns the status bar style, which the app updates
/// depending on what is drawn behind the status bar.
final class ResumeViewController: UIViewController {

    private var lightStatusBarIcons = true

    override var preferredStatusBarStyle: UIStatusBarStyle {
        lightStatusBarIcons ? .lightContent : .darkContent
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        let composeViewController = MainViewControllerKt.MainViewController { [weak self] light in
            guard let self, self.lightStatusBarIcons != light.boolValue else { return }
            self.lightStatusBarIcons = light.boolValue
            self.setNeedsStatusBarAppearanceUpdate()
        }

        addChild(composeViewController)
        composeViewController.view.frame = view.bounds
        composeViewController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.addSubview(composeViewController.view)
        composeViewController.didMove(toParent: self)
    }
}
