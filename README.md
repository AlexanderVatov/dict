Java client for the [DICT](https://en.wikipedia.org/wiki/DICT) network protocol

This project is a Java implementation of a subset of the DICT 
([RFC 2229](https://www.rfc-editor.org/rfc/rfc2229.html)) protocol. The DEFINE command
is currently supported. It consists of the following  classes:

- [DICTClient](src/DICTClient.java), which implements the DICT protocol and defines an
  interface DICTClient.Backend, which is implemented by:
- [TCPClient](src/TCPClient.java), which is responsible for the communication over TCP;
- [TCPShell](src/TCPClient.java), which implements a REPL shell for communication over
  TCP. It can be invoked as follows: `java TCPShell HOSTNAME [PORT]`.

The corresponding unit test classes are found in the [test](test) subdirectory.
