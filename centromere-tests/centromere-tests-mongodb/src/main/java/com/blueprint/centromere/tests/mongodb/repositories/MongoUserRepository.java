package com.blueprint.centromere.tests.mongodb.repositories;

import com.blueprint.centromere.core.repository.ModelResource;
import com.blueprint.centromere.tests.core.repositories.UserRepository;
import com.blueprint.centromere.tests.mongodb.models.MongoUser;

/**
 * @author woemler
 */
@ModelResource("user")
public interface MongoUserRepository extends UserRepository<MongoUser, String> {

}
