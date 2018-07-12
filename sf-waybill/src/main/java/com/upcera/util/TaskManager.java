package com.upcera.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程操作类
 *
 * @author Panda.HuangWei.
 * @version 1.0 .
 * @since 2016-12-19 10:53.
 */
public class TaskManager {
    private static final int DEFAULT_POOL_SIZE = 5;
    private static Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private static TaskManager taskManager;
    // 线程池
    private ThreadPoolExecutor threadPool;
    // 每个主线程能够同时创建的子线程数
    private int concurrentSubTask;
    // 任务控制器
    private Map<Long, TaskControl> taskControl;
    // 任务队列的缺省对象
    private Object NULL = new Object();
    // 关闭任务
    private boolean shutdown;
    // 休眠时间
    private int sleepTimeWhenNeed = 500;

    //休眠5秒
    private int sleepTime = 5;

    /**
     * 根据系统核数初始化线程数
     *
     * @return 线程控制实例
     */
    public static TaskManager getInstance() {
        if (taskManager == null) {
            synchronized (TaskManager.class) {
                if (taskManager == null) {
                    taskManager = new TaskManager(getDefaultTaskCount());
                }
            }
        }
        return taskManager;
    }

    /**
     * 指定线程数初始化线程实例
     *
     * @param maxTask 线程数
     * @return 线程控制实例
     */
    public static TaskManager newInstance(int maxTask) {
        if (maxTask < 5) {
            maxTask = getDefaultTaskCount();
        }
        return new TaskManager(maxTask);
    }

    /**
     * 根据任务数初始化线程实例
     *
     * @param maxTask 线程数
     * @return 线程控制实例
     */
    public static TaskManager newInstanceByTaskNum(int maxTask) {
        if (taskManager == null) {
            synchronized (TaskManager.class) {
                if (taskManager == null) {
                    taskManager = new TaskManager(maxTask);
                }
            }
        }
        return taskManager;
    }

    private TaskManager(int maxTask) {
        this.taskControl = new ConcurrentHashMap<Long, TaskControl>();
        int maxPoolSize = maxTask;
        int corePoolSize = maxTask;
        this.concurrentSubTask = maxTask;
        if (maxTask <= 0) {
            corePoolSize = DEFAULT_POOL_SIZE;
            maxPoolSize = corePoolSize;
            this.concurrentSubTask = corePoolSize / 2;
            if (this.concurrentSubTask < 1) {
                this.concurrentSubTask = 1;
            }
        }

        this.shutdown = false;

        this.threadPool = new ThreadPoolExecutor(corePoolSize, // core pool size
                maxPoolSize, // max pool size
                10, // alive time: 10 seconds
                TimeUnit.SECONDS,//
                new TaskBlockingQueue<Runnable>(), //
                new TaskPolicy());
    }

    /**
     * 缺省任务数
     *
     * @return CPU核数
     */
    public static int getDefaultTaskCount() {
        return getProcessorCount();
    }

    public void shutdown() {
        shutdown = true;
        threadPool.shutdown();
    }

    /**
     * 获取任务控制器
     *
     * @return 任务控制器
     */
    private TaskControl getTaskControl() {
        long threadId = Thread.currentThread().getId();
        TaskControl ctrl = taskControl.get(threadId);
        if (ctrl == null) {
            ctrl = new TaskControl(concurrentSubTask, getCaller());
            taskControl.put(threadId, ctrl);
        }
        return ctrl;
    }

    private String getCaller() {
        StackTraceElement[] stack = (new Throwable()).getStackTrace();
        if ((stack != null) && (stack.length > 3)) {
            StackTraceElement ste = stack[3];
            return ste.getMethodName();
        }
        return "";
    }

    /**
     * 设置当前线程的任务超时时间
     *
     * @param timeout
     * @param raiseError
     */
    public void setTimeout(long timeout, boolean raiseError) {
        getTaskControl().setTimeout(System.currentTimeMillis() + timeout, raiseError);
    }

    /**
     * 设置是否显示任务完成信息
     *
     * @param showInfo
     */
    public void setShowInfo(boolean showInfo) {
        getTaskControl().setShowInfo(showInfo);
    }

    /**
     * 设置异常类型
     *
     * @param errorClazz
     */
    public void setErrorClass(Class<? extends RuntimeException> errorClazz) {
        getTaskControl().setErrorClass(errorClazz);
    }

    /**
     * 在线程池空闲时增加任务
     *
     * @param task
     */
    public void executeTaskWhileNoFull(final Runnable task) {
        final TaskControl ctrl = getTaskControl();
        if (ctrl.incrementTask()) {
            try {
                threadPool.execute(new Runnable() {
                    public void run() {
                        try {
                            task.run();
                        } catch (Exception e) {
                            ctrl.incrementFailTask(e);
                            logger.error(String.format("[%s] task run error.", ctrl.getCallerInfo()), e);
                        } finally {
                            ctrl.decrementTask();
                        }
                    }
                });
            } catch (Exception e) {
                logger.error("threadPool execute error", e);
                ctrl.decrementTask();
                ctrl.incrementFailTask(e);
            }
        }
    }

    /**
     * 获得当前线程所发起的任务活动数量
     *
     * @return 任务活动数量
     */
    public int getCurrentThreadActiveCount() {
        long threadId = Thread.currentThread().getId();
        TaskControl ctrl = taskControl.get(threadId);
        return (ctrl == null) ? 0 : ctrl.getTaskCount();
    }

    /**
     * 等待直到本线程发起的任务完成
     */
    public void waitWhileTaskFinish() {
        long threadId = Thread.currentThread().getId();
        TaskControl ctrl = taskControl.get(threadId);
        if (ctrl != null) {
            try {
                boolean isTimeout = false;
                while ((ctrl.getTaskCount() > 0) && !shutdown && !isTimeout) {
                    sleep();
                    isTimeout = ctrl.checkTimeout();
                }

                ctrl.showInfo();
            } finally {
                taskControl.remove(threadId);
            }
        }
    }

    /**
     * 等待直到本线程发起的任务完成
     */
    public void waitTaskFinish() {
        long threadId = Thread.currentThread().getId();
        TaskControl ctrl = taskControl.get(threadId);
        if (ctrl != null) {
            try {
                boolean isTimeout = false;
                while ((ctrl.getTaskCount() > 0) && !shutdown && !isTimeout) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (Exception e) {
                        logger.error("task waiting error.", e);
                    }
                    isTimeout = ctrl.checkTimeout();
                }

                ctrl.showInfo();
            } finally {
                taskControl.remove(threadId);
            }
        }
    }

    /**
     * 等待直到本线程发起的任务完成
     *
     * @param waitMultiple 打印日志需要等待的睡眠次数
     * @param waitMessage  日志信息
     */
    public void waitWhileTaskFinish(int waitMultiple, String waitMessage) {
        long threadId = Thread.currentThread().getId();
        TaskControl ctrl = taskControl.get(threadId);
        if (ctrl != null) {
            try {
                boolean isTimeout = false;
                int count = 0;
                while ((ctrl.getTaskCount() > 0) && !shutdown && !isTimeout) {
                    sleep();
                    if (++count % waitMultiple == 0) {
                        logger.info(waitMessage);
                        count = 0;
                    }
                    isTimeout = ctrl.checkTimeout();
                }

                ctrl.showInfo();
            } finally {
                taskControl.remove(threadId);
            }
        }
    }

    public int getConcurrentSubTask() {
        return concurrentSubTask;
    }

    private void sleep() {
        try {
            Thread.sleep(sleepTimeWhenNeed);
        } catch (Exception e) {
            logger.error("task waiting error.", e);
        }
    }

    class TaskBlockingQueue<E> extends LinkedBlockingQueue<E> {

        private static final long serialVersionUID = 1L;

        public boolean offer(E o) {
            if (threadPool.getPoolSize() < threadPool.getMaximumPoolSize()) {
                return false;
            }
            return super.offer(o);
        }
    }

    class TaskPolicy implements RejectedExecutionHandler {

        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            executor.getQueue().add(r);
        }
    }

    class TaskControl {

        private long startTime;
        private AtomicInteger failTask;
        private AtomicInteger totalTask;
        private BlockingQueue<Object> ctrl;
        private String callerInfo;
        private Throwable error;
        private long timeout;
        private boolean raiseError;
        private boolean showTimeout;
        private boolean showInfo;
        private Class<? extends RuntimeException> errorClazz;

        public TaskControl(int concurrentSubTask, String callerInfo) {
            this.startTime = System.currentTimeMillis();
            this.failTask = new AtomicInteger(0);
            this.totalTask = new AtomicInteger(0);
            this.callerInfo = callerInfo;
            this.timeout = 0;
            this.raiseError = false;
            this.showTimeout = false;
            this.showInfo = true;
            this.errorClazz = null;
            this.ctrl = new ArrayBlockingQueue<Object>(concurrentSubTask);
        }

        public void incrementFailTask(Throwable error) {
            failTask.incrementAndGet();
            synchronized (this) {
                if ((this.error == null)) {
                    this.error = error;
                }
            }
        }

        public boolean incrementTask() {
            boolean isTimeout = false;
            try {
                isTimeout = checkTimeout();
                while (!isTimeout && !shutdown && !ctrl.offer(NULL, 10, TimeUnit.SECONDS)) {
                    isTimeout = checkTimeout();
                }

                if (!isTimeout) {
                    totalTask.incrementAndGet();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return !isTimeout;
        }

        public void decrementTask() {
            ctrl.poll();
        }

        public int getTaskCount() {
            return ctrl.size();
        }

        public String getCallerInfo() {
            return callerInfo;
        }

        public void setTimeout(long timeout, boolean raiseError) {
            this.timeout = timeout;
            this.raiseError = raiseError;
        }

        public void setShowInfo(boolean showInfo) {
            this.showInfo = showInfo;
        }

        public void setErrorClass(Class<? extends RuntimeException> errorClazz) {
            this.errorClazz = errorClazz;
        }

        private boolean checkTimeout() {
            boolean isTimeout = (timeout > 0) && (System.currentTimeMillis() > timeout);
            if (isTimeout) {
                if (!showTimeout) {
                    synchronized (this) {
                        if (!showTimeout) {
                            showTimeout = true;

                            double time = (System.currentTimeMillis() - this.startTime) / 1000.0;
                            String msg = String.format("[%s] task is timeout: %.2f seconds.", callerInfo, time);

                            if (raiseError) {
                                throw (error != null) ? new RuntimeException(msg, error) : new RuntimeException(msg);
                            } else {
                                logger.warn(msg);
                            }
                        }
                    }
                }
            }

            return isTimeout;
        }

        public void showInfo() {
            if (failTask.get() > 0) {
                if (errorClazz != null) {
                    try {
                        String msg = String.format("[%s] run task fail count: %d", callerInfo, failTask.get());
                        throw errorClazz.getConstructor(String.class, Throwable.class).newInstance(msg, error);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    String msg = String.format("[%s] run task fail count: %d", callerInfo, failTask.get());
                    throw new RuntimeException(msg, error);
                }
            }

            if (showInfo) {
                double time = (System.currentTimeMillis() - this.startTime) / 1000.0;
                logger.info(String.format("[%s] run %d task in %.2f seconds.", callerInfo, totalTask.get(), time));
            }
        }
    }

    private static final int DEFAULT_PROCESSORS_NUM = 4;

    public static int getProcessorCount() {
        String number = System.getenv("NUMBER_OF_PROCESSORS");
        try {
            if (number != null) {
                return Integer.parseInt(number);
            }
        } catch (Exception e) {
            logger.warn("can't getProcessorCount,use defaut:"
                    + DEFAULT_PROCESSORS_NUM);
        }

        return DEFAULT_PROCESSORS_NUM;
    }
}

