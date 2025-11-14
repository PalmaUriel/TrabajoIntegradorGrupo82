
package service;

import dao.GenericDao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class GenericService<T> {

    protected final GenericDao<T> dao;

    protected GenericService(GenericDao<T> dao) {
        this.dao = dao;
    }

    public T crear(T entity) throws RuntimeException {
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = dao.crear(entity, conn);
                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al crear entidad: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error de conexion al crear entidad", e);
        }
    }

    public T obtenerPorId(Long id) {
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            return dao.leer(id, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener por ID", e);
        }
    }

    public List<T> listarTodos() {
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            return dao.leerTodos(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar todas las entidades", e);
        }
    }

    public void actualizar(T entity) {
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                dao.actualizar(entity, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al actualizar entidad", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error de conexion al actualizar", e);
        }
    }

    public void eliminarLogico(Long id) {
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                dao.eliminar(id, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al eliminar logicamente", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error de conexion al eliminar", e);
        }
    }
}