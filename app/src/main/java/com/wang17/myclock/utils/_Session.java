package com.wang17.myclock.utils;

import android.os.Environment;


import com.wang17.myclock.model.Commodity;
import com.wang17.myclock.model.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2016/10/28.
 */

public class _Session {
    public static final String ACTION_ALARM_NIANFO_OVER = "ACTION_ALARM_NIANFO_OVER";
    public static final String ACTION_ALARM_TRADE = "ACTION_ALARM_TRADE";
    public static final String ACTION_ALARM_STOCK_SCREEN = "ACTION_ALARM_STOCK_SCREEN";

    public static final String NIAN_FO_ACTION = "NIAN_FO_VOICE";
    public static final String NIAN_FO_TIMER = "NIAN_FO_TIMER";
    public static final String POWER_MANAGER_ACTION = "POWER_MANAGER_VOICE";
    public static final UUID UUID_NULL = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final float ALERT_VOLUMN = 0.2f;
    public static final DateTime BIRTHDAY = new DateTime(1985, 11, 27);

    public static String BANK_CARD = "[" +
            "{'index':'0','bank':'微贷','card':'0000'}," +
            "{'index':'1','bank':'借呗','card':'0000'}," +
            "{'index':'2','bank':'交行','card':'7086'}," +
            "{'index':'3','bank':'浦发','card':'1826'}," +
            "{'index':'4','bank':'工行','card':'0714'}," +
            "{'index':'5','bank':'花呗','card':'0000'}]";

    public static int[] GEARS = new int[]{180, 120, 80, 60, 40};
    public static String[] GEAR_NAMES = new String[]{"等待", "步行", "单车", "汽车", "高速"};
    public static String[] TALLY_MUSIC_NAMES ;

    public static final File ROOT_DIR = new File(Environment.getExternalStorageDirectory()+"/0/myphone");

    public static List<Commodity> commoditys;

    private static final String _ATAG = "wangsc";

    static {
        try {
            /**
             * 加载念佛音乐列表
             */
            if (!ROOT_DIR.exists()) {
                ROOT_DIR.mkdirs();
            }

            TALLY_MUSIC_NAMES = _Utils.getFilesWithSuffix(ROOT_DIR.getPath(), ".mp3");
            Arrays.sort(TALLY_MUSIC_NAMES);


            /**
             * unit*cose：一个点位多少钱/手。
             */
            commoditys = new ArrayList<>();
            commoditys.add(new Commodity("ts", "二债",200,0.005));
            commoditys.add(new Commodity("tf", "五债",100,0.005));
            commoditys.add(new Commodity("t", "十债",10,0.005));
            commoditys.add(new Commodity("au", "金",1000,0.05));
            commoditys.add(new Commodity("ag", "银",15,1));
            commoditys.add(new Commodity("cu", "铜",5,10));
            commoditys.add(new Commodity("al", "铝",5,5));
            commoditys.add(new Commodity("zn", "锌",5,5));
            commoditys.add(new Commodity("pb", "铅",5,5));
            commoditys.add(new Commodity("ni", "镍",1,10));
            commoditys.add(new Commodity("sn", "锡",1,10));
            commoditys.add(new Commodity("j", "焦炭",100,0.5));
            commoditys.add(new Commodity("jm", "焦煤",60,0.5));
            commoditys.add(new Commodity("zc", "郑煤",100,0.2));
            commoditys.add(new Commodity("i", "铁矿",100,0.5));
            commoditys.add(new Commodity("wr", "线材",10,1));
            commoditys.add(new Commodity("rb", "螺纹",10,1));
            commoditys.add(new Commodity("hc", "热卷",10,1));
            commoditys.add(new Commodity("sf", "硅铁",5,2));
            commoditys.add(new Commodity("sm", "锰硅",5,2));
            commoditys.add(new Commodity("fg", "玻璃",20,1));
            commoditys.add(new Commodity("fu", "燃油",10,1));
            commoditys.add(new Commodity("sc", "原油",1000,0.1));
            commoditys.add(new Commodity("ru", "橡胶",10,5));
            commoditys.add(new Commodity("l", "塑料",5,5));
            commoditys.add(new Commodity("ta", "PTA",5,2));
            commoditys.add(new Commodity("v", "PVC",5,5));
            commoditys.add(new Commodity("pp", "PP",5,1));
            commoditys.add(new Commodity("ma", "郑醇",10,1));
            commoditys.add(new Commodity("bu", "沥青",10,2));
            commoditys.add(new Commodity("a", "豆一",10,1));
            commoditys.add(new Commodity("b", "豆二",10,1));
            commoditys.add(new Commodity("c", "玉米",10,1));
            commoditys.add(new Commodity("wh", "郑麦",20,1));
            commoditys.add(new Commodity("ri", "早稻",20,1));
            commoditys.add(new Commodity("lr", "晚稻",20,1));
            commoditys.add(new Commodity("jr", "粳稻",20,1));
            commoditys.add(new Commodity("rs", "菜籽",10,1));
            commoditys.add(new Commodity("ml", "豆粕",10,1));
            commoditys.add(new Commodity("rm", "菜粕",10,1));
            commoditys.add(new Commodity("y", "豆油",10,2));
            commoditys.add(new Commodity("oi", "郑油",10,1));
            commoditys.add(new Commodity("p", "棕榈",10,2));
            commoditys.add(new Commodity("cf", "郑棉",5,5));
            commoditys.add(new Commodity("sr", "白糖",10,1));
            commoditys.add(new Commodity("cy", "棉纱",5,5));
            commoditys.add(new Commodity("jd", "鸡蛋",5,1));
            commoditys.add(new Commodity("ap", "苹果",10,1));
            commoditys.add(new Commodity("cs", "淀粉",10,1));
            commoditys.add(new Commodity("fb", "纤板",500,0.05));
            commoditys.add(new Commodity("bb", "胶板",500,0.05));
        } catch (Exception e) {
            throw e;
        }

    }

}
