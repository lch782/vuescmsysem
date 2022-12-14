package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Buyorderm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BuyordermRepository extends JpaRepository<Buyorderm, String>, JpaSpecificationExecutor<Buyorderm> {
    Buyorderm findByBuycode (String buyCode);

    List<Buyorderm> findAllByCustcode (String custCode);

}