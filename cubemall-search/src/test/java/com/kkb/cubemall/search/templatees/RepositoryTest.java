package com.kkb.cubemall.search.templatees;


import com.kkb.cubemall.search.model.Blog;
import com.kkb.cubemall.search.repository.BlogRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

//@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RepositoryTest {

    @Autowired
    private BlogRepository blogRepository;

    @Test
    void testAddDocument(){
        Blog blog=new Blog();
        for (int i = 0; i < 50; i++) {
            blog.setId((long) (i+1));
            blog.setTitle("测试文档" + (i+1));
            blog.setContent("测试文档的内容1");
            blog.setComment("注释内容");
            blog.setMobile("133444556677");
            blogRepository.save(blog);
        }
    }

    @Test
    void testUpdateDocument(){
        Optional<Blog> optional=blogRepository.findById(1l);
        if(optional.isPresent()){
            Blog blog=optional.get();
            blog.setTitle("hello hello jack");
            blogRepository.save(blog);
        }
    }

    @Test
    void deleteDocument(){
        blogRepository.deleteById(1l);
    }

    @Test
    void testFindByTitle(){
        List<Blog> blogs = blogRepository.findByTitle("测试");
        blogs.forEach(System.out::println);
    }

    @Test
    void testFindByTitleAndContent(){
        List<Blog> blogs = blogRepository.findByTitleAndContent("37", "测试");
        blogs.forEach(System.out::println);
    }

}
