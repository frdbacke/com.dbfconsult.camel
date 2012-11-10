package com.dbfconsult.camel.stoproute;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class StopRouteProcessor implements Processor {

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

}
