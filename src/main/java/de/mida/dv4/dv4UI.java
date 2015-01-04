package de.mida.dv4;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@Theme("dv4")
public class dv4UI extends UI {

    @Override
    protected void init(VaadinRequest request) {

//		setContent(new AdjustableLayout());

        final TabsURL tabsURL = new TabsURL();
        setContent(tabsURL);
        tabsURL.selectTab();
        getPage().addUriFragmentChangedListener(
                new Page.UriFragmentChangedListener() {
                    @Override
                    public void uriFragmentChanged(
                            Page.UriFragmentChangedEvent event) {
                        tabsURL.selectTab();
                    }
                });


    }
}
