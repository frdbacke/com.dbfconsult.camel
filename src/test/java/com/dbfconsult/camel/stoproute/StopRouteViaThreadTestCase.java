package com.dbfconsult.camel.stoproute;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;

public class StopRouteViaThreadTestCase {

	@Test
	public void testStopRouteFromRoute() throws Exception {
		// create camel, add routes, and start camel
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(createMyRoutes());
		context.start();

		assertTrue("Route myRoute should be started",
				context.getRouteStatus("myRoute").isStarted());
		assertTrue("Route bar should be started", context.getRouteStatus("bar")
				.isStarted());

		// setup mock expectations for unit test
		MockEndpoint start = context.getEndpoint("mock:start",
				MockEndpoint.class);
		start.expectedMessageCount(1);
		MockEndpoint done = context
				.getEndpoint("mock:done", MockEndpoint.class);
		done.expectedMessageCount(1);

		// send a message to the route
		ProducerTemplate template = context.createProducerTemplate();
		template.sendBody("direct:start", "Hello Camel");

		// just wait a bit for the thread to stop the route
		Thread.sleep(1500);

		// the route should now be stopped
		assertTrue("Route myRoute should be stopped",
				context.getRouteStatus("myRoute").isStopped());
		assertTrue("Route bar should be started", context.getRouteStatus("bar")
				.isStarted());

		// stop camel
		context.stop();

		// unit test assertions
		start.assertIsSatisfied();
		done.assertIsSatisfied();
	}
	
	public RouteBuilder createMyRoutes() throws Exception {
	    return new RouteBuilder() {
	        @Override
	        public void configure() throws Exception {
	            from("direct:start").routeId("myRoute")
	                .to("mock:start")
	                .process(new Processor() {
	                    Thread stop;

	                    @Override
	                    public void process(final Exchange exchange) throws Exception {
	                        // stop this route using a thread that will stop
	                        // this route gracefully while we are still running
	                        if (stop == null) {
	                            stop = new Thread() {
	                                @Override
	                                public void run() {
	                                    try {
	                                        exchange.getContext().stopRoute("myRoute");
	                                    } catch (Exception e) {
	                                        // ignore
	                                    }
	                                }
	                            };
	                        }

	                        // start the thread that stops this route
	                        stop.start();
	                    }
	                }).to("mock:done");
	            
	            from("direct:bar").routeId("bar")
	                .to("mock:bar");
	        }
	    };
	}	
}
