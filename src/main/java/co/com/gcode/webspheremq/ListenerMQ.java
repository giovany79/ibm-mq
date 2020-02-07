package co.com.gcode.webspheremq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ListenerMQ {
	
	@JmsListener(destination = "DEV.BASE.TOPIC")
	public void receiveMessage1(String message) {
		System.out.println("Mensaje recibido 1: " + message);
	}
	
	@JmsListener(destination = "DEV.BASE.TOPIC")
	public void receiveMessage2(String message) {
		System.out.println("Mensaje recibido 2: " + message);
	}

}