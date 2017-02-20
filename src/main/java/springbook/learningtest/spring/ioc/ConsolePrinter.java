package springbook.learningtest.spring.ioc;

import springbook.learningtest.spring.ioc.bean.Printer;

public class ConsolePrinter implements Printer {

	@Override
	public void print(String message) {
		System.out.println(message);
	}

}
