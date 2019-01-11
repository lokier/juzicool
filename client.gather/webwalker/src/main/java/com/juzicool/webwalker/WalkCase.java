package com.juzicool.webwalker;


import com.juzicool.core.Promise;

public abstract class WalkCase  {

    protected WalkClient mClient = null;

    /**
     * 返回该case的超时时间
     * @return 毫秒
     */
   public abstract long getTimeout();

   protected abstract void doCase(WalkClient wclient, Promise pormise);


    /**
     * 创建
     */
    protected void onCreate(WalkClient client){
        mClient = client;
    }

    /**
     * 销毁
     */
    protected void onDestroy(){
        mClient = null;
    }

    /**
     * 中途取消执行。
     */
    protected void onCancel(){
        try {
            mClient.getWebClient().close();
        }catch (Exception ex){

        }
    }


    public static class DumpCase extends WalkCase {

        @Override
        public long getTimeout() {
            return 60 * 1000;
        }

        @Override
        public void doCase(WalkClient client, Promise pormise) {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onCreate(WalkClient client) {

        }

        @Override
        public void onDestroy() {

        }


    }
}
