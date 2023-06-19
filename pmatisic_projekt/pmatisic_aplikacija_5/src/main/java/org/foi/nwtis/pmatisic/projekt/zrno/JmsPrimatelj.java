package org.foi.nwtis.pmatisic.projekt.zrno;

import org.foi.nwtis.pmatisic.projekt.web.SakupljacJmsPoruka;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@MessageDriven(mappedName = "jms/NWTiS_pmatisic",
    activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue")})
public class JmsPrimatelj implements MessageListener {

  @Inject
  SakupljacJmsPoruka sakupljacJmsPoruka;

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
