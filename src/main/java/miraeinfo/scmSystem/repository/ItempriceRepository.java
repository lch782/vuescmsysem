package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Itemprice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ItempriceRepository extends JpaRepository<Itemprice, String>, JpaSpecificationExecutor<Itemprice> {

    List<Object> findByCustcodeAndItemcode(String custCode, String itemcode);
}