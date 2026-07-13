package model;

public class AreaTrabajo {
    private int idAreaTrabajo;
    private String nombre;
    private String ubicacion;
    private boolean estado;  // bit en SQL = boolean en Java

    public AreaTrabajo() {}

    public int getIdAreaTrabajo() { return idAreaTrabajo; }
    public void setIdAreaTrabajo(int idAreaTrabajo) { this.idAreaTrabajo = idAreaTrabajo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
}