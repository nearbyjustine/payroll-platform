package dev.justine.payroll;

import org.springframework.boot.SpringApplication;

public class TestPayrollApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(PayrollApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
