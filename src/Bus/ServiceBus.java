package Bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class ServiceBus extends Bus {
	
	private final Handler mainThread = new Handler(Looper.getMainLooper());
	
	@Override
	public void post(final Object event) {
		if(Looper.myLooper() == Looper.getMainLooper()) {
			super.post(event);
		} else {
			mainThread.post(new Runnable() {
				@Override
				public void run() {
					post(event);
				}
			});
		}
	}

}
