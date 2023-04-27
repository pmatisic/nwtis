package org.foi.nwtis.pmatisic.vjezba_06;

public class DretvaVremena extends Thread {
  private static int brojDretve = 0;
  private int brojCiklusa;
  private int trajanjeCiklusa;
  private boolean kraj = false;

  public DretvaVremena(int brojCiklusa, int trajanjeCiklusa) {
    super("pmatisic-" + brojDretve++);
    this.brojCiklusa = brojCiklusa;
    this.trajanjeCiklusa = trajanjeCiklusa;
  }

  @Override
  public void run() {
    int br = 0;
    /*
     * try { while (br < brojCiklusa) { System.out.println("Dretva: " + brojDretve + " brojac: " +
     * br++); Thread.sleep(trajanjeCiklusa); } } catch (InterruptedException e) {
     * e.printStackTrace(); }
     */


    while (br < brojCiklusa && !this.kraj) {
      System.out.println("Dretva: " + this.getName() + " brojac: " + br++);
      try {
        Thread.sleep(trajanjeCiklusa);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  @Override
  public synchronized void start() {
    super.start();
  }

  @Override
  public void interrupt() {
    this.kraj = true;
    super.interrupt();
  }
}
