package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;

import java.util.List;
import java.util.Optional;

public interface DAO<T, K> {
    public Optional<T> findById(K id) throws DAOException;

    public List<T> findAll() throws DAOException;

    public T save(T entity) throws DAOException;

    public void update(T entity) throws DAOException;

    public void delete(T entity) throws DAOException;
}
