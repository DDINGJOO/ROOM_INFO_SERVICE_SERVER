package com.teambind.springproject.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teambind.springproject.event.publisher.EventPublisher;
import com.teambind.springproject.mapper.RoomMapper;
import com.teambind.springproject.mapper.RoomQueryMapper;
import com.teambind.springproject.util.generator.PrimaryKeyGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    @Primary
    public EventPublisher mockEventPublisher() {
        return Mockito.mock(EventPublisher.class);
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public RoomMapper roomMapper() {
        return new RoomMapper(primaryKeyGenerator());
    }

    @Bean
    public RoomQueryMapper roomQueryMapper() {
        return new RoomQueryMapper();
    }

    @Bean
    public PrimaryKeyGenerator primaryKeyGenerator() {
        return new PrimaryKeyGenerator() {
            private long counter = 1L;

            @Override
            public String generateKey() {
                return String.valueOf(counter++);
            }

            @Override
            public Long generateLongKey() {
                return counter++;
            }
        };
    }
}