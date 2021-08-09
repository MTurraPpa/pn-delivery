package it.pagopa.pn.commons.abstractions.impl;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons.abstractions.KeyValueStore;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.EntityWriteResult;
import org.springframework.data.cassandra.core.InsertOptions;

public abstract class AbstractCassandraKeyValueStore<K, V> implements KeyValueStore<K, V> {

    private static final InsertOptions INSERT_OPTIONS = InsertOptions.builder()
            .consistencyLevel(ConsistencyLevel.LOCAL_QUORUM)
            .build();

    private static final InsertOptions INSERT_IF_NOT_EXISTS_OPTIONS = InsertOptions.builder()
            .consistencyLevel(ConsistencyLevel.LOCAL_QUORUM)
            .ifNotExists(true)
            .build();

    private final CassandraOperations cassandraTemplate;
    private final Class<V> entityClass;

    public AbstractCassandraKeyValueStore( CassandraOperations cassandraTemplate, Class<V> entityClass ) {
        this.cassandraTemplate = cassandraTemplate;
        this.entityClass = entityClass;
    }

    @Override
    public void put(V value) {
        cassandraTemplate.insert( value, INSERT_OPTIONS );
    }

    @Override
    public void putIfAbsent(V value) throws IdConflictException {
        EntityWriteResult<V> result = cassandraTemplate.insert( value, INSERT_IF_NOT_EXISTS_OPTIONS );
        if( ! result.wasApplied() ) {
            throw new IdConflictException( value );
        }
    }

    @Override
    public V get(K key) {
        return cassandraTemplate.selectOneById( key, entityClass );
    }

    @Override
    public void delete(K key) {
        cassandraTemplate.deleteById( key, entityClass );
    }
}
