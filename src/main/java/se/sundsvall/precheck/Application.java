package se.sundsvall.precheck;

import se.sundsvall.dept44.ServiceApplication;

import static org.springframework.boot.SpringApplication.run;
import org.springframework.cloud.openfeign.EnableFeignClients;

@ServiceApplication
@EnableFeignClients
public class Application {

	public static void main(String[] args) {
		run(Application.class, args);
	}
}