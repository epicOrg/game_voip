package services;

import android.content.Context;

/**
 * @author	Noris
 * @since	2015-03-26
 */

public interface Service {
	
	/**
	 * @return service response message
	 */
	public void start();
    public void setContext(Context context);
}