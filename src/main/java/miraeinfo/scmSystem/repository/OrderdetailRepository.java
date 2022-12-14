package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Orderdetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderdetailRepository extends JpaRepository<Orderdetail, Integer>, JpaSpecificationExecutor<Orderdetail> {

}