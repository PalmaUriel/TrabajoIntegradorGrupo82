
package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Todos los métodos reciben una Connection externa para participar de transacciones.
 */
public interface GenericDao<T> {

    /**
     * Inserta una nueva entidad en la base de datos.
     * @param entity entidad a crear
     * @param conn conexión activa (externa)
     * @return entidad con ID asignado
     * @throws SQLException si ocurre error en BD
     */
    T crear(T entity, Connection conn) throws SQLException;

    /**
     * Lee una entidad por su ID (solo activas: eliminado = FALSE).
     * @param id identificador
     * @param conn conexión activa
     * @return entidad, o {@code null} si no existe o está eliminada
     * @throws SQLException
     */
    T leer(Long id, Connection conn) throws SQLException;

    /**
     * Lista todas las entidades activas (eliminado = FALSE).
     * @param conn conexión activa
     * @return lista de entidades
     * @throws SQLException
     */
    List<T> leerTodos(Connection conn) throws SQLException;

    /**
     * Actualiza una entidad existente.
     * @param entity entidad con ID y datos actualizados
     * @param conn conexión activa
     * @throws SQLException
     */
    void actualizar(T entity, Connection conn) throws SQLException;

    /**
     * Eliminación lógica: marca como eliminado = TRUE.
     * @param id ID de la entidad a eliminar
     * @param conn conexión activa
     * @throws SQLException
     */
    void eliminar(Long id, Connection conn) throws SQLException;
}