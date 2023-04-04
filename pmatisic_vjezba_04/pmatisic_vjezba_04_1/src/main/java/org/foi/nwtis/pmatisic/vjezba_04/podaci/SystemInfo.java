package org.foi.nwtis.pmatisic.vjezba_04.podaci;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NonNull
public final class SystemInfo {
  private String nazivOS;
  private String proizvodacVM;
  private String verzijaVM;
  private String direktorijVM;
  private String direktorijTemp;
  private String direktorijKorisnik;

  public SystemInfo() {
    this.nazivOS = System.getProperty("os.name");
    this.proizvodacVM = System.getProperty("java.vendor");
    this.verzijaVM = System.getProperty("java.version");
    this.direktorijVM = System.getProperty("java.home");
    this.direktorijTemp = System.getProperty("java.io.tmpdir");
    this.direktorijKorisnik = System.getProperty("user.dir");
  }
}
