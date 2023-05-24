package org.foi.nwtis.pmatisic.zadaca_3.zrna;

import org.foi.nwtis.pmatisic.zadaca_3.web.SakupljacJmsPoruka;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

/**
 * JMS primatelj poruka koji sluša određeni JMS red. Primljene tekstualne poruke sprema putem klase
 * SakupljacJmsPoruka.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
@MessageDriven(mappedName = "jms/nwtis_queue_dz3",
    activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue")})
public class JmsPrimatelj implements MessageListener {

  @Inject
  SakupljacJmsPoruka sakupljacJmsPoruka;

  /**
   * Metoda koja se poziva kada stigne nova JMS poruka. Ako je poruka tipa TextMessage, tekst poruke
   * se sprema putem klase SakupljacJmsPoruka.
   *
   * @param message Nova JMS poruka
   */
  @Override
  public void onMessage(Message message) {
    if (message instanceof TextMessage) {
      try {
        var msg = (TextMessage) message;
        System.out.println("Stigla poruka: " + msg.getText());
        sakupljacJmsPoruka.spremiPoruku(msg.getText());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

}
