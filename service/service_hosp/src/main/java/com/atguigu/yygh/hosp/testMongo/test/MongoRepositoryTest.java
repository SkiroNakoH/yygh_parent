package com.atguigu.yygh.hosp.testMongo.test;

import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.hosp.testMongo.pojo.User;
import com.atguigu.yygh.hosp.testMongo.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Deprecated
@Api(tags = "MongoRepository测试")
@RestController
//@RequestMapping("/admin/mongo/repository")
@RequestMapping("/mongo/repository")
public class MongoRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @ApiOperation("新增")
    @PostMapping("/insert")
    public Result insert(User user) {
        if (StringUtils.isEmpty(user.getId())) {
            user.setId(null);
        }

        User save = userRepository.save(user);

        return Result.ok().data("save", save);
    }

    @ApiOperation("查询所有")
    @GetMapping("/findAll")
    public Result findAll() {
        List<User> userList = userRepository.findAll();

        return Result.ok().data("list", userList);
    }

    @ApiOperation("根据_id查询")
    @GetMapping("/{id}")
    public Result getById(@PathVariable String id) {
        Optional<User> userOptional = userRepository.findById(id);

        //非空校验
        if (!userOptional.isPresent()) {
            return Result.error().message("id有误");
        }
        User user = userOptional.get();

        return Result.ok().data("user", user);
    }

    @ApiOperation("条件查询")
    @GetMapping("/findQuery")
    public Result findQuery(User user) {
        Example<User> example = Example.of(user);
        List<User> userList = userRepository.findAll(example);

        return Result.ok().data("list", userList);
    }

    @ApiOperation("模糊查询:根据姓名查询")
    @GetMapping("findLikeByName")
    public Result findLikeByName(String name) {
        ExampleMatcher matching = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true);  //改变默认大小写忽略方式：忽略大小写

        User user = new User();
        user.setName(name);

        Example<User> example = Example.of(user, matching);

        List<User> userList = userRepository.findAll(example);
        return Result.ok().data("list", userList);
    }

    @ApiOperation("分页查询")
    @GetMapping("findPage/{page}/{size}")
    public Result findPage(@PathVariable Integer page, @PathVariable Integer size) {

        PageRequest pageable = PageRequest.of(page - 1, size);
        Page<User> userPage = userRepository.findAll(pageable);

        List<User> userList = userPage.toList();
        return Result.ok().data("page", userPage).data("list", userList);
    }


    @ApiOperation("修改")
    @PutMapping("/update")
    public Result update(User user) {
        String userId = user.getId();
        if (StringUtils.isEmpty(userId)) {
            return Result.error().message("id不能为空");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        /*
         * //userOptional非空校验
         * public boolean isPresent() {
         *         return value != null;
         *     }
         */
        if (!userOptional.isPresent()) {
            return Result.error().message("id有误");
        }

        //user存在
        User save = userRepository.save(user);
        return Result.ok().data("save", save);
    }


    @ApiOperation("根据_id删除")
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id) {
        userRepository.deleteById(id);
        return Result.ok();
    }

    @ApiOperation("自定义接口查询findByNameLike")
    @GetMapping("/findByNameLike")
    public Result findByNameLike(String name) {
        List<User> userList = userRepository.findByNameLike(name);
       return Result.ok().data("list",userList);
    }

    @ApiOperation("自定义接口查询findByName")
    @GetMapping("/findByName")
    public Result findByName(String name) {
        List<User> userList = userRepository.findByName(name);
        return Result.ok().data("list",userList);
    }

}
