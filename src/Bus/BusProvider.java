package Bus;

import com.squareup.otto.Bus;

public class BusProvider {
	private static final ServiceBus BUS = new ServiceBus();
	
	public static ServiceBus getInstance() {
		return BUS;
	}
	
	private BusProvider() {
		// no instances
	}
}
