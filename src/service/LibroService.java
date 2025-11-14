
package service;
import dao.LibroDao;
import dao.FichaBibliograficaDao;
import entities.Libro;
import entities.FichaBibliografica;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class LibroService extends GenericService<Libro> {

    private final LibroDao libroDao;
    private final FichaBibliograficaDao fichaDao;

    public LibroService() {
        super(new LibroDao());
        this.libroDao = (LibroDao) super.dao;
        this.fichaDao = new FichaBibliograficaDao();
    }

    /**
     * Operación compuesta: crea Libro + FichaBibliografica en una transacción.
     *
     * @throws IllegalArgumentException si libro/ficha son nulos o ya están asociados
     * @throws RuntimeException si ocurre error en BD (ej. ISBN duplicado)
     */
    public void crearLibroConFicha(Libro libro, FichaBibliografica ficha) {
        if (libro == null) {
            throw new IllegalArgumentException("El Libro no puede ser nulo");
        }
        if (ficha == null) {
            throw new IllegalArgumentException("La Ficha Bibliografica no puede ser nula");
        }
        if (libro.getFichaBibliografica() != null) {
            throw new IllegalStateException("El Libro ya tiene una Ficha Bibliografica");
        }

        // Validaciones de negocio (anticipadas, aunque BD también valida)
        libro.getTitulo(); // dispara validación en setter si no fue hecha antes
        libro.getAutor();
        if (ficha.getIsbn() != null) {
            // Validacion basica de ISBN: longitud ≤17 (la fuerte está en FichaBibliografica.setIsbn)
            if (ficha.getIsbn().length() > 17) {
                throw new IllegalArgumentException("ISBN excede los 17 caracteres permitidos");
            }
        }

        Connection conn = null;
        try {
            conn = config.DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Crear Libro → obtenemos ID generado
            libroDao.crear(libro, conn);

            // 2. Crear FichaBibliografica usando el ID del Libro recién creado
            fichaDao.crear(ficha, libro.getId(), conn);

            // 3. Sincronizar en memoria (opcional, pero útil para AppMenu)
            libro.setFichaBibliografica(ficha);

            // 4. Confirmar
            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                // Log interno (no se lanza)
                System.err.println("Advertencia: fallo rollback anidado: " + ex.getMessage());
            }
            // Mensaje amigable para AppMenu
            String msg = extraerMensajeUsuario(e);
            throw new RuntimeException("No se pudo crear Libro con Ficha: " + msg, e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexion: " + e.getMessage());
            }
        }
    }

    /**
     * Busca Libros por título (insensible a mayúsculas, %patrón%).
     */
    public List<Libro> buscarPorTitulo(String titulo) {
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            return libroDao.buscarPorTitulo(titulo, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por titulo", e);
        }
    }

    // ===== Métodos específicos sobrescritos para usar DAOs concretos =====
    @Override
    public Libro crear(Libro libro) {
        throw new UnsupportedOperationException("Usar crearLibroConFicha() para Libro con FichaBibliografica");
    }

    @Override
    public void actualizar(Libro libro) {
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Validar que no esté reasignando ficha
                if (libro.getFichaBibliografica() != null) {
                    // Si ya tenía ficha, no permitir cambiarla
                    Libro existente = libroDao.leer(libro.getId(), conn);
                    if (existente != null && existente.getFichaBibliografica() != null) {
                        throw new IllegalStateException("No se permite reasignar la FichaBibliografica");
                    }
                }

                libroDao.actualizar(libro, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al actualizar el Libro", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error de conexion al actualizar el Libro", e);
        }
    }

    // ===== Helper: mensajes de error amigables =====
    private String extraerMensajeUsuario(SQLException e) {
        String sqlState = e.getSQLState();
        String msg = e.getMessage().toLowerCase();

        if (sqlState != null && sqlState.startsWith("23")) { // Violación de restricción
            if (msg.contains("duplicate entry") && msg.contains("idlibro")) {
                return "Ya existe una FichaBibliografica para este Libro";
            }
            if (msg.contains("duplicate entry") && msg.contains("isbn")) {
                return "ISBN ya registrado en otra ficha";
            }
        }
        return "error en la base de datos";
    }
    
    public void cargarFichaBibliografica(Libro libro) {
        if (libro == null || libro.getId() == null) {
            return;
        }

        try (Connection conn = config.DatabaseConnection.getConnection()) {
            String sql = """
                SELECT id, isbn, clasificacionDewey, estanteria, idioma, eliminado
                FROM FichaBibliografica
                WHERE idLIBRO = ? AND eliminado = FALSE
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, libro.getId());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        FichaBibliografica ficha = new FichaBibliografica();
                        ficha.setId(rs.getLong("id"));
                        ficha.setIsbn(rs.getString("isbn"));
                        ficha.setClasificacionDewey(rs.getString("clasificacionDewey"));
                        ficha.setEstanteria(rs.getString("estanteria"));
                        ficha.setIdioma(rs.getString("idioma"));
                        ficha.setEliminado(rs.getBoolean("eliminado"));
                        libro.setFichaBibliografica(ficha);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar la FichaBibliografica", e);
        }
    }
}