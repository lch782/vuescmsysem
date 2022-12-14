package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.IteminfoMini;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IteminfoRepositoryMini extends JpaRepository<IteminfoMini, String>, JpaSpecificationExecutor<IteminfoMini> {
    @Query(value = "select  ITEMCODE , ITEMNAME , TYPE1 , TYPE2 , TYPE3  from ITEMINFO", nativeQuery = true)
    List<IteminfoMini> findAllMini();


}
