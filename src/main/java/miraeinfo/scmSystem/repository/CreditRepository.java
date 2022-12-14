package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CreditRepository extends JpaRepository<Credit, String>, JpaSpecificationExecutor<Credit> {

}