package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Custinfo;
import miraeinfo.scmSystem.entity.IteminfoMini;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustinfoRepository extends JpaRepository<Custinfo, String>, JpaSpecificationExecutor<Custinfo> {
//    Custinfo findByBiznoAndPassword(String bizno, String password);
    Custinfo findByBiznoAndPassword(String bizno, String password);

    Custinfo findByBizno(String bizno);

    @Query(value = "select * from CUSTINFO where ID = :userId", nativeQuery = true)
    Custinfo findByIdNative(@Param("userId") String userId);

    Custinfo findByIdAndPassword(String userId, String password);
}