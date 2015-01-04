package de.mida.dv4;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import de.mida.dv4.ui.CRUD;

import java.util.Iterator;

/**
 * Created by HP on 01.01.15.
 */
public class TabsURL extends TabSheet {


    public TabsURL() {
        createTabs();
        addSelectedTabChangeListener(new SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                String selectedTabName =
                        event.getTabSheet().getSelectedTab().
                                getCaption();
                UI.getCurrent().getPage().setUriFragment(selectedTabName);
            }
        });
    }

    private void createTabs() {

        addTab((new CRUD()), "Items");
        addTab(new Label("test"), "Test");
    }

    public void selectTab() {
        String fragment = UI.getCurrent().getPage().getUriFragment();
        if (fragment == null) {
            setSelectedTab(0);
            return;
        }
        Iterator<Component> iterator = iterator();
        while (iterator.hasNext()) {
            Component tab = iterator.next();
            String name = tab.getCaption().toLowerCase();
            if (fragment.toLowerCase().equals(name)) {
                setSelectedTab(tab);
                return;
            }
        }
        setSelectedTab(0);
    }
}
