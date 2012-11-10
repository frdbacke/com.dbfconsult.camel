package com.dbfconsult.jms;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JmsConsumeViaCamel {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "camelContext.xml" });
	}

}
