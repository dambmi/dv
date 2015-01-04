package de.mida.dv4.ui;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;

import java.sql.SQLException;

/**
 * Created by HP on 01.01.15.
 */
public class CRUD extends HorizontalSplitPanel {

    private final static Action
            ACTION_ADD = new Action("Add");
    private final static Action
            ACTION_DELETE = new Action("Delete");
    private int id = 0;

    public CRUD() {
//        fillContainer(products);
        try {
            setFirstComponent(createTable());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Table createTable() throws SQLException {
        JDBCConnectionPool pool = new SimpleJDBCConnectionPool("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/items", "root", "dobermana#");
        QueryDelegate query = new TableQuery("sold", pool);
        Table table = new Table("My Table", new SQLContainer(query));
        table.setSizeFull();
        table.setSelectable(true);
        table.setSizeFull();
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (MouseEventDetails.MouseButton.LEFT.getName().equals
                        (event.getButtonName())) {
                    setSecondComponent
                            (createForm(event.getItem()));
                }
            }
        });
        table.addActionHandler(new Action.Handler() {
            @Override
            public void handleAction
                    (Action action, Object sender, Object target) {
                if (ACTION_DELETE == action) {
//                    products.removeItem(target);
                }
                if (ACTION_ADD == action) {
//                    products.addBean(new Item());
                }
            }

            @Override
            public Action[] getActions
                    (Object target, Object sender) {
                return new Action[]
                        {ACTION_ADD, ACTION_DELETE};
            }
        });
        return table;
    }

    private Layout createForm(com.vaadin.data.Item item) {
        FormLayout layout = new FormLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        final FieldGroup group = new FieldGroup(item);
        for (Object propertyId :
                group.getUnboundPropertyIds()) {
            layout.addComponent
                    (group.buildAndBind(propertyId));
        }
        Button button = new Button("Commmit");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    group.commit();
                } catch (FieldGroup.CommitException e) {
                    Notification.show(e.getCause().getMessage(),
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        layout.addComponent(button);
        return layout;
    }
}
