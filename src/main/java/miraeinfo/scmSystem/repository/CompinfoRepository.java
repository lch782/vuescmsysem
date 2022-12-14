package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Compinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompinfoRepository extends JpaRepository<Compinfo, String>, JpaSpecificationExecutor<Compinfo> {

}