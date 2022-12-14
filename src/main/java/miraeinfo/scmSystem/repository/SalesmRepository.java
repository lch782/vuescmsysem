package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Salesm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SalesmRepository extends JpaRepository<Salesm, String>, JpaSpecificationExecutor<Salesm> {

}