package org.foi.nwtis.psimec.web.filteri;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.ejb.eb.Dnevnik;
import org.foi.nwtis.psimec.ejb.sb.DnevnikFacade;
import org.foi.nwtis.psimec.web.pomocni.JsonRestModel;

/**
 *
 * @author Paskal
 */
@WebFilter(filterName = "FilterZahtjeva", urlPatterns = {"*.xhtml"})
public class FilterZahtjeva implements Filter {

    DnevnikFacade dnevnikFacade = lookupDnevnikFacadeBean();

    private static final boolean debug = true;

    private FilterConfig filterConfig = null;

    public FilterZahtjeva() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        long pocetakObrade = System.currentTimeMillis();
        Dnevnik dnevnik = new Dnevnik();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String url = httpRequest.getRequestURI();
        String ipAdresa = httpRequest.getRemoteAddr();
        String korime;
        HttpSession sesija = httpRequest.getSession(false);
        if (sesija != null && sesija.getAttribute("korime") != null && sesija.getAttribute("lozinka") != null) {
            korime = (String) sesija.getAttribute("korime");
        } else {
            korime = "nepoznato";
        }
        chain.doFilter(request, response);
        long krajObrade = System.currentTimeMillis();

        dnevnik.setIpadresa(ipAdresa);
        dnevnik.setUrl(url);
        dnevnik.setStored(new java.sql.Timestamp(new java.util.Date().getTime()));
        dnevnik.setKorime(korime);
        dnevnik.setTrajanje((int) (krajObrade - pocetakObrade));
        dnevnikFacade.create(dnevnik);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
    }

    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("FilterZahtjeva()");
        }
        StringBuffer sb = new StringBuffer("FilterZahtjeva(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private DnevnikFacade lookupDnevnikFacadeBean() {
        try {
            Context c = new InitialContext();
            return (DnevnikFacade) c.lookup("java:global/psimec_aplikacija_2/psimec_aplikacija_2_1/DnevnikFacade!org.foi.nwtis.psimec.ejb.sb.DnevnikFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
