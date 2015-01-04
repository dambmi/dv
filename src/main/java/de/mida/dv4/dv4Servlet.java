package de.mida.dv4;

import com.vaadin.server.VaadinServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(
        asyncSupported = false,
        urlPatterns = {"/*", "/VAADIN/*"},
        initParams = {
                @WebInitParam(name = "ui", value = "de.mida.dv4.dv4UI")
        })
public class dv4Servlet extends VaadinServlet {
}
