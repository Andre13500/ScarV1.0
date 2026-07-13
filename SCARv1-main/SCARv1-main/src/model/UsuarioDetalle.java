package model;

import java.util.Date;

/**
 * DTO con los datos combinados de Usuario + Empleado + Rol + AreaTrabajo.
 * Se usa como resultado del login y para las tablas de los modulos.
 */
public class UsuarioDetalle {
    private int idUsuario;
    private int idEmpleado;
    private String usuario;
    private String rol;
    private String nombre;
    private String cedula;
    private String correo;
    private String area;
    private Date fechaRegistro;
    private boolean estado;

    public UsuarioDetalle() {}

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public boolean esAdministrador() {
        return "Administrador".equalsIgnoreCase(rol);
    }
}
