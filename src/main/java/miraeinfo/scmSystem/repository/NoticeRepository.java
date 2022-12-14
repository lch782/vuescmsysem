package miraeinfo.scmSystem.repository;

import miraeinfo.scmSystem.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer>, JpaSpecificationExecutor<Notice> {
    List<Notice> findAllByNumseqGreaterThanAndCompcodeEqualsOrderByNumseqAsc (int numseq, String searchGroup);
}