package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Iteminfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IteminfoRepository extends JpaRepository<Iteminfo, String>, JpaSpecificationExecutor<Iteminfo> {

    List<Object> findByItemcode (String itemcode);
}