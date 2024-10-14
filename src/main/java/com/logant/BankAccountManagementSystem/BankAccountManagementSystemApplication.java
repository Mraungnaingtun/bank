package com.logant.BankAccountManagementSystem;

import com.logant.BankAccountManagementSystem.Security.config.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@EnableConfigurationProperties(RSAKeyRecord.class)
@SpringBootApplication
public class BankAccountManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankAccountManagementSystemApplication.class, args);
	}

}

@Controller
class MyBackend {
	@GetMapping("/")
	public String welcome(){
		//tell bankend service is running!!!!!🙄
		return "index.html";
	}	
}
