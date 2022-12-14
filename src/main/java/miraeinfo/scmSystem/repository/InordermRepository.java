package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Inorderm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InordermRepository extends JpaRepository<Inorderm, String>, JpaSpecificationExecutor<Inorderm> {

}