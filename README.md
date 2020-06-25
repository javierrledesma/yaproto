# yaproto

This is yet another protocol development platform in JAVA.

Although it may be used to develop any application-level protocol given the state machine and the messages, 
some features allow you to implement network-level protocols, such as routing protocols. For instance, it allows multicast or raw sockets.

The Netlink protocol is partially implemented. This protocol allows communication with Linux kernel in order to publish/subscribe route updates.

Then, some routing protocols are being developed using this platform. 
* RIPv2. 
* OSPFv2. Status: partially developed. It only supports a single area (area 0). It also needs some improvements to support multicast in a right way and to communicate with Linux kernel through the netlink protocol.

Finally, a graphical deployment tool has been added. This allows to deploy some host/routers and connect them in a graphical way. They will become virtual machines you can connect to. Also, some configurable parameters or routing protocol details (states, messages,...) can be showed.
