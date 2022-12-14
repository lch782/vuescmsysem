package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Codedet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CodedetRepository extends JpaRepository<Codedet, String>, JpaSpecificationExecutor<Codedet> {

}