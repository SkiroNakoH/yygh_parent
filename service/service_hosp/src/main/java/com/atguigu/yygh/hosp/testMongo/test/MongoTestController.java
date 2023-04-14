package com.atguigu.yygh.hosp.testMongo.test;

import com.atguigu.yygh.common.utils.Result;

import com.atguigu.yygh.hosp.testMongo.pojo.User;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@Deprecated
@Api(tags = "MongodbTemplate测试")
@RestController
//@RequestMapping("/admin/mongo/mongoTemplate")
@RequestMapping("/mongo/mongoTemplate")
public class MongoTestController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @ApiOperation("新增")
    @PostMapping("/insert")
    public Result insert(User user){
    /*    if ("".equals(user.getId())){
            user.setId(null);
        }*/

        if (StringUtils.isEmpty(user.getId())) {
            user.setId(null);
        }

        mongoTemplate.insert(user);
        return Result.ok();
    }

    @ApiOperation("查询所有")
    @GetMapping("/findAll")
    public Result findAll(){
        List<User> userList = mongoTemplate.findAll(User.class);
        return Result.ok().data("list",userList);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/{id}")
    public Result getById(@PathVariable String id){
        User user = mongoTemplate.findById(id, User.class);

        return Result.ok().data("user",user);
    }


    @ApiOperation("条件查询:根据年龄查询")
    @GetMapping("/findByAge")
    public Result findByAge(Integer age){
        Query query = new Query(Criteria.where("age").is(age));

        List<User> userList = mongoTemplate.find(query, User.class);

        return Result.ok().data("list",userList);
    }

    @ApiOperation("模糊查询:根据姓名查询")
    @GetMapping("findLikeByName")
    public Result findLikeByName(String name){
        String regex = String.format("%s%s%s", "^.*", name, ".*$");     //^.*三.*$
        Pattern compile = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        Query query = new Query(Criteria.where("name").regex(compile));
        List<User> userList = mongoTemplate.find(query, User.class);

        return Result.ok().data("list",userList);
    }

    @ApiOperation("分页查询")
    @GetMapping("/findPage/{page}/{size}")
    public Result findPage(@PathVariable Integer page,@PathVariable Integer size){

        List<User> userList = mongoTemplate.find(new Query().skip((long) (page - 1) * size).limit(size), User.class);

        return Result.ok().data("list",userList);
    }

    @ApiOperation("根据id修改")
    @PutMapping("/updateById")
    public Result updateById(User user){
        Update update = new Update();
        if (!StringUtils.isEmpty(user.getName())) {
            update.set("name",user.getName());
        }
        if (!StringUtils.isEmpty(user.getAge())) {
            update.set("age",user.getAge());
        }
        if (!StringUtils.isEmpty(user.getEmail())) {
            update.set("email",user.getEmail());
        }
        if (!StringUtils.isEmpty(user.getCreateDate())) {
            update.set("createDate",user.getCreateDate());
        }

        UpdateResult updateResult = mongoTemplate.upsert(new Query(Criteria.where("_id").is(user.getId())), update, User.class);

        long modifiedCount = updateResult.getModifiedCount();
        return Result.ok().data("count","修改行数"+modifiedCount);
    }


    @ApiOperation("根据_id删除")
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id){
        DeleteResult deleteResult = mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), User.class);

        long deletedCount = deleteResult.getDeletedCount();
        return Result.ok().data("count","删除行数"+deletedCount);
    }
}
