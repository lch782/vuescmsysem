package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Buyorderd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BuyorderdRepository extends JpaRepository<Buyorderd, Integer>, JpaSpecificationExecutor<Buyorderd> {

    List<Buyorderd> findAllByBuycode (String buyCode);

    Buyorderd findByBuycodeAndBuyseq (String buyCode, int buySeq);
}