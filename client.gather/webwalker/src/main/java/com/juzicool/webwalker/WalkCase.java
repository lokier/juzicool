package com.juzicool.webwalker;

import java.io.Closeable;
import java.io.IOException;

public interface WalkCase  {

    /**
     * 返回该case的超时时间
     * @return 毫秒
     */
    long getTimeout();

    void doCase(WalkClient client,WalkPormise pormise);

    /**
     * 执行过程中，取消执行。
     */
    void cancel();

    public class DumpCase implements WalkCase {

        @Override
        public long getTimeout() {
            return 60 * 1000;
        }

        @Override
        public void doCase(WalkClient client, WalkPormise pormise) {

        }

        @Override
        public void cancel() {

        }

    }
}
