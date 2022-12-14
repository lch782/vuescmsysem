package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Inorderd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InorderdRepository extends JpaRepository<Inorderd, Integer>, JpaSpecificationExecutor<Inorderd> {

}