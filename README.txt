-- DISTRIBUTED CHATTING SYSTEM --

   To run the system, you are required to set up the host and port number in the "application.conf" file. 

   !!IMPORTANT!! Set up to run the Server FIRST, then only Client.

-- Running the Server --
   
   1. Navigate to (..\src\main\resources) and open "application.conf".
   2. Change the following variables:

	Canonical host: [Set this to your local IPV4 address (local IP)]
	Canonical port: Try using 23005
	Bind host: [Use the same local IPV4 address]
	Bind port: [Use the same portnumber as canonical]
   
   3. Run the Server.

-- Running the Client(s) --

   1. In the same config file, make the following changes:

	Canonical host: [Set this to your local IPV4 address (local IP)]
	Canonical port: 0 [Set to zero]
	Bind host: "" [Empty the string]
	Bind port: 0 [Set to zero]

   2. Run as many clients needed.

