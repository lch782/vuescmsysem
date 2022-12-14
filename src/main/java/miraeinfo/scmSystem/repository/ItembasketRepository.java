package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Itembasket;
import miraeinfo.scmSystem.entity.ItembasketPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItembasketRepository extends JpaRepository<Itembasket, ItembasketPK>, JpaSpecificationExecutor<Itembasket> {

}