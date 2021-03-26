package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.Git;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GitDao extends JpaRepository<Git, Long> {
    List<Git> findAll();

    Git findByLoginName(String loginName);

    List<Git> findListByLoginName(String loginName);

    Git findByLoginNameAndLoginPwd(String loginName, String loginPwd);

    @Query(value = "select * from cl_test where login_name = ?1",nativeQuery = true)
    List<Git> queryUser(String loginName);

    //Param使用org.springframework.data.repository.query.Param
    @Query(value = "select * from cl_test where login_name like %:name%", nativeQuery = true)
    List<Git> queryUserParam(@Param("name") String name);
}
