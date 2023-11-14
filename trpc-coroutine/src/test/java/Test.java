import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.reflections.ReflectionUtils;

public class Test {

    @org.junit.Test
    public void testCreateThreadFactory()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException {
        Class<?> thread = ReflectionUtils.forName("java.lang.Thread");
        Method ofVirtualMethod = thread.getDeclaredMethod("ofVirtual");
        Object virtual = ofVirtualMethod.invoke(thread);
        Class<?> virtualClazz = ofVirtualMethod.getReturnType();
        Method nameMethod = virtualClazz.getMethod("name", String.class, long.class);
        nameMethod.invoke(virtual, "test", 1);
        try {
            Method schedulerMethod = virtualClazz.getDeclaredMethod("scheduler", Executor.class);
            schedulerMethod.setAccessible(true);
            schedulerMethod.invoke(virtual, Executors.newWorkStealingPool(2));
        } catch (NoSuchMethodException exception) {
            System.out.println(exception);
        }
        Method factoryMethod = virtualClazz.getMethod("factory");
        ThreadFactory threadFactory = (ThreadFactory) factoryMethod.invoke(virtual);
        ExecutorService threadPool = new ThreadPoolExecutor(2,
                2, 10,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(20), threadFactory);
        threadPool.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("testCreateThreadFactory");
        });
        threadPool.awaitTermination(2, TimeUnit.SECONDS);
    }

}
