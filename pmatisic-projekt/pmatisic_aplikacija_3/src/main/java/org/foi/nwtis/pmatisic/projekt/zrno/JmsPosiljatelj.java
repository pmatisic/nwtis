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

/**
 * Bean bez stanja koji se koristi za slanje JMS poruka. Koristi JMS resurse definirane u
 * aplikacijskom poslužitelju.
 *
 * @author Petar Matišić (pmatisic@foi.hr)
 */
@Stateless
public class JmsPosiljatelj {

  @Resource(mappedName = "jms/nwtis_qf_dz3")
  private ConnectionFactory connectionFactory;
  @Resource(mappedName = "jms/NWTiS_pmatisic")
  private Queue queue;

  /**
   * Šalje JMS poruku sa zadanim tekstom. Kreira vezu, sesiju i producera za slanje poruka, zatim
   * šalje poruku i zatvara sve resurse.
   *
   * @param tekstPoruke Tekst JMS poruke koju želimo poslati.
   * @return True ako je poruka uspješno poslana, false ako je došlo do iznimke.
   */
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
