
package service;

import dao.FichaBibliograficaDao;
import dao.LibroDao;
import entities.FichaBibliografica;
import entities.Libro;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servicio para FichaBibliografica (Clase B).
 * Para mantener coherencia, toda FichaBibliografica debe pertenecer a un Libro existente y activo.
 */
public class FichaBibliograficaService extends GenericService<FichaBibliografica> {

    private final FichaBibliograficaDao fichaDao;
    private final LibroDao libroDao;

    public FichaBibliograficaService() {
        super(new FichaBibliograficaDao());
        this.fichaDao = (FichaBibliograficaDao) super.dao;
        this.libroDao = new LibroDao();
    }

    /**
     * Crea una FichaBibliografica asociada a un Libro existente.
     * 
     * @param ficha FichaBibliografica a crear (sin ID)
     * @param idLibro ID del Libro al que se asociará (debe existir y estar activo)
     * @return FichaBibliografica creada (con ID asignado)
     * @throws IllegalArgumentException si idLibro no existe o ficha es inválida
     */
    public FichaBibliografica crear(Long idLibro, FichaBibliografica ficha) {
        if (idLibro == null || idLibro <= 0) {
            throw new IllegalArgumentException("ID de Libro invalido");
        }
        if (ficha == null) {
            throw new IllegalArgumentException("La FichaBibliografica no puede ser nula");
        }

        // Validaciones de negocio
        if (ficha.getIsbn() != null) {
            String clean = ficha.getIsbn().replaceAll("[\\s\\-]", "");
            if (clean.length() != 10 && clean.length() != 13) {
                throw new IllegalArgumentException("ISBN debe tener 10 o 13 digitos");
            }
            if (ficha.getIsbn().length() > 17) {
                throw new IllegalArgumentException("ISBN excede los 17 caracteres permitidos");
            }
        }

        Connection conn = null;
        try {
            conn = config.DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Validar que el Libro exista y esté activo
            Libro libro = libroDao.leer(idLibro, conn);
            if (libro == null) {
                throw new IllegalArgumentException("No existe un Libro activo con ID: " + idLibro);
            }

            // Validar que el Libro no tenga ya una ficha
            // Aunque la BD lo impide con UK, validamos antes para aclarar el mensaje
            String sqlCheck = "SELECT COUNT(*) FROM FichaBibliografica WHERE idLIBRO = ? AND eliminado = FALSE";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlCheck)) {
                ps.setLong(1, idLibro);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new IllegalStateException("El Libro ya tiene una FichaBibliografica asociada");
                    }
                }
            }

            // Crear ficha
            FichaBibliografica creada = fichaDao.crear(ficha, idLibro, conn);
            conn.commit();
            return creada;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Advertencia: fallo rollback anidado: " + ex.getMessage());
            }
            String msg = extraerMensajeUsuario(e);
            throw new RuntimeException("No se pudo crear la FichaBibliografica: " + msg, e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    
    // Busca una FichaBibliografica por ISBN.
    
    public FichaBibliografica buscarPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN no puede ser nulo o vacio");
        }
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            return fichaDao.buscarPorIsbn(isbn, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar ficha por ISBN", e);
        }
    }

    // ===== Métodos sobrescritos para agregar validaciones =====
    @Override
    public void actualizar(FichaBibliografica ficha) {
        if (ficha == null || ficha.getId() == null) {
            throw new IllegalArgumentException("FichaBibliografica invalida para actualizar");
        }
        // No se permite cambiar idLIBRO directamente (eso se hace desde LibroService)
        // Solo se actualizan campos propios: isbn, estantería, etc.
        super.actualizar(ficha);
    }

    // ===== Helper: mensajes amigables =====
    private String extraerMensajeUsuario(SQLException e) {
        String sqlState = e.getSQLState();
        String msg = e.getMessage().toLowerCase();

        if (sqlState != null && sqlState.startsWith("23")) { // Constraint violation
            if (msg.contains("duplicate entry") && msg.contains("isbn")) {
                return "ISBN ya registrado en otra ficha";
            }
            if (msg.contains("duplicate entry") && msg.contains("idlibro")) {
                return "El Libro ya tiene una FichaBibliografica";
            }
        }
        return "error en la base de datos";
    }
    
    public FichaBibliografica buscarPorIdLibro(Long idLibro) {
    try (Connection conn = config.DatabaseConnection.getConnection()) {
        return fichaDao.buscarPorIdLibro(idLibro, conn);
    } catch (SQLException e) {
        throw new RuntimeException("Error al buscar ficha por ID de Libro", e);
    }
}
}