package org.foi.nwtis.pmatisic.projekt.servis;

import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.Context;

@WebService(serviceName = "letovi")
public class WsLetovi {

  @Context
  private ServletContext konfig;

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

}
