package org.eloy.gymfit_eloy;

import java.time.LocalDate;
import javafx.beans.property.*;

public class Socio {
    private final StringProperty nombre;
    private final StringProperty primerApellido;
    private final StringProperty segundoApellido;
    private final ObjectProperty<LocalDate> fechaNacimiento;
    private final StringProperty sexo;
    private final StringProperty provincia;
    private final StringProperty tipoCuota;
    private final StringProperty clases;
    private final StringProperty iban;
    private final StringProperty estado;

    public Socio(String nombre, String primerApellido, String segundoApellido, LocalDate fechaNacimiento,
                 String sexo, String provincia, String tipoCuota, String clases, String iban, String estado) {
        this.nombre = new SimpleStringProperty(nombre);
        this.primerApellido = new SimpleStringProperty(primerApellido);
        this.segundoApellido = new SimpleStringProperty(segundoApellido);
        this.fechaNacimiento = new SimpleObjectProperty<>(fechaNacimiento);
        this.sexo = new SimpleStringProperty(sexo);
        this.provincia = new SimpleStringProperty(provincia);
        this.tipoCuota = new SimpleStringProperty(tipoCuota);
        this.clases = new SimpleStringProperty(clases);
        this.iban = new SimpleStringProperty(iban);
        this.estado = new SimpleStringProperty(estado);
    }

    public String getNombre() { return nombre.get(); }
    public StringProperty nombreProperty() { return nombre; }

    public String getPrimerApellido() { return primerApellido.get(); }
    public StringProperty primerApellidoProperty() { return primerApellido; }

    public String getSegundoApellido() { return segundoApellido.get(); }
    public StringProperty segundoApellidoProperty() { return segundoApellido; }

    public String getNombreCompleto() {
        String segApp = (getSegundoApellido() != null) ? getSegundoApellido() : "";
        return getNombre() + " " + getPrimerApellido() + " " + segApp;
    }

    public LocalDate getFechaNacimiento() { return fechaNacimiento.get(); }
    public ObjectProperty<LocalDate> fechaNacimientoProperty() { return fechaNacimiento; }

    public String getSexo() { return sexo.get(); }
    public StringProperty sexoProperty() { return sexo; }

    public String getProvincia() { return provincia.get(); }
    public StringProperty provinciaProperty() { return provincia; }

    public String getTipoCuota() { return tipoCuota.get(); }
    public StringProperty tipoCuotaProperty() { return tipoCuota; }

    public String getClases() { return clases.get(); }
    public StringProperty clasesProperty() { return clases; }

    public String getIban() { return iban.get(); }
    public StringProperty ibanProperty() { return iban; }

    public String getEstado() { return estado.get(); }
    public StringProperty estadoProperty() { return estado; }
}
