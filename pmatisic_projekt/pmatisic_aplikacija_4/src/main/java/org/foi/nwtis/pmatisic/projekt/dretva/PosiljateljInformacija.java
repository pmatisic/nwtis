package org.foi.nwtis.pmatisic.projekt.dretva;

import org.foi.nwtis.pmatisic.projekt.websocket.WsInfo;

public class PosiljateljInformacija extends Thread {

  private final WsInfo wsInfo;
  private final int interval;

  public PosiljateljInformacija(WsInfo wsInfo, int intervalInSeconds) {
    this.wsInfo = wsInfo;
    this.interval = intervalInSeconds * 1000;
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        wsInfo.sendInfo();
        Thread.sleep(interval);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
  }

}
