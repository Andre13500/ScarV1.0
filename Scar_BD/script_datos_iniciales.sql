-- ============================================
-- DATOS INICIALES BD_SCAR (opcional)
-- La aplicacion Java tambien crea estos datos automaticamente
-- al arrancar (config.InicializadorBD), incluido el usuario
-- admin (admin / admin123).
-- ============================================

USE [BD_SCAR]
GO

-- Roles
IF NOT EXISTS (SELECT 1 FROM Rol WHERE tipo = 'Administrador')
    INSERT INTO Rol (tipo) VALUES ('Administrador');
IF NOT EXISTS (SELECT 1 FROM Rol WHERE tipo = 'Usuario')
    INSERT INTO Rol (tipo) VALUES ('Usuario');
GO

-- Areas de trabajo (por ahora solo 4)
IF NOT EXISTS (SELECT 1 FROM AreaTrabajo WHERE nombre = '1A')
    INSERT INTO AreaTrabajo (nombre, ubicacion, estado) VALUES ('1A', 'Area 1A', 1);
IF NOT EXISTS (SELECT 1 FROM AreaTrabajo WHERE nombre = '2B')
    INSERT INTO AreaTrabajo (nombre, ubicacion, estado) VALUES ('2B', 'Area 2B', 1);
IF NOT EXISTS (SELECT 1 FROM AreaTrabajo WHERE nombre = '3C')
    INSERT INTO AreaTrabajo (nombre, ubicacion, estado) VALUES ('3C', 'Area 3C', 1);
IF NOT EXISTS (SELECT 1 FROM AreaTrabajo WHERE nombre = '4C')
    INSERT INTO AreaTrabajo (nombre, ubicacion, estado) VALUES ('4C', 'Area 4C', 1);
GO

PRINT 'Datos iniciales insertados';
GO
