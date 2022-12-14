package miraeinfo.scmSystem.service;

import miraeinfo.scmSystem.repository.CustinfoRepository;
import miraeinfo.scmSystem.repository.OrderinfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
public class CommonService {



    public CommonService () {

    }

    public void testFunc () {
        System.out.println("thisFunc");
    }
}
