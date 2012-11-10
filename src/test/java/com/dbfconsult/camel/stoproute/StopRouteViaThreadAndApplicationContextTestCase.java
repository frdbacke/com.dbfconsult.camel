package com.dbfconsult.camel.stoproute;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StopRouteViaThreadAndApplicationContextTestCase extends CamelSpringTestSupport {

	@EndpointInject(uri="direct:start")
	ProducerTemplate template;
	
	@EndpointInject(uri="mock:start")
	MockEndpoint start;
	
	@EndpointInject(uri="mock:done")
	MockEndpoint done;
	
	@Test
	public void testStopRouteFromRoute() throws Exception {
		template.sendBody("direct:start", "Hello Camel");

		start.expectedMessageCount(1);
		done.expectedMessageCount(1);
		
		
		// just wait a bit for the thread to stop the route
		Thread.sleep(1500);

		// the route should now be stopped
		assertTrue("Route myRoute should be stopped",
				context.getRouteStatus("myRoute").isStopped());

		// stop camel
		context.stop();

		// unit test assertions
		start.assertIsSatisfied();
		done.assertIsSatisfied();
	}
	

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("camelContext.xml");
	}	
}
