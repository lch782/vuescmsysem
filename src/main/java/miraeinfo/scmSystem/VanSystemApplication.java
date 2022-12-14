package miraeinfo.scmSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //(exclude = DataSourceAutoConfiguration.class) // 임시
public class VanSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(VanSystemApplication.class, args);
	}

}
