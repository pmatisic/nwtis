package org.foi.nwtis.pmatisic.vjezba_06;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.PostavkeBazaPodataka;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Vjezba_06_2", urlPatterns = {"/Vjezba_06_2"}, initParams = {
    @WebInitParam(name = "konfiguracija", value = "NWTiS.db.config_1.xml")})
public class Vjezba_06_2 extends HttpServlet {


  private static final long serialVersionUID = -4364963269684445582L;
  private PostavkeBazaPodataka konfBP;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    String nazivDatoteke = this.getServletContext().getRealPath("WEB-INF")
        + File.separator + this.getInitParameter("konfiguracija");
    this.konfBP = new PostavkeBazaPodataka(nazivDatoteke);
    try {
      this.konfBP.ucitajKonfiguraciju();
      ispisKonfigPodataka();
    } catch (NeispravnaKonfiguracija e) {
      e.printStackTrace();
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }

  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String icaoFrom = req.getParameter("icaoFrom");
    String icaoTo = req.getParameter("icaoTo");
    String odgovor = this.ispisBazaPodataka(icaoFrom, icaoTo);

    resp.getWriter().append("<!doctype html>" + "<html><head>"
        + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>"
        + "</head><body>");
    resp.getWriter().append(odgovor);
    resp.getWriter().append("</body></html>");
  }

  private void ispisKonfigPodataka() {
    Logger.getGlobal().log(Level.INFO,
        "Baza: " + this.konfBP.getUserDatabase());
    Logger.getGlobal().log(Level.INFO,
        "Korisničko ime: " + this.konfBP.getUserUsername());
    Logger.getGlobal().log(Level.INFO,
        "Lozinka: " + this.konfBP.getUserPassword());
    Logger.getGlobal().log(Level.INFO,
        "Konekcija: " + this.konfBP.getServerDatabase());
    Logger.getGlobal().log(Level.INFO,
        "Driver: " + this.konfBP.getDriverDatabase());
  }

  private String ispisBazaPodataka(String icaoFrom, String icaoTo) {
    String baza = konfBP.getUserDatabase();
    String korime = konfBP.getUserUsername();
    String lozinka = konfBP.getUserPassword();
    String server = konfBP.getServerDatabase();
    String driver = konfBP.getDriverDatabase();

    String upit = "SELECT ICAO_FROM, ICAO_TO, COUNTRY, DIST_CTRY FROM "
        + "AIRPORTS_DISTANCE_MATRIX WHERE ICAO_FROM = ? AND ICAO_TO = ?";
    Connection con = null;
    PreparedStatement stmt = null;
    String odgovor = "";
    try {
      Class.forName(driver);
      con = DriverManager.getConnection(server + baza, korime, lozinka);
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icaoFrom);
      stmt.setString(2, icaoTo);

      float ukupno = 0;
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String drzava = rs.getString("COUNTRY");
        float udaljenost = rs.getFloat("DIST_CTRY");
        odgovor += "<p>" + drzava + " = " + udaljenost + "</p>";
        ukupno += udaljenost;
      }
      odgovor += "<p>Ukupno: " + ukupno + "</p>";
    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close();
        if (con != null && !con.isClosed())
          con.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return odgovor;
  }
}
