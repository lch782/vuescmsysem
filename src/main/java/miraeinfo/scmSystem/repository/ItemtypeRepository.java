package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Itemtype;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ItemtypeRepository extends JpaRepository<Itemtype, Integer>, JpaSpecificationExecutor<Itemtype> {
    List<Itemtype> findAllByNonuseAndWeb(String nonuse, String web);
}