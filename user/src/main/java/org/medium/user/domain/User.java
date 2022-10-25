package org.medium.user.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.index.CompositeQueryIndex;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;

import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@CompositeQueryIndex(fields = {"_class"})
public class User {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id @GeneratedValue(strategy = UNIQUE)
    private String id;
    @Field
    private String firstName;
    @Field
    private String lastName;
    @Field
    private String email;
    @Field
    private String username;
    @Field
    private String password;
    @Field
    private String avatarUrl;
}
