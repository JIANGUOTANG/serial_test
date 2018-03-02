package ad;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.jian.pus.R;
import com.ximon.core.XiMon;
import com.ximon.core.net.interceptors.DebugInterceptor;

import org.greenrobot.greendao.database.Database;

import ad.db.DaoMaster;
import ad.db.DaoSession;
import ad.db.SharePeferenceUtil;
import ad.util.RegisterManager;
import cn.jpush.android.api.JPushInterface;

import static ad.Constants.BASH_PATH;


/**
 * Created by ying on 17-10-25.
 */
public class App extends MultiDexApplication {
    private String TAG = "Application ";
    //视频缓存
    private HttpProxyCacheServer proxy;
    public static final boolean ENCRYPTED = true;
    private static DaoSession daoSession;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        //先执行上面代码再执行初始化
        initDatabase();
    }
    @Override
    public void onTerminate() {
        // 程序终止的时候执行

        isFroground = false;
        super.onTerminate();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        XiMon.Companion.init(this)
                .withInterceptor(new DebugInterceptor("tes", R.raw.test))
                .withApiHost(BASH_PATH)
                .configure();
        //获取标识码，注册
        isFroground = true;
        JPushInterface.setDebugMode(true);
        //注册广告机到推送
        if (SharePeferenceUtil.getCodeNumberId(this) != 0) {
            Log.e("mianJian", SharePeferenceUtil.getCodeNumberId(this) + "jian");
            JPushInterface.setAlias(this, SharePeferenceUtil.getCodeNumberId(this) + "", null);// 设置开启日志,发布时请关闭日志
            JPushInterface.init(this);            // 初始化 JPush
        }else{
            thread.start();
        }
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)// 1 Gb for cache
                .maxCacheFilesCount(20)
                .build();
    }

    private static class AppHolder {
        private final static App instance = new App();
    }

    public static App getInstance() {
        return AppHolder.instance;
    }

    private void initDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "users-db-encrypted" : "users-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getDaoSession() {

        return daoSession;
    }

    //用于标记停止线程
    private boolean isFroground = true;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (SharePeferenceUtil.getCodeNumberId(App.this) == 0 && isFroground) {
                RegisterManager.getCodeNumber(App.this);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    });


}