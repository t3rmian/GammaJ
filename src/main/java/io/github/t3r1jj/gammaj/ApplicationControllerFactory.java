package io.github.t3r1jj.gammaj;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import javafx.application.HostServices;
import javafx.util.Callback;

public class ApplicationControllerFactory implements Callback<Class<?>, Object> {

    private final HostServices hostServices;
    private final TrayRunnable trayRunnable;

    public ApplicationControllerFactory(HostServices hostServices, TrayRunnable trayRunnable) {
        this.hostServices = hostServices;
        this.trayRunnable = trayRunnable;
    }

    @Override
    public Object call(Class<?> param) {
        try {
            for (Constructor<?> constructor : param.getConstructors()) {
                if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].equals(HostServices.class)) {
                    return constructor.newInstance(hostServices);
                } else if (constructor.getParameterCount() == 2 && Arrays.equals(constructor.getParameterTypes(), new Class<?>[]{HostServices.class, TrayRunnable.class})) {
                    return constructor.newInstance(hostServices, trayRunnable);
                }
            }
            return param.newInstance();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
