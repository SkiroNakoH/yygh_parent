package com.atguigu.yygh.hosp.testMongo.repository;

import com.atguigu.yygh.hosp.testMongo.pojo.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Deprecated
@Repository
public interface UserRepository extends MongoRepository<User,String> {
    List<User> findByName(String name);

    List<User> findByNameLike(String name);
}
