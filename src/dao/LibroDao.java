
package dao;

import entities.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDao implements GenericDao<Libro> {

    // SQL statements 
    private static final String INSERT_SQL = """
        INSERT INTO Libro (titulo, autor, editorial, anioEdicion, eliminado)
        VALUES (?, ?, ?, ?, ?)
        """;
    private static final String SELECT_BY_ID_SQL = """
        SELECT id, titulo, autor, editorial, anioEdicion, eliminado
        FROM Libro
        WHERE id = ? AND eliminado = FALSE
        """;
    private static final String SELECT_ALL_SQL = """
        SELECT id, titulo, autor, editorial, anioEdicion, eliminado
        FROM Libro
        WHERE eliminado = FALSE
        ORDER BY titulo
        """;
    private static final String UPDATE_SQL = """
        UPDATE Libro
        SET titulo = ?, autor = ?, editorial = ?, anioEdicion = ?, eliminado = ?
        WHERE id = ?
        """;
    private static final String DELETE_LOGICO_SQL = """
        UPDATE Libro
        SET eliminado = TRUE
        WHERE id = ?
        """;
    private static final String BUSCAR_POR_TITULO_SQL = """
        SELECT id, titulo, autor, editorial, anioEdicion, eliminado
        FROM Libro
        WHERE UPPER(titulo) LIKE UPPER(?) AND eliminado = FALSE
        ORDER BY titulo
        """;

    @Override
    public Libro crear(Libro libro, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setObject(4, libro.getAnioEdicion()); // permite null
            ps.setBoolean(5, libro.getEliminado());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se pudo crear el Libro.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    libro.setId(rs.getLong(1));
                }
            }
            return libro;
        }
    }

    @Override
    public Libro leer(Long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLibro(rs);
                }
                return null; 
            }
        }
    }

    @Override
    public List<Libro> leerTodos(Connection conn) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                libros.add(mapResultSetToLibro(rs));
            }
        }
        return libros;
    }

    @Override
    public void actualizar(Libro libro, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setObject(4, libro.getAnioEdicion());
            ps.setBoolean(5, libro.getEliminado());
            ps.setLong(6, libro.getId());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se encontro el Libro con ID: " + libro.getId());
            }
        }
    }

    @Override
    public void eliminar(Long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_LOGICO_SQL)) {
            ps.setLong(1, id);
            ps.executeUpdate(); // no lanza excepción si no existe
        }
    }

    // ===== Método adicional de búsqueda =====
    public List<Libro> buscarPorTitulo(String titulo, Connection conn) throws SQLException {
        List<Libro> resultados = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(BUSCAR_POR_TITULO_SQL)) {
            ps.setString(1, "%" + titulo + "%"); // %patrón%
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapResultSetToLibro(rs));
                }
            }
        }
        return resultados;
    }

    // ===== Helper: mapeo de ResultSet a Libro =====
    private Libro mapResultSetToLibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setId(rs.getLong("id"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setEditorial(rs.getString("editorial"));
        libro.setAnioEdicion(rs.getObject("anioEdicion", Integer.class));
        libro.setEliminado(rs.getBoolean("eliminado"));
        return libro;
    }
}