package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Codemast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CodemastRepository extends JpaRepository<Codemast, String>, JpaSpecificationExecutor<Codemast> {

}