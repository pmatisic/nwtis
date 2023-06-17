package org.foi.nwtis.pmatisic.projekt.websocket;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArraySet;
import org.foi.nwtis.pmatisic.projekt.zrno.AerodromiLetoviFacade;
import org.foi.nwtis.pmatisic.projekt.zrno.KorisniciFacade;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/info")
public class WsInfo {

  private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

  @Inject
  KorisniciFacade korisniciFacade;

  @Inject
  AerodromiLetoviFacade alFacade;

  @OnOpen
  public void onOpen(Session session) {
    sessions.add(session);
  }

  @OnClose
  public void onClose(Session session) {
    sessions.remove(session);
  }

  @OnMessage
  public void onMessage(String message, Session session) {
    if ("dajMeteo".equals(message)) {
      sendInfo();
    }
  }

  public void sendInfo() {
    int ukupnoKorisnika = korisniciFacade.count();
    int ukupnoAerodroma = alFacade.count();
    LocalDateTime trenutnoVrijeme = LocalDateTime.now();

    String message =
        String.format("Trenutno vrijeme: %s, Ukupan broj korisnika: %d, Ukupan broj aerodroma: %d",
            trenutnoVrijeme, ukupnoKorisnika, ukupnoAerodroma);

    for (Session session : sessions) {
      try {
        session.getBasicRemote().sendText(message);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void sendInfo(int ukupnoKorisnika) {
    String message = "Ukupan broj korisnika: " + ukupnoKorisnika;
    for (Session session : sessions) {
      try {
        session.getBasicRemote().sendText(message);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void sendInfo(AerodromiLetoviFacade alFacade) {
    int ukupnoAerodroma = alFacade.count();
    String message = "Ukupan broj aerodroma: " + ukupnoAerodroma;
    for (Session session : sessions) {
      try {
        session.getBasicRemote().sendText(message);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
