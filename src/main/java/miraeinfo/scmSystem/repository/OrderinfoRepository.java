package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Orderinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderinfoRepository extends JpaRepository<Orderinfo, String>, JpaSpecificationExecutor<Orderinfo> {

    List<Orderinfo> findAllByCustcode (String custCode);
}