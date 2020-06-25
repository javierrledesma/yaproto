# yaproto

This is yet another protocol development platform in JAVA.

Although it may be used to develop any application-level protocol defined some state machines and the corresponding messages, 
some features allow you to implement network protocols, such as routing protocols. For instance, it allows multicast or raw sockets, even in JAVA 7 
(which doesn't provide native libraries for this..

First, the Netlink protocol is partially implemented. This protocol allows communication with Linux kernel in order to publish/subscribe route updates.

Then, some routing protocols are being developed using this platform. 
* RIPv2 is full developed.
* OSPFv2 is partially developed. Only a single area version. It needs improvements to support multicast and to communicate with Linux kernel throught the netlink protocol.

Finally, a graphical deployment tool has been added. This allows to deploy some host/routers and connect them in a graphical way. They will be virtual machines. Also, some configurable parameters or routing protocol details (states, messages,...) can be showed.
