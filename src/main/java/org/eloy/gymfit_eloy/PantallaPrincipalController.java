package org.eloy.gymfit_eloy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import java.time.LocalDate;

public class PantallaPrincipalController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrimerApellido;
    @FXML private TextField txtSegundoApellido;
    @FXML private DatePicker dpFechaNacimiento;

    @FXML private ToggleGroup opcionesSexo;
    @FXML private ComboBox<String> cbProvincia;
    @FXML private ToggleGroup opcionesCuota;

    // Checkboxes de clases
    @FXML private CheckBox chkAquagym, chkBodyPump, chkBoxeo, chkCrossTraining, chkEstiramientos, chkFuncional;
    @FXML private CheckBox chkHiit, chkNatacion, chkPilates, chkSpinning, chkYoga, chkZumba;

    @FXML private TextField txtIban;

    // Botones
    @FXML private Button btnNuevoSocio;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminarFicha;

    // Tabla y Columnas
    @FXML private TableView<Socio> tablaSocios;
    @FXML private TableColumn<Socio, String> colSocio;
    @FXML private TableColumn<Socio, LocalDate> colFechaNacimiento;
    @FXML private TableColumn<Socio, String> colCuota;
    @FXML private TableColumn<Socio, String> colEstado;

    private final ObservableList<Socio> listaSocios = FXCollections.observableArrayList();
    private Socio socioSeleccionado = null;

    @FXML
    public void initialize() {
        //Cargar opciones en el ComboBox de provincias
        cbProvincia.getItems().addAll("Almería", "Cádiz", "Córdoba", "Granada", "Huelva", "Jaén", "Málaga", "Sevilla");

        //Configurar los enlaces de columnas con la clase Socio
        colSocio.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreCompleto()));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colCuota.setCellValueFactory(new PropertyValueFactory<>("tipoCuota"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaSocios.setItems(listaSocios);

        //Cargar los datos en el formulario al hacer clic en un socio de la tabla
        tablaSocios.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                socioSeleccionado = newValue;
                cargarSocioEnFormulario(newValue);
            }
        });
        configurarMenuContextualTabla();
        configurarAtajosTeclado();
    }

    @FXML
    private void handleNuevoSocio() {
        String nombre = txtNombre.getText();
        String apellido1 = txtPrimerApellido.getText();
        String apellido2 = txtSegundoApellido.getText();
        LocalDate fechaNac = dpFechaNacimiento.getValue();

        RadioButton selectedSexo = (RadioButton) opcionesSexo.getSelectedToggle();
        String sexo = selectedSexo != null ? selectedSexo.getText() : "";

        String provincia = cbProvincia.getValue();

        RadioButton selectedCuota = (RadioButton) opcionesCuota.getSelectedToggle();
        String tipoCuota = selectedCuota != null ? selectedCuota.getText() : "";

        String clases = obtenerClasesSeleccionadas();
        String iban = txtIban.getText();
        if(!nombre.trim().isEmpty()  || !apellido1.trim().isEmpty() || !apellido2.trim().isEmpty() || fechaNac != null || !sexo.trim().isEmpty() || !provincia.trim().isEmpty() || !tipoCuota.trim().isEmpty() || txtIban != null){
            Alert confirmacionPrevia = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacionPrevia.setTitle("Cambios sin guardar");
            confirmacionPrevia.setHeaderText(null);
            confirmacionPrevia.setContentText("Tienes datos ya rellenados en el formulario, ¿quieres descartarlos y crear un nuevo socio?");
            if(confirmacionPrevia.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK){
                limpiarFormulario();
                tablaSocios.getSelectionModel().clearSelection();
                socioSeleccionado = null;
            }
        }

    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }

        String nombre = txtNombre.getText();
        String apellido1 = txtPrimerApellido.getText();
        String apellido2 = txtSegundoApellido.getText();
        LocalDate fechaNac = dpFechaNacimiento.getValue();

        RadioButton selectedSexo = (RadioButton) opcionesSexo.getSelectedToggle();
        String sexo = selectedSexo != null ? selectedSexo.getText() : "";

        String provincia = cbProvincia.getValue();

        RadioButton selectedCuota = (RadioButton) opcionesCuota.getSelectedToggle();
        String tipoCuota = selectedCuota != null ? selectedCuota.getText() : "Básica";

        String clases = obtenerClasesSeleccionadas();
        String iban = txtIban.getText();
        String estado = "Activo";

        if (socioSeleccionado == null) {
            Socio nuevoSocio = new Socio(nombre, apellido1, apellido2, fechaNac, sexo, provincia, tipoCuota, clases, iban, estado);
            listaSocios.add(nuevoSocio);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Socio registrado correctamente.");
        } else {
            socioSeleccionado.nombreProperty().set(nombre);
            socioSeleccionado.primerApellidoProperty().set(apellido1);
            socioSeleccionado.segundoApellidoProperty().set(apellido2);
            socioSeleccionado.fechaNacimientoProperty().set(fechaNac);
            socioSeleccionado.sexoProperty().set(sexo);
            socioSeleccionado.provinciaProperty().set(provincia);
            socioSeleccionado.tipoCuotaProperty().set(tipoCuota);
            socioSeleccionado.clasesProperty().set(clases);
            socioSeleccionado.ibanProperty().set(iban);

            tablaSocios.refresh();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Ficha de socio actualizada correctamente.");
        }

        limpiarFormulario();
    }

    @FXML
    private void handleEliminarFicha() {
        Socio seleccionado = tablaSocios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert accionDestructiva = new Alert(Alert.AlertType.CONFIRMATION);
            accionDestructiva.setTitle("Confirmar eliminación");
            accionDestructiva.setHeaderText(null);
            accionDestructiva.setContentText("¿Esta seguro de que quiere eliminar esta ficha de socio? Esta acción no se puede deshacer");
            if(accionDestructiva.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK){
                listaSocios.remove(seleccionado);
                limpiarFormulario();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "La ficha del socio ha sido eliminada.");
            }

        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un socio de la tabla para eliminar.");
        }
    }

    private void cargarSocioEnFormulario(Socio socio) {
        txtNombre.setText(socio.getNombre());
        txtPrimerApellido.setText(socio.getPrimerApellido());
        txtSegundoApellido.setText(socio.getSegundoApellido());
        dpFechaNacimiento.setValue(socio.getFechaNacimiento());
        cbProvincia.setValue(socio.getProvincia());
        txtIban.setText(socio.getIban());

        for (Toggle t : opcionesSexo.getToggles()) {
            RadioButton rb = (RadioButton) t;
            if (rb.getText().equals(socio.getSexo())) {
                rb.setSelected(true);
                break;
            }
        }

        for (Toggle t : opcionesCuota.getToggles()) {
            RadioButton rb = (RadioButton) t;
            if (rb.getText().equals(socio.getTipoCuota())) {
                rb.setSelected(true);
                break;
            }
        }

        desmarcarTodasLasClases();
        String clasesStr = socio.getClases();
        if (clasesStr != null) {
            for (CheckBox chk : new CheckBox[]{chkAquagym, chkBodyPump, chkBoxeo, chkCrossTraining,
                    chkEstiramientos, chkFuncional, chkHiit, chkNatacion, chkPilates, chkSpinning, chkYoga, chkZumba}) {
                if (clasesStr.contains(chk.getText())) {
                    chk.setSelected(true);
                }
            }
        }
    }

    private String obtenerClasesSeleccionadas() {
        StringBuilder sb = new StringBuilder();
        CheckBox[] checkboxes = {chkAquagym, chkBodyPump, chkBoxeo, chkCrossTraining,
                chkEstiramientos, chkFuncional, chkHiit, chkNatacion, chkPilates, chkSpinning, chkYoga, chkZumba};

        for (CheckBox chk : checkboxes) {
            if (chk.isSelected()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(chk.getText());
            }
        }
        return sb.toString();
    }

    private void desmarcarTodasLasClases() {
        CheckBox[] checkboxes = {chkAquagym, chkBodyPump, chkBoxeo, chkCrossTraining,
                chkEstiramientos, chkFuncional, chkHiit, chkNatacion, chkPilates, chkSpinning, chkYoga, chkZumba};
        for (CheckBox chk : checkboxes) {
            chk.setSelected(false);
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrimerApellido.clear();
        txtSegundoApellido.clear();
        dpFechaNacimiento.setValue(null);
        if (opcionesSexo.getSelectedToggle() != null) opcionesSexo.getSelectedToggle().setSelected(false);
        cbProvincia.getSelectionModel().clearSelection();
        if (opcionesCuota.getSelectedToggle() != null) opcionesCuota.getSelectedToggle().setSelected(false);
        desmarcarTodasLasClases();
        txtIban.clear();
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty() || txtPrimerApellido.getText().trim().isEmpty() || dpFechaNacimiento.getValue() == null || txtIban.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", "Hay campos obligatorios sin rellenar. Rellene todos los campos e inténtelo de nuevo.");
            return false;
        }
        String iban = txtIban.getText().trim().toUpperCase();
        // Comprueba el formato exacto: ES + 2 dígitos + 5 bloques de (espacio + 4 dígitos)
        if (!iban.matches("^ES\\d{2}(\\s\\d{4}){5}$")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", "El IBAN introducido no es válido. Debe seguir el formato: ES00 0000 0000 0000 0000 0000");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    private void configurarMenuContextualTabla() {
        ContextMenu menuContextual = new ContextMenu();

        MenuItem itemCambiarEstado = new MenuItem("Cambiar estado (Activo / Inactivo)");
        itemCambiarEstado.setOnAction(e -> {
            Socio seleccionado = tablaSocios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                String nuevoEstado = "Activo".equalsIgnoreCase(seleccionado.getEstado()) ? "Inactivo" : "Activo";
                seleccionado.estadoProperty().set(nuevoEstado);
                tablaSocios.refresh();
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecciona un socio para cambiar su estado.");
            }
        });

        MenuItem itemVerDetalle = new MenuItem("Ver detalle");
        itemVerDetalle.setOnAction(e -> {
            Socio seleccionado = tablaSocios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                String clasesTexto = (seleccionado.getClases() == null || seleccionado.getClases().trim().isEmpty())
                        ? "Ninguna"
                        : seleccionado.getClases();
                String detalle = String.format(
                        "Socio: %s%nFecha nacimiento: %s%nSexo: %s%nProvincia: %s%nCuota: %s%nEstado: %s%nClases: %s%nIBAN: %s",
                        seleccionado.getNombreCompleto(),
                        seleccionado.getFechaNacimiento(),
                        seleccionado.getSexo(),
                        seleccionado.getProvincia(),
                        seleccionado.getTipoCuota(),
                        seleccionado.getEstado(),
                        clasesTexto,
                        seleccionado.getIban()
                );
                mostrarAlerta(Alert.AlertType.INFORMATION, "Detalle del socio", detalle);
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecciona un socio para ver su detalle.");
            }
        });

        MenuItem itemDuplicar = new MenuItem("Duplicar socio");
        itemDuplicar.setOnAction(e -> {
            Socio seleccionado = tablaSocios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                Socio copia = new Socio(
                        seleccionado.getNombre(),
                        seleccionado.getPrimerApellido(),
                        seleccionado.getSegundoApellido(),
                        seleccionado.getFechaNacimiento(),
                        seleccionado.getSexo(),
                        seleccionado.getProvincia(),
                        seleccionado.getTipoCuota(),
                        seleccionado.getClases(),
                        seleccionado.getIban(),
                        seleccionado.getEstado()
                );
                listaSocios.add(copia);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Duplicado", "Se ha creado una copia de la ficha.");
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecciona un socio para duplicar.");
            }
        });

        MenuItem itemEliminar = new MenuItem("Eliminar ficha");
        itemEliminar.setOnAction(e -> handleEliminarFicha());

        menuContextual.getItems().addAll(

                itemCambiarEstado,
                itemVerDetalle,
                itemDuplicar,
                new SeparatorMenuItem(),
                itemEliminar
        );

        //Deshabilita opciones si no hay fila seleccionada
        menuContextual.setOnShowing(e -> {
            boolean haySeleccion = tablaSocios.getSelectionModel().getSelectedItem() != null;

            itemCambiarEstado.setDisable(!haySeleccion);
            itemVerDetalle.setDisable(!haySeleccion);
            itemDuplicar.setDisable(!haySeleccion);
            itemEliminar.setDisable(!haySeleccion);
        });

        tablaSocios.setContextMenu(menuContextual);
    }

    private void configurarAtajosTeclado() {
        //Asegurar que la Scene ya esté asociada a los controles tras cargar el FXML
        Platform.runLater(() -> {
            if (txtNombre.getScene() != null) {
                txtNombre.getScene().getAccelerators().put(
                        new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN),
                        this::handleNuevoSocio
                );

                txtNombre.getScene().getAccelerators().put(
                        new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN),
                        this::handleGuardar
                );

                txtNombre.getScene().getAccelerators().put(
                        new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN),
                        this::handleEliminarFicha
                );
            }
        });
    }
}
