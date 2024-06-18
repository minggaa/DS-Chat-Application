package com

import akka.actor.typed.ActorSystem
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.Scene

object Client extends JFXApp {
  // Initializing Actor System
  val greeterMain: ActorSystem[ChatClient.Command] = ActorSystem(ChatClient(), "ChatSystem")
  greeterMain ! ChatClient.start

  // Loading FXML and controller
  val loader = new FXMLLoader(null, NoDependencyResolver)
  loader.load(getClass.getResourceAsStream("/view/MainWindow.fxml"))
  val border: scalafx.scene.layout.BorderPane = loader.getRoot[javafx.scene.layout.BorderPane]()
  val control = loader.getController[com.view.MainWindowController#Controller]()
  control.chatClientRef = Option(greeterMain)
  val cssResource = getClass.getResource("/style/style.css")

  // Primary stage
  stage = new PrimaryStage() {
    scene = new Scene() {
      root = border
      stylesheets = List(cssResource.toExternalForm)
    }
  }

  // Handling close
  stage.onCloseRequest = handle({
    greeterMain.terminate
  })
}
