package de.mida.dv4;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by HP on 31.12.14.
 */
public class AdjustableLayout extends HorizontalSplitPanel {

    private static final String LIPSUM = "Lorem ipsum dolor …";

    public AdjustableLayout() {
        setFirstComponent(createMenu());
        setSecondComponent(createContentPanel());
        setSplitPosition(10, Unit.PERCENTAGE);
        setSizeFull();
    }

    private Tree createMenu() {
        Tree menu = new Tree();
        for (int i = 1; i < 6; i++) {
            String item = "item" + i;
            String childItem = "subitem" + i;
            menu.addItem(item);
            menu.addItem(childItem);
            menu.setParent(childItem, item);
            menu.setChildrenAllowed(childItem, false);
        }
        return menu;
    }

    private Component createContentPanel() {
        VerticalSplitPanel contentPanel = new VerticalSplitPanel();
        contentPanel.setFirstComponent(createEditorPanel());
        contentPanel.setSecondComponent(createTable());
        contentPanel.setSplitPosition(80, Unit.PERCENTAGE);
        return contentPanel;
    }

    private Component createTable() {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream("db.properties"));
            JDBCConnectionPool pool = new SimpleJDBCConnectionPool("com.mysql.jdbc.Driver",
                    p.getProperty("url"),
                    p.getProperty("user"),
                    p.getProperty("pw"));
            QueryDelegate query = new TableQuery("problem", pool);
            Table table = new Table("My Table", new SQLContainer(query));
            table.setSizeFull();
            return table;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Component createEditorPanel() {
        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(
                "<b>Help</b> <br />" + LIPSUM);
        HorizontalSplitPanel editorPanel =
                new HorizontalSplitPanel();
        RichTextArea editor = new RichTextArea();
        editor.setSizeFull();
        editor.setValue(LIPSUM);
        editorPanel.setFirstComponent(editor);
        editorPanel.setSecondComponent(
                new Label(safeHtml.asString(), ContentMode.HTML));
        editorPanel.setSplitPosition(80, Unit.PERCENTAGE);
        return editorPanel;
    }
}
