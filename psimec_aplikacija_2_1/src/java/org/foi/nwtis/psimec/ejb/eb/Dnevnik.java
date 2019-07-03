package org.foi.nwtis.psimec.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Paskal
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dnevnik.findAll", query = "SELECT d FROM Dnevnik d"),
    @NamedQuery(name = "Dnevnik.findByKorime", query = "SELECT d FROM Dnevnik d WHERE d.korime = :korime")})
public class Dnevnik implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    private String korime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String url;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String ipadresa;
    @Basic(optional = false)
    @NotNull
    private int trajanje;
    @Basic(optional = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date stored;

    public Dnevnik() {
    }

    public Dnevnik(Integer id) {
        this.id = id;
    }

    public Dnevnik(Integer id, String korime, String url, String ipadresa, int trajanje, Date stored) {
        this.id = id;
        this.korime = korime;
        this.url = url;
        this.ipadresa = ipadresa;
        this.trajanje = trajanje;
        this.stored = stored;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKorime() {
        return korime;
    }

    public void setKorime(String korime) {
        this.korime = korime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIpadresa() {
        return ipadresa;
    }

    public void setIpadresa(String ipadresa) {
        this.ipadresa = ipadresa;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        this.trajanje = trajanje;
    }

    public Date getStored() {
        return stored;
    }

    public void setStored(Date stored) {
        this.stored = stored;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dnevnik)) {
            return false;
        }
        Dnevnik other = (Dnevnik) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.psimec.ejb.eb.Dnevnik[ id=" + id + " ]";
    }

}
