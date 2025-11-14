
package dao;

import entities.FichaBibliografica;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FichaBibliograficaDao implements GenericDao<FichaBibliografica> {

    private static final String INSERT_SQL = """
        INSERT INTO FichaBibliografica (idLIBRO, isbn, clasificacionDewey, estanteria, idioma, eliminado)
        VALUES (?, ?, ?, ?, ?, ?)
        """;
    private static final String SELECT_BY_ID_SQL = """
        SELECT id, isbn, clasificacionDewey, estanteria, idioma, eliminado
        FROM FichaBibliografica
        WHERE id = ? AND eliminado = FALSE
        """;
    private static final String SELECT_ALL_SQL = """
        SELECT id, isbn, clasificacionDewey, estanteria, idioma, eliminado
        FROM FichaBibliografica
        WHERE eliminado = FALSE
        ORDER BY isbn
        """;
    private static final String UPDATE_SQL = """
        UPDATE FichaBibliografica
        SET isbn = ?, clasificacionDewey = ?, estanteria = ?, idioma = ?, eliminado = ?
        WHERE id = ?
        """;
    private static final String DELETE_LOGICO_SQL = """
        UPDATE FichaBibliografica
        SET eliminado = TRUE
        WHERE id = ?
        """;

    public FichaBibliografica crear(FichaBibliografica ficha, Long idLIBRO, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, idLIBRO); // 
            ps.setString(2, ficha.getIsbn());
            ps.setString(3, ficha.getClasificacionDewey());
            ps.setString(4, ficha.getEstanteria());
            ps.setString(5, ficha.getIdioma());
            ps.setBoolean(6, ficha.getEliminado());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se pudo crear la FichaBibliografica.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ficha.setId(rs.getLong(1));
                }
            }
            return ficha;
        }
    }

    // Sobrecarga sin idLIBRO
    public FichaBibliografica crear(FichaBibliografica ficha, Connection conn) throws SQLException {
        throw new UnsupportedOperationException("Usar crear(ficha, idLIBRO, conn)");
    }

    @Override
    public FichaBibliografica leer(Long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFicha(rs);
                }
                return null;
            }
        }
    }

    @Override
    public List<FichaBibliografica> leerTodos(Connection conn) throws SQLException {
        List<FichaBibliografica> fichas = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                fichas.add(mapResultSetToFicha(rs));
            }
        }
        return fichas;
    }

    @Override
    public void actualizar(FichaBibliografica ficha, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, ficha.getIsbn());
            ps.setString(2, ficha.getClasificacionDewey());
            ps.setString(3, ficha.getEstanteria());
            ps.setString(4, ficha.getIdioma());
            ps.setBoolean(5, ficha.getEliminado());
            ps.setLong(6, ficha.getId());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se encontro la FichaBibliografica con ID: " + ficha.getId());
            }
        }
    }

    @Override
    public void eliminar(Long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_LOGICO_SQL)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    //  Método adicional: buscar por ISBN
    public FichaBibliografica buscarPorIsbn(String isbn, Connection conn) throws SQLException {
        String sql = """
            SELECT id, isbn, clasificacionDewey, estanteria, idioma, eliminado
            FROM FichaBibliografica
            WHERE UPPER(isbn) = UPPER(?) AND eliminado = FALSE
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFicha(rs);
                }
                return null;
            }
        }
    }

    //  Helper: mapeo de ResultSet a FichaBibliografica 
    private FichaBibliografica mapResultSetToFicha(ResultSet rs) throws SQLException {
        FichaBibliografica ficha = new FichaBibliografica();
        ficha.setId(rs.getLong("id"));
        ficha.setIsbn(rs.getString("isbn"));
        ficha.setClasificacionDewey(rs.getString("clasificacionDewey"));
        ficha.setEstanteria(rs.getString("estanteria"));
        ficha.setIdioma(rs.getString("idioma"));
        ficha.setEliminado(rs.getBoolean("eliminado"));
        return ficha;
    }
    public FichaBibliografica buscarPorIdLibro(Long idLibro, Connection conn) throws SQLException {
    String sql = """
        SELECT id, isbn, clasificacionDewey, estanteria, idioma, eliminado
        FROM FichaBibliografica
        WHERE idLIBRO = ? AND eliminado = FALSE
        """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setLong(1, idLibro);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                FichaBibliografica f = new FichaBibliografica();
                f.setId(rs.getLong("id"));
                f.setIsbn(rs.getString("isbn"));
                f.setClasificacionDewey(rs.getString("clasificacionDewey"));
                f.setEstanteria(rs.getString("estanteria"));
                f.setIdioma(rs.getString("idioma"));
                f.setEliminado(rs.getBoolean("eliminado"));
                return f;
            }
            return null;
        }
    }
}
}