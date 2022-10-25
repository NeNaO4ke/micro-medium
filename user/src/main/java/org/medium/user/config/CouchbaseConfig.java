package org.medium.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;

import java.util.Arrays;

@Configuration
@EnableReactiveCouchbaseRepositories
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

    @Value("${spring.couchbase.host}")
    private String bootstrapHosts;
    @Value("${spring.couchbase.bucket-name}")
    private String bucketName;
    @Value("${spring.couchbase.password}")
    private String password;
    @Value("${spring.couchbase.username}")
    private String username;
    @Value("${spring.couchbase.port}")
    private int port;


    @Override
    public String getConnectionString() {
        return bootstrapHosts;
    }

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getBucketName() {
        return bucketName;
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
