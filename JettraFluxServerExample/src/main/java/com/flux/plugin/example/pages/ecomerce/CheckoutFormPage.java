package com.flux.plugin.example.pages.ecomerce;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.flux.core.Modifier;
import io.jettra.flux.widgets.*;
import io.jettra.core.security.widget.PageWidgetAllow;
import java.util.Map;
import com.flux.example.pages.template.TemplatePage;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER })
@io.jettra.core.server.Page(path = "/checkout-form")
public class CheckoutFormPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Checkout Form - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget addressSection = Column.of(
            Row.of(
                Column.of(Label.of("First Name").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")), TextBox.of("").modifier(new Modifier().style("width: 100%; padding: 12px; border-radius: 6px; border: 1px solid rgba(128,128,128,0.2); background: var(--background-color); color: var(--text-color);"))).modifier(new Modifier().style("flex: 1; margin-right: 15px;")),
                Column.of(Label.of("Last Name").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")), TextBox.of("").modifier(new Modifier().style("width: 100%; padding: 12px; border-radius: 6px; border: 1px solid rgba(128,128,128,0.2); background: var(--background-color); color: var(--text-color);"))).modifier(new Modifier().style("flex: 1; margin-left: 15px;"))
            ).modifier(new Modifier().style("display: flex; width: 100%; margin-bottom: 20px;")),
            Column.of(Label.of("Address").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")), TextBox.of("").modifier(new Modifier().style("width: 100%; padding: 12px; border-radius: 6px; border: 1px solid rgba(128,128,128,0.2); background: var(--background-color); color: var(--text-color);"))).modifier(new Modifier().style("margin-bottom: 20px;")),
            Row.of(
                Column.of(Label.of("City").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")), TextBox.of("").modifier(new Modifier().style("width: 100%; padding: 12px; border-radius: 6px; border: 1px solid rgba(128,128,128,0.2); background: var(--background-color); color: var(--text-color);"))).modifier(new Modifier().style("flex: 1; margin-right: 15px;")),
                Column.of(Label.of("Zip").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")), TextBox.of("").modifier(new Modifier().style("width: 100%; padding: 12px; border-radius: 6px; border: 1px solid rgba(128,128,128,0.2); background: var(--background-color); color: var(--text-color);"))).modifier(new Modifier().style("flex: 1; margin-left: 15px;"))
            ).modifier(new Modifier().style("display: flex; width: 100%; margin-bottom: 20px;")),
            Row.of(
                Checkbox.of("save"), Text.of("Save this address for next time").modifier(new Modifier().style("margin-left: 10px; color: var(--text-color); font-weight: 600; font-size: 0.9rem;"))
            ).modifier(new Modifier().style("align-items: center; margin-bottom: 20px;"))
        ).modifier(new Modifier().style("padding: 20px;"));

        Widget paymentSection = Column.of(
            Row.of(
                RadioButton.of("cc", "payment"), Text.of("Credit card").modifier(new Modifier().style("margin-left: 10px; color: var(--text-color); font-weight: 600; font-size: 0.9rem; margin-right: 20px;")),
                RadioButton.of("paypal", "payment"), Text.of("PayPal").modifier(new Modifier().style("margin-left: 10px; color: var(--text-color); font-weight: 600; font-size: 0.9rem;"))
            ).modifier(new Modifier().style("align-items: center; margin-bottom: 20px;")),
            Row.of(
                Column.of(Label.of("Name on card").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")), TextBox.of("").modifier(new Modifier().style("width: 100%; padding: 12px; border-radius: 6px; border: 1px solid rgba(128,128,128,0.2); background: var(--background-color); color: var(--text-color);"))).modifier(new Modifier().style("flex: 1; margin-right: 15px;")),
                Column.of(Label.of("Credit card number").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")), TextBox.of("").modifier(new Modifier().style("width: 100%; padding: 12px; border-radius: 6px; border: 1px solid rgba(128,128,128,0.2); background: var(--background-color); color: var(--text-color);"))).modifier(new Modifier().style("flex: 1; margin-left: 15px;"))
            ).modifier(new Modifier().style("display: flex; width: 100%; margin-bottom: 20px;")),
            Row.of(
                Column.of(Label.of("Expiration").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")), TextBox.of("").modifier(new Modifier().style("width: 100%; padding: 12px; border-radius: 6px; border: 1px solid rgba(128,128,128,0.2); background: var(--background-color); color: var(--text-color);"))).modifier(new Modifier().style("flex: 1; margin-right: 15px;")),
                Column.of(Label.of("CVV").modifier(new Modifier().style("margin-bottom: 8px; font-weight: 600; color: var(--text-color); font-size: 0.9rem;")), TextBox.of("").modifier(new Modifier().style("width: 100%; padding: 12px; border-radius: 6px; border: 1px solid rgba(128,128,128,0.2); background: var(--background-color); color: var(--text-color);"))).modifier(new Modifier().style("flex: 1; margin-left: 15px;"))
            ).modifier(new Modifier().style("display: flex; width: 100%; margin-bottom: 20px;"))
        ).modifier(new Modifier().style("padding: 20px;"));

        Widget confirmationSection = Column.of(
            Text.of("Please review your order before placing it.").modifier(new Modifier().style("color: #64748b; padding: 20px;")),
            ElevatedButton.of("Place Order").modifier(new Modifier().style("background: #22c55e; color: white; border: none; padding: 12px; border-radius: 6px; font-weight: 700; cursor: pointer; text-align: center; width: 100%; font-size: 1.1rem;"))
        ).modifier(new Modifier().style("padding: 20px;"));

        Widget checkoutAccordion = Accordion.of(
            AccordionTab.of("1. Shipping Address", addressSection),
            AccordionTab.of("2. Payment Method", paymentSection),
            AccordionTab.of("3. Confirmation", confirmationSection)
        ).modifier(new Modifier().style("flex: 2; margin-right: 30px;"));

        Widget orderSummary = Card.of(Column.of(
            Header.of(4, "Order Summary").modifier(new Modifier().style("margin-top: 0; margin-bottom: 25px; color: var(--text-color); font-weight: 700;")),
            
            buildSummaryItem("Bamboo Watch", "$65.00", "bamboo-watch.jpg"),
            buildSummaryItem("Black Watch", "$72.00", "black-watch.jpg"),
            
            Divider.of().modifier(new Modifier().style("margin-bottom: 20px;")),
            
            Row.of(Text.of("Subtotal").modifier(new Modifier().style("color: #64748b; font-weight: 600;")), Text.of("$137.00").modifier(new Modifier().style("color: var(--text-color); font-weight: 700;"))).modifier(new Modifier().style("justify-content: space-between; margin-bottom: 15px;")),
            Row.of(Text.of("Shipping").modifier(new Modifier().style("color: #64748b; font-weight: 600;")), Text.of("$5.00").modifier(new Modifier().style("color: var(--text-color); font-weight: 700;"))).modifier(new Modifier().style("justify-content: space-between; margin-bottom: 15px;")),
            Row.of(Text.of("Taxes").modifier(new Modifier().style("color: #64748b; font-weight: 600;")), Text.of("$10.96").modifier(new Modifier().style("color: var(--text-color); font-weight: 700;"))).modifier(new Modifier().style("justify-content: space-between; margin-bottom: 25px;")),
            
            Divider.of().modifier(new Modifier().style("margin-bottom: 20px;")),
            
            Row.of(Text.of("Total").modifier(new Modifier().style("color: var(--text-color); font-weight: 700; font-size: 1.1rem;")), Text.of("$152.96").modifier(new Modifier().style("color: var(--primary-color); font-weight: 700; font-size: 1.25rem;"))).modifier(new Modifier().style("justify-content: space-between; margin-bottom: 10px;"))
            
        )).modifier(new Modifier().style("flex: 1; min-width: 300px; padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: var(--surface-color); border: 1px solid rgba(128,128,128,0.2); align-self: flex-start;"));

        Widget mainRow = Row.of(checkoutAccordion, orderSummary).modifier(new Modifier().style("display: flex; flex-wrap: wrap; width: 100%;"));

        return Column.of(
            Header.of(2, "Checkout").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 30px; color: var(--text-color);")),
            mainRow
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px;"));
    }

    private Widget buildSummaryItem(String name, String price, String imgName) {
        return Row.of(
            Image.of("https://primefaces.org/cdn/primeng/images/demo/product/" + imgName).modifier(new Modifier().style("width: 60px; height: 60px; object-fit: cover; border-radius: 8px; border: 1px solid rgba(128,128,128,0.2); margin-right: 15px;")),
            Column.of(
                Text.of(name).modifier(new Modifier().style("margin: 0 0 5px 0; color: var(--text-color); font-weight: 700;")),
                Text.of("Qty: 1").modifier(new Modifier().style("color: #64748b; font-size: 0.85rem; font-weight: 600;"))
            ).modifier(new Modifier().style("flex: 1;")),
            Text.of(price).modifier(new Modifier().style("font-weight: 700; color: var(--text-color);"))
        ).modifier(new Modifier().style("align-items: center; width: 100%; margin-bottom: 20px;"));
    }
}
