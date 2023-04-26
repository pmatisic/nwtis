package org.foi.nwtis.pmatisic.vjezba_06;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Vjezba_06_3", urlPatterns = {"/Vjezba_06_3"})
public class Vjezba_06_3 extends HttpServlet {
  // private DretvaVremena dv;
  List<DretvaVremena> dretve = new ArrayList<DretvaVremena>();
  private static final long serialVersionUID = 7202653895867190684L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    int brojCiklusa = Integer.parseInt(req.getParameter("brojCiklusa"));
    int trajanjeCiklusa = Integer.parseInt(req.getParameter("trajanjeCiklusa"));

    DretvaVremena dv = new DretvaVremena(brojCiklusa, trajanjeCiklusa);
    dretve.add(dv);
    dv.start();
  }

  @Override
  public void destroy() {
    for (DretvaVremena dv : dretve) {
      if (dv != null && dv.isAlive())
        dv.interrupt();
    }
    super.destroy();
  }


}
