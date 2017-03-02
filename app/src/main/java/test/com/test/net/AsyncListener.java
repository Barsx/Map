package test.com.test.net;

import test.com.test.model.CustomData;

public interface AsyncListener {
	void onTaskPrepared();
    void onTaskCompleted(CustomData o);
}
