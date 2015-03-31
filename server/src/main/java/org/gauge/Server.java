package org.gauge;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by joel on 3/14/15.
 */
public class Server {

  static final Logger log = Logger.getLogger(Server.class);

  private volatile boolean isRunning;
  private volatile ServerSocket socket;
  private int port;

  public Server(int port) {
    isRunning = false;
    this.port = port;
  }

  public Server() {
    isRunning = false;
    this.port = 9000;
  }


  private void pollConnection() {
    Socket s;
    try {
      s = socket.accept();
      log.info("New Connection from: " + socket.getInetAddress());

      Packet packet = getPacket(s);
      process(s, packet);

      // close the socket when done; other sockets can now connect
      s.close();

    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
  }


  /**
   * Used to poll the socket for the packet.
   * <p/>
   * This adds another layer of abstraction over packet class,
   * number of bytes is receiived and bytes retrieved.  It is then
   * generated into a Packet instance.
   * <p/>
   * In this way, encryption can be added without altering
   * structure of packet.
   *
   * @param s
   * @return
   * @throws IOException
   */
  private Packet getPacket(Socket s) throws IOException {
    Packet result;
    DataInputStream dis;
    int length = 0;
    byte[] buffer;

    dis = new DataInputStream(s.getInputStream());
    length = dis.readInt();
    buffer = new byte[length];
    dis.read(buffer, 0, length);
    result = new Packet(buffer);

    return result;
  }


  private void sendPacket(Socket s, Packet packet) throws IOException {
    DataOutputStream dos;
    byte[] buffer = packet.toBytes();
    int length = buffer.length;

    dos = new DataOutputStream(s.getOutputStream());
    dos.writeInt(length);
    dos.write(buffer);
  }



  private void process(Socket s, Packet packet) throws IOException {
    log.info("Got Message: " + packet.toString());
    String header = packet.getHeader();
    if (header.equals("PING")) {
      sendPacket(s, new Packet("PING", "ACK"));
    } else if (header.equals("LOGIN")) {

    }
  }


  public Server start() {
    // exit and return if already running
    if (isRunning) {
      return this;
    }

    try {
      socket = new ServerSocket(port);
      Thread.sleep(1000); // give server time to start
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    log.debug("Waiting for connection on " + port + ".");

    isRunning = true;

    Runnable daemon = new Runnable() {
      public void run() {
        while (isRunning) {
          pollConnection();
        }
        log.info("Server stopped.");
      }
    };

    new Thread(daemon).start();
    log.info("Server started.");
    return this;
  }


  public Server stop() {
    isRunning = false;
    return this;
  }

}
