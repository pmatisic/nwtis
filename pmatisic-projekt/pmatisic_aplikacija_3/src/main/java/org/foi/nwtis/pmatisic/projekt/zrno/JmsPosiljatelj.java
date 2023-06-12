package org.foi.nwtis.pmatisic.projekt.zrno;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

@Stateless
public class JmsPosiljatelj {

  @Resource(mappedName = "jms/nwtis_qf_dz3")
  private ConnectionFactory connectionFactory;
  @Resource(mappedName = "jms/NWTiS_pmatisic")
  private Queue queue;

  public boolean saljiPoruku(String tekstPoruke) {
    boolean status = true;

    try {
      Connection connection = connectionFactory.createConnection();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      MessageProducer messageProducer = session.createProducer(queue);
      TextMessage message = session.createTextMessage();

      String poruka = tekstPoruke;

      message.setText(poruka);
      messageProducer.send(message);
      messageProducer.close();
      connection.close();
    } catch (JMSException ex) {
      ex.printStackTrace();
      status = false;
    }
    return status;
  }

}
