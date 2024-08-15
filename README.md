# Distributed Systems ─ Chat Application

This is a chatting application built with Scala, ScalaFx, and CSS. Supporting real-time seamless communication between users privately, and in group chats. 

The distributed system is developed using a UPNP (Universal Plug and Play) Protocol, achieved used an Akka actor model, providing concurrent activities to take place.


## Set Up

Ideally, this project would run best in an IntelliJ environment.

To run the system, you are required to set up the host and port number in the "application.conf" file. 

<sub>***IMPORTANT: Set up to run the Server FIRST, then only Client.***</sub>

**Running the Server**
   
   1. Navigate to (..\src\main\resources) and open "application.conf".

   2. Change the following variables:
        ```
        Canonical host: [Set this to your local IPV4 address (local IP)]
        Canonical port: Try using 23005
        Bind host: [Use the same local IPV4 address]
        Bind port: [Use the same portnumber as canonical]
        ```
   
   3. Run the Server.

**Running the Client(s)**

   1. In the same config file, make the following changes:
        ```
        Canonical host: [Set this to your local IPV4 address (local IP)]
        Canonical port: 0 [Set to zero]
        Bind host: "" [Empty the string]
        Bind port: 0 [Set to zero]
        ```

   2. Run as many clients needed.
        - **Intellij**:
            - Go to `Run` in the top menu bar.
            - Then `Edit Configurations..`
            - Select `Modify options` > `Allow multiple instances` (under Operating System).
            - Finally click `Apply`.
