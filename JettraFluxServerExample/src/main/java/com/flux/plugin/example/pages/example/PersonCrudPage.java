package com.flux.plugin.example.pages.example;

import com.flux.plugin.example.model.PersonModel;
import com.flux.plugin.example.entity.Person;
import com.flux.plugin.example.services.PersonService;
import com.flux.plugin.example.converter.PersonModelConverter;

import io.jettra.flux.widgets.Column;
import io.jettra.flux.widgets.Card;
import io.jettra.flux.widgets.TextField;
import io.jettra.flux.widgets.Form;
import io.jettra.flux.widgets.Paragraph;
import io.jettra.flux.widgets.Header;
import io.jettra.flux.widgets.Label;
import io.jettra.flux.widgets.ElevatedButton;
import io.jettra.flux.widgets.Row;
import io.jettra.flux.widgets.Alert;
import io.jettra.flux.widgets.Text;
import io.jettra.flux.widgets.Datatable;
import io.jettra.flux.widgets.TD;
import io.jettra.flux.widgets.Modal;
import io.jettra.flux.widgets.Button;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.core.server.Page;
import io.jettra.flux.core.Modifier;
import io.jettra.core.inject.annotation.Inject;
import io.jettra.core.security.widget.PageWidgetAllow;
import io.jettra.server.JettraServer;
import io.jettra.flux.binding.FluxBinder;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;
import io.jettra.rules.core.RuleResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER })
@Page(path = "/person-crud")
public class PersonCrudPage extends com.flux.example.pages.template.TemplatePage {

    @Inject
    private PersonService personService;
    
    @Inject
    private PersonModelConverter converter;

    private void saveForm(HttpExchange exchange, Map<String, String> params) {
        PersonModel personForm = new PersonModel();
        
        List<RuleResult> results = new FluxBinder(personForm)
                .bind(params)
                .compute()
                .validate();

        boolean hasErrors = false;
        StringBuilder errorMsg = new StringBuilder();
        for (RuleResult result : results) {
            if (!result.isValid()) {
                hasErrors = true;
                if (errorMsg.length() > 0) errorMsg.append(" | ");
                errorMsg.append(result.getMessage());
            }
        }

        if (hasErrors) {
            try {
                redirect(exchange, "/person-crud?error=" + java.net.URLEncoder.encode(errorMsg.toString(), "UTF-8"));
            } catch (Exception e) { }
        } else {
            // Asumiremos que si 'isEdit' es true, llamamos a update si el API lo soporta. Pero PersonService solo tiene save.
            personService.save(converter.toRecord(personForm));
            try {
                redirect(exchange, "/person-crud?success=true");
            } catch (Exception e) { }
        }
    }

    private void deleteRecord(HttpExchange exchange, Map<String, String> params) {
        String email = params.get("delete_email");
        if (email != null && !email.isEmpty()) {
            personService.delete(email); // asume que delete recibe el email
            try {
                redirect(exchange, "/person-crud?success=true");
            } catch (Exception e) { }
        }
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        // Búsqueda rudimentaria en memoria ya que PersonService no soporta paginación en findAll() actualmente
        String query = params.containsKey("q") ? params.get("q").toLowerCase() : "";

        // Alertas
        Widget alert = null;
        if ("true".equals(params.get("success"))) {
            alert = Alert.of(Text.of("Operación realizada con éxito.")).severity("success")
                         .modifier(new Modifier().style("margin-bottom: 20px; width: 100%;"));
        } else if (params.containsKey("error")) {
            String rawErr = params.get("error").replace("+", " ");
            alert = Alert.of(Text.of(rawErr)).severity("danger")
                         .modifier(new Modifier().style("margin-bottom: 20px; width: 100%;"));
        }

        // --- 1. Toolbar y Búsqueda ---
        Widget searchInput = TextField.of("Buscar", "Buscar...")
                .id("searchQuery").value(query)
                .modifier(new Modifier().style("padding: 8px; border-radius: 4px; border: 1px solid #ccc; margin-right: 10px;"));
        Widget searchBtn = Button.of(Text.of("Buscar"))
                .attribute("onclick", "window.location.href='/person-crud?q=' + document.getElementById('searchQuery').value")
                .modifier(new Modifier().style("margin-right: 20px; background-color: #0d6efd; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer;"));

        Widget addBtn = Button.of(Text.of("Nuevo Registro"))
            .modifier(new Modifier().style("margin-right: 10px; background-color: #28a745; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer;"))
            .attribute("onclick", "document.getElementById('name').value=''; document.getElementById('email').value=''; document.getElementById('age').value=''; document.getElementById('modal_create').style.display='flex'");
            
        Widget printBtn = Button.of(Text.of("Imprimir / Ver"))
            .modifier(new Modifier().style("margin-right: 10px; background-color: #17a2b8; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer;"))
            .attribute("onclick", "document.getElementById('modal_report').style.display='flex'");
            
        Widget toolbar = Row.of(searchInput, searchBtn, addBtn, printBtn)
            .modifier(new Modifier().style("padding: 10px 0; align-items: center;"));

        // --- 2. DataTable ---
        List<Widget> headers = new ArrayList<>();
        headers.add(TD.of(Text.of("NOMBRE")));
        headers.add(TD.of(Text.of("CORREO")));
        headers.add(TD.of(Text.of("EDAD")));
        headers.add(TD.of(Text.of("ACCIONES")));

        List<List<Widget>> rows = new ArrayList<>();
        List<Person> rawPersons = personService.findAll();
        List<PersonModel> persons = new ArrayList<>();
        if (rawPersons != null) {
            persons = rawPersons.stream()
                .map(converter::toModel)
                .filter(p -> query.isEmpty() || (p.getName() != null && p.getName().toLowerCase().contains(query)) || (p.getEmail() != null && p.getEmail().toLowerCase().contains(query)))
                .collect(Collectors.toList());
        }
        
        for (PersonModel p : persons) {
            List<Widget> r = new ArrayList<>();
            r.add(TD.of(Text.of(p.getName() != null ? p.getName() : "")));
            r.add(TD.of(Text.of(p.getEmail() != null ? p.getEmail() : "")));
            r.add(TD.of(Text.of(p.getAge() != null ? String.valueOf(p.getAge()) : "")));
            
            List<Widget> actions = new ArrayList<>();
            String editScript = String.format("document.getElementById('name').value='%s'; document.getElementById('email').value='%s'; document.getElementById('age').value='%s'; document.getElementById('modal_create').style.display='flex'",
                p.getName(), p.getEmail(), p.getAge());

            actions.add(Button.of(Text.of("Editar"))
                .modifier(new Modifier().style("margin-right: 5px; background-color: #f39c12; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;"))
                .attribute("onclick", editScript));
                
            String deleteScript = String.format("document.getElementById('delete_email').value='%s'; document.getElementById('modal_delete').style.display='flex'", p.getEmail());
            actions.add(Button.of(Text.of("Eliminar"))
                .modifier(new Modifier().style("background-color: #e74c3c; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;"))
                .attribute("onclick", deleteScript));
            r.add(TD.of(Row.of(actions.toArray(new Widget[0]))));
            rows.add(r);
        }

        Widget table = Datatable.ofWidgets(headers, rows).modifier(new Modifier().style("width: 100%; border-collapse: collapse; margin-top: 15px;"));

        // --- 3. Modals ---
        Widget createForm = Form.of(Card.of(Column.of(
            Header.of(4, "Registrar / Editar Persona"),
            Label.of("Nombre").forId("name"),
            TextField.of("Nombre", "Ingrese nombre").id("name").binding(PersonModel.class, "name").modifier(new Modifier().style("width: 100%; margin-bottom: 10px; padding: 8px;")),
            Label.of("Correo").forId("email"),
            TextField.of("Correo", "Ingrese correo").id("email").binding(PersonModel.class, "email").modifier(new Modifier().style("width: 100%; margin-bottom: 10px; padding: 8px;")),
            Label.of("Edad").forId("age"),
            TextField.of("Edad", "Ingrese edad").id("age").binding(PersonModel.class, "age").attribute("type", "number").attribute("min", "0").modifier(new Modifier().style("width: 100%; margin-bottom: 20px; padding: 8px;")),
            Row.of(
                ElevatedButton.of(Text.of("Guardar")).modifier(new Modifier().style("background-color: #0d6efd; color: white; padding: 10px 15px; border:none; border-radius:6px; cursor:pointer;")),
                Button.of(Text.of("Cancelar")).attribute("type", "button").attribute("onclick", "document.getElementById('modal_create').style.display='none'")
                     .modifier(new Modifier().style("margin-left: 10px; padding: 10px 15px; border-radius:6px; cursor:pointer; border: 1px solid #ccc;"))
            )
        ).modifier(new Modifier().style("width: 100%; min-width: 400px; padding: 20px; background: white; border-radius: 8px;"))))
        .action(JettraServer.resolvePath("/person-crud?_action_method=saveForm")).method("POST").attribute("id", "personForm");

        Widget createModal = Modal.of(createForm).attribute("id", "modal_create");
        
        Widget deleteForm = Form.of(Card.of(Column.of(
            Header.of(4, "Confirmar Eliminación"),
            Paragraph.of("¿Está seguro de que desea eliminar este registro?"),
            TextField.of("Email", "").id("delete_email").attribute("name", "delete_email").attribute("type", "hidden"),
            Row.of(
                ElevatedButton.of(Text.of("Eliminar")).modifier(new Modifier().style("background-color: #e74c3c; color: white; padding: 10px 15px; border:none; border-radius:6px; cursor:pointer;")),
                Button.of(Text.of("Cancelar")).attribute("type", "button").attribute("onclick", "document.getElementById('modal_delete').style.display='none'")
                     .modifier(new Modifier().style("margin-left: 10px; padding: 10px 15px; border-radius:6px; cursor:pointer; border: 1px solid #ccc;"))
            )
        ).modifier(new Modifier().style("width: 100%; padding: 20px; background: white; border-radius: 8px;"))))
        .action(JettraServer.resolvePath("/person-crud?_action_method=deleteRecord")).method("POST").attribute("id", "deleteForm");

        Widget deleteModal = Modal.of(deleteForm).attribute("id", "modal_delete");
            
        Widget reportModal = Modal.of(Card.of(Column.of(
            Header.of(4, "Vista de Reporte"),
            Paragraph.of("Generando vista de impresión..."),
            Button.of(Text.of("Cerrar")).attribute("onclick", "document.getElementById('modal_report').style.display='none'")
                .modifier(new Modifier().style("margin-top: 15px; padding: 10px 15px; border-radius:6px; cursor:pointer; border: 1px solid #ccc;"))
        ).modifier(new Modifier().style("width: 100%; min-width: 300px; padding: 20px; background: white; border-radius: 8px;")))).attribute("id", "modal_report");

        // Reglas JS
        Widget webRulesScriptWidget = FluxBinder.generateWebRulesWidget(PersonModel.class, "personForm");

        // --- Layout ---
        List<Widget> centerWidgets = new ArrayList<>();
        centerWidgets.add(Header.of(2, "Gestión de Personas (CRUD)").modifier(new Modifier().style("margin-bottom: 20px; font-family: sans-serif;")));
        if (alert != null) centerWidgets.add(alert);
        centerWidgets.add(toolbar);
        centerWidgets.add(table);
        centerWidgets.add(createModal);
        centerWidgets.add(deleteModal);
        centerWidgets.add(reportModal);
        centerWidgets.add(webRulesScriptWidget);

        return Column.of(centerWidgets.toArray(new Widget[0]))
            .modifier(new Modifier().style("width: 100%; padding: 20px; max-width: 1200px; margin: 0 auto; font-family: sans-serif;"));
    }
}
