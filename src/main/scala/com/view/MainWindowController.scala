package com.view

import akka.actor.typed.ActorRef
import com.{ChatClient, ChatServer, Client, Group, User}
import com.ChatClient.Command
import javafx.scene.control.SelectionMode
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label, ListView, TextField}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafxml.core.macros.sfxml
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.Includes.handle
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.text.Text
import scalafx.scene.input.MouseEvent

import scala.collection.mutable.ArrayBuffer


@sfxml
class MainWindowController(private val userName: TextField,
                           private val joinBtn: Button,
                           private val statusLbl: Label,
                           private val onlineUsers: ListView[String],
                           private val activeGroups: ListView[String],
                           private val inputGrpName: TextField,
                           private val btnCreateGroup: Button,
                           private val messageView: ListView[String],
                           private val messageField: TextField,
                           private val sendBtn: Button,
                           private val userGrp: Text) { //delete userGrp if anything

  var chatClientRef: Option[ActorRef[Command]] = None
  val users: ObservableBuffer[User] = new ObservableBuffer[User]()
  val groups: ObservableBuffer[Group] = new ObservableBuffer[Group]()

  private var userChatMap = Map[User, ObservableBuffer[String]]()
  private var groupChatMap = Map[Group, ObservableBuffer[String]]()
  private var thisUser: User = _

  onlineUsers.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)

  // Disable the Send buttons initially
  sendBtn.disable = true
  btnCreateGroup.disable = true
  joinBtn.disable = true
  inputGrpName.disable = true
  messageField.disable = true

  // Enable Send buttons when a message is entered
  messageField.text.onChange { (_, _, newValue) =>
    sendBtn.disable = newValue.trim.isEmpty
  }

  inputGrpName.text.onChange { (_, _, newValue) =>
    btnCreateGroup.disable = newValue.trim.isEmpty
  }

  // Enable Join button only if name is entered
  userName.text.onChange { (_, _, newValue) =>
    joinBtn.disable = newValue.trim.isEmpty
  }

  groups.onChange { (grps, _) =>
    val names: ObservableBuffer[String] = new ObservableBuffer[String]()
    for (grp <- grps) {
      names += grp.name
    }
    activeGroups.items = names
  }

  def handleJoin(action: ActionEvent): Unit = {
    if (userName.text.value.trim.nonEmpty) {
      chatClientRef foreach (_ ! ChatClient.StartJoin(userName.text()))
      sendBtn.disable = false
      joinBtn.disable = true
    }
    else {
      displayStatus("Please enter your name to join.")
    }
  }

  def setUser(user: User): Unit = {
    thisUser = user
  }

  def displayStatus(text: String): Unit = {
    statusLbl.text = text
  }

  // Update the updateList method to populate the map
  def updateList(x: Iterable[User]): Unit = {
    users.clear()
    users ++= x
    val names: ObservableBuffer[String] = new ObservableBuffer[String]()
    for (user <- users) {
      names += user.name
    }
    onlineUsers.items = names
    for (user <- x) {
      if (!userChatMap.contains(user)) {
        userChatMap += (user -> new ObservableBuffer[String]())
      }
    }
  }

  def handleSelectedUsers(mouseEvent: MouseEvent): Unit = {
    val indices = onlineUsers.selectionModel().getSelectedIndices

    if (indices.size() == 1 && indices.get(0) >= 0) {
      val index = indices.get(0)
      val user: User = users(index)
      messageView.items = userChatMap(user)
      messageField.disable = false
      activeGroups.getSelectionModel.clearSelection()
      userGrp.setText("")
    }

    else if (indices.size() > 1) {
      messageView.items = null
      inputGrpName.disable = false
      messageField.disable = true
      activeGroups.getSelectionModel.clearSelection()
      userGrp.setText("")
    }

    else if (activeGroups.getSelectionModel.getSelectedIndex < 0) {
      messageField.disable = true
      userGrp.setText("")
    }
  }

  def handleSelectedGroup(mouseEvent: MouseEvent): Unit = {
    val index = activeGroups.getSelectionModel.getSelectedIndex
    if (index >= 0) {
      val group: Group = groups(index)
      messageView.items = groupChatMap(group)
      messageField.disable = false
      onlineUsers.getSelectionModel.clearSelection()
      var groupNames: String = "("

      group.users.foreach(x => groupNames += s"${x.name}, ")
      groupNames = groupNames.dropRight(2)
      groupNames += ")"
      userGrp.setText(groupNames)
    }

    else if (onlineUsers.getSelectionModel.getSelectedIndices.size() == 0) {
      messageField.disable = true
      userGrp.setText("")
    }
  }

  // Update the handleSend method
  def handleSend(actionEvent: ActionEvent): Unit = {
    val indexUser = onlineUsers.getSelectionModel.getSelectedIndex
    val indexGroup = activeGroups.getSelectionModel.getSelectedIndex

    if (indexUser >= 0) {
      val user: User = users(indexUser)
      Client.greeterMain ! ChatClient.SendMessage(user.ref, messageField.text())
      addTextToUser(user, thisUser.name, messageField.text())
    }
    else if (indexGroup >= 0) {
      val group: Group = groups(indexGroup)
      Client.greeterMain ! ChatClient.SendMessageToGroup(group, group.users.filter(x => x.ref.path != thisUser.ref.path), messageField.text())
      addTextToGroup(group, thisUser.name, messageField.text())
    }
    messageField.clear()
  }

  def handleCreateGroup(actionEvent: ActionEvent): Unit = {
    if (inputGrpName.text.value.trim.nonEmpty) {
      val indices = onlineUsers.getSelectionModel.getSelectedIndices
      val usersGroup = ArrayBuffer[User]()
      usersGroup += thisUser
      indices.forEach { index =>
        usersGroup += users.get(index)
      }

      val group = Group(inputGrpName.text(), usersGroup.toList)
      usersGroup.foreach(_.ref ! ChatClient.CreateGroup(group: Group))
      inputGrpName.clear()
      inputGrpName.disable = true
    }
  }
  def addGroup (group: Group): Unit = {
    groups += group
    groupChatMap += (group -> new ObservableBuffer[String]())
  }

  def addTextToUser(user: User, sender: String, text: String): Unit = {
    userChatMap(user) += s"$sender: $text"
  }

  def addTextToGroup(group: Group, sender: String, text: String): Unit = {
    groupChatMap(group) += s"$sender: $text"
  }
}
