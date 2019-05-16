package com.blueprint.centromere.tests.mongodb.models;

import com.blueprint.centromere.tests.core.models.User;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author woemler
 */
@Document
@Data
@ToString(callSuper = true)
public class MongoUser extends User<String> {

    @Id
    private String id;

}
